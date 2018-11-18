package zz.zolboo;

import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;
import static zz.zolboo.StartPlanService.CHANNEL_ID;

/**
 * Created by Zolboo Erdenebaatar
 * 11/18/2018
 * This service opens the settings and then wait for the user to
 * grant the permission for Usage_Stats. It also implements a timer
 * that checks if the app has Usage_Stats permission. Once the user
 * grants the permission, the user is automatically taken back to
 * the current app.
 */
public class OpenSettingsService extends Service {
    CountDownTimer checkIfPermissionGranted;
    private int offTimeNotificationID= 1;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        Intent intentToStart= new Intent(getApplicationContext(), absolutelyNeed.class);
        /**
         Here's where you build the notification telling the user that we're opening the
         settings so that they can grant their permissions
         */
        intentToStart.putExtra(timePicker.UNIQUE_ID, "OpenSettingsService");
        intentToStart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent= PendingIntent.getActivity(getApplicationContext(), 0, intentToStart, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.unplugged_thumbnail)
                .setContentTitle("Habit Free")
                .setContentText("Opening settings to grant permissions")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManager= NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(offTimeNotificationID, mBuilder.build());
        checkIfPermissionGranted= new CountDownTimer(200000, 1000) {
            @Override
            /**
             * Here, the app checks whether the user granted the permission every 1 second and when
             * and IF it is, you app opens up automatically.
             */
            public void onTick(long l) {
                AppOpsManager appOps = (AppOpsManager) getApplicationContext().getSystemService(Context.APP_OPS_SERVICE);
                int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getApplicationContext().getPackageName());
                if(mode == MODE_ALLOWED) {
                    Intent intentToOpenAppAgain= new Intent(getApplicationContext(), absolutelyNeed.class);
                    intentToOpenAppAgain.putExtra(timePicker.UNIQUE_ID, "OpenSettingsService");
                    intentToOpenAppAgain.setAction(Intent.ACTION_VIEW);
                    intentToOpenAppAgain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intentToOpenAppAgain);
                }
            }
            @Override
            public void onFinish() {
                this.cancel();
            }
        }.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        checkIfPermissionGranted.cancel();
        NotificationManagerCompat notificationManager= NotificationManagerCompat.from(getApplicationContext());
        notificationManager.cancel(offTimeNotificationID);
        super.onDestroy();
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
