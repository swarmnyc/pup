package com.swarmnyc.pup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.swarmnyc.pup.RestApis.GameRestApi;
import com.swarmnyc.pup.RestApis.IsoDateTypeAdapter;
import com.swarmnyc.pup.RestApis.LobbyRestApi;
import com.swarmnyc.pup.RestApis.UserRestApi;
import com.swarmnyc.pup.Services.GameService;
import com.swarmnyc.pup.Services.GameServiceImpl;
import com.swarmnyc.pup.Services.LobbyService;
import com.swarmnyc.pup.Services.LobbyServiceImpl;
import com.swarmnyc.pup.chat.ChatService;
import com.swarmnyc.pup.chat.MockChatService;

import java.util.Date;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

@Module
public class PuPApiModule {
    private final RestAdapter restAdapter;

    public PuPApiModule() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new IsoDateTypeAdapter())
                .create();

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(Config.getConfigString(R.string.PuP_API_Url))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        if (User.isLoggedIn()) {
                            request.addHeader("Authorization", "Bearer " + User.current.getAccessToken());
                        }
                        }
                })
	            .setLogLevel( BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE )
                .setConverter( new GsonConverter( gson ) )
                .build();
    }

    @Provides
    @Singleton
    public UserRestApi provideUserService() {
        return restAdapter.create(UserRestApi.class);
    }

    @Provides
    @Singleton
    public LobbyService provideLobbyService() {
        return new LobbyServiceImpl(restAdapter.create(LobbyRestApi.class));
    }

    @Provides
    @Singleton
    public GameService provideGameService() {
        return new GameServiceImpl(restAdapter.create(GameRestApi.class));
    }

    @Provides
    @Singleton
    public ChatService provideChatService() {
        return new MockChatService();
        //return new QuickbloxChatService();
    }
}
