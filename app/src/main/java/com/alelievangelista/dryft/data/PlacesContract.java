package com.alelievangelista.dryft.data;

import android.net.Uri;

/**
 * Created by aevangelista on 16-04-18.
 */
public class PlacesContract {

    public static final String CONTENT_AUTHORITY = "com.alelievangelista.dryft";
    public static final Uri BASE_URI = Uri.parse("content://com.alelievangelista.dryft");

    interface PlacesColumns{
        /** Type: INTEGER PRIMARY KEY AUTOINCREMENT */
        String _ID = "_id";
        /** Type: TEXT */
        String PLACE_ID = "place_id";
        /** Type: TEXT NOT NULL */
        String NAME = "name";
        /** Type: TEXT NOT NULL */
        String PHONE = "phone";
        /** Type: TEXT NOT NULL */
        String ADDRESS = "address";
        /** Type: INTEGER NOT NULL DEFAULT 0 */
        String CATEGORY = "category";
        /** Type: TEXT NOT NULL */
        String LATITUDE = "latitude";
        /** Type: REAL NOT NULL DEFAULT 1.5 */
        String LONGITUDE = "longitude";
        /** Type: TEXT NOT NULL */
        String MAIN_PHOTO = "main_photo";
    }

    public static class Places implements PlacesColumns{
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.alelievangelista.dryft.places";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.alelievangelista.dryft.places";

        //public static final String DEFAULT_SORT = PUBLISHED_DATE + " DESC";

        /** Matches: /places/ */
        public static Uri buildDirUri() {
            return BASE_URI.buildUpon().appendPath("places").build();
        }

        /** Matches: /items/[_id]/ */
        public static Uri buildPlaceUri(long _id) {
            return BASE_URI.buildUpon().appendPath("places").appendPath(Long.toString(_id)).build();
        }

        /** Read item ID item detail URI. */
        public static long getPlaceId(Uri itemUri) {
            return Long.parseLong(itemUri.getPathSegments().get(1));
        }
    }

    private PlacesContract() {
    }

}
