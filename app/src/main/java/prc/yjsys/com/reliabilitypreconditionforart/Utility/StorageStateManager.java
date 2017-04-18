package prc.yjsys.com.reliabilitypreconditionforart.Utility;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

import prc.yjsys.com.reliabilitypreconditionforart.R;

/**
 * Created by jeongbin.son on 2017-02-23.
 */
public class StorageStateManager extends Fragment {


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

    private String internalAutofillPath = getActivity().getResources().getString(R.string.dummy_save_path);

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

        mStorageType = (TextView) rootView.findViewById(R.id.storage_type);
        mStorageState = (TextView) rootView.findViewById(R.id.storage_usage);
        mUsageStorageProgress = (ProgressBar) rootView.findViewById(R.id.storage_usage_guage);

        mStorageType.setText(R.string.text_storage_type);
        refrestStorageStateView();

        return rootView;
    }

    public void refrestStorageStateView(){
        refreshCurrentStorage();

        mStorageState.setText(usingStorageBytes + FLAG_GB + " / " + totalStorageBytes + FLAG_GB);
        mUsageStorageProgress.setMax(Integer.parseInt(Long.toString(totalStorageBytes)));
        mUsageStorageProgress.setProgress(Integer.parseInt(Long.toString(usingStorageBytes)));
    }


    // Description : 함수 호출시점마다의 단말용량 현황 생성자를 업데이트
    public void refreshCurrentStorage(){
        path = Environment.getDataDirectory();
        stat = new StatFs(path.getPath());

        availableStorageBytes = getAvailableStorage("GB");
        usingStorageBytes = getUsingStorage("GB", "TOTAL");
        totalStorageBytes = getTotalStorage("GB");
    }

    // Description : 현재사용가능한 메모리 용량 확인
    public long getAvailableStorage(String unitType){
        if(unitType.equals(FLAG_KB)){
            availableStorageBytes = Integer.parseInt(Long.toString(stat.getFreeBlocksLong() * stat.getBlockSizeLong() / 1024));
        }else if(unitType.equals(FLAG_MB)){
            availableStorageBytes = Integer.parseInt(Long.toString(stat.getFreeBlocksLong() * stat.getBlockSizeLong() / 1024 / 1024));
        }else if(unitType.equals(FLAG_GB)){
            availableStorageBytes = Integer.parseInt(Long.toString(stat.getFreeBlocksLong() * stat.getBlockSizeLong() / 1024 / 1024 / 1024));
        }
        return availableStorageBytes;
    }


    // Description : 현재 사용중 메모리용량을 확인
    public long getUsingStorage(String unitType, String Path){
        // AutoFill 경로의 사용중 사용중 메모리 Return
        if(Path.equals("internalAutofillPath")) {
            tempPath = new File(internalAutofillPath);
            if (unitType.equals(FLAG_KB)) {
                usingStorageBytes = getFolderSize(new File(internalAutofillPath)) / 1024;
            } else if (unitType.equals(FLAG_MB)) {
                usingStorageBytes = getFolderSize(new File(internalAutofillPath)) / 1024 / 1024;
            } else if (unitType.equals(FLAG_GB)) {
                usingStorageBytes = getFolderSize(new File(internalAutofillPath)) / 1024 / 1024 / 1024;
            }
        }

        // 전체 사용중 메모리 Return
        else{
            if(unitType.equals(FLAG_KB)){
                usingStorageBytes = getTotalStorage("KB") - getAvailableStorage("KB");
            }else if(unitType.equals(FLAG_MB)){
                usingStorageBytes = getTotalStorage("MB") - getAvailableStorage("MB");
            }else if(unitType.equals(FLAG_GB)){
                usingStorageBytes = getTotalStorage("GB") - getAvailableStorage("GB");
            }
        }

        tempPath = null;
        return usingStorageBytes;
    }

    public long getTotalStorage(String unitType){
        totalStorageBytes = stat.getBlockSizeLong() * stat.getBlockCountLong();

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
}
