package com.alelievangelista.dryft;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback,
        PlacesAsyncTask.PlacesAsyncResponse{

    private GoogleMap googleMap;

    private final String LOG_TAG = "MainActivity";
    private final int MY_PERMISSIONS_REQUEST_LOCATION_FINE = 200;

    private int permissionLocationFine;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private String mLatitude;
    private String mLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //CollapsingToolbarLayout toolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        //Because you were able to grab the user's location
        if(isNetworkAvailable()){
            Log.d(LOG_TAG, "Network is available - now launching PlacesAsyncTask");
            PlacesAsyncTask task = new PlacesAsyncTask(this);
            task.delegate = this; //this to set delegate/listener back to this class

            task.execute();
        }

        //Set up the map
        /*SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.htab_tabs);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.htab_viewpager);

        setupViewPager(viewPager); //Set up with adapter and tab names
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        break;
                    case 1:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new DummyFragment(), "WELCOME");
        adapter.addFrag(new DummyFragment(), "TOUR");
        adapter.addFrag(new DummyFragment(), "MAP");
        viewPager.setAdapter(adapter);

    }


    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {

        Log.d(LOG_TAG, "Connected to Google API");

        //Grab current permissions for location
        permissionLocationFine = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionLocationFine != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showMessageOKCancel("You need to allow access to Contacts",
                            new DialogInterface.OnClickListener() {
                                @TargetApi(Build.VERSION_CODES.M)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                                            MY_PERMISSIONS_REQUEST_LOCATION_FINE);
                                }
                            });
                    return;
                }
            }

            Log.d(LOG_TAG, "Permission not granted");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION_FINE);
            return;
        }

        getCurrentUserLocation();

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    //Check network connectivity
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void getCurrentUserLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Log.d(LOG_TAG, "Permission has been granted");


        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            String mLatitude = Double.toString(mLastLocation.getLatitude());
            String mLongitude = Double.toString(mLastLocation.getLongitude());

            Log.d(LOG_TAG, "User's current coordinates: Long:" + mLatitude + " - " + "Lat:" + mLongitude);

        }

        if(mLastLocation == null){
            Log.d(LOG_TAG, "Couldnt grab location");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION_FINE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getCurrentUserLocation();

                } else {
                    //TODO - Disable the permission in question
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                return;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        //map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    public void createMapVisualization(ArrayList<Place> tourList){

        //Bounds
        double minLng = 0;
        double minLat = 0;
        double maxLng = 0;
        double maxLat = 0;

        // Create a LatLngBounds that includes Australia.
        /*LatLngBounds AUSTRALIA = new LatLngBounds(
                new LatLng(-44, 113), new LatLng(-10, 154));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(AUSTRALIA, 0));*/

        for (int i = 0; i < tourList.size(); i++) {
            Place p = tourList.get(i);
            if (googleMap != null){
                Log.d(LOG_TAG, "Place #" + i + ": " + p.getName());
                double lat = Double.parseDouble(p.getLatitude());
                double lng = Double.parseDouble(p.getLongitude());
                String title = p.getName();
                googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(title));

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
        }

        Log.d(LOG_TAG, "Min Lat : " + minLat);
        Log.d(LOG_TAG, "Min Lng : " + minLng);
        Log.d(LOG_TAG, "Max Lat : " + maxLat);
        Log.d(LOG_TAG, "Max Lng : " + maxLng);


        //Create bounds
        LatLngBounds BOUNDED = new LatLngBounds(
                new LatLng(minLat, minLng), new LatLng(maxLat, maxLng));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(BOUNDED, 40));

    }

    @Override
    public void processFinish(ArrayList<Place> output) {

        //Build the map
        //createMapVisualization(output);

        //Build the welcome message
    }
}
