package zz.zolboo;

import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;

/** Created by Zolboo Erdenebaatar on 07/11/2018
 * This class is designed to, in general, track the usage and foreground time of each
 * of the applications (or packages) running on the user's device. Then, it has classes
 * like printUsageStats() that also checks if the user opens any application in the last
 * 2 seconds of them using their application
 */
public class Plan_UsageStats{
    public Plan_UsageStats(){}
    /** This method fetches the UsageStats manager and
     * runs through the queryEvents method and gets the list of applications currently
     * opened has worked for the last 2 seconds
     */
    public UsageEvents getUsageEvents(Context context){
        UsageStatsManager usm = getUsageStatsManager(context);
        if(usm!=null) {
            return usm.queryEvents(System.currentTimeMillis() - 5000, System.currentTimeMillis());
        }
        else {
            return null;
        }
    }
    /** This method runs through the queryEvents method and gets the list of applications currently
     * opened/ has worked for the last 2 seconds and then for each of the app or package that has
     * been used. Then, it gets the time stamp, checks if its zero, then gets the package name
     * and searches the name in the "Unblocked list" through the searchByName() method. If
     * the package name is not found and the timestamp is not zero (which means its not null), then
     * it sends the sign true which signals the startplanservice class to display the message
     * that launches another activity. The way how queryEvents work is that it basically contains
     * all the events on the phone, but if you call the method Event, then it only points to the
     * first event that happened during the time limit (-1 to 0). Then, if you say queryEvents.getNextEvent(currentEvent);
     * it then points to the next event after the "currentEvent".
     * Returns String- indicates if any of the app has been opened. If so, it returns the name of the app.
     */
    public String printUsageStats(Context context,
                                  ArrayList<String> extraAppList, PackageManager packageManager){
        String namePackage=null;
        UsageEvents queryEvents = getUsageEvents(context);
        if (queryEvents != null) {
            UsageEvents.Event eventAux = new UsageEvents.Event();
            while (queryEvents.hasNextEvent()) {
                eventAux= new UsageEvents.Event();
                String thisName= eventAux.getPackageName();
                long thisTimeStamp= eventAux.getTimeStamp();
                if(thisTimeStamp!=0 && !thisName.equals("zz.zolboo")
                        && findIfInTheBlockedList(thisName, extraAppList, packageManager)) {
                    namePackage=thisName;
                }
                queryEvents.getNextEvent(eventAux);
            }
            String thisName= eventAux.getPackageName();
            long thisTimeStamp= eventAux.getTimeStamp();
            if(thisTimeStamp!=0 && !thisName.equals("zz.zolboo")
                    && findIfInTheBlockedList(thisName, extraAppList, packageManager)) {
                namePackage=thisName;
            }
        }
        return namePackage;
    }
    /**
     * This method returns the usageStatsManager for the android device
     * @return UsageStatsManager
     */
    private UsageStatsManager getUsageStatsManager(Context context){
        return (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
    }
    private boolean findIfInTheBlockedList(String packageName, ArrayList<String> stringArr, PackageManager packageManager){
        boolean toReturn= false;
        stringArr= labelsToPackageNames(stringArr, packageManager);
        if(stringArr!=null&& !stringArr.isEmpty()){
            for(String each: stringArr){
                if(each.equals(packageName) && !each.equals("zz.zolboo")){
                    toReturn= true;
                }
            }
        }
        return toReturn;
    }
    private ArrayList<String> labelsToPackageNames(ArrayList<String> labels, PackageManager packageManager){
        ArrayList<String> arrToRet= new ArrayList<>();
        List<ApplicationInfo> appInfo= packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        if(appInfo!=null && labels!=null) {
            for (ApplicationInfo each : appInfo) {
                for (String eachString : labels) {
                    if (eachString.equals(each.loadLabel(packageManager).toString()) &&
                            !eachString.equals("Habit Free")) {
                        arrToRet.add(each.packageName);
                    }
                }
            }
        }
        return arrToRet;
    }
    public boolean checkForPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        return mode == MODE_ALLOWED;
    }
}
