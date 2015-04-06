package com.swarmnyc.pup;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;

import java.util.Date;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class TestHelper {
    public static final String UserToken = "owaUfV1P1cRbuRhX9gb8YZ_P8Y_FK0TnK1ph1WagU0oYW7Pe_d3lRFukt6iGi5rrJj0H_ayN30qp_qNpzhQIKGel6Z0BT-j930NsTnP8pOoynI4Na4MBsw_i9sCmFqZ_VkEt_kRqBMCFsCrfAPYXnutZz6TSdVCP25wf_luLTH4ExTPsyl7wLDIWbxgKZ6-NuZGr3x0DMoK-lrrarXZPFFFZVGW_3CbMp_hDcXnoFVHCkasgPxO5SKBriE3IWi88JCV7DetoXxkTFDc-T_j_6iKRSfb1M6L194iChoMWGiVsbdvz56JvKo8ftlDz08GklkfuFKndAfmKYpMcdZIR015e9vhGfx7xPs7PIDPAX5UXJxNJNHWxg18kwqxAkR2sBO62sNeCittIoTEJ0iULm2Q1tDUdHqJZMK65ytqqGhsr0H84-X7UWjIaCilD4UKgH6MFcRFeyRwRUe_JE5A9e5YVu62PiijY2Hb0hRv34ps";
    public static final String PUP_API_URL = "http://pup.azurewebsites.net/api/";
    private static RestAdapter restAdapter;

    public static <T> T getService(Class<T> cla){
        if (restAdapter==null){
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new DateTypeAdapter())
                    .create();


            restAdapter = new RestAdapter.Builder()
                    .setEndpoint(PUP_API_URL)
                    .setRequestInterceptor(new RequestInterceptor() {
                        @Override
                        public void intercept(RequestFacade request) {
                            request.addHeader("Authorization", "Bearer " + UserToken);

                        }
                    })
                    .setConverter(new GsonConverter(gson))
                    .build();
        }

        return restAdapter.create(cla);
    }

}
