package com.alelievangelista.dryft.widget;


import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.alelievangelista.dryft.data.PlacesContract;
import com.alelievangelista.dryft.data.PlacesProvider;

import java.util.ArrayList;

/**
 * Created by aevangelista on 15-12-02.
 */
public class WidgetListProvider implements RemoteViewsFactory {

    private PlacesProvider provider;
    private ArrayList<WidgetListItem> listItemList = new ArrayList();
    private Context context = null;
    private int appWidgetId;

    //SQL
    private String mSelectionClause =  PlacesContract.Places.IS_DISPLAY + " = ?";
    private String[] mArgsYes = new String[]{"1"};

    public WidgetListProvider(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {

        provider = new PlacesProvider();

        //Set up cursor
        Cursor cursor = provider.query(
                PlacesContract.Places.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                mSelectionClause, // cols for "where" clause
                mArgsYes, // values for "where" clause
                null  // sort order
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    /*WidgetListItem listItem = new WidgetListItem();

                    listItem.setHomeTeam(cursor.getString(adapter.COL_HOME));
                    listItem.setAwayTeam(cursor.getString(adapter.COL_AWAY));
                    listItem.setHomeScore(Utilies.getScore(cursor.getInt(adapter.COL_HOME_GOALS)));
                    listItem.setAwayScore(Utilies.getScore(cursor.getInt(adapter.COL_AWAY_GOALS)));

                    listItem.setHomeLogo(Utilies.getTeamCrestByTeamName(
                            cursor.getString(adapter.COL_HOME)));
                    listItem.setAwayLogo(Utilies.getTeamCrestByTeamName(
                            cursor.getString(adapter.COL_AWAY)));

                    listItemList.add(listItem);*/

                    cursor.moveToNext();
                }
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
        /*final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.widget_list_item);

        WidgetListItem listItem = listItemList.get(position);

        //Set up UI on list item
        remoteView.setTextViewText(R.id.homeTeamName, listItem.getHomeTeam());
        remoteView.setTextViewText(R.id.awayTeamName, listItem.getAwayTeam());
        remoteView.setTextViewText(R.id.homeTeamScore, listItem.getHomeScore());
        remoteView.setTextViewText(R.id.awayTeamScore, listItem.getAwayScore());

        remoteView.setImageViewResource(R.id.homeTeamLogo, listItem.getHomeLogo());
        remoteView.setImageViewResource(R.id.awayTeamLogo, listItem.getAwayLogo());

        return remoteView;*/
        return null;
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
