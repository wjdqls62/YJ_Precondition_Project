package prc.yjsys.com.reliabilitypreconditionforart.Fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
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
public class Information_Fragment extends Fragment{

    TextView dModelName, dIMEI, dPhoneNumber, dOperater = null;
    Context context = null;
    DeviceManager DM = null;

    public static Fragment newInstance(){
        return new Information_Fragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_info, container, false);

        DM = new DeviceManager(getActivity());
        dModelName = (TextView) rootView.findViewById(R.id.des_model);
        dIMEI = (TextView) rootView.findViewById(R.id.des_imei);
        dPhoneNumber = (TextView) rootView.findViewById(R.id.des_phonenumber);
        dOperater = (TextView) rootView.findViewById(R.id.des_operator);

        Init_DevInfo();

        return rootView;
    }

    private void Init_DevInfo(){
        dModelName.setText(getDeviceModel());
        dIMEI.setText(getIMEI());
        dPhoneNumber.setText(getPhoneNumber());
        dOperater.setText(DM.getOperator());
    }

    private String getDeviceModel(){
        return Build.MODEL;
    }

    private String getIMEI(){
        return DM.getIMEI();
    }

    private String getPhoneNumber(){
        return DM.getPhoneNumber();
    }


}


