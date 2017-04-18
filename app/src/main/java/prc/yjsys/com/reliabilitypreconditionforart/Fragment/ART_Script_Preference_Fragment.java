package prc.yjsys.com.reliabilitypreconditionforart.Fragment;

        import android.content.Context;
        import android.preference.PreferenceFragment;
        import android.os.Bundle;

        import android.app.Fragment;

        import prc.yjsys.com.reliabilitypreconditionforart.Utility.PreferenceXMLCopyManager;
        import prc.yjsys.com.reliabilitypreconditionforart.R;

public class ART_Script_Preference_Fragment extends PreferenceFragment {

    // Init PreferenceXMLCopyManager
    Context context = null;
    PreferenceXMLCopyManager fc = null;

    // init Debugging Log TAG
    String TAG = "Reliability";

    public static Fragment newInstance(){
        return new ART_Script_Preference_Fragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();
        fc = new PreferenceXMLCopyManager(context, getView());

        addPreferencesFromResource(R.xml.art_script_partial_play);
    }
    @Override
    public void onPause () {
        fc.fileCopy();
        super.onPause();
    }
}