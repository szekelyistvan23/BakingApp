package com.stevensekler.baker.bakingapp.utils;

import com.stevensekler.baker.bakingapp.model.Cake;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Interface for Retrofit, the implementation is based on:
 * https://www.youtube.com/watch?v=R4XU8yPzSx0
 */

public interface InternetClient {
    @GET("/topher/2017/May/59121517_baking/baking.json")
    Call<List<Cake>> cakesData();
}
