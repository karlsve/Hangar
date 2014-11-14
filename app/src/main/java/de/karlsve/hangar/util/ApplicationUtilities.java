package de.karlsve.hangar.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;

/**
 * Created by karlsve on 12.11.2014.
 */
public abstract class ApplicationUtilities {

    public static boolean isLaunchableApp(Context context, ApplicationInfo applicationInfo) {
        return !((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 || context.getPackageManager().getLaunchIntentForPackage(applicationInfo.packageName) == null);
    }

}
