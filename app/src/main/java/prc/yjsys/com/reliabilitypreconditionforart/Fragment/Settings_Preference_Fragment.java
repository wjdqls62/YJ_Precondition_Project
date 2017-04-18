package prc.yjsys.com.reliabilitypreconditionforart.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import prc.yjsys.com.reliabilitypreconditionforart.R;

/**
 * Created by jeongbin.son on 2017-04-11.
 */
public class Settings_Preference_Fragment extends PreferenceFragment {

    private SharedPreferences pref = null;


    public static Fragment newInstance(){
        return new Settings_Preference_Fragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.yj_precondition_settings);

    }
}
