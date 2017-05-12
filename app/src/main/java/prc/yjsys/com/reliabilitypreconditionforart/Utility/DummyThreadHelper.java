package prc.yjsys.com.reliabilitypreconditionforart.Utility;

import android.app.NotificationManager;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import prc.yjsys.com.reliabilitypreconditionforart.Fragment.Settings_Preference_Fragment;
import prc.yjsys.com.reliabilitypreconditionforart.R;

import static android.R.attr.id;


/**
 * Created by jeongbin.son on 2017-01-19.
 */
public class DummyThreadHelper {
    private NotificationManager mNotificationManager    = null;
    private NotificationCompat.Builder mNotifyBuilder   = null;
    private View view                                   = null;

    private Context context                             = null;
    private GenerateDummy gd = null;

    private boolean isRandomNumber                      = false;
    private boolean isDeleteContact                     = false;
    private int mNotifyID                               = 23;
    private int cnt_contact                             = 0;
    private long cnt_file                               = 0L;

    private String contact_number                       = null;
    private String TAG                                  = "GenerateDummy";


    public DummyThreadHelper(Context context, View view){
        this.view = view;
        this.context = context;
        this.mNotifyBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(android.support.v7.appcompat.R.drawable.notify_panel_notification_icon_bg)
                .setContentTitle("FileMaker")
                .setContentText("Generate Dummy File")
                .setProgress(100,0,false);
        this.mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        gd = new GenerateDummy(context);
        return;
   }

    public synchronized void exeAsyncTask(long file, int contact, String contact_number){
        Log.d(TAG, "Generate Button Disable");
        GenerateAsyncTask mAsyncTask = new GenerateAsyncTask(view);
        this.cnt_contact = contact;
        this.cnt_file = file;
        this.contact_number = contact_number;

        if(mAsyncTask != null){
            mAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else{
            mAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private class GenerateDummy{
        private Calendar calendar = null;
        private ContentResolver cr = null;
        private ContentValues cv = null;
        private SharedPreferences pref = null;

        private String cDisplayName = "Reliability_";
        private String cPhoneNum_first = "010";
        private String cPhoneNum_middle = "2500";
        private String cPhoneNum_end = "1000";
        private ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();


        public GenerateDummy(Context context){
            cr = context.getContentResolver();
            cv = new ContentValues();
            calendar = Calendar.getInstance();
            pref = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
            isRandomNumber = pref.getBoolean("autofill_dummy_rnadom_number",false);
            isDeleteContact = pref.getBoolean("autofill_dummy_delete_contact", false);
        }

        private void generate_contact(int num){
            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build());

            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
            .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, cDisplayName + String.format("%04d",num)).build());

            // 번호생성시 Option
            // 무작위 번호생성 Enable, 사용자 입력번호가 없을경우
            if(isRandomNumber && contact_number == null){
                num = (int)(Math.random() * 9999)+1;
            // 무작위 번호생성여부와 상관없이 사용자 입력번호가 있을경우
            }else if(!(contact_number==null)){
                num = Integer.parseInt(contact_number);
            // 아무런 옵션이 없을경우 1000부터 순차생성
            }else{
                num = Integer.parseInt(cPhoneNum_end) + num;
            }

            if(contact_number == null) {
                ops.add(ContentProviderOperation.
                        newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, cPhoneNum_first + cPhoneNum_middle + (String.format("%04d",num)))
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                        .build());

            }else{
                ops.add(ContentProviderOperation.
                        newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact_number)
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                        .build());
            }

            try{
                context.getContentResolver().applyBatch(ContactsContract.AUTHORITY,ops);
            }catch(Exception e){
                e.printStackTrace();
            }

