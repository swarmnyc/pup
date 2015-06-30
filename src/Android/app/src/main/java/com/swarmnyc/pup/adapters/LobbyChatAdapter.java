package com.swarmnyc.pup.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.swarmnyc.pup.EventBus;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.StringUtils;
import com.swarmnyc.pup.User;
import com.swarmnyc.pup.chat.ChatMessage;
import com.swarmnyc.pup.events.RequireChatHistoryEvent;
import com.swarmnyc.pup.models.Lobby;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class LobbyChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
	private static final int HEADER      = 0;
	private static final int SYSTEM      = -1;
	private static final int LoadControl = -2;
	private static final int ITEM        = 1;
	private List<ChatMessage> m_chatMessages;
	private HashSet<String>   m_chatMessageIds;
	private Context           m_context;
	private Lobby             m_lobby;
	private LayoutInflater    m_inflater;
	private boolean           m_showReadMore;

	public LobbyChatAdapter(
		final Context context, final Lobby lobby
	)
	{
		m_context = context;
		m_lobby = lobby;
		m_inflater = (LayoutInflater) m_context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		m_chatMessages = new ArrayList<>();
		m_chatMessageIds = new HashSet<>();
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder( final ViewGroup parent, final int viewType )
	{
		//        if ( viewType == HEADER )
		//        {
		//            View view = m_inflater.inflate( R.layout.item_lobby_chat_header, parent, false );
		//            return new HeaderViewHolder( view );
		//        }
		//        else
		if ( viewType == LoadControl )
		{
			View view = m_inflater.inflate( R.layout.item_lobby_load_control, parent, false );
			return new LoadControlViewHolder( view );
		}
		else if ( viewType == SYSTEM )
		{
			View view = m_inflater.inflate( R.layout.item_lobby_chat_system, parent, false );
			return new SystemViewHolder( view );
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
			ItemViewHolder item = ( (ItemViewHolder) holder );
			ChatMessage message = getChatMessage( position );
			item.setCharMessage( message );
		}
		else if ( SystemViewHolder.class.isInstance( holder ) )
		{
			if ( m_chatMessages.size() > 0 )
			{
				( (SystemViewHolder) holder ).setMessage( getChatMessage( position ) );
			}
		}
	}

	@Override
	public int getItemViewType( final int position )
	{
		if ( position == 0 && m_showReadMore )
		{
			return LoadControl;
		}
		else if ( getChatMessage( position ).isSystemMessage() )
		{
			return SYSTEM;
		}
		else
		{
			return ITEM;
		}

	}

	@Override
	public int getItemCount()
	{

		// Header + LoadControl
		return m_chatMessages.size() + ( m_showReadMore ? 1 : 0 );
	}


	public ChatMessage getChatMessage( final int location )
	{
		return m_chatMessages.get( location - ( m_showReadMore ? 1 : 0 ) );
	}

	public ChatMessage getFirstChatMessage()
	{
		return m_chatMessages.get( 0 );
	}

	public void addMessages( int location, List<ChatMessage> messages )
	{
		ChatMessage previous = null;
		if ( location > 0 )
		{
			previous = m_chatMessages.get( location - 1 );

		}

		for ( ChatMessage message : messages )
		{

			if ( m_chatMessageIds.contains( message.getId() ) )
			{
				continue;
			}

			m_chatMessageIds.add( message.getId() );

			if ( previous != null && !previous.isSystemMessage() && !message.isSystemMessage() && message.getUser()
			                                                                                             .getId()
			                                                                                             .equals(
				                                                                                             previous
					                                                                                             .getUser()
					                                                                                             .getId()
			                                                                                             ) )

			{
				previous.setBody( previous.getBody() + "\r\n" + message.getBody() );
			}
			else
			{
				previous = message;

				m_chatMessages.add( location++, message );

			}
		}

		notifyDataSetChanged();
	}


	public void addMessages( List<ChatMessage> messages )
	{
		addMessages( m_chatMessages.size(), messages );
	}

	public void showLoadMore( boolean canReadMore )
	{
		m_showReadMore = canReadMore;
	}

	class ItemViewHolder extends RecyclerView.ViewHolder
	{
		@InjectView( R.id.contentPanel ) ViewGroup container;

		@InjectView( R.id.img_portrait ) ImageView portrait;

		@InjectView( R.id.text_name ) TextView nameText;

		@InjectView( R.id.text_message ) TextView messageText;

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


	class SystemViewHolder extends RecyclerView.ViewHolder
	{
		public SystemViewHolder( final View view )
		{
			super( view );
		}

		public void setMessage( final ChatMessage chatMessage )
		{
			( (TextView) itemView ).setText( chatMessage.getBody() );
		}
	}


	class LoadControlViewHolder extends RecyclerView.ViewHolder
	{
		public LoadControlViewHolder( final View view )
		{
			super( view );
			view.setOnClickListener(
				new View.OnClickListener()
				{
					@Override
					public void onClick( final View v )
					{
						EventBus.getBus().post( new RequireChatHistoryEvent() );
					}
				}
			);
		}
	}

}