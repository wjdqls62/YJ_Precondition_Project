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

import prc.yjsys.com.reliabilitypreconditionforart.R;
import prc.yjsys.com.reliabilitypreconditionforart.Utility.DeviceManager;

/**
 * Created by jeongbin.son on 2017-01-19.
 */
public class Information_Fragment extends PreferenceFragment{

    Preference dModelName, dIMEI, dPhoneNumber, dOperater = null;
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
    }
}


