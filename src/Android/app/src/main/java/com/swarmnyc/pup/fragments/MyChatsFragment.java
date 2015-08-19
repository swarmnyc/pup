package com.swarmnyc.pup.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.swarmnyc.pup.Config;
import com.swarmnyc.pup.Consts;
import com.swarmnyc.pup.EventBus;
import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.Services.Filter.LobbyFilter;
import com.swarmnyc.pup.Services.LobbyService;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.adapters.MyChatAdapter;
import com.swarmnyc.pup.components.Action;
import com.swarmnyc.pup.events.ChatMessageReceiveEvent;
import com.swarmnyc.pup.models.Lobby;
import com.swarmnyc.pup.view.DividerItemDecoration;

import java.util.List;


import butterknife.Bind;
import butterknife.ButterKnife;

public class MyChatsFragment extends BaseFragment {
    @Bind(R.id.text_empty_results)
    public TextView m_noResultView;
    @Bind(R.id.list_chat)
    RecyclerView m_chatList;
    @Bind(R.id.layout_refresh)
    SwipeRefreshLayout m_refreshLayout;
    @Bind(R.id.layout_empty_results)
    ViewGroup m_emptyResults;
    private int pageIndex = -1;
    private LobbyService m_lobbyService;
    private MyChatAdapter m_myChatAdapter;
    private Action<Object> m_loadMore;

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
        m_lobbyService = PuPApplication.getInstance().getModule().provideLobbyService();

        m_myChatAdapter = new MyChatAdapter(this.getActivity());
        m_loadMore = new Action<Object>() {
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
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getBus().register(this);

        if (pageIndex == -1 || Config.getBool(Consts.KEY_NEED_UPDATE_MY)) {
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
                            m_noResultView.setText(R.string.message_no_lobbies);
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
