package com.example.apm.aop;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.EventListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@Aspect
public class OkHttp3Aspect {

    private static final String TAG = "OkHttp3Aspect";


    ///这种情况只有获取的时候才会调用
    @Around("get(okhttp3.EventListener.Factory okhttp3.OkHttpClient.eventListenerFactory)")
    public Object aroundEventFactoryGet(ProceedingJoinPoint joinPoint) throws Throwable {
        Log.e(TAG, "aroundEventFactoryGet:1 ");
        Object target = joinPoint.proceed();
        if (target instanceof EventListener.Factory) {
            Log.e(TAG, "aroundEventFactoryGet:2 ");
            EventListener.Factory factory = (EventListener.Factory) target;
            return OkHttpEventListenerFactoryWrapper.wrap(factory);
        }
        return target;
    }
/*
OKhttp有两种调用方式：
1、new Thread() {
        @Override
        public void run() {
            super.run();
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build();
            Request request = new Request.Builder()
                    .url(GET_URL)
                    .build();

            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String string = response.body().string();
                    Log.i(TAG, "asyncGetRequest onResponse: ");
                }
            });
        }
    }.start();

        2、new Thread(new Runnable() {
        @Override
        public void run() {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(GET_URL)
                    .build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
                String responseBody = response.body().string();
                Log.i(TAG, "asyncGetRequest: " + responseBody);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }).start();
 */

    ///只能hook OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build();无法hook OkHttpClient client = new OkHttpClient();
    @Pointcut("call(public okhttp3.OkHttpClient build())")
    public void build() {

    }

    @Around("build()")
    public Object aroundBuild(ProceedingJoinPoint joinPoint) throws Throwable {
        Object target = joinPoint.getTarget();
        if (target instanceof OkHttpClient.Builder) {
            OkHttpClient.Builder builder = (OkHttpClient.Builder) target;
            Object value = getValue(builder, builder.getClass(), "eventListenerFactory");
            if (value != null) {
                if (value instanceof EventListener.Factory) {
                    EventListener.Factory factory = (EventListener.Factory) value;
                    //适配原本存在的EventListenerFactory，如果直接使用builder.eventListenerFactory(OkHttpEventListener.FACTORY)
                    // 会导致原本的EventListenerFactory被覆盖，影响原本功能
                    builder.eventListenerFactory(OkHttpEventListenerFactoryWrapper.wrap(factory));
                }
            } else {
                builder.eventListenerFactory(OkHttpEventListener.FACTORY);
            }

        }
        Object proceedObject = joinPoint.proceed();
        return proceedObject;
    }

    ///能hook OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build();和OkHttpClient client = new OkHttpClient();
    @Pointcut("call(okhttp3.OkHttpClient.new(..))")
    public void OkHttpClientConstructor() {
        System.out.println("OkHttpClientConstructor");
    }

    @Around("OkHttpClientConstructor()")
    public Object OkHttpClientConstructor(ProceedingJoinPoint joinPoint) throws Throwable {
        Object proceedObject = joinPoint.proceed();
        if (proceedObject instanceof OkHttpClient) {
            OkHttpClient okHttpClient = (OkHttpClient) proceedObject;
            EventListener.Factory factory = okHttpClient.eventListenerFactory();
            EventListener.Factory wrap = OkHttpEventListenerFactoryWrapper.wrap(factory);
            setValue(okHttpClient, okHttpClient.getClass(), "eventListenerFactory", wrap);

        }
        return proceedObject;
    }

    public static Object getValue(@Nullable Object source, @NonNull Class<?> target,
                                  @NonNull String name) {
        Field field = null;
        Object value = null;
        try {
            field = target.getDeclaredField(name);
            field.setAccessible(true); // 如果字段是私有的，需要设置访问权限
            value = field.get(source);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static boolean setValue(@Nullable Object source, @NonNull Class<?> target,
                                   @NonNull String name, @Nullable Object value) {
        Field field = null;
        int modify = 0;
        Field modifiersField = null;
        boolean removeFinal = false;
        try {
            field = target.getDeclaredField(name);
            modify = field.getModifiers();
            //final修饰的基本类型不可修改
            if (field.getType().isPrimitive() && Modifier.isFinal(modify)) {
                return false;
            }
            //获取访问权限
            if (!Modifier.isPublic(modify) || Modifier.isFinal(modify)) {
                field.setAccessible(true);
            }
            //static final同时修饰
            removeFinal = Modifier.isStatic(modify) && Modifier.isFinal(modify);
            if (removeFinal) {
                modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, modify & ~Modifier.FINAL);
            }
            //按照类型调用设置方法
            if (value != null && field.getType().isPrimitive()) {
                if ("int".equals(field.getType().getName()) && value instanceof Number) {
                    field.setInt(source, ((Number) value).intValue());
                } else if ("boolean".equals(field.getType().getName()) && value instanceof Boolean) {
                    field.setBoolean(source, (Boolean) value);
                } else if ("byte".equals(field.getType().getName()) && value instanceof Byte) {
                    field.setByte(source, (Byte) value);
                } else if ("char".equals(field.getType().getName()) && value instanceof Character) {
                    field.setChar(source, (Character) value);
                } else if ("double".equals(field.getType().getName()) && value instanceof Number) {
                    field.setDouble(source, ((Number) value).doubleValue());
                } else if ("long".equals(field.getType().getName()) && value instanceof Number) {
                    field.setLong(source, ((Number) value).longValue());
                } else if ("float".equals(field.getType().getName()) && value instanceof Number) {
                    field.setFloat(source, ((Number) value).floatValue());
                } else if ("short".equals(field.getType().getName()) && value instanceof Number) {
                    field.setShort(source, ((Number) value).shortValue());
                } else {
                    return false;
                }
            } else {
                field.set(source, value);
            }
        } catch (Exception e) {
            return false;
        } finally {
            try {
                //权限还原
                if (field != null) {
                    if (removeFinal && modifiersField != null) {
                        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                        modifiersField.setAccessible(false);
                    }
                    if (!Modifier.isPublic(modify) || Modifier.isFinal(modify)) {
                        field.setAccessible(false);
                    }
                }
            } catch (IllegalAccessException e) {
                //
            }
        }
        return true;
    }

}
