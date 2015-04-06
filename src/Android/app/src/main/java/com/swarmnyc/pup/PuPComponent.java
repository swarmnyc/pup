package com.swarmnyc.pup;

import com.swarmnyc.pup.fragments.LobbyListFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = PuPApiModule.class)
public interface PuPComponent {
    void inject(LobbyListFragment lobbyListFragment);
}
