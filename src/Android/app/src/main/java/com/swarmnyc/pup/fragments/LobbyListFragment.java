package com.swarmnyc.pup.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.Services.Filter.GameFilter;
import com.swarmnyc.pup.Services.Filter.LobbyFilter;
import com.swarmnyc.pup.Services.GameService;
import com.swarmnyc.pup.Services.LobbyService;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.activities.LobbyActivity;
import com.swarmnyc.pup.activities.MainActivity;
import com.swarmnyc.pup.adapters.AutoCompleteForPicturedModelAdapter;
import com.swarmnyc.pup.components.Action;
import com.swarmnyc.pup.components.Navigator;
import com.swarmnyc.pup.models.Game;
import com.swarmnyc.pup.models.Lobby;
import com.swarmnyc.pup.view.GamePlatformSelectView;
import com.swarmnyc.pup.view.LobbyListItemView;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class LobbyListFragment extends Fragment
{
	private MainActivity        activity;
	private LobbyAdapter        m_lobbyAdapter;
	private LinearLayoutManager mLayoutManager;
	private LobbyFilter m_lobbyFilter = new LobbyFilter();
	private GameFilter  m_gameFilter  = new GameFilter();
	private AutoCompleteForPicturedModelAdapter<Game> gameAdapter;
	private Game                                      selectedGame;

	public LobbyListFragment()
	{
	}

	@Inject                                         GameService          gameService;
	@InjectView( R.id.txt_game_serach ) public      AutoCompleteTextView m_gameSearch;
	@InjectView( R.id.layout_sliding_panel ) public SlidingUpPanelLayout m_slidingPanel;

	@InjectView( R.id.list_lobby ) public RecyclerView m_lobbyRecyclerView;

	@InjectView( R.id.btn_create_lobby ) public     ImageButton            m_createLobbyButton;
	@InjectView( R.id.view_platform_select ) public GamePlatformSelectView m_gamePlatformSelectView;
	@InjectView( R.id.layout_empty_results ) public ViewGroup              m_emptyResults;

	//	@OnClick( R.id.btn_create_lobby ) public void onCreateLobbyButtonClicked()
	//	{
	//		this.startActivityForResult(
	//			new Intent( this.activity, CreateLobbyActivity.class ), CreateLobbyActivity.REQUEST_CODE_CREATE_LOBBY
	//		);
	//	}
	//
	@OnClick( R.id.btn_create_lobby ) public void onCreateLobbyButtonClicked()
	{
		Navigator.ToCreateLobby();
	}

	@Inject LobbyService lobbyService;

	float m_panelSize = 0.0f;

	LayoutInflater inflater;


	@Override public View onCreateView(
		LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
	)
	{
		MainActivity.getInstance().showToolbar();
		PuPApplication.getInstance().getComponent().inject( this );
		View view = inflater.inflate( R.layout.fragment_lobby_list, container, false );
		ButterKnife.inject( this, view );
		setHasOptionsMenu( true );

		m_gamePlatformSelectView.setPlatformSelectionChangedListener(
			new GamePlatformSelectView.OnPlatformSelectionChangedListener()
			{
				@Override public void onPlatformSelectionChanged()
				{

					m_lobbyFilter.setPlatformList( m_gamePlatformSelectView.getSelectedGamePlatforms() );
					reloadData();
				}
			}
		);

		gameAdapter = new AutoCompleteForPicturedModelAdapter<Game>( this.getActivity() );

		gameAdapter.setSearchAction(
			new Action<CharSequence>()
			{
				@Override public void call( CharSequence constraint )
				{
					m_gameFilter.setSearch( constraint.toString() );
					gameService.getGames(
						m_gameFilter, new ServiceCallback<List<Game>>()
						{
							@Override public void success( List<Game> value )
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
				@Override public void onItemClick( AdapterView<?> parent, View view, int position, long id )
				{
					selectedGame = gameAdapter.getItem( position );
					m_lobbyFilter.setGame( selectedGame );
					reloadData();

				}
			}
		);

		m_gameSearch.addTextChangedListener(
			new TextWatcher()
			{
				@Override public void beforeTextChanged(
					final CharSequence s, final int start, final int count, final int after
				)
				{

				}

				@Override public void onTextChanged(
					final CharSequence s, final int start, final int before, final int count
				)
				{

				}

				@Override public void afterTextChanged( final Editable s )
				{
					if (s.length() == 0)
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
				@Override public void onPanelSlide( final View view, final float v )
				{
					Log.d( "LobbyListFragment", String.format( "onPanelSlide ([view, %f])", v ) );

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

				@Override public void onPanelCollapsed( final View view )
				{
					// Hide Keyboard
					InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
						Context.INPUT_METHOD_SERVICE
					);
					imm.hideSoftInputFromWindow( m_gameSearch.getWindowToken(), 0 );
				}

				@Override public void onPanelExpanded( final View view )
				{

				}


				@Override public void onPanelAnchored( final View view )
				{


				}

				@Override public void onPanelHidden( final View view )
				{

				}
			}
		);

		setHasOptionsMenu( true );

		this.inflater = inflater;


		//		m_createLobbyButton.setVisibility( User.isLoggedIn() ? View.VISIBLE : View.GONE ); Button should be
		// visile aciton should be different.

		m_lobbyAdapter = new LobbyAdapter( getActivity() );
		m_lobbyRecyclerView.setAdapter( m_lobbyAdapter );

		mLayoutManager = new LinearLayoutManager( getActivity() );
		m_lobbyRecyclerView.setLayoutManager( mLayoutManager );


		return view;
	}

	@Override public void onStart()
	{
		reloadData();

		super.onStart();
	}


	private void reloadData()
	{
		final ProgressDialog progressDialog = ProgressDialog.show( getActivity(), "", "Loading games", true, false );
		if ( m_emptyResults.getVisibility() == View.VISIBLE ) // Hide empty results before loading
		{
			com.swarmnyc.pup.components.ViewAnimationUtils.hideWithAnimation( getActivity(), m_emptyResults );
		}

		lobbyService.getLobbies(
			m_lobbyFilter, new ServiceCallback<List<Lobby>>()
			{
				@Override public void success( List<Lobby> lobbies )
				{
					m_lobbyAdapter.setLobbies( lobbies );
					progressDialog.dismiss();
					if ( lobbies.size() == 0 )
					{
						com.swarmnyc.pup.components.ViewAnimationUtils.showWithAnimation(
							getActivity(), m_emptyResults
						);
					}
				}
			}
		);
	}

	@Override public void onAttach( Activity activity )
	{
		super.onAttach( activity );
		this.activity = (MainActivity) activity;
	}


	@Override public void onCreateOptionsMenu( final Menu menu, final MenuInflater inflater )
	{
		inflater.inflate( R.menu.menu_all_lobbies, menu );
		super.onCreateOptionsMenu( menu, inflater );
	}

	@Override public boolean onOptionsItemSelected( final MenuItem item )
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

	private class LobbyAdapter extends RecyclerView.Adapter<LobbyAdapter.ViewHolder>
	{
		private List<Lobby> m_lobbies = new ArrayList<>();
		Context m_context;

		private LobbyAdapter( final Context context )
		{
			m_context = context;
		}

		public void setLobbies( final List<Lobby> lobbies )
		{
			m_lobbies = lobbies;
			notifyDataSetChanged();
		}

		public List<Lobby> getLobbies()
		{
			return m_lobbies;
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
						@Override public void onClick( final View v )
						{

							Lobby lobby = (Lobby) lobbyListItemView.getLobby();
							Intent intent = new Intent( LobbyListFragment.this.activity, LobbyActivity.class );
							intent.putExtra( "lobbyId", lobby.getId() );
							LobbyListFragment.this.activity.startActivity( intent );
						}
					}
				);
			}
		}

		@Override public ViewHolder onCreateViewHolder(
			final ViewGroup viewGroup, final int i
		)
		{
			return new ViewHolder( new LobbyListItemView( m_context ) );
		}

		@Override public void onBindViewHolder( final ViewHolder viewHolder, final int i )
		{
			viewHolder.m_lobbyListItemView.setLobby( m_lobbies.get( i ) );
		}

		@Override public long getItemId( final int position )
		{
			return position;
		}

		@Override public int getItemCount()
		{
			return m_lobbies.size();
		}


	}

}
