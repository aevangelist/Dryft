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
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
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

import java.util.ArrayList;

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

    private TextView mPlaceWebsite;

    //Containers
    private LinearLayout mContactSection;

    //ListViews
    private ListView mHoursListView;
    private ArrayList<Hour> hourArrayList = new ArrayList<Hour>();

    private ListView mTipsListView;
    private ArrayList<String> tipArrayList = new ArrayList<String>();


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
    private String mSelectionClauseTips =  PlacesContract.Tips.PLACE_ID + " = ?";


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

        //mPlaceWebsite = (TextView) view.findViewById(R.id.place_detail_website);

        //Get containers
        mContactSection = (LinearLayout) view.findViewById(R.id.contact_section);



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

        //Set up cursor for tips
        Cursor cursorTips = getActivity().getContentResolver().query(
                PlacesContract.Tips.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                mSelectionClauseTips, // cols for "where" clause
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

        }


        //Place detailed information
        if( cursorDetail != null && cursorDetail.moveToFirst() ){
            String a = cursorDetail.getString(cursorDetail.getColumnIndex(PlacesContract.PlaceDetail.PLACE_ID));
            String description = cursorDetail.getString(cursorDetail.getColumnIndex(PlacesContract.PlaceDetail.DESCRIPTION));

            //Setting up address
            String a1 = cursorDetail.getString(cursorDetail.getColumnIndex(PlacesContract.PlaceDetail.ADDRESS));
            String a2 = cursorDetail.getString(cursorDetail.getColumnIndex(PlacesContract.PlaceDetail.CROSS_STREET));
            String a3 = cursorDetail.getString(cursorDetail.getColumnIndex(PlacesContract.PlaceDetail.CITY));
            String a4 = cursorDetail.getString(cursorDetail.getColumnIndex(PlacesContract.PlaceDetail.STATE));
            String a5 = cursorDetail.getString(cursorDetail.getColumnIndex(PlacesContract.PlaceDetail.POSTAL_CODE));

            String address = "";
            String[] addressParts = {a1, a2, a3, a4, a5};
            for (String s : addressParts){
                if(!s.isEmpty()){
                    address += s + "\n";
                }
            }

            //String address = a1 + "\n" + a2 + "\n" + a3 + "\n" + a4 + "\n" + a5;

            if(!address.isEmpty()){

                View addressView = inflater.inflate(R.layout.contact_item, container, false);

                TextView addressLabel = (TextView)
                        addressView.findViewById(R.id.contact_label);
                TextView addressValue = (TextView)
                        addressView.findViewById(R.id.contact_value);

                addressLabel.setText("Address");
                addressValue.setText(address);
                mContactSection.addView(addressView);
            }

            //Setting up phone number
            String phone = cursorDetail.getString(cursorDetail.getColumnIndex(PlacesContract.PlaceDetail.PHONE));

            if(!phone.isEmpty()){

                View phoneView = inflater.inflate(R.layout.contact_item, container, false);

                TextView phoneLabel = (TextView)
                        phoneView.findViewById(R.id.contact_label);
                TextView phoneValue = (TextView)
                        phoneView.findViewById(R.id.contact_value);

                phoneLabel.setText("Phone");
                phoneValue.setText(phone);
                mContactSection.addView(phoneView);
            }

            //Setting up Twitter
            String twitter = cursorDetail.getString(cursorDetail.getColumnIndex(PlacesContract.PlaceDetail.TWITTER));

            if(!twitter.isEmpty()){
                View twitterView = inflater.inflate(R.layout.contact_item, container, false);

                TextView twitterLabel = (TextView)
                        twitterView.findViewById(R.id.contact_label);
                TextView twitterValue = (TextView)
                        twitterView.findViewById(R.id.contact_value);

                twitterLabel.setText("Twitter");
                twitterValue.setText(twitter);
                mContactSection.addView(twitterView);
            }

            //Setting up website
            String website = cursorDetail.getString(cursorDetail.getColumnIndex(PlacesContract.PlaceDetail.WEBSITE));

            if(!website.isEmpty()){
                View websiteView = inflater.inflate(R.layout.website_item, container, false);

                TextView websiteLink = (TextView)
                        websiteView.findViewById(R.id.place_detail_website_link);
                websiteLink.setText(Html.fromHtml("<a href=\"" + website + "\"> VISIT WEBSITE</a>"));
                websiteLink.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());

                mContactSection.addView(websiteView);
            }

            imageUrl = cursorDetail.getString(cursorDetail.getColumnIndex(PlacesContract.PlaceDetail.BEST_PHOTO));

            mPlaceDescription.setText(description);

            if(!imageUrl.isEmpty()){
                Picasso.with(getActivity()).load(imageUrl).into(mMainImage);
            }

        }

        //Hours information
        if( cursorHours != null ){

            //Get list views
            mHoursListView = (ListView) view.findViewById(R.id.hours_listview);

            cursorHours.moveToFirst();
            while(cursorHours.moveToNext()){
                String day = cursorHours.getString(cursorHours.getColumnIndex(PlacesContract.Hours.DAY));
                String time = cursorHours.getString(cursorHours.getColumnIndex(PlacesContract.Hours.TIME));
                Log.d(LOG_TAG, "Cursor Hours: " + day + " Time: " + time);
                hourArrayList.add(new Hour(placeId, day, time));
            }

            HoursListAdapter hoursListAdapter = new HoursListAdapter(this.getActivity(), hourArrayList);
            mHoursListView.setAdapter(hoursListAdapter);
        }

        //Tips information
        if( cursorTips != null ){

            //Get list views
            mTipsListView = (ListView) view.findViewById(R.id.tips_listview);

            int i = 0;

            cursorTips.moveToFirst();
            while(cursorTips.moveToNext()){
                String tip = cursorTips.getString(cursorTips.getColumnIndex(PlacesContract.Tips.TIP));
                tipArrayList.add(tip);
                Log.d(LOG_TAG, "Cursor Tip: " + tipArrayList.get(i));
                i++;
            }

            TipsListAdapter tipsAdapter = new TipsListAdapter(this.getActivity(), tipArrayList);
            mTipsListView.setAdapter(tipsAdapter);
            setListViewHeightBasedOnItems(mTipsListView);
        }

        //Close the cursors
        cursorPlace.close();
        cursorDetail.close();
        cursorHours.close();
        cursorTips.close();

        return view;
    }

    /**
     * Sets ListView height dynamically based on the height of the items.
     *
     * @param listView to be resized
     * @return true if the listView is successfully resized, false otherwise
     */
    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();
            //Log.d(LOG_TAG, "Number of items: " + numberOfItems);

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                //item.measure(0, 0);
                item.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalItemsHeight += item.getMeasuredHeight() + 10;
                //Log.d(LOG_TAG, "Item height: " + totalItemsHeight);
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            //params.height = 500;
            //  Log.d(LOG_TAG, "Params: " + params.height);

            //int total = totalItemsHeight + totalDividersHeight;
            //listView.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, total));
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

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
