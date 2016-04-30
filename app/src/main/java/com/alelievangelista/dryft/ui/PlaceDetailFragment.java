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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

public class PlaceDetailFragment extends Fragment implements OnMapReadyCallback,
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = "PlaceDetailFragment";
    private static final String PLACE_ID_TAG = "PlaceIdTag";
    private final int LOADER_ID = 10;

    private GoogleMap googleMap;
    private Cursor cursor;

    private Toolbar toolbar;
    private String placeId;
    private String placeName;
    private String latitude;
    private String longitude;

    //Element
    private ImageView mMainImage;
    private TextView mPlaceTitle;
    private TextView mPlaceCategory;
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
    private String mSelectionClauseHours =  PlacesContract.Hours.PLACE_ID + " = ?";

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

        //Set up the map
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.place_map);
        mapFragment.getMapAsync(this);

        PlaceDetailFragment.this.restartLoader();

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
        mPlaceDescription = (TextView) view.findViewById(R.id.place_detail_description);

        mPlaceAddress = (TextView) view.findViewById(R.id.place_detail_address1);

        mPlacePhone = (TextView) view.findViewById(R.id.place_detail_phone);
        mPlaceTwitter = (TextView) view.findViewById(R.id.place_detail_twitter);
        //mPlaceWebsite = (TextView) view.findViewById(R.id.place_detail_website);

        //Set up cursor
        Cursor cursorPlace = getActivity().getContentResolver().query(
                PlacesContract.Places.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                mSelectionClause, // cols for "where" clause
                mArgs, // values for "where" clause
                null  // sort order
        );

        //Set up cursor for details
        Cursor cursorDetail = getActivity().getContentResolver().query(
                PlacesContract.PlaceDetail.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                mSelectionClauseDetail, // cols for "where" clause
                mArgs, // values for "where" clause
                null  // sort order
        );

        //Set up cursor for hours
        Cursor cursorHours = getActivity().getContentResolver().query(
                PlacesContract.Hours.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                mSelectionClauseHours, // cols for "where" clause
                mArgs, // values for "where" clause
                null  // sort order
        );

        //Regular place information
        if( cursorPlace != null && cursorPlace.moveToFirst() ){
            String category = cursorPlace.getString(cursorPlace.getColumnIndex(PlacesContract.Places.CATEGORY));
            String address = cursorPlace.getString(cursorPlace.getColumnIndex(PlacesContract.Places.ADDRESS));
            String phone = cursorPlace.getString(cursorPlace.getColumnIndex(PlacesContract.Places.PHONE));

            placeName = cursorPlace.getString(cursorPlace.getColumnIndex(PlacesContract.Places.NAME));
            latitude = cursorPlace.getString(cursorPlace.getColumnIndex(PlacesContract.Places.LATITUDE));
            longitude = cursorPlace.getString(cursorPlace.getColumnIndex(PlacesContract.Places.LONGITUDE));


            //Format address
            String strArray[] = address.split(",");
            String s1 = strArray[0];
            s1.trim();
            String address1 = s1.substring(2, s1.length() - 1);

            mPlaceTitle.setText(placeName);
            mPlaceCategory.setText(category);

            mPlaceAddress.setText(address1);

            mPlacePhone.setText(phone);

        }


        //Place detailed information
        if( cursorDetail != null && cursorDetail.moveToFirst() ){
            String a = cursorDetail.getString(cursorDetail.getColumnIndex(PlacesContract.PlaceDetail.PLACE_ID));
            String description = cursorDetail.getString(cursorDetail.getColumnIndex(PlacesContract.PlaceDetail.DESCRIPTION));
            String twitter = cursorDetail.getString(cursorDetail.getColumnIndex(PlacesContract.PlaceDetail.TWITTER));

            imageUrl = cursorDetail.getString(cursorDetail.getColumnIndex(PlacesContract.PlaceDetail.BEST_PHOTO));

            mPlaceDescription.setText(description);
            mPlaceTwitter.setText(twitter);

            if(!imageUrl.isEmpty()){
                Picasso.with(getActivity()).load(imageUrl).into(mMainImage);
            }

        }

        //Hours information
        if( cursorHours != null ){

            cursorHours.moveToFirst();
            while(cursorHours.moveToNext()){
                String x = cursorHours.getString(cursorHours.getColumnIndex(PlacesContract.Hours.DAY));
                String y = cursorHours.getString(cursorHours.getColumnIndex(PlacesContract.Hours.TIME));

                Log.d(LOG_TAG, "Hours cursor: " + x + " " + y);
            }
        }

        //Close the cursors
        cursorPlace.close();
        cursorDetail.close();
        cursorHours.close();

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
        cursor = data;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursor = null;
    }

    private void restartLoader(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }


    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        //Determine locations
        getPlaces();
    }

    /**
     * This will extract the locations of the places generated for the tour
     * @param c
     */
    private void getPlaces(){

        if (!latitude.isEmpty() && !longitude.isEmpty()){
            double lat = Double.parseDouble(latitude);
            double lng = Double.parseDouble(longitude);

            googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(placeName));

            // Move the camera instantly to location with a zoom of 15.
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15));

            // Zoom in, animating the camera.
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);

        }

    }
}
