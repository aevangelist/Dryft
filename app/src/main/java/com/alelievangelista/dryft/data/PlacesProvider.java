package com.alelievangelista.dryft.data;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aevangelista on 16-04-18.
 */
public class PlacesProvider extends ContentProvider {
    private SQLiteOpenHelper mOpenHelper;

    interface Tables {
        String PLACES = "places";
        String PLACE_DETAIL = "place_detail";
        String TIPS = "tips";
        String HOURS = "hours";
    }

    private static final int PLACES = 0;
    private static final int PLACES__ID = 1;

    private static final int PLACE_DETAIL = 200;
    private static final int PLACE_DETAIL___ID = 201;

    private static final int TIPS = 400;
    private static final int TIPS___ID = 401;

    private static final int HOURS = 500;
    private static final int HOURS___ID = 501;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PlacesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, PlacesContract.PATH_PLACES +"/#", PLACES__ID);
        matcher.addURI(authority, PlacesContract.PATH_PLACES_DETAIL +"/#", PLACE_DETAIL___ID);
        matcher.addURI(authority, PlacesContract.PATH_TIPS +"/#", TIPS___ID);
        matcher.addURI(authority, PlacesContract.PATH_HOURS +"/#", HOURS___ID);

        matcher.addURI(authority, PlacesContract.PATH_PLACES, PLACES);
        matcher.addURI(authority, PlacesContract.PATH_PLACES_DETAIL, PLACE_DETAIL);
        matcher.addURI(authority, PlacesContract.PATH_TIPS, TIPS);
        matcher.addURI(authority, PlacesContract.PATH_HOURS, HOURS);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new PlacesDatabase(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final SelectionBuilder builder = buildSelection(uri);
        Cursor cursor = builder.where(selection, selectionArgs).query(db, projection, sortOrder);
        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PLACES:
                return PlacesContract.Places.CONTENT_TYPE;
            case PLACE_DETAIL:
                return PlacesContract.PlaceDetail.CONTENT_TYPE;
            case TIPS:
                return PlacesContract.Tips.CONTENT_TYPE;
            case HOURS:
                return PlacesContract.Hours.CONTENT_TYPE;
            case PLACES__ID:
                return PlacesContract.Places.CONTENT_ITEM_TYPE;
            case PLACE_DETAIL___ID:
                return PlacesContract.PlaceDetail.CONTENT_ITEM_TYPE;
            case TIPS___ID:
                return PlacesContract.Tips.CONTENT_ITEM_TYPE;
            case HOURS___ID:
                return PlacesContract.Hours.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case PLACES: {
                final long _id = db.insert(Tables.PLACES, null, values);
                if ( _id > 0 ){
                    returnUri = PlacesContract.Places.buildPlaceUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            }
            case PLACE_DETAIL: {
                final long _id = db.insert(Tables.PLACE_DETAIL, null, values);
                if ( _id > 0 ){
                    returnUri = PlacesContract.PlaceDetail.buildPlaceDetailUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            }
            case TIPS: {
                final long _id = db.insert(Tables.TIPS, null, values);
                if ( _id > 0 ){
                    returnUri = PlacesContract.Tips.buildTipsUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            }
            case HOURS: {
                final long _id = db.insert(Tables.HOURS, null, values);
                if ( _id > 0 ){
                    returnUri = PlacesContract.Hours.buildHoursUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSelection(uri);
        getContext().getContentResolver().notifyChange(uri, null);
        return builder.where(selection, selectionArgs).delete(db);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSelection(uri);
        getContext().getContentResolver().notifyChange(uri, null);
        return builder.where(selection, selectionArgs).update(db, values);
    }

    private SelectionBuilder buildSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);
        return buildSelection(uri, match, builder);
    }

    private SelectionBuilder buildSelection(Uri uri, int match, SelectionBuilder builder) {
        final List<String> paths = uri.getPathSegments();
        switch (match) {
            case PLACES: {
                return builder.table(Tables.PLACES);
            }
            case PLACE_DETAIL: {
                return builder.table(Tables.PLACE_DETAIL);
            }
            case TIPS: {
                return builder.table(Tables.TIPS);
            }
            case HOURS: {
                return builder.table(Tables.HOURS);
            }
            case PLACES__ID: {
                final String _id = paths.get(1);
                return builder.table(Tables.PLACES).where(PlacesContract.Places._ID + "=?", _id);
            }
            case PLACE_DETAIL___ID: {
                final String _id = paths.get(1);
                return builder.table(Tables.PLACE_DETAIL).where(PlacesContract.PlaceDetail._ID + "=?", _id);
            }
            case TIPS___ID: {
                final String _id = paths.get(1);
                return builder.table(Tables.TIPS).where(PlacesContract.Tips._ID + "=?", _id);
            }
            case HOURS___ID: {
                final String _id = paths.get(1);
                return builder.table(Tables.HOURS).where(PlacesContract.Hours._ID + "=?", _id);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    /**
     * Apply the given set of {@link ContentProviderOperation}, executing inside
     * a {@link SQLiteDatabase} transaction. All changes will be rolled back if
     * any single one fails.
     */
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

}
