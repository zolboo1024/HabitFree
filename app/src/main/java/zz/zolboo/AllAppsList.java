package zz.zolboo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AllAppsList extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private PackageManager packageManager= null;
    private List<ApplicationInfo> appInfo= null;
    private List<UserModel> userModelList= null;
    private AppListAdapter listAdapter= null;
    ListView allAppsList;
    Button done;
    int size;
    public static final String filename= "Selected_Applications";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_apps_list);
        new runnableGetAppsToDisplay().execute("");
    }

    private List<ApplicationInfo> updateList(List<ApplicationInfo> listToUpdate, PackageManager packageManager){
        List<ApplicationInfo> toReturn= new ArrayList<ApplicationInfo>();
        if(listToUpdate!=null){
            for(ApplicationInfo each: listToUpdate){
                if(isSystemPackage(each, packageManager)) {}
                else {
                    if(each!=null){
                        toReturn.add(each);
                    }
                }
            }
        }
        return toReturn;
    }
    private boolean isSystemPackage(ApplicationInfo applicationInfo, PackageManager packageManager) {
        boolean toReturn= true;
        if(packageManager.getLaunchIntentForPackage(applicationInfo.packageName)!=null){
            if(!applicationInfo.packageName.equals("zz.zolboo")) {
                toReturn = false;
            }
        }
        return toReturn;
    }
    private List<ApplicationInfo> findTheInfoChosen(List<UserModel> initInfo){
        List<ApplicationInfo> listToReturn= new ArrayList<>();
        if(initInfo!=null){
            for(UserModel each: initInfo){
                if(each.isSelected()){
                    listToReturn.add(each.getAppInfo());
                }
            }
        }
        return listToReturn;
    }
    private class doneIntent extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String name= "extra";
            ArrayList<String> arrToReturn= new ArrayList<>();
            Bundle bundleToReturn= new Bundle();
            Intent intent= new Intent(getApplicationContext(), absolutelyNeed.class);
            List<ApplicationInfo> listToDisplay= new ArrayList<>();
            if(findTheInfoChosen(userModelList)!=null) {
                listToDisplay = findTheInfoChosen(userModelList);
                for (ApplicationInfo each: listToDisplay){
                    arrToReturn.add(each.loadLabel(packageManager).toString());
                }
                bundleToReturn.putStringArrayList(name, arrToReturn);
                intent= new Intent(getApplicationContext(), absolutelyNeed.class);
                intent.putExtra("extra", bundleToReturn);
            }
            Set<String> appSet= new HashSet<String>(arrToReturn);
            SharedPreferences.Editor editor = getSharedPreferences(filename, MODE_PRIVATE).edit();
            if(getSharedPreferences(filename, MODE_PRIVATE).getStringSet("unblockedApps", null)!=null){
                editor.remove(filename);
            }
            editor.putStringSet("unblockedApps", appSet);
            editor.apply();
            intent.putExtra(timePicker.UNIQUE_ID, "AllAppsList");
            startActivity(intent);
            return null;
        }
    }

    private class runnableGetAppsToDisplay extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(AllAppsList.this);
            progressDialog.setCancelable(true);
            progressDialog.setMessage("Loading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgress(0);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            packageManager= getPackageManager();
            appInfo= packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            appInfo= updateList(appInfo, packageManager);
            userModelList= new ArrayList<UserModel>();
            ArrayList<String> selectedApps= new ArrayList<>();
            SharedPreferences sharedPreferences= getSharedPreferences(filename, MODE_PRIVATE);
            if(sharedPreferences.getStringSet("unblockedApps", null)!=null) {
                selectedApps = new ArrayList<>(sharedPreferences.getStringSet("unblockedApps", null));
            }
            if(!selectedApps.isEmpty()){
                for(ApplicationInfo each: appInfo) {
                    boolean found= false;
                    String label= each.loadLabel(packageManager).toString();
                    for(String eachString: selectedApps) {
                        if (eachString.equals(label)) {
                            found=true;
                        }
                    }
                    if(found) {
                        userModelList.add(new UserModel(true, each));
                    }
                    else if(!found){
                        userModelList.add(new UserModel(false, each));
                    }
                }
            }
            else if(appInfo!=null){
                for(ApplicationInfo each: appInfo){
                    userModelList.add(new UserModel(false, each));
                }
            }
            size= appInfo.size();
            listAdapter= new AppListAdapter(getApplicationContext(), size, userModelList);
            return null;
        }
        @Override
        protected void onPostExecute(String result){
            allAppsList= (ListView) findViewById(R.id.allAppsList);
            allAppsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                    /** when this line is called, the adapter picks up which row is selected,
                     * then it sets the isSelected method to true if it was false and false if it
                     * was true. Then it updates the userModelList in the AppListAdapter class
                     * correspondant to the model list*/
                    UserModel model= userModelList.get(pos);
                    if(model.isSelected()) {
                        model.setSelected(false);
                    }
                    else model.setSelected(true);
                    userModelList.set(pos, model);
                    listAdapter.updateRecords(userModelList);
                }
            });
            allAppsList.setAdapter(listAdapter);
            done= (Button) findViewById(R.id.done);
            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new doneIntent().execute();
                }
            });
            progressDialog.dismiss();
        }
    }
}
