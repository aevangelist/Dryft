package com.alelievangelista.dryft.ui;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alelievangelista.dryft.R;
import com.alelievangelista.dryft.api.PlaceListAdapter;
import com.alelievangelista.dryft.api.PlacesAsyncTask;
import com.alelievangelista.dryft.data.PlacesContract;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class TourFragment extends Fragment implements
        PlacesAsyncTask.PlacesAsyncResponse,
        LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = "TourFragment";
    private final int LOADER_ID = 10;
    private int position = ListView.INVALID_POSITION;


    private Activity activity;
    private ListView mListView;
    private PlaceListAdapter placeListAdapter;


    public TourFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tour_fragment, container, false);

        activity = getActivity();

        mListView = (ListView) view.findViewById(R.id.list_view);

        //Set up cursor
        Cursor cursor = getActivity().getContentResolver().query(
                PlacesContract.Places.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        //Set up custom adapter
        placeListAdapter = new PlaceListAdapter(getActivity(), cursor, 0);
        mListView.setAdapter(placeListAdapter);


        //Because you were able to grab the user's location
        if(isNetworkAvailable()){
            Log.d(LOG_TAG, "Network is available - now launching PlacesAsyncTask");
            PlacesAsyncTask task = new PlacesAsyncTask(activity);
            task.delegate = this; //this to set delegate/listener back to this class

            task.execute();
        }

        return view;

    }

    //Check network connectivity
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void processFinish(ArrayList<Place> output) {

    }

    private void restartLoader(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(
                getActivity(),
                PlacesContract.Places.CONTENT_URI,
                null,
                null, // cols for "where" clause
                null, // values for "where" clause
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        placeListAdapter.swapCursor(data);
        if (position != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(position);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        placeListAdapter.swapCursor(null);
    }


}
