package com.swarmnyc.pup.fragments;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.Bind;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.swarmnyc.pup.Config;
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
import com.swarmnyc.pup.components.GamePlatformUtils;
import com.swarmnyc.pup.helpers.SoftKeyboardHelper;
import com.swarmnyc.pup.components.Utility;
import com.swarmnyc.pup.models.Game;
import com.swarmnyc.pup.models.GamePlatform;
import com.swarmnyc.pup.view.GamePlatformSelectView;
import com.swarmnyc.pup.viewmodels.LobbySearchResult;
import com.uservoice.uservoicesdk.UserVoice;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class LobbyListFragment extends BaseFragment {
    @Bind(R.id.txt_game_serach)
    public AutoCompleteTextView m_gameSearch;
    @Bind(R.id.layout_sliding_panel)
    public SlidingUpPanelLayout m_slidingPanel;
    @Bind(R.id.list_lobby)
    public RecyclerView m_lobbyRecyclerView;
    @Bind(R.id.platform_select)
    public GamePlatformSelectView m_gamePlatformSelectView;
    @Bind(R.id.layout_empty_results)
    public ViewGroup m_emptyResults;
    @Bind(R.id.layout_refresh)
    public SwipeRefreshLayout m_refreshLayout;
    @Bind(R.id.slidePanel)
    public ViewGroup m_slidePanel;
    @Bind(R.id.text_empty_results)
    public TextView m_noResultView;

    private GameService gameService;
    private LobbyService lobbyService;

    private LayoutInflater inflater;
    private MainActivity activity;
    private LobbyAdapter m_lobbyAdapter;
    private LinearLayoutManager mLayoutManager;
    private LobbyFilter m_lobbyFilter = new LobbyFilter();
    private GameFilter m_gameFilter = new GameFilter();
    private AutoCompleteForPicturedModelAdapter<Game> gameAdapter;
    private AtomicBoolean m_isLoading = new AtomicBoolean(false);
    private Action<Object> m_loadMore;

    public LobbyListFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
    }


    @Override
    public void onCreate(
            @Nullable
            final Bundle savedInstanceState
    ) {
        Log.d("LobbyListFragment", String.format("onCreate (savedInstanceState = %s)", savedInstanceState));

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
    ) {
        gameService = PuPApplication.getInstance().getModule().provideGameService();
        lobbyService = PuPApplication.getInstance().getModule().provideLobbyService();

        View view = inflater.inflate(R.layout.fragment_lobby_list, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        m_gamePlatformSelectView.setPlatformSelectionChangedListener(
                new GamePlatformSelectView.OnPlatformSelectionChangedListener() {
                    @Override
                    public void onPlatformSelectionChanged(final View v) {

                        m_lobbyFilter.setPlatformList(m_gamePlatformSelectView.getSelectedGamePlatforms());
                        if (v.isSelected()) // only collapse if something is selected.
                        {
                            m_slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        }
                        reloadData(true);
                    }
                }
        );

        gameAdapter = new AutoCompleteForPicturedModelAdapter<Game>(this.getActivity());

        gameAdapter.setSearchAction(
                new Action<CharSequence>() {
                    @Override
                    public void call(CharSequence constraint) {
                        m_gameFilter.setSearch(constraint.toString());
                        gameService.getGames(
                                m_gameFilter, new ServiceCallback<List<Game>>() {
                                    @Override
                                    public void success(List<Game> value) {
                                        if (value.size() == 0) {
                                            Game game = new Game();
                                            game.setThumbnailPictureUrl(
                                                    Utility.getResourceUri(
                                                            getActivity(), R.drawable.ico_plus
                                                    ).toString()
                                            );
                                            game.setName(getString(R.string.text_request_game));
                                            value.add(game);
                                        }

                                        gameAdapter.finishSearch(value);
                                    }
                                }
                        );
                    }
                }
        );

        m_gameSearch.setAdapter(gameAdapter);

        m_gameSearch.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Game selectedGame = gameAdapter.getItem(position);
                        if (selectedGame.getId() == null) {
                            m_gameSearch.setText("");
                            UserVoice.launchPostIdea(getActivity());
                        } else {
                            m_lobbyFilter.setGame(selectedGame);

                            reloadData(true);
                            SoftKeyboardHelper.hideSoftKeyboard(getActivity());
                            m_slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        }
                    }
                }
        );

        m_gameSearch.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            final CharSequence s, final int start, final int count, final int after
                    ) {

                    }

                    @Override
                    public void onTextChanged(
                            final CharSequence s, final int start, final int before, final int count
                    ) {

                    }

                    @Override
                    public void afterTextChanged(final Editable s) {
                        if (s.length() == 0) {
                            m_lobbyFilter.setGame(null);
                            reloadData(true);
                        }
                    }
                }
        );

        m_slidingPanel.setPanelSlideListener(
                new SlidingUpPanelLayout.PanelSlideListener() {
                    @Override
                    public void onPanelSlide(final View view, final float v) {
                        // Hide Keyboard
                        SoftKeyboardHelper.hideSoftKeyboard(getActivity());

                        // show the overlay programmingly, the package's overlay cannot block touch event so I add a Layout to do this thing.
                        if (v > 0.95) {
                            m_slidePanel.setBackgroundResource(android.R.drawable.screen_background_dark_transparent);

                        } else {
                            m_slidePanel.setBackgroundColor(0);
                        }
                    }

                    @Override
                    public void onPanelCollapsed(final View view) {

                    }

                    @Override
                    public void onPanelExpanded(final View view) {
                    }


                    @Override
                    public void onPanelAnchored(final View view) {
                    }

                    @Override
                    public void onPanelHidden(final View view) {
                    }
                }
        );

        setHasOptionsMenu(true);

        this.inflater = inflater;

        m_lobbyAdapter = new LobbyAdapter(getActivity());
        m_lobbyRecyclerView.setAdapter(m_lobbyAdapter);

        mLayoutManager = new LinearLayoutManager(getActivity());

        m_lobbyRecyclerView.setLayoutManager(mLayoutManager);

        m_loadMore = new Action<Object>() {
            @Override
            public void call(final Object value) {
                Log.d("LobbyListFragment", "Load More");
                m_lobbyRecyclerView.postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                reloadData(false);
                            }
                        }, 500
                );
            }
        };

        m_refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        reloadData(true);
                    }
                }
        );

        if (null != savedInstanceState) {
            m_lobbyFilter = Utility.fromJson(savedInstanceState.getString("filter"), LobbyFilter.class);
            m_gamePlatformSelectView.setSelectedGamePlatforms(m_lobbyFilter.getPlatforms());
        }

        return view;
    }

    @Override
    public void onViewCreated(
            final View view,
            @Nullable
            final Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("LobbyListFragment", String.format("onViewCreated (%s, savedInstanceState = %s)", view, savedInstanceState)
        );
        if (null == savedInstanceState) {
            reloadData(true);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onResume() {
        super.onResume();

        if (Config.getBool(Consts.KEY_NEED_UPDATE_LIST)) {
            Config.setBool(Consts.KEY_NEED_UPDATE_LIST, false);
            reloadData(true);
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        outState.putString("filter", Utility.toJson(m_lobbyFilter));

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.menu_all_lobbies, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int itemId = item.getItemId();

        if (itemId == R.id.menu_filter) {
            if (m_slidingPanel != null) {
                if (m_slidingPanel.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    m_slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                } else {
                    m_slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void reloadData(final boolean restart) {
        if (m_isLoading.getAndSet(true)) {
            return; //Sometimes different event will trigger at the same time;
        }

        if (restart) {
            m_lobbyFilter.setNeedCount(true);
            m_lobbyFilter.setStartTime(new Date());
        } else {
            m_lobbyFilter.setNeedCount(false);
            m_lobbyFilter.setStartTime(m_lobbyAdapter.getLastItem().getStartTime());
        }

        if (m_emptyResults.getVisibility() == View.VISIBLE) // Hide empty results before loading
        {
            com.swarmnyc.pup.components.ViewAnimationUtils.hideWithAnimation(getActivity(), m_emptyResults);
        }

        m_lobbyAdapter.startLoading();

        lobbyService.getLobbies(
                m_lobbyFilter, new ServiceCallback<LobbySearchResult>() {
                    @Override
                    public void success(LobbySearchResult result) {
                        if (isDetached()) return;

                        m_refreshLayout.setRefreshing(false);
                        m_isLoading.set(false);
                        m_lobbyAdapter.endLoading();
                        if (restart) {
                            m_lobbyAdapter.setItem(result.getResult());
                            m_lobbyAdapter.setCount(result.getCounts());
                        } else {
                            m_lobbyAdapter.addItem(result.getResult());
                        }

                        m_lobbyAdapter.setReachEndAction(Consts.PAGE_SIZE == result.getResult().size() ? m_loadMore : null);

                        if (restart && result.getResult().size() == 0) {
                            m_noResultView.setText(R.string.message_no_lobbies);
                            com.swarmnyc.pup.components.ViewAnimationUtils.showWithAnimation(getActivity(), m_emptyResults);
                        } else {
                            com.swarmnyc.pup.components.ViewAnimationUtils.hideWithAnimation(getActivity(), m_emptyResults);
                        }
                    }

                    @Override
                    public void failure(String message) {
                        m_refreshLayout.setRefreshing(false);
                        m_isLoading.set(false);
                        m_lobbyAdapter.endLoading();

                        if (m_lobbyAdapter.getItemCount() == 0) {
                            com.swarmnyc.pup.components.ViewAnimationUtils.showWithAnimation(
                                    getActivity(), m_emptyResults
                            );
                        }
                    }
                }
        );

        if (Consts.currentPage == 0)
            updateTitle();
    }

    @Override
    public void updateTitle() {
        String title = null == m_lobbyFilter.getGame() ? "All Games" : m_lobbyFilter.getGame().getName();
        setTitle(title);
        setSubtitle(null);

        if (m_lobbyFilter.getPlatforms().size() > 0) {
            List<GamePlatform> platforms = new ArrayList<>(m_lobbyFilter.getPlatforms());

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < platforms.size(); i++) {
                final GamePlatform platform = platforms.get(i);

                if (i > 0) {
                    stringBuilder.append(", ");
                }

                stringBuilder.append(GamePlatformUtils.labelForPlatform(getActivity(), platform));

            }

            setSubtitle(stringBuilder.toString());
        }
    }

    @Override
    public String getScreenName() {
        return "Lobby List";
    }
}
