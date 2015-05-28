package com.swarmnyc.pup.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.swarmnyc.pup.Consts;
import com.swarmnyc.pup.R;


public class SplashActivity extends Activity
{
	@InjectView( R.id.pup_logo )
	ImageView logoImage;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView(  R.layout.activity_splash );
		ButterKnife.inject( this );

		int size = (int) ( Consts.windowWidth * 0.7 );
		logoImage.getLayoutParams().width = size;
		logoImage.getLayoutParams().height = size;
		( (RelativeLayout.LayoutParams) logoImage.getLayoutParams() ).setMargins( 0, (int) ( size * 0.1 ), 0, 0 );
	}

	@OnClick(R.id.container)
	void close(){
		finish();
	}
}
