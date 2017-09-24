package com.obelix.receiptsbox;

import android.provider.BaseColumns;

interface DbSchema {

    String DB_NAME = "receipts.db";

    String TBL_ITEMS = "receipts";

    String COL_ID = BaseColumns._ID;
    String COL_title = "title";

    String COL_type = "type";

    String COL_date = "date";
    String COL_place = "place";
    String COL_amount = "amount";

    String COL_card_payment = "card_payment";
    String COL_archived = "archived";

    String COL_deleted = "deleted";

    String COL_cloud_id = "cloud_id";

    // BE AWARE: Normally you would store the LOOKUP_KEY
    // of a contact from the device. But this would
    // have needless complicated the sample. Thus I
    // omitted it.
    String DDL_CREATE_TBL_ITEMS =
            "CREATE TABLE receipts (" +
                    "_id           INTEGER  PRIMARY KEY AUTOINCREMENT, \n" +
                    "title     TEXT,\n" +
                    "type     TEXT,\n" +
                    "date     INTEGER NOT NULL,\n" +
                    "place     TEXT,\n" +
                    "amount     REAL NOT NULL,\n" +
                    "card_payment      INTEGER DEFAULT 1,\n" +
                    "archived      INTEGER DEFAULT 0,\n" +
                    "deleted      INTEGER DEFAULT 0,\n" +
                    "cloud_id      TEXT DEFAULT NOT_BACKED\n" +
                    ")";

    String DDL_DROP_TBL_ITEMS =
            "DROP TABLE IF EXISTS receipts";


    String DML_WHERE_ID_CLAUSE = "_id = ?";

    String DEFAULT_TBL_ITEMS_SORT_ORDER = "name ASC";

}