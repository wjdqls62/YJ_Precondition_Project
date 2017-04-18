package prc.yjsys.com.reliabilitypreconditionforart;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import prc.yjsys.com.reliabilitypreconditionforart.Fragment.AppPermission_Fragment;
import prc.yjsys.com.reliabilitypreconditionforart.Fragment.DummyFileList_Fragment;
import prc.yjsys.com.reliabilitypreconditionforart.Fragment.Generate_Dummy_Fragment;
import prc.yjsys.com.reliabilitypreconditionforart.Fragment.Information_Fragment;
import prc.yjsys.com.reliabilitypreconditionforart.Fragment.ART_Script_Preference_Fragment;
import prc.yjsys.com.reliabilitypreconditionforart.Fragment.Settings_Preference_Fragment;
import prc.yjsys.com.reliabilitypreconditionforart.Utility.DeviceManager;
import prc.yjsys.com.reliabilitypreconditionforart.Utility.PreferenceXMLCopyManager;

/**
 * Created by jeongbin.son on 2017-01-16.
 */
public class Main_Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private final String[] mDummyDirPath = new String[]{"/sdcard","/YJ_Precondition"};
    private final int MY_PERMISSION = 0;

    private PreferenceXMLCopyManager fc = null;
    private DeviceManager DM = null;
    private TextView Header_Model_Name = null;
    private TextView Header_Phone_Num = null;

    private Fragment Fragment = null;
    private FragmentManager FM = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setPrmissionCheck();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        FM = getFragmentManager();
        Fragment = new ART_Script_Preference_Fragment();
        FM.beginTransaction().replace(R.id.content_frame,Fragment).commit();


        drawer.addDrawerListener(toggle);
        toggle.syncState();



    }

    public void init_Navi_Header(){

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View nav_header_view = navigationView.getHeaderView(0);
        Header_Model_Name = (TextView) nav_header_view.findViewById(R.id.header_model_name);
        Header_Phone_Num =  (TextView) nav_header_view.findViewById(R.id.header_phone_num);

        DM = new DeviceManager(getApplicationContext());
        Header_Model_Name.setText(DM.getModelName());
        Header_Phone_Num.setText(DM.getPhoneNumber());

        navigationView.setItemIconTintList(null);
        navigationView.getMenu().getItem(0).setChecked(true);


    }


    private void setPrmissionCheck() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.WRITE_CONTACTS
                    , Manifest.permission.READ_PHONE_STATE}, MY_PERMISSION);
        }else{
            init_Navi_Header();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 권한수락을 거부하였을 경우 어플리케이션 종료
        for(int i=0; i<permissions.length; i++){
            if(grantResults[i] == -1){
                android.os.Process.killProcess(android.os.Process.myPid());
            }else{
                // 권한 수락할 경우 NagationView의 초기화를 시작
                init_Navi_Header();

            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        setPrmissionCheck();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment = null;
        FM = getFragmentManager();

        // Handle navigation view item clicks here.
        int id = item.getItemId();
         if (id == R.id.navi_script) {
             Fragment = new ART_Script_Preference_Fragment();
             FM.beginTransaction().replace(R.id.content_frame,Fragment).commit();

        } else if (id == R.id.navi_permision) {
             Fragment = new AppPermission_Fragment();
             FM.beginTransaction().replace(R.id.content_frame,Fragment).commit();

        } else if (id == R.id.navi_autofill) {
             Fragment = new Generate_Dummy_Fragment();
             FM.beginTransaction().replace(R.id.content_frame,Fragment).commit();

        } else if (id == R.id.navi_trash) {
             Fragment = new DummyFileList_Fragment();
             FM.beginTransaction().replace(R.id.content_frame,Fragment).commit();

        } else if (id == R.id.navi_devinfo) {
             Fragment = new Information_Fragment();
             FM.beginTransaction().replace(R.id.content_frame,Fragment).commit();
        }else if (id == R.id.navi_appsettings) {
             Fragment = new Settings_Preference_Fragment();
             FM.beginTransaction().replace(R.id.content_frame,Fragment).commit();
         }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}