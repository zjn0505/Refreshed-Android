package xyz.jienan.refreshed.base;

import android.app.Application;

/**
 * Created by jienanzhang on 11/01/2018.
 */

public class RefreshedApplication extends Application {

    private static RefreshedApplication mInstance;

    public static RefreshedApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
}
