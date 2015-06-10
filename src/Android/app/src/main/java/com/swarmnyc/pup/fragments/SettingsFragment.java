package com.swarmnyc.pup.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.swarmnyc.pup.*;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.Services.UserService;
import com.swarmnyc.pup.activities.MainActivity;
import com.swarmnyc.pup.components.FacebookHelper;
import com.swarmnyc.pup.components.PhotoHelper;
import com.swarmnyc.pup.components.Screen;
import com.swarmnyc.pup.components.TwitterHelper;

import javax.inject.Inject;

public class SettingsFragment extends Fragment implements Screen
{
	@Inject
	UserService m_userService;

	@InjectView( R.id.text_name )
	EditText m_nameText;

	@InjectView( R.id.img_portrait )
	ImageView m_portrait;

	@InjectView( R.id.switch_facebook )
	Switch m_fbSwitch;

	@InjectView( R.id.switch_twitter )
	Switch m_twSwitch;

	@Override
	public String toString()
	{
		return "Settings";
	}

	@Override
	public void onActivityResult( final int requestCode, final int resultCode, final Intent data )
	{
		super.onActivityResult( requestCode, resultCode, data );
		PhotoHelper.catchPhotoIntent( requestCode, resultCode, data );
	}

	@Override
	public View onCreateView(
		LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
	)
	{
		return inflater.inflate( R.layout.fragment_settings, container, false );
	}

	@Override
	public void onViewCreated( View view, Bundle savedInstanceState )
	{
		super.onViewCreated( view, savedInstanceState );
		ButterKnife.inject( this, view );
		PuPApplication.getInstance().getComponent().inject( this );
		MainActivity.getInstance().getToolbar().setTitle( R.string.label_settings );
		MainActivity.getInstance().getToolbar().setSubtitle( null );

		if ( StringUtils.isNotEmpty( User.current.getPortraitUrl() ) )
		{
			Picasso.with( this.getActivity() ).load( User.current.getPortraitUrl() ).into( m_portrait );
		}

		m_nameText.setText( User.current.getUserName() );

		m_fbSwitch.setChecked( User.current.hasMedium( Consts.KEY_FACEBOOK ) );
		m_twSwitch.setChecked( User.current.hasMedium( Consts.KEY_TWITTER ) );

	}

	@Override
	public void onStart()
	{
		super.onStart();
		MainDrawerFragment.getInstance().highLight( Consts.KEY_SETTINGS );
	}

	@Override
	public void onDestroy()
	{
		PhotoHelper.close();
		super.onDestroy();
	}

	@OnClick( {R.id.img_camera, R.id.img_portrait} )
	void choosePortrait()
	{
		PhotoHelper.startPhotoIntent(
			this, new AsyncCallback<Uri>()
			{
				@Override
				public void success( final Uri uri )
				{
					String path;

					m_portrait.setImageURI( uri );

					Cursor cursor = getActivity().getContentResolver().query(
						uri, null, null, null, null
					);

					if ( cursor == null )
					{
						path = uri.getPath();
					}
					else
					{
						cursor.moveToFirst();
						int idx = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
						path = cursor.getString( idx );
					}

					m_userService.updatePortrait(
						path, new ServiceCallback<String>()
						{
							@Override
							public void success( final String value )
							{
							    User.current.setPortraitUrl( value );
								User.update();
							}
						}
					);
				}
			}
		);
	}

	@OnClick( R.id.switch_facebook )
	void connectToFacebook()
	{
		if ( m_fbSwitch.isChecked() )
		{
			FacebookHelper.startLoginRequire(
				new AsyncCallback()
				{
					@Override
					public void failure()
					{
						m_fbSwitch.setChecked( false );
					}
				}
			);
		}
		else
		{
			m_userService.deleteFacebookToken(
				new ServiceCallback()
				{
					@Override
					public void success( final Object value )
					{
						User.removeMedium( Consts.KEY_FACEBOOK );
						Toast.makeText( getActivity(), R.string.message_disconnect_success, Toast.LENGTH_LONG ).show();
					}
				}
			);
		}
	}

	@OnClick( R.id.switch_twitter )
	void connectToTwitter()
	{
		if ( m_twSwitch.isChecked() )
		{
			TwitterHelper.startLoginRequire(
				new AsyncCallback()
				{
					@Override
					public void failure()
					{
						m_twSwitch.setChecked( false );
					}
				}
			);
		}
		else
		{
			m_userService.deleteTwitterToken(
				new ServiceCallback()
				{
					@Override
					public void success( final Object value )
					{
						User.removeMedium( Consts.KEY_TWITTER );
						Toast.makeText( getActivity(), R.string.message_disconnect_success, Toast.LENGTH_LONG ).show();
					}
				}
			);
		}
	}
}
