package com.wuxinle.touchimageview.utils;

import android.util.Log;

/**
 * Log统一管理类
 *
 * @author wuxin
 */
public class L {

    private L() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isDebug = true;// 是否需要打印bug，可以在application的onCreate函数里面初始化
    private static final String TAG = "wuxin";

    // 下面四个是默认tag的函数
    public static void i(String msg) {
        if (isDebug)
            Log.i(TAG, msg);
    }

    public static void d(String msg) {
        if (isDebug)
            Log.d(TAG, msg);
    }

    public static void e(String msg) {
        if (isDebug)
            Log.e(TAG, msg);
    }

    public static void v(String msg) {
        if (isDebug)
            Log.v(TAG, msg);
    }

    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg) {
        if (isDebug)
            Log.i(TAG + tag, msg);
    }

    public static void d(String tag, String msg) {
        if (isDebug)
            Log.i(TAG + tag, msg);
    }

    public static void e(String tag, String msg) {
        if (isDebug)
            Log.i(TAG + tag, msg);
    }

    public static void v(String tag, String msg) {
        if (isDebug)
            Log.i(TAG + tag, msg);
    }

    public static void okmeg(String tag, String response){

        StringBuilder sb = new StringBuilder();
        sb.append(tag).append(response);
        //由于logcat字数限制，当一条文本过长时，拆成多条信息log
        if (sb.length() > 800) {
            for (int i = 0; i < sb.length(); i += 800) {
                int end = i + 800;
                if (end > sb.length()) {
                    end = sb.length();
                }
                L.v(sb.substring(i, end) + "\n");
            }
        } else {
            L.v(sb.toString());
        }
    }

    public static String getDefaultTag() {
        return TAG;
    }
}