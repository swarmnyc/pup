package com.swarmnyc.pup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.swarmnyc.pup.RestApis.GameRestApi;
import com.swarmnyc.pup.RestApis.IsoDateTypeAdapter;
import com.swarmnyc.pup.RestApis.LobbyRestApi;
import com.swarmnyc.pup.RestApis.UserRestApi;
import com.swarmnyc.pup.Services.*;
import dagger.Module;
import dagger.Provides;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

import java.util.Date;

@Module
public class PuPApiModule
{
	private final RestAdapter restAdapter;

	public PuPApiModule()
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

	@Provides
	public LobbyService provideLobbyService()
	{
		return new LobbyServiceImpl( restAdapter.create( LobbyRestApi.class ) );
	}

	@Provides
	public GameService provideGameService()
	{
		return new GameServiceImpl( restAdapter.create( GameRestApi.class ) );
	}

	@Provides
	public UserService provideUserService()
	{
		return new UserServiceImpl( restAdapter.create( UserRestApi.class ) );
	}
}
