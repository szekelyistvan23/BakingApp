package com.stevensekler.baker.bakingapp.utils;

import android.content.Context;

import com.stevensekler.baker.bakingapp.R;
import com.stevensekler.baker.bakingapp.model.Cake;

import java.util.List;

import static com.stevensekler.baker.bakingapp.FragmentsActivity.INITIALIZING_INT_VARIABLE;

public class Methods {
    public static final String NUTELLA_PIE = "Nutella Pie";
    public static final String BROWNIES = "Brownies";
    public static final String YELLOW_CAKE = "Yellow Cake";
    public static final String CHEESECAKE = "Cheesecake";

    public static int selectImage(List<Cake> cakes, int position){
        String cake = cakes.get(position).getName();
        return returnCakeImageId(cake);
    }
    public static boolean isTablet(Context context){
        boolean result = false;
        try {
            result = context.getResources().getBoolean(R.bool.isTablet);
        } catch (RuntimeException e){
            e.printStackTrace();
        }
        return result;
    }

    public static int returnCakeImageId(String imageName){
        int result = INITIALIZING_INT_VARIABLE;
        switch (imageName){
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
}
