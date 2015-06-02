package com.swarmnyc.pup.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.swarmnyc.pup.Consts;
import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.Services.Filter.LobbyFilter;
import com.swarmnyc.pup.Services.LobbyService;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.components.Navigator;
import com.swarmnyc.pup.models.Lobby;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class MyChatAdapter extends RecyclerView.Adapter<MyChatAdapter.MyChatViewHolder>
{
	private final LayoutInflater m_inflater;
	private final Activity       m_activity;
	private final List<Lobby>    m_lobbies;
	@Inject
	LobbyService m_lobbyService;
	private int pageIndex = 0;
	private boolean          m_noMoreData;
	private double           m_lastPosition;
	private double           m_downPosition;
	private MyChatViewHolder m_currentViewHolder;

	public MyChatAdapter( final Activity activity )
	{
		PuPApplication.getInstance().getComponent().inject( this );
		m_activity = activity;
		m_inflater = m_activity.getLayoutInflater();
		m_lobbies = new ArrayList<>();
		fetchMoreData();
	}

	private void fetchMoreData()
	{
		if ( m_noMoreData )
		{ return; }

		LobbyFilter filter = new LobbyFilter();
		filter.setPageIndex( pageIndex++ );
		m_lobbyService.getMyLobbies(
			filter, new ServiceCallback<List<Lobby>>()
			{
				@Override
				public void success( final List<Lobby> value )
				{
					if ( value.size() == 0 )
					{
						m_noMoreData = true;
					}
					else
					{
						if ( value.size() < Consts.PAGE_SIZE )
						{ m_noMoreData = true; }

						m_lobbies.addAll( value );
						notifyDataSetChanged();
					}
				}
			}
		);
	}

	@Override
	public MyChatViewHolder onCreateViewHolder( final ViewGroup parent, final int viewType )
	{
		return new MyChatViewHolder( m_inflater.inflate( R.layout.item_my_lobby, null ) );
	}

	@Override
	public void onBindViewHolder( final MyChatViewHolder holder, final int position )
	{
		holder.setLobby( m_lobbies.get( position ) );
		if ( !m_noMoreData && position + 1 >= ( pageIndex * Consts.PAGE_SIZE ) )
		{
			fetchMoreData();
		}
	}

	@Override
	public int getItemCount()
	{
		return m_lobbies.size();
	}

	@Override
	public void onAttachedToRecyclerView( final RecyclerView recyclerView )
	{
		super.onAttachedToRecyclerView( recyclerView );
		recyclerView.setOnTouchListener(
			new View.OnTouchListener()
			{
				@Override
				public boolean onTouch( final View v, final MotionEvent event )
				{
					if ( m_currentViewHolder == null )
					{ return false; }

					View view = m_currentViewHolder.m_contentPanel;
					switch ( event.getAction() )
					{
						case MotionEvent.ACTION_DOWN:
							m_lastPosition = event.getX();
							break;
						case MotionEvent.ACTION_MOVE:
							double move = event.getX() - m_lastPosition;
							Log.d( "Move", "Left:" + view.getLeft() + ", Right:" + view.getRight() );

							if ( view.getLeft() < 20 )
							{
								view.setLeft( (int) ( view.getLeft() + move ) );
								view.setRight( view.getLeft() + recyclerView.getWidth() );
							}

							m_lastPosition = event.getX();
							break;
						case MotionEvent.ACTION_UP:
							if ( event.getEventTime() - event.getDownTime() < 250 )
							{
								//click
								Navigator.ToLobby(
									m_currentViewHolder.m_lobby.getId(),
									m_currentViewHolder.m_lobby.getName(),
									Consts.KEY_MY_LOBBIES,
									false
								);
							}
							else
							{
								//Todo: removing it
								view.setLeft( recyclerView.getLeft() );
								view.setRight( recyclerView.getRight() );
							}

							break;
						case MotionEvent.ACTION_CANCEL:
							view.setLeft( recyclerView.getLeft() );
							view.setRight( recyclerView.getRight() );

							break;
					}

					return true;
				}
			}
		);
	}

	public class MyChatViewHolder extends RecyclerView.ViewHolder
	{
		@InjectView( R.id.contentPanel )
		RelativeLayout m_contentPanel;
		@InjectView( R.id.img_game )
		ImageView      m_gameImage;
		@InjectView( R.id.txt_game_name )
		TextView       m_gameName;
		@InjectView( R.id.txt_game_time )
		TextView       m_gameTime;
		@InjectView( R.id.txt_description )
		TextView       m_description;
		@InjectView( R.id.txt_platform )
		TextView       m_platform;

		private Lobby m_lobby;

		public MyChatViewHolder( final View itemView )
		{
			super( itemView );
			itemView.setTag( this );
			ButterKnife.inject( this, itemView );

			itemView.setOnTouchListener(
				new View.OnTouchListener()
				{
					@Override
					public boolean onTouch( final View v, final MotionEvent event )
					{
						if ( event.getAction() == MotionEvent.ACTION_DOWN )
						{ m_currentViewHolder = MyChatViewHolder.this; }

						return false;
					}
				}
			);
		}

		public void setLobby( final Lobby lobby )
		{
			m_lobby = lobby;
			Picasso.with( m_activity ).load( lobby.getPictureUrl() ).into( m_gameImage );

			m_gameName.setText( lobby.getName() );

			m_description.setText( lobby.getDescription() );

			m_platform.setText( lobby.getPlatform().toString() );

			m_gameTime.setText(
				DateUtils.getRelativeTimeSpanString( lobby.getStartTime().getTime() )
			);
		}
	}
}
