package com.swarmnyc.pup.ui.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.swarmnyc.pup.*;
import com.swarmnyc.pup.module.service.ServiceCallback;
import com.swarmnyc.pup.module.service.UserService;
import com.swarmnyc.pup.utils.AsyncCallback;
import com.swarmnyc.pup.utils.StringUtils;
import com.swarmnyc.pup.ui.helpers.FacebookHelper;
import com.swarmnyc.pup.ui.helpers.PhotoHelper;
import com.uservoice.uservoicesdk.UserVoice;


public class SettingsFragment extends BaseFragment
{
	UserService m_userService;

	@Bind( R.id.text_name )
	EditText m_nameText;

	@Bind( R.id.img_portrait )
	ImageView m_portrait;

	@Bind( R.id.switch_facebook )
	Switch m_fbSwitch;

	@Bind( R.id.switch_twitter )
	Switch m_twitterSwitch;

	@Bind( R.id.switch_reddit )
	Switch m_redditSwitch;

	@Bind( R.id.switch_tumblr )
	Switch m_tumblrSwitch;

	@Bind(R.id.text_tos)
	TextView m_tosText;

	@Override
	public String getScreenName()
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
		ButterKnife.bind( this, view );
		m_userService = PuPApplication.getInstance().getModule().provideUserService();

		if ( StringUtils.isNotEmpty(User.current.getPortraitUrl()) )
		{
			Picasso.with( this.getActivity() ).load( User.current.getPortraitUrl() ).into( m_portrait );
		}

		m_nameText.setText( User.current.getUserName() );

		m_fbSwitch.setChecked( User.current.hasSocialMedium(Consts.KEY_FACEBOOK) );
		m_twitterSwitch.setChecked( User.current.hasSocialMedium(Consts.KEY_TWITTER) );
		m_redditSwitch.setChecked( User.current.hasSocialMedium(Consts.KEY_REDDIT) );
		m_tumblrSwitch.setChecked( User.current.hasSocialMedium(Consts.KEY_TUMBLR) );

		m_tosText.setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	public void onResume()
	{
		super.onResume();
	}


	@Override
	public void updateTitle()
	{
		setTitle( R.string.label_settings );
		setSubtitle(null);
	}

	@Override
	public void onDestroy()
	{
		PhotoHelper.close();
		super.onDestroy();
	}

	@OnClick(R.id.text_feedback)
	void feedback(){
		UserVoice.launchPostIdea(this.getActivity());
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

					if ( StringUtils.isNotEmpty( User.current.getPortraitUrl() ) )
					{
						Picasso.with( getActivity() ).invalidate( User.current.getPortraitUrl() );
					}

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

					Toast.makeText( getActivity(), "Updating", Toast.LENGTH_LONG ).show();
					m_userService.updatePortrait(
						path, new ServiceCallback<String>()
						{
							@Override
							public void success( final String value )
							{
								Toast.makeText( getActivity(), "Updated", Toast.LENGTH_LONG ).show();
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
			FacebookHelper.startLoginRequire(getActivity(),
				new AsyncCallback()
				{
					@Override
					public void success()
					{
						Toast.makeText( getActivity(), R.string.message_connect_success, Toast.LENGTH_LONG )
						     .show();
					}

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
			m_userService.deleteMedium(
				Consts.KEY_FACEBOOK, new ServiceCallback<String>()
				{
					@Override
					public void success( final String value )
					{
						User.removeSocialMedium(Consts.KEY_FACEBOOK);
						Toast.makeText( getActivity(), R.string.message_disconnect_success, Toast.LENGTH_LONG ).show();
					}
				}
			);
		}
	}

	@OnClick( R.id.switch_twitter )
	void connectToTwitter()
	{
		if ( m_twitterSwitch.isChecked() )
		{
			m_twitterSwitch.setChecked( false );

			OAuthFragment oAuthFragment = new OAuthFragment();
			oAuthFragment.initialize(
				Consts.KEY_TWITTER, new AsyncCallback()
				{
					@Override
					public void success()
					{
						User.addSocialMedium(Consts.KEY_TWITTER);
						m_twitterSwitch.setChecked( true );
						Toast.makeText( getActivity(), R.string.message_connect_success, Toast.LENGTH_LONG ).show();
					}
				}
			);

			oAuthFragment.show( this.getFragmentManager(), null );
		}
		else
		{
			m_userService.deleteMedium(
				Consts.KEY_TWITTER, new ServiceCallback<String>()
				{
					@Override
					public void success( final String value )
					{
						User.removeSocialMedium(Consts.KEY_TWITTER);
						Toast.makeText( getActivity(), R.string.message_disconnect_success, Toast.LENGTH_LONG ).show();
					}
				}
			);
		}
	}

	@OnClick( R.id.switch_tumblr )
	void connectToTumblr()
	{
		if ( m_tumblrSwitch.isChecked() )
		{
			m_tumblrSwitch.setChecked( false );

			OAuthFragment oAuthFragment = new OAuthFragment();
			oAuthFragment.initialize(
				Consts.KEY_TUMBLR, new AsyncCallback()
				{
					@Override
					public void success()
					{
						User.addSocialMedium(Consts.KEY_TUMBLR);
						m_tumblrSwitch.setChecked( true );
						Toast.makeText( getActivity(), R.string.message_connect_success, Toast.LENGTH_LONG ).show();
					}
				}
			);

			oAuthFragment.show( this.getFragmentManager(), null );
		}
		else
		{
			m_userService.deleteMedium(
				Consts.KEY_TUMBLR, new ServiceCallback<String>()
				{
					@Override
					public void success( final String value )
					{
						User.removeSocialMedium(Consts.KEY_TUMBLR);
						Toast.makeText( getActivity(), R.string.message_disconnect_success, Toast.LENGTH_LONG ).show();
					}
				}
			);
		}
	}

	@OnClick( R.id.switch_reddit )
	void connectToReddit()
	{
		if ( m_redditSwitch.isChecked() )
		{
			m_redditSwitch.setChecked( false );

			OAuthFragment oAuthFragment = new OAuthFragment();
			oAuthFragment.initialize(
					Consts.KEY_REDDIT, new AsyncCallback()
					{
						@Override
						public void success()
						{
							User.addSocialMedium(Consts.KEY_REDDIT);
							m_redditSwitch.setChecked( true );
							Toast.makeText( getActivity(), R.string.message_connect_success, Toast.LENGTH_LONG ).show();
						}
					}
			);

			oAuthFragment.show( this.getFragmentManager(), null );
		}
		else
		{
			m_userService.deleteMedium(
				Consts.KEY_REDDIT, new ServiceCallback<String>()
				{
					@Override
					public void success( final String value )
					{
						User.removeSocialMedium(Consts.KEY_REDDIT);
						Toast.makeText( getActivity(), R.string.message_disconnect_success, Toast.LENGTH_LONG ).show();
					}
				}
			);
		}
	}
}
