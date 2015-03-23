package com.swarmnyc.pup.components;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final  class Utility {
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
    private Utility(){
    }

    public static JSONObject ToJson(InputStream is) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] data = new byte[2048];
        int len;
        while ((len = is.read(data, 0, data.length)) >= 0) {
            bos.write(data, 0, len);
        }

        return new JSONObject(new String(bos.toByteArray(), "UTF-8"));
    }

    public static Date getDateFromJsonString(String value) throws Exception {
        return sdf.parse(value.substring(0,value.length()-1) + "+00");
    }
}
