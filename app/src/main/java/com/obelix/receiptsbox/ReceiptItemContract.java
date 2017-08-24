package com.obelix.receiptsbox;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by obelix on 16/06/2017.
 */

public class ReceiptItemContract {



    /**
     * The authority of the lentitems provider.
     */
    public static final String AUTHORITY =
            "com.obelix.receiptsbox";
    /**
     * The content URI for the top-level
     * lentitems authority.
     */
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY+ "/receipts");

    /**
     * Constants for the Items table
     * of the lentitems provider.
     */
    public static final class ReceiptItems
            implements BaseColumns {


        // Table name
        public static final String TABLE_NAME = "receipts";


        public static String COL_ID = BaseColumns._ID;
        public static String COL_title = "title";

        public static String COL_type = "type";

        public static String COL_date = "date";
        public static String COL_place = "place";
        public static String COL_amount = "amount";

        public static String COL_card_payment = "card_payment";
        public static String COL_archived = "archived";

        public static String COL_deleted = "deleted";

        public static String COL_cloud_id = "cloud_id";


        /**
         * The content URI for this table.
         */
        //public static final Uri CONTENT_URI =  Uri.withAppendedPath(ReceiptItemContract.CONTENT_URI, "receipts");
        /**
         * The mime type of a directory of items.
         *
         *
         */

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + "receipts";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + "receipt";


        /**
         * A projection of all columns in the items table.
         */
        public static final String[] PROJECTION_ALL = {
                _ID,
                DbSchema.COL_title,
                DbSchema.COL_type,
                DbSchema.COL_date,
                DbSchema.COL_place,
                DbSchema.COL_amount,
                DbSchema.COL_card_payment,
                DbSchema.COL_archived,
                DbSchema.COL_deleted};
        /**
         * The default sort order for queries containing NAME fields.
         */

        public static Uri buildReceipt(long receipt_id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(receipt_id)).build();
        }

        public static Uri buildReceiptWithDate(long date) {
            return CONTENT_URI.buildUpon()
                    .appendPath("date")
                    .appendPath(Long.toString(date)).build();
        }

        public static Uri buildArchivedReceiptWithDate(long date) {
            return CONTENT_URI.buildUpon()
                    .appendPath("archived")
                    .appendPath(Long.toString(date)).build();
        }

        public static Uri buildReceiptWithTypeAndDate(String type, long date) {
            return CONTENT_URI.buildUpon()
                    .appendPath("types")
                    .appendPath(type)
                    .appendPath(Long.toString(date))
                    .build();
        }

        public static Uri buildReceiptWithAmountAndDate(double amount, long date) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(date))
                    .appendPath(Double.toString(amount))
                    .build();
        }

        public static Uri buildReceiptWithAmountAndType(String type, double amount) {
            return CONTENT_URI.buildUpon()
                    .appendPath(type)
                    .appendPath(Double.toString(amount))
                    .build();
        }


        public static String getReceiptTypeFromUri(Uri uri) {

            Log.d("getReceiptTypeFromUri",uri+":"+uri.getPathSegments().get(1));
            return uri.getPathSegments().get(2);
        }

        public static long getDateFromUri(Uri uri, int position) {
            return Long.parseLong(uri.getPathSegments().get(position));
        }

        public static float getAmountFromUri(Uri uri) {
            return Float.parseFloat(uri.getPathSegments().get(2));
        }


    }


}
