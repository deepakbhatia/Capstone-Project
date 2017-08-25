package com.obelix.receiptsbox;

import android.database.MatrixCursor;
import android.os.AsyncTask;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import static com.obelix.receiptsbox.Constants.receiptsEndpoint;

/**
 * Created by obelix on 23/08/2017.
 */

public class QueryFirebaseTask extends AsyncTask<String, Void, Void> {

    DatabaseReference mRef;



    public QueryCompleteInterface qInterface;

    public interface QueryCompleteInterface{
        public void queryCompleted(MatrixCursor matrixCursor);
    }
    @Override
    protected Void doInBackground(String... params) {

        String sortBy = params[0];

        String sortByValue = params[1];


        mRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Constants.baseUrl+receiptsEndpoint);

        mRef.orderByChild(sortBy)
                .equalTo(sortByValue)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {


                                MatrixCursor mc = new MatrixCursor(ReceiptItemFragment.PROJECTION_ALL); // properties from the JSONObjects

                                Iterator snapshotIt = dataSnapshot.getChildren().iterator();

                                while(snapshotIt.hasNext()){


                                    DataSnapshot currentItem = (DataSnapshot) snapshotIt.next();

                                    Receipt receipt = currentItem.getValue(Receipt.class);

                                    mc.addRow(new Object[] {
                                            receipt.receipt_id,
                                            receipt.title,
                                            receipt.type,
                                            receipt.date,
                                            receipt.place,
                                            receipt.amount,
                                            receipt.card_payment,
                                            receipt.archived,
                                            receipt.deleted,
                                            receipt.cloud_id

                                    });


                                }


                                qInterface.queryCompleted(mc);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }


                        });





        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
