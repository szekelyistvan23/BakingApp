package com.stevensekler.baker.bakingapp;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stevensekler.baker.bakingapp.model.Cake;
import com.stevensekler.baker.bakingapp.utils.InternetClient;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://d17h27t6h515a5.cloudfront.net/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        InternetClient client = retrofit.create(InternetClient.class);
        Call<List<Cake>> call = client.cakesData();

        call.enqueue(new Callback<List<Cake>>() {

            @Override
            public void onResponse(Call<List<Cake>> call, Response<List<Cake>> response) {
                List<Cake> cakes = response.body();
            }

            @Override
            public void onFailure(Call<List<Cake>> call, Throwable t) {
//                Toast.makeText(MainActivity.this,"No Internet connection!",Toast.LENGTH_SHORT).show();
                Snackbar.make(findViewById(R.id.main_activity), "No Internet connection", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
