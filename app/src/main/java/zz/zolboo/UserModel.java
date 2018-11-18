package zz.zolboo;

import android.content.pm.ApplicationInfo;

/**
 * Created by Zolboo Erdenebaatar
 */
public class UserModel {
    private boolean isSelected;
    private ApplicationInfo appInfo;
    public UserModel (boolean isSelected, ApplicationInfo appInfp) {
        this.isSelected= isSelected;
        this.appInfo= appInfp;
    }
    public boolean isSelected(){
        return  isSelected;
    }
    public void setSelected(boolean selected){
        isSelected= selected;
    }
    public ApplicationInfo getAppInfo(){
        return appInfo;
    }
    public void setAppName(ApplicationInfo appName){
        this.appInfo= appName;
    }

}
