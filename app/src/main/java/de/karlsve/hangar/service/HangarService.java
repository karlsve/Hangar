package de.karlsve.hangar.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import de.karlsve.hangar.R;
import de.karlsve.hangar.app.App;
import de.karlsve.hangar.app.AppList;

;

public class HangarService extends Service {
    private static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;
    private static final long DELAY = 1;

    private static final int APP_LIMIT = 7;

    private Notification public_notification = null;

    private AppList appList = null;

    public interface HangarServiceListener {
        public void onUpdate(AppList appList);
    }

    private List<HangarServiceListener> listener = new ArrayList<HangarServiceListener>();

    public void addListener(HangarServiceListener listener) {
        this.listener.add(listener);
    }

    public void removeListener(HangarServiceListener listener) {
        this.listener.remove(listener);
    }

    public class HangarServiceBinder extends Binder {
        public HangarService getService() {
            return HangarService.this;
        }
    }

    private final IBinder BINDER = new HangarServiceBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return this.BINDER;
    }

    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);

    @Override
    public void onCreate() {
        this.updateNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.initBackground();
        return START_STICKY;
    }

    private Notification getPublicNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getApplicationContext());
        builder.setSmallIcon(R.drawable.ic_logo);
        builder.setContentTitle(this.getResources().getString(R.string.app_name));
        builder.setContentText(this.getResources().getString(R.string.notification_public_text));
        builder.setPriority(NotificationCompat.PRIORITY_MIN);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        return builder.build();
    }

    private void updateNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getApplicationContext());
        builder.setSmallIcon(R.drawable.ic_logo);
        if(this.getAppList() == null) {
            builder.setContentTitle(this.getResources().getString(R.string.app_name));
            builder.setContentText(this.getResources().getString(R.string.notification_loading));
        } else if(this.getAppList().size() <= 0) {
            builder.setContentTitle(this.getResources().getString(R.string.app_name));
            builder.setContentText(this.getResources().getString(R.string.notification_loading));
        } else {
            builder.setContent(this.getRemoteViews());
        }
        builder.setOngoing(true);
        builder.setPriority(NotificationCompat.PRIORITY_MIN);
        builder.setVisibility(NotificationCompat.VISIBILITY_PRIVATE);
        builder.setPublicVersion(this.getPublicNotification());
        this.startForeground(4224, builder.build());
    }

    private RemoteViews getRemoteViews() {
        RemoteViews remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.notification_layout);
        remoteViews.removeAllViews(R.id.hangar_notification_container);
        for(App app : this.appList.getTopApps(HangarService.APP_LIMIT)) {
            remoteViews.addView(R.id.hangar_notification_container, app.getNotificationView(this));
        }
        return remoteViews;
    }

    private void initBackground() {
        this.scheduledThreadPoolExecutor.scheduleAtFixedRate(this.updater, 0L, HangarService.DELAY, HangarService.TIME_UNIT);
    }

    protected final Runnable updater = new Runnable() {
        public void run() {
            HangarService.this.update();
        }
    };

    private synchronized void update() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(HangarService.this.appList != null) {
                    HangarService.this.appList.clear();
                    HangarService.this.appList = null;
                }
                HangarService.this.appList = new AppList(HangarService.this.getApplicationContext());
                HangarService.this.appList.sort(AppList.USAGE_COMPARATOR_INVERTED);
                HangarService.this.updateNotification();
            }
        };
        new Thread(runnable).start();
    }

    public AppList getAppList() {
        return this.appList;
    }

}