            ops.clear();
        }
    }

    private class GenerateAsyncTask extends AsyncTask<Void, Integer, Integer>{
        private final Integer FLAG_DUMMY_CONTACT = 0;
        private final Integer FLAG_DUMMY_FILE    = 1;
        private View view                        = null;
        private Button mGenerateDummy            = null;
        private FileOutputStream fos             = null;
        private BufferedOutputStream bos         = null;
        private byte[] m_byteFiledata          = new byte[1024];
        private long i                           = 0L;
        private String d_folder_path             = context.getResources().getString(R.string.dummy_save_path);
        private String d_file_name               = null;
        private File mFolder                     = null;
        private File data                        = null;
        private int progress,temp                = 0;
        private Calendar calendar                = null;
        private Global_IsRunning_Thread isRun          = null;
        private SharedPreferences preference = null;

        public GenerateAsyncTask(View view){
            this.view = view;
            mGenerateDummy = (Button) view.findViewById(R.id.generate_dummy);
            isRun = Global_IsRunning_Thread.getInstance();
        }

        private void deleteContacts()
        {
            String[] projection = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME };
            Cursor localCursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, projection, null, null, null);
            int count = localCursor.getCount();
            if(count > 0)
            {
                localCursor.moveToFirst();
                String id = "";
                String del_name = "";
                Log.v(TAG, String.valueOf(localCursor.getCount()));
                for(int i = 0; i < count; i++)
                {
                    String str = "contact_id=" + localCursor.getInt(0);
                    id = localCursor.getString(localCursor.getColumnIndex(ContactsContract.Contacts._ID));
                    del_name = localCursor.getString(localCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if(del_name.equals("AMBS")){
                        Log.d(TAG,"not delete : id = "+id+", del_name :"+del_name + "low"+str);
                    }
                    else  {
                        Log.d(TAG,"delete : id = "+id+", del_name :"+del_name+ "low"+str);
                        context.getContentResolver().delete(ContactsContract.RawContacts.CONTENT_URI, ContactsContract.RawContacts.CONTACT_ID+" ="+id , null);
                    }
                    localCursor.moveToNext();
                }
            }
            localCursor.close();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isRun.setDummyThread_IsRun(true);
            mGenerateDummy.setEnabled(false);
            mNotificationManager.notify(mNotifyID, mNotifyBuilder.build());

            if(!(cnt_file == 0)){
                calendar = Calendar.getInstance();
                d_file_name =
                        String.valueOf(calendar.get(Calendar.YEAR)) +
                                String.format("%02d", (calendar.get(Calendar.MONTH)+1))+
                                String.format("%02d", (calendar.get(Calendar.DATE)+1))+
                                String.format("%02d", (calendar.get(Calendar.HOUR)+12))+
                                String.format("%02d", (calendar.get(Calendar.MINUTE)+1))+
                                String.format("%02d", (calendar.get(Calendar.SECOND)+1))+ ".dat";
            }
        }

        @Override
        protected Integer doInBackground(Void... params) {
            // 더미파일 생성부
            try {
                if (cnt_file != 0) {
                    mFolder = new File(d_folder_path);

                    if (!mFolder.exists()) {
                        mFolder.mkdirs();
                    }

                    data = new File(d_folder_path,d_file_name);
                    if (!data.exists()) {
                        data.createNewFile();
                    }

                    fos = new FileOutputStream(data);
                    bos = new BufferedOutputStream(fos, 4096);

                    for (i = 0; i <= cnt_file; i += 1024) {
                        bos.write(m_byteFiledata);

                        //progress = Integer.valueOf((int) ((double) i / (double) cnt_file * 100.0));

                        progress = (int) (((double)i/(double)cnt_file) * 100);
                        if(temp != progress){
                            temp = progress;
                            publishProgress(progress, FLAG_DUMMY_FILE);
                        }
                    }
                    bos.flush();
                }
            }catch(java.io.IOException e){
                    e.printStackTrace();
                }
                finally{
                    Log.d(TAG, "Finally");

                    if (bos != null) try {
                        bos.close();
                    } catch (Exception e) {
                    };
                    if (fos != null) try {
                        fos.close();
                    } catch (Exception e) {
                    }
                    publishProgress(100, FLAG_DUMMY_FILE);
                }

            // 더미주소록 생성부
            if(cnt_contact != 0){
                if(isDeleteContact){
                    deleteContacts();
                }
                for (int i=0; i<cnt_contact; i++){
                    gd.generate_contact(i);
                    publishProgress(Integer.valueOf((int)( (double)i/(double)cnt_contact * 100.0)), FLAG_DUMMY_CONTACT);
                }
                publishProgress(100, FLAG_DUMMY_CONTACT);
            }
            return null;
        }



        @Override
        protected void onProgressUpdate(Integer... values) {
            if(values[1].equals(FLAG_DUMMY_FILE)){
                mNotifyBuilder.setProgress(100, values[0], false);
                mNotificationManager.notify(mNotifyID, mNotifyBuilder
                        .setContentTitle(context.getResources().getString(R.string.notification_generating_dummy))
                        .setContentText(values[0] + "%").build());

            }
            else if(values[1].equals(FLAG_DUMMY_CONTACT)){
                mNotifyBuilder.setProgress(100, values[0], false);
                mNotificationManager.notify(mNotifyID, mNotifyBuilder
                        .setContentTitle(context.getResources().getString(R.string.notification_generating_contact))
                        .setContentText(values[0] + "%").build());
            }
            super.onProgressUpdate(values[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mNotifyBuilder
                    .setContentTitle("")
                    .setContentText(context.getResources().getString(R.string.notification_generate_success))
                    .setProgress(100, 100, false);
            mNotificationManager.notify(mNotifyID, mNotifyBuilder.build());

            isRun.setDummyThread_IsRun(false);
            mGenerateDummy.setEnabled(true);
            Log.d(TAG + "_onPostExecute", "Dummy file complete.");
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.d(TAG,"Generate Button Enable");
            mGenerateDummy.setEnabled(true);
        }
    }
}
