package com.swarmnyc.pup.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
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
import com.swarmnyc.pup.adapters.EndlessRecyclerOnScrollListener;
import com.swarmnyc.pup.components.*;
import com.swarmnyc.pup.models.Game;
import com.swarmnyc.pup.models.GamePlatform;
import com.swarmnyc.pup.models.Lobby;
import com.swarmnyc.pup.view.GamePlatformSelectView;
import com.swarmnyc.pup.view.LobbyListItemView;
import com.uservoice.uservoicesdk.UserVoice;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LobbyListFragment extends Fragment implements Screen
{
	@InjectView( R.id.txt_game_serach ) public      AutoCompleteTextView   m_gameSearch;
	@InjectView( R.id.layout_sliding_panel ) public SlidingUpPanelLayout   m_slidingPanel;
	@InjectView( R.id.list_lobby ) public           RecyclerView           m_lobbyRecyclerView;
	@InjectView( R.id.btn_create_lobby ) public     ImageButton            m_createLobbyButton;
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
	private EndlessRecyclerOnScrollListener           m_endlessRecyclerOnScrollListener;
	private LayoutInflater                            m_layoutInflater;

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
	public void onCreate(
		@Nullable
		final Bundle savedInstanceState
	)
	{
		Log.d( "LobbyListFragment", String.format( "onCreate (savedInstanceState = %s)",savedInstanceState ));

		super.onCreate( savedInstanceState );
	}

	@Override
	public View onCreateView(
		LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
	)
	{
		m_layoutInflater = inflater;
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
					reloadData( 0 );
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
								if (value.size() == 0){
									Game game = new Game();
									game.setThumbnailPictureUrl( Utility.getResourceUri( getActivity(), R.drawable.ico_plus ).toString() );
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
					Game selectedGame =gameAdapter.getItem( position );
					if ( selectedGame.getId() == null ){
						m_gameSearch.setText( "" );
						UserVoice.launchPostIdea( getActivity() );
					} else  {
						m_lobbyFilter.setGame( selectedGame );

						reloadData( 0 );
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
						reloadData( 0 );
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

		m_endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener( mLayoutManager )
		{
			@Override
			public void onLoadComplete( final int current_page )
			{
				m_lobbyAdapter.onLoadComplete( current_page );
			}

			@Override
			public void onLoadMore( int current_page )
			{
				// do something...
				reloadData( current_page );
			}
		};
		m_lobbyRecyclerView.addOnScrollListener(
			m_endlessRecyclerOnScrollListener
		);


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
			"LobbyListFragment", String.format( "onViewCreated (%s, savedInstanceState = %s)",view,
			savedInstanceState));
		if (null == savedInstanceState)
		{
			reloadData( 0 );
		}
		super.onViewCreated( view, savedInstanceState );
	}

	@Override
	public void onStart()
	{
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

	private void reloadData( final int current_page )
	{
		m_lobbyFilter.setPageIndex( current_page );
		if ( current_page == 0 )
		{
			m_lobbyFilter.setStartTime(new Date());
			m_endlessRecyclerOnScrollListener.reset();
		}
		else
		{
			m_lobbyAdapter.onLoading( current_page );
		}
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
					if ( current_page == 0 )
					{
						m_lobbyAdapter.setLobbies( lobbies );
					}
					else
					{
						m_lobbyAdapter.addLobbies( lobbies );
					}
					progressDialog.hide();
					if ( current_page == 0 && lobbies.size() == 0 )
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
		final Toolbar actionBar = MainActivity.getInstance().getToolbar();
		actionBar.setTitle( title );
		actionBar.setSubtitle( null );

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

			actionBar.setSubtitle( subtitle );
		}
	}

	private static class LobbyAdapter extends RecyclerView.Adapter<LobbyAdapter.BaseViewHolder>
	{
		Context m_context;
		private List<Lobby> m_lobbies = new ArrayList<>();
		boolean m_isLoading = false;
		private LayoutInflater m_layoutInflater;


		enum ItemViewType
		{
			RegularView,
			LoadingView,
		}


		private LobbyAdapter( final Context context )
		{
			m_context = context;

			m_layoutInflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

		}

		public List<Lobby> getLobbies()
		{
			return m_lobbies;
		}

		public void setLobbies( final List<Lobby> lobbies )
		{
			m_lobbies = lobbies;
			notifyDataSetChanged();
			onLoadComplete( 0 );
		}

		public void addLobbies( final List<Lobby> lobbies )
		{
			final int start = m_lobbies.size();
			m_lobbies.addAll( lobbies );
			notifyItemRangeInserted( start, lobbies.size() );
			onLoadComplete( 0 );
		}

		public void onLoadComplete( final int current_page )
		{
			if (m_isLoading)
			{
				m_isLoading = false;
				notifyItemRemoved( m_lobbies.size() );
			}
		}

		public void onLoading( final int current_page )
		{
			m_isLoading = true;
			notifyItemInserted( m_lobbies.size() );
		}

		@Override
		public BaseViewHolder onCreateViewHolder(
			final ViewGroup viewGroup, final int viewType
		)
		{
			switch ( ItemViewType.values()[viewType] )
			{
				case RegularView:
					return new LobbyViewHolder( new LobbyListItemView( m_context ) );
				case LoadingView:
					return new LoadingMoreViewHolder( m_layoutInflater.inflate( R.layout.item_loading, null ) );
			}
			return null;
		}

		@Override
		public void onBindViewHolder( final BaseViewHolder viewHolder, final int i )
		{
			if ( viewHolder instanceof LobbyViewHolder )
			{
				( (LobbyViewHolder) viewHolder ).m_lobbyListItemView.setLobby( m_lobbies.get( i ) );
			}
			else if ( viewHolder instanceof LoadingMoreViewHolder )
			{
			}
		}

		@Override
		public long getItemId( final int position )
		{
			return position;
		}

		@Override
		public int getItemCount()
		{
			if ( m_isLoading )
			{
				return m_lobbies.size() + 1;
			}
			return m_lobbies.size();
		}


		@Override
		public int getItemViewType( final int position )
		{
			if ( m_isLoading && position >= m_lobbies.size() )
			{
				return ItemViewType.LoadingView.ordinal();
			}
			else if ( position < m_lobbies.size() )
			{
				return ItemViewType.RegularView.ordinal();
			}
			return super.getItemViewType( position );
		}

		public abstract class BaseViewHolder extends RecyclerView.ViewHolder
		{
			public BaseViewHolder( final View itemView )
			{
				super( itemView );
			}
		}

		public class LoadingMoreViewHolder extends BaseViewHolder
		{
			// each data item is just a string in this case
			public View m_progressBar;

			public LoadingMoreViewHolder( final View view )
			{
				super( view );
				m_progressBar = view;
				view.setLayoutParams( new RecyclerView.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT,
				                                                     ViewGroup.LayoutParams.WRAP_CONTENT ) );
			}
		}

		// Provide a reference to the views for each data item
		// Complex data items may need more than one view per item, and
		// you provide access to all the views for a data item in a view holder
		public class LobbyViewHolder extends BaseViewHolder
		{
			// each data item is just a string in this case
			public LobbyListItemView m_lobbyListItemView;

			public LobbyViewHolder( final LobbyListItemView lobbyListItemView )
			{
				super( lobbyListItemView );
				m_lobbyListItemView = lobbyListItemView;
				m_lobbyListItemView.setOnClickListener(
					new View.OnClickListener()
					{
						@Override
						public void onClick( final View v )
						{
							Navigator.ToLobby(
								lobbyListItemView.getLobby().getId(),
								lobbyListItemView.getLobby().getName(),
								false
							);
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
