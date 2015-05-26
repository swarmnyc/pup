package com.swarmnyc.pup;

import com.swarmnyc.pup.activities.MainActivity;
import com.swarmnyc.pup.activities.SplashActivity;
import com.swarmnyc.pup.fragments.CreateLobbyFragment;
import com.swarmnyc.pup.fragments.LobbyFragment;
import com.swarmnyc.pup.fragments.LobbyListFragment;
import com.swarmnyc.pup.fragments.RegisterFragment;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = PuPApiModule.class)
public interface PuPComponent {
    void inject(MainActivity mainActivity);

    void inject(SplashActivity splashActivity);

    void inject(LobbyListFragment lobbyListFragment);

    void inject(CreateLobbyFragment createLobbyFragment);

	void inject( LobbyFragment lobbyFragment );

	void inject( RegisterFragment registerFragment );
}
