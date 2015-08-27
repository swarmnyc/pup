package com.swarmnyc.pup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.swarmnyc.pup.module.restapi.GameRestApi;
import com.swarmnyc.pup.module.restapi.IsoDateTypeAdapter;
import com.swarmnyc.pup.module.restapi.LobbyRestApi;
import com.swarmnyc.pup.module.restapi.UserRestApi;
import com.swarmnyc.pup.module.service.*;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

import java.util.Date;


public class PuPModule
{
	private final RestAdapter restAdapter;

	public PuPModule()
	{
		Gson gson = new GsonBuilder().registerTypeAdapter( Date.class, new IsoDateTypeAdapter() ).create();

		restAdapter = new RestAdapter.Builder().setEndpoint( Config.getConfigString( R.string.PuP_Url ) + "api/" )
		                                       .setRequestInterceptor(
			                                       new RequestInterceptor()
			                                       {
				                                       @Override
				                                       public void intercept( RequestFacade request )
				                                       {
					                                       if ( User.isLoggedIn() )
					                                       {
						                                       request.addHeader(
							                                       "Authorization",
							                                       "Bearer "
							                                       + User.current.getAccessToken()
						                                       );
					                                       }
				                                       }
			                                       }
		                                       )
		                                       .setLogLevel(
			                                       BuildConfig.ENABLE_LOG_REFROFIT
			                                       ? RestAdapter.LogLevel.FULL
			                                       : RestAdapter.LogLevel.NONE
		                                       )
		                                       .setConverter( new GsonConverter( gson ) )
		                                       .build();
	}

	public LobbyService provideLobbyService()
	{
		return new LobbyServiceImpl( restAdapter.create( LobbyRestApi.class ) );
	}

	public GameService provideGameService()
	{
		return new GameServiceImpl( restAdapter.create( GameRestApi.class ) );
	}

	public UserService provideUserService()
	{
		return new UserServiceImpl( restAdapter.create( UserRestApi.class ) );
	}
}
