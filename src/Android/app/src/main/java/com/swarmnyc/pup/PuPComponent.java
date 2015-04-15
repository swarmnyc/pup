package com.swarmnyc.pup;

import com.swarmnyc.pup.activities.AuthActivity;
import com.swarmnyc.pup.activities.CreateLobbyActivity;
import com.swarmnyc.pup.activities.LobbyActivity;
import com.swarmnyc.pup.fragments.LobbyListFragment;
import com.swarmnyc.pup.fragments.LoginFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = PuPApiModule.class)
public interface PuPComponent {
    void inject(LobbyListFragment lobbyListFragment);

    void inject(CreateLobbyActivity createLobbyActivity);

    void inject(LobbyActivity lobbyActivity);

    void inject(LoginFragment loginFragment);

    void inject(AuthActivity authActivity);
}
