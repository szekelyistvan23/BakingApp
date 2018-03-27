package com.stevensekler.baker.bakingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.stevensekler.baker.bakingapp.model.Cake;
import com.stevensekler.baker.bakingapp.utils.InternetClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private List<Cake> cakes;
    public static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/";
    public static final String COMMA = ",";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            }

            @Override
            public void onFailure(Call<List<Cake>> call, Throwable t) {
                Toast.makeText(MainActivity.this,"No Internet connection!",Toast.LENGTH_SHORT).show();
            }
        });
    }
    /** Retrieves the Ingredients from a Cake object to be displayed in the RecyclerView.*/
    private String extractIngredients (Cake cake){
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < cake.getIngredients().size(); i++){
            result.append(cake.getIngredients().get(i).getIngredient());
            if (i != cake.getIngredients().size() - 1){
                result.append(COMMA);
            }
        }
        return result.toString();
    }
}
