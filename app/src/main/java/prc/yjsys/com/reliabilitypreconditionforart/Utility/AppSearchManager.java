package prc.yjsys.com.reliabilitypreconditionforart.Utility;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import jxl.Cell;
import jxl.Range;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.read.biff.PasswordException;

/**
 * Created by jeongbin.son on 2017-02-18.
 */
public class AppSearchManager {
    private File tempFile = null;
    private FileInputStream fis = null;
    private Workbook workbook = null;


    ApplicationInfo appInfo = null;
    Context context = null;
    PackageManager pm = null;
    PackageInfo pi = null;
    boolean isInstalled = false;
    String appId = null;
    Drawable thumbnail = null;

    String TAG = "AppManager";

    public AppSearchManager(Context context){
        this.context = context;
        pm = context.getPackageManager();
    }

    public boolean isInstalled(String appName, String packageName){
        try {
            pi = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA);
            appInfo = pi.applicationInfo;
            appId = appInfo.loadLabel(pm).toString();
            Log.d(TAG,"appID : "+appId);

            if(appId.equals(appName)){
                Log.d(TAG, "App Installed. Same AppID");
                thumbnail = appInfo.loadIcon(pm);
                return true;
            }else{
                thumbnail = null;
                Log.d(TAG, "App Installed. Not Same AppID");
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            thumbnail = null;
            Log.d(TAG, "Not installed app");
            return false;
        }

        return isInstalled;
    }

    public Drawable getIcon(){
        return thumbnail;
    }

    }





