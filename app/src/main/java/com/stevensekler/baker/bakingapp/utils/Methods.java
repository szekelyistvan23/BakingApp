package com.stevensekler.baker.bakingapp.utils;

/**
 * Contains methods used in various parts of the app.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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

    /** Saves a string to SharedPreferences.
     * @param context the context
     * @param key the key to save data
     * @param text ta actual data to be  saved
     * */
    public static void saveToSharedPreferences(Context context, String key, String text) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences != null && key !=  null && text != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, text);
            editor.apply();
        }
    }

    /** Reads data from Shared Preferences.
     * @param context the context
     * @param key the key to be read
     * @return null, if there is no saved data, this happens at the first run
     *  */
    public static String readFromSharedPreferences(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences != null) {
            return sharedPreferences.getString(key, null);
        }
        return null;
    }
}
