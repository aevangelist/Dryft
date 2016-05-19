package com.alelievangelista.dryft.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alelievangelista.dryft.R;
import com.alelievangelista.dryft.ui.Hour;

import java.util.ArrayList;

/**
 * Created by aevangelista on 16-04-29.
 */
public class HoursListAdapter extends BaseAdapter {

    private ArrayList<Hour> hourArrayList;
    private Context context;
    private static LayoutInflater inflater = null;


    public HoursListAdapter(Context context, ArrayList<Hour> hours) {
        this.context = context;
        this.hourArrayList = hours;

        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return hourArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        HourHolder holder = new HourHolder();

            View rowView = inflater.inflate(R.layout.list_item_hours, null);

            Hour hour = hourArrayList.get(position);

            holder.dayTextView = (TextView) rowView.findViewById(R.id.place_day);
            holder.hoursTextView = (TextView) rowView.findViewById(R.id.place_hours);

            if (holder.dayTextView != null) {
                holder.dayTextView.setText(hour.getDay());
            }

            if (holder.hoursTextView != null) {
                holder.hoursTextView.setText(hour.getHours());
            }

        return rowView;
    }

    public class HourHolder {
        TextView dayTextView;
        TextView hoursTextView;
    }
}
