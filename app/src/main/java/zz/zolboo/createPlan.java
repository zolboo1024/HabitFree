package zz.zolboo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class createPlan extends AppCompatActivity {
    Button blockButton;
    Button limitButton;
    Button planButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_create_plan_slide_1);
            blockButton = (Button) findViewById(R.id.blockButton);
            limitButton = (Button) findViewById(R.id.limitButton);
            planButton = (Button) findViewById(R.id.planButton);
            blockButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), timePicker.class);
                    startActivity(intent);
                }
            });

        }
    }
}

