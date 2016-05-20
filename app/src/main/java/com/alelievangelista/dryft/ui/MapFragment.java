package com.alelievangelista.dryft.ui;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alelievangelista.dryft.R;
import com.alelievangelista.dryft.data.PlacesContract;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback,
        LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = "MapFragment";
    private final int LOADER_ID = 30;

    private String mSelectionClauseDisplay =  PlacesContract.Places.IS_DISPLAY + " = ?";
    private String[] mArgsYes = new String[]{"1"};

    private PreferenceChangeListener listener;
    private SharedPreferences settingsPref;

    private Cursor cursor;

    private GoogleMap googleMap;

    public MapFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        //Setting up preferences listener
        settingsPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        listener = new PreferenceChangeListener();
        settingsPref.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_map, container, false);

        //Set up the map
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        MapFragment.this.restartLoader();

        //Set up cursor
        cursor = getActivity().getContentResolver().query(
                PlacesContract.Places.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                mSelectionClauseDisplay, // cols for "where" clause
                mArgsYes, // values for "where" clause
                null  // sort order
        );

        return view;
    }


    private void restartLoader(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                getPlaces(cursor);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * This will extract the locations of the places generated for the tour
     * @param c
     */
    private void getPlaces(Cursor c){

        //Bounds
        double minLng = 0;
        double minLat = 0;
        double maxLng = 0;
        double maxLat = 0;

        while (c.moveToNext() && googleMap != null) {
            String placeName = c.getString(c.getColumnIndex(PlacesContract.Places.NAME));
            String latitude = c.getString(c.getColumnIndex(PlacesContract.Places.LATITUDE));
            String longitude = c.getString(c.getColumnIndex(PlacesContract.Places.LONGITUDE));

            double lat = Double.parseDouble(latitude);
            double lng = Double.parseDouble(longitude);

            googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(placeName));

            if(lat < minLat || minLat == 0){
                minLat = lat;
            }
            if(lng < minLng || minLng == 0){
                minLng = lng;
            }
            if(lat > maxLat || maxLat == 0){
                maxLat = lat;
            }
            if(lng > maxLng || maxLng == 0){
                maxLng = lng;
            }

        }

        //Create bounds
        LatLngBounds BOUNDED = new LatLngBounds(
                new LatLng(minLat, minLng), new LatLng(maxLat, maxLng));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(BOUNDED, 80));

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(
                getActivity(),
                PlacesContract.Places.CONTENT_URI,
                null,
                mSelectionClauseDisplay, // cols for "where" clause
                mArgsYes, // values for "where" clause
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


    // Handle preferences changes
    private class PreferenceChangeListener implements
            SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs,
                                              String key) {
           //Set up the map again

        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
