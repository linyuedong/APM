package com.example.apm.aop.db;

/**
 * Created by Nelson on 2019-12-10.
 */
public class Util {

    public static <T> T checkNotNull(T object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }
}
