package prc.yjsys.com.reliabilitypreconditionforart.Utility;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by jeongbin.son on 2017-04-07.
 */
public class DeviceManager {
    private Process process = null;
    private InputStream IS = null;
    private BufferedReader BR = null;
    private TelephonyManager TM = null;

    private Context context = null;
    private String tempStr = null;

    public DeviceManager(Context context){
        TM = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

    }

    public String getSystemprop(String command){

        return System.getProperty(command);

    }

    public String getModelName(){
        return Build.MODEL;
    }

    public String getPhoneNumber(){
        return TM.getLine1Number();
    }



    public String getIMEI(){
        return TM.getDeviceId();
    }

    public String getOperator(){
        return TM.getSimOperator();
    }

    public String getDisplay(){
        return Build.DISPLAY;
    }


}
