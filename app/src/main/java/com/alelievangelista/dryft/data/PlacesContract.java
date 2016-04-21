package com.alelievangelista.dryft.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by aevangelista on 16-04-18.
 */
public class PlacesContract {

    public static final String CONTENT_AUTHORITY = "com.alelievangelista.dryft";
    public static final Uri BASE_URI = Uri.parse("content://com.alelievangelista.dryft");
    public static final String PATH_PLACES = "places";

    public static final class Places implements BaseColumns {
        public static final String PLACE_ID = "place_id";
        public static final String NAME = "name";
        public static final String PHONE = "phone";
        public static final String ADDRESS = "address";
        public static final String CATEGORY = "category";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String MAIN_PHOTO = "main_photo";
        public static final String IS_SAVED = "is_saved";
        public static final String IS_DISPLAY = "is_display";


        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_PLACES).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_PLACES;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_PLACES;

        public static Uri buildPlaceUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

    }


}
