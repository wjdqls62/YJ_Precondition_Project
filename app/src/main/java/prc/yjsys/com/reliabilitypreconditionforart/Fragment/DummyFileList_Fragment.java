package prc.yjsys.com.reliabilitypreconditionforart.Fragment;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import prc.yjsys.com.reliabilitypreconditionforart.R;
import prc.yjsys.com.reliabilitypreconditionforart.Utility.DummySearchManager;
import prc.yjsys.com.reliabilitypreconditionforart.Utility.StorageStateManager;

/**
 * Created by jeongbin.son on 2017-02-20.
 */
public class DummyFileList_Fragment extends Fragment implements View.OnClickListener{
    private final String KB                                                         = "KB";
    private final String MB                                                         = "MB";
    private final String GB                                                         = "GB";
    private final int    SnackBar_Length_Long                                       = 3000;

    private FragmentManager FM = null;
    private ListViewItem item                                                       = null;
    private DummySearchManager DummySearchManager                                   = null;
    private StorageStateManager StorageStateManager                                 = null;
    private DecimalFormat df                                                        = null;

    private TextView available_textView, using_textView, empty_listItem_textView    = null;
    private Button btn_delete                                                       = null;
    private ListView listview                                                       = null;
    private ListViewAdapter adapter                                                 = null;
    public ArrayList<ListViewItem> listViewItemList                                 = null;
    private ArrayList<File> mDeleteFileName                                         = null;
    private File[] files                                                            = null;
    private final String TAG                                                        = "DummyFileList_Fragment";

    public static Fragment newInstance(){
        return new DummyFileList_Fragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        df = new DecimalFormat("###,###.####");
        mDeleteFileName = new ArrayList<File>();
        StorageStateManager = new StorageStateManager();
        listViewItemList = new ArrayList<ListViewItem>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frgm_del_dummy_list, container, false);
        empty_listItem_textView = (TextView) rootView.findViewById(R.id.text_empty_listitem);

        btn_delete = (Button) rootView.findViewById(R.id.btn_delete);

        listview = (ListView) rootView.findViewById(R.id.delete_listview);


        FM = getActivity().getFragmentManager();
        FM.beginTransaction().replace(R.id.del_list_storage_state_view, StorageStateManager).commit();

        btn_delete.setOnClickListener(this);

        init_ListViewItem();

        return rootView;
    }

    public void init_ListViewItem(){
        adapter = new ListViewAdapter();
        listViewItemList.clear();
        mDeleteFileName.clear();

        try{
            files = new DummySearchManager(getActivity()).getFileList();

            if(files.length != 0){
                for(int i=0; i<files.length; i++){
                    adapter.addItem(files[i]);
                }
                listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                listview.setAdapter(adapter);
            }else{
                listview.setEmptyView(empty_listItem_textView);
            }
        }catch (NullPointerException e){
            listview.setEmptyView(empty_listItem_textView);
        }

        //if(files.length != 0){
        //    for(int i=0; i<files.length; i++){
        //        adapter.addItem(files[i]);
        //    }
        //    listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        //    listview.setAdapter(adapter);
        //}else{
        //    listview.setEmptyView(empty_listItem_textView);
        //}
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_delete:
                deleteSelectedFiles();
                break;
        }
    }

    public void deleteSelectedFiles(){
        if(mDeleteFileName.size() != 0){
            for(int i=0; i<mDeleteFileName.size(); i++){
                mDeleteFileName.get(i).delete();
            }

            Snackbar.make(getView(),getResources().getString(R.string.toast_delete_success), SnackBar_Length_Long).show();
            //Toast.makeText(getActivity(),getResources().getString(R.string.toast_delete_success), Toast.LENGTH_SHORT).show();
        }else{
            Snackbar.make(getView(), getResources().getString(R.string.toast_not_select), SnackBar_Length_Long).show();
            //Toast.makeText(getActivity(),getResources().getString(R.string.toast_not_select), Toast.LENGTH_SHORT).show();
        }

        init_ListViewItem();
        StorageStateManager.refrestStorageStateView();
    }

    public class ListViewAdapter extends BaseAdapter {
        ViewHolder holder;

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
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Context context = parent.getContext();
            final View v = convertView;
            holder = new ViewHolder();

            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_del_item, parent, false);

                holder.chk_delete = (CheckBox) convertView.findViewById(R.id.chk_delete);
                holder.text_fileName = (TextView) convertView.findViewById(R.id.del_filename);
                holder.text_fileSize = (TextView) convertView.findViewById(R.id.del_filesize);
                holder.chk_delete.setFocusable(false);
                holder.chk_delete.setClickable(false);

                convertView.setTag(holder);
            }

            holder = (ViewHolder) convertView.getTag();

            holder.text_fileSize.setText(df.format(listViewItemList.get(position).fileSize / 1024) + KB);
            holder.text_fileName.setText(listViewItemList.get(position).fileName);
            holder.chk_delete.setChecked(((ListView) parent).isItemChecked(position));
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // ListViewItem을 터치 후 Check state가 변경된 후 Listener 호출됨
                    // ListViewItem이 Check 상태
                    if (listview.isItemChecked(position) == true) {
                        holder.chk_delete.setChecked(true);
                        mDeleteFileName.add(listViewItemList.get(position).getFile());

                    } else {
                        holder.chk_delete.setChecked(false);
                        for(int i=0; i<mDeleteFileName.size(); i++){
                            if(listViewItemList.get(position).fileName.equals(mDeleteFileName.get(i).getName())){
                                mDeleteFileName.remove(i);
                                break;
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            });
            return convertView;
        }

        public void addItem(File file){
            item = new ListViewItem();

            item.setFile(file);
            listViewItemList.add(item);
        }
    }

    static class ViewHolder{
        CheckBox chk_delete;
        TextView text_fileName;
        TextView text_fileSize;

    }

    public class ListViewItem{
        private File file;
        private String fileName;
        private long fileSize;
        private String filePath;
        public boolean isDeleteChecked = false;

        public void setFile(File file){
            this.file = file;
            fileName = file.getName();
            fileSize = file.length();
            filePath = file.getPath();
        }

        public File getFile(){
            return file;
        }

    }


}
