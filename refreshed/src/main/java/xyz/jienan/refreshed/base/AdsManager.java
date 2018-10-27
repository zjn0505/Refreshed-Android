package xyz.jienan.refreshed.base;

import xyz.jienan.refreshed.BuildConfig;
import xyz.jienan.refreshed.MetaUtils;

import static xyz.jienan.refreshed.MetaUtils.ADMOB_APP_ID;

/**
 * Created by Jienan on 2018/1/23.
 */

public class AdsManager {

    private static final String TEST_ADS_APP_ID = "ca-app-pub-3940256099942544~3347511713";

    private static final String TEST_ADS_UNIT_ID = "ca-app-pub-3940256099942544/6300978111";

    public static String getAdsAppId() {
        if (BuildConfig.DEBUG) {
            return TEST_ADS_APP_ID;
        } else {
            return MetaUtils.getMeta(ADMOB_APP_ID);
        }

    }

    public static String getAdsUnitId(String key) {
        if (BuildConfig.DEBUG) {
            return TEST_ADS_UNIT_ID;
        } else {
            return MetaUtils.getMeta(key);
        }
    }
}
