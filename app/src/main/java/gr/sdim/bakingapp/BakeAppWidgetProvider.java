package gr.sdim.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class BakeAppWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, String recipyIngredients,
                                int appWidgetId) {


     //   SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
      //  int defaultValue = getResources().getInteger(R.integer.saved_high_score_default_key);
       // int highScore = sharedPref.getInt(getString(R.string.saved_high_score_key), defaultValue);

        // Create an Intent to launch MainActivity when clicked
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.bake_app_widget_provider);
        if (recipyIngredients == null || recipyIngredients.isEmpty()) { // update after the widget is loaded or after regular update
            SharedPreferences sharedPreferences = context.getSharedPreferences(String.valueOf(R.string.saved_recipy_ingredients_list), Context.MODE_PRIVATE);

            String ingredientsList = sharedPreferences.getString(String.valueOf(R.string.saved_recipy_ingredients_list), "");
            if (ingredientsList == null || ingredientsList.isEmpty()){   // if there is no list available. Usually after fist setup, before using the app
                views.setTextViewText(R.id.appwidget_text,"Please use the app to select a recipy!");
            } else {                                                    // if the recipy ingredients list is available
                views.setTextViewText(R.id.appwidget_text,ingredientsList);
            }

        } else {                                                        // if the widget is updated by the app
            views.setTextViewText(R.id.appwidget_text,recipyIngredients);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static void updateRecipyIngredients(Context context, AppWidgetManager appWidgetManager, String recipyIngredients, int[] appWidgetIds){
        for (int appWidgetId : appWidgetIds){
            updateAppWidget(context,appWidgetManager, recipyIngredients, appWidgetId);
        }
    }

}

