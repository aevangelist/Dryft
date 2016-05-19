package com.alelievangelista.dryft.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
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
 * Created by aevangelista on 16-05-19.
 */
public class MyTourListAdapter extends CursorAdapter {

    private static final String LOG_TAG = "MyTourListAdapter";
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
        public final ImageView deleteButton;

        public ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.placeName);
            category = (TextView) view.findViewById(R.id.placeCategory);
            mainPhoto = (ImageView) view.findViewById(R.id.placePic);
            deleteButton = (ImageView) view.findViewById(R.id.deleteFromTour);

        }
    }

    public MyTourListAdapter(Context context, Cursor c, int flags) {
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

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_mytour, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        final String placeId = cursor.getString(cursor.getColumnIndex(PlacesContract.Places.PLACE_ID));
        final String placeName = cursor.getString(cursor.getColumnIndex(PlacesContract.Places.NAME));


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

        final MyTourListAdapter adapter = this;

        //Set up button
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fragmentActivity != null && cursor != null){
                    String removeFromTourFormat = fragmentActivity.getResources().getString(R.string.remove_from_tour);
                    String removeFromTourMessage = String.format(removeFromTourFormat, placeName);

                    Snackbar snackbar = Snackbar
                            .make(v, removeFromTourMessage, Snackbar.LENGTH_SHORT);
                    snackbar.show();

                    mArgs[0] = placeId;

                    ContentValues values = new ContentValues();
                    values.put(PlacesContract.Places.IS_SAVED, "0");
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
        viewHolder.deleteButton.setContentDescription(fragmentActivity.getResources().getString(R.string.msg_remove_from_tour));
        String placePicFormat = fragmentActivity.getResources().getString(R.string.msg_image2);
        String placePicMessage = String.format(placePicFormat, placeName);
        viewHolder.mainPhoto.setContentDescription(placePicMessage);
    }
}
