package zz.zolboo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TimePicker;

@Widget
public class MyTimePicker extends TimePicker {
    public MyTimePicker(Context context) {
        super(context);
    }

    public MyTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}