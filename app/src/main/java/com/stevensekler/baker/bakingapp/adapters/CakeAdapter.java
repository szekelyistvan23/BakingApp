package com.stevensekler.baker.bakingapp.adapters;

/**
 * Custom adapter for the RecyclerView from MainActivity.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.stevensekler.baker.bakingapp.R;
import com.stevensekler.baker.bakingapp.model.Cake;
import com.stevensekler.baker.bakingapp.utils.Methods;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CakeAdapter extends RecyclerView.Adapter<CakeAdapter.CakeViewHolder> {
    private List<Cake> cakeArray;
    private OnItemClickListener cakeListener;
    public static final String EMPTY_STRING = "";

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

        String url = cakeArray.get(position).getImage();
        if (!url.equals(EMPTY_STRING)) {
            Picasso.get()
                    .load(cakeArray.get(position).getImage())
                    .placeholder(R.drawable.placeholder)
                    .error(Methods.selectImage(cakeArray, position))
                    .into(holder.cakeImages);
        } else {
            holder.cakeImages.setImageResource(Methods.selectImage(cakeArray, position));
        }

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


    class CakeViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.cake_name)
        TextView cakeName;
        @BindView(R.id.cake_image)
        ImageView cakeImages;

        public CakeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
