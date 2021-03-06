package com.stevensekler.baker.bakingapp;

/**
 *
 * Downloads data with Retrofit if isn't available in SharedPreferences and
 * displays the cake name's in a RecyclerView.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stevensekler.baker.bakingapp.adapters.CakeAdapter;
import com.stevensekler.baker.bakingapp.model.Cake;
import com.stevensekler.baker.bakingapp.utils.InternetClient;
import com.stevensekler.baker.bakingapp.utils.Methods;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private List<Cake> cakes = null;
    @BindView(R.id.cake_recycler_view)
    RecyclerView recyclerView;
    private CakeAdapter adapter;
    public static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/";
    public static final String CAKE_OBJECT = "cake_object";
    public static final String JSON_DATA = "json_data";
    public static final int SPAN_COUNT_ONE = 1;
    public static final int SPAN_COUNT_TWO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupRecyclerView();

        String jsonData = Methods.readFromSharedPreferences(this,JSON_DATA);

        if (jsonData != null){
                cakes = deserializeJson(jsonData);
                adapter.changeCakeData(cakes);
        } else {
            downloadJsonData();
        }
    }
    /** Downloads data from the Internet using Retrofit and converts it to a List using
     * Gson converter. The implementation is based on:
     * https://www.youtube.com/watch?v=R4XU8yPzSx0
     * */
    private void downloadJsonData(){

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        InternetClient client = retrofit.create(InternetClient.class);
        Call<List<Cake>> call = client.cakesData();

        call.enqueue(new Callback<List<Cake>>() {

            @Override
            public void onResponse(Call<List<Cake>> call, Response<List<Cake>> response) {
                cakes = response.body();
                adapter.changeCakeData(cakes);
                Methods.saveToSharedPreferences(MainActivity.this, JSON_DATA, serializeCakeArray(cakes));
//                saveSharedPreferences(serializeCakeArray(cakes));
            }

            @Override
            public void onFailure(Call<List<Cake>> call, Throwable t) {
                finish();
                Toast.makeText(MainActivity.this, R.string.no_internet,Toast.LENGTH_SHORT).show();
            }
        });
    }
    /** Sets up a RecyclerView to display the available cakes. */
    private void setupRecyclerView(){
        // Butterknife is distributed under Apache License, Version 2.0
        ButterKnife.bind(this);

        setTitle(getString(R.string.cakes_title));

        recyclerView.setHasFixedSize(true);

        GridLayoutManager layoutManager = new GridLayoutManager(this, calculateSpanCount());
        recyclerView.setLayoutManager(layoutManager);
//        Based on: https://antonioleiva.com/recyclerview-listener/
        adapter = new CakeAdapter(new ArrayList<Cake>(), new CakeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Cake cake) {
                Bundle args = new Bundle();
                args.putParcelable(CAKE_OBJECT, cake);
                Intent intent = new Intent(MainActivity.this, FragmentsActivity.class);
                intent.putExtras(args);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }
    /** Serializes an array to be saved to Shared Preferences
     *  @param cakes Cake array to be serialized
     *  @return Json array
     * */
    private String serializeCakeArray(List<Cake> cakes){
        Gson gson = new Gson();
        return gson.toJson(cakes);
    }

    /** Transforms Json to Cake array
     *  @param string Json data
     *  @return Cake array will be used in the app without downloading data from the Internet,
     *          except the videos
     *  */
    private List<Cake> deserializeJson(String string){
        Gson gson = new Gson();
        Type cakeType = new TypeToken<List<Cake>>(){}.getType();
        return gson.fromJson(string, cakeType);
    }

    /** Calculates the span count for GridLayout
     *  @return 1 if the device is a phone, 2 if the device is a tablet
     * */
    private int calculateSpanCount(){
        int orientation = this.getResources().getConfiguration().orientation;
        if (Methods.isTablet(MainActivity.this) && orientation == Configuration.ORIENTATION_LANDSCAPE){
            return SPAN_COUNT_TWO;
        } else  {
            return SPAN_COUNT_ONE;
        }
    }
}
