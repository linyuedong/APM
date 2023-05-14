package com.example.apm.aop;

import android.util.Log;

import okhttp3.EventListener;

public class Animal {

    private static final String TAG = "Animal";
    final private int age;
    final EventListener.Factory eventListener;
    final Object object;
    public Animal() {
        this.age = 10;
        this.object = null;
        this.eventListener = null;
    }

    public int getAge() {
        Log.e(TAG, "age =  " + age);
        return this.age;
    }

    public EventListener.Factory getEventListener() {
        Log.e(TAG, this.eventListener == null ? "EventListener = null " : "EventListener = not null " );
        return this.eventListener;
    }

}
