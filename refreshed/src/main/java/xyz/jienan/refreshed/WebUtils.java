package xyz.jienan.refreshed;

import android.content.Context;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;

/**
 * Created by jienanzhang on 13/01/2018.
 */

public class WebUtils {

    public static void openLink(Context context, String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(context.getResources().getColor(R.color.colorPrimary));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(context, Uri.parse(url));
    }
}
