package com.obelix.receiptsbox;

import android.content.ContentValues;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by obelix on 20/07/2017.
 */

public class Constants {

    public static HashMap<Long, ContentValues> selectedItems = new HashMap<>();

    public static HashMap<Integer, View> selectedItemViews = new HashMap<>();


    public static ArrayList<View> selectedItemViewsList = new ArrayList<>();


    public static Boolean authenticated = false;

    public static String baseUrl = "https://receiptsbox-e7d6f.firebaseio.com/";
    public static String receiptsEndpoint = "receipts";
    public static String RECEIPTS_UPDATED = "com.obelix.receiptsbox.RECEIPTS_UPDATED";

    public static String uid;
}
