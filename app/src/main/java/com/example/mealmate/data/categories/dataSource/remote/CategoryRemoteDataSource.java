package com.example.mealmate.data.categories.dataSource.remote;

import com.example.mealmate.data.categories.model.CategoryResponse;
import com.example.mealmate.data.network.MealService;
import com.example.mealmate.data.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryRemoteDataSource {
    private MealService mealService;

    public CategoryRemoteDataSource() {
        mealService = RetrofitClient.getInstance().getMealService();
    }

    public void getCategoriesService(NetworkCategoryResponse callback){
        mealService.getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.code() == 200)
                    callback.onSuccess(response.body().categories);
                else
                    callback.onFailure("Error with code:" + response.code());
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                callback.onFailure("Error with Msg:" + t.getMessage());
            }
        });
    }
}
