package zz.zolboo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentCallbacks2;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.Duration;

/**
 * Created by Zolboo Erdenebaatar
 * 08/01/2018
 * This class is the last step/ activity that displays
 * the "Start" and "Force Stop" buttons so that they
 * can block the applications. It also displays the
 * applications that the user chose to block.
 */
public class absolutelyNeed extends AppCompatActivity implements ComponentCallbacks2 {
    private PackageManager packageManager = null;
    private List<ApplicationInfo> appInfo = null;
    private Button goToList;
    private Button forceStop;
    private TextView timeOffUntil;
    private TextView timeOffUntilFirst;
    private CountDownTimer countDownTimer;
    private static long timeLeftInMillis;
    private TextView timeLeftClock;
    public static int timeOffHours;
    public static int timeOffMinutes;
    private static ArrayList<String> strArray;
    public static int START_TIME_IN_MILLIS;
    private static Intent intent;
    private LinearLayout gallery;
    private LayoutInflater inflater;
    private static Intent intentForService;
    private static Intent intentForOpenSettingsService;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private HashMap<String, Drawable> iconMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        if (intent.getStringExtra(timePicker.UNIQUE_ID).equals("timePicker")) {
            new registerTime().execute("");
        }
        setContentView(R.layout.activity_absolutely_need);
        packageManager = getPackageManager();
        timeOffUntil = (TextView) findViewById(R.id.timeOffUntilSpecific);
        timeOffUntil.setVisibility(View.INVISIBLE);
        timeOffUntilFirst = (TextView) findViewById(R.id.timeOffUntil);
        timeOffUntilFirst.setVisibility(View.INVISIBLE);
        forceStop = (Button) findViewById(R.id.forceStop);
        forceStop.setVisibility(View.INVISIBLE);
        appInfo = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        goToList = (Button) findViewById(R.id.anyApp);
        /** OPEN THE APP LIST*/
        goToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AllAppsList.class);
                startActivity(intent);
            }
        });

        /** START THE FUCKER */

        timeLeftClock = (TextView) findViewById(R.id.timeTickingClock);
        updateCountdownText(START_TIME_IN_MILLIS);
        final Button start = (Button) findViewById(R.id.startButton);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Plan_UsageStats plan1 = new Plan_UsageStats();
                if (!plan1.checkForPermission(getApplicationContext())) {
                    new AlertDialog.Builder(absolutelyNeed.this).setTitle("Permission for your phone Usage Stats needed")
                            .setMessage("This permission is needed help us track and block your phone activities. Please press Ok to go to the settings and allow the access.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    intentForOpenSettingsService= new Intent(getApplicationContext(), OpenSettingsService.class);
                                    startService(intentForOpenSettingsService);
                                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).create().show();
                } else if(strArray==null || strArray.isEmpty()){
                    Toast.makeText(getApplicationContext(), "You must select at least 1 application", Toast.LENGTH_SHORT).show();
                }
                else {
                    timeLeftInMillis = START_TIME_IN_MILLIS;
                    goToList.setVisibility(View.INVISIBLE);
                    countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                            updateCountdownText(millisUntilFinished);
                        }

                        @Override
                        public void onFinish() {
                            if (intentForService.getIntExtra("finished", 0) == 0) {
                                intentForService.putExtra("finished", 1);
                                stopService(intentForService);
                            }
                            Intent intent = new Intent(getApplicationContext(), SureToStop.class);
                            int time = (int) (START_TIME_IN_MILLIS - timeLeftInMillis);
                            time = time / 1000;
                            intent.putExtra("time left", time);
                            startActivity(intent);
                        }
                    }.start();
                    forceStop.setVisibility(View.VISIBLE);
                    goToList.setVisibility(View.INVISIBLE);
                    int timeOffHoursPm = timeOffHours;
                    if (timeOffHours > 12) {
                        timeOffHoursPm = timeOffHours - 12;
                    }
                    String textToSet = String.format("%02d:%02d", timeOffHoursPm, timeOffMinutes);
                    if (timeOffHours > 12) {
                        textToSet += " PM";
                    } else {
                        textToSet += " AM";
                    }
                    start.setVisibility(View.INVISIBLE);
                    timeOffUntil.setText(textToSet);
                    timeOffUntil.setVisibility(View.VISIBLE);
                    timeOffUntilFirst.setText("Time Off Until:");
                    timeOffUntilFirst.setVisibility(View.VISIBLE);
                    intentForService = new Intent(getApplicationContext(), StartPlanService.class);
                    intentForService.putExtra("extra", START_TIME_IN_MILLIS);
                    intentForService.putExtra("extraAppNames", strArray);
                    startService(intentForService);
                }
            }
        });

        /** FORCE STOP */

        forceStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(absolutelyNeed.this);
                builder.setMessage(R.string.giveUp)
                        .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                countDownTimer.cancel();
                                if (intentForService.getIntExtra("finished", 0) == 0) {
                                    intentForService.putExtra("finished", 1);
                                    stopService(intentForService);
                                }
                                Intent intent = new Intent(getApplicationContext(), SureToStop.class);
                                int time = (int) (START_TIME_IN_MILLIS - timeLeftInMillis);
                                time = time / 1000;
                                intent.putExtra("time left", time);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog finalDialog = builder.create();
                finalDialog.show();
            }
        });

        /** FROM APP LIST */
        if (intent.getStringExtra(timePicker.UNIQUE_ID).equals("AllAppsList")) {
            new DrawTheIcons().execute("");
        }
        else {
            sharedPreferences= getSharedPreferences(AllAppsList.filename, MODE_PRIVATE);
            if(sharedPreferences.getStringSet("unblockedApps", null)!=null) {
                strArray = new ArrayList<>(sharedPreferences.getStringSet("unblockedApps", null));
            }
            if (strArray != null && !strArray.isEmpty()) {
                new DrawTheIcons().execute("");
            }
        }
        /** FROM OPEN SETTINGS SERVICE */
        if(intent.getStringExtra(timePicker.UNIQUE_ID).equals("OpenSettingsService")) {
            stopService(intentForOpenSettingsService);
        }

            /** OPENING FROM SERVICE*/

            if(intent.getStringExtra(timePicker.UNIQUE_ID).equals("StartPlanService")){
                int timeLeft= intent.getIntExtra("timeLeft", 0);
                goToList.setVisibility(View.INVISIBLE);
                countDownTimer = new CountDownTimer(timeLeft, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        updateCountdownText(millisUntilFinished);
                    }

                    @Override
                    public void onFinish() {
                        if (intentForService.getIntExtra("finished", 0) == 0) {
                            intentForService.putExtra("finished", 1);
                            stopService(intentForService);
                        }
                        Intent intent = new Intent(getApplicationContext(), SureToStop.class);
                        int time = (int) (START_TIME_IN_MILLIS - timeLeftInMillis);
                        time = time / 1000;
                        intent.putExtra("time left", time);
                        startActivity(intent);
                    }
                }.start();
                start.setVisibility(View.INVISIBLE);
                forceStop.setVisibility(View.VISIBLE);
                goToList.setVisibility(View.INVISIBLE);
                int timeOffHoursPm = timeOffHours;
                if (timeOffHours > 12) {
                    timeOffHoursPm = timeOffHours - 12;
                }
                String textToSet = String.format("%02d:%02d", timeOffHoursPm, timeOffMinutes);
                if (timeOffHours > 12) {
                    textToSet += " PM";
                } else {
                    textToSet += " AM";
                }
                timeOffUntil.setText(textToSet);
                timeOffUntil.setVisibility(View.VISIBLE);
                timeOffUntilFirst.setText("Time Off Until:");
                timeOffUntilFirst.setVisibility(View.VISIBLE);
            }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(intentForService!=null){
            stopService(intentForService);
        }
        if(intentForOpenSettingsService!=null){
            stopService(intentForOpenSettingsService);
        }
    }

    private Drawable getIcon(String nameToSearch, List<ApplicationInfo> listToSearch, PackageManager pm) {
        Drawable drawableToReturn = null;
        if (!listToSearch.isEmpty()) {
            for (ApplicationInfo each : listToSearch) {
                if (each.loadLabel(pm).toString().equals(nameToSearch)) {
                    drawableToReturn = each.loadIcon(pm);
                }
            }
        }
        return drawableToReturn;
    }

    private void updateCountdownText(long millisUntilFinished) {
        timeLeftInMillis = millisUntilFinished;
        int tempTime = (int) timeLeftInMillis / 1000;
        int hours = (int) (tempTime) / 3600;
        tempTime = tempTime - (tempTime / 3600) * 3600;
        int minutes = (tempTime) / 60;
        int seconds = tempTime % 60;
        String timeLeftFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        timeLeftClock.setText(timeLeftFormatted);
    }

    private class DrawTheIcons extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute(){
            progressDialog= new ProgressDialog(absolutelyNeed.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgress(0);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            gallery = findViewById(R.id.gallery);
            inflater = LayoutInflater.from(getApplicationContext());
            if(intent!=null && intent.getBundleExtra("extra")!=null) {
                Bundle bundle = intent.getBundleExtra("extra");
                strArray = bundle.getStringArrayList("extra");
            }
            iconMap= new HashMap<>();
            if (strArray != null && !strArray.isEmpty()) {
                for (String each : strArray) {
                    iconMap.put(each, getIcon(each, appInfo, packageManager));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (strArray != null && !strArray.isEmpty()) {
                for (String each : strArray) {
                    View view = inflater.inflate(R.layout.icon_list_view, gallery, false);
                    ImageView imageView = view.findViewById(R.id.icon_in_list);
                    imageView.setImageDrawable(iconMap.get(each));
                    gallery.addView(view);
                }
            }
            progressDialog.cancel();
        }
    }
    private class registerTime extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            timeOffHours = intent.getIntExtra(timePicker.TIME_OFF_UNTIL_HOURS, 0);
            timeOffMinutes = intent.getIntExtra(timePicker.TIME_OFF_UNTIL_MINUTES, 0);
            START_TIME_IN_MILLIS = intent.getIntExtra(timePicker.TOTAL_OFF_TIME, 0);
            if(START_TIME_IN_MILLIS==0){
                START_TIME_IN_MILLIS= 86400000;
            }
            return null;
        }
    }
    public void onTrimMemory(int level) {
        switch (level) {

            case ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN:
                break;

            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL:
                break;
            case ComponentCallbacks2.TRIM_MEMORY_BACKGROUND:
            case ComponentCallbacks2.TRIM_MEMORY_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_COMPLETE:
                break;
            default:
                break;
        }
    }
}
