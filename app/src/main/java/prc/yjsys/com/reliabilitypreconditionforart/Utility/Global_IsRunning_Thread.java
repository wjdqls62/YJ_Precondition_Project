package prc.yjsys.com.reliabilitypreconditionforart.Utility;

/**
 * Created by jeongbin.son on 2017-04-10.
 */
public class Global_IsRunning_Thread {

    private boolean isRun_DummyThread;
    private static Global_IsRunning_Thread instance = null;

    public static synchronized Global_IsRunning_Thread getInstance(){
        if(null==instance){
            instance = new Global_IsRunning_Thread();
        }
        return instance;
    }

    public boolean isRun_DummyThread(){
        return isRun_DummyThread;
    }

    public void setDummyThread_IsRun(boolean value){
        isRun_DummyThread = value;
    }
}
