package com.stevensekler.baker.bakingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.stevensekler.baker.bakingapp.fragments.ListFragment;
import com.stevensekler.baker.bakingapp.model.Cake;
import com.stevensekler.baker.bakingapp.model.Step;

import java.util.ArrayList;
import java.util.List;

import static com.stevensekler.baker.bakingapp.MainActivity.CAKE_OBJECT;

public class FragmentsActivity extends AppCompatActivity {
    private Cake cakeDetail;
    public static final String CAKE_STEPS = "cake_steps";
    public static final String STEPS_LIST = "steps_list";
    public static final int INGREDIENTS_ID = 0;
    public static final String INGREDIENTS_SHORT_DESCRIPTION = "Ingredients";
    public static final String NEW_LINE = "\n";

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
        args.putParcelableArray(CAKE_STEPS, arrayListToStepArray(cakeDetail.getSteps()));

        ListFragment searchListFragment = (ListFragment) getSupportFragmentManager().findFragmentByTag("stepsList");

        if (searchListFragment != null){
            getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, searchListFragment, STEPS_LIST)
            .commit();
        } else {

            ListFragment listFragment = new ListFragment();
            listFragment.setArguments(args);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, listFragment, STEPS_LIST)
                    .commit();
        }
    }
    private Step[] arrayListToStepArray(List<Step> steps){
        steps = addingStepForIngredients(steps);
        Step[] result = new Step[steps.size()];
        for (int i = 0; i < steps.size(); i++){
            result[i] = steps.get(i);
        }
        return result;
    }
    private List<Step> addingStepForIngredients (List<Step> steps){
        List<Step> result = new ArrayList<>();
        Step ingredients = new Step();

        ingredients.setId(INGREDIENTS_ID);
        ingredients.setShortDescription(INGREDIENTS_SHORT_DESCRIPTION);
        ingredients.setDescription(makeIngredientsList());

        result.add(ingredients);
        result.addAll(steps);

        return result;
    }

    private String makeIngredientsList (){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < cakeDetail.getIngredients().size(); i++){
         stringBuilder.append(cakeDetail.getIngredients().get(i).toString());
         stringBuilder.append(NEW_LINE);
        }
        return stringBuilder.toString();
    }
}
