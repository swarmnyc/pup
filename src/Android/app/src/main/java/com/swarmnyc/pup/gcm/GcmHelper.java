package com.swarmnyc.pup.gcm;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.swarmnyc.pup.Config;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.utils.Action;
import com.swarmnyc.pup.utils.StringUtils;

import java.io.IOException;

public class GcmHelper {
    private static final String TAG = "GcmHelper";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String GCMID = "GCMID";

    private GoogleCloudMessaging googleCloudMessaging;
    private Activity activity;

    public static void register(final Activity activity, final Action<String> callback) {

        if (checkPlayServices(activity)) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    GoogleCloudMessaging googleCloudMessaging = GoogleCloudMessaging.getInstance(activity);
                    try {
                        String gcmId = googleCloudMessaging.register(Config.getConfigString(R.string.google_gcm_key));
                        Config.setString(GCMID, gcmId);
                        if (callback != null)
                            callback.call(gcmId);

                    } catch (IOException e) {
                        Log.e(TAG, "register", e);
                    }
                }
            }).start();

        }

    }

    public static String GetId() {
        return Config.getString(GCMID);
    }

    public static void RegisterOrGetId(Activity activity, Action<String> callback) {
        String gcmId = Config.getString(GCMID);

        if (StringUtils.isEmpty(gcmId)) {
            register(activity, callback);
        } else {
            if (callback != null)
                callback.call(gcmId);
        }
    }

    private static boolean checkPlayServices(Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }

        return true;
    }
}