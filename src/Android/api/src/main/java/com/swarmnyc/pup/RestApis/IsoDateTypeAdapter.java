package com.swarmnyc.pup.RestApis;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.swarmnyc.pup.StringUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public final class IsoDateTypeAdapter extends TypeAdapter<Date>
{
	private final DateFormat iso8601Format;

	public IsoDateTypeAdapter()
	{
		iso8601Format = StringUtils.iso8601Format;
	}

	@Override
	public Date read( JsonReader in ) throws IOException
	{
		if ( in.peek() == JsonToken.NULL )
		{
			in.nextNull();
			return null;
		}
		return deserializeToDate( in.nextString() );
	}

	private synchronized Date deserializeToDate( String json )
	{
		try
		{
			return iso8601Format.parse( json );
		}
		catch ( ParseException e )
		{
			throw new JsonSyntaxException( json, e );
		}
	}

	@Override
	public synchronized void write( JsonWriter out, Date value ) throws IOException
	{
		if ( value == null )
		{
			out.nullValue();
			return;
		}
		String dateFormatAsString = iso8601Format.format( value );
		out.value( dateFormatAsString );
	}
}
