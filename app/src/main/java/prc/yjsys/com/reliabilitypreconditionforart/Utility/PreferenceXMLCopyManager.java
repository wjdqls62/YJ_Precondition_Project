package prc.yjsys.com.reliabilitypreconditionforart.Utility;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import prc.yjsys.com.reliabilitypreconditionforart.R;

/**
 * Created by jeongbin.son on 2017-01-12.
 * Description : 앱의 preference.xml 을 하기 copy_Path 변수의 경로에 file Copy한다.
 */

public class PreferenceXMLCopyManager {
    final static int BUFFER_SIZE                = 10;
    BufferedInputStream bis                     = null;
    BufferedOutputStream bos                    = null;
    FileOutputStream fos                        = null;
    FileInputStream fis                         = null;
    File copy_File, tempFile                    = null;
    Context context                             = null;
    View view                                   = null;
    String PackageName, pref_Path, Copy_Path    = null;
    String TAG                                  = "PreferenceXMLCopyManager";

    int i, len = 0;
    byte[]  buffer = new byte[BUFFER_SIZE];

    public PreferenceXMLCopyManager(Context context, View view){
        this.context = context;
        this.view = view;
        PackageName = context.getPackageName();

        // Copy될 preference.xml 파일의 경로
        Copy_Path = context.getResources().getString(R.string.pref_folder_path);

        // Original preference.xml 파일의 경로
        pref_Path = "/data/data/" + PackageName + "/shared_prefs/" + PackageName + "_preferences.xml";
        copy_File = new File(Copy_Path + PackageName + "_preferences.xml");

        // Copy_Path경로가 없을경우 Copy전 디렉토리 생성
        tempFile = new File(Copy_Path);
        if(!tempFile.exists()){
            tempFile.mkdirs();
        }
    }

    public void fileCopy(){
        try{
            fis = new FileInputStream(pref_Path);
            fos  = new FileOutputStream(copy_File);
            bis = new BufferedInputStream(fis);
            bos = new BufferedOutputStream(fos, BUFFER_SIZE);

            while((i = bis.read(buffer)) != -1){
                bos.write(buffer, 0, i);
                bos.flush();
                len += i;
            }

            fis.close();
            fos.close();
            Toast.makeText(context, "Preference XML Copy to Success!", Toast.LENGTH_SHORT).show();

        }catch(FileNotFoundException e){
            Toast.makeText(context, "Not found Original Preference XML", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }catch(Exception e){
            Toast.makeText(context, "Error!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
