package com.slc.android.sceneliner.control;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
//import com.parse.ParseCrashReporting;

/**
 * Created by Robert on 9/21/2015.
 */
public class ParseApplication extends Application{

    public static final String PARSE_APP_KEY = "qJBsxM22BwWCrK3OTO033bMteUxhaC2J2LzkgxBh";
    public static final String PARSE_CLIENT_KEY = "aKUCpkBjixCgauLeV9zlFr2DPrqgzdDRqEl1VFha";
    public static final String BEANSTOCK_URL = "http://parseserver-642yy-env.us-east-1.elasticbeanstalk.com/parse/";

    @Override
    public void onCreate(){
        super.onCreate();


        //ParseCrashReporting.enable(this);
        Parse.enableLocalDatastore(this);

        Parse.initialize(this,PARSE_APP_KEY,PARSE_CLIENT_KEY);
        /*try {
            Parse.initialize(new Parse.Configuration.Builder(this)
                            .applicationId(PARSE_APP_KEY)
                            .clientKey(PARSE_CLIENT_KEY)
                            .server(BEANSTOCK_URL)
                            .build()
            );
        } catch(Exception e) {
            Log.e("parseapplication", "failed to initalize");


        }*/



    }
}
