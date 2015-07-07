package com.swarmnyc.pup.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.swarmnyc.pup.Consts;
import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.Services.Filter.GameFilter;
import com.swarmnyc.pup.Services.Filter.LobbyFilter;
import com.swarmnyc.pup.Services.GameService;
import com.swarmnyc.pup.Services.LobbyService;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.activities.MainActivity;
import com.swarmnyc.pup.adapters.AutoCompleteForPicturedModelAdapter;
import com.swarmnyc.pup.adapters.LobbyAdapter;
import com.swarmnyc.pup.components.Action;
import com.swarmnyc.pup.components.DialogHelper;
import com.swarmnyc.pup.components.GamePlatformUtils;
import com.swarmnyc.pup.components.Utility;
import com.swarmnyc.pup.models.Game;
import com.swarmnyc.pup.models.GamePlatform;
import com.swarmnyc.pup.models.Lobby;
import com.swarmnyc.pup.view.GamePlatformSelectView;
import com.uservoice.uservoicesdk.UserVoice;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LobbyListFragment extends BaseFragment
{
	@InjectView( R.id.txt_game_serach ) public      AutoCompleteTextView   m_gameSearch;
	@InjectView( R.id.layout_sliding_panel ) public SlidingUpPanelLayout   m_slidingPanel;
	@InjectView( R.id.list_lobby ) public           RecyclerView           m_lobbyRecyclerView;
	@InjectView( R.id.platform_select ) public      GamePlatformSelectView m_gamePlatformSelectView;
	@InjectView( R.id.layout_empty_results ) public ViewGroup              m_emptyResults;
	@Inject                                         GameService            gameService;
	@Inject                                         LobbyService           lobbyService;
	float m_panelSize = 0.0f;
	LayoutInflater inflater;
	private MainActivity        activity;
	private LobbyAdapter        m_lobbyAdapter;
	private LinearLayoutManager mLayoutManager;
	private LobbyFilter m_lobbyFilter = new LobbyFilter();
	private GameFilter  m_gameFilter  = new GameFilter();
	private AutoCompleteForPicturedModelAdapter<Game> gameAdapter;
	private boolean m_canLoadMore = false;

	public LobbyListFragment()
	{
	}


	@Override
	public void onAttach( Activity activity )
	{
		super.onAttach( activity );
		this.activity = (MainActivity) activity;
	}


	@Override
	public void onCreate(
		@Nullable
		final Bundle savedInstanceState
	)
	{
		Log.d( "LobbyListFragment", String.format( "onCreate (savedInstanceState = %s)", savedInstanceState ) );

		super.onCreate( savedInstanceState );
	}

	@Override
	public View onCreateView(
		LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
	)
	{
		PuPApplication.getInstance().getComponent().inject( this );
		View view = inflater.inflate( R.layout.fragment_lobby_list, container, false );
		ButterKnife.inject( this, view );
		setHasOptionsMenu( true );

		m_gamePlatformSelectView.setPlatformSelectionChangedListener(
			new GamePlatformSelectView.OnPlatformSelectionChangedListener()
			{
				@Override
				public void onPlatformSelectionChanged( final View v )
				{

					m_lobbyFilter.setPlatformList( m_gamePlatformSelectView.getSelectedGamePlatforms() );
					if ( v.isSelected() ) // only collapse if something is selected.
					{
						m_slidingPanel.setPanelState( SlidingUpPanelLayout.PanelState.COLLAPSED );
					}
					reloadData( true );
				}
			}
		);

		gameAdapter = new AutoCompleteForPicturedModelAdapter<Game>( this.getActivity() );

		gameAdapter.setSearchAction(
			new Action<CharSequence>()
			{
				@Override
				public void call( CharSequence constraint )
				{
					m_gameFilter.setSearch( constraint.toString() );
					gameService.getGames(
						m_gameFilter, new ServiceCallback<List<Game>>()
						{
							@Override
							public void success( List<Game> value )
							{
								if ( value.size() == 0 )
								{
									Game game = new Game();
									game.setThumbnailPictureUrl(
										Utility.getResourceUri(
											getActivity(), R.drawable.ico_plus
										).toString()
									);
									game.setName( getString( R.string.text_request_game ) );
									value.add( game );
								}

								gameAdapter.finishSearch( value );
							}
						}
					);
				}
			}
		);

		m_gameSearch.setAdapter( gameAdapter );

		m_gameSearch.setOnItemClickListener(
			new AdapterView.OnItemClickListener()
			{
				@Override
				public void onItemClick( AdapterView<?> parent, View view, int position, long id )
				{
					Game selectedGame = gameAdapter.getItem( position );
					if ( selectedGame.getId() == null )
					{
						m_gameSearch.setText( "" );
						UserVoice.launchPostIdea( getActivity() );
					}
					else
					{
						m_lobbyFilter.setGame( selectedGame );

						reloadData( true );
						hideKeyboard();
						m_slidingPanel.setPanelState( SlidingUpPanelLayout.PanelState.COLLAPSED );
					}
				}
			}
		);

		m_gameSearch.addTextChangedListener(
			new TextWatcher()
			{
				@Override
				public void beforeTextChanged(
					final CharSequence s, final int start, final int count, final int after
				)
				{

				}

				@Override
				public void onTextChanged(
					final CharSequence s, final int start, final int before, final int count
				)
				{

				}

				@Override
				public void afterTextChanged( final Editable s )
				{
					if ( s.length() == 0 )
					{
						m_lobbyFilter.setGame( null );
						reloadData( true );
					}
				}
			}
		);


		m_slidingPanel.setOverlayed( true );


		m_slidingPanel.setPanelSlideListener(
			new SlidingUpPanelLayout.PanelSlideListener()
			{
				@Override
				public void onPanelSlide( final View view, final float v )
				{
					if ( m_panelSize == 0 && v > 0 )
					{
						//						com.swarmnyc.pup.components.ViewAnimationUtils.hideWithAnimation(
						//							getActivity(), m_createLobbyButton
						//						);
					}
					else if ( m_panelSize == 1.0 && v < 1.0f )
					{
						//						com.swarmnyc.pup.components.ViewAnimationUtils.showWithAnimation(
						//							getActivity(), m_createLobbyButton
						//						);

					}
					m_panelSize = v;
				}

				@Override
				public void onPanelCollapsed( final View view )
				{
					// Hide Keyboard
					hideKeyboard();
				}

				@Override
				public void onPanelExpanded( final View view )
				{

				}


				@Override
				public void onPanelAnchored( final View view )
				{


				}

				@Override
				public void onPanelHidden( final View view )
				{

				}
			}
		);

		setHasOptionsMenu( true );

		this.inflater = inflater;


		// m_createLobbyButton.setVisibility( User.isLoggedIn() ? View.VISIBLE : View.GONE ); Button should be
		// visile aciton should be different.

		m_lobbyAdapter = new LobbyAdapter( getActivity() );
		m_lobbyAdapter.setReachEndAction(
			new Action()
			{
				@Override
				public void call( final Object value )
				{
					if ( m_canLoadMore )
					{
						Log.d( "LobbyListFragment", "Load More" );
						m_lobbyRecyclerView.postDelayed(
							new Runnable()
							{
								@Override
								public void run()
								{
									reloadData( false );
								}
							}, 500
						);
					}
				}
			}
		);
		m_lobbyRecyclerView.setAdapter( m_lobbyAdapter );

		mLayoutManager = new LinearLayoutManager( getActivity() );

		m_lobbyRecyclerView.setLayoutManager( mLayoutManager );


		if ( null != savedInstanceState )
		{
			m_lobbyFilter = Utility.fromJson( savedInstanceState.getString( "filter" ), LobbyFilter.class );
			m_gamePlatformSelectView.setSelectedGamePlatforms( m_lobbyFilter.getPlatforms() );
		}


		return view;
	}

	@Override
	public void onViewCreated(
		final View view,
		@Nullable
		final Bundle savedInstanceState
	)
	{
		Log.d(
			"LobbyListFragment", String.format(
				"onViewCreated (%s, savedInstanceState = %s)", view, savedInstanceState
			)
		);
		if ( null == savedInstanceState )
		{
			reloadData( true );
		}
		super.onViewCreated( view, savedInstanceState );
	}

	@Override
	public void onStart()
	{
		super.onStart();
		//		MainDrawerFragment.getInstance().highLight( Consts.KEY_LOBBIES );
	}


	@Override
	public void onResume()
	{
		super.onResume();
	}

	@Override
	public void onSaveInstanceState( final Bundle outState )
	{
		outState.putString( "filter", Utility.toJson( m_lobbyFilter ) );

		super.onSaveInstanceState( outState );
	}

	@Override
	public void onCreateOptionsMenu( final Menu menu, final MenuInflater inflater )
	{
		inflater.inflate( R.menu.menu_all_lobbies, menu );
		super.onCreateOptionsMenu( menu, inflater );
	}

	@Override
	public boolean onOptionsItemSelected( final MenuItem item )
	{
		final int itemId = item.getItemId();

		if ( itemId == R.id.menu_filter )
		{
			if ( m_slidingPanel != null )
			{
				if ( m_slidingPanel.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED )
				{
					m_slidingPanel.setPanelState( SlidingUpPanelLayout.PanelState.COLLAPSED );
				}
				else
				{
					m_slidingPanel.setPanelState( SlidingUpPanelLayout.PanelState.EXPANDED );
				}
			}

			return true;
		}
		return super.onOptionsItemSelected( item );
	}

	private void reloadData( final boolean restart )
	{
		if ( restart )
		{
			DialogHelper.showProgressDialog( this.getActivity(), R.string.message_loading );
			m_lobbyFilter.setStartTime( new Date() );
		}
		else
		{
			m_lobbyAdapter.startLoading();
			m_lobbyFilter.setStartTime( m_lobbyAdapter.getLastItem().getStartTime() );
		}

		if ( m_emptyResults.getVisibility() == View.VISIBLE ) // Hide empty results before loading
		{
			com.swarmnyc.pup.components.ViewAnimationUtils.hideWithAnimation( getActivity(), m_emptyResults );
		}


		lobbyService.getLobbies(
			m_lobbyFilter, new ServiceCallback<List<Lobby>>()
			{
				@Override
				public void success( List<Lobby> lobbies )
				{
					if ( restart )
					{
						DialogHelper.hide();
						m_lobbyAdapter.setItem( lobbies );
					}
					else
					{
						m_lobbyAdapter.endLoading();
						m_lobbyAdapter.addItem( lobbies );
					}

					m_canLoadMore = Consts.PAGE_SIZE == lobbies.size();

					if ( restart && lobbies.size() == 0 )
					{
						com.swarmnyc.pup.components.ViewAnimationUtils.showWithAnimation(
							getActivity(), m_emptyResults
						);
					}
				}
			}
		);

		updateTitle();
	}

	private void hideKeyboard()
	{
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
			Context.INPUT_METHOD_SERVICE
		);
		imm.hideSoftInputFromWindow( m_gameSearch.getWindowToken(), 0 );
	}

	@Override
	public void updateTitle()
	{
		final String title = null == m_lobbyFilter.getGame() ? "All Games" : m_lobbyFilter.getGame().getName();
		setTitle( title );
		setSubtitle( null );

		if ( m_lobbyFilter.getPlatforms().size() > 0 )
		{
			List<GamePlatform> platforms = new ArrayList<>( m_lobbyFilter.getPlatforms() );

			StringBuilder stringBuilder = new StringBuilder();
			for ( int i = 0; i < platforms.size(); i++ )
			{
				final GamePlatform platform = platforms.get( i );

				if ( i > 0 )
				{
					stringBuilder.append( ", " );
				}

				stringBuilder.append( GamePlatformUtils.labelForPlatform( getActivity(), platform ) );

			}

			Spanned subtitle = Html.fromHtml( String.format( "<small>%s</small>", stringBuilder.toString() ) );

			setSubtitle( subtitle );
		}
	}


	@Override
	public String getScreenName()
	{
		return "Lobby List";
	}
}
