package com.alelievangelista.dryft.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alelievangelista.dryft.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class MainFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private CharSequence title;

    private final String LOG_TAG = "MainActivity";
    private final int MY_PERMISSIONS_REQUEST_LOCATION_FINE = 200;

    private int permissionLocationFine;

    private DrawerLayout mDrawer;
    private NavigationView mNavView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private String mLatitude;
    private String mLongitude;


    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        MainActivity activity = (MainActivity) getActivity();
        //Send custom toolbar to set up as nav drawer
        activity.setupNavDrawer(toolbar);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.htab_tabs);
        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.htab_viewpager);

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

        return  view;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFrag(new TourFragment(), "TOUR");
        adapter.addFrag(new MapFragment(), "MAP");
        viewPager.setAdapter(adapter);
    }


    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {

        Log.d(LOG_TAG, "Connected to Google API");

        //Grab current permissions for location
        permissionLocationFine = ActivityCompat.checkSelfPermission(getActivity(),
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
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION_FINE);
            return;
        }

        getCurrentUserLocation();

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    private void getCurrentUserLocation() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                    Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT)
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

}
