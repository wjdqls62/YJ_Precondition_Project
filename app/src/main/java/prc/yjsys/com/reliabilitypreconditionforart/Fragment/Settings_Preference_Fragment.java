package prc.yjsys.com.reliabilitypreconditionforart.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import prc.yjsys.com.reliabilitypreconditionforart.R;

/**
 * Created by jeongbin.son on 2017-04-11.
 */
public class Settings_Preference_Fragment extends PreferenceFragment {

    private SharedPreferences pref = null;
    private SwitchPreference mAutofill_Random_Number = null;
    private SharedPreferences.Editor editor = null;

    public static Fragment newInstance(){
        return new Settings_Preference_Fragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.yj_precondition_settings);
        mAutofill_Random_Number = (SwitchPreference) findPreference("autofill_dummy_rnadom_number");

        pref = getActivity().getSharedPreferences("settings",Context.MODE_PRIVATE);
        editor = pref.edit();

        mAutofill_Random_Number.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if(((SwitchPreference)preference).isChecked()){
                    editor.putBoolean(((SwitchPreference)preference).getKey(), false).commit();
                    mAutofill_Random_Number.setChecked(false);
                }else{
                    editor.putBoolean(((SwitchPreference)preference).getKey(), true).commit();
                    mAutofill_Random_Number.setChecked(true);
                }
                return false;
            }
        });

    }
}
