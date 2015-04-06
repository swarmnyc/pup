package com.swarmnyc.pup;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

@Module
public class PuPApiModule {
    private final RestAdapter restAdapter;

    public PuPApiModule() {
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(Config.getConfigString(R.string.PuP_API_Url))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        if (Config.isLoggedIn()) {
                            request.addHeader("Authorization", "Bearer " + Config.getUserToken());
                        }
                    }
                })
                .build();
    }

    @Provides
    @Singleton
    public UserService provideUserService() {
        return restAdapter.create(UserService.class);
    }

    @Provides
    @Singleton
    public LobbyService provideLobbyService() {
        return restAdapter.create(LobbyService.class);
    }
}
