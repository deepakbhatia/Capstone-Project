package com.obelix.receiptsbox;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import static com.obelix.receiptsbox.ReceiptItemContract.AUTHORITY;

public class ReceiptContentProvider extends ContentProvider {
    private ReceiptItemsOpenHelper mOpenHelper;

    public ReceiptContentProvider() {
    }




    private static SQLiteQueryBuilder sReceiptQueryBuilder = null;

    static {
        sReceiptQueryBuilder = new SQLiteQueryBuilder();

        sReceiptQueryBuilder.setTables(ReceiptItemContract.ReceiptItems.TABLE_NAME);

    }
    /**
     * The content URI for this table.
     */

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/receipts";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/receipt";


    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);


    private static final String BASE_PATH = "receipts";


    private static final int RECEIPTS = 1003;

    private static final int RECEIPT_ID = 1004;

    private static final int RECEIPTS_DATE = 1008;

    private static final int RECEIPTS_TYPE_DATE = 1005;

    private static final int RECEIPTS_AMOUNT_DATE = 1006;

    private static final int RECEIPTS_AMOUNT_TYPE = 1007;


    private static final int RECEIPTS_ARCHIVED = 1009;

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, RECEIPTS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", RECEIPT_ID);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/types/*/#", RECEIPTS_TYPE_DATE);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/date/*", RECEIPTS_DATE);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/archived/*", RECEIPTS_ARCHIVED);

        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/amount/*/#", RECEIPTS_AMOUNT_TYPE);




    }

    //type = ?
    private static final String sTypeSelection =
           ReceiptItemContract.ReceiptItems.TABLE_NAME+
                    "." + ReceiptItemContract.ReceiptItems.COL_type + " = ? ";

    //type = ?
    private static final String sAmountSelection =
            ReceiptItemContract.ReceiptItems.TABLE_NAME+
                    "." + ReceiptItemContract.ReceiptItems.COL_amount + " >= ? ";

    private static final String sDaySelection =
            ReceiptItemContract.ReceiptItems.TABLE_NAME+
                    "." + ReceiptItemContract.ReceiptItems.COL_archived + " = ? AND " +
                    ReceiptItemContract.ReceiptItems.COL_date + " <= ? ";

    //type = ? AND date >= ?
    private static final String sTypeWithStartDateSelection =
            ReceiptItemContract.ReceiptItems.TABLE_NAME+
                    "." + ReceiptItemContract.ReceiptItems.COL_type + " = ? AND " +
                    ReceiptItemContract.ReceiptItems.COL_date + " <= ? ";
    //type = ? AND date >= ?
    private static final String sArchivedWithStartDateSelection =
            ReceiptItemContract.ReceiptItems.TABLE_NAME+
                    "." + ReceiptItemContract.ReceiptItems.COL_archived + " = ? AND " +
                    ReceiptItemContract.ReceiptItems.COL_date + " <= ? ";

    //type = ? AND date = ?
    private static final String sTypeAndDaySelection =
            ReceiptItemContract.ReceiptItems.TABLE_NAME+
                    "." + ReceiptItemContract.ReceiptItems.COL_type + " = ? AND " +
                    ReceiptItemContract.ReceiptItems.COL_date + " = ? ";

    //amount = ? AND date = ?
    private static final String sAmountAndDaySelection =
            ReceiptItemContract.ReceiptItems.TABLE_NAME+
                    "." + ReceiptItemContract.ReceiptItems.COL_amount + " >= ? AND " +
                    ReceiptItemContract.ReceiptItems.COL_date + " = ? ";

    //type = ? AND amount = ?
    private static final String sAmountAndTypeSelection =
            ReceiptItemContract.ReceiptItems.TABLE_NAME+
                    "." + ReceiptItemContract.ReceiptItems.COL_amount + " >= ? AND " +
                    ReceiptItemContract.ReceiptItems.COL_type + " = ? ";


    private Cursor getReceiptsByDate(Uri uri, String[] projection, String sortOrder) {
        long startDate = ReceiptItemContract.ReceiptItems.getDateFromUri(uri,2);

        String[] selectionArgs;
        String selection;

        {
            selectionArgs = new String[]{String.valueOf(0),Long.toString(startDate)};
            selection = sDaySelection;
        }

        return sReceiptQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getArchivedReceiptsByDate(Uri uri, String[] projection, String sortOrder) {
        long startDate = ReceiptItemContract.ReceiptItems.getDateFromUri(uri,2);

        String[] selectionArgs;
        String selection;

        {
            selectionArgs = new String[]{String.valueOf(1),Long.toString(startDate)};
            selection = sArchivedWithStartDateSelection;
        }

        return sReceiptQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getReceiptsByTypeAndDate(Uri uri, String[] projection, String sortOrder) {
        String type = ReceiptItemContract.ReceiptItems.getReceiptTypeFromUri(uri);
        long startDate = ReceiptItemContract.ReceiptItems.getDateFromUri(uri,3);

        String[] selectionArgs;
        String selection;

        if (startDate == 0) {
            selection = sTypeSelection;
            selectionArgs = new String[]{type};
        } else {
            selectionArgs = new String[]{type, Long.toString(startDate)};
            selection = sTypeWithStartDateSelection;
        }

        return mOpenHelper.getReadableDatabase().query(
                ReceiptItemContract.ReceiptItems.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getReceiptsByTypeAndAmount(Uri uri, String[] projection, String sortOrder) {
        String type = ReceiptItemContract.ReceiptItems.getReceiptTypeFromUri(uri);
        double amount = ReceiptItemContract.ReceiptItems.getAmountFromUri(uri);

        String[] selectionArgs;
        String selection;

       {
           selectionArgs = new String[]{Double.toString(amount), type};
            selection = sAmountAndTypeSelection;
        }

        return sReceiptQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }


    private Cursor getReceiptsByAmountAndDate(Uri uri, String[] projection, String sortOrder) {
        double amount = ReceiptItemContract.ReceiptItems.getAmountFromUri(uri);
        long startDate = ReceiptItemContract.ReceiptItems.getDateFromUri(uri,1);

        String[] selectionArgs;
        String selection;

        if (startDate == 0) {
            selection = sAmountSelection;
            selectionArgs = new String[]{Double.toString(amount)};
        } else {
            selectionArgs = new String[]{Double.toString(amount), Long.toString(startDate)};
            selection = sAmountAndDaySelection;
        }

        return sReceiptQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sURIMatcher.match(uri);
        int rowsDeleted;

        Log.d("RECEIPT_ID",""+uri);

        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {

            case RECEIPT_ID:

                rowsDeleted = db.delete(
                        ReceiptItemContract.ReceiptItems.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.

        final int match = sURIMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case RECEIPTS:
                return CONTENT_TYPE;
            case RECEIPT_ID:
                return CONTENT_ITEM_TYPE;
            case RECEIPTS_AMOUNT_DATE:
                return CONTENT_TYPE;
            case RECEIPTS_AMOUNT_TYPE:
                return CONTENT_TYPE;
            case RECEIPTS_TYPE_DATE:
                return CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sURIMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case RECEIPTS: {
                long _id = db.insert(ReceiptItemContract.ReceiptItems.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ReceiptItemContract.ReceiptItems.buildReceipt(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        mOpenHelper = new ReceiptItemsOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sURIMatcher.match(uri)) {

            case RECEIPTS_ARCHIVED:{

                retCursor = getArchivedReceiptsByDate(uri, projection, sortOrder);
                break;
            }
            // "weather/*/*"
            case RECEIPTS_AMOUNT_DATE:
            {
                retCursor = getReceiptsByAmountAndDate(uri, projection, sortOrder);
                break;
            }
            // "weather/*"
            case RECEIPTS_AMOUNT_TYPE: {
                retCursor = getReceiptsByTypeAndAmount(uri, projection, sortOrder);
                break;
            }
            // "weather"
            case RECEIPTS_TYPE_DATE: {

                retCursor = getReceiptsByTypeAndDate(uri, projection, sortOrder);

                /*retCursor = mOpenHelper.getReadableDatabase().query(
                        ReceiptItemContract.ReceiptItems.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );*/
                break;
            }
            case RECEIPTS_DATE: {

                retCursor = getReceiptsByDate(uri, projection, sortOrder);
                break;
            }
            // "location"
            case RECEIPT_ID: {

                retCursor = getReceiptsByDate(uri, projection, sortOrder);
                /*retCursor = mOpenHelper.getReadableDatabase().query(
                        ReceiptItemContract.ReceiptItems.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );*/
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sURIMatcher.match(uri);
        int rowsUpdated;

        switch (match) {

            case RECEIPT_ID:
                rowsUpdated = db.update(ReceiptItemContract.ReceiptItems.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        Log.d("update",""+rowsUpdated);
        return rowsUpdated;
    }
}
