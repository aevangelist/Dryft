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
    private static final int DATABASE_VERSION = 4;

    public PlacesDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.PLACES + " ("
                + PlacesContract.Places._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PlacesContract.Places.PLACE_ID + " TEXT NOT NULL,"
                + PlacesContract.Places.NAME + " TEXT NOT NULL,"
                + PlacesContract.Places.PHONE + " TEXT,"
                + PlacesContract.Places.ADDRESS + " TEXT,"
                + PlacesContract.Places.CATEGORY + " TEXT,"
                + PlacesContract.Places.LATITUDE + " TEXT,"
                + PlacesContract.Places.LONGITUDE + " TEXT,"
                + PlacesContract.Places.MAIN_PHOTO + " TEXT,"
                + PlacesContract.Places.IS_SAVED + " TEXT,"
                + PlacesContract.Places.IS_DISPLAY + " TEXT"
                + ")" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.PLACES);
        onCreate(db);
    }
}
