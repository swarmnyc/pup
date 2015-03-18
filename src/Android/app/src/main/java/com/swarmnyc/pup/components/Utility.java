package com.swarmnyc.pup.components;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final  class Utility {
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
}
