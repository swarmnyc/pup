package com.swarmnyc.pup.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
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
import com.swarmnyc.pup.components.*;
import com.swarmnyc.pup.models.Game;
import com.swarmnyc.pup.models.GamePlatform;
import com.swarmnyc.pup.models.Lobby;
import com.swarmnyc.pup.view.GamePlatformSelectView;
import com.swarmnyc.pup.view.LobbyListItemView;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class LobbyListFragment extends Fragment implements Screen
{
	@InjectView( R.id.txt_game_serach )
	public AutoCompleteTextView m_gameSearch;
	@InjectView( R.id.layout_sliding_panel )
	public SlidingUpPanelLayout m_slidingPanel;
	@InjectView( R.id.list_lobby )
	public RecyclerView m_lobbyRecyclerView;
	@InjectView( R.id.btn_create_lobby )
	public ImageButton            m_createLobbyButton;
	@InjectView( R.id.platform_select )
	public GamePlatformSelectView m_gamePlatformSelectView;
	@InjectView( R.id.layout_empty_results )
	public ViewGroup              m_emptyResults;
	@Inject
	GameService gameService;
	@Inject
	LobbyService lobbyService;
	float m_panelSize = 0.0f;
	LayoutInflater inflater;
	private MainActivity        activity;
	private LobbyAdapter        m_lobbyAdapter;
	private LinearLayoutManager mLayoutManager;
	private LobbyFilter m_lobbyFilter = new LobbyFilter();
	private GameFilter  m_gameFilter  = new GameFilter();
	private AutoCompleteForPicturedModelAdapter<Game> gameAdapter;

	public LobbyListFragment()
	{
	}

	@OnClick( R.id.btn_create_lobby )
	public void onCreateLobbyButtonClicked()
	{
		Navigator.ToCreateLobby();
	}

	@Override
	public void onAttach( Activity activity )
	{
		super.onAttach( activity );
		this.activity = (MainActivity) activity;


		updateTitle();

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
					reloadData();
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
					m_lobbyFilter.setGame( gameAdapter.getItem( position ) );

					reloadData();
					hideKeyboard();
					m_slidingPanel.setPanelState( SlidingUpPanelLayout.PanelState.COLLAPSED );

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
						reloadData();
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
						com.swarmnyc.pup.components.ViewAnimationUtils.hideWithAnimation(
							getActivity(), m_createLobbyButton
						);
					}
					else if ( m_panelSize == 1.0 && v < 1.0f )
					{
						com.swarmnyc.pup.components.ViewAnimationUtils.showWithAnimation(
							getActivity(), m_createLobbyButton
						);

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
	public void onStart()
	{
		reloadData();

		super.onStart();
		MainDrawerFragment.getInstance().highLight( Consts.KEY_LOBBIES );
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

	private void reloadData()
	{
		final ContentLoadingProgressBar progressDialog = new ContentLoadingProgressBar( getActivity() );
		progressDialog.show();
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
					m_lobbyAdapter.setLobbies( lobbies );
					progressDialog.hide();
					if ( lobbies.size() == 0 )
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

	private void updateTitle()
	{
		final String title = null == m_lobbyFilter.getGame() ? "All Lobbies" : m_lobbyFilter.getGame().getName();
		final ActionBar actionBar = ( (AppCompatActivity) getActivity() ).getSupportActionBar();
		actionBar.setTitle( title );
		actionBar.setSubtitle( null );

		if ( m_lobbyFilter.getPlatforms().size() > 0 )
		{ ; }
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
			actionBar.setSubtitle( stringBuilder.toString() );
		}

	}

	private class LobbyAdapter extends RecyclerView.Adapter<LobbyAdapter.ViewHolder>
	{
		Context m_context;
		private List<Lobby> m_lobbies = new ArrayList<>();

		private LobbyAdapter( final Context context )
		{
			m_context = context;
		}

		public List<Lobby> getLobbies()
		{
			return m_lobbies;
		}

		public void setLobbies( final List<Lobby> lobbies )
		{
			m_lobbies = lobbies;
			notifyDataSetChanged();
		}

		@Override
		public ViewHolder onCreateViewHolder(
			final ViewGroup viewGroup, final int i
		)
		{
			return new ViewHolder( new LobbyListItemView( m_context ) );
		}

		@Override
		public void onBindViewHolder( final ViewHolder viewHolder, final int i )
		{
			viewHolder.m_lobbyListItemView.setLobby( m_lobbies.get( i ) );
		}

		@Override
		public long getItemId( final int position )
		{
			return position;
		}

		@Override
		public int getItemCount()
		{
			return m_lobbies.size();
		}

		// Provide a reference to the views for each data item
		// Complex data items may need more than one view per item, and
		// you provide access to all the views for a data item in a view holder
		public class ViewHolder extends RecyclerView.ViewHolder
		{
			// each data item is just a string in this case
			public LobbyListItemView m_lobbyListItemView;

			public ViewHolder( final LobbyListItemView lobbyListItemView )
			{
				super( lobbyListItemView );
				m_lobbyListItemView = lobbyListItemView;
				m_lobbyListItemView.setOnClickListener(
					new View.OnClickListener()
					{
						@Override
						public void onClick( final View v )
						{
							Navigator.ToLobby( lobbyListItemView.getLobby().getId(),lobbyListItemView.getLobby().getName(), Consts.KEY_LOBBIES, false );
						}
					}
				);
			}
		}
	}

	@Override
	public String toString()
	{
		return "Lobby List";
	}
}
