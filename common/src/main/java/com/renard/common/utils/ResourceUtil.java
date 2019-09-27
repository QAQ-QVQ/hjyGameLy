package com.renard.common.utils;

import android.content.Context;

import java.lang.reflect.Field;

/**
 * Created by Riven_rabbit on 2019/4/30
 *
 * @author Lemon酱
 */
public class ResourceUtil {
    public ResourceUtil() {
    }

    public static int getLayoutResIDByName(Context context, String name) {
        return context.getResources().getIdentifier(name, "layout", context.getPackageName());
    }

    public static int getIdResIDByName(Context context, String name) {
        return context.getResources().getIdentifier(name, "id", context.getPackageName());
    }

    public static int getStringResIDByName(Context context, String name) {
        return context.getResources().getIdentifier(name, "string", context.getPackageName());
    }

    public static int getDrawableResIDByName(Context context, String name) {
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }

    public static int getRawResIDByName(Context context, String name) {
        return context.getResources().getIdentifier(name, "raw", context.getPackageName());
    }

    public static int getAnimResIDByName(Context context, String name) {
        return context.getResources().getIdentifier(name, "anim", context.getPackageName());
    }

    public static int getStyleResIDByName(Context context, String name) {
        return context.getResources().getIdentifier(name, "style", context.getPackageName());
    }

    public static int getColorResIDByName(Context context, String name) {
        return context.getResources().getIdentifier(name, "color", context.getPackageName());
    }

    public static int getIntegerResIDByName(Context context, String name) {
        return context.getResources().getIdentifier(name, "integer", context.getPackageName());
    }

    public static int getBoolResIDByName(Context context, String name) {
        return context.getResources().getIdentifier(name, "bool", context.getPackageName());
    }

    public static int getDimenResIDByName(Context context, String name) {
        return context.getResources().getIdentifier(name, "dimen", context.getPackageName());
    }

    private static Object getResourceId(Context context, String name, String type) {
        String className = context.getPackageName() + ".R";

        try {
            Class e = Class.forName(className);
            Class[] var8;
            int var7 = (var8 = e.getClasses()).length;

            for(int var6 = 0; var6 < var7; ++var6) {
                Class childClass = var8[var6];
                String simple = childClass.getSimpleName();
                if(simple.equals(type)) {
                    Field[] var13;
                    int var12 = (var13 = childClass.getFields()).length;

                    for(int var11 = 0; var11 < var12; ++var11) {
                        Field field = var13[var11];
                        String fieldName = field.getName();
                        if(fieldName.equals(name)) {
                            System.out.println(fieldName);
                            return field.get((Object)null);
                        }
                    }
                }
            }
        } catch (Exception var15) {
            var15.printStackTrace();
        }

        return null;
    }

    public static int getStyleableResIDByName(Context context, String name) {
        return ((Integer)getResourceId(context, name, "styleable")).intValue();
    }

    public static int[] getStyleableArrayResIDByName(Context context, String name) {
        return (int[])getResourceId(context, name, "styleable");
    }
}
