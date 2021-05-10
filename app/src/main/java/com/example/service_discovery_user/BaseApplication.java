package com.example.service_discovery_user;

import android.app.Application;
import android.content.Context;

public class BaseApplication extends Application {
    private static Context mContext;
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext(){
        return mContext;
    }
}
