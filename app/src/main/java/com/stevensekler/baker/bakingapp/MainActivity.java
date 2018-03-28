package com.stevensekler.baker.bakingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.stevensekler.baker.bakingapp.adapters.CakeAdapter;
import com.stevensekler.baker.bakingapp.model.Cake;
import com.stevensekler.baker.bakingapp.utils.InternetClient;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupRecyclerView();
        downloadJsonData();
    }
    /** Downloads data from the Internet using Retrofit and converts data to a List using
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
            }

            @Override
            public void onFailure(Call<List<Cake>> call, Throwable t) {
                Toast.makeText(MainActivity.this,"No Internet connection!",Toast.LENGTH_SHORT).show();
            }
        });
    }
    /** Sets up a RecyclerView to display the available cakes. */
    private void setupRecyclerView(){
        // Butterknife is distributed under Apache License, Version 2.0

        ButterKnife.bind(this);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
//        Based on: https://antonioleiva.com/recyclerview-listener/
        adapter = new CakeAdapter(new ArrayList<Cake>(), new CakeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Cake cake) {
            }
        });
        recyclerView.setAdapter(adapter);
    }
}
