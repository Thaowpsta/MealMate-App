package com.example.mealmate.data.repositories;

import android.content.Context;

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
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

    public MealRepository(Context context) {
        this.remoteMealDataSource = new MealRemoteDataSource();
        this.remoteCategoryDataSource = new CategoryRemoteDataSource();
        this.localDataSource = MealDatabase.getInstance(context).mealDao();
        this.auth = FirebaseAuth.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
    }

    // ================= NETWORK CALLS (Search & Filter) =================

    public Single<List<Meal>> getRandomMeal() {
        return remoteMealDataSource.getRandomMealService();
    }

    public Single<List<Category>> getCategories() {
        return remoteCategoryDataSource.getCategoriesService();
    }

    public Single<List<Meal>> searchMeals(String query) {
        return remoteMealDataSource.searchMeals(query)
                .subscribeOn(Schedulers.io());
    }

    public Single<List<Meal>> filterBy(String type, String query) {
        Single<MealResponse> request;
        switch (type) {
            case "Category": request = remoteMealDataSource.filterByCategory(query); break;
            case "Area": request = remoteMealDataSource.filterByArea(query); break;
            case "Ingredient": request = remoteMealDataSource.filterByIngredient(query); break;
            default: return Single.error(new IllegalArgumentException("Invalid filter type"));
        }
        return request.map(response -> response.meals).subscribeOn(Schedulers.io());
    }

    // ================= FAVORITES (Offline First) =================

    public Flowable<List<Meal>> getFavorites() {
        return localDataSource.getFavoriteMeals()
                .map(entities -> entities.stream()
                        .map(MealDTO::toMeal)
                        .collect(Collectors.toList()))
                .subscribeOn(Schedulers.io());
    }

    public Single<Boolean> isFavorite(String id) {
        return localDataSource.isFavorite(id)
                .subscribeOn(Schedulers.io());
    }

    public Completable addFavorite(Meal meal) {
        MealDTO entity = MealDTO.fromMeal(meal);
        entity.isFavorite = true;

        return localDataSource.insertFavorite(entity)
                .andThen(updateFirestoreFavorite(meal, true))
                .subscribeOn(Schedulers.io());
    }

    public Completable removeFavorite(Meal meal) {
        return localDataSource.removeFavorite(meal.getId())
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
        return localDataSource.getPlansByDate(date).subscribeOn(Schedulers.io());
    }

    public Completable addPlan(Meal meal, String date, String dayOfWeek, String type) {
        PlannedMealDTO plan = new PlannedMealDTO();
        plan.mealId = meal.getId();
        plan.mealName = meal.strMeal;
        plan.mealThumb = meal.strMealThumb;
        plan.date = date;
        plan.dayOfWeek = dayOfWeek;
        plan.mealType = type;

        return localDataSource.insertMeal(MealDTO.fromMeal(meal))
                .andThen(localDataSource.insertPlan(plan))
                .andThen(syncPlanToFirestore(plan))
                .subscribeOn(Schedulers.io());
    }

    private Completable syncPlanToFirestore(PlannedMealDTO plan) {
        return Completable.create(emitter -> {
            String uid = auth.getUid();
            if (uid == null) { emitter.onComplete(); return; }

            String docId = plan.date + "_" + plan.mealType + "_" + plan.mealId;

            firestore.collection("users").document(uid)
                    .collection("plans").document(docId)
                    .set(plan)
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }
}
