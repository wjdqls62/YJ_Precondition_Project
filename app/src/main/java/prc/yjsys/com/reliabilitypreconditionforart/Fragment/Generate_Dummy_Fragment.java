package prc.yjsys.com.reliabilitypreconditionforart.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import prc.yjsys.com.reliabilitypreconditionforart.R;
import prc.yjsys.com.reliabilitypreconditionforart.Utility.DummyThreadHelper;
import prc.yjsys.com.reliabilitypreconditionforart.Utility.DummyViewHelper;
import prc.yjsys.com.reliabilitypreconditionforart.Utility.Global_IsRunning_Thread;
import prc.yjsys.com.reliabilitypreconditionforart.Utility.StorageStateManager;


/**
 * Created by jeongbin.son on 2017-01-17.
 */

public class Generate_Dummy_Fragment extends Fragment
        implements View.OnClickListener {

    private final int SnackBar_Length_Long                      = 3000;
    private final int Generate_Dummy_Fragment_Padding           = 15;

    private Global_IsRunning_Thread isRun                       = null;
    private DummyThreadHelper dcm                               = null;
    private DummyViewHelper sm                                  = null;

    private LayoutInflater mInflater                            = null;
    private StorageStateManager SSM                             = null;
    private Button mGenerateDummy                               = null;
    private Context context                                     = null;
    private TextView text_available_storage_mb, text_in_use_mb  = null;

    private String TAG                                          = "Generate_Dummy_Fragment";

    public static Fragment newInstance(){
        return new Generate_Dummy_Fragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.frgm_dummy, container, false);

        sm = new DummyViewHelper(context, rootView);

        dcm = new DummyThreadHelper(getActivity(), rootView);

        // DummyFile Generate Button Initialize
        mGenerateDummy = (Button) rootView.findViewById(R.id.generate_dummy);

        mGenerateDummy.setOnClickListener(this);

        SSM = new StorageStateManager();
        FragmentManager FM = getActivity().getFragmentManager();
        FM.beginTransaction().replace(R.id.storage_state_view, SSM).commit();

        return rootView;
    }

    public void check_limit_generate(){
        isRun = Global_IsRunning_Thread.getInstance();
        if (isRun.isRun_DummyThread() == true){
            mGenerateDummy.setEnabled(false);
        }
    }

    // Back key, Home button 등 앱이 Pause 후 Resume될때 다시 용량을 계산
    @Override
    public void onStart() {
        super.onStart();
        check_limit_generate();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sm.reset_Progress_Config();
    }

    @Override
    public void onClick(View v) {
            switch(v.getId()){
                // execute Thread
                case R.id.generate_dummy :
                    if(sm.ready_dummy_file == 0 && sm.ready_dummy_contact == 0){
                        Snackbar.make(getView(), getResources().getString(R.string.toast_dummy_notinput), SnackBar_Length_Long).show();
                        //Toast.makeText(context, getResources().getString(R.string.toast_dummy_notinput), Toast.LENGTH_SHORT).show();
                        break;
                    }else{
                        dcm.exeAsyncTask(sm.ready_dummy_file, sm.ready_dummy_contact, sm.ready_dummy_contact_number);
                        Snackbar.make(getView(),getResources().getString(R.string.snackbar_start_generate),Snackbar.LENGTH_LONG).show();
                        break;
                    }
            }
        }
    }
