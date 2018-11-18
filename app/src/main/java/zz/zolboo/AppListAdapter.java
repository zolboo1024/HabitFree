package zz.zolboo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Zolboo Erdenebaatar
 */
public class AppListAdapter extends ArrayAdapter<UserModel> {
    private List<UserModel> appList = null;
    private Context context;
    private PackageManager packageManager;

    public AppListAdapter(@NonNull Context context, int resource, @NonNull List<UserModel> objects) {
        super(context, resource, objects);
        this.context = context;
        this.appList = objects;
        packageManager = context.getPackageManager();
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public UserModel getItem(int position) {
        return (appList != null) ? appList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if (null == view) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.applist_inflater, null);
        }

        ApplicationInfo data = appList.get(position).getAppInfo();

        if (data != null){
            holder= new ViewHolder();
            holder.appName = (TextView) view.findViewById(R.id.app_name);
            holder.appIcon = (ImageView) view.findViewById(R.id.app_icon);
            holder.appName.setText(data.loadLabel(packageManager));
            holder.appIcon.setImageDrawable(data.loadIcon(packageManager));
            holder.ivCheckBox= (ImageView) view.findViewById(R.id.icon);
            UserModel userModel = appList.get(position);
            if (userModel.isSelected()) {
                holder.ivCheckBox.setBackgroundResource(R.drawable.optimized_checked);
            } else holder.ivCheckBox.setBackgroundResource(R.drawable.optimized_unchecked);
        }
        return view;
    }

    public void updateRecords(List<UserModel> users) {
        this.appList= users;
        notifyDataSetChanged();
    }
    class ViewHolder {
        TextView appName;
        ImageView appIcon;
        ImageView ivCheckBox;
    }
}

