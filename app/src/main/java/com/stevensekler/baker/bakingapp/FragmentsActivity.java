package com.stevensekler.baker.bakingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.stevensekler.baker.bakingapp.R;
import com.stevensekler.baker.bakingapp.model.Cake;

import static com.stevensekler.baker.bakingapp.MainActivity.CAKE_OBJECT;

public class FragmentsActivity extends AppCompatActivity {
    private Cake cakeDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);

        setTitle(getString(R.string.make_cake));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cakeDetail = extras.getParcelable(CAKE_OBJECT);
        } else {
            finish();
            Toast.makeText(FragmentsActivity.this, R.string.no_data, Toast.LENGTH_SHORT).show();
        }
    }
}
