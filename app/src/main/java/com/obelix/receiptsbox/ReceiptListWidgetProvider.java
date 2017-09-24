package com.obelix.receiptsbox;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


/**
 * Created by obelix on 07/12/2016.
 */

public class ReceiptListWidgetProvider extends AppWidgetProvider {

    private static final String CLICK_ACTION = "";

    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();
        if (action.equals(Constants.RECEIPTS_UPDATED)) {
            final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            final ComponentName cn = new ComponentName(context, ReceiptListWidgetProvider.class);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.receipt_list_widget);

        }
        if (action.equals(CLICK_ACTION)) {
            Intent widgetIntentClick = new Intent(context, ReceiptDetailActivity.class);

            widgetIntentClick.putExtra(DbSchema.COL_amount, intent.getStringExtra(DbSchema.COL_amount));
            widgetIntentClick.putExtra(DbSchema.COL_title, intent.getStringExtra(DbSchema.COL_title));

            widgetIntentClick.putExtra(DbSchema.COL_date, intent.getStringExtra(DbSchema.COL_date));

            widgetIntentClick.putExtra(DbSchema.COL_amount, intent.getStringExtra(DbSchema.COL_amount));
            widgetIntentClick.putExtra(DbSchema.COL_type, intent.getStringExtra(DbSchema.COL_type));
            widgetIntentClick.putExtra(DbSchema.COL_place, intent.getStringExtra(DbSchema.COL_place));

            widgetIntentClick.setFlags(FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(widgetIntentClick);
        }
        super.onReceive(context, intent);
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, ReceiptWidgetService.class);
/*
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
*/

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.list_receipt_widget_layout);

            views.setRemoteAdapter(R.id.receipt_list_widget, intent);

            views.setEmptyView(R.id.receipt_list_widget, R.id.widget_error);

            final Intent onClickIntent = new Intent(context, ReceiptListWidgetProvider.class);
            onClickIntent.setAction(ReceiptListWidgetProvider.CLICK_ACTION);
            onClickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            //onClickIntent.setData(Uri.parse(onClickIntent.toUri(Intent.URI_INTENT_SCHEME)));
            final PendingIntent onClickPendingIntent = PendingIntent.getBroadcast(context, 0,
                    onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.receipt_list_widget, onClickPendingIntent);


            // Get the layout for the App Widget and attach an on-click listener
            // to the button


            //views.setOnClickFillInIntent(R.id.stock_list_widget, intent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
