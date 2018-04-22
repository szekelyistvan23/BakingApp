package com.stevensekler.baker.bakingapp;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    private static final String PREFS_NAME = "com.stevensekler.baker.bakingapp.IngredientWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    TextView mAppWidgetText;
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = IngredientWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            String widgetText = getCheckedRadioButton();
            saveTitlePref(context, mAppWidgetId, widgetText);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            IngredientWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public IngredientWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_cake_ingredients);
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.ingredient_widget_configure);
        mAppWidgetText = (TextView) findViewById(R.id.cake_ingredients);
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

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
        if (mAppWidgetText != null) {
            mAppWidgetText.setText(loadTitlePref(IngredientWidgetConfigureActivity.this, mAppWidgetId));
        }
    }

    private String getCheckedRadioButton(){
        ButterKnife.bind(this);
        String textToDisplay = "";
        if (radioButtonNutellaPie.isChecked()){
            textToDisplay = getResources().getText(R.string.nutella_pie_ingredients).toString();
        } else if (radioButtonBrownies.isChecked()){
            textToDisplay = getResources().getText(R.string.brownies_ingredients).toString();
        } else if (radioButtonYellowCake.isChecked()){
            textToDisplay = getResources().getText(R.string.yellow_cake_ingredients).toString();
        } else if (radioButtonCheesecake.isChecked()){
            textToDisplay = getResources().getText(R.string.cheesecake_ingredients).toString();
        }
        return textToDisplay;
    }
}

