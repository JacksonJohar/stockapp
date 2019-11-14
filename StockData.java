package com.johar.Johar_Stocks;

import android.provider.BaseColumns;

public class StockData {
    private StockData() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "stocks";
        public static final String COLUMN_NAME_SYMBOL = "title";
        public static final String COLUMN_NAME_TITLE = "subtitle";
        public static final float COLUMN_NAME_PRICE = 0;
        public static final float COLUMN_NAME_CHANGE = 0;
        public static final float COLUMN_NAME_PERCENT = 0;
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
                    FeedEntry.COLUMN_NAME_SYMBOL + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
}
