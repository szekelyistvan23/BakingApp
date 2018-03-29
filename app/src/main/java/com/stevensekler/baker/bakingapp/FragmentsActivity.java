package com.stevensekler.baker.bakingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.stevensekler.baker.bakingapp.R;
import com.stevensekler.baker.bakingapp.fragments.ListFragment;
import com.stevensekler.baker.bakingapp.model.Cake;

import static com.stevensekler.baker.bakingapp.MainActivity.CAKE_OBJECT;

public class FragmentsActivity extends AppCompatActivity {
    private Cake cakeDetail;
    public static final String CAKE_STEPS = "cake_steps";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);

        extractBundleData();
    }

    private void extractBundleData(){
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cakeDetail = extras.getParcelable(CAKE_OBJECT);
            setTitle(cakeDetail.getName());
            displayListFragment();
        } else {
            finish();
            Toast.makeText(FragmentsActivity.this, R.string.no_data, Toast.LENGTH_SHORT).show();
        }
    }

    private void displayListFragment(){
        Bundle args = new Bundle();
//        args.putParcelableArray(CAKE_STEPS, cakeDetail.getSteps());

        ListFragment listFragment = new ListFragment();
//        listFragment.setArguments(extras);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.list_fragment, listFragment)
                .commit();

    }
}
