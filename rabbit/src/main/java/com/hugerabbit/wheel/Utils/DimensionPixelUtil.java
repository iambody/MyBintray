package com.hugerabbit.wheel.Utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

/**
 * desc  ${DESC}
 * author wangyongkui
 */
public class DimensionPixelUtil {
    public final static int PX = TypedValue.COMPLEX_UNIT_PX;
    public final static int DIP = TypedValue.COMPLEX_UNIT_DIP;
    public final static int SP = TypedValue.COMPLEX_UNIT_SP;

    /**
     *
     * @param unit
     *            单位 </br>0 px</br>1 dip</br>2 sp
     * @param value
     *            size 大小
     * @param context
     * @return
     */
    public static float getDimensionPixelSize(int unit, float value,
                                              Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        switch (unit) {
            case PX:
                return value;
            case DIP:
            case SP:
                return TypedValue.applyDimension(unit, value, metrics);
            default:
                throw new IllegalArgumentException("unknow unix");
        }
    }

    /**
     * 根据手机的屏幕属性从 dip 的单位 转成为 px(像素)
     *
     * @param context
     * @param value
     * @return
     */
    public static int dip2px(Context context, float value) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) (value * metrics.density);
    }
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context,float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    /**
     * 根据手机的屏幕属性从 px(像素) 的单位 转成为 dip
     *
     * @param context
     * @param value
     * @return
     */
    public static float px2dip(Context context, float value) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return value / metrics.density;
    }

    /**
     * 根据手机的屏幕属性从 sp的单位 转成为px(像素)
     *
     * @param context
     * @param value
     * @return
     */
    public static float sp2px(Context context, float value) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return value * metrics.scaledDensity;
    }

    /**
     * 根据手机的屏幕属性从 px(像素) 的单位 转成为 sp
     *
     * @param context
     * @param value
     * @return
     */
    public static float px2sp(Context context, float value) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return value / metrics.scaledDensity;
    }
    /**
     * 获取手机的宽度
     * @return
     */
    public static int getDisplayWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕信息
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        return screenWidth;
    }

    /**
     * 获取屏幕的宽度
     *
     * @param context context
     * @return int
     */
    public static int getScreenWidth(@NonNull final Context context) {
        return context.getApplicationContext().getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param ctx context
     * @return int
     */
    public static int getScreenHeight(@NonNull Context ctx) {
        return ctx.getApplicationContext().getResources().getDisplayMetrics().heightPixels;
    }
}
