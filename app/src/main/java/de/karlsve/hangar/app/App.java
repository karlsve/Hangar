package de.karlsve.hangar.app;

import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

import java.util.Random;

import de.karlsve.hangar.util.ApplicationUtilities;
import de.karlsve.hangar.util.BitmapUtilities;
import de.karlsve.hangar.R;

/**
 * Created by karlsve on 12.11.2014.
 */
public class App {

    private UsageStats usageStats;
    private ApplicationInfo applicationInfo;

    public App(Context context, UsageStats usageStats) throws Exception {
        this.usageStats = usageStats;
        this.applicationInfo = context.getPackageManager().getApplicationInfo(this.usageStats.getPackageName(), 0);
        if(!ApplicationUtilities.isLaunchableApp(context, this.applicationInfo)) {
            throw new RuntimeException("Not launchable");
        }
    }

    public UsageStats getUsageStats() {
        return this.usageStats;
    }

    public ApplicationInfo getApplicationInfo() {
        return this.applicationInfo;
    }

    public RemoteViews getNotificationView(Context context) {
        int size = Math.round(context.getResources().getDimension(android.R.dimen.notification_large_icon_height) * 0.8f);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_item_layout);
        Bitmap bitmap = BitmapUtilities.drawableToBitmap(this.getApplicationInfo().loadIcon(context.getPackageManager()));
        remoteViews.setImageViewBitmap(R.id.imageButton, BitmapUtilities.cropToSquare(bitmap, size));
        PendingIntent pending = this.getPendingIntent(context);
        remoteViews.setOnClickPendingIntent(R.id.notification_item_container, pending);
        return remoteViews;
    }

    public PendingIntent getPendingIntent(Context context) {
        // Generate random number for pendingIntent
        Random r = new Random();
        int random = r.nextInt(99 - 1 + 1) + 1;
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(this.getApplicationInfo().packageName);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        PendingIntent pending = PendingIntent.getActivity(context, random, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return pending;
    }
}
