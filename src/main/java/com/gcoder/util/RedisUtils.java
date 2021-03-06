package com.gcoder.util;

import java.nio.charset.Charset;

/**
 * Created by gcoder on 2017/6/19.
 */
public final class RedisUtils {

    private static final String CHARSET_UTF8 = "UTF-8";

    public static final byte[] getKey(String... key) {
        return getStringKey(key).getBytes(Charset.forName(CHARSET_UTF8));
    }
    
    public static final String getStringKey(String... key) {
    	String strKey = "";
        for (int i = 0; i < key.length; i++) {
            strKey = strKey.concat(key[i]).concat(":");
        }
        return strKey;
    }
    
}
