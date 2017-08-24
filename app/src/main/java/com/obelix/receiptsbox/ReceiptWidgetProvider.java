package com.obelix.receiptsbox;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Created by obelix on 07/12/2016.
 */

public class ReceiptWidgetProvider extends AppWidgetProvider {

    private static final String CLICK_ACTION = "";

    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();

        if (action.equals(CLICK_ACTION)) {
                   Intent widgetIntentClick = new Intent(context, AddReceipt.class);
            widgetIntentClick.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(widgetIntentClick);
        }
        super.onReceive(context, intent);
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            Intent intent = new Intent(context, AddReceipt.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.add_receipt_widget);


            views.setOnClickPendingIntent(R.id.add_receipt_widget_action,pendingIntent);


            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}