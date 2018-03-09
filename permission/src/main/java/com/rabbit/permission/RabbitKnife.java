package com.rabbit.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * desc  ${DESC}
 * author wangyongkui
 */
public class RabbitKnife {
    public static void initPromissions(Activity targetActivity) {
        if (null == targetActivity || !(targetActivity instanceof Activity)) return;
        Method[] fields = targetActivity.getClass().getMethods();

        if (null == fields || 0 == fields.length) return;
        for (Method field : fields) {
            if (field.isAnnotationPresent(Rpermission.class)) {
                Rpermission rpermission = field.getAnnotation(Rpermission.class);
                if (null == rpermission) continue;

                String[] permissions = rpermission.permission();
                List<String> permissionss = new ArrayList<>();
//                for (String s : permissions) {
                    int result = ContextCompat.checkSelfPermission(targetActivity, permissions[0]);
                    if (result != PackageManager.PERMISSION_GRANTED) {//需要
                        permissionss.add(permissions[0]);
                    } else {//已经存在

                        initPromissionsResult1(targetActivity,true);
                        break;
                    }
//                }
                String[] ps = new String[permissionss.size()];
                permissionss.toArray(ps);
                if (ps.length > 0)
                    ActivityCompat.requestPermissions(targetActivity, ps, 1111);
            }
        }
//        Rpermission rpermission = targetActivity.getClass().getAnnotation(Rpermission.class);
//        if (null != rpermission) {
//            String[] permissions = rpermission.permission();
//            List<String> requests = new ArrayList<>();
//            for (String s : permissions) {
//                int result = ContextCompat.checkSelfPermission(targetActivity, s);
//                if (result != PackageManager.PERMISSION_GRANTED) {
//                    requests.add(s);
//                }
//            }
//            String[] ps = new String[requests.size()];
//            requests.toArray(ps);
//            if (permissions.length > 0)
//                ActivityCompat.requestPermissions(targetActivity, ps, 1);
//
//
//        }


    }


    public static void initPromissionsResult(Activity targetActivity, String[] permissions, int[] grantResults) {
        if (null == targetActivity || !(targetActivity instanceof Activity)) return;


    }


    public static void initPromissionsResult(Activity targetActivity, boolean permissions) {
        if (null == targetActivity || !(targetActivity instanceof Activity)) return;
        Class clazz = targetActivity.getClass();

        Method[] methods = clazz.getMethods();
        if (null == methods || 0 == methods.length) return;
        for (Method method : methods) {
            if (method.isAnnotationPresent(RpermissionResult.class)) {
                RpermissionResult rpermissionResult = method.getAnnotation(RpermissionResult.class);
                if (permissions == rpermissionResult.permissionResult()) {

                    try {
                        method.setAccessible(true);
                        method.invoke(targetActivity);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }


}
    public static void initPromissionsResult1(Activity targetActivity, boolean permissions) {
        if (null == targetActivity || !(targetActivity instanceof Activity)) return;
        Class clazz = targetActivity.getClass();

        Method[] methods = clazz.getMethods();
        if (null == methods || 0 == methods.length) return;
        for (Method method : methods) {
            if (method.isAnnotationPresent(RpermissionResult.class)) {
                RpermissionResult rpermissionResult = method.getAnnotation(RpermissionResult.class);
                if (permissions == rpermissionResult.permissionResult()) {

                    try {
                        method.setAccessible(true);
                        method.invoke(targetActivity);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }
    }