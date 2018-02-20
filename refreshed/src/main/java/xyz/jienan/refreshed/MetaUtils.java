package xyz.jienan.refreshed;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import xyz.jienan.refreshed.base.RefreshedApplication;

/**
 * Created by Jienan on 2018/1/23.
 */

public class MetaUtils {

    public static final String ADMOB_APP_ID = "ADMOB_APP_ID";
    public static final String ADMOD_LIST_INSERT_ADS_UNIT_ID = "ADMOD_LIST_INSERT_ADS_UNIT_ID";
    public static final String NEWSAPI_API_KEY = "NEWSAPI_API_KEY";
    public static final String ALTER_HOST_API_KEY = "ALTER_HOST_API_KEY";


    public static String getMeta(String key) {
        String packageName = RefreshedApplication.getInstance().getPackageName();
        String result = "";
        try {
            ApplicationInfo appInfo = RefreshedApplication.getInstance().getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                result = appInfo.metaData.getString(key);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }
}
