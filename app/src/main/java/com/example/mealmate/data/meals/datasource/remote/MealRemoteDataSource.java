package com.example.mealmate.data.meals.datasource.remote;

import com.example.mealmate.data.meals.model.MealResponse;
import com.example.mealmate.data.network.MealService;
import com.example.mealmate.data.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealRemoteDataSource {

    private MealService mealService;

    public MealRemoteDataSource() {
        mealService = RetrofitClient.getInstance().getMealService();
    }

    public void getRandomMealService(NetworkMealResponse callback){
        mealService.getRandomMeal().enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.code() == 200)
                    callback.onSuccess(response.body().meals);
                else
                    callback.onFailure("Error with code:" + response.code());
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                callback.onFailure("Error with Msg:" + t.getMessage());
            }
        });
    }
}
