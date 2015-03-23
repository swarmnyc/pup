package com.swarmnyc.pup.ui.view;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.model.Lobby;

import java.util.Date;

/**
 * TODO: document your custom view class.
 */
public class LobbyListItemView extends RelativeLayout
{

	@InjectView(R.id.img_game)      ImageView m_gameImage;
	@InjectView(R.id.txt_game_name) TextView  m_gameName;

	@InjectView(R.id.txt_game_time)   TextView m_gameTime;
	@InjectView(R.id.txt_description) TextView m_description;
	@InjectView(R.id.txt_platform)    TextView m_platform;
	@InjectView(R.id.txt_gamer_style) TextView m_gamerStyle;
	@InjectView(R.id.txt_room_size)   TextView m_room_size;
	private Lobby m_lobby;

	public LobbyListItemView( Context context )
	{
		super( context );
		init( null, 0 );
	}

	public LobbyListItemView( Context context, AttributeSet attrs )
	{
		super( context, attrs );
		init( attrs, 0 );
	}

	public LobbyListItemView( Context context, AttributeSet attrs, int defStyle )
	{
		super( context, attrs, defStyle );
		init( attrs, defStyle );
	}

	private void init( AttributeSet attrs, int defStyle )
	{

		final LayoutInflater infalter = (LayoutInflater) getContext().getSystemService(
			Context.LAYOUT_INFLATER_SERVICE
		);
		final View view = infalter.inflate( R.layout.list_item_lobby, this, true );
		ButterKnife.inject( this, view );
	}

	public void setLobby(final Lobby lobby)
	{
		m_lobby = lobby;

		Picasso.with( getContext() ).load( lobby.m_game.m_imageUrl ).centerCrop().fit().into( m_gameImage );


		m_gameName.setText( lobby.m_game.m_title );
		m_gameTime.setText(  DateUtils.getRelativeTimeSpanString( getContext(), lobby.m_startTime ));
		m_description.setText( lobby.m_description );
		m_platform.setText( lobby.m_game.m_gamingSystem.toString() );
		m_gamerStyle.setText( lobby.m_gamerStyle.toString().toUpperCase() );
		m_room_size.setText( String.format( "RM CAP %d / %d", lobby.m_gamerList.size(), lobby.m_roomSize ) );

	}

	public Lobby getLobby()
	{
		return m_lobby;
	}
}
