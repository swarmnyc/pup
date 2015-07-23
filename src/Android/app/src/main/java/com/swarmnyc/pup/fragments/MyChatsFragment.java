package com.swarmnyc.pup.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Bind;

import com.squareup.otto.Subscribe;
import com.swarmnyc.pup.*;
import com.swarmnyc.pup.Services.Filter.LobbyFilter;
import com.swarmnyc.pup.Services.LobbyService;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.adapters.MyChatAdapter;
import com.swarmnyc.pup.components.Action;
import com.swarmnyc.pup.components.UnreadCounter;
import com.swarmnyc.pup.events.ChatMessageReceiveEvent;
import com.swarmnyc.pup.models.Lobby;
import com.swarmnyc.pup.view.DividerItemDecoration;

import javax.inject.Inject;

import java.util.List;

public class MyChatsFragment extends BaseFragment {
    @Inject
    LobbyService m_lobbyService;

    @Bind(R.id.list_chat)
    RecyclerView m_chatList;
    @Bind(R.id.layout_refresh)
    SwipeRefreshLayout m_refreshLayout;
    @Bind(R.id.layout_empty_results)
    ViewGroup m_emptyResults;

    private int pageIndex;
    private MyChatAdapter m_myChatAdapter;
    private Action m_loadMore;

    @Override
    public String getScreenName() {
        return "My Lobbies";
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_my_chats, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        PuPApplication.getInstance().getComponent().inject(this);
        pageIndex = 0;
        m_myChatAdapter = new MyChatAdapter(this.getActivity());
        m_loadMore = new Action() {
            @Override
            public void call(Object value) {
                Log.d("MyChats", "Load More");

                fetchMoreData();
            }
        };

        m_chatList.setAdapter(m_myChatAdapter);
        m_chatList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        m_chatList.addItemDecoration(
                new DividerItemDecoration(
                        getActivity(), DividerItemDecoration.VERTICAL_LIST
                )
        );

        m_refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        reloadData();
                    }
                }
        );

        if (savedInstanceState == null) {
            fetchMoreData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getBus().register(this);

        if (Config.getBool(Consts.KEY_NEED_UPDATE_MY)) {
            Config.setBool(Consts.KEY_NEED_UPDATE_MY, false);
            reloadData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getBus().unregister(this);
    }

    public void updateTitle() {
        setTitle(R.string.text_lobbies);
        setSubtitle(null);
    }

    private void reloadData() {
        pageIndex = 0;
        m_myChatAdapter.removeLobbies();
        fetchMoreData();
    }

    private void fetchMoreData() {
        LobbyFilter filter = new LobbyFilter();
        filter.setPageIndex(pageIndex++);
        m_lobbyService.getMyLobbies(
                filter, new ServiceCallback<List<Lobby>>() {
                    @Override
                    public void success(final List<Lobby> value) {
                        m_refreshLayout.setRefreshing(false);
                        if (!isAdded()) {
                            return;
                        }

                        if (value.size() == 0) {
                            m_myChatAdapter.setReachEndAction(null);
                        } else {
                            if (value.size() == Consts.PAGE_SIZE) {
                                m_myChatAdapter.setReachEndAction(m_loadMore);
                            }

                            m_myChatAdapter.AddLobbies(value);
                        }

                        if (value.size() == 0) {
                            com.swarmnyc.pup.components.ViewAnimationUtils.showWithAnimation(getActivity(), m_emptyResults);
                        } else {
                            com.swarmnyc.pup.components.ViewAnimationUtils.hideWithAnimation(getActivity(), m_emptyResults);
                        }
                    }

                    @Override
                    public void failure(String message) {
                        m_refreshLayout.setRefreshing(false);

                        if (m_myChatAdapter.getItemCount() == 0) {
                            com.swarmnyc.pup.components.ViewAnimationUtils.showWithAnimation(getActivity(), m_emptyResults);
                        }
                    }
                }
        );
    }

    @Subscribe
    public void receiveMessage(final ChatMessageReceiveEvent event) {
        if (!event.isNewMessage() && isDetached()) {
            return;
        }

        //updateTitle();
        m_myChatAdapter.updateLastMessage(event);
    }
}
