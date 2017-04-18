package prc.yjsys.com.reliabilitypreconditionforart.Utility;

import android.content.Context;
import android.util.Log;

import java.io.File;

import prc.yjsys.com.reliabilitypreconditionforart.R;

/**
 * Created by jeongbin.son on 2017-02-23.
 */
public class DummySearchManager {

    private File[] files    = null;
    private Context context = null;
    private String dummy_save_path = null;
    private String TAG      = "DummySearchManager";

    public DummySearchManager(Context context){
        this.context = context;
        dummy_save_path = context.getResources().getString(R.string.dummy_save_path);
        files = new File(dummy_save_path).listFiles();
    }

    public File[] getFileList(){
        if(!new File(dummy_save_path).exists()){ new File(dummy_save_path).mkdirs(); }
        files = null;
        files = new File(dummy_save_path).listFiles();
        if(files == null){
            Log.d(TAG, "getFileList() : Null");
            return null;
        }else{
            return files;
        }
    }





}
