package com.example.apm.utils;

import android.text.TextUtils;
import android.util.Log;

public class LogUtil {
    private static boolean isDebug = true;
    public static String customTagPrefix = "";
    private static final String contentFormat = "[(%s:%d)#%s] >>> %s";
    private LogUtil(){}

    public static void setGlobleTag(String globleTag){
        customTagPrefix = globleTag;
    }

    public static void d(String content) {
        if (!isDebug) return;
        Log.d(generateTag(), generateContent(content));
    }

    public static void d(String content, Throwable tr) {
        if (!isDebug) return;
        Log.d(generateTag(), generateContent(content), tr);
    }

    public static void e(String content) {
        if (!isDebug) return;
        Log.e(generateTag(), generateContent(content));
    }

    public static void e(String content, Throwable tr) {
        if (!isDebug) return;
        Log.e(generateTag(), generateContent(content), tr);
    }

    public static void i(String content) {
        if (!isDebug) return;
        Log.i(generateTag(), generateContent(content));
    }

    public static void i(String content, Throwable tr) {
        if (!isDebug) return;
        Log.i(generateTag(), generateContent(content), tr);
    }

    public static void v(String content) {
        if (!isDebug) return;
        Log.v(generateTag(), generateContent(content));
    }

    public static void v(String content, Throwable tr) {
        if (!isDebug) return;
        Log.v(generateTag(), generateContent(content), tr);
    }

    public static void w(String content) {
        if (!isDebug) return;
        Log.w(generateTag(), generateContent(content));
    }

    public static void w(String content, Throwable tr) {
        if (!isDebug) return;
        Log.w(generateTag(), generateContent(content), tr);
    }

    public static void w(Throwable tr) {
        if (!isDebug) return;
        Log.w(generateTag(), tr);
    }

    public static void wtf(String content) {
        if (!isDebug) return;
        Log.wtf(generateTag(), generateContent(content));
    }

    public static void wtf(String content, Throwable tr) {
        if (!isDebug) return;
        Log.wtf(generateTag(), generateContent(content), tr);
    }

    public static void wtf(Throwable tr) {
        if (!isDebug) return;
        Log.wtf(generateTag(), tr);
    }


    private static String generateTag(){
        StackTraceElement caller = new Throwable().getStackTrace()[2];
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        String tag = TextUtils.isEmpty(customTagPrefix) ? callerClazzName : customTagPrefix + "-" + callerClazzName;
        return tag;
    }

    private static String generateContent(String content){
        StackTraceElement caller = new Throwable().getStackTrace()[2];
        return String.format(contentFormat,caller.getFileName(),caller.getLineNumber(),caller.getMethodName(),content);
    }
}
