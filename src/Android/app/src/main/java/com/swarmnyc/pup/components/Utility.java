package com.swarmnyc.pup.components;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.swarmnyc.pup.Config;
import com.swarmnyc.pup.R;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class Utility
{
	static SimpleDateFormat sdf  = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US );
	static Gson             gson = new Gson();

	private Utility()
	{
	}

	public static JSONObject ToJson( InputStream is ) throws Exception
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] data = new byte[2048];
		int len;
		while ( ( len = is.read( data, 0, data.length ) ) >= 0 )
		{
			bos.write( data, 0, len );
		}

		return new JSONObject( new String( bos.toByteArray(), "UTF-8" ) );
	}

	public static Date getDateFromJsonString( String value ) throws Exception
	{
		return sdf.parse( value.substring( 0, value.length() - 1 ) + "+00" );
	}

	public static String toJson(final Object o)
	{
		return gson.toJson( o );
	}

	public static <T> T fromJson( final String json, final Class<T> classOfT ) throws JsonSyntaxException
	{return gson.fromJson( json, classOfT );}

	public static <T> T fromJson( final String json, final Type typeOfT ) throws JsonSyntaxException
	{return gson.fromJson( json, typeOfT );}

	public static String urlContent( final String url )
	{
		if ( url==null )
			return null;

		return url.replace( "~/", Config.getConfigString( R.string.PuP_Url ));
	}
}
