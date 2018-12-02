package com.slc.android.sceneliner.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.slc.android.sceneliner.control.AppController;
import com.slc.android.sceneliner.control.Business;
import com.slc.android.sceneliner_1_0.R;

import java.util.ArrayList;
import java.util.Set;


public class MainActivity extends ActionBarActivity {

    private Set<String> regionList = null;
    private ArrayList<Business> perRegionBusinessList = null;
    private ArrayList<Bitmap> businessLogos = null;

    private ViewFlipper displayChanger;
    private ListView regionsListView;
    private ListView perRegionBusinessListView;
    private static final long SPLASH_SCREEN_DURATION_IN_MS = 750;
    private Context appContext;
    private Boolean initialLaunch = true;
    private Toolbar toolbar;
    private TextView toolBarTitleView;
    
    private Handler businessListCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = this.getApplicationContext();
        AppController.initialize(appContext);
        setContentView(R.layout.activity_main);
        displayChanger = (ViewFlipper) findViewById(R.id.browserFlipper);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolBarTitleView = (TextView) findViewById(R.id.mainActivityToolBarLabel);

        setSupportActionBar(toolbar);

        getSupportActionBar().hide();
        //getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setCustomView(R.layout.toolbar);
        //toolbar.setLogo(R.drawable.lynes_logo_small);
        toolbar.setTitleTextColor(0xFFFFFFFF);

    }

    private void populateRegionsList() {
        //TODO:  start timer task to check for the region list ready before populating list.
        getSupportActionBar().show();
        //toolBarTitleView.setText("Regions");
        toolbar.setTitle("Regions");
        regionList = AppController.getRegionsList();
        regionsListView = (ListView) findViewById(R.id.regionBrowseScreenListView);
        regionsListView.setAdapter(new RegionsListAdapter(getApplicationContext(),
                new ArrayList<String>(regionList)));



       /* ArrayAdapter<String> regionListAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, new ArrayList<String>(regionList));
        regionsListView.setAdapter(regionListAdapter);*/
        regionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RegionsListAdapter regionsList = (RegionsListAdapter) parent.getAdapter();
                String targetRegion = (String) regionsList.getItem(position);
                populateBusinessListForRegionAndSwitchView(targetRegion);
            }
        });
    }

    private void populateBusinessListForRegionAndSwitchView(String region){
        //toolBarTitleView.setText("Businesses");
        toolbar.setTitle("Businesses");
        toolbar.setNavigationIcon(R.drawable.left_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToRegionsScreen();
            }
        });
        AppController.setCurrentRegion(region);
        perRegionBusinessList = AppController.getBusinessListForCurrentRegion();

        perRegionBusinessListView = (ListView) findViewById(R.id.businessBrowseScreenListView);
        perRegionBusinessListView.setAdapter(new BusinessWithLogoAdapter(getApplicationContext(),
                perRegionBusinessList));

        perRegionBusinessListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setTargetBusinessAndStartPlayer(position);
            }
        });


        //TODO: need to do set the next layout with displayChanger

        if (displayChanger.getDisplayedChild() == 1) {
            displayChanger.setOutAnimation(this, R.anim.abc_slide_out_bottom);
            displayChanger.setInAnimation(this, R.anim.abc_slide_in_top);
            displayChanger.showNext();
        }
    }


    private void setTargetBusinessAndStartPlayer(int businessIndex) {
        if (AppController.getCurrentBusiness() == null) {
            AppController.setCurrentBusiness(businessIndex);
            Intent intent = new Intent(appContext, SLPlayerActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_logout) {
            AppController.userLogout();
            while (displayChanger.getDisplayedChild() > 0) {
                displayChanger.setInAnimation(this, R.anim.abc_slide_in_bottom);
                displayChanger.setOutAnimation(this, R.anim.abc_slide_out_top);
                displayChanger.showPrevious();
            }
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_update_map) {
            while (displayChanger.getDisplayedChild() > 0) {
                displayChanger.setInAnimation(this, R.anim.abc_slide_in_bottom);
                displayChanger.setOutAnimation(this, R.anim.abc_slide_out_top);
                displayChanger.showPrevious();
            }
            Handler delay = new Handler();
            delay.postDelayed(new UpdateBusinessMapDelay(), 500);

        }

        return super.onOptionsItemSelected(item);
    }

    class UpdateBusinessMapDelay implements Runnable {

        @Override
        public void run() {
            AppController.setBusinessMapStatus(AppController.BUSINESS_MAP_EMPTY);
            /*CharSequence message = "Updating Business Lists";
            Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show();*/
            progress = new ProgressDialog(MainActivity.this);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setTitle("Please wait...");
            progress.setMessage("Updating Business Lists...");
            progress.setIndeterminate(true);
            progress.setCanceledOnTouchOutside(false);
            progress.show();
            endOfSplashScreenRoutine();
        }
    }

    private void goBackToRegionsScreen() {
        AppController.setCurrentRegion(null);
        displayChanger.setInAnimation(this, R.anim.abc_slide_in_bottom);
        displayChanger.setOutAnimation(this, R.anim.abc_slide_out_top);
        displayChanger.showPrevious();
        toolbar.setTitle("Regions");
        toolbar.setNavigationIcon(null);
    }

    @Override
    public void onBackPressed() {
        if (displayChanger.getDisplayedChild() == 2) {
            goBackToRegionsScreen();
        } else {
            this.finish();
        }
    }

   /* class DelayForBusinessMapUpdate implements Runnable {
        @Override
        public void run() {

            if (!AppController.isBusinessListReady()) {
                businessListCallback = new Handler();
                businessListCallback.postDelayed(new DelayForBusinessMapUpdate(), 500);
            } else {
                populateRegionsList();
            }
        }
    }*/
    
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onResume() {
        super.onResume();
        ConnectivityManager cm =
                (ConnectivityManager)appContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isNetworkConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (initialLaunch) {
            initialLaunch = false;

            progress = new ProgressDialog(MainActivity.this);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setTitle("Please wait...");
            progress.setMessage("Acquiring Business Lists...");
            progress.setIndeterminate(true);
            progress.setCanceledOnTouchOutside(false);
            progress.show();

            Handler splashScreenTimer = new Handler();
            splashScreenTimer.postDelayed(new Runnable() {
                @Override
                public void run() {

                    endOfSplashScreenRoutine();
                }
            }, SPLASH_SCREEN_DURATION_IN_MS);
        } else {
            endOfSplashScreenRoutine();
        }


    }

    private ProgressDialog progress;
    private boolean isNetworkConnected;


    private void endOfSplashScreenRoutine() {

        if (!isNetworkConnected) {
            CharSequence message = "No internet connection detected... Please check networks settings";
            Toast.makeText(appContext, message, Toast.LENGTH_LONG).show();
            this.finish();
        } else {



            if (!AppController.isUserLoggedIn()) {
                Intent intent = new Intent(appContext, LoginActivity.class);
                startActivity(intent);
            } else if (AppController.getBusinessMapStatus() == AppController.BUSINESS_MAP_EMPTY) {
                AppController.getCloudData();
                businessListCallback = new Handler();
                businessListCallback.postDelayed(new DelayForBusinessMap(), 500);
            } else if (AppController.getBusinessMapStatus() == AppController.BUSINESS_MAP_BUILDING) {
                businessListCallback = new Handler();
                businessListCallback.postDelayed(new DelayForBusinessMap(), 500);
            } else if (AppController.getBusinessMapStatus() == AppController.BUSINESS_MAP_READY) {
                progress.dismiss();
                Log.e("MAIN_ACTIVITY", "Dismissing Dialog");
                if (displayChanger.getDisplayedChild() == 0) {
                    displayChanger.setInAnimation(appContext, R.anim.abc_slide_in_bottom);
                    displayChanger.setOutAnimation(appContext, R.anim.abc_slide_out_top);
                    displayChanger.showNext();
                    populateRegionsList();
                }
                /*runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                        Log.e("MAIN_ACTIVITY", "Dismissing Dialog");
                        if (displayChanger.getDisplayedChild() == 0) {
                            displayChanger.setInAnimation(appContext, R.anim.abc_slide_in_bottom);
                            displayChanger.setOutAnimation(appContext, R.anim.abc_slide_out_top);
                            displayChanger.showNext();
                            populateRegionsList();
                        }
                    }
                });*/

            }
        }


    }

    class DelayForBusinessMap implements Runnable {
        @Override
        public void run() {
            endOfSplashScreenRoutine();
        }
    }



}
