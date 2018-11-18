package zz.zolboo;

import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class timePicker extends AppCompatActivity implements ComponentCallbacks2{
    public static long timeToPass;
    private TimePicker time_picker;
    private Button sendTimeButton;
    public static final String TIME_OFF_UNTIL_HOURS= "time_off_until_hours";
    public static final String TIME_OFF_UNTIL_MINUTES= "time_off_until_minutes";
    public static final String TOTAL_OFF_TIME= "total_off_time";
    public static final String UNIQUE_ID= "unique_id";
    private Intent intentToAbs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_time_picker);
            time_picker= (MyTimePicker) findViewById(R.id.timePickerYo);
            sendTimeButton= (Button) findViewById(R.id.sendTimeButton);
            sendTime();
        }
    }
    @Override
    protected void onDestroy(){
            super.onDestroy();
    }
    private void sendTime(){
        sendTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SendTimeAsync().execute("");
            }
        });
    }
    private class SendTimeAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            long minTimeOff=0;
            int currentHour= time_picker.getCurrentHour();
            int currentMinute= time_picker.getCurrentMinute();
            int currentTimeInMin= currentMinute+currentHour*60;
            Date currentTime= Calendar.getInstance().getTime();
            int currentTimeInMin2= currentTime.getMinutes()+currentTime.getHours()*60;
            if(currentTimeInMin2>currentTimeInMin){
                minTimeOff= 24*60-(currentTimeInMin2-currentTimeInMin);
            }
            else {
                minTimeOff= currentTimeInMin-currentTimeInMin2;
            }
            int timeOffInMillis= (int) minTimeOff*60*1000;
            intentToAbs= new Intent(getApplicationContext(), absolutelyNeed.class);
            intentToAbs.putExtra(TOTAL_OFF_TIME, timeOffInMillis);
            intentToAbs.putExtra(TIME_OFF_UNTIL_HOURS, currentHour);
            intentToAbs.putExtra(TIME_OFF_UNTIL_MINUTES, currentMinute);
            intentToAbs.putExtra(UNIQUE_ID, "timePicker");
            startActivity(intentToAbs);
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
