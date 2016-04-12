package com.alelievangelista.dryft;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

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
    private static final String TAG_LATITUDE = "lat";
    private static final String TAG_LONGITUDE = "lng";


    private static final String TAG_NUM_RESULTS = "totalResults";


    private JSONArray arr;
    private ArrayList<Place> result = new ArrayList<Place>();
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

        int numResults = 0;

        //Build URL
        String URL = URL_BASE + URL_SETTING + URL_CLIENT_ID + ID + URL_CLIENT_SECRET + SECRET + NYC_ATTRACTIONS;
        Log.d(LOG_TAG, "API call URL: " + URL);

        //Scope out the potential results
        try {
            numResults = scopeAPICall(URL);
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Generate tour
        if(numResults > 0){
            createAttrTour(URL, numResults);
        }

        return null;

    }

    /**
     * This will generate an array of integers that represent key attractions to visit in a given city
     */
    private void createAttrTour(String base, int results){

        int[] selected = selectPlace(results, NUM_ATTRACTIONS);
        Log.d(LOG_TAG, "Number of selected places: " + selected.length);
        for (int i = 0; i < selected.length; i++) {
            //Reconstruct the API call
            String newURL = base + "&offset=" + selected[i];
            Place p = dataAPICall(newURL);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //TODO Make getting the base URL better
            String restaurantBase = activity.getResources().getString(R.string.restaurant_url_base);

            if(p != null){
                switch (i) {
                    case 0:
                        String m1URL = restaurantBase + "&ll=" + p.getLatitude() + "," + p.getLongitude() +
                                "&query=breakfast" + "&radius=2000";
                        Log.d(LOG_TAG, "Breakfast: " + m1URL);
                        suggestRestaurant(m1URL);
                        break;
                    case 1:
                        String m2URL = restaurantBase + "&ll=" + p.getLatitude() + "," + p.getLongitude() +
                                "&query=lunch" + "&radius=2000";
                        Log.d(LOG_TAG, "Lunch: " + m2URL);
                        suggestRestaurant(m2URL);
                        break;
                    case 2:
                        String m3URL = restaurantBase + "&ll=" + p.getLatitude() + "," + p.getLongitude() +
                                "&query=dinner" + "&radius=2000";
                        Log.d(LOG_TAG, "Dinner: " + m3URL);
                        suggestRestaurant(m3URL);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     *
     * @param url
     */
    private void suggestRestaurant(String url){
        int numResults = scopeAPICall(url);

        //Generate tour
        if(numResults > 0){

            //Look for restaurant
            int[] selected = selectPlace(numResults, NUM_RESTAURANT);
            for (int i = 0; i < selected.length; i++) {

                Place p = dataAPICall(url);

                try {
                    Thread.sleep(1000);
                    //Log.d(LOG_TAG, "Restaurant: " + p.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                }
            }
        }

    /**
     * The purpose of this API call is to scope out the result set so that we know how to query it to get what we need
     * @param url
     * @return
     */
    private int scopeAPICall(String url){

        JSONParser jParser = new JSONParser();
        int numPlaces = 0;

        try {
            //Get JSON from URL
            obj = jParser.getJSONFromUrl(url);

            Callable<Integer> callable = new Callable<Integer>() {

                @Override
                public Integer call() {

                    int finalCount = 0;

                    try {
                        String numResults = obj.getJSONObject(TAG_RESPONSE).getString(TAG_NUM_RESULTS);
                        finalCount = Integer.parseInt(numResults);
                        numPlacesInCity = finalCount;
                        Log.d(LOG_TAG, "Number of places: " + numPlacesInCity);

                        } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    return finalCount;
                }
            };

            FutureTask<Integer> task = new FutureTask<>(callable);

            activity.runOnUiThread(task);
            numPlaces = task.get();

        }finally{
            return numPlaces;
        }
    }


    /**
     *
     * @param url
     * @return
     */
    private Place dataAPICall(String url){

        JSONParser jParser = new JSONParser();
        Place selectedPlace = new Place();

        try {
            //Get JSON from URL
            obj = jParser.getJSONFromUrl(url);

            Callable<Place> callable = new Callable<Place>() {

                @Override
                public Place call() {

                    Place place = new Place();

                    try {
                        arr = obj.getJSONObject(TAG_RESPONSE).getJSONArray(TAG_GROUPS)
                                .getJSONObject(0).getJSONArray(TAG_ITEMS);

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject o = arr.getJSONObject(i);
                            JSONObject venueObj = o.getJSONObject(TAG_VENUE);
                            place = convertPlace(venueObj);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    return place;
                }
            };

            FutureTask<Place> task = new FutureTask<>(callable);

            activity.runOnUiThread(task);
            selectedPlace = task.get();


        }finally{
            return selectedPlace;
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
        String latitude = "";
        String longitude = "";
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

        if(obj.getJSONObject(TAG_LOCATION).has(TAG_LATITUDE) && obj.getJSONObject(TAG_LOCATION).has(TAG_LONGITUDE)){
            latitude = obj.getJSONObject(TAG_LOCATION).getString(TAG_LATITUDE);
            longitude = obj.getJSONObject(TAG_LOCATION).getString(TAG_LONGITUDE);

        }


        Log.d(LOG_TAG, id + " \n" + name + " \n" + "(" + latitude + ", " + longitude + ")"  + " \n" + address + " \n" + category);

        return new Place(id, name, phone, address, category, latitude, longitude);

        //return new MovieElement(id, name, API_IMAGE_URL1 + poster, API_IMAGE_URL2 + backdrop, synopsis, rating, votes, releaseDate);
    }

}

