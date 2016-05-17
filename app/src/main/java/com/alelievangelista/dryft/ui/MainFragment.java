package com.alelievangelista.dryft.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alelievangelista.dryft.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class MainFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private CharSequence title;

    private final String LOG_TAG = "MainActivity";
    public static final String PREF_CITY = "PREF_CITY";

    private final int MY_PERMISSIONS_REQUEST_LOCATION_FINE = 200;

    private int permissionLocationFine;

    private DrawerLayout mDrawer;
    private NavigationView mNavView;
    private Toolbar toolbar;
    private ImageView placeBackground;
    private ActionBarDrawerToggle drawerToggle;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private String mLatitude;
    private String mLongitude;

    private PreferenceChangeListener listener;
    private SharedPreferences settingsPref;


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

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        //Set up ads
        AdView mAdView = (AdView) view.findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder().build();
        if (mAdView != null) {
            mAdView.loadAd(adRequest);
        }

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setupNavDrawer(toolbar, view);

        placeBackground = (ImageView) view.findViewById(R.id.imageViewPlaces);
        getCityPreference();


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

    private void getCityPreference(){
        //Determine city
        SharedPreferences sharedPref;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        String cityPreference = sharedPref.getString(PREF_CITY, "1");

        //City
        switch (cityPreference) {
            case "1":
                placeBackground.setImageResource(R.drawable.bg_london);
                break;
            case "2":
                placeBackground.setImageResource(R.drawable.bg_nyc);
                break;
            case "3":
                placeBackground.setImageResource(R.drawable.bg_sf);
                break;
            case "4":
                placeBackground.setImageResource(R.drawable.bg_toronto);
                break;
            default:
                placeBackground.setImageResource(R.drawable.bg_nyc);
                break;
        }
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

    /*@Override
    public void onConnected(Bundle bundle) {

        Log.d(LOG_TAG, "Connected to Google API");

        //Grab current permissions for location
        permissionLocationFine = ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionLocationFine != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showMessageOKCancel(getResources().getString(R.string.location_permission),
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
                .setPositiveButton(getResources().getString(R.string.ok), okListener)
                .setNegativeButton(getResources().getString(R.string.cancel), null)
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
            Log.d(LOG_TAG, "Could not grab location");
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
                    Toast.makeText(getActivity(), getResources().getString(R.string.permission_denied), Toast.LENGTH_SHORT)
                            .show();
                }
                return;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }*/

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.

        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }


    public void setupNavDrawer(Toolbar t, View v){
        // Find our drawer view
        mDrawer = (DrawerLayout) v.findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle(t);

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

        //Set up navigation bar
        mNavView = (NavigationView) v.findViewById(R.id.nav_view);
        setupDrawerContent(mNavView);
    }

    private ActionBarDrawerToggle setupDrawerToggle(Toolbar tbar) {
        return new ActionBarDrawerToggle(getActivity(), mDrawer, tbar, R.string.drawer_open,  R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        try {
                            selectDrawerItem(menuItem);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (java.lang.InstantiationException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) throws IllegalAccessException, java.lang.InstantiationException {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;

        switch(menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                break;
            case R.id.nav_second_fragment:
                // Create new fragment and transaction
                Fragment newFragment = new MyTourFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.add(R.id.container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();

                break;
            case R.id.nav_third_fragment:
                startActivity(new Intent(getActivity(), PrefActivity.class));
                break;
            case R.id.nav_fourth_fragment:
                String title = getActivity().getResources().getString(R.string.about_title);
                String message = getActivity().getResources().getString(R.string.about_text);

                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle(title);

                alertDialog.setMessage(message);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.got_it),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                break;
            default:
                //fragmentClass = AboutFragment.class;
        }
        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        getActivity().setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    @Override
    public void onDestroyView() {
        ViewGroup mContainer = (ViewGroup) getActivity().findViewById(R.id.container);
        mContainer.removeAllViews();
        super.onDestroyView();
    }

    // Handle preferences changes
    private class PreferenceChangeListener implements
            SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs,
                                              String key) {
            getCityPreference();
        }
    }


}
