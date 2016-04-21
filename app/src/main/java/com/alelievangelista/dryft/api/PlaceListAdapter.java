package com.alelievangelista.dryft.api;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alelievangelista.dryft.R;
import com.alelievangelista.dryft.data.PlacesContract;
import com.squareup.picasso.Picasso;

/**
 * Created by aevangelista on 16-04-19.
 */
public class PlaceListAdapter extends CursorAdapter {

    public static class ViewHolder {
        public final TextView name;
        public final TextView address;
        public final TextView category;
        public final ImageView mainPhoto;


        public ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.placeName);
            address = (TextView) view.findViewById(R.id.placeAddress);
            category = (TextView) view.findViewById(R.id.placeCategory);
            mainPhoto = (ImageView) view.findViewById(R.id.placePic);

        }
    }

    public PlaceListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        Log.d("PlaceListAdapter", "Creating the adapter");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_place, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String placeName = cursor.getString(cursor.getColumnIndex(PlacesContract.Places.NAME));
        viewHolder.name.setText(placeName);

        String placeAddress = cursor.getString(cursor.getColumnIndex(PlacesContract.Places.ADDRESS));
        //Format address
        String strArray[] = placeAddress.split(",");
        String s1 = strArray[0];
        String s = s1.substring(2, s1.length() - 1);

        viewHolder.address.setText(s);

        String placeCategory = cursor.getString(cursor.getColumnIndex(PlacesContract.Places.CATEGORY));
        viewHolder.category.setText(placeCategory);

        String imgUrl = cursor.getString(cursor.getColumnIndex(PlacesContract.Places.MAIN_PHOTO));
        Picasso.with(context).load(imgUrl).into(viewHolder.mainPhoto);

    }
}
