package prc.yjsys.com.reliabilitypreconditionforart.Utility;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import prc.yjsys.com.reliabilitypreconditionforart.R;

/**
 * Created by jeongbin.son on 2017-04-17.
 */
public class ExcelHelper {

    private String xlsPath = null;
    private String dirPath = null;

    private Context context= null;
    private FileInputStream fis = null;
    private File tempFile = null;
    private ArrayList<AppInfo> arrAppInfo = null;

    private Workbook workbook = null;
    private Cell AppNameCell = null;
    private Cell PackageCell = null;
    private Sheet sheet = null;

    private String TAG = "ExcelHelper";
    private final int MAX = 100;

    public ExcelHelper(Context context){
        this.context = context;

        xlsPath = context.getResources().getString(R.string.permission_xls_path);
        dirPath = context.getResources().getString(R.string.permission_folder_path);

        if(!isExistExcel()){
            mkdirExcelDir();
        }

        init_openExcel();
    }

    public void init_openExcel(){
        arrAppInfo = new ArrayList<AppInfo>();

        try {
            fis = new FileInputStream(new File(xlsPath));
            workbook = Workbook.getWorkbook(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean isExistExcel(){
        tempFile = new File(xlsPath);

        if(tempFile.exists()){
            return true;
        }else{
            return false;
        }
    }

    public void mkdirExcelDir(){
        tempFile = new File(dirPath);
        tempFile.mkdirs();
    }

    public ArrayList<AppInfo> getData(){
        final int AppName_line = 1, PackageName_line = AppName_line+1;
        AppInfo AI = null;

        // i : Sheet Count, j : Contents Count
        int i=0, j=1;

        // Sheet 2개
        for(i=0; i<2;  i++){
            sheet = workbook.getSheet(i);
            for(j=1; j<100; j++){
                AI = new AppInfo();
                AppNameCell = sheet.getCell(AppName_line, j);
                PackageCell = sheet.getCell(PackageName_line, j);
                if(!AppNameCell.getContents().equals("") && !PackageCell.getContents().equals("")){
                    AI.add(AppNameCell.getContents(), PackageCell.getContents());
                    arrAppInfo.add(AI);;
                }else{
                    break;
                }
            }
        }


        for(int l=0;l<arrAppInfo.size(); l++){
            Log.d(TAG, "AppName : "+arrAppInfo.get(l).AppName+" / PackageName : " +arrAppInfo.get(l).packageName);
        }

        return arrAppInfo;
    }

    public class AppInfo{
        private String AppName;
        private String packageName;

        public void add(String AppName, String packageName){
            this.AppName = AppName;
            this.packageName = packageName;
        }

        public String getAppName(){
            return this.AppName;
        }

        public String getPackageName(){
            return this.packageName;
        }
    }

}
