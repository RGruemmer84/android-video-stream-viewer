package com.slc.android.sceneliner.control;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.parse.ParseUser;
import com.slc.android.sceneliner.app.SLPlayerActivity;
import com.slc.android.sceneliner_1_0.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 * Created by Robert Gruemmer on 6/1/2015.
 */
public class AppController{

    static Context appContext;
    private static String currentRegion;
    private static Business currentBusiness;
    private static MediaManager mediaManager;

    private static int businessMapStatus = 0;
    public static final int BUSINESS_MAP_EMPTY = 0;
    public static final int BUSINESS_MAP_BUILDING = 1;
    public static final int BUSINESS_MAP_READY = 2;

    public AppController(Context context) {
        throw new UnsupportedOperationException();
    }

    public static void initialize(Context context) {
        appContext = context;
        CloudController.initialize(context);
    }

    public static boolean isUserLoggedIn() {
        return CloudController.isUserLoggedInUser();
    }

    public static void userLogin(String user, String pw) {
        CloudController.login(user, pw);
    }

    public static void userLogout() {
        CloudController.logout();
    }

    public static String getCurrentUserEmail() {
        return CloudController.curUser.email;
    }

    public static String getCurrentUsername() {
        return CloudController.curUser.userName;
    }

    public static void createAccount(String user, String pw, String pwVerify, String email) {
        CloudController.createAccount(user, pw, pwVerify, email);
    }

    public static void deleteAccount() {
        CloudController.deleteAccount();
    }

   /* public static void updateFeedsMap() {
        CloudController.updateFeedsMap();
    }*/

    public static void getCloudData() {
        CloudController.getCloudData();
    }

    public static Set<String> getRegionsList() {
        return new TreeSet<String>(CloudController.getRegionsList());
    }

    public static void setCurrentRegion(String region) {
        currentRegion = region;
    }

    public static ArrayList<Business> getBusinessListForCurrentRegion() {
        return CloudController.getBusinessListForRegion(currentRegion);
    }

    public static void setCurrentBusiness(int businessIndex) {
        currentBusiness = CloudController.getBusiness(currentRegion, businessIndex);
    }


    public static void setCurrentBusiness(Business business) {
        currentBusiness = business;
    }

    public static boolean isCurUserABusiness() {
        return CloudController.isCurUserABusiness();
    }

    public static void initializeMediaManagerForSurface(SurfaceHolder sHolder, SurfaceView surface,
                                                        ProgressDialog progress) {
        killMediaManager();
        mediaManager =  new MediaManager(appContext, sHolder, surface, currentBusiness, progress);
    }

    public static void switchToNextCamera() {
        mediaManager.switchToNextCamera();
    }

    public static void switchToPreviousCamera() {
        mediaManager.switchToPreviousCamera();
    }

    public static int getCurCamNum() {
        return mediaManager.getCurCamNum();
    }

    public static void killMediaManager() {
        if (mediaManager != null)
            mediaManager.killFeed();
    }

    public static void resumeMediaManager() {
        mediaManager.resumeFeed();
    }

    public static Business getCurrentBusiness() {
        return currentBusiness;
    }

    public static int getNumCamsForCurBusiness() {
        return mediaManager.getNumCameras();
    }

    public static ArrayList<String> getContentList() {
        return currentBusiness.specials;
    }

    public static String getCurrentBusinessName() {
        return currentBusiness.name;
    }

    public static String getCurrentBusinessAddress() {
        String outputAddress;
        StringTokenizer st = new StringTokenizer(currentBusiness.address, ":");
        outputAddress = String.format("%s\n%s %s, %s", st.nextToken(), st.nextToken(),
                st.nextToken(), st.nextToken());

        return outputAddress;
    }

    public static String getCurrentBusinessPhoneNumber() {
        return currentBusiness.phoneNumber;
    }

    /*public static boolean isBusinessListReady() {
        return CloudController.isBusinessMapReady();
    }*/

    public static int getBusinessMapStatus() {
        return businessMapStatus;
    }

    public static void setBusinessMapStatus(int status) {
        if (status < 0 || status > 2)
            throw new IllegalArgumentException();
        else
            businessMapStatus = status;

    }

    public static void setBusinessEnabled(boolean enable) {
        CloudController.setBusinessEnabled(enable);
    }

    public static boolean isCurrentBusinessEnabled() {
        return CloudController.curBusinessEnabled;
    }

    public static boolean isQueryInProgress() {
        return CloudController.queryInProgress;
    }

    public static void updateSpecials(String special1, String special2, String special3, String special4) {
        CloudController.updateSpecials(special1, special2, special3, special4);
    }

    public static void checkIfCurrentBusinessIsActive() {
        CloudController.checkIfCurrentBusinessIsActive(currentBusiness);
    }

    public static String getCurrentBusinessType() {
        return currentBusiness.type;
    }

    public static ArrayList<String> getCurrentBusinessSpecials() {
        if (CloudController.isCurUserABusiness()) {
            return CloudController.curUser.curBusinessInfo.specials;
        } else
            return null;

    }

    public static String getCurrentUserRegion() {
        return CloudController.curUser.curBusinessInfo.region;
    }

    public static Business getCurrentUserBusinessObject() {
        return CloudController.curUser.curBusinessInfo;
    }

    public static Bitmap getAppIcon() {
        return BitmapFactory.decodeResource(appContext.getResources(), R.drawable.ic_launcher);
    }


    private static boolean videoReadyFlag = false;

    public static void setVideoReadyFlag(boolean b) {
        videoReadyFlag = b;
    }

    public static boolean getVideoReadyFlag() {
        return videoReadyFlag;
    }
}



