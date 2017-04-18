package prc.yjsys.com.reliabilitypreconditionforart.Fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import prc.yjsys.com.reliabilitypreconditionforart.R;
import prc.yjsys.com.reliabilitypreconditionforart.Utility.DeviceManager;

/**
 * Created by jeongbin.son on 2017-01-19.
 */
public class Information_Fragment extends PreferenceFragment{

    Process process = null;
    BufferedReader bufferedReader = null;
    String GETPROP_EXECUTABLE_PATH = "/system/bin/getprop";

    Preference dModelName, dIMEI, dPhoneNumber, dBoard, dAndroidVersion,dSWVersion, dManufacturer = null;
    DeviceManager DM = null;

    public static Fragment newInstance(){
        return new Information_Fragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.dev_info);


        DM = new DeviceManager(getActivity());

        dModelName = findPreference("devinfo_title_model");
        dModelName.setSummary(DM.getModelName());

        dIMEI = findPreference("devinfo_title_imei");
        dIMEI.setSummary(DM.getIMEI());


        dPhoneNumber = findPreference("devinfo_title_phonenumber");
        dPhoneNumber.setSummary(DM.getPhoneNumber());

        dBoard = findPreference("devinfo_title_board");
        dBoard.setSummary(readProp("ro.product.board"));

        dAndroidVersion = findPreference("devinfo_title_android_version");
        dAndroidVersion.setSummary(readProp("ro.build.version.release"));

        dSWVersion = findPreference("devinfo_title_swversion");
        dSWVersion.setSummary(readProp("ro.lge.swversion"));

        dManufacturer = findPreference("devinfo_title_manufacturer");
        dManufacturer.setSummary(readProp("ro.product.manufacturer"));
    }

    public String readProp(String propName) {
        try {
            process = new ProcessBuilder().command(GETPROP_EXECUTABLE_PATH, propName).redirectErrorStream(true).start();
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = bufferedReader.readLine();

            if (line == null) {
                line = "";
            }
            return line;
        } catch (IOException e) {
            return "";
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    return "";
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
    }




}


