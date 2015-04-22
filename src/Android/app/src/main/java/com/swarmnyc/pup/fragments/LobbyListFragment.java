package com.swarmnyc.pup.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.Services.Filter.LobbyFilter;
import com.swarmnyc.pup.Services.LobbyService;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.activities.CreateLobbyActivity;
import com.swarmnyc.pup.activities.LobbyActivity;
import com.swarmnyc.pup.activities.MainActivity;
import com.swarmnyc.pup.components.AnimationEndListener;
import com.swarmnyc.pup.components.AnimationStartListener;
import com.swarmnyc.pup.models.Lobby;
import com.swarmnyc.pup.view.LobbyListItemView;

import retrofit.client.Response;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;

public class LobbyListFragment extends Fragment {
    private MainActivity activity;
    private LobbyAdapter m_lobbyAdapter;
    private LinearLayoutManager mLayoutManager;

    public LobbyListFragment() {
    }

    @InjectView(R.id.layout_sliding_panel)
    public SlidingUpPanelLayout m_slidingPanel;

    @InjectView(R.id.list_lobby)
    public RecyclerView m_lobbyRecyclerView;

    @InjectView(R.id.btn_create_lobby)
    public Button m_createLobbyButton;

    @OnClick(R.id.btn_create_lobby)
    public void onCreateLobbyButtonClicked() {
        this.startActivityForResult(
                new Intent(this.activity, CreateLobbyActivity.class), CreateLobbyActivity.REQUEST_CODE_CREATE_LOBBY
        );
    }

    @Inject
    LobbyService lobbyService;

    LayoutInflater inflater;

    LobbyFilter filter = new LobbyFilter();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
    ) {
        MainActivity.getInstance().showToolbar();
        PuPApplication.getInstance().getComponent().inject(this);
        View view = inflater.inflate(R.layout.fragment_lobby_list, container, false);
        ButterKnife.inject(this, view);
        setHasOptionsMenu(true);


        m_slidingPanel.setOverlayed(true);

        m_slidingPanel.setPanelSlideListener(
                new SlidingUpPanelLayout.PanelSlideListener() {
                    @Override
                    public void onPanelSlide(final View view, final float v) {

                    }

                    @Override
                    public void onPanelCollapsed(final View view) {
                        final Animation animation = AnimationUtils.loadAnimation(
                                getActivity(), R.anim.abc_grow_fade_in_from_bottom
                        );
                        m_createLobbyButton.startAnimation(animation);
                        animation.setAnimationListener(
                                new AnimationStartListener() {
                                    @Override
                                    public void onAnimationStart(final Animation animation) {
                                        m_createLobbyButton.setVisibility(View.VISIBLE);
                                    }
                                }
                        );


                    }

                    @Override
                    public void onPanelExpanded(final View view) {
                        final Animation animation = AnimationUtils.loadAnimation(
                                getActivity(), R.anim.abc_shrink_fade_out_from_bottom
                        );
                        m_createLobbyButton.startAnimation(animation);
                        animation.setAnimationListener(
                                new AnimationEndListener() {

                                    @Override
                                    public void onAnimationEnd(final Animation animation) {
                                        m_createLobbyButton.setVisibility(View.GONE);
                                    }
                                }
                        );


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
        /*m_lobbyRecyclerView.setOnItemClickListener(
            new AdapterView.OnItemClickListener()
			{
				@Override public void onItemClick( AdapterView<?> parent, View view, int position, long id )
				{
					Lobby lobby = (Lobby) parent.getAdapter().getItem( position );
					Intent intent = new Intent( LobbyListFragment.this.activity, LobbyActivity.class );
					intent.putExtra( "lobbyId", lobby.getId() );
					LobbyListFragment.this.activity.startActivity( intent );
				}
			}
		);*/

        //		m_createLobbyButton.setVisibility( User.isLoggedIn() ? View.VISIBLE : View.GONE ); Button should be
        // visile aciton should be different.

        m_lobbyAdapter = new LobbyAdapter(getActivity());
        m_lobbyRecyclerView.setAdapter(m_lobbyAdapter);

        mLayoutManager = new LinearLayoutManager(getActivity());
        m_lobbyRecyclerView.setLayoutManager(mLayoutManager);

        m_lobbyRecyclerView.setItemAnimator(new LobbyItemAnimator());

        reloadData();

        return view;
    }

    @Override
    public void onStart() {
        reloadData();

        super.onStart();
    }


    private void reloadData() {
        lobbyService.getLobbies(
                filter, new ServiceCallback<List<Lobby>>() {
                    @Override
                    public void success(List<Lobby> lobbies) {
                        m_lobbyAdapter.setLobbies(lobbies);
                    }
                }
        );
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
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

    private class LobbyAdapter extends RecyclerView.Adapter<LobbyAdapter.ViewHolder> {
        private List<Lobby> m_lobbies = new ArrayList<>();
        Context m_context;

        private LobbyAdapter(final Context context) {
            m_context = context;
        }

        public void setLobbies(final List<Lobby> lobbies) {
            m_lobbies = lobbies;
            notifyDataSetChanged();
        }

        public List<Lobby> getLobbies() {
            return m_lobbies;
        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public LobbyListItemView m_lobbyListItemView;

            public ViewHolder(final LobbyListItemView lobbyListItemView) {
                super(lobbyListItemView);
                m_lobbyListItemView = lobbyListItemView;
                m_lobbyListItemView.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(final View v) {

                                Lobby lobby = (Lobby) lobbyListItemView.getLobby();
                                Intent intent = new Intent(LobbyListFragment.this.activity, LobbyActivity.class);
                                intent.putExtra("lobbyId", lobby.getId());
                                LobbyListFragment.this.activity.startActivity(intent);
                            }
                        }
                );
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(
                final ViewGroup viewGroup, final int i
        ) {
            return new ViewHolder(new LobbyListItemView(m_context));
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
            viewHolder.m_lobbyListItemView.setLobby(m_lobbies.get(i));
        }

        @Override
        public long getItemId(final int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return m_lobbies.size();
        }


    }

    private static class LobbyItemAnimator extends RecyclerView.ItemAnimator {
        @Override
        public void runPendingAnimations() {

        }

        @Override
        public boolean animateRemove(
                final RecyclerView.ViewHolder viewHolder
        ) {
            return true;
        }

        @Override
        public boolean animateAdd(final RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        @Override
        public boolean animateMove(
                final RecyclerView.ViewHolder viewHolder, final int i, final int i2, final int i3, final int i4
        ) {
            return false;
        }

        @Override
        public boolean animateChange(
                final RecyclerView.ViewHolder viewHolder,
                final RecyclerView.ViewHolder viewHolder2,
                final int i,
                final int i2,
                final int i3,
                final int i4
        ) {
            return false;
        }

        @Override
        public void endAnimation(final RecyclerView.ViewHolder viewHolder) {

        }

        @Override
        public void endAnimations() {

        }

        @Override
        public boolean isRunning() {
            return false;
        }
    }
}
