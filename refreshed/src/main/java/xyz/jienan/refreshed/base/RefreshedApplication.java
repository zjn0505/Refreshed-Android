package xyz.jienan.refreshed.base;

import android.app.Application;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by jienanzhang on 11/01/2018.
 */

public class RefreshedApplication extends Application {

    private static RefreshedApplication mInstance;
    public static boolean isGoogleServiceAvaliable = false;
    public static RefreshedApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        isGoogleServiceAvaliable = isGooglePlayServicesAvailable();
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        Integer resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }
}
