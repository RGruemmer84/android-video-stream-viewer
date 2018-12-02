package com.slc.android.sceneliner.app;

import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.slc.android.sceneliner_1_0.R;
import com.slc.android.sceneliner.control.AppController;

import java.util.ArrayList;


public class SLPlayerActivity extends ActionBarActivity implements SurfaceHolder.Callback{

    private LinearLayout videoLayout;
    private SurfaceView playerSurfacePortrait;
    private SurfaceView playerSurfaceLandscape;

    private TextView titleTextView;
    private TextView mainTitleTextView;
    private TextView businessTypeTextView;
    private TextView addressView;
    private TextView phoneNumView;
    private TextView feedNumView;

    private TextView phoneButton;
    private TextView mapButton;

    private ImageView leftArrowButton;
    private ImageView rightArrowButton;

    private TextView special1;
    private TextView special2;
    private TextView special3;
    private TextView special4;

    private SurfaceHolder sHolder;
    private int surfaceWidthPortrait;
    private int surfaceHeightPortrait;
    private int surfaceLayoutWidthPortrait;
    private int surfaceLayoutHeightPortrait;

    private ProgressDialog progress;

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_slplayer);
        videoLayout = (LinearLayout) findViewById(R.id.playerScreenVideoLayout);
        playerSurfacePortrait = (SurfaceView) findViewById(R.id.playerScreenSurfaceView);
        titleTextView = (TextView) findViewById(R.id.titleView);
        businessTypeTextView = (TextView) findViewById(R.id.playerScreenBusinessTypeLabel);
        addressView = (TextView) findViewById(R.id.addressView);
        phoneNumView = (TextView) findViewById(R.id.phoneNumberView);
        feedNumView = (TextView) findViewById(R.id.feedNumView);
        leftArrowButton = (ImageView) findViewById(R.id.leftArrowButton);
        rightArrowButton = (ImageView) findViewById(R.id.rightArrowButton);
        special1 = (TextView) findViewById(R.id.playerScreenSpecial1Label);
        special2 = (TextView) findViewById(R.id.playerScreenSpecial2Label);
        special3 = (TextView) findViewById(R.id.playerScreenSpecial3Label);
        special4 = (TextView) findViewById(R.id.playerScreenSpecial4Label);
        phoneButton = (TextView) findViewById(R.id.playerScreenCallButton);
        mapButton = (TextView) findViewById(R.id.playerScreenMapButton);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(AppController.getCurrentBusinessName());
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar.setNavigationIcon(R.drawable.left_arrow);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        titleTextView.setText(AppController.getCurrentBusinessName());
        businessTypeTextView.setText(AppController.getCurrentBusinessType());
        addressView.setText(AppController.getCurrentBusinessAddress());
        phoneNumView.setText(AppController.getCurrentBusinessPhoneNumber());

        ArrayList<String> contentList = AppController.getContentList();
        special1.setText(contentList.get(0));
        special2.setText(contentList.get(1));
        special3.setText(contentList.get(2));
        special4.setText(contentList.get(3));


        leftArrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("PLAYER_SCREEN", "Left arrow clicked.");
                switchToPreviousCamera();
            }
        });

        rightArrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("PLAYER_SCREEN", "Right arrow clicked.");
                switchToNextCamera();
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMapWithAddress(addressView.getText().toString());
            }
        });

        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchDialerWithNumber(phoneNumView.getText().toString());
            }
        });

        sHolder = playerSurfacePortrait.getHolder();
        sHolder.addCallback(this);

        ViewGroup.LayoutParams videoLayoutParams = videoLayout.getLayoutParams();
        surfaceLayoutHeightPortrait = videoLayoutParams.height;
        surfaceLayoutWidthPortrait = videoLayoutParams.width;

        ViewGroup.LayoutParams videoParams = playerSurfacePortrait.getLayoutParams();
        surfaceHeightPortrait = videoParams.height;
        surfaceWidthPortrait = videoParams.width;


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    class DelayForBusinessEnableCheck implements Runnable {
        Context appContext;

        DelayForBusinessEnableCheck(Context context) {
            this.appContext = context;
        }

        @Override
        public void run() {
            ConnectivityManager cm =
                    (ConnectivityManager)appContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            if (!isConnected) {
                CharSequence message = "Internet Connection seems to be down.";
                Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                        Log.e("PLAYER_ACTIVITY", "Dismissing Dialog");
                    }
                });
                return;
            } else {


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (AppController.getVideoReadyFlag() == false) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                Log.e("PLAYER_ACTIVITY", "Progress Thread interrupted exception occurred");
                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.dismiss();
                                Log.e("PLAYER_ACTIVITY", "Dismissing Dialog");
                            }
                        });
                    }
                }).start();


                if (AppController.isQueryInProgress()) {
                    Handler businessEnableCheckHandler = new Handler();
                    businessEnableCheckHandler.postDelayed(new DelayForBusinessEnableCheck(appContext), 500);
                } else {
                    if (AppController.isCurrentBusinessEnabled()) {
                        AppController.initializeMediaManagerForSurface(sHolder, playerSurfacePortrait, progress);
                        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                            feedNumView.setText(String.valueOf(AppController.getCurCamNum() + 1 ) + " / " +
                                    String.valueOf(AppController.getNumCamsForCurBusiness()));
                        }
                    } else {
                        Log.e("PLAYER_ACTIVITY", "Current Business not streaming");
                        CharSequence message = "Business not currently streaming";
                        Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }
    }



    private void switchToNextCamera() {

        AppController.switchToNextCamera();
        feedNumView.setText(String.valueOf(AppController.getCurCamNum() + 1 ) + " / " +
                String.valueOf(AppController.getNumCamsForCurBusiness()));
    }

    private void switchToPreviousCamera() {

        AppController.switchToPreviousCamera();
        feedNumView.setText(String.valueOf(AppController.getCurCamNum() + 1 ) + " / " +
                String.valueOf(AppController.getNumCamsForCurBusiness()));
    }



    private void launchDialerWithNumber(String s) {
        String num = AppController.getCurrentBusinessPhoneNumber();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(String.format("tel:%s", num)));
        startActivity(intent);
    }

    private void launchMapWithAddress(String s) {
        String map = "http://maps.google.co.in/maps?q=" + s;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        playerSurfacePortrait.setBackground(new BitmapDrawable(null,
                AppController.getCurrentBusiness().getLogoImage()));
        AppController.setVideoReadyFlag(false);
        AppController.checkIfCurrentBusinessIsActive();
        Handler businessEnableCheckHandler = new Handler();
        businessEnableCheckHandler.postDelayed(new DelayForBusinessEnableCheck(getApplicationContext()), 500);

        progress = new ProgressDialog(SLPlayerActivity.this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setTitle("Please wait...");
        progress.setMessage("Downloading video feed...");
        progress.setCancelable(true);
        progress.setIndeterminate(true);
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        Log.e("PLAYER_ACTIVITY", "Showing progress dialog");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("PLAYER", "Pausing app");
        AppController.killMediaManager();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppController.setCurrentBusiness(null);
        Log.e("PLAYER", "Killing Player Activity");
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            videoLayout.setBackgroundColor(Color.BLACK);
            //mainTitleTextView.setVisibility(View.GONE);
            getSupportActionBar().hide();

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            ViewGroup.LayoutParams videoLayoutParams = videoLayout.getLayoutParams();
            videoLayoutParams.width = displayMetrics.widthPixels;
            videoLayoutParams.height = displayMetrics.heightPixels;

            ViewGroup.LayoutParams videoParams = playerSurfacePortrait.getLayoutParams();
            videoParams.width = displayMetrics.widthPixels;
            videoParams.height = displayMetrics.heightPixels;
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //mainTitleTextView.setVisibility(View.VISIBLE);
            getSupportActionBar().show();



            ViewGroup.LayoutParams videoLayoutParams = videoLayout.getLayoutParams();
            videoLayoutParams.width = surfaceLayoutWidthPortrait;
            videoLayoutParams.height = surfaceLayoutHeightPortrait;

            ViewGroup.LayoutParams videoParams = playerSurfacePortrait.getLayoutParams();
            videoParams.width = surfaceWidthPortrait;
            videoParams.height = surfaceHeightPortrait;

        }

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

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
            if (AppController.isCurUserABusiness()) {
                Intent intent = new Intent(this, BusinessSettingsActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            }
        } /*else if (id == R.id.action_logout) {
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

        }*/

        return super.onOptionsItemSelected(item);
    }


}
