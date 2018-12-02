package com.slc.android.sceneliner.app;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.slc.android.sceneliner.control.Business;
import com.slc.android.sceneliner_1_0.R;

import java.util.ArrayList;

/**
 * Created by Robert on 10/10/2015.
 */
public class RegionsListAdapter extends BaseAdapter{

    ArrayList<String> regionsList;
    Context context;
    private static LayoutInflater inflater = null;

    public RegionsListAdapter(Context context, ArrayList<String> regionsList) {
        this.context = context;
        this.regionsList = regionsList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return regionsList.size();
    }

    @Override
    public Object getItem(int position) {
        return regionsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class RowHolder {
        TextView regionName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RowHolder rowHolder = new RowHolder();
        View rowView;

        rowView = inflater.inflate(R.layout.regions_list, null);
        rowHolder.regionName = (TextView) rowView.findViewById(R.id.regionsListTextView);

        rowHolder.regionName.setText(regionsList.get(position));

        return rowView;
    }
}
