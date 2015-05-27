package com.swarmnyc.pup.adapters;

import android.app.Activity;
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
import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.StringUtils;
import com.swarmnyc.pup.chat.ChatMessage;
import com.swarmnyc.pup.chat.ChatMessageListener;
import com.swarmnyc.pup.chat.ChatRoomService;
import com.swarmnyc.pup.chat.ChatService;
import com.swarmnyc.pup.models.Lobby;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ChatMessageListener
{
	private static final int HEADER = 0;
	private static final int ITEM   = 1;
	private Context        m_context;
	private Lobby          m_lobby;
	private LayoutInflater m_inflater;

	ChatRoomService   m_chatRoomService;
	List<ChatMessage> m_chatMessages;

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
			return new HeaderViewHolder( m_context, view, m_lobby );
		}
		else
		{
			View view = m_inflater.inflate( R.layout.item_lobby_chat, null );

			return new ItemViewHolder( m_context, view );
		}
	}

	@Override
	public void onBindViewHolder( final RecyclerView.ViewHolder holder, final int position )
	{
		if ( position != 0 )
		{
			( (ItemViewHolder) holder ).setCharMessage( m_chatMessages.get( position - 1 ) );
		}
	}

	@Override
	public int getItemViewType( final int position )
	{
		return position == HEADER ? HEADER : ITEM;
	}

	@Override
	public int getItemCount()
	{
		return m_chatMessages.size() + 1;
	}

	@Override
	public void receive( final List<ChatMessage> message )
	{
		m_chatMessages.addAll( message );
		notifyDataSetChanged();
	}


	class ItemViewHolder extends RecyclerView.ViewHolder
	{
		@InjectView( R.id.img_portrait )
		ImageView portrait;

		@InjectView( R.id.text_name )
		TextView nameText;

		@InjectView( R.id.text_message )
		TextView messageText;
		private Context m_context;

		public ItemViewHolder( final Context context, final View view )
		{
			super( view );
			m_context = context;

			ButterKnife.inject( this, view );
		}

		public void setCharMessage( final ChatMessage chatMessage )
		{
			portrait.setImageResource( R.drawable.default_portrait );
			nameText.setText( chatMessage.getUserId() );
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

		private Context m_context;
		private Lobby   m_lobby;

		public HeaderViewHolder( final Context context, final View view, final Lobby lobby )
		{
			super( view );
			m_context = context;
			m_lobby = lobby;

			ButterKnife.inject( this, view );

			//name
			lobbyNameText.setText( lobby.getOwner().getName() + "'s\n" + lobby.getName() );

			//img
			if ( StringUtils.isNotEmpty( lobby.getPictureUrl() ) )
			{
				Picasso.with( context ).load( lobby.getPictureUrl() ).centerCrop().fit().into( gameImageView );
			}

			//type
			lobbyTypeText.setText( String.format( "%s,%s", lobby.getPlayStyle(), lobby.getSkillLevel() ) );

			//description
			lobbyDescriptionText.setText( lobby.getDescription() );
		}
	}
}
