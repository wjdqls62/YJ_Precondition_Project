package prc.yjsys.com.reliabilitypreconditionforart.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Locale;
import prc.yjsys.com.reliabilitypreconditionforart.R;
import prc.yjsys.com.reliabilitypreconditionforart.Utility.AppSearchManager;
import prc.yjsys.com.reliabilitypreconditionforart.Utility.ExcelHelper;

/**
 * Created by jeongbin.son on 2017-02-18.
 */
public class AppPermission_Fragment extends Fragment {
    private Intent intent = null;
    private TextView emptyPermission = null;
    private ListView listview = null;
    private ListViewAdapter adapter = null;
    private AppSearchManager asm = null;

    private ArrayList<ExcelHelper.AppInfo> arrAppInfo = null;
    private ExcelHelper EH = null;

    private Locale systemLocale = null;
    private String strLanguage = null;


    String TAG = "AppPermission_Fragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        asm = new AppSearchManager(getActivity());
        arrAppInfo = new ArrayList<ExcelHelper.AppInfo>();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(!getSystemLanguage().equals("en")){
            Snackbar.make(getView(),"시스템 언어가 영어일 경우만 모두 표시됩니다", 3000)
                    .setAction("Settings", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClassName("com.android.settings","com.android.settings.LanguageSettings");
                            startActivity(intent);
                        }
                    }).show();
        }
    }

    public String getSystemLanguage(){
        systemLocale = getResources().getConfiguration().locale;
        return systemLocale.getLanguage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_permission, container, false);

        listview = (ListView) rootView.findViewById(R.id.permission_listView);
        emptyPermission = (TextView) rootView.findViewById(R.id.permission_empty);
        adapter = new ListViewAdapter();

        try {
            EH = new ExcelHelper(getActivity());
            arrAppInfo = EH.getData();

            if(arrAppInfo.size() != 0){
                for(int i=0; i<arrAppInfo.size(); i++){
                    if(asm.isInstalled(arrAppInfo.get(i).getAppName(), arrAppInfo.get(i).getPackageName()))
                    {
                        adapter.addItem(asm.getIcon(), arrAppInfo.get(i).getAppName(), arrAppInfo.get(i).getPackageName());
                    }
                }

                listview.setAdapter(adapter);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        goAppPermission(adapter.listViewItemList.get(position).getPackageName());
                    }
                });
            }else{
                listview.setEmptyView(emptyPermission);
            }
        }
        catch (Exception e){
            listview.setEmptyView(emptyPermission);
        }
        return rootView;
    }

    public static Fragment newInstance(){
        return new AppPermission_Fragment();
    }

    public void goAppPermission(String packageName){
        intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", packageName, null);
        intent.setData(uri);
        getActivity().startActivity(intent);
    }

    public class ListViewAdapter extends BaseAdapter{

        private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>();

        @Override
        public int getCount() {
            return listViewItemList.size();
        }

        @Override
        public Object getItem(int position) {
            return listViewItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Context context = parent.getContext();

            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_item, parent, false);
            }

            ImageView iconImageView = (ImageView) convertView.findViewById(R.id.list_item_thumbnail);
            TextView titleTextView = (TextView) convertView.findViewById(R.id.list_title);

            ListViewItem listViewItem = listViewItemList.get(position);

            iconImageView.setImageDrawable(listViewItem.getIcon());
            titleTextView.setText(listViewItem.getTitle());

            return convertView;
        }

        public void addItem(Drawable icon, String title, String packageName){
            ListViewItem item = new ListViewItem();

            item.setIcon(icon);
            item.setTitle(title);
            item.setPackageName(packageName);

            listViewItemList.add(item);
        }
    }

    public class ListViewItem{
        private Drawable iconDrawble;
        private String titleStr;
        private String descStr;
        private String packageStr;

        public void setIcon(Drawable icon){
            iconDrawble = icon;
        }

        public void setTitle(String title){
            titleStr = title;
        }

        public void setDesc(String desc){
            descStr = desc;
        }

        public Drawable getIcon(){
            return this.iconDrawble;
        }

        public String getTitle(){
            return this.titleStr;
        }

        public String getDesc(){
            return this.descStr;
        }

        public void setPackageName(String packageName){
            this.packageStr = packageName;
        }

        public String getPackageName(){
            return packageStr;
        }
    }
}
