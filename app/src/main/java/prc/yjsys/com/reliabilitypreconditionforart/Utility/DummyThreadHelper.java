package prc.yjsys.com.reliabilitypreconditionforart.Utility;

import android.app.NotificationManager;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
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


/**
 * Created by jeongbin.son on 2017-01-19.
 */
public class DummyThreadHelper {
    private NotificationManager mNotificationManager    = null;
    private NotificationCompat.Builder mNotifyBuilder   = null;
    private View view                                   = null;

    private Context context                             = null;
    private GenerateDummy gd = null;

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

        Snackbar.make(view, "Generate Contact : "+contact+"\n"+"Generate File : "+Long.toString(file), 4000).show();
        //Toast.makeText(context, "Generate Contact : "+contact+"\n"+"Generate File : "+Long.toString(file), Toast.LENGTH_SHORT).show();
        if(mAsyncTask != null){
            mAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else{
            mAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private class GenerateDummy{
        Calendar calendar = null;
        ContentResolver cr = null;
        ContentValues cv = null;


        private String cDisplayName = "Reliability_";
        private String cPhoneNum_first = "010";
        private String cPhoneNum_middle = "2500";
        private String cPhoneNum_end = "1000";
        private ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();


        public GenerateDummy(Context context){
            cr = context.getContentResolver();
            cv = new ContentValues();
            calendar = Calendar.getInstance();
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

            if(contact_number == null) {
                ops.add(ContentProviderOperation.
                        newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, cPhoneNum_first + cPhoneNum_middle + (Integer.parseInt(cPhoneNum_end)+num))
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
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
        private byte[] m_byteFiledata            = new byte[1024];
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isRun.setDummyThread_IsRun(true);
            mGenerateDummy.setEnabled(false);
            mNotificationManager.notify(mNotifyID, mNotifyBuilder.build());
            calendar = Calendar.getInstance();
            d_file_name =
                    String.valueOf(calendar.get(Calendar.YEAR)) +
                    String.valueOf(calendar.get(Calendar.MONTH)+1)+
                    String.valueOf(calendar.get(Calendar.DATE))+
                    String.valueOf(calendar.get(Calendar.HOUR))+
                    String.valueOf(calendar.get(Calendar.MINUTE))+
                    String.valueOf(calendar.get(Calendar.SECOND))+ ".dat";
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
                    bos = new BufferedOutputStream(fos, 1024);

                    for (i = 0; i < cnt_file; i += 1024) {
                        bos.write(m_byteFiledata);
                        progress = Integer.valueOf((int) ((double) i / (double) cnt_file * 100.0));

                        if(temp != progress){
                            temp = progress;
                            publishProgress(progress, FLAG_DUMMY_FILE);
                        }
                    }
                    bos.flush();
                }
            }catch(java.io.IOException e){
                    e.printStackTrace();
                } finally{
                    publishProgress(100, FLAG_DUMMY_FILE);
                    if (bos != null) try {
                        bos.close();
                    } catch (Exception e) {
                    };
                    if (fos != null) try {
                        fos.close();
                    } catch (Exception e) {
                    }
                }

            // 더미주소록 생성부
            if(cnt_contact != 0){
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
                mNotificationManager.notify(mNotifyID, mNotifyBuilder
                        .setContentTitle(context.getResources().getString(R.string.notification_generating_dummy))
                        .setContentText(values[0] + "%").build());

                mNotifyBuilder.setProgress(100, values[0], false);
                super.onProgressUpdate(values[0]);
            }

            else if(values[1].equals(FLAG_DUMMY_CONTACT)){
                mNotificationManager.notify(mNotifyID, mNotifyBuilder
                        .setContentTitle(context.getResources().getString(R.string.notification_generating_contact))
                        .setContentText(values[0] + "%").build());

                mNotifyBuilder.setProgress(100, values[0], false);
                super.onProgressUpdate(values[0]);
            }


        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mNotifyBuilder
                    .setContentTitle("")
                    .setContentText(context.getResources().getString(R.string.notification_generate_success))
                    .setProgress(0, 0, false);
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
