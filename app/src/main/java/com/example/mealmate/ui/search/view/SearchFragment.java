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
    private EditText searchBar;
    private Dialog errorDialog;
    private RecyclerView rvFilterValues;
    private FilterAdapter filterAdapter;
    private String currentFilterType = null;
    private List<String> currentFilterValues = new ArrayList<>();

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

        adapter = new MealsAdapter(searchResults, this);
        rvMeals.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvMeals.setAdapter(adapter);

        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // Initialize filter adapter BEFORE chip listener
        filterAdapter = new FilterAdapter(selectedItems -> {
            currentFilterValues = selectedItems;
            triggerSearch(); // Fetch meals for the selected filter
        });

        rvFilterValues.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvFilterValues.setAdapter(filterAdapter);

        chipGroupFilters.setOnCheckedChangeListener((group, checkedId) -> {
            // Clear current filter values
            currentFilterValues.clear();

            // Clear the search bar text when switching filter types
            searchBar.setText("");

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
                // No chip selected - show all meals
                currentFilterType = null;
                rvFilterValues.setVisibility(View.GONE);
                presenter.getAllMeals();
            }
        });

        setupSearchObserver(searchBar);

        // Load initial meals
        presenter.getAllMeals();
    }

    private void triggerSearch() {
        String query = searchBar.getText().toString();
        // This will use the Presenter's cache if the filter hasn't changed
        presenter.searchMeals(query, currentFilterType, currentFilterValues);
    }

    @Override
    public void showFilterOptions(List<FilterUIModel> options, boolean allowMultiSelect) {
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
        // Prevent showing the dialog if it's already visible
        if (errorDialog != null && errorDialog.isShowing()) {
            return;
        }

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
        if (errorDialog != null && errorDialog.isShowing()) {
            errorDialog.dismiss();
        }
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