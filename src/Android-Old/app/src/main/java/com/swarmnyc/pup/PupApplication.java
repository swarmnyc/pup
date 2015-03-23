package com.swarmnyc.pup;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.PushService;
import com.swarmnyc.pup.ui.MainActivity;
import com.uservoice.uservoicesdk.Config;
import com.uservoice.uservoicesdk.UserVoice;
import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * Created by somya on 11/1/14.
 */
public class PupApplication extends Application
{

	static PupApplication pupApplication = null;
	private ObjectGraph m_objectGraph;

	@Override public void onCreate()
	{

		pupApplication = this;

		m_objectGraph = ObjectGraph.create( new AppModule() );

		// Set this up once when your application launches
		Config config = new Config("swarmnyc.uservoice.com");
		config.setForumId(272754);
		// config.identifyUser("USER_ID", "User Name", "email@example.com");
		UserVoice.init(config, this);

		Parse.initialize(this, "hq5lLutpqWvuESKQPPaFf26TcvlbZLxYx1gzq39Q", "ovhfGhGum3MWzOVWEdY9ZMHqf5OzJET1nggCMaB7");
//		PushService.setDefaultPushCallback( this, MainActivity.class );


		super.onCreate();
	}

	public ObjectGraph getObjectGraph()
	{
		return m_objectGraph;
	}

	public ObjectGraph plus( Object o )
	{
		m_objectGraph = m_objectGraph.plus( o );
		return m_objectGraph;
	}

	public static PupApplication instance()
	{
		return pupApplication;
	}

	@Module ( library = true)
	class AppModule
	{
		@Provides @Singleton Application provideApplication()
		{
			return PupApplication.instance();
		}

	}
}


