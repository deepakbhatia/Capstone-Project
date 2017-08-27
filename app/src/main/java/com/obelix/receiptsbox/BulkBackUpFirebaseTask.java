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
        cursor.moveToFirst();

        int count = 0;
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


        long _id = cursor.getLong(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_ID));

        double _amount = cursor.getDouble(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_amount));

        int _archived = cursor.getInt(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_archived));

        int _card_payment = cursor.getInt(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_card_payment));

        long _date = cursor.getLong(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_date));

        int _deleted = cursor.getInt(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_deleted));

        String _place = cursor.getString(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_place));

        String _title = cursor.getString(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_title));

        String _type = cursor.getString(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_type));


        long _priority = Long.valueOf(cursor.getLong(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_date)));
        receiptValue.put(ReceiptItemContract.ReceiptItems.COL_ID, _id);

        receiptValue.put(ReceiptItemContract.ReceiptItems.COL_amount, _amount);
        receiptValue.put(ReceiptItemContract.ReceiptItems.COL_archived, _archived);
        receiptValue.put(ReceiptItemContract.ReceiptItems.COL_card_payment, _card_payment);
        receiptValue.put(ReceiptItemContract.ReceiptItems.COL_date, _date);
        receiptValue.put(ReceiptItemContract.ReceiptItems.COL_deleted, _deleted);
        receiptValue.put(ReceiptItemContract.ReceiptItems.COL_place, _place);
        receiptValue.put(ReceiptItemContract.ReceiptItems.COL_title, _title);
        receiptValue.put(ReceiptItemContract.ReceiptItems.COL_type, _type);
        receiptValue.put(AddReceipt.FIREBASE_DATE_SORT_KEY, _priority);


        receiptValueContent = new ContentValues();


        receiptValueContent.put(ReceiptItemContract.ReceiptItems.COL_ID, _id);

        receiptValueContent.put(ReceiptItemContract.ReceiptItems.COL_amount, _amount);
        receiptValueContent.put(ReceiptItemContract.ReceiptItems.COL_archived, _archived);
        receiptValueContent.put(ReceiptItemContract.ReceiptItems.COL_card_payment, _card_payment);
        receiptValueContent.put(ReceiptItemContract.ReceiptItems.COL_date, _date);
        receiptValueContent.put(ReceiptItemContract.ReceiptItems.COL_deleted, _deleted);
        receiptValueContent.put(ReceiptItemContract.ReceiptItems.COL_place, _place);
        receiptValueContent.put(ReceiptItemContract.ReceiptItems.COL_title, _title);
        receiptValueContent.put(ReceiptItemContract.ReceiptItems.COL_type, _type);
        //receiptValueContent.put(AddReceipt.FIREBASE_DATE_SORT_KEY, -Long.valueOf(cursor.getLong(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_date))));

        if(Constants.authenticated){
            String key = cursor.getString(cursor.getColumnIndex(ReceiptItemContract.ReceiptItems.COL_cloud_id));
            if(key == null)
                key = mDatabase.push().getKey();

            receiptValue.put(ReceiptItemContract.ReceiptItems.COL_cloud_id, key);

            receiptValueContent.put(ReceiptItemContract.ReceiptItems.COL_cloud_id,key);


            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put( key, receiptValue);



            //childUpdates.put("/user-posts/" + userId + "/" + key, postValues);
            //mDatabase.setValue(receiptValue);

            long local_db_item_id = _id;
            mContext.getContentResolver().update(ReceiptItemContract.ReceiptItems.buildReceipt(Long.parseLong(""+receiptValue.get(ReceiptItemContract.ReceiptItems.COL_ID))),receiptValueContent, ReceiptItemContract.ReceiptItems.TABLE_NAME+
                    "." + ReceiptItemContract.ReceiptItems.COL_ID + " = ?",new String[]{String.valueOf(local_db_item_id)});

            mDatabase.child(Constants.uid).updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError == null){

                    }
                }
            });
        }


    }
}
