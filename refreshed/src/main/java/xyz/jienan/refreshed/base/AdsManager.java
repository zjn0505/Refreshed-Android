package xyz.jienan.refreshed.base;

import xyz.jienan.refreshed.BuildConfig;
import xyz.jienan.refreshed.MetaUtils;

/**
 * Created by Jienan on 2018/1/23.
 */

public class AdsManager {

    private final static String TEST_ADS_UNIT_ID = "ca-app-pub-3940256099942544/6300978111";

    public final static String getAdsUnitId(String key) {
        if (BuildConfig.DEBUG) {
            return TEST_ADS_UNIT_ID;
        } else {
            return MetaUtils.getMeta(key);
        }
    }
}
