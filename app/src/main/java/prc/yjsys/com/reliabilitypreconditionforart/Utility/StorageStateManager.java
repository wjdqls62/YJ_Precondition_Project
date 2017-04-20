package prc.yjsys.com.reliabilitypreconditionforart.Utility;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

import prc.yjsys.com.reliabilitypreconditionforart.R;

import static android.content.ContentValues.TAG;

/**
 * Created by jeongbin.son on 2017-02-23.
 */
public class StorageStateManager extends Fragment {

    private final String FLAG_AUTO      = "AUTO";
    private final String FLAG_BYTE     = "BYTE";
    private final String FLAG_KB       = "KB";
    private final String FLAG_MB       = "MB";
    private final String FLAG_GB       = "GB";

    private StatFs stat                = null;
    private File path                  = null;
    private File tempPath              = null;

    private long availableStorageBytes = 0;
    private long usingStorageBytes     = 0;
    private long totalStorageBytes     = 0;
    private int  temp = 0;

    private String internalAutofillPath =null;

    TextView mStorageType = null;
    TextView mStorageState = null;
    ProgressBar mUsageStorageProgress = null;

    public StorageStateManager(){

        path = Environment.getDataDirectory();
        stat = new StatFs(path.getPath());
    }


    @Override
    public void onStart() {
        refrestStorageStateView();

        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.common_storage_state_view, container, false);

        internalAutofillPath = getActivity().getResources().getString(R.string.dummy_save_path);
        mStorageType = (TextView) rootView.findViewById(R.id.storage_type);
        mStorageState = (TextView) rootView.findViewById(R.id.storage_usage);
        mUsageStorageProgress = (ProgressBar) rootView.findViewById(R.id.storage_usage_guage);

        mStorageType.setText(R.string.text_storage_type);
        refrestStorageStateView();

