package com.slc.android.sceneliner.control;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.slc.android.sceneliner_1_0.R;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Robert Gruemmer on 5/30/2015.
 */
public class MediaManager implements MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnInfoListener{

    private Context appContext;
    private Timer interruptHandler;
    private Business curBusiness;

    private SurfaceHolder sHolder;
    private SurfaceView surface;
    MediaPlayer mPlayer;
    private int numCameras;
    private int curCamera;

    private ProgressDialog progress;

    private boolean playVideoFlag = true;

    public MediaManager(Context context, SurfaceHolder sh, SurfaceView surface, Business business,
                        ProgressDialog progress) {
        appContext = context;
        curBusiness = business;
        numCameras = business.numCameras;
        sHolder = sh;
        this.surface = surface;
        this.progress = progress;
        interruptHandler = new Timer();
        curCamera = 0;
        initializeMedia(curCamera);
        //createTimerInterruptForUpdate(100);
    }



    private void initializeMedia(int camNum) {

        Uri sourceUri = Uri.parse(curBusiness.camUrlList.get(camNum));
        mPlayer = new MediaPlayer();
        mPlayer.setOnErrorListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setLooping(false);
        mPlayer.setDisplay(sHolder);

        while (sHolder.isCreating()) {
            Log.e("MEDIA_DRIVER", "SURFACE NOT CREATED YET!");
        }

        try {
            mPlayer.setDataSource(appContext, sourceUri);
        } catch (IllegalArgumentException ex) {
            Log.e("MEDIA_DRIVER", "Failed to set data source: Illegal Argument Exception");
        } catch (SecurityException ex) {
            Log.e("MEDIA_DRIVER", "Failed to set data source: Security Exception");
        } catch (IllegalStateException ex) {
            Log.e("MEDIA_DRIVER", "Failed to set data source: Illegal State Exception");
        } catch (IOException ex) {
            Log.e("MEDIA_DRIVER", "Failed to set data source: I/O Exception");
        }

        mPlayer.prepareAsync();
        Handler bufferMonitorHandler = new Handler();

    }

    class BufferMonitorHandler implements Runnable {

        @Override
        public void run() {

        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        surface.setBackground(null);
        AppController.setVideoReadyFlag(true);
        Log.e("MEDIA_MANAGER", "Starting stream");
        mPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mPlayer.release();
        initializeMedia(curCamera);
    }

    public void killFeed() {
        mPlayer.release();
    }

    public void resumeFeed() {
        initializeMedia(curCamera);
    }


    public void switchToNextCamera(){

        if ( curCamera < numCameras - 1 ) {
            mPlayer.release();
            curCamera++;
            initializeMedia(curCamera);
            Log.e("MEDIA_MANAGER", "Switching to next camera");
        }
    }

    public void switchToPreviousCamera(){

        if ( curCamera > 0 ) {
            mPlayer.release();
            curCamera--;
            initializeMedia(curCamera);
            Log.e("MEDIA_MANAGER", "Switching to previous camera");
        }
    }

    public int getNumCameras() {
        return numCameras;
    }

    public int getCurCamNum() {
        return curCamera;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        CharSequence message = "We're sorry, there was a problem loading this video";
        Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show();
        surface.setBackgroundResource(R.drawable.ic_launcher);
        return false;
    }


    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    private boolean bufferingStarted = false;
    private boolean bufferingTimedOut = false;

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                bufferingStarted = true;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:

        }
        return false;
    }
}
