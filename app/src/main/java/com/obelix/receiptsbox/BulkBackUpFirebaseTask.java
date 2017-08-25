package com.obelix.receiptsbox;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by obelix on 24/08/2017.
 */

public class BulkBackUpFirebaseTask extends AsyncTask<Cursor, Void, Boolean> {

    private Context mContext;

    private DatabaseReference mDatabase;

    public BulkBackUpFirebaseTask(Context context){

        this.mContext = context;
    }

    @Override
    protected Boolean doInBackground(Cursor[] objects) {

        mDatabase = FirebaseDatabase.getInstance().getReference("receipts");

        Cursor cursor =   objects[0];
        while (!cursor.isAfterLast()){

            setData(cursor);

            cursor.moveToNext();
        }
        return false;
    }

     Map<String, Object> receiptValue ;

    ContentValues receiptValueContent ;

    private void setData(Cursor cursor) {

        receiptValue = new HashMap<>();


        receiptValue.put(ReceiptItemContract.ReceiptItems.COL_ID, cursor.getLong(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_ID)));

        receiptValue.put(ReceiptItemContract.ReceiptItems.COL_amount, cursor.getDouble(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_amount)));
        receiptValue.put(ReceiptItemContract.ReceiptItems.COL_archived, cursor.getInt(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_archived)));
        receiptValue.put(ReceiptItemContract.ReceiptItems.COL_card_payment, cursor.getInt(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_card_payment)));
        receiptValue.put(ReceiptItemContract.ReceiptItems.COL_date, cursor.getLong(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_date)));
        receiptValue.put(ReceiptItemContract.ReceiptItems.COL_deleted, cursor.getInt(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_deleted)));
        receiptValue.put(ReceiptItemContract.ReceiptItems.COL_place, cursor.getString(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_place)));
        receiptValue.put(ReceiptItemContract.ReceiptItems.COL_title, cursor.getString(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_title)));
        receiptValue.put(ReceiptItemContract.ReceiptItems.COL_type, cursor.getString(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_type)));
        receiptValue.put(AddReceipt.FIREBASE_DATE_SORT_KEY, -Long.valueOf(cursor.getLong(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_date))));


        receiptValueContent = new ContentValues();


        receiptValueContent.put(ReceiptItemContract.ReceiptItems.COL_ID, cursor.getLong(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_ID)));

        receiptValueContent.put(ReceiptItemContract.ReceiptItems.COL_amount, cursor.getDouble(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_amount)));
        receiptValueContent.put(ReceiptItemContract.ReceiptItems.COL_archived, cursor.getInt(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_archived)));
        receiptValueContent.put(ReceiptItemContract.ReceiptItems.COL_card_payment, cursor.getInt(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_card_payment)));
        receiptValueContent.put(ReceiptItemContract.ReceiptItems.COL_date, cursor.getLong(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_date)));
        receiptValueContent.put(ReceiptItemContract.ReceiptItems.COL_deleted, cursor.getInt(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_deleted)));
        receiptValueContent.put(ReceiptItemContract.ReceiptItems.COL_place, cursor.getString(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_place)));
        receiptValueContent.put(ReceiptItemContract.ReceiptItems.COL_title, cursor.getString(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_title)));
        receiptValueContent.put(ReceiptItemContract.ReceiptItems.COL_type, cursor.getString(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_type)));

        if(Constants.authenticated){
            String key = mDatabase.push().getKey();

            receiptValue.put(ReceiptItemContract.ReceiptItems.COL_cloud_id, key);

            receiptValueContent.put(ReceiptItemContract.ReceiptItems.COL_cloud_id,key);


            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put( key, receiptValue);



            //childUpdates.put("/user-posts/" + userId + "/" + key, postValues);
            //mDatabase.setValue(receiptValue);

            mDatabase.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError == null){
                        long local_db_item_id = Long.parseLong(""+receiptValue.get(ReceiptItemContract.ReceiptItems.COL_ID));
                        mContext.getContentResolver().update(ReceiptItemContract.ReceiptItems.buildReceipt(Long.parseLong(""+receiptValue.get(ReceiptItemContract.ReceiptItems.COL_ID))),receiptValueContent, ReceiptItemContract.ReceiptItems.TABLE_NAME+
                                "." + ReceiptItemContract.ReceiptItems.COL_ID + " = ?",new String[]{String.valueOf(local_db_item_id)});

                    }
                }
            });
        }


    }
}
