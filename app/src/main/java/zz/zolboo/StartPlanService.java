package zz.zolboo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.ArrayList;

/** Created by Zolboo Erdenebaatar on 07/11/2018
 *  This Class creates and manages a new service. This new service initiates a
 *  background activity to read and print the current usage status of the android user
 *  which the system tracks through the Android System
 */
public class StartPlanService extends Service {
    public static final String CHANNEL_ID= "LMAO";
    public static final int offTimeNotificationID= 1024;
    public static long timeLeftInMillis;
    public static int START_TIME;
    public PackageManager packageManager;
    private static ArrayList<String> extraAppList;
    private int mInterval = 5000;
    private CountDownTimer serviceStartTimer;
    /**
     * onBind method is null because there is no activity to bind to, yet
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * This method allows the service to process, "capture" and report
     * which applications are open on the user's Android system every second.
     * The CountDownTimer is set to track the app usage stats for a set amount of time
     * every 1000 milliseconds.
     * It returns START_STICKY file because this method is linked to the Button intent method
     * in the Main Activity. Therefore, it doesn't need to return anything else.
     * @return START_STICKY
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(getApplicationContext()!=null){
            packageManager= getApplicationContext().getPackageManager();
        }
        /** the method executes the code down below every tick (1 seconds)*/
        START_TIME= intent.getIntExtra("extra", 0);
        extraAppList= intent.getStringArrayListExtra("extraAppNames");
        serviceStartTimer= new CountDownTimer(START_TIME, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                /** This method checks the status of the device (how many and what, if any,
                 * applications are opened on the device) and the value is returned true if any
                 * of the application is opened. If it is opened, it creates an image view, and then
                 * WindowManager that has a set parameter to basically take over the entire screen
                 * and then the WindowManager sets the imageview to be displayed on it. Then, the
                 * imageview is set to a picture saying to stow using the device and they are
                 * displayed.*/
                /** CORRECTION: Initially, I was planning to use imageView launched by WindowManager
                 * but that was problematic because once the imageView was created, the removeView method
                 * was not working. Now the application basically instantly launches another
                 * activity from the service to our main application.*/
                Plan_UsageStats stats1= new Plan_UsageStats();
                String cancelSign= stats1.printUsageStats(getApplicationContext(), extraAppList, packageManager);
                if(cancelSign!=null) {
                    Intent displayIntent= new Intent(getApplicationContext(), absolutelyNeed.class);
                    displayIntent.setAction(Intent.ACTION_VIEW); //action view means that the activity
                    // is prioritized to open first
                    displayIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    displayIntent.putExtra(timePicker.UNIQUE_ID, "StartPlanService");
                    int millisUntilFinishedInt= (int) millisUntilFinished;
                    displayIntent.putExtra("timeLeft", millisUntilFinishedInt);
                    getApplicationContext().startActivity(displayIntent);
                }
            }

            @Override
            public void onFinish() {
                try{
                    Intent intent = new Intent(getApplicationContext(), SureToStop.class);
                    int time = (int) (START_TIME);
                    time = time / 1000;
                    intent.putExtra("time left", time);
                    startActivity(intent);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
        startForeground(offTimeNotificationID, createForegroundNotif());
        return START_STICKY; /** This object means that the method is linked to the Intent on Main activity.*/
    }
    /** This method is linked to the stopPlanUsage() method in the appsBlocked_message.java class */
    @Override
    public void onDestroy() {
        serviceStartTimer.cancel();
        NotificationManagerCompat notificationManager= NotificationManagerCompat.from(getApplicationContext());
        notificationManager.cancel(offTimeNotificationID);
        stopForeground(true);
        super.onDestroy();
    }

    private Notification createForegroundNotif() {
        createNotificationChannel();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setOngoing(true)
                .setSmallIcon(R.drawable.unplugged_thumbnail)
                .setContentTitle("Habit Free")
                .setContentText("All your distracting applications are blocked")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        return (mBuilder.build());
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
