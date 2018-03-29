package com.stevensekler.baker.bakingapp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stevensekler.baker.bakingapp.R;
import com.stevensekler.baker.bakingapp.adapters.StepAdapter;
import com.stevensekler.baker.bakingapp.model.Cake;
import com.stevensekler.baker.bakingapp.model.Step;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.stevensekler.baker.bakingapp.FragmentsActivity.CAKE_STEPS;
import static com.stevensekler.baker.bakingapp.MainActivity.CAKE_OBJECT;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {

    @BindView(R.id.step_recycler_view)
    RecyclerView stepRecyclerView;
    private StepAdapter stepAdapter;
    private Unbinder unbinder;
    private List<Step> stepsFromActivity;

    public ListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        if (getArguments() != null){
            Step[] newSteps = (Step[]) getArguments().getParcelableArray(CAKE_STEPS);
            stepsFromActivity = parcelableArrayToListArray(newSteps);
            stepsFromActivity = addingStepForIngredients(stepsFromActivity);
        }
        setupStepRecyclerView(view);
        return view;
    }

    /** Sets up a RecyclerView to display the steps to make a cake. */
    private void setupStepRecyclerView(View view){
        // Butterknife is distributed under Apache License, Version 2.0
        unbinder = ButterKnife.bind(this, view);

        stepRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        stepRecyclerView.setLayoutManager(layoutManager);
//        Based on: https://antonioleiva.com/recyclerview-listener/
        if (stepsFromActivity != null) {
            stepAdapter = new StepAdapter(stepsFromActivity, new StepAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Step step) {

                }
            });
            stepRecyclerView.setAdapter(stepAdapter);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private List<Step> parcelableArrayToListArray (Step[] steps){
        List<Step> result = new ArrayList<>();
        for (int i = 0; i < steps.length; i++){
            result.add(steps[i]);
        }
        return result;
    }

    private List<Step> addingStepForIngredients (List<Step> steps){
        List<Step> result = new ArrayList<>();
        Step ingredients = new Step();

        ingredients.setId(0);
        ingredients.setShortDescription("Ingredients");

        result.add(ingredients);
        result.addAll(steps);

        return result;
    }
}
