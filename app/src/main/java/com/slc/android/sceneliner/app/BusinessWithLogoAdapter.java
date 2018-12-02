package com.slc.android.sceneliner.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.slc.android.sceneliner.control.AppController;
import com.slc.android.sceneliner.control.Business;
import com.slc.android.sceneliner_1_0.R;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Robert on 9/25/2015.
 */
public class BusinessWithLogoAdapter extends BaseAdapter {

    ArrayList<Business> businessList;
    Context context;
    private static LayoutInflater inflater = null;

    public BusinessWithLogoAdapter(Context context, ArrayList<Business> businessList) {
        this.context = context;
        this.businessList = businessList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return businessList.size();
    }

    @Override
    public Object getItem(int position) {
        return businessList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class RowHolder {
        ImageView businessLogo;
        TextView businessName;
        TextView businessType;
        TextView isBusinessActive;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RowHolder rowHolder = new RowHolder();
        View rowView;

        rowView = inflater.inflate(R.layout.business_list, null);
        rowHolder.businessLogo = (ImageView) rowView.findViewById(R.id.customListViewLogoImageView);
        rowHolder.businessName = (TextView) rowView.findViewById(R.id.customListViewNameTextView);
        rowHolder.businessType = (TextView) rowView.findViewById(R.id.customListViewTypeTextView);
        rowHolder.isBusinessActive = (TextView) rowView.findViewById(R.id.customListIsActiveTextView);

        //rowHolder.businessLogo.setImageResource(R.drawable.ic_launcher);
        rowHolder.businessLogo.setImageBitmap(businessList.get(position).getLogoImage());
        rowHolder.businessName.setText(businessList.get(position).getName());
        rowHolder.businessType.setText(businessList.get(position).getType());
        String activeMessage;
        int color;
        if (businessList.get(position).isActive()) {
            activeMessage = "Currently Streaming";
            color = Color.GREEN;
        } else {
            activeMessage = "Not Currently Streaming";
            color = Color.RED;
        }
        rowHolder.isBusinessActive.setText(activeMessage);
        rowHolder.isBusinessActive.setTextColor(color);


        return rowView;
    }

}