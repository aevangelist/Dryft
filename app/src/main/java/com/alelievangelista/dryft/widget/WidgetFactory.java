package com.alelievangelista.dryft.widget;


import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.alelievangelista.dryft.R;
import com.alelievangelista.dryft.data.PlacesContract;
import com.alelievangelista.dryft.data.PlacesProvider;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by aevangelista on 15-12-02.
 */
public class WidgetFactory implements RemoteViewsFactory {

    private final String LOG_TAG = "WidgetListProvider";

    private PlacesProvider provider;

    private ArrayList<WidgetPlaceItem> listItemList = new ArrayList();
    private Context context = null;
    private int appWidgetId;

    //SQL
    private String mSelectionClause =  PlacesContract.Places.IS_DISPLAY + " = ?";
    private String[] mArgsYes = new String[]{"1"};

    public WidgetFactory(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {

        provider = new PlacesProvider();

        Cursor cursor = context.getContentResolver().query(
                PlacesContract.Places.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                mSelectionClause, // cols for "where" clause
                mArgsYes, // values for "where" clause
                null  // sort order
        );


        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    WidgetPlaceItem listItem = new WidgetPlaceItem();

                    String placeName = cursor.getString(cursor.getColumnIndex(PlacesContract.Places.NAME));
                    String placeAddress = cursor.getString(cursor.getColumnIndex(PlacesContract.Places.ADDRESS));
                    String placeCategory = cursor.getString(cursor.getColumnIndex(PlacesContract.Places.CATEGORY));
                    String placePhone = cursor.getString(cursor.getColumnIndex(PlacesContract.Places.PHONE));
                    String placePhoto = cursor.getString(cursor.getColumnIndex(PlacesContract.Places.MAIN_PHOTO));


                    listItem.setPlaceName(placeName);
                    listItem.setPlaceAddress(placeAddress);
                    listItem.setPlaceCategory(placeCategory);
                    listItem.setPlacePhone(placePhone);
                    listItem.setPlacePhoto(placePhoto);


                    listItemList.add(listItem);

                    cursor.moveToNext();
                }
                cursor.close();
            }
        }

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return listItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    /*
    * Similar to getView of Adapter where instead of View
    * we return RemoteViews
    */
    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.widget_list_item);

        WidgetPlaceItem item = listItemList.get(position);

        remoteView.setTextViewText(R.id.widget_place_name, item.getPlaceName());
        remoteView.setTextViewText(R.id.widget_place_address, item.getPlaceCategory());

        try {
            Bitmap b = Picasso.with(context).load(item.getPlacePhoto()).get();
            remoteView.setImageViewBitmap(R.id.widget_image, b);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

}
