package com.alelievangelista.dryft.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;

import com.alelievangelista.dryft.R;

public class MainActivity extends AppCompatActivity {

    private CharSequence title;

    private final String LOG_TAG = "MainActivity";

    public static AppCompatActivity activity;

    public boolean isTwoPane;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = MainActivity.this;

        FragmentManager fragmentManager = getSupportFragmentManager();

        /*Configuration config = getResources().getConfiguration();
        int i = config.smallestScreenWidthDp;
        Log.d(LOG_TAG, "SMALLEST WIDTHL " + i);

        int a = this.getResources().getConfiguration().orientation;
        if (a == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d(LOG_TAG, "LANDSCAPE");
        }

        if (a == Configuration.ORIENTATION_PORTRAIT) {
            Log.d(LOG_TAG, "PORTRAIT");
        }*/

        setContentView(R.layout.activity_main);

        fragmentManager.beginTransaction().add(R.id.container, new MainFragment()).commit();

        if (findViewById(R.id.details_container) != null) {
            Log.d(LOG_TAG, "DETERMINED TO BE TWO-PANE");
            isTwoPane = true;
            fragmentManager.beginTransaction().add(R.id.details_container, new MapFragment()).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        }
        else {
            getFragmentManager().popBackStack();
        }
    }

}
