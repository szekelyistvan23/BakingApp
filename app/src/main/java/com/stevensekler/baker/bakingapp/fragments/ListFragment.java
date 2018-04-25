package com.stevensekler.baker.bakingapp.fragments;

/* Displays the steps to bake a cake with the help of a RecyclerView. */

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stevensekler.baker.bakingapp.R;
import com.stevensekler.baker.bakingapp.adapters.StepAdapter;
import com.stevensekler.baker.bakingapp.model.Step;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.stevensekler.baker.bakingapp.FragmentsActivity.CAKE_STEPS;
import static com.stevensekler.baker.bakingapp.FragmentsActivity.INITIALIZING_INT_VARIABLE;
import static com.stevensekler.baker.bakingapp.fragments.DescriptionFragment.FIRST_ITEM_FROM_ARRAY;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {

    @BindView(R.id.step_recycler_view)
    RecyclerView stepRecyclerView;
    private LinearLayoutManager layoutManager;
    private Unbinder unbinder;
    private Step[] stepsFromActivity;
    private int position;
    private Parcelable restoreState;
    private boolean twoPane;
    public static final String RECYCLER_VIEW_POSITION = "position";
    public static final String STEP_OBJECT = "step_object";
    public static final String STEP_ARRAY_SIZE = "step_array_size";
    public static final String STEP_ARRAY_POSITION = "step_array_position";
    public static final String STEP_ARRAY = "step_array";
    public static final String DESCRIPTION_FRAGMENT = "description_fragment";
    public static final int DELAYS_IN_MILLISECONDS = 1;
    SendPositionToActivity callbackForPosition;

    public ListFragment() {
    }
    /** Sends RecyclerView's LinearLayoutManager's state to FragmentsActivity. */
    public interface SendPositionToActivity{
        void listRecyclerViewPosition(Parcelable parcelable);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getArguments() != null) {
            stepsFromActivity = (Step[]) getArguments().getParcelableArray(CAKE_STEPS);
            restoreState = getArguments().getParcelable(RECYCLER_VIEW_POSITION);
        }

        View view = inflater.inflate(R.layout.fragment_list, container, false);

        if (getActivity().findViewById(R.id.master_detail_layout) != null) {
            twoPane = true;
        } else {
            twoPane = false;
        }

        setupStepRecyclerView(view, savedInstanceState);

        return view;
    }

    /**
     * Sets up a RecyclerView to display the steps to make a cake.
     * @param view the fragment's view
     * @param state checks if there is any data in savedInstanceState
     */
    private void setupStepRecyclerView(View view, Bundle state) {
        // Butterknife is distributed under Apache License, Version 2.0
        unbinder = ButterKnife.bind(this, view);

        int result = INITIALIZING_INT_VARIABLE;

        if (twoPane){
            result = R.id.master_description;
        } else {
            result = R.id.fragment_container;
        }

        final int container = result;

        stepRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        stepRecyclerView.setLayoutManager(layoutManager);
//        Based on: https://antonioleiva.com/recyclerview-listener/
        if (stepsFromActivity != null) {
            StepAdapter stepAdapter = new StepAdapter(stepsFromActivity, new StepAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Step step) {
                    Bundle args = new Bundle();
                    args.putInt(STEP_ARRAY_POSITION, getStepPositionFromArray(step));
                    args.putParcelableArray(STEP_ARRAY, stepsFromActivity);

                        DescriptionFragment searchDescriptionFragment =
                                (DescriptionFragment) getActivity()
                                        .getSupportFragmentManager()
                                        .findFragmentByTag(DESCRIPTION_FRAGMENT);

                        DescriptionFragment descriptionFragment = new DescriptionFragment();
                        descriptionFragment.setArguments(args);
//                      Two pane mode.
                        if (searchDescriptionFragment != null && twoPane) {

                            getActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(container, descriptionFragment, DESCRIPTION_FRAGMENT)
                                    .commit();
                        }
                        if (searchDescriptionFragment == null && twoPane){
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .add(container, descriptionFragment, DESCRIPTION_FRAGMENT)
                                    .commit();
                        }
//                      One pane mode.
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(container, descriptionFragment, DESCRIPTION_FRAGMENT)
                            .commit();
                }
            });
            stepRecyclerView.setAdapter(stepAdapter);
        }

        if (twoPane && state == null){
            stepRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stepRecyclerView.findViewHolderForAdapterPosition(FIRST_ITEM_FROM_ARRAY).itemView.performClick();
                }
            },DELAYS_IN_MILLISECONDS);
        }
    }
    /** Restores LinearLayoutManager's state.
     * @param savedInstanceState the state is retrieved from a Bundle
     * */
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (restoreState !=  null){
            layoutManager.onRestoreInstanceState(restoreState);
        }
    }
    /** Creates a new ListFragment.
     * @param state to restore RecyclerView's position
     * @param steps without the array there will be no data to display
     * @return the new fragment
     * */
    public static ListFragment newInstance(Parcelable state, Step[] steps) {

        Bundle args = new Bundle();

        args.putParcelable(RECYCLER_VIEW_POSITION, state);
        args.putParcelableArray(CAKE_STEPS, steps);

        ListFragment fragment = new ListFragment();
        fragment.setArguments(args);
        return fragment;
    }
    /** Returns the index of a Step object from an array.
     * @param step the methods search for this object's index
     * @return the index number or -1 if couldn't find it*/
    private int getStepPositionFromArray(Step step) {
        int result = INITIALIZING_INT_VARIABLE;
        for (int i = INITIALIZING_INT_VARIABLE; i < stepsFromActivity.length; i++) {
            if (stepsFromActivity[i].equals(step)) {
                result = i;
                return result;
            }
        }
        return -1;
    }
    /** Checks if the interface is implemented in the activity. */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callbackForPosition = (SendPositionToActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + getString(R.string.implement_send_position_to_activity));
        }
    }
    /** Saves LinearLayoutManager's state. */
    @Override
    public void onStop() {
        super.onStop();
        callbackForPosition.listRecyclerViewPosition(layoutManager.onSaveInstanceState());
    }
    /** Unbinder for Butterknife. */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
