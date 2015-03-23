package com.swarmnyc.pup.ui.fragment;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.model.Lobby;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentViewLobby#newInstance} factory method to
 * createGame an instance of this fragment.
 */
public class FragmentViewLobby extends Fragment
{
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_LOBBY = "lobby";

	// TODO: Rename and change types of parameters
	private Lobby m_lobby;

	@InjectView( R.id.img_game )      ImageView m_gameImage;
	@InjectView( R.id.txt_game_name ) TextView  m_gameName;

	@InjectView( R.id.txt_game_time )   TextView m_gameTime;
	@InjectView( R.id.txt_description ) TextView m_description;
	@InjectView( R.id.txt_platform )    TextView m_platform;
	@InjectView( R.id.txt_gamer_style ) TextView m_gamerStyle;
	@InjectView( R.id.txt_room_size )   TextView m_room_size;


	@InjectView( R.id.pager_chat_members ) ViewPager     m_pager;
	@InjectView( R.id.pager_top_strip )    PagerTabStrip m_pagerTabStrip;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param lobby Parameter 1.
	 * @return A new instance of fragment ViewLobbyFragment.
	 */
	// TODO: Rename and change types and number of parameters
	/*public static FragmentViewLobby newInstance( String lobbyId )
	{
		FragmentViewLobby fragment = new FragmentViewLobby();
		Bundle args = new Bundle();
		args.putString( ARG_LOBBY, lobbyId );
		fragment.setArguments( args );
		return fragment;
	}*/
	public static FragmentViewLobby newInstance( Lobby lobby )
	{
		FragmentViewLobby fragment = new FragmentViewLobby();
		Bundle args = new Bundle();
		args.putString( ARG_LOBBY, ( new Gson() ).toJson( lobby ) );
		fragment.setArguments( args );
		return fragment;
	}

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		if ( getArguments() != null )
		{
			m_lobby = ( new Gson() ).fromJson( getArguments().getString( ARG_LOBBY ), Lobby.class );
		}


		setHasOptionsMenu( true );
	}

	@Override public void onCreateOptionsMenu( final Menu menu, final MenuInflater inflater )
	{
//		if (!m_mFragmentNavigationDrawer.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			inflater.inflate( R.menu.lobby, menu );
//		}
		super.onCreateOptionsMenu( menu, inflater );
	}

	@Override public boolean onOptionsItemSelected( final MenuItem item )
	{
		return super.onOptionsItemSelected( item );
	}

	@Override
	public View onCreateView(
		LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
	)
	{
		// Inflate the layout for this fragment
		final View inflate = inflater.inflate( R.layout.fragment_view_lobby, container, false );
		ButterKnife.inject( this, inflate );
		return inflate;
	}

	@Override public void onViewCreated( final View view, final Bundle savedInstanceState )
	{

		super.onViewCreated( view, savedInstanceState );
		if (null != m_lobby)
		{
			setLobby( m_lobby );

			getActivity().setTitle( m_lobby.m_game.m_title );
		}

	}

	public void setLobby( final Lobby lobby )
	{

		Picasso.with( getActivity() ).load( lobby.m_game.m_imageUrl ).centerCrop().fit().into( m_gameImage );


		m_gameName.setText( lobby.m_game.m_title );
		m_gameTime.setText( DateUtils.getRelativeTimeSpanString( getActivity(), lobby.m_startTime ) );
		m_description.setText( lobby.m_description );
		m_platform.setText( lobby.m_game.m_gamingSystem.toString() );
		m_gamerStyle.setText( lobby.m_gamerStyle.toString().toUpperCase() );
		m_room_size.setText( String.format( "RM CAP %d / %d", lobby.m_gamerList.size(), lobby.m_roomSize ) );

		m_pager.setAdapter( new DetailPagerAdapter(getActivity()) );

	}

	private static class DetailPagerAdapter extends PagerAdapter
	{


		private final Context m_context;

		private DetailPagerAdapter( final Context context )
		{
			m_context = context;
		}

		public static final String[] TITLES = new String[]{"CHAT", "PLAYERS"};

		@Override public int getCount()
		{
			return 2;
		}

		@Override public boolean isViewFromObject( final View view, final Object o )
		{
			return true;
		}

		@Override public CharSequence getPageTitle( final int position )
		{
			return TITLES[position];

		}

		@Override public Object instantiateItem( final ViewGroup container, final int position )
		{
			return new View( m_context );
		}
	}

}
