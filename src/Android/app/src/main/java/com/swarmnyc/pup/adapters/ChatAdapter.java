package com.swarmnyc.pup.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.swarmnyc.pup.*;
import com.swarmnyc.pup.Services.LobbyService;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.chat.ChatMessage;
import com.swarmnyc.pup.chat.ChatMessageListener;
import com.swarmnyc.pup.chat.ChatRoomService;
import com.swarmnyc.pup.components.Action;
import com.swarmnyc.pup.components.FacebookHelper;
import com.swarmnyc.pup.components.TwitterHelper;
import com.swarmnyc.pup.models.Lobby;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ChatMessageListener
{
	private static final int HEADER   = 0;
	private static final int SHARE    = -1;
	private static final int LOADDING = -2;
	private static final int ITEM     = 1;
	ChatRoomService   m_chatRoomService;
	List<ChatMessage> m_chatMessages;
	LobbyService      m_lobbyService;
	private Context        m_context;
	private Lobby          m_lobby;
	private LayoutInflater m_inflater;
	private RecyclerView   m_recyclerView;
	private boolean        isloaded;

	public ChatAdapter(
		final Context context, final LobbyService lobbyService, final ChatRoomService chatRoomService, final Lobby lobby
	)
	{
		m_context = context;
		m_lobby = lobby;
		m_inflater = (LayoutInflater) m_context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		m_lobbyService = lobbyService;
		m_chatMessages = new LinkedList<>();

		m_chatRoomService = chatRoomService;
		m_chatRoomService.setMessageListener( this );
		m_chatRoomService.login(
			new Action()
			{
				@Override
				public void call( final Object value )
				{
					isloaded = true;
					notifyDataSetChanged();
				}
			}
		);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder( final ViewGroup parent, final int viewType )
	{
		if ( viewType == HEADER )
		{
			View view = m_inflater.inflate( R.layout.item_lobby_chat_header, parent, false );
			return new HeaderViewHolder( view );
		}
		else if ( viewType == SHARE )
		{
			View view = m_inflater.inflate( R.layout.item_lobby_chat_share, parent, false );
			return new ShareViewHolder( view );
		}
		else if ( viewType == LOADDING )
		{
			View view = m_inflater.inflate( R.layout.item_lobby_chat_loading, parent, false );
			return new LoadingViewHolder( view );
		}
		else
		{
			View view = m_inflater.inflate( R.layout.item_lobby_chat, parent, false );
			return new ItemViewHolder( view );
		}
	}

	@Override
	public void onBindViewHolder( final RecyclerView.ViewHolder holder, final int position )
	{
		if ( ItemViewHolder.class.isInstance( holder ) )
		{
			( (ItemViewHolder) holder ).setCharMessage( m_chatMessages.get( position - 1 ) );
		}
	}

	@Override
	public int getItemViewType( final int position )
	{
		if ( m_chatMessages.size() == 0 )
		{
			if ( isloaded )
			{ return position == HEADER ? HEADER : SHARE; }
			else
			{ return position == HEADER ? HEADER : LOADDING; }
		}
		else
		{
			return position == HEADER ? HEADER : ITEM;
		}
	}

	@Override
	public int getItemCount()
	{
		if ( m_chatMessages.size() == 0 )
		{
			return m_lobby.isDwellingUser( User.current.getId() ) ? 2 : 1;
		}
		else
		{
			return m_chatMessages.size() + 1;
		}
	}

	@Override
	public void onAttachedToRecyclerView( final RecyclerView recyclerView )
	{
		m_recyclerView = recyclerView;
		super.onAttachedToRecyclerView( recyclerView );
	}

	@Override
	public void onDetachedFromRecyclerView( final RecyclerView recyclerView )
	{
		super.onDetachedFromRecyclerView( recyclerView );
		m_chatRoomService.leave();
	}

	@Override
	public void receive( final List<ChatMessage> message )
	{
		m_chatMessages.addAll( message );
		notifyDataSetChanged();

		//TODO: Better Scrolling
		m_recyclerView.scrollToPosition( m_chatMessages.size() );
	}

	class ItemViewHolder extends RecyclerView.ViewHolder
	{
		@InjectView( R.id.contentPanel )
		ViewGroup container;

		@InjectView( R.id.img_portrait )
		ImageView portrait;

		@InjectView( R.id.text_name )
		TextView nameText;

		@InjectView( R.id.text_message )
		TextView messageText;

		public ItemViewHolder( final View view )
		{
			super( view );

			ButterKnife.inject( this, view );
		}

		public void setCharMessage( final ChatMessage chatMessage )
		{
			if ( StringUtils.isEmpty( chatMessage.getUser().getPortraitUrl() ) )
			{
				portrait.setImageResource( R.drawable.default_portrait );
			}
			else
			{
				Picasso.with( m_context ).load( chatMessage.getUser().getPortraitUrl() ).into( portrait );
			}

			nameText.setText( chatMessage.getUser().getUserName() );
			messageText.setText( chatMessage.getBody() );

			if ( chatMessage.getUser().getId().equals( User.current.getId() ) )
			{
				container.setBackgroundResource( R.color.pup_my_chat );
			}
			else
			{
				container.setBackgroundResource( R.color.pup_white );
			}

		}
	}

	class HeaderViewHolder extends RecyclerView.ViewHolder
	{
		@InjectView( R.id.img_game )
		ImageView gameImageView;

		@InjectView( R.id.text_name )
		TextView lobbyNameText;

		@InjectView( R.id.text_lobby_type )
		TextView lobbyTypeText;

		@InjectView( R.id.text_description )
		TextView lobbyDescriptionText;

		public HeaderViewHolder( final View view )
		{
			super( view );

			ButterKnife.inject( this, view );

			//name
			lobbyNameText.setText( m_lobby.getOwner().getUserName() + "'s\n" + m_lobby.getName() );

			//img
			if ( StringUtils.isNotEmpty( m_lobby.getPictureUrl() ) )
			{
				Picasso.with( m_context ).load( m_lobby.getPictureUrl() ).centerCrop().fit().into( gameImageView );
			}

			//type
			lobbyTypeText.setText( String.format( "%s,%s", m_lobby.getPlayStyle(), m_lobby.getSkillLevel() ) );

			//description
			lobbyDescriptionText.setText( m_lobby.getDescription() );
		}
	}

	class ShareViewHolder extends RecyclerView.ViewHolder
	{
		@InjectView( R.id.btn_facebook )
		View m_facebookButton;

		@InjectView( R.id.btn_twitter )
		View m_twitterButton;

		public ShareViewHolder( final View view )
		{
			super( view );
			ButterKnife.inject( this, view );

			setButtonState( User.current.hasMedium( Consts.KEY_FACEBOOK ), m_facebookButton );
			setButtonState( User.current.hasMedium( Consts.KEY_TWITTER ), m_twitterButton );
		}

		private void setButtonState( boolean share, View button )
		{
			button.setActivated( share );
			button.setAlpha( share ? 1f : 0.5f );
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
				if ( User.current.hasMedium( Consts.KEY_FACEBOOK ) )
				{
					setButtonState( true, m_facebookButton );
				}
				else
				{
					FacebookHelper.startLoginRequire(
						new AsyncCallback()
						{
							@Override
							public void success( )
							{
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
				if ( User.current.hasMedium( Consts.KEY_TWITTER ) )
				{
					setButtonState( true, m_twitterButton );
				}
				else
				{
					TwitterHelper.startLoginRequire(
						new AsyncCallback()
						{
							@Override
							public void success( )
							{
								setButtonState( true, m_twitterButton );
							}
						}
					);
				}
			}
		}

		@OnClick( R.id.btn_invite )
		void invite()
		{
			List<String> types = new ArrayList<>();
			if ( m_facebookButton.isActivated() )
			{ types.add( Consts.KEY_FACEBOOK ); }

			if ( m_twitterButton.isActivated() )
			{ types.add( Consts.KEY_TWITTER ); }

			if ( types.size() == 0 )
			{
				Toast.makeText( m_context, "You need to choose at least one social medium", Toast.LENGTH_SHORT ).show();
			}
			else
			{
				m_lobbyService.invite(
					m_lobby.getId(), types, new ServiceCallback()
					{
						@Override
						public void success( final Object value )
						{
							Toast.makeText( m_context, "Invite Succeeded", Toast.LENGTH_SHORT ).show();
						}
					}
				);
			}

		}
	}

	class LoadingViewHolder extends RecyclerView.ViewHolder
	{
		public LoadingViewHolder( final View view )
		{
			super( view );
		}
	}
}
