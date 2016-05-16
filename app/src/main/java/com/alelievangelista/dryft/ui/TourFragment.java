package com.alelievangelista.dryft.ui;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

    private String mSelectionClauseDisplay =  PlacesContract.Places.IS_DISPLAY + " = ?";
    private String mSelectionClauseSaved =  PlacesContract.Places.IS_SAVED + " = ?";
    private String[] mArgsYes = new String[]{"1"};
    private String[] mArgsNo = new String[]{"0"};


    private Activity activity;
    private ListView mListView;
    private PlaceListAdapter placeListAdapter;

    private PreferenceChangeListener listener;
    private SharedPreferences settingsPref;
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;

    public TourFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setting up preferences listener
        settingsPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        listener = new PreferenceChangeListener();
        settingsPref.registerOnSharedPreferenceChangeListener(listener);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tour, container, false);

        activity = getActivity();

        mListView = (ListView) view.findViewById(R.id.list_view);

        //Floating Action Button behaviour
        FloatingActionButton myFab = (FloatingActionButton) view.findViewById(R.id.fab_reload);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createNewTour();
            }
        });

        //Set up cursor
        Cursor cursor = getActivity().getContentResolver().query(
                PlacesContract.Places.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                mSelectionClauseDisplay, // cols for "where" clause
                mArgsYes, // values for "where" clause
                null  // sort order
        );

        //Set up custom adapter
        placeListAdapter = new PlaceListAdapter(getActivity(), cursor, 0);
        mListView.setAdapter(placeListAdapter);
        mListView.setItemsCanFocus(false);

        TourFragment.this.restartLoader();

        return view;

    }

    //Check network connectivity
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * This is the handler for when the FAB is clicked
     */
    private void createNewTour(){
        Snackbar snackbar = Snackbar
                .make(mListView, getResources().getString(R.string.new_tour)
                        , Snackbar.LENGTH_LONG);

        snackbar.show();

        //First, delete items in content provider that have not been saved
        activity.getContentResolver().delete(PlacesContract.Places.CONTENT_URI, mSelectionClauseDisplay, mArgsYes);
        activity.getContentResolver().delete(PlacesContract.Places.CONTENT_URI, mSelectionClauseSaved, mArgsNo);

        //Next, connect out to via API to generate a new tour
        if(isNetworkAvailable()){
            Log.d(LOG_TAG, "Network is available - now launching PlacesAsyncTask");
            PlacesAsyncTask task = new PlacesAsyncTask(activity);
            task.delegate = this; //this to set delegate/listener back to this class

            task.execute();
        }

    }

    // Handle preferences changes
    private class PreferenceChangeListener implements
            SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs,
                                              String key) {
            Snackbar snackbar = Snackbar
                    .make(mListView, getResources().getString(R.string.change_tour), Snackbar.LENGTH_LONG);
            snackbar.show();

            //First, delete items in content provider that have not been saved
            activity.getContentResolver().delete(PlacesContract.Places.CONTENT_URI, mSelectionClauseDisplay, mArgsYes);
            activity.getContentResolver().delete(PlacesContract.Places.CONTENT_URI, mSelectionClauseSaved, mArgsNo);

            //Next, connect out to via API to generate a new tour
            if(isNetworkAvailable()){
                Log.d(LOG_TAG, "Network is available - now launching PlacesAsyncTask");
                PlacesAsyncTask task = new PlacesAsyncTask(activity);
                task.execute();
            }
        }
    }

    @Override
    public void processFinish(ArrayList<Place> output) {
        Log.e(LOG_TAG, "Asynctask has finished!!!");
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


    @Override
    public void onDestroyView() {
        ViewGroup mContainer = (ViewGroup) getActivity().findViewById(R.id.container);
        mContainer.removeAllViews();
        super.onDestroyView();
    }
}
