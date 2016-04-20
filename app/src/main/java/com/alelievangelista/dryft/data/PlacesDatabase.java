package com.alelievangelista.dryft.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.alelievangelista.dryft.data.PlacesProvider.Tables;

/**
 * Created by aevangelista on 16-04-18.
 */
public class PlacesDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dryft.db";
    private static final int DATABASE_VERSION = 1;

    public PlacesDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.PLACES + " ("
                + PlacesContract.PlacesColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PlacesContract.PlacesColumns.PLACE_ID + " TEXT,"
                + PlacesContract.PlacesColumns.NAME + " TEXT NOT NULL,"
                + PlacesContract.PlacesColumns.PHONE + " TEXT NOT NULL,"
                + PlacesContract.PlacesColumns.ADDRESS + " TEXT NOT NULL,"
                + PlacesContract.PlacesColumns.CATEGORY + " TEXT NOT NULL,"
                + PlacesContract.PlacesColumns.LATITUDE + " TEXT NOT NULL,"
                + PlacesContract.PlacesColumns.LONGITUDE + " REAL NOT NULL DEFAULT 1.5,"
                + PlacesContract.PlacesColumns.MAIN_PHOTO + " INTEGER NOT NULL DEFAULT 0"
                + ")" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.PLACES);
        onCreate(db);
    }
}
