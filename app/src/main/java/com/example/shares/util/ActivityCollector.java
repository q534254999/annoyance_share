package com.example.shares.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wenpzhou on 2017/4/9 0009.
 * 用于随时随地退出程序
 */

public class ActivityCollector {
    public static List<Activity> activityList=new ArrayList<>();
    public static void addActivity(Activity activity){
        activityList.add(activity);
    }
    public static void removeActivity(Activity activity){
        activityList.remove(activity);
    }
    public static void finishAll(){
        for(Activity activity:activityList){
            if(!activity.isFinishing())
                activity.finish();
        }
    }
}
