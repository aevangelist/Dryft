package com.alelievangelista.dryft.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alelievangelista.dryft.R;
import com.alelievangelista.dryft.data.PlacesContract;
import com.squareup.picasso.Picasso;

public class PlaceDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = "PlaceDetailFragment";
    private static final String PLACE_ID_TAG = "PlaceIdTag";

    private Toolbar toolbar;
    private String placeId;

    //Element
    private ImageView mMainImage;
    private TextView mPlaceTitle;
    private TextView mPlaceCategory;
    private TextView mPlacePrice;
    private TextView mPlaceDescription;

    private TextView mPlaceAddress;
    private TextView mPlacePhone;
    private TextView mPlaceTwitter;
    private TextView mPlaceWebsite;

    //Values
    private String imageUrl;
    private String placeTitle;
    private String placeCategory;
    private String placePrice;

    private String placeDescription;
    private String placeAddress;
    private String placePhone;
    private String placeTwitter;
    private String placeWebsite;

    //SQL
    private String mSelectionClause =  PlacesContract.Places.PLACE_ID + " = ?";
    private String mSelectionClauseDetail =  PlacesContract.PlaceDetail.PLACE_ID + " = ?";

    private String[] mArgs = new String[1];

    public PlaceDetailFragment() {
        // Required empty public constructor
    }

    public static PlaceDetailFragment newInstance(String param) {
        PlaceDetailFragment fragment = new PlaceDetailFragment();
        Bundle args = new Bundle();
        args.putString(PLACE_ID_TAG, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            placeId = getArguments().getString(PLACE_ID_TAG, "");
            //Set up SQL arguments
            mArgs[0] = placeId;
            Log.d(LOG_TAG, "PlaceDetailsFragment Got: " + placeId);
        }

        setHasOptionsMenu(true);

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
                Log.d(LOG_TAG, "This is what you clicked: " + item.getItemId());
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_place_detail, container, false);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(" ");

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        //Get elements
        mMainImage = (ImageView) view.findViewById(R.id.place_detail_image);

        mPlaceTitle = (TextView) view.findViewById(R.id.place_detail_name);
        mPlaceCategory = (TextView) view.findViewById(R.id.place_detail_category);
        mPlacePrice = (TextView) view.findViewById(R.id.place_detail_price);
        mPlaceDescription = (TextView) view.findViewById(R.id.place_detail_description);
        mPlaceAddress = (TextView) view.findViewById(R.id.place_detail_address);
        mPlacePhone = (TextView) view.findViewById(R.id.place_detail_phone);
        mPlaceTwitter = (TextView) view.findViewById(R.id.place_detail_twitter);
        mPlaceWebsite = (TextView) view.findViewById(R.id.place_detail_website);

        //Set up cursor
        Cursor cursorPlace = getActivity().getContentResolver().query(
                PlacesContract.Places.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                mSelectionClause, // cols for "where" clause
                mArgs, // values for "where" clause
                null  // sort order
        );

        //Set up cursor
        Cursor cursorDetail = getActivity().getContentResolver().query(
                PlacesContract.PlaceDetail.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                mSelectionClauseDetail, // cols for "where" clause
                mArgs, // values for "where" clause
                null  // sort order
        );

        //Regular place information
        if( cursorPlace != null && cursorPlace.moveToFirst() ){
            String b = cursorPlace.getString(cursorPlace.getColumnIndex(PlacesContract.Places.NAME));
            String c = cursorPlace.getString(cursorPlace.getColumnIndex(PlacesContract.Places.CATEGORY));

            Log.d(LOG_TAG, "This is what I got from cursor: " + b + "\n" + c);
        }


        //Place detailed information 
        if( cursorDetail != null && cursorDetail.moveToFirst() ){
            String a = cursorDetail.getString(cursorDetail.getColumnIndex(PlacesContract.PlaceDetail.PLACE_ID));
            String d = cursorDetail.getString(cursorDetail.getColumnIndex(PlacesContract.PlaceDetail.DESCRIPTION));


            Log.d(LOG_TAG, "This is what I got from cursor: " + a + "\n" + d);

            imageUrl = cursorDetail.getString(cursorDetail.getColumnIndex(PlacesContract.PlaceDetail.BEST_PHOTO));
            Log.d(LOG_TAG, "Best photo: " + imageUrl);

            if(!imageUrl.isEmpty()){
                Picasso.with(getActivity()).load(imageUrl).into(mMainImage);
            }

            cursorPlace.close();
            cursorDetail.close();
        }


        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(
                getActivity(),
                PlacesContract.PlaceDetail.CONTENT_URI,
                null,
                mSelectionClause, // cols for "where" clause
                mArgs, // values for "where" clause
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
