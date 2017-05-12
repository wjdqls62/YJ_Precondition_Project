package prc.yjsys.com.reliabilitypreconditionforart.Utility;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;

import prc.yjsys.com.reliabilitypreconditionforart.R;

/**
 * Created by jeongbin.son on 2017-01-26.
 * Description :
 */
public class DummyViewHelper implements SeekBar.OnSeekBarChangeListener, CheckBox.OnCheckedChangeListener{
    private final int FLAG_READY_FILE                                                     = 0;
    private final int FLAG_READY_CONTACT                                                  = 1;
    //private final int FLAG_READY_CALLLOGS                                               = 2;
    private final int FILE_PERCENTAGE_MAX                                                 = 70; // 최대 더미파일 생성 Percentage
    private final int CONTACT_EA_MAX                                                      = 2000; // 최대 주소록 생성 갯수
    private final String MB                                                               = "MB";
    private final String EA                                                               = "EA";

    private static long availableBytes                                                    = 0;
    public  static int ready_dummy_contact                                                = 0;
    public  static String ready_dummy_contact_number                                      = null;
    public  static long ready_dummy_file                                                  = 0;

    private File path                                                                     = null;
    private DecimalFormat df                                                              = null;
    private Context context                                                               = null;
    private StorageStateManager StorageStateManager                                       = null;

    private View rootView                                                                 = null;
    private SeekBar file_seek                                                             = null;
    private SeekBar contact_seek                                                          = null;
    private TextView available_storage_mb, pre_dummy_mb, pre_contact_ea                   = null;
    private TextView contact_seekbar_ea, file_seekbar_percent                             = null;
    private EditText manual_input_contact, manual_input_file, manual_input_contact_number = null;
    private CheckBox chk_Dummy                                                            = null;
    private CheckBox chk_Contact                                                          = null;

    private StatFs stat                                                                   = null;
    private String TAG                                                                    = "DummyViewHelper";

    public DummyViewHelper(Context context, View rootView){
        this.rootView = rootView;
        this.context = context;
        this.pre_dummy_mb = (TextView) rootView.findViewById(R.id.pre_dummy_mb);
        this.pre_contact_ea = (TextView) rootView.findViewById(R.id.pre_contact_ea);
        //this.available_storage_mb = (TextView) rootView.findViewById(R.id.text_available_storage_mb);
        this.file_seek = (SeekBar) rootView.findViewById(R.id.dummy_file_seekBar);
        this.contact_seek = (SeekBar) rootView.findViewById(R.id.contact_seekBar);
        this.manual_input_contact = (EditText) rootView.findViewById(R.id.input_manual_dummy_contact);
        this.manual_input_contact_number = (EditText) rootView.findViewById(R.id.input_manual_dummy_contact_number);
        this.manual_input_file = (EditText) rootView.findViewById(R.id.input_manual_dummy_file);
        this.chk_Dummy = (CheckBox) rootView.findViewById(R.id.chk_dummy);
        this.chk_Contact = (CheckBox) rootView.findViewById(R.id.chk_contact);
        contact_seekbar_ea = (TextView) rootView.findViewById(R.id.contact_seekBar_ea);
        file_seekbar_percent = (TextView) rootView.findViewById(R.id.file_seekBar_percent);
        df = new DecimalFormat("###,###.####");
        file_seek.setMax(FILE_PERCENTAGE_MAX);
        file_seek.setOnSeekBarChangeListener(this);
        contact_seek.setMax(CONTACT_EA_MAX);
        contact_seek.setOnSeekBarChangeListener(this);

        chk_Dummy.setOnCheckedChangeListener(this);
        chk_Contact.setOnCheckedChangeListener(this);
        Refresh_Current_Storage();
        Init_EditText_Listener();

    }

    public void reset_Progress_Config(){
        ready_dummy_contact = 0;
        ready_dummy_contact_number = null;
        ready_dummy_file = 0L;

        manual_input_contact.clearComposingText();;
        manual_input_contact_number.clearComposingText();
        manual_input_file.clearComposingText();

        file_seek.setProgress(0);
        contact_seek.setProgress(0);
    }

