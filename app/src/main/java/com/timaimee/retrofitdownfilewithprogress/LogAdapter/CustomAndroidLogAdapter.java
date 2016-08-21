package com.timaimee.retrofitdownfilewithprogress.LogAdapter;

import android.util.Log;

import com.orhanobut.logger.LogAdapter;

/**
 * Created by TimAimee on 2016/7/27.
 */
public class CustomAndroidLogAdapter implements LogAdapter {

    @Override
    public void d(String s, String s1) {
        Log.d(s,s1);
    }

    @Override
    public void i(String s, String s1) {
        Log.i(s,s1);
    }

    @Override
    public void e(String s, String s1) {
        Log.e(s,s1);
    }

    @Override
    public void v(String s, String s1) {
        Log.v(s,s1);
    }

    @Override
    public void w(String s, String s1) {
        Log.w(s,s1);
    }

    @Override
    public void wtf(String s, String s1) {
        Log.e(s,s1);
    }
}

