package com.alelievangelista.dryft;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by aevangelista on 16-04-04.
 */
public class PlacesAsyncTask extends AsyncTask<Void, Void, ArrayList<Place>> {

    private static final String LOG_TAG = "PlacesAsyncTask";

    //Tour template
    private static final int NUM_ATTRACTIONS = 3;
    private static final int NUM_RESTAURANT = 1;


    //JSON node names
    private static final String TAG_RESPONSE = "response";
    private static final String TAG_GROUPS = "groups";
    private static final String TAG_ITEMS = "items";
    private static final String TAG_VENUE = "venue";

    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_CONTACT = "contact";
    private static final String TAG_PHONE = "formattedPhone";
    private static final String TAG_LOCATION = "location";
    private static final String TAG_ADDRESS = "formattedAddress";
    private static final String TAG_CATEGORIES = "categories";

    private static final String TAG_NUM_RESULTS = "totalResults";


    private JSONArray arr;
    private ArrayList<Place> result = new ArrayList<Place>();
    private Place place;
    private JSONObject obj;
    private Activity activity;

    //Chosen places
    private int numPlacesInCity;
    private int[] selectedAttractions;

    public PlacesAsyncTask(Activity activity) {
        this.activity = activity;
    }


    @Override
    protected void onPostExecute(ArrayList<Place> result) {
        super.onPostExecute(result);
        Log.d("PlacesAsyncTask", "Post Execute");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<Place> doInBackground(Void... params) {

        String URL_BASE = activity.getResources().getString(R.string.foursquare_url_base);
        String URL_SETTING = activity.getResources().getString(R.string.foursquare_explore);
        String URL_CLIENT_ID = activity.getResources().getString(R.string.foursquare_client_id);
        String ID = activity.getResources().getString(R.string.foursquare_id);
        String URL_CLIENT_SECRET = activity.getResources().getString(R.string.foursquare_client_secret);
        String SECRET = activity.getResources().getString(R.string.foursquare_secret);

        String TEST = activity.getResources().getString(R.string.test_vals);
        String NYC_ATTRACTIONS = activity.getResources().getString(R.string.nyc_attractions);


        //Build URL
        String URL = URL_BASE + URL_SETTING + URL_CLIENT_ID + ID + URL_CLIENT_SECRET + SECRET + NYC_ATTRACTIONS;
        Log.d(LOG_TAG, "API call URL: " + URL);

        //Scope out the potential results
        try {
            scopeAPICall(URL);
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Generate tour
        if(numPlacesInCity > 0){
            createAttrTour(URL);
        }

        //Reconstruct API call
        String urlWithOffset = URL;


        return dataAPICall(urlWithOffset);

    }

    /**
     * This will generate an array of integers that represent key attractions to visit in a given city
     */
    private void createAttrTour(String base){

        int[] selected = selectPlace(numPlacesInCity, NUM_ATTRACTIONS);
        for (int i = 0; i < selected.length; i++) {
            //Reconstruct the API call
            String newURL = base + "&offset=" + selected[i];
            dataAPICall(newURL);
        }
    }

    /**
     * The purpose of this API call is to scope out the result set so that we know how to query it to get what we need
     * @param url
     * @return
     */
    private void scopeAPICall(String url){

        JSONParser jParser = new JSONParser();

        try {
            //Get JSON from URL
            obj = jParser.getJSONFromUrl(url);

            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    try {

                        String numResults = obj.getJSONObject(TAG_RESPONSE).getString(TAG_NUM_RESULTS);
                        int finalCount = Integer.parseInt(numResults);
                        numPlacesInCity = finalCount;
                        Log.d(LOG_TAG, "Number of places: " + numPlacesInCity);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }finally{
            return;
        }
    }


    /**
     *
     * @param url
     * @return
     */
    private ArrayList<Place> dataAPICall(String url){

        JSONParser jParser = new JSONParser();

        try {
            //Get JSON from URL
            obj = jParser.getJSONFromUrl(url);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        arr = obj.getJSONObject(TAG_RESPONSE).getJSONArray(TAG_GROUPS)
                                .getJSONObject(0).getJSONArray(TAG_ITEMS);

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject o = arr.getJSONObject(i);
                            JSONObject venueObj = o.getJSONObject(TAG_VENUE);
                            Place place = convertPlace(venueObj);
                            result.add(place);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }finally{
            return result;
        }
    }

    private int[] selectPlace(int numAttrs, int limit){

        int[] array = new int[limit];

            //Pick random attractions
            Random random = new Random();
            for(int i = 0; i < limit; i++) {
                int answer = random.nextInt(numAttrs);
                array[i] = answer;
                Log.d(LOG_TAG, "Random number = " + answer);
            }

        return array;
    }


    private Place convertPlace(JSONObject obj) throws JSONException {

        String id = "";
        String name = "";
        String phone = "";
        String address = "";
        String category = "";



        if(obj.has(TAG_ID)) {
            id = obj.getString(TAG_ID);
        }

        if(obj.has(TAG_NAME)) {
            name = obj.getString(TAG_NAME);
        }

        if(obj.getJSONObject(TAG_CONTACT).has(TAG_PHONE)) {
            phone = obj.getJSONObject(TAG_CONTACT).getString(TAG_PHONE);
        }

        if(obj.getJSONObject(TAG_LOCATION).has(TAG_ADDRESS)) {
            address = obj.getJSONObject(TAG_LOCATION).getString(TAG_ADDRESS);
        }

        if(obj.getJSONArray(TAG_CATEGORIES).getJSONObject(0).has(TAG_NAME)) {
            category = obj.getJSONArray(TAG_CATEGORIES).getJSONObject(0).getString(TAG_NAME);
        }

        Log.d(LOG_TAG, id + " \n" + name + " \n" + phone + " \n" + address + " \n" + category);

        return null;

        //return new MovieElement(id, name, API_IMAGE_URL1 + poster, API_IMAGE_URL2 + backdrop, synopsis, rating, votes, releaseDate);
    }

}

