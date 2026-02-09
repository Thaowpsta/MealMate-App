package com.example.mealmate.ui.search.view;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealmate.R;
import com.example.mealmate.data.meals.models.FilterUIModel;
import com.example.mealmate.data.meals.models.Meal;
import com.example.mealmate.ui.meals.view.MealsAdapter;
import com.example.mealmate.ui.search.presenter.SearchPresenter;
import com.example.mealmate.ui.search.presenter.SearchPresenterImp;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchFragment extends Fragment implements SearchView, MealsAdapter.OnMealClickListener {

    private SearchPresenter presenter;
    private MealsAdapter adapter;
    private final List<Meal> searchResults = new ArrayList<>();
    private final CompositeDisposable disposable = new CompositeDisposable();
    private EditText searchBar;
    private Dialog errorDialog;
    private RecyclerView rvFilterValues;
    private FilterAdapter filterAdapter;
    private String currentFilterType = null;
    private final Map<String, List<String>> activeFilters = new HashMap<>();
    private Chip chipCategory, chipArea, chipIngredient;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new SearchPresenterImp(this, requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvMeals = view.findViewById(R.id.rv_meals);
        searchBar = view.findViewById(R.id.search_bar);
        ImageButton btnBack = view.findViewById(R.id.btn_back);
        ChipGroup chipGroupFilters = view.findViewById(R.id.chip_group_search_filters);
        rvFilterValues = view.findViewById(R.id.rv_filter_values);
        chipCategory = view.findViewById(R.id.chip_category);
        chipArea = view.findViewById(R.id.chip_area);
        chipIngredient = view.findViewById(R.id.chip_ingredient);

        adapter = new MealsAdapter(searchResults, this);
        rvMeals.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvMeals.setAdapter(adapter);

        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        filterAdapter = new FilterAdapter(selectedItems -> {
            if (currentFilterType != null) {
                if (selectedItems.isEmpty()) {
                    activeFilters.remove(currentFilterType);
                } else {
                    activeFilters.put(currentFilterType, selectedItems);
                }
                updateFilterChipsVisuals();
                triggerSearch();
            }
        });

        rvFilterValues.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvFilterValues.setAdapter(filterAdapter);

        chipGroupFilters.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.chip_category) {
                currentFilterType = "Category";
                rvFilterValues.setVisibility(View.VISIBLE);
                presenter.loadCategories();
            } else if (checkedId == R.id.chip_area) {
                currentFilterType = "Area";
                rvFilterValues.setVisibility(View.VISIBLE);
                presenter.loadAreas();
            } else if (checkedId == R.id.chip_ingredient) {
                currentFilterType = "Ingredient";
                rvFilterValues.setVisibility(View.VISIBLE);
                presenter.loadIngredients();
            } else {
                currentFilterType = null;
                rvFilterValues.setVisibility(View.GONE);
                activeFilters.clear();
                updateFilterChipsVisuals();
                presenter.getAllMeals();
            }
        });

        setupSearchObserver(searchBar);
        presenter.getAllMeals();
    }

    private void updateFilterChipsVisuals() {
        updateChipLabel(chipCategory, "Category");
        updateChipLabel(chipArea, "Area");
        updateChipLabel(chipIngredient, "Ingredient");
    }

    private void updateChipLabel(Chip chip, String type) {
        if (chip == null) return;

        if (activeFilters.containsKey(type) && !activeFilters.get(type).isEmpty()) {
            int count = activeFilters.get(type).size();
            chip.setText(type + " (" + count + ")");
        } else {
            chip.setText(type);
        }
    }

    private void triggerSearch() {
        String query = searchBar.getText().toString();
        presenter.searchMeals(query, activeFilters);
    }

    @Override
    public void showFilterOptions(List<FilterUIModel> options, boolean allowMultiSelect) {
        if (activeFilters.containsKey(currentFilterType)) {
            List<String> activeValues = activeFilters.get(currentFilterType);
            for (FilterUIModel option : options) {
                if (activeValues != null && activeValues.contains(option.getName())) {
                    option.setSelected(true);
                }
            }
        }

        filterAdapter.setItems(options);
        filterAdapter.setMultiSelect(allowMultiSelect);
        rvFilterValues.setVisibility(View.VISIBLE);
    }

    private void setupSearchObserver(EditText searchBar) {
        Observable<String> searchObservable = Observable.create(emitter -> {
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    emitter.onNext(s.toString());
                }
                @Override
                public void afterTextChanged(Editable s) {}
            };
            searchBar.addTextChangedListener(textWatcher);
            emitter.setCancellable(() -> searchBar.removeTextChangedListener(textWatcher));
        });

        disposable.add(searchObservable
                .debounce(500, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> triggerSearch()));
    }

    @Override
    public void showLoading() { }

    @Override
    public void hideLoading() { }

    @Override
    public void showSearchResults(List<Meal> meals) {
        searchResults.clear();
        searchResults.addAll(meals);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyState() {
        searchResults.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showError(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showConnectionError() {
        if (errorDialog != null && errorDialog.isShowing()) return;
        errorDialog = new Dialog(requireContext());
        errorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        errorDialog.setContentView(R.layout.dialog_no_connection);

        if (errorDialog.getWindow() != null) {
            errorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            errorDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        errorDialog.findViewById(R.id.btn_ok).setOnClickListener(v -> errorDialog.dismiss());
        errorDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (errorDialog != null && errorDialog.isShowing()) errorDialog.dismiss();
        presenter.onDestroy();
        disposable.clear();
    }

    @Override
    public void onMealClick(Meal meal) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("meal", meal);
        Navigation.findNavController(requireView()).navigate(R.id.action_searchFragment_to_mealDetailsFragment, bundle);
    }
}