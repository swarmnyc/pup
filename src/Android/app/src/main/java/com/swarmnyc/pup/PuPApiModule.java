package com.swarmnyc.pup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.swarmnyc.pup.chat.ChatService;
import com.swarmnyc.pup.chat.MockChatService;
import com.swarmnyc.pup.chat.QuickbloxChatService;

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
                .setConverter(new GsonConverter(gson))
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

    @Provides
    @Singleton
    public GameService provideGameService() {
        return restAdapter.create(GameService.class);
    }

    @Provides
    @Singleton
    public ChatService provideChatService() {
        return new MockChatService();
        //return new QuickbloxChatService();
    }
}
