package com.stevensekler.baker.bakingapp;

import android.os.DeadSystemException;
import android.os.Parcelable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.stevensekler.baker.bakingapp.fragments.DescriptionFragment;
import com.stevensekler.baker.bakingapp.fragments.ListFragment;
import com.stevensekler.baker.bakingapp.model.Cake;
import com.stevensekler.baker.bakingapp.model.Step;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Optional;

import static com.stevensekler.baker.bakingapp.MainActivity.CAKE_OBJECT;
import static com.stevensekler.baker.bakingapp.fragments.ListFragment.DESCRIPTION_FRAGMENT;

public class FragmentsActivity extends AppCompatActivity implements DescriptionFragment.PassDataToActivity,
ListFragment.SendPositionToActivity{
    private Cake cakeDetail;
    public static final String CAKE_STEPS = "cake_steps";
    public static final String STEPS_LIST = "steps_list";
    public static final int INGREDIENTS_ID = 0;
    public static final String INGREDIENTS_SHORT_DESCRIPTION = "Ingredients";
    public static final String INGREDIENTS_NO_VIDEO = "";
    public static final String NEW_LINE = "\n";
    public static final String LIST_FRAGMENT_STATE = "list_fragment_state";
    private int recyclerViewPosition = 0;
    private boolean isDescriptionFragmentDisplayed;
    private Parcelable listFragmentState;
    private ListFragment listFragment;
    private DescriptionFragment descriptionFragment;
    private boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);

        if (findViewById(R.id.master_detail_layout) !=null){
            twoPane = true;
            extractBundleData(savedInstanceState, R.id.master_list, R.id.master_description);
        } else {
            twoPane = false;
            extractBundleData(savedInstanceState, R.id.fragment_container, R.id.fragment_container);
        }
    }

    private void extractBundleData(Bundle bundle, int listFragmentContainer, int descriptionFragmentContainer){
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (bundle != null) {
                isDescriptionFragmentDisplayed = bundle.getBoolean("STATE");
                listFragmentState = bundle.getParcelable(LIST_FRAGMENT_STATE);
            }
            cakeDetail = extras.getParcelable(CAKE_OBJECT);
            cakeDetail.setSteps(addingStepForIngredients(cakeDetail.getSteps()));
            setTitle(cakeDetail.getName());


            displayListFragment(listFragmentContainer);
            displayDescriptionFragment(descriptionFragmentContainer);
        } else {
            finish();
            Toast.makeText(FragmentsActivity.this, R.string.no_data, Toast.LENGTH_SHORT).show();
        }
    }

    private void displayListFragment(int descriptionFragmentContainer){
        ButterKnife.bind(this);
        Bundle args = new Bundle();
        args.putParcelableArray(CAKE_STEPS, cakeDetail.getSteps());

        ListFragment searchListFragment = (ListFragment) getSupportFragmentManager().findFragmentByTag(STEPS_LIST);

        if (searchListFragment != null){
            getSupportFragmentManager()
            .beginTransaction()
            .replace(descriptionFragmentContainer, searchListFragment, STEPS_LIST)
            .commit();
        } else {

            ListFragment listFragment = new ListFragment();
            listFragment.setArguments(args);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(descriptionFragmentContainer, listFragment, STEPS_LIST)
                    .commit();
        }
    }
    private Step[] addingStepForIngredients (Step[] steps){
        Step[] result = new Step[steps.length];
        Step ingredients = new Step();

        ingredients.setId(INGREDIENTS_ID);
        ingredients.setShortDescription(INGREDIENTS_SHORT_DESCRIPTION);
        ingredients.setVideoURL(INGREDIENTS_NO_VIDEO);
        ingredients.setDescription(makeIngredientsList());

        result[0] = ingredients;
        for (int i = 1; i < steps.length; i++){
            result[i] = steps[i-1];
        }
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

    private void displayDescriptionFragment(int descriptionFragmentContainer){
        if (isDescriptionFragmentDisplayed) {

            DescriptionFragment searchDescriptionFragment =
                    (DescriptionFragment) getSupportFragmentManager()
                            .findFragmentByTag(DESCRIPTION_FRAGMENT);

            if (searchDescriptionFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(descriptionFragmentContainer, searchDescriptionFragment, DESCRIPTION_FRAGMENT)
                        .commit();
            }
        }
    }

    private void initializeAndSearchFragments(){
        listFragment =
                ListFragment.newInstance(listFragmentState, cakeDetail.getSteps());
        descriptionFragment =
                (DescriptionFragment) getSupportFragmentManager().findFragmentByTag(DESCRIPTION_FRAGMENT);
    }

    private void addNewListFragment(){
        int container = 0;

        if (!twoPane){
            container = R.id.fragment_container;
        } else {
            container = R.id.master_list;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(container, listFragment, STEPS_LIST)
                .commit();
        isDescriptionFragmentDisplayed = false;
    }

    @Override
    public void descriptionFragmentDisplayed(boolean state) {
        isDescriptionFragmentDisplayed = state;
    }

    @Override
    public void listRecyclerViewPosition(Parcelable parcelable) {
        listFragmentState = parcelable;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("STATE",isDescriptionFragmentDisplayed);
        outState.putParcelable(LIST_FRAGMENT_STATE, listFragmentState);
    }

    @Override
    public void onBackPressed() {
        initializeAndSearchFragments();

        if (descriptionFragment != null && descriptionFragment.isVisible() && !twoPane){
        addNewListFragment();
            isDescriptionFragmentDisplayed = false;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        initializeAndSearchFragments();

        switch (item.getItemId()) {
            case android.R.id.home:
                if (descriptionFragment != null && descriptionFragment.isVisible() && !twoPane){
                addNewListFragment();
                } else {
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
