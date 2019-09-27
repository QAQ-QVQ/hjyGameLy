package com.renard.common.parse.plugin;

import com.renard.common.utils.KLog;

import java.lang.reflect.Method;

/**
 * Created by Riven_rabbit on 2019/4/24
 *
 * @author Lemon酱
 */
public class PluginReflectApi {
    protected Object invoke(String methodName){
        try {
            Method method = this.getClass().getMethod(methodName);
            method.setAccessible(true); //设置访问私有方法
            return method.invoke(this);
        } catch (Exception e) {
            try {
                Method method = this.getClass().getDeclaredMethod(methodName);
                method.setAccessible(true);//设置访问私有方法
                return method.invoke(this);
            } catch (Exception e1) {
                e1.printStackTrace();
                KLog.d(this.getClass().getSimpleName() + ",没有实现该方法： " + methodName);
                return null;
            }
        }
    }

    protected Object invoke(String methodName, Class<?>[] argTypes, Object[] args) {
        try {
            Method method = this.getClass().getMethod(methodName, argTypes);
            method.setAccessible(true);//设置访问私有方法
            return method.invoke(this, args);
        } catch (Exception e) {
            try {
                Method method = this.getClass().getDeclaredMethod(methodName, argTypes);
                method.setAccessible(true);//设置访问私有方法
                return method.invoke(this, args);
            } catch (Exception e1) {
                e1.printStackTrace();
                KLog.d(this.getClass().getSimpleName() + ",没有实现该方法： " + methodName);
                return null;
            }
        }
    }

    protected Object invoke(Object mainObject, String methodName) {
        Class<?> clazz = mainObject.getClass();
        try {
            Method method = clazz.getMethod(methodName);
            method.setAccessible(true);//设置访问私有方法
            return method.invoke(mainObject);
        } catch (Exception e) {
            try {
                Method method = clazz.getDeclaredMethod(methodName);
                method.setAccessible(true);//设置访问私有方法
                return method.invoke(mainObject);
            }catch (Exception e1){
                e1.printStackTrace();
                KLog.d(mainObject.getClass().getSimpleName() + ",没有实现该方法： " + methodName);
                return null;
            }
        }
    }

    protected Object invoke(Object mainObject, String methodName, Class[] paramTypes, Object[] objects) {
        Class<?> clazz = mainObject.getClass();
        try {
            Method method = clazz.getMethod(methodName, paramTypes);
            method.setAccessible(true);//设置访问私有方法
            return method.invoke(mainObject, objects);
        } catch (Exception e) {
            try {
                Method method = clazz.getDeclaredMethod(methodName, paramTypes);
                method.setAccessible(true);//设置访问私有方法
                return method.invoke(mainObject, objects);
            } catch (Exception e1) {
                e1.printStackTrace();
                KLog.d(mainObject.getClass().getSimpleName() + ",没有实现该方法： " + methodName);
                return null;
            }
        }
    }
}
