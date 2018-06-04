package xyz.jienan.refreshed.base;

import android.app.Application;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by jienanzhang on 11/01/2018.
 */

public class RefreshedApplication extends MultiDexApplication {

    private static RefreshedApplication mInstance;
    public static boolean isGoogleServiceAvaliable = false;
    public static RefreshedApplication getInstance() {
        return mInstance;
    }
    private RxBus bus;

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        mInstance = this;
        isGoogleServiceAvaliable = isGooglePlayServicesAvailable();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("news.realm")
                .schemaVersion(0)
                .build();
        bus = new RxBus();
        AnalyticsManager.getInstance().setContext(this);
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        Integer resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    public RxBus bus() {
        return bus;
    }
}
