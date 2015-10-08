package com.szn.jukebox.utils;

/**
 * Created by Julien Sezn on 05/10/2015.
 * For Infogene
 */
public class StringUtils {


    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }
}
