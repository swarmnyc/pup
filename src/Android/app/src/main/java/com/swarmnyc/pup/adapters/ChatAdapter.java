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
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.StringUtils;
import com.swarmnyc.pup.chat.ChatMessage;
import com.swarmnyc.pup.chat.ChatMessageListener;
import com.swarmnyc.pup.chat.ChatRoomService;
import com.swarmnyc.pup.models.Lobby;

import java.util.LinkedList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ChatMessageListener
{
	private static final int HEADER = 0;
	private static final int SHARE  = -1;
	private static final int ITEM   = 1;
	private Context        m_context;
	private Lobby          m_lobby;
	private LayoutInflater m_inflater;

	ChatRoomService   m_chatRoomService;
	List<ChatMessage> m_chatMessages;
	private RecyclerView m_recyclerView;

	public ChatAdapter( final Context context, final ChatRoomService chatRoomService, final Lobby lobby )
	{
		m_context = context;
		m_lobby = lobby;
		m_inflater = (LayoutInflater) m_context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		m_chatMessages = new LinkedList<>();

		m_chatRoomService = chatRoomService;
		m_chatRoomService.setMessageListener( this );
		m_chatRoomService.login();
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
	public RecyclerView.ViewHolder onCreateViewHolder( final ViewGroup parent, final int viewType )
	{
		if ( viewType == HEADER )
		{
			View view = m_inflater.inflate( R.layout.item_lobby_chat_header, null );
			return new HeaderViewHolder( view );
		}
		else if ( viewType == SHARE )
		{
			View view = m_inflater.inflate( R.layout.item_lobby_chat_share, null );
			return new ShareViewHolder( view );
		}
		else
		{
			View view = m_inflater.inflate( R.layout.item_lobby_chat, null );
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
			return position == HEADER ? HEADER : SHARE;
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
			return 2;
		}
		else
		{
			return m_chatMessages.size() + 1;
		}
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
			if (StringUtils.isEmpty( chatMessage.getUser().getPortraitUrl()) ){
				portrait.setImageResource( R.drawable.default_portrait );
			}else{
				Picasso.with( m_context ).load(chatMessage.getUser().getPortraitUrl()).into( portrait );
			}

			nameText.setText( chatMessage.getUser().getUserName() );
			messageText.setText( chatMessage.getBody() );
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
		public ShareViewHolder(
			final View view
		)
		{super( view );}
	}
}