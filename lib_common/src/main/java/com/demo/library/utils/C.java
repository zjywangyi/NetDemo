package com.demo.library.utils;

import android.content.Context;

public class C {

    private static Context mContext;

    private C() {
    }

    public static Context get() {
        return mContext;
    }

    public static void setContext(Context context) {
        C.mContext = context == null ? null : context.getApplicationContext();
    }

}
