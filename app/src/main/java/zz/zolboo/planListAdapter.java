package zz.zolboo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import zz.zolboo.R;

public class planListAdapter extends BaseAdapter {

    String planNames[];
    String planTimes[];

    LayoutInflater inflater;


    public planListAdapter(Context c, String pPlanNames[], String pPlanTimes[]){

        planNames= pPlanNames;
        planTimes= pPlanTimes;
        inflater= (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return planNames.length;
    }

    @Override
    public Object getItem(int i) {
        return planNames[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View viewList= inflater.inflate(R.layout.main_plan_list_view, null);
        TextView planNameTextView= (TextView) viewList.findViewById(R.id.planNameTextView);
        TextView planTimeTextView= (TextView) viewList.findViewById(R.id.planTimeTextView);
        planNameTextView.setText(planNames[i]);
        planTimeTextView.setText(planTimes[i]);

        return viewList;
    }
}