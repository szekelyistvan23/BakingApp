package com.stevensekler.baker.bakingapp;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stevensekler.baker.bakingapp.utils.Methods;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.stevensekler.baker.bakingapp.utils.Methods.NUTELLA_PIE;

/**
 * The configuration screen for the {@link IngredientWidget IngredientWidget} AppWidget.
 */
public class IngredientWidgetConfigureActivity extends Activity {
    @BindView(R.id.radio_button_nutella_pie)
    RadioButton radioButtonNutellaPie;
    @BindView(R.id.radio_button_brownies)
    RadioButton radioButtonBrownies;
    @BindView(R.id.radio_button_yellow_cake)
    RadioButton radioButtonYellowCake;
    @BindView(R.id.radio_button_cheesecake)
    RadioButton radioButtonCheesecake;
    @Nullable
    @BindView(R.id.cake_ingredients)
    TextView mAppWidgetText;
    @Nullable
    @BindView(R.id.cake_name)
    TextView mAppWidgetTitle;

    private static final String PREFS_NAME = "com.stevensekler.baker.bakingapp.IngredientWidget";
    public static final String PREF_PREFIX_KEY = "appwidget_";
    public static final String NUTELLA_PIE_INGREDIENTS = "2 CUP Graham Cracker crumbs \n" +
                                                        "6 TBLSP unsalted butter, melted\n" +
                                                        "0.5 CUP granulated sugar\n" +
                                                        "1.5 TSP salt\n" +
                                                        "5 TBLSP vanilla\n" +
                                                        "1 K Nutella or other chocolate-hazelnut spread\n" +
                                                        "500 G Mascapone Cheese(room temperature)\n" +
                                                        "1 CUP heavy cream(cold)\n" +
                                                        "4 OZ cream cheese(softened)";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    @OnClick(R.id.add_button)
        public void onClick(View v) {
            final Context context = IngredientWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            String widgetText = getCheckedRadioButton();
            Methods.saveToSharedPreferences(context, PREF_PREFIX_KEY + mAppWidgetId, widgetText);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            IngredientWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
    }

    public IngredientWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, gets the default from constants
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String titleValue = Methods.readFromSharedPreferences(context, PREF_PREFIX_KEY + appWidgetId);

        if (titleValue != null) {
            return titleValue;
        } else {
            Map<String, String> defaultMap = new HashMap<>();
            defaultMap.put(NUTELLA_PIE, NUTELLA_PIE_INGREDIENTS);
            return mapToString(defaultMap);
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.ingredient_widget_configure);

        ButterKnife.bind(this);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
        if (mAppWidgetText != null && mAppWidgetTitle !=null) {
            String getMap = loadTitlePref(IngredientWidgetConfigureActivity.this, mAppWidgetId);
            Map<String, String> mapWithText = stringToMap(getMap);
            String key = mapWithText.keySet().toArray()[0].toString();

            mAppWidgetTitle.setText(key);
            mAppWidgetText.setText(mapWithText.get(key));
        }
    }
    /** Loads the ingredients for the selected cake */
    private String getCheckedRadioButton(){
        Map<String, String> cakeMap = new HashMap<String, String>();
            if (radioButtonNutellaPie.isChecked()) {
                cakeMap.put(getResources().getText(R.string.nutella_pie).toString(),
                        getResources().getText(R.string.nutella_pie_ingredients).toString());
            } else if (radioButtonBrownies.isChecked()) {
                cakeMap.put(getResources().getText(R.string.brownies).toString(),
                        getResources().getText(R.string.brownies_ingredients).toString());
            } else if (radioButtonYellowCake.isChecked()) {
                cakeMap.put(getResources().getText(R.string.yellow_cake).toString(),
                        getResources().getText(R.string.yellow_cake_ingredients).toString());
            } else if (radioButtonCheesecake.isChecked()) {
                cakeMap.put(getResources().getText(R.string.cheesecake).toString(),
                        getResources().getText(R.string.cheesecake_ingredients).toString());
            }
        return mapToString(cakeMap);
    }
    /** Tranforms a Map object into a String.*/
    static String mapToString(Map<String, String> map){
        Gson gson = new Gson();
        return gson.toJson(map);
    }
    /** Tranforms a String object into a Map.*/
    static Map<String,String> stringToMap (String json){
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        return gson.fromJson(json, type);
    }
}

