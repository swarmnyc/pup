package com.swarmnyc.pup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.swarmnyc.pup.RestApis.IsoDateTypeAdapter;

import java.util.Date;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class TestHelper {
    public static final String UserToken = "XW7yOdXrkYRCYj5CWWouYJX7uBGRXTFjmc5jJ3w9Zc96VOp6P2Qa8m8zky_aEjadWmkoH3NWMSb1AO3CFCdyKSU_zcXn6DJrBOSCFkbBGiE6brPmJQlZX_e7FFk80hwYWVfKGf-9tufxuPbJfVgIvpeRo-Sv5g_Xzl_cS-J66ueqX7noaeXA8meb1XeuJEZStkTe_bgC9pasWpp61WM0JGc1FnLrB9YeY0V_i3ur1gNnBk30ea_LJ9_mck1ZDzc-3SI3YNndpsn98QixsbLwxmKkshdskrDe0SUEgAASnKooxsYl9VbubgYM7Q_1FAcLIcJh1JNAQcb3kxF8pMogG-Z_B46S_l0nv3Xf-At0UrZ4shXwejIUsC6agoX2_5K-UrINA1ECzV_3y-GCVtpkymCxV-rNNCLP9caMPbkb-JCtL57vtqtCevNRpyo2Q05rNu8QxREGN8Sa9-NrfDKzxw";
    public static final String PUP_API_URL = "http://pup.azurewebsites.net/api/";
    private static RestAdapter restAdapter;

    public static <T> T getRestApi(Class<T> cla) {
        if (restAdapter == null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new IsoDateTypeAdapter())
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

            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        }

        return restAdapter.create(cla);
    }

}
