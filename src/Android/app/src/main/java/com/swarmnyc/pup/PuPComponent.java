package com.swarmnyc.pup;

import com.swarmnyc.pup.Services.UserService;
import com.swarmnyc.pup.activities.MainActivity;
import com.swarmnyc.pup.activities.SplashActivity;
import com.swarmnyc.pup.adapters.ChatAdapter;
import com.swarmnyc.pup.adapters.MyChatAdapter;
import com.swarmnyc.pup.fragments.*;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = PuPApiModule.class)
public interface PuPComponent {
    UserService getUserService();

    void inject(MainActivity mainActivity);

    void inject(SplashActivity splashActivity);

    void inject(LobbyListFragment lobbyListFragment);

    void inject(CreateLobbyFragment createLobbyFragment);

	void inject( LobbyFragment lobbyFragment );

	void inject( RegisterDialogFragment registerDialogFragment );

    void inject( ChatAdapter chatAdapter );

    void inject( MyChatAdapter myChatAdapter );

    void inject( SettingsFragment settingsFragment );
}
