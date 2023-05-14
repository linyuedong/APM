package com.example.apm.aop;

import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import okhttp3.EventListener;

@Aspect
public class FieldAspect {
    private static final String TAG = "FieldAspect";

    @Around("get(int com.example.apm.aop.Animal.age)")
    public int aroundFieldGet(ProceedingJoinPoint joinPoint) throws Throwable {
        // 执行原代码
        Object obj = joinPoint.proceed();
        int age = Integer.parseInt(obj.toString());
        Log.e(TAG, "age: " + age);
        return 100;
    }
    @Around("get(okhttp3.EventListener.Factory com.example.apm.aop.Animal.eventListener)")
    public Object aroundFieldGetE(ProceedingJoinPoint joinPoint) throws Throwable {
        // 执行原代码
        Object target = joinPoint.proceed();
        if (target == null) {
            Log.e(TAG, "aroundFieldGetE1: ");
            EventListener.Factory factory = (EventListener.Factory) target;
            return OkHttpEventListener.FACTORY;
        }
        Log.e(TAG, "aroundFieldGetE: ");
        return target;
    }
}
