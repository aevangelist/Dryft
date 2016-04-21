package com.alelievangelista.dryft.api;

import android.app.Activity;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import com.alelievangelista.dryft.R;
import com.alelievangelista.dryft.data.PlacesContract;
import com.alelievangelista.dryft.ui.Place;

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

    public PlacesAsyncResponse delegate = null;

    //Tour template - the number of attractions and restaurants to generate in between each
    private static final int GENERATED_ATTR = 3;
    private static final int GENERATED_RESTR = 1;


    //JSON node names
    private static final String TAG_RESPONSE = "response";
    private static final String TAG_GROUPS = "groups";
    private static final String TAG_ITEMS = "items";
    private static final String TAG_VENUE = "venue";
    private static final String TAG_COUNT = "count";

    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_CONTACT = "contact";
    private static final String TAG_PHONE = "formattedPhone";
    private static final String TAG_LOCATION = "location";
    private static final String TAG_ADDRESS = "formattedAddress";
    private static final String TAG_CATEGORIES = "categories";

    private static final String TAG_PHOTOS = "featuredPhotos";
    private static final String TAG_PREFIX = "prefix";
    private static final String TAG_SUFFIX = "suffix";
    private static final String TAG_WIDTH = "width";
    private static final String TAG_HEIGHT = "height";


    private static final String TAG_LATITUDE = "lat";
    private static final String TAG_LONGITUDE = "lng";

    private static final String TAG_NUM_RESULTS = "totalResults";

    //Pieces of the API
    private String PREFIX_URL; //This is the base for the API call
    private String URL_BASE;
    private String URL_SETTING;
    private String URL_CLIENT_ID;
    private String ID;
    private String URL_CLIENT_SECRET;
    private String SECRET;
    private String VERSION;
    private String LIMIT;
    private String OFFSET;
    private String LAT_LONG;
    private String VENUE_PHOTOS;

    private String BREAKFAST;
    private String LUNCH;
    private String DINNER;

    private String RADIUS;

    //Cities supported
    private String NYC_ATTRACTIONS;
    private String SF_ATTRACTIONS;
    private String TO_ATTRACTIONS;
    private String LONDON_ATTRACTIONS;

    private JSONArray arr;
    private JSONObject obj;
    private Activity activity;

    //This is the container that will hold the Place objects of the tour
    ArrayList<Place> tourList = new ArrayList<Place>();;

    public PlacesAsyncTask(Activity activity) {
        this.activity = activity;
    }


    @Override
    protected void onPostExecute(ArrayList<Place> result) {
        super.onPostExecute(tourList);
        delegate.processFinish(tourList);
        Log.d("PlacesAsyncTask", "Post Execute");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<Place> doInBackground(Void... params) {

        URL_BASE = activity.getResources().getString(R.string.foursquare_url_base);
        URL_SETTING = activity.getResources().getString(R.string.foursquare_explore);
        URL_CLIENT_ID = activity.getResources().getString(R.string.foursquare_client_id);
        ID = activity.getResources().getString(R.string.foursquare_id);
        URL_CLIENT_SECRET = activity.getResources().getString(R.string.foursquare_client_secret);
        SECRET = activity.getResources().getString(R.string.foursquare_secret);
        VERSION = activity.getResources().getString(R.string.foursquare_version);
        LIMIT = activity.getResources().getString(R.string.foursquare_results_limit);
        OFFSET = activity.getResources().getString(R.string.foursquare_offset);
        LAT_LONG = activity.getResources().getString(R.string.foursquare_lat_lng);
        VENUE_PHOTOS = activity.getResources().getString(R.string.foursquare_venue_photos);

        //Restaurant related
        BREAKFAST = activity.getResources().getString(R.string.foursquare_breakfast);
        LUNCH = activity.getResources().getString(R.string.foursquare_lunch);
        DINNER = activity.getResources().getString(R.string.foursquare_dinner);
        RADIUS = activity.getResources().getString(R.string.foursquare_radius_2000);

        //Supported cities
        NYC_ATTRACTIONS = activity.getResources().getString(R.string.nyc_attractions);
        SF_ATTRACTIONS = activity.getResources().getString(R.string.sf_attractions);
        TO_ATTRACTIONS = activity.getResources().getString(R.string.toronto_attractions);
        LONDON_ATTRACTIONS = activity.getResources().getString(R.string.london_attractions);

        int numResults = 0;

        //Build URL
        PREFIX_URL = URL_BASE + URL_SETTING + URL_CLIENT_ID + ID + URL_CLIENT_SECRET + SECRET + VERSION + LIMIT;
        String URL = PREFIX_URL + NYC_ATTRACTIONS; //CHANGE YOUR CITY HERE!!!

        //Scope out the potential results
        try {
            numResults = scopeAPICall(URL);
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Generate tour if we got results against the city (which we should)
        if(numResults > 0){
            createTour(URL, numResults);
        }

        return tourList;

    }

    /**
     * This will generate an array of integers that represent key attractions to visit in a given city
     */
    private void createTour(String base, int results){

        //Randomly select 3 attractions based on the result set
        int[] selected = selectPlace(results, GENERATED_ATTR);

        for (int i = 0; i < selected.length; i++) {

            //Reconstruct the API call
            String newURL = base + VENUE_PHOTOS + OFFSET + selected[i];
            Log.d(LOG_TAG, "URL CALL: " + newURL);
            Place p = dataAPICall(newURL); //This will hold the actual data for the selected place

            addToTour(p);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(p != null){
                switch (i) {
                    case 0:
                        String m1URL = PREFIX_URL + LAT_LONG + p.getLatitude() + "," + p.getLongitude() +
                                BREAKFAST + RADIUS;
                        selectRestaurant(m1URL);
                        break;
                    case 1:
                        String m2URL = PREFIX_URL + LAT_LONG + p.getLatitude() + "," + p.getLongitude() +
                                LUNCH + RADIUS;
                        selectRestaurant(m2URL);
                        break;
                    case 2:
                        String m3URL = PREFIX_URL + LAT_LONG + p.getLatitude() + "," + p.getLongitude() +
                                DINNER + RADIUS;
                        selectRestaurant(m3URL);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * This will generate restaurants based on the choices available in the area
     * @param url
     */
    private void selectRestaurant(String url){

        //Scope out the restaurants in the area
        int numResults = scopeAPICall(url);

        //Generate tour
        if(numResults > 0){

            //Look for restaurant
            int[] selected = selectPlace(numResults, GENERATED_RESTR);
            for (int i = 0; i < selected.length; i++) {
                String newUrl = url + VENUE_PHOTOS + OFFSET + selected[i];
                Log.d(LOG_TAG, "URL CALL: " + newUrl);
                Place p = dataAPICall(newUrl); //Grab the info for selected restaurant

                addToTour(p);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                }
            }
        }

    //This will add the place into the list of tours
    private void addToTour(Place p){
        tourList.add(p);
    }

    /**
     * The purpose of this API call is to scope out the result set so that we know how to query it
     * to generate recommendations for a tour itinerary. This method will return a number of results
     * matching our query against the city
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

    /**
     * This will randomly select 3 numbers within a certain range, which represents the
     * attractions selected by the algorithm (sophisticated, huh) #AI
     * @param numAttrs
     * @param limit
     * @return
     */
    private int[] selectPlace(int numAttrs, int limit){

        int[] array = new int[limit];

            //Pick random attractions
            Random random = new Random();
            for(int i = 0; i < limit; i++) {
                int answer = random.nextInt(numAttrs);
                array[i] = answer;
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
        String photo = "";

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

        if(obj.getJSONObject(TAG_PHOTOS).getInt(TAG_COUNT) > 0) {
            String a = obj.getJSONObject(TAG_PHOTOS).getJSONArray(TAG_ITEMS).getJSONObject(0).getString(TAG_PREFIX);
            String b = obj.getJSONObject(TAG_PHOTOS).getJSONArray(TAG_ITEMS).getJSONObject(0).getString(TAG_SUFFIX);
            String c = obj.getJSONObject(TAG_PHOTOS).getJSONArray(TAG_ITEMS).getJSONObject(0).getString(TAG_WIDTH);
            String d = obj.getJSONObject(TAG_PHOTOS).getJSONArray(TAG_ITEMS).getJSONObject(0).getString(TAG_HEIGHT);

            photo = a + c + "x" + d + b;
        }

        Log.d(LOG_TAG, id + " \n" + name + " \n" + "(" + latitude + ", " + longitude + ")" + " \n" + address + " \n" + category + " \n" + photo);

        Place place = new Place(id, name, phone, address, category, latitude, longitude, photo);

        //Send to content provider
        writeBackTour(id, name, phone, address, category, latitude, longitude, photo);

        return place;

    }

    private void writeBackTour(String id, String name, String phone, String address, String category, String latitude, String longitude, String photo) {
        ContentValues values= new ContentValues();
        values.put(PlacesContract.Places.PLACE_ID, id);
        values.put(PlacesContract.Places.NAME, name);
        values.put(PlacesContract.Places.PHONE, phone);
        values.put(PlacesContract.Places.ADDRESS, address);
        values.put(PlacesContract.Places.CATEGORY, category);
        values.put(PlacesContract.Places.LATITUDE, latitude);
        values.put(PlacesContract.Places.LONGITUDE, longitude);
        values.put(PlacesContract.Places.MAIN_PHOTO, photo);
        activity.getContentResolver().insert(PlacesContract.Places.CONTENT_URI,values);
    }

    public interface PlacesAsyncResponse {
        void processFinish(ArrayList<Place> output);
    }

}

