package com.swarmnyc.pup.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.squareup.picasso.Picasso;
import com.swarmnyc.pup.EventBus;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.StringUtils;
import com.swarmnyc.pup.TimeUtils;
import com.swarmnyc.pup.User;
import com.swarmnyc.pup.chat.ChatMessage;
import com.swarmnyc.pup.components.Action;
import com.swarmnyc.pup.components.GamePlatformUtils;
import com.swarmnyc.pup.models.Lobby;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class LobbyChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int SYSTEM = 0;
    private static final int HEADER = 1;
    private static final int ITEM = 2;
    private static final int LOADING = 3;

    private List<ChatMessage> m_chatMessages;
    private HashSet<String> m_chatMessageIds;
    private Context m_context;
    private Lobby m_lobby;
    private LayoutInflater m_inflater;
    private Action m_reachBeginAction;
    private boolean m_isLoading = false;
    private int m_offset = 0;

    public LobbyChatAdapter(
            final Context context
    ) {
        m_context = context;
        m_inflater = (LayoutInflater) m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        m_chatMessages = new ArrayList<>();
        m_chatMessageIds = new HashSet<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View view;
        switch (viewType) {
            case ITEM:
                view = m_inflater.inflate(R.layout.item_lobby_chat, parent, false);
                return new ItemViewHolder(view);
            case SYSTEM:
                view = m_inflater.inflate(R.layout.item_lobby_chat_system, parent, false);
                return new SystemViewHolder(view);
            case HEADER:
                view = m_inflater.inflate(R.layout.item_lobby_chat_header, parent, false);
                return new HeaderViewHolder(view);
            case LOADING:
                view = m_inflater.inflate(R.layout.item_loading, parent, false);
                return new LoadingViewHolder(view);
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (position == 0 && m_reachBeginAction != null) {
            m_reachBeginAction.call(null);
        }

        if (holder instanceof ItemViewHolder) {
            ItemViewHolder item = ((ItemViewHolder) holder);
            ChatMessage message = getChatMessage(position);
            item.setCharMessage(message);
        } else if (holder instanceof SystemViewHolder) {
            if (m_chatMessages.size() > 0) {
                ((SystemViewHolder) holder).setMessage(getChatMessage(position));
            }
        } else if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).setLobby(m_lobby);
        }
    }

    @Override
    public int getItemViewType(final int position) {
        if (position == 0) {
            if (m_lobby == null) {
                return LOADING;
            } else {
                return HEADER;
            }
        } else if (m_isLoading && position == m_chatMessages.size() + 1) {
            return LOADING;
        } else if (getChatMessage(position).isSystemMessage()) {
            return SYSTEM;
        } else {
            return ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return m_chatMessages.size() + m_offset;
    }

    public void setReachBeginAction(Action action) {
        m_reachBeginAction = action;
    }

    public ChatMessage getChatMessage(final int location) {
        return m_chatMessages.get(location - 1);
    }

    public ChatMessage getFirstChatMessage() {
        return m_chatMessages.get(0);
    }

    public void addMessages(int location, List<ChatMessage> messages) {
        ChatMessage previous = null;
        if (location > 0) {
            previous = m_chatMessages.get(location - 1);
        }

        for (ChatMessage message : messages) {

            if (m_chatMessageIds.contains(message.getId())) {
                continue;
            }

            m_chatMessageIds.add(message.getId());

            if (previous != null &&
                    !previous.isSystemMessage() &&
                    !message.isSystemMessage() &&
                    message.getUser().getId().equals(
                            previous.getUser().getId()
                    ))

            {
                previous.setBody(previous.getBody() + "\r\n" + message.getBody());
            } else {
                previous = message;

                //temporary switch user;
                if (message.getUser() != null) {
                    message.setUser(m_lobby.getUser(message.getUser().getId()));
                }

                m_chatMessages.add(location++, message);
            }
        }

        notifyDataSetChanged();
    }

    public void addMessages(List<ChatMessage> messages) {
        addMessages(m_chatMessages.size(), messages);
    }

    public void setLobby(final Lobby lobby) {
        m_lobby = lobby;
        computeOffset();
    }

    public void isLoading(boolean loading) {
        this.m_isLoading = loading;
        computeOffset();
    }

    public void clear() {
        this.m_chatMessages.clear();
        this.m_chatMessageIds.clear();
    }

    private void computeOffset() {
        m_offset = (m_lobby == null ? 0 : 1) + (m_isLoading ? 1 : 0);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.contentPanel)
        ViewGroup container;

        @InjectView(R.id.img_portrait)
        ImageView portrait;

        @InjectView(R.id.text_name)
        TextView nameText;

        @InjectView(R.id.text_message)
        TextView messageText;

        public ItemViewHolder(final View view) {
            super(view);

            ButterKnife.inject(this, view);
        }

        public void setCharMessage(final ChatMessage chatMessage) {
            if (StringUtils.isEmpty(chatMessage.getUser().getPortraitUrl())) {
                portrait.setImageResource(R.drawable.default_portrait);
            } else {
                Picasso.with(m_context).load(chatMessage.getUser().getPortraitUrl()).into(portrait);
            }

            nameText.setText(chatMessage.getUser().getUserName());
            messageText.setText(chatMessage.getBody());

            if (chatMessage.getUser().getId().equals(User.current.getId())) {
                container.setBackgroundResource(R.color.pup_my_chat);
            } else {
                container.setBackgroundResource(R.color.pup_white);
            }
        }
    }

    class SystemViewHolder extends RecyclerView.ViewHolder {
        public SystemViewHolder(final View view) {
            super(view);
        }

        public void setMessage(final ChatMessage chatMessage) {
            ((TextView) itemView).setText(chatMessage.getBody());
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.text_description)
        TextView descriptionText;

        @InjectView(R.id.text_lobby_type)
        TextView typeText;

        @InjectView(R.id.text_time)
        TextView timeText;

        public HeaderViewHolder(final View view) {
            super(view);
            ButterKnife.inject(this, view);
        }

        public void setLobby(final Lobby lobby) {
            descriptionText.setText(lobby.getDescription());
            typeText.setText(lobby.getPlayStyle().name() + " | " + lobby.getSkillLevel().name());

            long offset = m_lobby.getStartTime().getTime() - TimeUtils.todayTimeMillis();
            String time;
            if (offset < 0) {
                SimpleDateFormat format = new SimpleDateFormat("MMM dd @ h:mm a", Locale.getDefault());
                time = "Started " + format.format(m_lobby.getStartTime());
            } else if (offset < TimeUtils.day_in_millis) {
                SimpleDateFormat format = new SimpleDateFormat("@ h:mm a", Locale.getDefault());
                time = "Today " + format.format(m_lobby.getStartTime());
            } else if (offset < TimeUtils.week_in_millis) {
                SimpleDateFormat format = new SimpleDateFormat("EEEE @ h:mm a", Locale.getDefault());
                time = format.format(m_lobby.getStartTime());
            } else {
                SimpleDateFormat format = new SimpleDateFormat("MMM dd @ h:mm a", Locale.getDefault());
                time = format.format(m_lobby.getStartTime());
            }

            time = String.format("%s: %s", GamePlatformUtils.labelForPlatform(m_context, m_lobby.getPlatform()), time);

            timeText.setText(time);
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(final View view) {
            super(view);
        }
    }
}
