package com.obelix.receiptsbox;

import android.content.ContentValues;

import java.util.HashMap;

/**
 * Created by obelix on 19/07/2017.
 */

public class ReceiptActions {

    long id;
    boolean archive;
    boolean delete;
    ContentValues cv;
    HashMap<Long, ContentValues> selectedItems;

    public ReceiptActions(long id, boolean archive, boolean delete, ContentValues cv){
        this.id = id;

        this.archive = archive;

        this.delete = delete;

        this.cv = cv;
    }


    public ReceiptActions(boolean archive, boolean delete,  HashMap<Long, ContentValues> selectedItems){

        this.archive = archive;

        this.delete = delete;

        this.selectedItems = selectedItems;
    }
}
