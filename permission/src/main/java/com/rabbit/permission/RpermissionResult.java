package com.rabbit.permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * desc  ${DESC}
 * author wangyongkui
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpermissionResult {
    boolean permissionResult();
    int requestCode();

}
