package com.stevensekler.baker.bakingapp.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.List;

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
    private StepAdapter stepAdapter;
    private Unbinder unbinder;
    private List<Step> stepsFromActivity;
    public static final String RECYCLER_VIEW_POSITION = "position";
    public static final String STEP_OBJECT = "step_object";
    public static final String DESCRIPTION_FRAGMENT_DISPLAYED = "description_fragment_displayed";
    public static final String DESCRIPTION_FRAGMENT = "description_fragment";


    public ListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getArguments() != null) {
            Step[] newSteps = (Step[]) getArguments().getParcelableArray(CAKE_STEPS);
            stepsFromActivity = parcelableArrayToListArray(newSteps);
        }

        View view = inflater.inflate(R.layout.fragment_list, container, false);

        setupStepRecyclerView(view, savedInstanceState);

        return view;
    }

    /**
     * Sets up a RecyclerView to display the steps to make a cake.
     */
    private void setupStepRecyclerView(View view, Bundle state) {
        // Butterknife is distributed under Apache License, Version 2.0
        unbinder = ButterKnife.bind(this, view);

        stepRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        stepRecyclerView.setLayoutManager(layoutManager);
//        Based on: https://antonioleiva.com/recyclerview-listener/
        if (stepsFromActivity != null) {
            stepAdapter = new StepAdapter(stepsFromActivity, new StepAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Step step) {
                    Bundle args = new Bundle();
                    args.putParcelable(STEP_OBJECT, step);

                        DescriptionFragment descriptionFragment = new DescriptionFragment();
                        descriptionFragment.setArguments(args);

                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, descriptionFragment, DESCRIPTION_FRAGMENT)
                                .commit();
                }
            });
            stepRecyclerView.setAdapter(stepAdapter);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            scrollToPosition(savedInstanceState.getInt(RECYCLER_VIEW_POSITION));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private List<Step> parcelableArrayToListArray(Step[] steps) {
        List<Step> result = new ArrayList<>();
        for (int i = 0; i < steps.length; i++) {
            result.add(steps[i]);
        }
        return result;
    }

    private void scrollToPosition(final int position) {
            stepRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    if (layoutManager != null)
                        layoutManager.scrollToPosition(position);
                }
            });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(RECYCLER_VIEW_POSITION,layoutManager.findFirstCompletelyVisibleItemPosition());
    }
}
