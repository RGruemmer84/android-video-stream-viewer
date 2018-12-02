package com.slc.android.sceneliner.control;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseUser;
import com.slc.android.sceneliner.app.SLPlayerActivity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Robert Gruemmer on 6/3/2015.
 */
public class Business implements Comparable<Business> {

    String id;
    String name;
    String region;
    String address;
    String phoneNumber;
    String type;
    ArrayList<String> specials;
    ArrayList<String> camUrlList;
    int numCameras;
    boolean isActive;

    String logoUrlString;
    URL logoUrl = null;
    Bitmap logoImage = null;
    Context appContext;

    String baseUrl;

    public Business(Context context, ParseUser business) {
        appContext = context;
        id = business.getObjectId();
        name = (String) business.get("BusinessName");
        region = (String) business.get("Region");
        address = (String) business.get("Address");
        phoneNumber = (String) business.get("PhoneNumber");
        numCameras = (Integer) business.get("numCameras");
        type = (String) business.get("BusinessType");
        isActive = (Boolean) business.get("isCameraActive");
        baseUrl = (String) business.get("File_URL");

        specials = new ArrayList<String>();
        specials.add((String) business.get("Special_0"));
        specials.add((String) business.get("Special_1"));
        specials.add((String) business.get("Special_2"));
        specials.add((String) business.get("Special_3"));


        camUrlList = new ArrayList<String>();

        for (int i = 0; i < numCameras; i++) {
            camUrlList.add(baseUrl + "cam_feed_" + String.valueOf(i) + ".mp4");
        }

        logoUrlString = baseUrl + "icon.png";

        try {
            logoUrl = new URL(logoUrlString);
        } catch (MalformedURLException e) {
            Log.e("BUSINESS_CLASS", "Malformed Logo Url");
        }

        Object[] dummyArray = new Object[1];
        dummyArray[0] = this;
        new DownloadLogoImage().execute(dummyArray);


    }

    public boolean isActive() {
        return isActive;
    }

    public URL getLogoUrl() {
        return logoUrl;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Bitmap getLogoImage(){
        return logoImage;
    }

    //This is for sort purposes only...
    @Override
    public int compareTo(Business another) {
        return name.compareTo(another.name);
    }



    class DownloadLogoImage extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            Business b = (Business) params[0];
            try {
                logoImage = BitmapFactory.decodeStream(b.getLogoUrl().openConnection().getInputStream());
            } catch (IOException e) {
                Log.e("BUSINESS_CLASS", "IOException: failed to download logo image.");
                logoImage = AppController.getAppIcon();
            }
            return null;
        }
    }
}