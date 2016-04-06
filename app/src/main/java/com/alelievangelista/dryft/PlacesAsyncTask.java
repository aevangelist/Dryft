package com.alelievangelista.dryft;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by aevangelista on 16-04-04.
 */
public class PlacesAsyncTask extends AsyncTask<Void, Void, ArrayList<Place>> {

    String API_URL = "";

    //JSON node names
    private static final String TAG_RESULTS = "results";
    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "original_title";
    private static final String TAG_OVERVIEW = "overview";
    private static final String TAG_DATE = "release_date";
    private static final String TAG_RATING = "vote_average";
    private static final String TAG_VOTES = "vote_count";
    private static final String TAG_POSTER = "poster_path";
    private static final String TAG_BACKDROP = "backdrop_path";

    private JSONArray arr;
    private ArrayList<Place> result = new ArrayList<Place>();
    private Place place;
    private JSONObject obj;
    private Activity activity;


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

        String url;

        //url = API_URL + API_PARAM1 + API_KEY;
        //return createAPICallMultiple(url);

        return null;

    }

    /*private Place createAPICallSingle(String url){

        JSONParser jParser = new JSONParser();

        try {
            //Get JSON from URL
            obj = jParser.getJSONFromUrl(url);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        movie = convertMovie(obj); //Overwrite private variable
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }finally{
            return movie;
        }
    }


    private ArrayList<Place> createAPICallMultiple(String url){

        JSONParser jParser = new JSONParser();

        try {
            //Get JSON from URL
            obj = jParser.getJSONFromUrl(url);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        arr = obj.getJSONArray(TAG_RESULTS);
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject o = arr.getJSONObject(i);
                            MovieElement m = convertMovie(o);
                            result.add(m);
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


    private MovieElement convertMovie(JSONObject obj) throws JSONException {

        String id = obj.getString(TAG_ID);
        String name = obj.getString(TAG_TITLE);
        String backdrop = obj.getString(TAG_BACKDROP);
        String poster = obj.getString(TAG_POSTER);
        String synopsis = obj.getString(TAG_OVERVIEW);
        String rating = obj.getString(TAG_RATING);
        String releaseDate = obj.getString(TAG_DATE);
        String votes = obj.getString(TAG_VOTES);

        return new MovieElement(id, name, API_IMAGE_URL1 + poster, API_IMAGE_URL2 + backdrop, synopsis, rating, votes, releaseDate);
    }*/

}

