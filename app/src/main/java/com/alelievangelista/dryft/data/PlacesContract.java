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
    public static final String PATH_PLACES_DETAIL = "places_detail";
    public static final String PATH_CATEGORIES = "categories";
    public static final String PATH_TIPS = "tips";
    public static final String PATH_HOURS = "houts";


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

    public static final class PlaceDetail implements BaseColumns {
        public static final String PLACE_ID = "place_id";
        public static final String DESCRIPTION = "description";
        public static final String TWITTER = "twitter";
        public static final String WEBSITE = "website";
        public static final String BEST_PHOTO = "best_photo";
        public static final String HAS_MENU = "has_menu";
        public static final String MENU_URL = "menu_url";
        public static final String PRICE = "price";


        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_PLACES_DETAIL).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_PLACES_DETAIL;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_PLACES_DETAIL;

        public static Uri buildPlaceDetailUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }
    }

    public static final class Tips implements BaseColumns {
        public static final String PLACE_ID = "place_id";
        public static final String TIP = "tip";

        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_PLACES).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_TIPS;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_TIPS;

        public static Uri buildTipsUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }
    }

    public static final class Hours implements BaseColumns {
        public static final String PLACE_ID = "place_id";
        public static final String DAY = "day";
        public static final String TIME = "time";
        public static final String ORDER = "order";

        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_PLACES).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_HOURS;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_HOURS;

        public static Uri buildHoursUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }
    }


}
