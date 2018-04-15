package com.stevensekler.baker.bakingapp.utils;

import android.content.Context;

import com.stevensekler.baker.bakingapp.R;
import com.stevensekler.baker.bakingapp.model.Cake;

import java.util.List;

public class Methods {
    public static final String NUTELLA_PIE = "Nutella Pie";
    public static final String BROWNIES = "Brownies";
    public static final String YELLOW_CAKE = "Yellow Cake";
    public static final String CHEESECAKE = "Cheesecake";

    public static int selectImage(List<Cake> cakes, int position){
        int result = 0;
        String cake = cakes.get(position).getName();
        switch (cake){
            case NUTELLA_PIE:
                result = R.drawable.nutellapie;
                break;
            case BROWNIES:
                result = R.drawable.brownie;
                break;
            case YELLOW_CAKE:
                result = R.drawable.yellowcake;
                break;
            case CHEESECAKE:
                result = R.drawable.cheesecake;
                break;
        }
        return result;
    }
    public static boolean isTablet(Context context){
        boolean result = context.getResources().getBoolean(R.bool.isTablet);
        if (result){
            return true;
        } else {
            return false;
        }
    }
}
