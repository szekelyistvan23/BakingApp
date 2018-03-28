package com.stevensekler.baker.bakingapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stevensekler.baker.bakingapp.R;
import com.stevensekler.baker.bakingapp.model.Cake;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CakeAdapter extends RecyclerView.Adapter<CakeAdapter.CakeViewHolder> {
    private List<Cake> cakeArray;
    private OnItemClickListener cakeListener;

    public CakeAdapter(List<Cake> cakeArray, OnItemClickListener listener) {
        this.cakeArray = cakeArray;
        this.cakeListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Cake cake);
    }

    @Override
    public CakeAdapter.CakeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cake_item, parent, false);
        return new CakeAdapter.CakeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CakeAdapter.CakeViewHolder holder, int position) {
        holder.cakeName.setText(cakeArray.get(position).getName());
        holder.cakeIngredients.setText(extractIngredients(cakeArray.get(position)));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cakeListener.onItemClick(cakeArray.get(holder.getAdapterPosition()));
        }
    });
    }

    @Override
    public int getItemCount() {
        return cakeArray.size();
    }

    public void changeCakeData(List<Cake> newCakeArray) {
        cakeArray = newCakeArray;
        notifyDataSetChanged();
    }

    /** Retrieves the Ingredients from a Cake object to be displayed in the RecyclerView.*/
    private String extractIngredients (Cake cake){
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < cake.getIngredients().size(); i++){
            result.append(cake.getIngredients().get(i).getIngredient().toLowerCase());
            if (i < cake.getIngredients().size() - 1){
                result.append(", ");
            }
        }
        return result.toString();
    }

    class CakeViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.cake_name)
        TextView cakeName;
        @BindView(R.id.cake_ingredients)
        TextView cakeIngredients;

        public CakeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
