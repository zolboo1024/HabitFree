package zz.zolboo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SureToStop extends AppCompatActivity {
    Drawable congratsPic;
    TextView congratsText;
    Button done;
    int timeOffInSeconds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent= getIntent();
        if (intent!=null) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sure_to_stop);
            timeOffInSeconds= intent.getIntExtra("time left", 0);
            congratsText= (TextView) findViewById(R.id.minutesAchieved);
            congratsText.setText(formatHHMMSS(timeOffInSeconds));
            done= (Button) findViewById(R.id.finished);
        }
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), timePicker.class);
                startActivity(intent);
            }
        });
        long toAdd= (long) getSharedPreferences(SplashScreen.timeSaved, MODE_PRIVATE).getLong(SplashScreen.tity, 0) + timeOffInSeconds;
        SharedPreferences.Editor editor= getSharedPreferences(SplashScreen.timeSaved, MODE_PRIVATE).edit();
        editor.remove(SplashScreen.tity);
        editor.putLong(SplashScreen.tity, toAdd);
        editor.apply();
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
