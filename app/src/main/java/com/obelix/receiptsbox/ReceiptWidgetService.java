package com.obelix.receiptsbox; /**
 * Created by obelix on 03/12/2016.
 */

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import static com.obelix.receiptsbox.ReceiptItemContract.ReceiptItems.PROJECTION_ALL;

/**
 * This is the service that provides the factory to be bound to the collection service.
 */
public class ReceiptWidgetService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ReceiptRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

/**
 * This is the factory that will provide data to the collection widget.
 */
class ReceiptRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context mContext;
    private Cursor mCursor;

    private final int mAppWidgetId;

    public ReceiptRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public void onCreate() {
        // Since we reload the cursor in onDataSetChanged() which gets called immediately after
        // onCreate(), we do nothing here.
    }

    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    public int getCount() {
        return mCursor.getCount();
    }

    public RemoteViews getViewAt(int position) {
        // Get the data for this position from the content provider

        String receiptTitle;
        String receiptPrice;

        String receiptDate;
        String receiptType;

        String receiptPlace;


        String stockHistory = "empty";
        final int itemId = R.layout.list_receipt_widget_item;

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), itemId);


        if (mCursor.moveToPosition(position)) {

            receiptTitle = mCursor.getString(mCursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_title));
            receiptPrice = mCursor.getString(mCursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_amount));
            receiptDate = mCursor.getString(mCursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_date));

            receiptType = mCursor.getString(mCursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_type));

            receiptPlace = mCursor.getString(mCursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_place));


            // Set Stock Value for layout item with the Price, Symbol & Stock Change
            rv.setTextViewText(R.id.receiptTitle, String.valueOf(receiptTitle));


            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);


            rv.setTextViewText(R.id.receiptPrice, sp.getString(mContext.getString(R.string.currency_key), mContext.getString(R.string.default_currency)).concat(" ") + String.valueOf(receiptPrice));
            rv.setTextViewText(R.id.receiptDate, ReceiptsAdapter.getDate(Long.parseLong(String.valueOf(receiptDate))));


            // Set the click intent so that we can handle it and show a toast message
            final Intent fillInIntent = new Intent();
            final Bundle extras = new Bundle();
            extras.putString(DbSchema.COL_amount, receiptPrice);
            extras.putString(DbSchema.COL_title, receiptTitle);

            extras.putString(DbSchema.COL_date, receiptDate);

            extras.putString(DbSchema.COL_amount, receiptPrice);
            extras.putString(DbSchema.COL_type, receiptType);
            extras.putString(DbSchema.COL_place, receiptPlace);

            fillInIntent.putExtras(extras);
            rv.setOnClickFillInIntent(R.id.receipt_widget_item, fillInIntent);
        }

        return rv;
    }

    public RemoteViews getLoadingView() {
        // We aren't going to return a default loading view in this sample
        return null;
    }

    public int getViewTypeCount() {
        // Technically, we have two types of views (the dark and light background views)
        return 2;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
        // Refresh the cursor
        if (mCursor != null) {
            mCursor.close();
        }
        String sortOrder = ReceiptItemContract.ReceiptItems.COL_date + " DESC";

        Uri receiptUri = ReceiptItemContract.ReceiptItems.buildReceiptWithDate(
                System.currentTimeMillis());

        mCursor = mContext.getContentResolver().query(receiptUri, PROJECTION_ALL, null,
                null, sortOrder);

    }
}