package com.slc.android.sceneliner.control;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Robert Gruemmer on 6/3/2015.
 */
class User {

    ParseUser curUser;
    String id;
    String userName;
    String email;
    boolean isProprietor = false;
    Business curBusinessInfo = null;
    HashMap<String, ArrayList<String>> favorites; //region, favorites

    public User(ParseUser usr){
        curUser = usr;
        id = usr.getObjectId();
        userName = usr.getUsername();
        email = usr.getEmail();
        isProprietor = usr.getBoolean("isProprietor");
        if (isProprietor)
            curBusinessInfo = new Business(AppController.appContext, curUser);
    }


}