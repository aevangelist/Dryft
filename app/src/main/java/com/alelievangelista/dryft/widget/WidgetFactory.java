package com.alelievangelista.dryft.widget;


import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.alelievangelista.dryft.R;
import com.alelievangelista.dryft.data.PlacesContract;
import com.alelievangelista.dryft.data.PlacesProvider;

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

        //Set up cursor
        /*Cursor cursor = provider.query(
                PlacesContract.Places.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                mSelectionClause, // cols for "where" clause
                mArgsYes, // values for "where" clause
                null  // sort order
        );*/

        if (cursor != null) {
            if (cursor.moveToNext()) {
                String placeName = cursor.getString(cursor.getColumnIndex(PlacesContract.Places.NAME));
                Log.d(LOG_TAG, "suuppp!!!!" + placeName);
            }
            cursor.close();
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

        WidgetPlaceItem listItem = listItemList.get(position);

        //Set up UI on list item
        //remoteView.setTextViewText(R.id.homeTeamName, listItem.getHomeTeam());

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
