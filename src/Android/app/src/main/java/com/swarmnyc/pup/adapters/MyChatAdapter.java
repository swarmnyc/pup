package com.swarmnyc.pup.adapters;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.Bind;

import com.squareup.picasso.Picasso;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.StringUtils;
import com.swarmnyc.pup.chat.ChatMessage;
import com.swarmnyc.pup.components.Action;
import com.swarmnyc.pup.components.GamePlatformUtils;
import com.swarmnyc.pup.components.Navigator;
import com.swarmnyc.pup.components.UnreadCounter;
import com.swarmnyc.pup.events.ChatMessageReceiveEvent;
import com.swarmnyc.pup.models.Lobby;
import com.swarmnyc.pup.models.PuPTag;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyChatAdapter extends RecyclerView.Adapter<MyChatAdapter.MyChatViewHolder> {
    private final LayoutInflater m_inflater;
    private final Activity m_activity;
    private final List<Lobby> m_lobbies;

    private Action m_reachEndAction;

    public MyChatAdapter(final Activity activity) {
        m_activity = activity;
        m_inflater = m_activity.getLayoutInflater();
        m_lobbies = new ArrayList<>();
    }

    public void AddLobbies(List<Lobby> newLobbies) {
        int start = m_lobbies.size();
        for (Lobby newLobby : newLobbies) {
            UnreadCounter.reset(newLobby.getRoomId(), newLobby.getUnreadMessageCount());
        }

        m_lobbies.addAll(newLobbies);
        notifyItemRangeInserted(start, newLobbies.size());
    }

    public void AddLobby(Lobby newLobby) {
        int start = m_lobbies.size();
        m_lobbies.add(newLobby);
        notifyItemInserted(start + 1);
    }

    public void removeLobbies() {
        m_lobbies.clear();
        notifyDataSetChanged();
    }

    public void setReachEndAction(Action action) {
        m_reachEndAction = action;
    }

    @Override
    public MyChatViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return new MyChatViewHolder(m_inflater.inflate(R.layout.item_my_lobby, null));
    }

    @Override
    public void onBindViewHolder(final MyChatViewHolder holder, final int position) {
        holder.setLobby(m_lobbies.get(position));
        if (m_reachEndAction != null && position == m_lobbies.size() - 1) {
            m_reachEndAction.call(null);
        }
    }

    @Override
    public int getItemCount() {
        return m_lobbies.size();
    }

    public void updateLastMessage(final ChatMessageReceiveEvent event) {
        for (int i = 0; i < m_lobbies.size(); i++) {
            Lobby lobby = m_lobbies.get(i);
            if (event.getRoomId().equals(lobby.getRoomId())) {
                ChatMessage message = event.getMessages().get(event.getMessages().size() - 1);
                lobby.setLastMessage(message.getBody());
                lobby.setLastMessageAt(new Date(message.getSentAt() * 1000));

                lobby.getTags().add(new PuPTag("SHINE", "1"));
                notifyItemChanged(i);
                break;
            }
        }
    }

    public class MyChatViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.img_game)
        ImageView m_gameImage;
        @Bind(R.id.img_game_border)
        View m_gameImageBorder;
        @Bind(R.id.txt_game_name)
        TextView m_gameName;
        @Bind(R.id.txt_game_time)
        TextView m_gameTime;
        @Bind(R.id.txt_lastMessage)
        TextView m_lastMessage;
        @Bind(R.id.txt_platform)
        TextView m_platform;

        private Lobby m_lobby;

        public MyChatViewHolder(final View itemView) {
            super(itemView);

            itemView.setTag(this);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            Navigator.ToLobby(m_activity, m_lobby);
                        }
                    }
            );
        }

        public void setLobby(final Lobby lobby) {
            m_lobby = lobby;

            if (StringUtils.isNotEmpty(lobby.getPictureUrl())) {
                Picasso.with(m_activity).load(lobby.getPictureUrl()).into(m_gameImage);
            }

            m_gameName.setText(lobby.getName());

            m_lastMessage.setText(lobby.getLastMessage());

            m_platform.setText(GamePlatformUtils.labelResIdForPlatform(lobby.getPlatform()));

            //TODO a better time indication
            long time = lobby.getStartTime().getTime();
            if (time > System.currentTimeMillis()) {
                m_gameTime.setText(DateUtils.getRelativeTimeSpanString(time));
            } else {
                m_gameTime.setText("Game Expired");
            }

            if (UnreadCounter.get(m_lobby.getRoomId()) == 0) {
                m_gameImageBorder.setVisibility(View.GONE);
            } else {
                m_gameImageBorder.setVisibility(View.VISIBLE);
            }

            if (lobby.getTagValueAndRemove("SHINE") != null) {
                final Animator animator = AnimatorInflater.loadAnimator(m_activity, R.animator.blinking);
                animator.setTarget(m_gameImageBorder);
                animator.start();
            }
        }
    }
}
