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
    private static final int DATABASE_VERSION = 11;

    public PlacesDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_PLACE_TABLE = "CREATE TABLE " + Tables.PLACES + " ("
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
                + ")";

        final String SQL_CREATE_PLACE_DETAIL_TABLE = "CREATE TABLE " + Tables.PLACE_DETAIL + " ("
                + PlacesContract.PlaceDetail._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PlacesContract.PlaceDetail.PLACE_ID + " TEXT NOT NULL,"
                + PlacesContract.PlaceDetail.DESCRIPTION + " TEXT,"
                + PlacesContract.PlaceDetail.TWITTER + " TEXT,"
                + PlacesContract.PlaceDetail.WEBSITE + " TEXT,"
                + PlacesContract.PlaceDetail.BEST_PHOTO + " TEXT,"
                + PlacesContract.PlaceDetail.HAS_MENU + " TEXT,"
                + PlacesContract.PlaceDetail.MENU_URL + " TEXT,"
                + PlacesContract.PlaceDetail.PRICE + " TEXT"
                + ")";

        final String SQL_CREATE_TIPS_TABLE = "CREATE TABLE " + Tables.TIPS + " ("
                + PlacesContract.Tips._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PlacesContract.Tips.PLACE_ID + " TEXT NOT NULL,"
                + PlacesContract.Tips.TIP + " TEXT"
                + ")";

        final String SQL_CREATE_HOURS_TABLE = "CREATE TABLE " + Tables.HOURS + " ("
                + PlacesContract.Hours._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PlacesContract.Hours.PLACE_ID + " TEXT NOT NULL,"
                + PlacesContract.Hours.DAY + " TEXT,"
                + PlacesContract.Hours.TIME + " TEXT,"
                + PlacesContract.Hours.POSITION + " INTEGER"
                + ")";


        //Create database
        db.execSQL(SQL_CREATE_PLACE_TABLE);
        db.execSQL(SQL_CREATE_PLACE_DETAIL_TABLE);
        db.execSQL(SQL_CREATE_HOURS_TABLE);
        db.execSQL(SQL_CREATE_TIPS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.PLACES);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.PLACE_DETAIL);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.TIPS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.HOURS);
        onCreate(db);
    }
}
