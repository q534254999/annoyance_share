package com.example.shares;

import android.app.Application;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import cn.bmob.newim.BmobIM;
import cn.bmob.v3.Bmob;

/**
 * Created by Wenpzhou on 2017/5/8 0008.
 */

public class MyApplication extends Application {
    private static Context context;
    private static MyApplication INSTANCE;

    public static MyApplication INSTANCE(){
        return INSTANCE;
    }
    private void setInstance(MyApplication app) {
        setBmobIMApplication(app);
    }
    private static void setBmobIMApplication(MyApplication a) {
        MyApplication.INSTANCE = a;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setInstance(this);
        context=getApplicationContext();
        //初始化crashhandler，用于捕获UncaughtException
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);

        //只有主进程运行的时候才需要初始化
        if (getApplicationInfo().packageName.equals(getMyProcessName())){
            //im初始化
            BmobIM.init(this);
            //注册消息接收器
            BmobIM.registerDefaultMessageHandler(new MyIMMessageHandler(context));
        }
    }
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Context getContext(){
        return context;
    }
}
