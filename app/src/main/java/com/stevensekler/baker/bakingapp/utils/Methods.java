package com.stevensekler.baker.bakingapp.utils;

/**
 * Contains methods used in various parts of the app.
 */

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
    /** Returns an image corresponding to a Cake object from the array.
     * @param cakes the array
     * @param position the object's index from the array
     * @return the resource id of the object
     * */
    public static int selectImage(List<Cake> cakes, int position){
        return returnCakeImageId(cakes.get(position).getName());
    }
    /** Checks if the device is a phone or a tablet.
     * @param context the context in which the method is used
     * @return true if is a tablet, false if is a phone
     * */
    public static boolean isTablet(Context context){
        boolean result = false;
        try {
            result = context.getResources().getBoolean(R.bool.isTablet);
        } catch (RuntimeException e){
            e.printStackTrace();
        }
        return result;
    }
    /** Takes in a cakes' name and return the corresponding image's id.
     * @param imageName the String with the image's name
     * @return the corresponding image resource id
     * */
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
