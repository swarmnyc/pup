package com.swarmnyc.pup.view;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import com.swarmnyc.pup.AsyncCallback;
import com.swarmnyc.pup.Consts;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.Services.LobbyService;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.User;
import com.swarmnyc.pup.helpers.FacebookHelper;
import com.swarmnyc.pup.fragments.OAuthFragment;
import com.swarmnyc.pup.fragments.RedditShareFragment;
import com.swarmnyc.pup.models.Lobby;

import java.util.ArrayList;
import java.util.List;

public class ShareView extends LinearLayout
{
	@Bind( R.id.btn_facebook ) ImageView m_facebookButton;

	@Bind( R.id.btn_twitter ) ImageView m_twitterButton;

	@Bind( R.id.btn_tumblr ) ImageView m_tumblrButton;

	@Bind( R.id.btn_reddit ) ImageView m_redditButton;

	private LobbyService m_lobbyService;
	private Lobby m_lobby;
	private FragmentActivity m_activity;

	public ShareView( Context context, AttributeSet attrs )
	{
		super( context, attrs );

		init();
	}

	public void init()
	{
		setOrientation( VERTICAL );
		inflate( getContext(), R.layout.view_share, this );
		if ( !isInEditMode() )
		{
			ButterKnife.bind( this );

			setButtonState( User.current.hasSocialMedium( Consts.KEY_FACEBOOK ), m_facebookButton );
			setButtonState( User.current.hasSocialMedium( Consts.KEY_TWITTER ), m_twitterButton );
			setButtonState( User.current.hasSocialMedium( Consts.KEY_TUMBLR ), m_tumblrButton );
			setButtonState( User.current.hasSocialMedium( Consts.KEY_REDDIT ), m_redditButton );
		}
	}


	public void setLobbyService( LobbyService m_lobbyService )
	{
		this.m_lobbyService = m_lobbyService;
	}

	public void setLobby( Lobby m_lobby )
	{
		this.m_lobby = m_lobby;
	}

	public void setActivity( FragmentActivity activity )
	{
		m_activity = activity;
	}

	@OnClick( R.id.btn_facebook )
	void tapOnFacebook()
	{
		if ( m_facebookButton.isActivated() )
		{
			setButtonState( false, m_facebookButton );
		}
		else
		{
			if ( User.current.hasSocialMedium( Consts.KEY_FACEBOOK ) )
			{
				setButtonState( true, m_facebookButton );
			}
			else
			{
				FacebookHelper.startLoginRequire(m_activity,
					new AsyncCallback()
					{
						@Override
						public void success()
						{
							Toast.makeText( m_activity , R.string.message_connect_success, Toast.LENGTH_LONG )
							     .show();

							setButtonState( true, m_facebookButton );
						}
					}
				);
			}
		}
	}

	@OnClick( R.id.btn_twitter )
	void tapOnTwitter()
	{
		if ( m_twitterButton.isActivated() )
		{
			setButtonState( false, m_twitterButton );
		}
		else
		{
			if ( User.current.hasSocialMedium( Consts.KEY_TWITTER ) )
			{
				setButtonState( true, m_twitterButton );
			}
			else
			{
				OAuthFragment oAuthFragment = new OAuthFragment();
				oAuthFragment.initialize(
					Consts.KEY_TWITTER, new AsyncCallback()
					{
						@Override
						public void success()
						{
							User.addSocialMedium( Consts.KEY_TWITTER );
							setButtonState( true, m_twitterButton );
						}
					}
				);

				oAuthFragment.show( m_activity.getSupportFragmentManager(), null );
			}
		}
	}

	@OnClick( R.id.btn_tumblr )
	void tapOnTumblr()
	{
		if ( m_tumblrButton.isActivated() )
		{
			setButtonState( false, m_tumblrButton );
		}
		else
		{
			if ( User.current.hasSocialMedium( Consts.KEY_TUMBLR ) )
			{
				setButtonState( true, m_tumblrButton );
			}
			else
			{
				OAuthFragment oAuthFragment = new OAuthFragment();
				oAuthFragment.initialize(
					Consts.KEY_TUMBLR, new AsyncCallback()
					{
						@Override
						public void success()
						{
							User.addSocialMedium( Consts.KEY_TUMBLR );
							setButtonState( true, m_tumblrButton );
						}
					}
				);

				oAuthFragment.show( m_activity.getSupportFragmentManager(), null );
			}
		}
	}

	@OnClick( R.id.btn_reddit )
	void tapOnReddit()
	{
		if ( m_redditButton.isActivated() )
		{
			setButtonState( false, m_redditButton );
		}
		else
		{
			if ( User.current.hasSocialMedium( Consts.KEY_REDDIT ) )
			{
				setButtonState( true, m_redditButton );
			}
			else
			{
				OAuthFragment oAuthFragment = new OAuthFragment();
				oAuthFragment.initialize(
					Consts.KEY_REDDIT, new AsyncCallback()
					{
						@Override
						public void success()
						{
							User.addSocialMedium( Consts.KEY_REDDIT );
							setButtonState( true, m_redditButton );
						}
					}
				);

				oAuthFragment.show( m_activity.getSupportFragmentManager(), null );
			}
		}
	}

	@OnClick( R.id.btn_invite )
	void invite()
	{
		List<String> types = new ArrayList<>();
		if ( m_facebookButton.isActivated() )
		{
			types.add( Consts.KEY_FACEBOOK );
		}

		if ( m_twitterButton.isActivated() )
		{
			types.add( Consts.KEY_TWITTER );
		}

		if ( m_twitterButton.isActivated() )
		{
			types.add( Consts.KEY_TUMBLR );
		}

		if ( types.size() == 0 )
		{
			if ( m_redditButton.isActivated() )
			{
				ShareToReddit();
			}
			else
			{
				Toast.makeText( getContext(), "You need to choose at least one social medium", Toast.LENGTH_SHORT )
				     .show();
			}

		}
		else
		{
			m_lobbyService.invite(
				m_lobby, types, new ServiceCallback<String>()
				{
					@Override
					public void success( final String value )
					{
						if ( m_redditButton.isActivated() )
						{
							ShareToReddit();
						}
						else
						{
							Toast.makeText( getContext(), "Share Succeeded", Toast.LENGTH_SHORT ).show();
						}
					}
				}
			);
		}
	}

	private void ShareToReddit()
	{
		RedditShareFragment fragment = new RedditShareFragment();
		fragment.initialize(
			m_lobby, new AsyncCallback()
			{
				@Override
				public void success()
				{
					Toast.makeText( getContext(), "Share Succeeded", Toast.LENGTH_SHORT ).show();
				}
			}
		);

		fragment.show( m_activity.getSupportFragmentManager(), "Reddit" );
	}

	private void setButtonState( boolean share, ImageView button )
	{
		button.setActivated( share );
		button.setImageResource(
			getResources().getIdentifier(
				"ico_" + button.getTag() + ( share ? "_select" : "" ),
				"drawable",
				getContext().getPackageName()
			)
		);
	}
}
