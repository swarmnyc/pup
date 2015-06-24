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
import com.swarmnyc.pup.*;
import com.swarmnyc.pup.chat.ChatMessage;
import com.swarmnyc.pup.chat.ChatRoomService;
import com.swarmnyc.pup.models.Lobby;

import java.util.LinkedList;
import java.util.List;

public class LobbyChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int HEADER = 0;
    private static final int SYSTEM = -1;
    private static final int ITEM = 1;
    ChatRoomService m_chatRoomService;
    List<ChatMessage> m_chatMessages;
    private Context m_context;
    private Lobby m_lobby;
    private LayoutInflater m_inflater;

    public LobbyChatAdapter(
            final Context context, final Lobby lobby
    ) {
        m_context = context;
        m_lobby = lobby;
        m_inflater = (LayoutInflater) m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        m_chatMessages = new LinkedList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        if (viewType == HEADER) {
            View view = m_inflater.inflate(R.layout.item_lobby_chat_header, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == SYSTEM) {
            View view = m_inflater.inflate(R.layout.item_lobby_chat_system, parent, false);
            return new SystemViewHolder(view);
        } else {
            View view = m_inflater.inflate(R.layout.item_lobby_chat, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (ItemViewHolder.class.isInstance(holder)) {
            ItemViewHolder item = ((ItemViewHolder) holder);
            ChatMessage message = getChatMessage(position);
            item.setCharMessage(message);
        } else if (SystemViewHolder.class.isInstance(holder)) {
            if (m_chatMessages.size() > 0) {
                ((SystemViewHolder) holder).setMessage(getChatMessage(position));
            }
        }
    }

    @Override
    public int getItemViewType(final int position) {
        if (m_chatMessages.size() == 0) {
            return position == HEADER ? HEADER : SYSTEM;
        } else {
            if (position == HEADER) {
                return HEADER;
            } else if (getChatMessage(position).isSystemMessage()) {
                return SYSTEM;
            } else {
                return ITEM;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (m_chatMessages.size() == 0) {
            return m_lobby.isAliveUser(User.current.getId()) ? 2 : 1;
        } else {
            return m_chatMessages.size() + 1;
        }
    }

    @Override
    public void onDetachedFromRecyclerView(final RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        m_chatRoomService.leave();
    }

    private ChatMessage getChatMessage(final int location) {
        return m_chatMessages.get(location - 1);
    }

    public void addMessages(List<ChatMessage> messages) {
        ChatMessage previous = null;
        if (m_chatMessages.size()>0)
        {
            previous = m_chatMessages.get(m_chatMessages.size()-1);
        }

        for (ChatMessage message : messages) {
            if (previous != null && !previous.isSystemMessage() && !message.isSystemMessage() && message.getUser().getId().equals(previous.getUser().getId())) {
                previous.setBody(previous.getBody() + "\r\n" + message.getBody());
            } else {
                previous = message;
                m_chatMessages.add(message);
            }
        }

        notifyDataSetChanged();
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

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.img_game)
        ImageView gameImageView;

        @InjectView(R.id.text_name)
        TextView lobbyNameText;

        @InjectView(R.id.text_lobby_type)
        TextView lobbyTypeText;

        @InjectView(R.id.text_description)
        TextView lobbyDescriptionText;

        public HeaderViewHolder(final View view) {
            super(view);

            ButterKnife.inject(this, view);

            //name
            lobbyNameText.setText(m_lobby.getOwner().getUserName() + "'s\n" + m_lobby.getName());

            //img
            if (StringUtils.isNotEmpty(m_lobby.getPictureUrl())) {
                Picasso.with(m_context).load(m_lobby.getPictureUrl()).centerCrop().fit().into(gameImageView);
            }

            //type
            lobbyTypeText.setText(String.format("%s,%s", m_lobby.getPlayStyle(), m_lobby.getSkillLevel()));

            //description
            lobbyDescriptionText.setText(m_lobby.getDescription());
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
}
