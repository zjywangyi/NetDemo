package com.demo.library.ui.base;

import android.app.Application;
import android.content.Context;

import com.demo.library.utils.C;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        init(this);
    }



    protected void init(Context context) {
        C.setContext(context);
    }

}
