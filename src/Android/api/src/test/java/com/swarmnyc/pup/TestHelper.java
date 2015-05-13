package com.swarmnyc.pup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.swarmnyc.pup.RestApis.IsoDateTypeAdapter;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.swarmnyc.pup.RestApis.RestApiCallback;
import com.swarmnyc.pup.RestApis.UserRestApi;
import com.swarmnyc.pup.models.LoggedInUser;
import org.junit.Assert;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

import javax.naming.AuthenticationException;

public class TestHelper
{
	public static String UserToken;
	public static final String PUP_API_URL = "http://pup.azurewebsites.net/api/";
	private static RestAdapter restAdapter;

	public static <T> T getRestApi( Class<T> cla )
	{
		if ( restAdapter == null )
		{
			Gson gson = new GsonBuilder().registerTypeAdapter( Date.class, new IsoDateTypeAdapter() ).create();


			restAdapter = new RestAdapter.Builder().setEndpoint( PUP_API_URL ).setRequestInterceptor(
				new RequestInterceptor()
				{
					@Override
					public void intercept( RequestFacade request )
					{
						if ( UserToken != null )
						{ request.addHeader( "Authorization", "Bearer " + UserToken ); }
					}
				}
			).setConverter( new GsonConverter( gson ) )

			                                       .build();

			restAdapter.setLogLevel( RestAdapter.LogLevel.FULL );
		}

		return restAdapter.create( cla );
	}

	public static void ensureLoggedin() throws Exception
	{
		if ( UserToken!=null )
			return;

		UserRestApi userRestApi = getRestApi( UserRestApi.class );
		final CountDownLatch signal = new CountDownLatch( 1 );

		userRestApi.login(
			"hello@swarmnyc.com", "swarmnyc", new RestApiCallback<LoggedInUser>()
			{
				@Override
				public void success( LoggedInUser loggedInUser, Response response )
				{
					UserToken = loggedInUser.getAccessToken();
					signal.countDown();
				}

				@Override
				public void failure( final RetrofitError error )
				{
					signal.countDown();
				}
			}
		);

		signal.await();

		if ( UserToken == null )
		{
			throw new Exception( "Login Failure" );
		}
	}

}
