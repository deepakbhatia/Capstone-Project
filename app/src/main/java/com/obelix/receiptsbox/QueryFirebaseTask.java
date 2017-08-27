package com.obelix.receiptsbox;

import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import static com.obelix.receiptsbox.Constants.receiptsEndpoint;
import static com.obelix.receiptsbox.ReceiptItemFragment.RECEIPT_SORT_TYPE;

/**
 * Created by obelix on 23/08/2017.
 */

public class QueryFirebaseTask extends AsyncTask<String, Void, Void> {

    DatabaseReference mRef;



    public QueryCompleteInterface qInterface;


    public QueryFirebaseTask(ReceiptItemFragment fragment){
        qInterface = fragment;
    }

    public interface QueryCompleteInterface{
        void queryCompleted(MatrixCursor matrixCursor);
    }

    static String sortBy;
    @Override
    protected Void doInBackground(String... params) {

        sortBy = params[0];

        final String sortByValue = params[1];

        mRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(Constants.baseUrl+receiptsEndpoint);

        Query qRef ;


        if(sortBy.equals(RECEIPT_SORT_TYPE)){
            qRef = mRef.child(Constants.uid).orderByChild(sortBy).equalTo(sortByValue); ;
        }else{
            qRef = mRef.child(Constants.uid).orderByChild(sortBy).endAt(Long.parseLong(sortByValue));
        }

        qRef.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                MatrixCursor mc = new MatrixCursor(ReceiptItemFragment.PROJECTION_ALL); // properties from the JSONObjects

                                Iterator snapshotIt = dataSnapshot.getChildren().iterator();

                                while(snapshotIt.hasNext()){


                                    DataSnapshot currentItem = (DataSnapshot) snapshotIt.next();

                                    Receipt receipt = currentItem.getValue(Receipt.class);

                                    if(receipt!=null ){
                                        if(!sortBy.equals(RECEIPT_SORT_TYPE) && receipt.date > Long.parseLong(sortByValue)){
                                            continue;
                                        }

                                        mc.addRow(new Object[] {
                                                receipt._id,
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


                                }


                                qInterface.queryCompleted(mc);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }


                        });





        return null;
    }

}
