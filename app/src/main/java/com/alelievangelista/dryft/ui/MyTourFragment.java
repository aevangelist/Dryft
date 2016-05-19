package com.alelievangelista.dryft.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alelievangelista.dryft.R;
import com.alelievangelista.dryft.data.MyTourListAdapter;
import com.alelievangelista.dryft.data.PlacesContract;

public class MyTourFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = "MyTourFragment";
    private final int LOADER_ID = 20;
    private int position = ListView.INVALID_POSITION;

    private MyTourListAdapter myTourListAdapter;
    private Toolbar toolbar;
    private ListView mListView;
    private SwipeRefreshLayout swipeContainer;

    private String mSelectionClauseSaved =  PlacesContract.Places.IS_SAVED + " = ?";
    private String[] mArgsYes = new String[]{"1"};

    public MyTourFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(LOG_TAG, "onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_tour, container, false);

        toolbar = (Toolbar) view.findViewById(R.id.opaque_toolbar);
        toolbar.setTitle(getResources().getString(R.string.my_tour));

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        setHasOptionsMenu(true);

        mListView = (ListView) view.findViewById(R.id.my_tour_list_view);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);

        //Set up swipe container
        setUpSwipeContainer();

        //Set up cursor
        Cursor cursor = getActivity().getContentResolver().query(
                PlacesContract.Places.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                mSelectionClauseSaved, // cols for "where" clause
                mArgsYes, // values for "where" clause
                null  // sort order
        );

        //Set up custom adapter
        myTourListAdapter = new MyTourListAdapter(getActivity(), cursor, 0);
        mListView.setAdapter(myTourListAdapter);

        MyTourFragment.this.restartLoader();

        return view;

    }

    private void setUpSwipeContainer(){

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLoaderManager().restartLoader(0, null, MyTourFragment.this);
                swipeContainer.setRefreshing(false);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    /**
     * React to the user tapping the back/up icon in the action bar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void restartLoader(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getActivity(),
                PlacesContract.Places.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                mSelectionClauseSaved, // cols for "where" clause
                mArgsYes, // values for "where" clause
                null  // sort order
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        myTourListAdapter.swapCursor(data);
        if (position != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(position);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        myTourListAdapter.swapCursor(null);
    }
}
