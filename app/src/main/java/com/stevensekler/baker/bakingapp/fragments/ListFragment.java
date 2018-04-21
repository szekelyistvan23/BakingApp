package com.stevensekler.baker.bakingapp.fragments;


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
    SendPositionToActivity callbackForPosition;

    public ListFragment() {
    }

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
        /* TODO Try to solve this with Butterknife */
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
     */
    private void setupStepRecyclerView(View view, Bundle state) {
        // Butterknife is distributed under Apache License, Version 2.0
        unbinder = ButterKnife.bind(this, view);

        int result = 0;

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
                    stepRecyclerView.findViewHolderForAdapterPosition(0).itemView.performClick();
                }
            },1);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (restoreState !=  null){
            layoutManager.onRestoreInstanceState(restoreState);
        }
    }

    public static ListFragment newInstance(Parcelable state, Step[] steps) {

        Bundle args = new Bundle();

        args.putParcelable(RECYCLER_VIEW_POSITION, state);
        args.putParcelableArray(CAKE_STEPS, steps);

        ListFragment fragment = new ListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private int getStepPositionFromArray(Step step) {
        int result = 0;
        for (int i = 0; i < stepsFromActivity.length; i++) {
            if (stepsFromActivity[i].equals(step)) {
                result = i;
                return result;
            }
        }
        return -1;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callbackForPosition = (SendPositionToActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + getString(R.string.implement_send_position_to_activity));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        callbackForPosition.listRecyclerViewPosition(layoutManager.onSaveInstanceState());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
