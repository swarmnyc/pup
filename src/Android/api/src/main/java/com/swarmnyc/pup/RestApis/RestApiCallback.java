package com.swarmnyc.pup.RestApis;


import com.swarmnyc.pup.EventBus;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.models.CurrentUserInfo;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RestApiCallback<T> implements Callback<T>
{
	private ServiceCallback callback;

	public RestApiCallback(ServiceCallback callback) {
		this.callback = callback;
	}

	@Override
	public void success(T t, Response response) {
		if (callback!=null)
			callback.success(t);
	}

	@Override
	public void failure( RetrofitError error )
	{
		if (callback!=null)
			callback.failure(null);

		EventBus.getBus().post( error );
	}
}

