package com.swarmnyc.pup.adapters;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.swarmnyc.pup.Consts;
import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.Services.Filter.LobbyFilter;
import com.swarmnyc.pup.Services.LobbyService;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.User;
import com.swarmnyc.pup.components.GamePlatformUtils;
import com.swarmnyc.pup.components.Navigator;
import com.swarmnyc.pup.models.Lobby;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class MyChatAdapter extends RecyclerView.Adapter<MyChatAdapter.MyChatViewHolder> implements View.OnTouchListener
{
	public static final int ScrollBarHodler = 10;
	private final LayoutInflater m_inflater;
	private final Activity       m_activity;
	private final List<Lobby>    m_lobbies;
	@Inject
	LobbyService m_lobbyService;
	private int pageIndex = 0;
	private boolean m_noMoreData;
	private double  m_lastPositionX;
	private double  m_lastPositionY;
	private double  m_downPositionX;
	private double  m_downPositionY;
	private boolean m_handled = false;
	private double m_tigger;

	private MyChatViewHolder m_currentViewHolder;
	private RecyclerView     m_recyclerView;


	public MyChatAdapter( final Activity activity )
	{
		PuPApplication.getInstance().getComponent().inject( this );
		m_activity = activity;
		m_inflater = m_activity.getLayoutInflater();
		m_lobbies = new ArrayList<>();
		m_tigger = -TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_DIP, 90, m_activity.getResources().getDisplayMetrics()
		);
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
		recyclerView.setOnTouchListener( this );
		this.m_recyclerView = recyclerView;
	}

	@Override
	public boolean onTouch( final View v, final MotionEvent event )
	{
		if ( m_currentViewHolder == null )
		{ return false; }

		View view = m_currentViewHolder.m_contentPanel;
		switch ( event.getAction() )
		{
			case MotionEvent.ACTION_DOWN:
				m_handled = false;
				m_downPositionX = m_lastPositionX = event.getX();
				m_downPositionY = m_lastPositionY = event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				double moveX = event.getX() - m_lastPositionX;
				double moveY = event.getY() - m_lastPositionY;

				if ( !m_handled && Math.abs( moveY ) > Math.abs( moveX ) && Math.abs( moveY ) > ScrollBarHodler )
				{
					view.setLeft( m_recyclerView.getLeft() );
					view.setRight( m_recyclerView.getRight() );
					m_currentViewHolder = null;
				}
				else
				{
					m_handled = m_handled || Math.abs( m_downPositionX - m_lastPositionX ) > ScrollBarHodler;

					//Log.d( "Move", "Left:" + m_downPositionX + ", Right:" + m_lastPositionX + ", handled:" +
					// m_handled );
					//Log.d( "Move", "Left:" + view.getLeft() + ", Right:" + view.getRight() + ", handled:" +
					// m_handled );

					int left = (int) ( view.getLeft() + moveX );
					if ( left > 20 )
					{
						left = 20;
					}
					else if ( left < m_tigger )
					{
						left = (int) m_tigger;
					}

					view.setLeft( left );
					view.setRight( view.getLeft() + m_recyclerView.getWidth() );

					m_currentViewHolder.m_deleteMode = ( view.getLeft() <= m_tigger + 1 );
				}

				m_lastPositionX = event.getX();
				m_lastPositionY = event.getY();

				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				Log.d(
					"Mode",
					"handled:" + m_handled + ", deleteMode:" + m_currentViewHolder.m_deleteMode + ", X:" + event.getX()
				);
				if ( m_handled )
				{
					if ( !m_currentViewHolder.m_deleteMode )
					{
						//return
						view.setLeft( m_recyclerView.getLeft() );
						view.setRight( m_recyclerView.getRight() );
					}
				}
				else
				{
					if ( m_currentViewHolder.m_deleteMode )
					{
						double x = event.getX();
						if ( x > m_currentViewHolder.m_deleteImage.getX() )
						{
							m_currentViewHolder.remove();
						}
						else
						{
							//return
							view.setLeft( m_recyclerView.getLeft() );
							view.setRight( m_recyclerView.getRight() );
						}
					}
					else
					{
						//click
						Navigator.ToLobby(
							m_currentViewHolder.m_lobby.getId(),
							m_currentViewHolder.m_lobby.getName(),
							false
						);
					}
				}
				break;
		}

		return m_handled;
	}

	public class MyChatViewHolder extends RecyclerView.ViewHolder
	{
		@InjectView( R.id.contentPanel )
		RelativeLayout m_contentPanel;
		@InjectView( R.id.img_game )
		ImageView      m_gameImage;
		@InjectView( R.id.img_game_border )
		View     	   m_gameImageBorder;
		@InjectView( R.id.img_delete )
		ImageView      m_deleteImage;
		@InjectView( R.id.txt_game_name )
		TextView       m_gameName;
		@InjectView( R.id.txt_game_time )
		TextView       m_gameTime;
		@InjectView( R.id.txt_lastMessage )
		TextView m_lastMessage;
		@InjectView( R.id.txt_platform )
		TextView       m_platform;

		private boolean m_deleteMode;
		private Lobby   m_lobby;

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

		private void remove()
		{
			//Log.d( "Remove", "LobbyId:" + m_lobby.getId() );
			if ( m_lobby.getAliveUser(User.current.getId()).getIsOwner() )
			{
				Toast.makeText( m_activity, R.string.message_leave_room_owner, Toast.LENGTH_LONG ).show();
			}
			else
			{
				m_lobbies.remove( this.getAdapterPosition() );
				notifyItemRemoved( this.getAdapterPosition() );
				m_lobbyService.leave(
					m_lobby.getId(), new ServiceCallback()
					{
						@Override
						public void success( final Object value )
						{
							showUndo();
						}
					}
				);
			}
		}

		private void showUndo()
		{
			Snackbar snackbar = Snackbar.make( m_recyclerView, R.string.message_leave_room, Snackbar.LENGTH_LONG );
			snackbar.setAction(
				R.string.text_undo, new View.OnClickListener()
				{
					@Override
					public void onClick( final View v )
					{
						m_lobbyService.join(
							m_lobby.getId(), new ServiceCallback()
							{
								@Override
								public void success( final Object value )
								{
								    m_lobbies.add( m_lobby );
									notifyDataSetChanged();
								}
							}
						);
					}
				}
			);
			snackbar.getView().setPadding( 0, 30, 0, 30 );
			snackbar.setActionTextColor( m_activity.getResources().getColor( R.color.pup_white ) );
			snackbar.getView().setBackgroundResource( R.color.pup_orange );
			snackbar.show();
		}

		public void setLobby( final Lobby lobby )
		{
			m_lobby = lobby;
			Picasso.with( m_activity ).load( lobby.getPictureUrl() ).into( m_gameImage );

			m_gameName.setText( lobby.getName() );

			m_lastMessage.setText(lobby.getLastMessage());

			m_platform.setText( GamePlatformUtils.labelResIdForPlatform( lobby.getPlatform() ) );

			m_gameTime.setText(
				DateUtils.getRelativeTimeSpanString( lobby.getStartTime().getTime() )
			);

			if (lobby.getUnreadMessageCount() == 0){
				m_gameImageBorder.setVisibility(View.GONE);
			} else {
				m_gameImageBorder.setVisibility(View.VISIBLE);
			}
		}
	}


}
