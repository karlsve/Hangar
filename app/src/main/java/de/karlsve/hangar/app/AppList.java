package de.karlsve.hangar.app;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by karlsve on 12.11.2014.
 */
public class AppList extends ArrayList<App> {

    public static final Comparator<App> USAGE_COMPARATOR = new Comparator<App>() {
        @Override
        public int compare(App one, App two) {
            if(one.getUsageStats().getTotalTimeInForeground() > two.getUsageStats().getTotalTimeInForeground()) {
                return 1;
            } else if(one.getUsageStats().getTotalTimeInForeground() < two.getUsageStats().getTotalTimeInForeground()) {
                return -1;
            } else {
                return 0;
            }
        }
    };

    public static final Comparator<App> USAGE_COMPARATOR_INVERTED = new Comparator<App>() {
        @Override
        public int compare(App one, App two) {
            if(one.getUsageStats().getTotalTimeInForeground() > two.getUsageStats().getTotalTimeInForeground()) {
                return -1;
            } else if(one.getUsageStats().getTotalTimeInForeground() < two.getUsageStats().getTotalTimeInForeground()) {
                return 1;
            } else {
                return 0;
            }
        }
    };

    public AppList(Context context) {
        final UsageStatsManager usageStatsManager = (UsageStatsManager)context.getSystemService("usagestats");
        for(UsageStats usageStats : this.getUsageStatsList(usageStatsManager)) {
            try {
                App app = new App(context, usageStats);
                this.add(app);
            } catch(Exception e) {
            }
        }
    }

    private List<UsageStats> getUsageStatsList(UsageStatsManager usageStatsManager) {
        int year = Calendar.getInstance().get(Calendar.YEAR);

        Calendar beginCal = Calendar.getInstance();
        beginCal.set(Calendar.YEAR, year-1);

        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.YEAR, year+1);
        List<UsageStats> usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_YEARLY, beginCal.getTimeInMillis(), endCal.getTimeInMillis());
        return usageStats;
    }

    public void sort(Comparator<App> appComparator) {
        Collections.sort(this, appComparator);
    }

    public List<App> getTopApps(int appCount) {
        List<App> appList = new ArrayList<App>();
        for(int i = 0; i < appCount && i < this.size(); i++) {
            appList.add(this.get(i));
        }
        return appList;
    }

}