        return rootView;
    }

    public void refrestStorageStateView(){
        AutoChageStorageUnit usageObj = new AutoChageStorageUnit();
        AutoChageStorageUnit totalObj = new AutoChageStorageUnit();
        refreshCurrentStorage();

        usageObj.chageDataUnit(usingStorageBytes);
        totalObj.chageDataUnit(totalStorageBytes);
        Log.d("TAG", usingStorageBytes+"");

        mStorageState.setText( usageObj.dataQuantity+usageObj.dataUnit + " / " + totalObj.dataQuantity + totalObj.dataUnit);
        mUsageStorageProgress.setMax(Integer.parseInt(Long.toString(getTotalStorage(FLAG_GB))));
        mUsageStorageProgress.setProgress(Integer.parseInt(Long.toString(getUsingStorage(FLAG_GB,"TOTAL"))));
    }


    // Description : 함수 호출시점마다의 단말용량 현황 생성자를 업데이트
    public void refreshCurrentStorage(){
        path = Environment.getDataDirectory();
        stat = new StatFs(path.getPath());

        availableStorageBytes = getAvailableStorage(FLAG_BYTE);
        usingStorageBytes = getUsingStorage(FLAG_BYTE, "TOTAL");
        totalStorageBytes = getTotalStorage(FLAG_BYTE);
    }

    // Description : 현재사용가능한 메모리 용량 확인
    public long getAvailableStorage(String unitType){
        if(unitType.equals(FLAG_BYTE)){
            availableStorageBytes = stat.getFreeBlocksLong() * stat.getBlockSizeLong();
        }else if(unitType.equals(FLAG_KB)){
            availableStorageBytes = stat.getFreeBlocksLong() * stat.getBlockSizeLong() / 1024;
        }else if(unitType.equals(FLAG_MB)){
            availableStorageBytes = stat.getFreeBlocksLong() * stat.getBlockSizeLong() / 1024 / 1024;
        }else if(unitType.equals(FLAG_GB)){
            availableStorageBytes = stat.getFreeBlocksLong() * stat.getBlockSizeLong() / 1024 / 1024 / 1024;
        }
        return availableStorageBytes;
    }


    // Description : 현재 사용중 메모리용량을 확인
    public long getUsingStorage(String unitType, String Path){
        // AutoFill 경로의 사용중 사용중 메모리 Return
        if(Path.equals("internalAutofillPath")) {
            tempPath = new File(internalAutofillPath);
            if(unitType.equals(FLAG_BYTE)){
                return usingStorageBytes;
            }else if (unitType.equals(FLAG_KB)) {
                usingStorageBytes = getFolderSize(new File(internalAutofillPath)) / 1024;
            } else if (unitType.equals(FLAG_MB)) {
                usingStorageBytes = getFolderSize(new File(internalAutofillPath)) / 1024 / 1024;
            } else if (unitType.equals(FLAG_GB)) {
                usingStorageBytes = getFolderSize(new File(internalAutofillPath)) / 1024 / 1024 / 1024;
            }
        }
        // 전체 사용중 메모리 Return
        else{
            if(unitType.equals(FLAG_BYTE)){
                usingStorageBytes = getTotalStorage(FLAG_BYTE) - getAvailableStorage(FLAG_BYTE);
            }else if(unitType.equals(FLAG_KB)){
                usingStorageBytes = getTotalStorage(FLAG_KB) - getAvailableStorage(FLAG_KB);
            }else if(unitType.equals(FLAG_MB)){
                usingStorageBytes = getTotalStorage(FLAG_MB) - getAvailableStorage(FLAG_MB);
            }else if(unitType.equals(FLAG_GB)){
                usingStorageBytes = getTotalStorage(FLAG_GB) - getAvailableStorage(FLAG_GB
                );
            }
        }

        tempPath = null;
        return usingStorageBytes;
    }

    public long getTotalStorage(String unitType){
        totalStorageBytes = stat.getBlockSizeLong() * stat.getBlockCountLong();

        if(unitType.equals(FLAG_BYTE)){
            return totalStorageBytes;
        }

        if(unitType.equals(FLAG_KB)){
            totalStorageBytes = totalStorageBytes / 1024;
        }
        if(unitType.equals(FLAG_MB)){
            totalStorageBytes = totalStorageBytes / 1024 / 1024;
        }
        if(unitType.equals(FLAG_GB)){
            totalStorageBytes = totalStorageBytes / 1024 / 1024 / 1024;
        }

        return totalStorageBytes;
    }

    // Function    : getFolderSize
    // Parameter   : File
    // Description : Parameter의 경로의 전체용량을 계산
    // Return Type : long
    public long getFolderSize(File file){
        long dFolderSize = 0;
        tempPath = file;
        File[] fList = tempPath.listFiles();
        for (int i = 0; i < fList.length; i++) {
            if (fList[i].isFile()) {
                dFolderSize += fList[i].length();
            } else {
                getFolderSize(fList[i]);
            }
        }
        return dFolderSize;
    }

    private class AutoChageStorageUnit{
        public String dataUnit;
        public String dataQuantity;

        public void chageDataUnit(long orignalDataQuantity){
            // 기준 : 최초 Byte로 받기떄문에 Byte단위부터 시작
            // GB표시하였으나 0으로 표기될 경우
            if(orignalDataQuantity / 1024 / 1024 / 1024 < 1){
                this.dataQuantity = Long.toString(orignalDataQuantity / 1024 / 1024);
                this.dataUnit = "MB";
            }else{
                this.dataQuantity = Long.toString(orignalDataQuantity / 1024 / 1024 / 1024);
                this.dataUnit = "GB";
            }
            //if(orignalDataQuantity / 1024 / 1024 <1){
            //    this.dataQuantity = Long.toString(orignalDataQuantity / 1024);
            //    this.dataUnit = "KB";
            //}else{
            //    this.dataQuantity = Long.toString(orignalDataQuantity / 1024 / 1024);
            //    this.dataUnit = "MB";
            //}
        }
    }


}
