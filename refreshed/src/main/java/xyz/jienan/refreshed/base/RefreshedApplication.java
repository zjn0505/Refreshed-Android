package xyz.jienan.refreshed.base;

import android.app.Application;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;
import xyz.jienan.refreshed.BuildConfig;

/**
 * Created by jienanzhang on 11/01/2018.
 */

public class RefreshedApplication extends Application {

    private static RefreshedApplication mInstance;
    public static boolean isGoogleServiceAvailable = false;
    public static RefreshedApplication getInstance() {
        return mInstance;
    }
    private RxBus bus;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        isGoogleServiceAvailable = isGooglePlayServicesAvailable();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("news.realm")
                .schemaVersion(0)
                .build();
        Realm.setDefaultConfiguration(config);
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
