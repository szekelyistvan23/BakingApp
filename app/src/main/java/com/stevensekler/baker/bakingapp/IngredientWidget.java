package com.stevensekler.baker.bakingapp;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import com.stevensekler.baker.bakingapp.utils.Methods;

import java.util.Map;

import static com.stevensekler.baker.bakingapp.IngredientWidgetConfigureActivity.loadTitlePref;
import static com.stevensekler.baker.bakingapp.fragments.DescriptionFragment.FIRST_ITEM_FROM_ARRAY;
import static com.stevensekler.baker.bakingapp.IngredientWidgetConfigureActivity.PREF_PREFIX_KEY;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link IngredientWidgetConfigureActivity IngredientWidgetConfigureActivity}
 */
public class IngredientWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        String getMap = loadTitlePref(context, appWidgetId);
        @SuppressWarnings("unchecked")
        Map<String, String> mapWithText = (Map<String, String>) Methods.stringToObject(getMap);
        String key = mapWithText.keySet().toArray()[FIRST_ITEM_FROM_ARRAY].toString();
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredient_widget);
        views.setTextViewText(R.id.cake_name, key);
        views.setTextViewText(R.id.cake_ingredients, mapWithText.get(key));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
//            IngredientWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
            Methods.deleteDataFromSharedPreferences(context, PREF_PREFIX_KEY + appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

