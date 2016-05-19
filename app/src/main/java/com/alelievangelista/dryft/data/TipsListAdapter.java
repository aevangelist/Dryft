package com.alelievangelista.dryft.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alelievangelista.dryft.R;

import java.util.ArrayList;

/**
 * Created by aevangelista on 16-04-30.
 */
public class TipsListAdapter extends BaseAdapter {

    private ArrayList<String> tipArrayList;
    private Context context;
    private static LayoutInflater inflater = null;

    public TipsListAdapter(Context context, ArrayList<String> tipArrayList) {
        this.context = context;
        this.tipArrayList = tipArrayList;

        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return tipArrayList.size();
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
        TipHolder holder = new TipHolder();

        View rowView = inflater.inflate(R.layout.list_item_tips, null);

        String tip = tipArrayList.get(position);

        holder.tipTextView = (TextView) rowView.findViewById(R.id.tip);

        if (holder.tipTextView != null) {
            holder.tipTextView.setText(tip);
        }

        return rowView;
    }

    public class TipHolder {
        TextView tipTextView;
    }
}
