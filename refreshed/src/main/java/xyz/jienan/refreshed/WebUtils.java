package xyz.jienan.refreshed;

import android.content.Context;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;

import xyz.jienan.refreshed.base.AnalyticsManager;

import static xyz.jienan.refreshed.base.Const.EVENT_NEWS_CLICKED;

/**
 * Created by jienanzhang on 13/01/2018.
 */

public class WebUtils {

    public static void openLink(Context context, String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(context.getResources().getColor(R.color.colorPrimary));
        CustomTabsIntent customTabsIntent = builder.setShowTitle(true).addDefaultShareMenuItem().build();
        customTabsIntent.launchUrl(context, Uri.parse(url));
        AnalyticsManager.getInstance().logEvent(EVENT_NEWS_CLICKED);
    }
}
