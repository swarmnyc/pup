package com.swarmnyc.pup.RestApis;


import com.swarmnyc.pup.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;

public abstract class RestApiCallback<T> implements Callback<T>
{
	@Override
	public void failure( RetrofitError error )
	{
		error.printStackTrace();
		EventBus.getBus().post( error );
	}
}

