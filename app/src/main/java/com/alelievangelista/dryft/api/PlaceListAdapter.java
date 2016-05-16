package com.alelievangelista.dryft.api;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alelievangelista.dryft.R;
import com.alelievangelista.dryft.data.PlacesContract;
import com.alelievangelista.dryft.ui.MainActivity;
import com.alelievangelista.dryft.ui.PlaceDetailFragment;
import com.squareup.picasso.Picasso;

/**
 * Created by aevangelista on 16-04-19.
 */
public class PlaceListAdapter extends CursorAdapter {

    private static final String LOG_TAG = "PlaceListAdapter";
    private static final String PLACE_ID_TAG = "PlaceIdTag";

    private Cursor cursor;
    private MainActivity fragmentActivity;

    public static class ViewHolder {
        public final TextView name;
        //public final TextView address;
        public final TextView category;
        public final ImageView mainPhoto;


        public ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.placeName);
            //address = (TextView) view.findViewById(R.id.placeAddress);
            category = (TextView) view.findViewById(R.id.placeCategory);
            mainPhoto = (ImageView) view.findViewById(R.id.placePic);

        }
    }

    public PlaceListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.cursor = c;
        this.fragmentActivity = (MainActivity) context;
        Log.d("PlaceListAdapter", "Creating the adapter");
    }

    public Cursor getCursor() {
        return cursor;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_place_2, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        final String placeId = cursor.getString(cursor.getColumnIndex(PlacesContract.Places.PLACE_ID));
        final String placeName = cursor.getString(cursor.getColumnIndex(PlacesContract.Places.NAME));

        Log.d(LOG_TAG, "New view: " + placeName + ", " + placeId);


        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

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

        //Set up onclick listener
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(LOG_TAG, "Choosing item: " + placeName + ", " + placeId);

                PlaceDetailFragment fragment = PlaceDetailFragment.newInstance(placeId);

                FragmentManager fragManager = fragmentActivity.getSupportFragmentManager();

                fragManager.beginTransaction()
                        .add(R.id.container, fragment)
                        .addToBackStack(null)
                        .commit();

            }
        });

    }


}
