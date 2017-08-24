package com.obelix.receiptsbox;

import android.os.AsyncTask;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by obelix on 23/08/2017.
 */

public class QueryFirebaseTask extends AsyncTask<String, Void, Void> {

    DatabaseReference mRef;

    @Override
    protected Void doInBackground(String... params) {

        String sortBy = params[0];

        String sortByValue = params[1];

        mRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://mymap-3fd93.firebaseio.com/Users");

        mRef.orderByChild(sortBy)
                .equalTo(sortByValue)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }


                        });



        return null;
    }
}
