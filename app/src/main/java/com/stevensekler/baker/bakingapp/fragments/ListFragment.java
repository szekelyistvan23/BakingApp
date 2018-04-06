package com.stevensekler.baker.bakingapp.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stevensekler.baker.bakingapp.FragmentsActivity;
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
    private int position;
    private Parcelable restoreState;
    public static final String RECYCLER_VIEW_POSITION = "position";
    public static final String STEP_OBJECT = "step_object";
    public static final String STEP_ARRAY_SIZE = "step_array_size";
    public static final String STEP_ARRAY_POSITION = "step_array_position";
    public static final String DESCRIPTION_FRAGMENT_DISPLAYED = "description_fragment_displayed";
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
            Step[] newSteps = (Step[]) getArguments().getParcelableArray(CAKE_STEPS);
            stepsFromActivity = parcelableArrayToListArray(newSteps);
            restoreState = getArguments().getParcelable(RECYCLER_VIEW_POSITION);
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
                    args.putInt(STEP_ARRAY_SIZE, stepAdapter.getItemCount());
                    args.putInt(STEP_ARRAY_POSITION, getStepPositionFromArray(step));


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
        if (restoreState !=  null){
            layoutManager.onRestoreInstanceState(restoreState);
        }
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

    public static Step[] arrayListToStepArray(List<Step> steps){
        Step[] result = new Step[steps.size()];
        for (int i = 0; i < steps.size(); i++){
            result[i] = steps.get(i);
        }
        return result;
    }

    public static ListFragment newInstance(Parcelable state, List<Step> steps) {

        Bundle args = new Bundle();

        args.putParcelable(RECYCLER_VIEW_POSITION, state);
        args.putParcelableArray(CAKE_STEPS, arrayListToStepArray(steps));

        ListFragment fragment = new ListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private int getStepPositionFromArray(Step step) {
        int result = 0;
        for (int i = 0; i < stepsFromActivity.size(); i++) {
            if (stepsFromActivity.get(i).equals(step)) {
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
            throw new ClassCastException(context.toString() + "must implement SendPositionToActivity");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(RECYCLER_VIEW_POSITION,layoutManager.findFirstCompletelyVisibleItemPosition());
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