    public void Init_EditText_Listener(){

        Log.d(TAG,manual_input_file.getText().toString());
        Log.d(TAG, manual_input_contact.getText().toString());

        manual_input_contact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    Refresh_Manual_Input_Progress_UI(R.id.input_manual_dummy_contact, 0);
                } else if (!TextUtils.isEmpty(s) && 1 <= Integer.parseInt(s.toString())) {
                    Log.d(TAG + "_onTextChanged", "" + Integer.parseInt(s.toString()));
                    Refresh_Manual_Input_Progress_UI(R.id.input_manual_dummy_contact, Integer.parseInt(s.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        manual_input_file.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(TextUtils.isEmpty(s)){
                    Refresh_Manual_Input_Progress_UI(R.id.input_manual_dummy_file, 0);
                }else if(!TextUtils.isEmpty(s) && 1 <= Integer.parseInt(s.toString())){
                    Refresh_Manual_Input_Progress_UI(R.id.input_manual_dummy_file, Integer.parseInt(s.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        manual_input_contact_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(s)){
                    ready_dummy_contact_number = null;
                    manual_input_contact_number.clearComposingText();
                }else{
                    ready_dummy_contact_number = s.toString();
                    Log.d(TAG, "전화번호 수동입력 : "+ready_dummy_contact_number);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void Refresh_Current_Storage(){
        StorageStateManager = new StorageStateManager();
        availableBytes = StorageStateManager.getAvailableStorage("MB");
    }

    public long Refresh_Current_Storage(boolean isExtend){
        path = Environment.getDataDirectory();
        stat = new StatFs(path.getPath());
        return Integer.parseInt(Long.toString(stat.getFreeBlocksLong() * stat.getBlockSizeLong() / 1024 / 1024));
    }

    public long Refresh_Current_Progress_UI(int progress){
        return availableBytes * progress / 100;
    }

    public void Refresh_Manual_Input_Progress_UI(int viewID, int progress){
        if(viewID == R.id.input_manual_dummy_file){
            if(progress == 0) {
                file_seek.setProgress(progress);
                manual_input_file.clearComposingText();
            }
            else if(progress <= FILE_PERCENTAGE_MAX && progress >= 1){
                file_seek.setProgress(progress);
            }else{
                manual_input_file.setText(String.valueOf(FILE_PERCENTAGE_MAX));
                Snackbar.make(rootView, String.valueOf(FILE_PERCENTAGE_MAX) + "% 이하로 입력하세요", Snackbar.LENGTH_LONG).show();
                //Toast.makeText(context, String.valueOf(FILE_PERCENTAGE_MAX) + "% 이하로 입력하세요", Toast.LENGTH_SHORT).show();
                progress = FILE_PERCENTAGE_MAX;
                file_seek.setProgress(progress);
            }

            pre_dummy_mb.setText(df.format(Refresh_Current_Progress_UI(progress)) + MB);
            Ready_Manual_Generate(FLAG_READY_FILE);

        }else if(viewID == R.id.input_manual_dummy_contact){
            if(progress == 0){
                contact_seek.setProgress(progress);
                pre_contact_ea.setText("0");
                manual_input_contact.clearComposingText();
            }
            else if(progress <= CONTACT_EA_MAX && progress >= 1){
                contact_seek.setProgress(progress);
                pre_contact_ea.setText(df.format(progress)+EA);

            }else{
                manual_input_contact.setText(String.valueOf(CONTACT_EA_MAX));
                progress = CONTACT_EA_MAX;
                contact_seek.setProgress(progress);
                pre_contact_ea.setText(df.format(progress) + EA);
                Snackbar.make(rootView, String.valueOf(CONTACT_EA_MAX)+"개 이하로 입력하세요", Snackbar.LENGTH_LONG).show();
                //Toast.makeText(context,String.valueOf(CONTACT_EA_MAX)+"개 이하로 입력하세요", Toast.LENGTH_SHORT).show();
            }

            Ready_Manual_Generate(FLAG_READY_CONTACT);
        }
    }

    public void Refresh_Current_Stroage_UI(){
        Refresh_Current_Storage();
        //available_storage_mb.setText(String.format("%,d", availableBytes) + MB);

    }

    public void Ready_to_Generate(int seekBarID, int value){
        if(seekBarID == R.id.contact_seekBar){
            ready_dummy_contact = value;
        }else if(seekBarID == R.id.dummy_file_seekBar){
            ready_dummy_file = (availableBytes * value) *1024*1024 / 100;
            Log.d(TAG,"progressbar file size : "+ready_dummy_file);
        }
    }

    public void Ready_Manual_Generate(int type){
        if (TextUtils.isEmpty(manual_input_file.getText())) {
            ready_dummy_file = 0;
        }
        else
        {
            if (type == FLAG_READY_FILE)
            {
                ready_dummy_file = (availableBytes * Long.valueOf(manual_input_file.getText().toString())) *1024*1024 / 100;
                Log.d(TAG,""+ready_dummy_file );
            }
        }

        if (TextUtils.isEmpty(manual_input_contact.getText()))
        {
            ready_dummy_contact = 0;
        }
        else
        {
            if (type == FLAG_READY_CONTACT)
            {
                ready_dummy_contact = Integer.parseInt(manual_input_contact.getText().toString());
            }
        }
    }
    // 파일 생성전 생성 후 내장메모리가 600MB이상 남아있을 경우만 file write
    public boolean canFileWrite(long size){
        if(availableBytes-size >=629145600){
            return true;
        }else{
            file_seek.setProgress(0);
            manual_input_file.clearComposingText();
            ready_dummy_file = 0;
            return false;
        }
    }

    public void onHideKeypad(){
        InputMethodManager inputManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
    }

    public boolean canWrite(){


        return false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()){
            case R.id.dummy_file_seekBar :
                file_seekbar_percent.setText(progress + "%");
                pre_dummy_mb.setText(df.format(Refresh_Current_Progress_UI(progress)) + MB);
                break;
            case R.id.contact_seekBar :
                pre_contact_ea.setText(df.format(progress) + EA);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()){
            case R.id.dummy_file_seekBar :
                manual_input_file.setVisibility(View.GONE);
                ready_dummy_file = 0;
                break;
            case R.id.contact_seekBar :
                manual_input_contact.setVisibility(View.GONE);
                manual_input_contact_number.setVisibility(View.GONE);
                ready_dummy_contact = 0;
                break;
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch(seekBar.getId()){
            case R.id.dummy_file_seekBar :
                manual_input_file.setVisibility(View.VISIBLE);
                manual_input_file.setText(String.valueOf(seekBar.getProgress()));
                break;
            case R.id.contact_seekBar :
                manual_input_contact.setText(String.valueOf(seekBar.getProgress()));
                manual_input_contact.setVisibility(View.VISIBLE);
                manual_input_contact_number.setVisibility(View.VISIBLE);
                break;
        }
        Ready_to_Generate(seekBar.getId(), seekBar.getProgress());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView.getId() == R.id.chk_contact) {
            if (isChecked == false) {
                contact_seekbar_ea.setEnabled(false);
                contact_seek.setEnabled(false);
                manual_input_contact_number.setVisibility(View.GONE);
                manual_input_contact.setVisibility(View.GONE);
                pre_contact_ea.setVisibility(View.GONE);
                ready_dummy_contact = 0;
                ready_dummy_contact_number = null;
            } else {
                contact_seekbar_ea.setEnabled(true);
                contact_seek.setEnabled(true);
                manual_input_contact_number.setVisibility(View.VISIBLE);
                manual_input_contact.setText(String.valueOf(contact_seek.getProgress()));

                manual_input_contact.setVisibility(View.VISIBLE);
                pre_contact_ea.setVisibility(View.VISIBLE);


                ready_dummy_contact = contact_seek.getProgress();
                ready_dummy_contact_number = manual_input_contact_number.getText().toString();

            }
        }
        else if(buttonView.getId() == R.id.chk_dummy){
                if(isChecked == false){
                    file_seek.setEnabled(false);
                    file_seekbar_percent.setEnabled(false);
                    manual_input_file.setVisibility(View.GONE);
                    pre_dummy_mb.setVisibility(View.GONE);
                    ready_dummy_file = 0;

                }else{
                    file_seek.setEnabled(true);
                    file_seekbar_percent.setEnabled(true);
                    manual_input_file.setVisibility(View.VISIBLE);
                    pre_dummy_mb.setVisibility(View.VISIBLE);
                    ready_dummy_file = file_seek.getProgress();
                }
            }
        }
    }
