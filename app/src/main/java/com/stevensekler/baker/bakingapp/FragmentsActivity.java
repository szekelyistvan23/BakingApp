package com.stevensekler.baker.bakingapp;

/**
 *
 * This is the base activity to display the fragments of this app. Regardless of the device's type: phone
 * or tablet.
 *
 * */

import android.os.Parcelable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.stevensekler.baker.bakingapp.fragments.DescriptionFragment;
import com.stevensekler.baker.bakingapp.fragments.ListFragment;
import com.stevensekler.baker.bakingapp.model.Cake;
import com.stevensekler.baker.bakingapp.model.Step;

import butterknife.ButterKnife;

import static com.stevensekler.baker.bakingapp.MainActivity.CAKE_OBJECT;
import static com.stevensekler.baker.bakingapp.fragments.DescriptionFragment.FIRST_ITEM_FROM_ARRAY;
import static com.stevensekler.baker.bakingapp.fragments.ListFragment.DESCRIPTION_FRAGMENT;

public class FragmentsActivity extends AppCompatActivity implements DescriptionFragment.PassDataToActivity,
ListFragment.SendPositionToActivity{
    private Cake cakeDetail;
    public static final String CAKE_STEPS = "cake_steps";
    public static final String STEPS_LIST = "steps_list";
    public static final int INGREDIENTS_ID = 0;
    public static final int INITIALIZING_INT_VARIABLE = 0;
    public static final String INGREDIENTS_SHORT_DESCRIPTION = "Ingredients";
    public static final String INGREDIENTS_NO_VIDEO = "";
    public static final String NEW_LINE = "\n";
    public static final String LIST_FRAGMENT_STATE = "list_fragment_state";
    public static final String DESCRIPTION_FRAGMENT_DISPLAYED ="description_fragment_displayed";
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
    /** Extracts data from savedInstanceState and displays ListFragment or DescriptionFragment.
     * If the app is started for the first time and there is no Internet it will display an error
     * message, otherwise will load data from Shared Preferences.
     * @param bundle data from savedInstanceState
     * @param listFragmentContainer depends on the device type
     * @param descriptionFragmentContainer depends on the device type
     * */
    private void extractBundleData(Bundle bundle, int listFragmentContainer, int descriptionFragmentContainer){
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (bundle != null) {
                isDescriptionFragmentDisplayed = bundle.getBoolean(DESCRIPTION_FRAGMENT_DISPLAYED);
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
    /** Displays a ListFragment.
     *  @param listFragmentContainer the fragment's container, varies if the device is a phone or a tablet.
     */
    private void displayListFragment(int listFragmentContainer){
        ButterKnife.bind(this);
        Bundle args = new Bundle();
        args.putParcelableArray(CAKE_STEPS, cakeDetail.getSteps());

        ListFragment searchListFragment = (ListFragment) getSupportFragmentManager().findFragmentByTag(STEPS_LIST);

        if (searchListFragment != null){
            getSupportFragmentManager()
            .beginTransaction()
            .replace(listFragmentContainer, searchListFragment, STEPS_LIST)
            .commit();
        } else {

            ListFragment listFragment = new ListFragment();
            listFragment.setArguments(args);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(listFragmentContainer, listFragment, STEPS_LIST)
                    .commit();
        }
    }

    /** Adds a step object to the begining of the array, it is needed to display the ingredients of
     *  a cake.
     *  @return a Step array with the added object
     *  */
    private Step[] addingStepForIngredients (Step[] steps){
        Step[] result = new Step[steps.length];
        Step ingredients = new Step();

        ingredients.setId(INGREDIENTS_ID);
        ingredients.setShortDescription(INGREDIENTS_SHORT_DESCRIPTION);
        ingredients.setVideoURL(INGREDIENTS_NO_VIDEO);
        ingredients.setDescription(makeIngredientsList());

        result[FIRST_ITEM_FROM_ARRAY] = ingredients;
        for (int i = 1; i < steps.length; i++){
            result[i] = steps[i-1];
        }
        return result;
    }
    /** Makes a String object, which contains the ingredients of a cake.
     * @return the ingredients
     * */
    private String makeIngredientsList (){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = INITIALIZING_INT_VARIABLE; i < cakeDetail.getIngredients().size(); i++){
         stringBuilder.append(cakeDetail.getIngredients().get(i).toString());
         stringBuilder.append(NEW_LINE);
        }
        return stringBuilder.toString();
    }
    /** Restores the DescriptionFragment after screen rotation.*/
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
    /** Checks the actual state of the fragments and saves them for later use in onBackPressed
     * and onOptionsItemSelected.
     * */
    private void initializeAndSearchFragments(){
        listFragment =
                ListFragment.newInstance(listFragmentState, cakeDetail.getSteps());
        descriptionFragment =
                (DescriptionFragment) getSupportFragmentManager().findFragmentByTag(DESCRIPTION_FRAGMENT);
    }
    /** Makes possible the navigation between fragments and activities in onBackPressed and
     * onOptionsItemSelected.
     * */
    private void addNewListFragment(){
        int container = INITIALIZING_INT_VARIABLE;

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
    /** Implements the interface from DescriptionFragment.*/
    @Override
    public void descriptionFragmentDisplayed(boolean state) {
        isDescriptionFragmentDisplayed = state;
    }
    /** Implements the interface from ListFragment.*/
    @Override
    public void listRecyclerViewPosition(Parcelable parcelable) {
        listFragmentState = parcelable;
    }
    /** Saves data in the case of a screen rotation received from the displayed fragments.*/
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(DESCRIPTION_FRAGMENT_DISPLAYED,isDescriptionFragmentDisplayed);
        outState.putParcelable(LIST_FRAGMENT_STATE, listFragmentState);
    }
    /** Facilitates the navigation between fragments and activities.*/
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
    /** Facilitates the navigation between fragments and activities.*/
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
