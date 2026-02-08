package com.example.mealmate.data.repositories;

import android.content.Context;
import android.util.Log;

import com.example.mealmate.data.SharedPreferencesManager;
import com.example.mealmate.data.categories.dataSource.remote.CategoryRemoteDataSource;
import com.example.mealmate.data.categories.model.Category;
import com.example.mealmate.data.db.MealDatabase;
import com.example.mealmate.data.meals.datasource.local.MealDAO;
import com.example.mealmate.data.meals.datasource.local.MealDTO;
import com.example.mealmate.data.meals.datasource.local.PlannedMealDTO;
import com.example.mealmate.data.meals.datasource.remote.MealRemoteDataSource;
import com.example.mealmate.data.meals.models.Meal;
import com.example.mealmate.data.meals.models.MealResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MealRepository {

    private final MealRemoteDataSource remoteMealDataSource;
    private final CategoryRemoteDataSource remoteCategoryDataSource;
    private final MealDAO localDataSource;
    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;
    private final SharedPreferencesManager sharedPrefsManager;

    public MealRepository(Context context) {
        this.remoteMealDataSource = new MealRemoteDataSource();
        this.remoteCategoryDataSource = new CategoryRemoteDataSource();
        this.localDataSource = MealDatabase.getInstance(context).mealDao();
        this.auth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
        this.sharedPrefsManager = SharedPreferencesManager.getInstance(context);
    }

    // ================= NETWORK CALLS (Search & Filter) =================

    public Single<List<Meal>> getRandomMeal() {
        return remoteMealDataSource.getRandomMealService();
    }

    public Single<Meal> getMealById(String id) {
        Meal cachedMeal = sharedPrefsManager.getCachedMealDetails(id);
        if (cachedMeal != null) {
            return Single.just(cachedMeal);
        }

        return remoteMealDataSource.getMealByIdService(id)
                .doOnSuccess(meal -> {
                    sharedPrefsManager.cacheMealDetails(meal);
                })
                .subscribeOn(Schedulers.io());
    }

    public Single<List<Meal>> searchMeals(String query) {
        return remoteMealDataSource.searchMeals(query)
                .subscribeOn(Schedulers.io());
    }

    // ================= CATEGORIES WITH CACHING =================

    public Single<List<Category>> getCategories() {
        List<Category> cachedCategories = sharedPrefsManager.getCachedCategories();
        if (cachedCategories != null && !cachedCategories.isEmpty()) {
            return Single.just(cachedCategories);
        }

        return remoteCategoryDataSource.getCategoriesService()
                .doOnSuccess(categories -> {
                    sharedPrefsManager.cacheCategories(categories);
                })
                .subscribeOn(Schedulers.io());
    }

    // ================= FILTER BY CATEGORY WITH CACHING =================

    public Single<List<Meal>> filterBy(String type, String query) {
        String cacheKey = "filter_" + type + "_" + query;

        if ("Category".equals(type)) {
            List<Meal> cachedCategoryMeals = sharedPrefsManager.getCachedCategoryMeals(query);
            if (cachedCategoryMeals != null && !cachedCategoryMeals.isEmpty()) {
                return Single.just(cachedCategoryMeals);
            }
        }

        List<Meal> cachedMeals = sharedPrefsManager.getCachedMealList(cacheKey);
        if (cachedMeals != null && !cachedMeals.isEmpty()) {
            return Single.just(cachedMeals);
        }

        Single<MealResponse> request;
        switch (type) {
            case "Category":
                request = remoteMealDataSource.filterByCategory(query);
                break;
            case "Area":
                request = remoteMealDataSource.filterByArea(query);
                break;
            case "Ingredient":
                request = remoteMealDataSource.filterByIngredient(query);
                break;
            default:
                return Single.error(new IllegalArgumentException("Invalid filter type"));
        }

        return request.map(response -> response.meals)
                .doOnSuccess(meals -> {
                    sharedPrefsManager.cacheMealList(cacheKey, meals);

                    if ("Category".equals(type)) {
                        sharedPrefsManager.cacheCategoryMeals(query, meals);
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    // ================= PRELOAD CATEGORY MEALS =================

    public Completable preloadCategoryMeals(List<Category> categories) {
        return Completable.create(emitter -> {
            List<Completable> allRequests = new ArrayList<>();

            for (Category category : categories) {
                List<Meal> cached = sharedPrefsManager.getCachedCategoryMeals(category.getStrCategory());
                if (cached != null && !cached.isEmpty()) {
                    continue;
                }

                Completable request = remoteMealDataSource.filterByCategory(category.getStrCategory())
                        .map(response -> response.meals)
                        .doOnSuccess(meals -> {
                            // Cache the category meals
                            sharedPrefsManager.cacheCategoryMeals(category.getStrCategory(), meals);
                        })
                        .ignoreElement()
                        .subscribeOn(Schedulers.io());

                allRequests.add(request);
            }

            if (allRequests.isEmpty()) {
                emitter.onComplete();
                return;
            }

            Completable.merge(allRequests)
                    .subscribe(emitter::onComplete, emitter::onError);
        });
    }

    // ================= FAVORITES (Offline First) =================

    public Flowable<List<Meal>> getFavorites() {
        String uid = auth.getUid();
        if (uid == null) return Flowable.error(new Exception("User not logged in"));

        return localDataSource.getFavoriteMeals(uid)
                .map(entities -> entities.stream()
                        .map(MealDTO::toMeal)
                        .collect(Collectors.toList()))
                .subscribeOn(Schedulers.io());
    }

    public Single<Boolean> isFavorite(String id) {
        String uid = auth.getUid();
        if (uid == null) return Single.just(false);

        return localDataSource.isFavorite(id, uid)
                .subscribeOn(Schedulers.io());
    }

    public Completable addFavorite(Meal meal) {
        String uid = auth.getUid();
        if (uid == null) return Completable.error(new Exception("User not logged in"));

        MealDTO entity = MealDTO.fromMeal(meal, uid);
        entity.isFavorite = true;

        return localDataSource.insertFavorite(entity)
                .andThen(updateFirestoreFavorite(meal, true))
                .subscribeOn(Schedulers.io());
    }

    public Completable removeFavorite(Meal meal) {
        String uid = auth.getUid();
        if (uid == null) return Completable.error(new Exception("User not logged in"));

        return localDataSource.removeFavorite(meal.getId(), uid)
                .andThen(updateFirestoreFavorite(meal, false))
                .subscribeOn(Schedulers.io());
    }

    private Completable updateFirestoreFavorite(Meal meal, boolean isFavorite) {
        return Completable.create(emitter -> {
            String uid = auth.getUid();
            if (uid == null) { emitter.onComplete(); return; }

            if (isFavorite) {
                Map<String, Object> data = new HashMap<>();
                data.put("id", meal.getId());
                data.put("name", meal.strMeal);
                data.put("thumb", meal.strMealThumb);

                firestore.collection("users").document(uid)
                        .collection("favorites").document(meal.getId())
                        .set(data)
                        .addOnSuccessListener(aVoid -> emitter.onComplete())
                        .addOnFailureListener(emitter::onError);
            } else {
                firestore.collection("users").document(uid)
                        .collection("favorites").document(meal.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> emitter.onComplete())
                        .addOnFailureListener(emitter::onError);
            }
        });
    }

    // ================= MEAL PLANS (Offline First) =================

    public Flowable<List<PlannedMealDTO>> getPlansByDate(String date) {
        String uid = auth.getUid();
        if (uid == null) return Flowable.just(new ArrayList<>());

        return localDataSource.getPlansByDate(date, uid).subscribeOn(Schedulers.io());
    }

    public Flowable<Integer> getPlansCount() {
        String uid = auth.getUid();
        if (uid == null) return Flowable.just(0);

        return localDataSource.getPlansCount(uid).subscribeOn(Schedulers.io());
    }

    public Flowable<List<PlannedMealDTO>> getAllPlans() {
        String uid = auth.getUid();
        if (uid == null) return Flowable.just(new ArrayList<>());

        return localDataSource.getAllPlans(uid).subscribeOn(Schedulers.io());
    }

    public Completable addPlan(Meal meal, String date, String dayOfWeek, String type) {
        return Completable.fromAction(() -> {
            String uid = auth.getUid();
            if (uid == null) throw new Exception("User not logged in");

            PlannedMealDTO newPlan = new PlannedMealDTO();
            newPlan.userId = uid;
            newPlan.mealId = meal.getId();
            newPlan.mealName = meal.strMeal;
            newPlan.mealThumb = meal.strMealThumb;
            newPlan.date = date;
            newPlan.dayOfWeek = dayOfWeek;
            newPlan.mealType = type;

            PlannedMealDTO existingPlan = localDataSource.getPlanByDateAndTypeSync(date, type, uid);
            if (existingPlan != null) {
                localDataSource.deletePlanSync(existingPlan);
                deleteFirestorePlanSync(existingPlan);
            }

            localDataSource.insertMealSync(MealDTO.fromMeal(meal, uid));
            localDataSource.insertPlanSync(newPlan);
            syncPlanToFirestoreSync(newPlan);
        }).subscribeOn(Schedulers.io());
    }

    private void deleteFirestorePlanSync(PlannedMealDTO plan) {
        String uid = auth.getUid();
        if (uid == null) return;

        String docId = plan.date + "_" + plan.mealType + "_" + plan.mealId;
        firestore.collection("users").document(uid).collection("plans").document(docId)
                .delete()
                .addOnFailureListener(e -> Log.e("MealRepository", "Failed to delete plan from Firestore", e));
    }

    private void syncPlanToFirestoreSync(PlannedMealDTO plan) {
        String uid = auth.getUid();
        if (uid == null) return;

        String docId = plan.date + "_" + plan.mealType + "_" + plan.mealId;
        firestore.collection("users").document(uid).collection("plans").document(docId)
                .set(plan)
                .addOnFailureListener(e -> Log.e("MealRepository", "Failed to sync plan to Firestore", e));
    }

    public void deletePastPlans() {
        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String todayStr = dbFormat.format(new Date());
        String uid = auth.getUid();

        if (uid == null) return;

        localDataSource.getPastPlans(todayStr, uid)
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(plans -> {
                    if (plans.isEmpty()) return Completable.complete();

                    if (uid == null) return Completable.complete();

                    WriteBatch batch = firestore.batch();
                    for (PlannedMealDTO plan : plans) {
                        String docId = plan.date + "_" + plan.mealType + "_" + plan.mealId;
                        DocumentReference docRef = firestore.collection("users")
                                .document(uid)
                                .collection("plans")
                                .document(docId);
                        batch.delete(docRef);
                    }

                    return Completable.create(emitter ->
                            batch.commit()
                                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                                    .addOnFailureListener(emitter::onError)
                    );
                })
                .andThen(localDataSource.deletePastPlans(todayStr, uid))
                .subscribe(
                        () -> Log.d("MealRepository", "Past plans cleaned up"),
                        error -> Log.e("MealRepository", "Failed cleanup: " + error.getMessage())
                );
    }

    private Completable syncPlanToFirestore(PlannedMealDTO plan) {
        return Completable.create(emitter -> {
            String uid = auth.getUid();
            if (uid == null) { emitter.onComplete(); return; }
            String docId = plan.date + "_" + plan.mealType + "_" + plan.mealId;
            firestore.collection("users").document(uid).collection("plans").document(docId).set(plan).addOnSuccessListener(aVoid -> emitter.onComplete()).addOnFailureListener(emitter::onError);
        });
    }

    private Completable deleteFirestorePlan(PlannedMealDTO plan) {
        return Completable.create(emitter -> {
            String uid = auth.getUid();
            if (uid == null) { emitter.onComplete(); return; }
            String docId = plan.date + "_" + plan.mealType + "_" + plan.mealId;
            firestore.collection("users").document(uid).collection("plans").document(docId).delete().addOnSuccessListener(aVoid -> emitter.onComplete()).addOnFailureListener(emitter::onError);
        });
    }

    // ================= CLEAR CACHE =================

    public void clearAllCache() {
        Completable.fromAction(() -> {
                    sharedPrefsManager.clearAll();
                }).subscribeOn(Schedulers.io())
                .subscribe(
                        () -> Log.d("MealRepository", "All cache cleared"),
                        error -> Log.e("MealRepository", "Failed to clear cache", error)
                );
    }

    public void clearCategoryCache() {
        Completable.fromAction(() -> {
                    sharedPrefsManager.clearCategoryCache();
                }).subscribeOn(Schedulers.io())
                .subscribe(
                        () -> Log.d("MealRepository", "Category cache cleared"),
                        error -> Log.e("MealRepository", "Failed to clear category cache", error)
                );
    }
}