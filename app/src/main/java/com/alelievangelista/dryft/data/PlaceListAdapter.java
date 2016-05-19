package com.alelievangelista.dryft.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alelievangelista.dryft.R;
import com.alelievangelista.dryft.ui.MainActivity;
import com.alelievangelista.dryft.ui.PlaceDetailFragment;
import com.squareup.picasso.Picasso;

/**
 * Created by aevangelista on 16-04-19.
 */
public class PlaceListAdapter extends CursorAdapter {

    private static final String LOG_TAG = "PlaceListAdapter";
    private static final String PLACE_ID_TAG = "PlaceIdTag";

    private Context context;
    private Cursor cursor;
    private MainActivity fragmentActivity;

    private String mSelectionClause =  PlacesContract.Places.PLACE_ID + " = ?";
    private String[] mArgs = new String[1];


    public static class ViewHolder {
        public final TextView name;
        //public final TextView address;
        public final TextView category;
        public final ImageView mainPhoto;
        public final ImageView addButton;

        public ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.placeName);
            category = (TextView) view.findViewById(R.id.placeCategory);
            mainPhoto = (ImageView) view.findViewById(R.id.placePic);
            addButton = (ImageView) view.findViewById(R.id.addToTour);

        }
    }

    public PlaceListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
        this.cursor = c;
        this.fragmentActivity = (MainActivity) context;
    }

    public Cursor getCursor() {
        return cursor;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_place, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        final String placeId = cursor.getString(cursor.getColumnIndex(PlacesContract.Places.PLACE_ID));
        final String placeName = cursor.getString(cursor.getColumnIndex(PlacesContract.Places.NAME));

        Log.d(LOG_TAG, "New view: " + placeName + ", " + placeId);


        return view;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        final String placeId = cursor.getString(cursor.getColumnIndex(PlacesContract.Places.PLACE_ID));
        final String placeName = cursor.getString(cursor.getColumnIndex(PlacesContract.Places.NAME));
        viewHolder.name.setText(placeName);

        String placeAddress = cursor.getString(cursor.getColumnIndex(PlacesContract.Places.ADDRESS));
        //Format address
        String strArray[] = placeAddress.split(",");
        String s1 = strArray[0];
        s1.trim();
        String s = s1.substring(2, s1.length() - 1);

        //viewHolder.address.setText(s);

        String placeCategory = cursor.getString(cursor.getColumnIndex(PlacesContract.Places.CATEGORY));
        viewHolder.category.setText(placeCategory);

        String imgUrl = cursor.getString(cursor.getColumnIndex(PlacesContract.Places.MAIN_PHOTO));
        if(!imgUrl.isEmpty()){
            Picasso.with(context).load(imgUrl).into(viewHolder.mainPhoto);
        }

        //Set up button
        viewHolder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fragmentActivity != null && cursor != null){
                    String addToTourFormat = fragmentActivity.getResources().getString(R.string.add_to_tour);
                    String addToTourMessage = String.format(addToTourFormat, placeName);

                    Snackbar snackbar = Snackbar
                            .make(v, addToTourMessage, Snackbar.LENGTH_SHORT);
                    snackbar.show();

                    mArgs[0] = cursor.getString(cursor.getColumnIndex(PlacesContract.Places.PLACE_ID));

                    ContentValues values = new ContentValues();
                    values.put(PlacesContract.Places.IS_SAVED, "1");
                    fragmentActivity.getContentResolver().update(PlacesContract.Places.CONTENT_URI, values, mSelectionClause, mArgs);

                }
            }
        });

        //Set up onclick listener
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PlaceDetailFragment fragment = PlaceDetailFragment.newInstance(placeId);

                FragmentManager fragManager = fragmentActivity.getSupportFragmentManager();

                fragManager.beginTransaction()
                        .add(R.id.container, fragment)
                        .addToBackStack(null)
                        .commit();

            }
        });

        //Add content descriptors
        viewHolder.addButton.setContentDescription(fragmentActivity.getResources().getString(R.string.msg_add_to_tour));
        String placePicFormat = fragmentActivity.getResources().getString(R.string.msg_image2);
        String placePicMessage = String.format(placePicFormat, placeName);
        viewHolder.mainPhoto.setContentDescription(placePicMessage);
    }


}
