package com.stevensekler.baker.bakingapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stevensekler.baker.bakingapp.R;
import com.stevensekler.baker.bakingapp.model.Step;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.stevensekler.baker.bakingapp.FragmentsActivity.INGREDIENTS_SHORT_DESCRIPTION;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder>{

    private Step[] stepArray;
    private OnItemClickListener stepListener;
    public static final String DOT_AND_SPACE = ". ";

    public StepAdapter(Step[] stepArray, OnItemClickListener stepListener) {
        this.stepArray = stepArray;
        this.stepListener = stepListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Step step);
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.step_item, parent, false);
        return new StepAdapter.StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, final int position) {
        String description;
        if (stepArray[position].getShortDescription().equals(INGREDIENTS_SHORT_DESCRIPTION)){
            description = stepArray[position].getShortDescription();
        } else {
            description = stepArray[position].getId() +
                    DOT_AND_SPACE + stepArray[position].getShortDescription();
        }
        holder.shortDescription.setText(description);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepListener.onItemClick(stepArray[position]);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stepArray.length;
    }

    class StepViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.step_short_description)
        TextView shortDescription;

        public StepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
