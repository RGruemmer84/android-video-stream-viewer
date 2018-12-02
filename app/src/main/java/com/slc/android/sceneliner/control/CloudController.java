package com.slc.android.sceneliner.control;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Robert Gruemmer on 5/30/2015.
 */

public class CloudController{

    private static Context appContext;
    static User curUser = null;
    private static HashMap<String, ArrayList<Business>> businessMap;
    private static boolean businessMapReady = false;
    private static boolean logosReady = false;
    static boolean curBusinessEnabled = false;
    static boolean queryInProgress = false;

    public static final String PARSE_APP_KEY = "qJBsxM22BwWCrK3OTO033bMteUxhaC2J2LzkgxBh";
    public static final String PARSE_CLIENT_KEY = "aKUCpkBjixCgauLeV9zlFr2DPrqgzdDRqEl1VFha";


    public CloudController(Context context, String appKey, String clientKey, HashMap<String, ArrayList<Business>> businessMap){
        throw new UnsupportedOperationException();
    }

    public static void initialize(Context context) {
        appContext = context;
        /*Parse.enableLocalDatastore(context);
        Parse.initialize(context, PARSE_APP_KEY, PARSE_CLIENT_KEY);*/
    }
    
    public static boolean isUserLoggedInUser(){
        if (curUser == null) {
            ParseUser curParseUser = ParseUser.getCurrentUser();
            if (curParseUser == null) {
                return false;
            } else {
                curUser = new User(curParseUser);
                updateFeedsMap();
                return true;
            }
        } else
            return true;
    }




    public static void login(String user, String pw) {
        ParseUser.logInInBackground(user, pw, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser != null) {
                    Log.i("PARSE_LOGIN", "Logged in successfully");
                    getCloudData();
                    curUser = new User(parseUser);
                } else {
                    Log.e("PARSE_LOGIN", "Failed to login!");
                    CharSequence message = "Failed to login!";
                    Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public static void logout() {
        ParseUser.logOut();
        curUser = null;
    }

    public static void createAccount(String user, String pw, String pwVerify, String email) {
        if (!pw.equals(pwVerify)) {
            Log.e("CREATE_ACCOUNT", "PASSWORD DOESN'T MATCH");
            CharSequence message = "Passwords don't match!";
            Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show();
        } else {
            ParseUser pUser = new ParseUser();
            ParseACL userAcl = new ParseACL();
            userAcl.setPublicWriteAccess(false);
            userAcl.setPublicReadAccess(false);
            pUser.setACL(userAcl);
            pUser.setUsername(user);
            pUser.setPassword(pw);
            pUser.setEmail(email);
            pUser.put("isProprietor", false);

            pUser.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        Log.i("CREATE_ACCOUNT", "Account created successfully");
                        curUser = new User(ParseUser.getCurrentUser());
                        getCloudData();
                    } else {
                        Log.e("CREATE_ACCOUNT", "Unable to create account");
                        CharSequence message = "Problem creating account.  Contact Support";
                        Toast.makeText(appContext, message, Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    }

    public static void deleteAccount() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", curUser.id);
        query.findInBackground(new FindCallback<ParseUser>() {

            public void done(List<ParseUser> parseUser, ParseException e) {
                if (e == null) {
                    ParseUser.deleteAllInBackground(parseUser);
                    curUser = null;
                }
            }
        });

    }

    public static Set<String> getRegionsList() {
        return (businessMapReady) ? businessMap.keySet() : null;
    }

    public static ArrayList<Business> getBusinessListForRegion(String region) {
        return (region == null) ? null : businessMap.get(region);
    }


    public static Business getBusiness(String region, int arrayIndex) {
        return businessMap.get(region).get(arrayIndex);
    }

    public static void getCloudData() {
        updateFeedsMap();
    }

    private static List<ParseUser>  businesses;
    public static void updateFeedsMap() {
        businessMap = null;
        AppController.setBusinessMapStatus(AppController.BUSINESS_MAP_BUILDING);
        businessMapReady = false;

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("isProprietor", true);
        query.findInBackground(new FindCallback<ParseUser>() {

            public void done(List<ParseUser> businessOwners, ParseException e) {
                if (e == null) {
                    businesses = businessOwners;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            businessMap = new HashMap<String, ArrayList<Business>>();
                            Business business;

                            for (ParseUser businessOwner : businesses) {

                                String region = (String) businessOwner.get("Region");

                                ArrayList<Business> curRegionBusinessList = businessMap.get(region);

                                if (curRegionBusinessList == null)
                                    curRegionBusinessList = new ArrayList<Business>();

                                business = new Business(appContext, businessOwner);

                                curRegionBusinessList.add(business);
                                businessMap.put(region, curRegionBusinessList);
                            }

                            for (String location : businessMap.keySet()) {
                                ArrayList<Business> curRegionBusinessList = businessMap.get(location);
                                Collections.sort(curRegionBusinessList);
                            }


                            do {
                                logosReady = true;
                                for (String location : businessMap.keySet()) {
                                    ArrayList<Business> curRegionBusinessList = businessMap.get(location);
                                    for (Business b : curRegionBusinessList) {
                                        if (b.logoImage == null) {
                                            logosReady = false;
                                        }
                                    }
                                }
                            } while (!logosReady);

                            AppController.setBusinessMapStatus(AppController.BUSINESS_MAP_READY);
                            businessMapReady = true;
                        }
                    }).start();



                } else {
                    Log.e("CLOUD_CONTROLLER", "Failed to update Feeds List");
                    CharSequence message = "Failed to get business info from server";
                    Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public static boolean isBusinessMapReady() {
        return businessMapReady;
    }



    public static boolean isCurUserABusiness() {
        return curUser.isProprietor;
    }

    public static void setBusinessEnabled(boolean enable) {
        if (curUser.isProprietor) {
            ParseUser user = curUser.curUser;
            user.put("isCameraActive", enable);
            user.saveInBackground();
        }
    }


    public static void updateSpecials(String special1, String special2, String special3, String special4) {
        ParseUser usr = curUser.curUser;
        usr.put("Special_0", special1);
        usr.put("Special_1", special2);
        usr.put("Special_2", special3);
        usr.put("Special_3", special4);
        usr.saveInBackground();
    }

    public static void checkIfCurrentBusinessIsActive(Business curBus) {
        curBusinessEnabled = false;
        queryInProgress = true;
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", curBus.id);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (parseUsers == null || e != null) {
                    Log.e("CLOUD_CONTROLLER", "Failed to update Feeds List");
                    CharSequence message = "Failed to get business info from server";
                    Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show();
                    queryInProgress = false;
                } else {
                    curBusinessEnabled = (boolean) parseUsers.get(0).get("isCameraActive");
                    queryInProgress = false;
                }

            }
        });
    }
}
