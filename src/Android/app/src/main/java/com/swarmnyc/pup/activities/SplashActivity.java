package com.swarmnyc.pup.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.swarmnyc.pup.Consts;
import com.swarmnyc.pup.R;


public class SplashActivity extends Activity
{
	@Bind( R.id.pup_logo )
	ImageView logoImage;

	Handler m_handler = new Handler()
	{
		@Override
		public void handleMessage( final Message msg )
		{
			finish();
		}
	};

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_splash );
		ButterKnife.bind( this );

		int size = (int) ( Consts.windowWidth * 0.7 );
		logoImage.getLayoutParams().width = size;
		logoImage.getLayoutParams().height = size;
		( (RelativeLayout.LayoutParams) logoImage.getLayoutParams() ).setMargins( 0, (int) ( size * 0.1 ), 0, 0 );

		m_handler.sendEmptyMessageDelayed( 0, 3000 );
	}
}
