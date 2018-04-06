package com.stevensekler.baker.bakingapp.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.stevensekler.baker.bakingapp.R;
import com.stevensekler.baker.bakingapp.model.Step;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.stevensekler.baker.bakingapp.MainActivity.CAKE_OBJECT;
import static com.stevensekler.baker.bakingapp.fragments.ListFragment.DESCRIPTION_FRAGMENT;
import static com.stevensekler.baker.bakingapp.fragments.ListFragment.DESCRIPTION_FRAGMENT_DISPLAYED;
import static com.stevensekler.baker.bakingapp.fragments.ListFragment.STEP_ARRAY;
import static com.stevensekler.baker.bakingapp.fragments.ListFragment.STEP_ARRAY_POSITION;
import static com.stevensekler.baker.bakingapp.fragments.ListFragment.STEP_ARRAY_SIZE;
import static com.stevensekler.baker.bakingapp.fragments.ListFragment.STEP_OBJECT;

/**
 * A simple {@link Fragment} subclass.
 */
public class DescriptionFragment extends Fragment {
    @BindView(R.id.step_description)
    TextView stepDescription;
    @BindView(R.id.previous_button)
    Button previousButton;
    @BindView(R.id.next_button)
    Button nextButton;
    private Unbinder unbinder;
    private int arrayPosition;
    private Step[] steps;

    PassDataToActivity callback;

    public DescriptionFragment() {
    }

    public interface PassDataToActivity{
        void descriptionFragmentDisplayed(boolean state);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_description, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (getArguments().containsKey(STEP_ARRAY_POSITION)) {
            arrayPosition = getArguments().getInt(STEP_ARRAY_POSITION);
        }

        if (getArguments().containsKey(STEP_ARRAY)) {
            steps = (Step[]) getArguments().getParcelableArray(STEP_ARRAY);
        }

        if (stepDescription != null && steps != null){
            stepDescription.setText(steps[arrayPosition].getDescription());
        }

        if (callback != null) {
            callback.descriptionFragmentDisplayed(true);
        }

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayPosition > 0){
                    arrayPosition--;
                    createNewDescriptionFragment(arrayPosition);
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayPosition < steps.length - 1) {
                    arrayPosition++;
                    createNewDescriptionFragment(arrayPosition);
                }
            }
        });
        return view;
    }

    private void createNewDescriptionFragment(int position){
        Bundle args = new Bundle();
        args.putInt(STEP_ARRAY_POSITION, position);
        args.putParcelableArray(STEP_ARRAY, steps);


        DescriptionFragment descriptionFragment = new DescriptionFragment();
        descriptionFragment.setArguments(args);

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, descriptionFragment, DESCRIPTION_FRAGMENT)
                .commit();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (arrayPosition == 0){
            previousButton.setVisibility(View.GONE);
        }

        if (arrayPosition == steps.length - 1){
            nextButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (PassDataToActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement PassDataToActivity");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
