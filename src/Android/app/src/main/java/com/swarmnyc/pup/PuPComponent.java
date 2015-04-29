package com.swarmnyc.pup;

import com.swarmnyc.pup.activities.AuthActivity;
import com.swarmnyc.pup.activities.LobbyActivity;
import com.swarmnyc.pup.activities.MainActivity;
import com.swarmnyc.pup.activities.SplashActivity;
import com.swarmnyc.pup.fragments.*;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = PuPApiModule.class)
public interface PuPComponent {
    void inject(LobbyListFragment lobbyListFragment);

    void inject(LobbyActivity lobbyActivity);

    void inject(LoginFragment loginFragment);

    void inject(AuthActivity authActivity);

    void inject(MainActivity mainActivity);

    void inject(SplashActivity splashActivity);

    void inject(CreateLobbyFragment createLobbyFragment);

	void inject( LobbyFragment lobbyFragment );

	void inject( SignupFragment signupFragment );
}
