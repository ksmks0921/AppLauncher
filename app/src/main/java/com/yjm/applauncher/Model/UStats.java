package com.yjm.applauncher.Model;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class UStats {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy HH:mm:ss");
    public static final String TAG = UStats.class.getSimpleName();
    public String className;
    @SuppressWarnings("ResourceType")
    public static String getStats(Context context) {
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
        int interval = UsageStatsManager.INTERVAL_YEARLY;
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        String value = "null";

        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();
        Log.d(TAG, "Range start:" + dateFormat.format(startTime));
        Log.d(TAG, "Range end:" + dateFormat.format(endTime));
        UsageEvents uEvents = usm.queryEvents(startTime, endTime);
        while (uEvents.hasNextEvent()) {
            UsageEvents.Event e = new UsageEvents.Event();
            uEvents.getNextEvent(e);
            if (e != null && e.getClassName() != null) {

                    if(e.getClassName().equals("com.android.systemui.recents.RecentsActivity")){
                        value = e.getClassName();
                    }


                Log.d(TAG, "Event______________: " + e.getPackageName() + "\t" + e.getTimeStamp() + "\t" + e.getClassName());
            }
        }
        return value;
    }

    public static List<UsageStats> getUsageStatsList(Context context) {
        UsageStatsManager usm = getUsageStatsManager(context);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();
        Log.d(TAG, "Range start:" + dateFormat.format(startTime));
        Log.d(TAG, "Range end:" + dateFormat.format(endTime));
        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
        return usageStatsList;
    }
    public void printUsageStats(List<UsageStats> usageStatsList) {
        String value = null;
        for (UsageStats u : usageStatsList) {
            Log.d(TAG, "Pkg: " + u.getPackageName() + "\t" + "ForegroundTime: "
                    + u.getTotalTimeInForeground());
            value = "Pkg: " + u.getPackageName() + "\t" + "ForegroundTime: " + u.getTotalTimeInForeground();
        }
    }
    public void printCurrentUsageStatus(Context context) {
        printUsageStats(getUsageStatsList(context));
    }
    public static String printUsageStatus(Context context) {
        return printUsageStatss(getUsageStatsList(context));
    }
    @SuppressWarnings("ResourceType")
    private static UsageStatsManager getUsageStatsManager(Context context) {
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
        return usm;
    }
    public static String printUsageStatss(List<UsageStats> usageStatsList) {
        String value = null;
        for (UsageStats u : usageStatsList) {
            Log.d(TAG, "Pkg____)))): " + u.getPackageName() + "\t" + "ForegroundTime: "
                    + u.getTotalTimeInForeground());

            if(u.getPackageName().equals("com.android.systemui")){
                value =  u.getPackageName();
//                value = u.getClass().getCanonicalName();



            }

        }

        return value;
    }

}
