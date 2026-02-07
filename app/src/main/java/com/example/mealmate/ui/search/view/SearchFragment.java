package com.example.mealmate.ui.search.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealmate.R;
import com.example.mealmate.data.meals.models.Meal;
import com.example.mealmate.ui.meals.view.MealsAdapter;
import com.example.mealmate.ui.search.presenter.SearchPresenter;
import com.example.mealmate.ui.search.presenter.SearchPresenterImp;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
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

    private ChipGroup chipGroupFilters;
    private EditText searchBar;

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
        chipGroupFilters = view.findViewById(R.id.chip_group_search_filters);

        adapter = new MealsAdapter(searchResults, this);
        rvMeals.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvMeals.setAdapter(adapter);

        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // Handle Chip Selection Changes
        chipGroupFilters.setOnCheckedChangeListener((group, checkedIds) -> {
            // 1. Get current text
            String query = searchBar.getText().toString();

            // 2. Trigger search immediately with new filters
            triggerSearch(query);
        });

        setupSearchObserver(searchBar);

        // Load all meals by default
        presenter.getAllMeals();
    }

    private void triggerSearch(String query) {
        List<String> selectedTypes = getSelectedSearchTypes();
        presenter.searchMeals(query, selectedTypes);
    }

    private List<String> getSelectedSearchTypes() {
        List<String> types = new ArrayList<>();
        List<Integer> ids = chipGroupFilters.getCheckedChipIds();

        for (Integer id : ids) {
            if (id == R.id.chip_name) types.add("Name");
            else if (id == R.id.chip_category) types.add("Category");
            else if (id == R.id.chip_area) types.add("Area");
            else if (id == R.id.chip_ingredient) types.add("Ingredient");
        }

        // Default to Name if nothing selected
        if (types.isEmpty()) {
            types.add("Name");
        }
        return types;
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
                .subscribe(this::triggerSearch));
    }

    @Override
    public void showLoading() {
        // Implement loading UI logic here
    }

    @Override
    public void hideLoading() {
        // Hide loading UI logic here
    }

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
    public void onDestroyView() {
        super.onDestroyView();
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