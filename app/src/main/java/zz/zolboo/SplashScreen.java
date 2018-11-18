package zz.zolboo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {
    public static long totalTimeSaved;
    public final static String tity= "tity";
    public final static String timeSaved= "totalTimeSaved";
    private static int SPLASH_DISPLAY_LENGTH= 2000;
    private TextView timeSavedText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        timeSavedText= (TextView) findViewById(R.id.timeSaved);
        if (getSharedPreferences(timeSaved, MODE_PRIVATE)==null) {
            totalTimeSaved= 0;
            SharedPreferences.Editor editor= getSharedPreferences(timeSaved, MODE_PRIVATE).edit();
            editor.putLong(tity, totalTimeSaved);
            editor.apply();
        }
        else {
            totalTimeSaved= getSharedPreferences(timeSaved, MODE_PRIVATE).getLong(tity, 0);
        }
        timeSavedText.setText(formatHHMMSS((int)totalTimeSaved));
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashScreen.this,timePicker.class);
                SplashScreen.this.startActivity(mainIntent);
                SplashScreen.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
    public String formatHHMMSS(int secondsCount){
        String toRet= "";
        int seconds = secondsCount %60;
        secondsCount -= seconds;
        long minutesCount = secondsCount / 60;
        long minutes = minutesCount % 60;
        minutesCount -= minutes;
        long hoursCount = minutesCount / 60;
        if(secondsCount>3600) {
            toRet = "" + hoursCount+ " hours, " + minutes+ " minutes and " + seconds+ " seconds.";
        }
        else if(secondsCount>=60 && secondsCount<3600 ){
            toRet= "" + minutes+ " minutes and " + seconds+ " seconds.";
        }
        else {
            toRet= "" + seconds+ " seconds.";
        }
        return toRet;
    }
}
