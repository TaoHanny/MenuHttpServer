package com.shutuo.menuhttpserver;

import android.app.Application;

public class MyApp extends Application {

    private static Application myApp;

    public static Application get() {
        if(myApp!=null) return myApp;
        synchronized (MyApp.class){
            if(myApp==null){
                myApp = new Application();
            }
        }
        return myApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
