package com.mzdhr.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.mzdhr.bakingapp.ui.activity.MainActivity;
import com.mzdhr.bakingapp.R;

/**
 * Implementation of App Widget functionality.
 */
public class BakingAppWidgetProvider extends AppWidgetProvider {

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, String titleText, String bodyText) {

        if (titleText == null) {
            titleText = context.getString(R.string.add_widget_help);
        }

        if (bodyText == null) {
            bodyText = context.getString(R.string.app_name);
        }

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget_provider);
        views.setTextViewText(R.id.ingredient_appwidget_text_textView, bodyText);
        views.setTextViewText(R.id.title_appwidget_text_textView, titleText);

        // Create an Intent to launch Activity when clicked
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // onClick
        views.setOnClickPendingIntent(R.id.ingredient_appwidget_text_textView, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, context.getString(R.string.app_name), context.getString(R.string.add_widget_help));
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

