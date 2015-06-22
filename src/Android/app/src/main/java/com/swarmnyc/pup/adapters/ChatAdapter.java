package com.swarmnyc.pup.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.squareup.picasso.Picasso;
import com.swarmnyc.pup.*;
import com.swarmnyc.pup.Services.LobbyService;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.activities.MainActivity;
import com.swarmnyc.pup.chat.ChatMessage;
import com.swarmnyc.pup.chat.ChatMessageListener;
import com.swarmnyc.pup.chat.ChatRoomService;
import com.swarmnyc.pup.components.FacebookHelper;
import com.swarmnyc.pup.components.Utility;
import com.swarmnyc.pup.events.LobbyUserChanged;
import com.swarmnyc.pup.fragments.OAuthFragment;
import com.swarmnyc.pup.fragments.RedditShareFragment;
import com.swarmnyc.pup.models.Lobby;
import com.swarmnyc.pup.models.LobbyUserInfo;
import com.swarmnyc.pup.models.UserInfo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ChatMessageListener {
    private static final int HEADER = 0;
    private static final int SHARE = -1;
    private static final int SYSTEM = -2;
    private static final int ITEM = 1;
    ChatRoomService m_chatRoomService;
    List<ChatMessage> m_chatMessages;
    LobbyService m_lobbyService;
    private Context m_context;
    private Lobby m_lobby;
    private LayoutInflater m_inflater;
    private RecyclerView m_recyclerView;
    private boolean m_isloaded;

    public ChatAdapter(
            final Context context, final LobbyService lobbyService, final Lobby lobby
    ) {
        m_context = context;
        m_lobby = lobby;
        m_inflater = (LayoutInflater) m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        m_lobbyService = lobbyService;
        m_chatMessages = new LinkedList<>();
    }

    public void setChatRoomService(final ChatRoomService chatRoomService, boolean loadHistory) {
        m_chatRoomService = chatRoomService;
        m_chatRoomService.setMessageListener(this);
        m_chatRoomService.login(loadHistory);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        if (viewType == HEADER) {
            View view = m_inflater.inflate(R.layout.item_lobby_chat_header, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == SHARE) {
            View view = m_inflater.inflate(R.layout.item_lobby_chat_share, parent, false);
            return new ShareViewHolder(view);
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
            ((ItemViewHolder) holder).setCharMessage(getChatMessage(position));
        } else if (SystemViewHolder.class.isInstance(holder)) {
            if (m_chatMessages.size() > 0) {
                ((SystemViewHolder) holder).setMessage(getChatMessage(position));
            }
        }
    }

    @Override
    public int getItemViewType(final int position) {
        if (m_chatMessages.size() == 0) {
            if (m_isloaded) {
                return position == HEADER ? HEADER : SHARE;
            } else {
                return position == HEADER ? HEADER : SYSTEM;
            }
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
            return m_lobby.isDwellingUser(User.current.getId()) ? 2 : 1;
        } else {
            return m_chatMessages.size() + 1;
        }
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        m_recyclerView = recyclerView;
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(final RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        m_chatRoomService.leave();
    }

    private ChatMessage getChatMessage(final int location) {
        return m_chatMessages.get(location - 1);
    }

    @Override
    public void receive(final List<ChatMessage> messages) {
        m_isloaded = true;

        if (messages.size() == 1) {
            ChatMessage cm = messages.get(0);
            if (cm.isNewMessage() && cm.isSystemMessage() && cm.getCode() != null) {
                boolean isCurrentUser = false;
                //Join and Left System messages.
                if (cm.getCode().equals("Join") && cm.getCodeBody() != null) {
                    UserInfo[] users = Utility.fromJson(cm.getCodeBody(), UserInfo[].class);
                    for (UserInfo u : users) {
                        isCurrentUser = u.getId().equals(User.current.getId());
                        LobbyUserInfo user = m_lobby.getUser(u.getId());
                        if (user == null) {
                            user = new LobbyUserInfo();
                            user.setId(u.getId());
                            user.setUserName(u.getUserName());
                            user.setPortraitUrl(u.getPortraitUrl());
                            m_lobby.getUsers().add(user);
                        } else {
                            //rejoin
                            user.setIsLeave(false);
                        }
                    }

                    EventBus.getBus().post(new LobbyUserChanged(isCurrentUser));
                } else if (cm.getCode().equals("Leave") && cm.getCodeBody() != null) {
                    UserInfo[] users = Utility.fromJson(cm.getCodeBody(), UserInfo[].class);
                    for (UserInfo u : users) {
                        LobbyUserInfo user = m_lobby.getUser(u.getId());
                        if (user != null) {
                            //rejoin
                            user.setIsLeave(true);
                        }
                    }

                    EventBus.getBus().post(new LobbyUserChanged(false));
                }

            }
        }
        m_chatMessages.addAll(messages);
        notifyDataSetChanged();

        //TODO: Better Scrolling
        m_recyclerView.scrollToPosition(m_chatMessages.size());
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

    class ShareViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.btn_facebook)
        ImageView m_facebookButton;

        @InjectView(R.id.btn_twitter)
        ImageView m_twitterButton;

        @InjectView(R.id.btn_tumblr)
        ImageView m_tumblrButton;

        @InjectView(R.id.btn_reddit)
        ImageView m_redditButton;

        public ShareViewHolder(final View view) {
            super(view);
            ButterKnife.inject(this, view);

            setButtonState(User.current.hasMedium(Consts.KEY_FACEBOOK), m_facebookButton);
            setButtonState(User.current.hasMedium(Consts.KEY_TWITTER), m_twitterButton);
            setButtonState(User.current.hasMedium(Consts.KEY_TUMBLR), m_tumblrButton);
            setButtonState(User.current.hasMedium(Consts.KEY_REDDIT), m_redditButton);
        }

        private void setButtonState(boolean share, ImageView button) {
            button.setActivated(share);
            button.setImageResource(m_context.getResources().getIdentifier("ico_" + button.getTag() + (share ? "_select" : ""), "drawable", m_context.getPackageName()));
        }

        @OnClick(R.id.btn_facebook)
        void tapOnFacebook() {
            if (m_facebookButton.isActivated()) {
                setButtonState(false, m_facebookButton);
            } else {
                if (User.current.hasMedium(Consts.KEY_FACEBOOK)) {
                    setButtonState(true, m_facebookButton);
                } else {
                    FacebookHelper.startLoginRequire(
                            new AsyncCallback() {
                                @Override
                                public void success() {
                                    setButtonState(true, m_facebookButton);
                                }
                            }
                    );
                }
            }
        }

        @OnClick(R.id.btn_twitter)
        void tapOnTwitter() {
            if (m_twitterButton.isActivated()) {
                setButtonState(false, m_twitterButton);
            } else {
                if (User.current.hasMedium(Consts.KEY_TWITTER)) {
                    setButtonState(true, m_twitterButton);
                } else {
                    OAuthFragment oAuthFragment = new OAuthFragment();
                    oAuthFragment.initialize(
                            Consts.KEY_TWITTER, new AsyncCallback() {
                                @Override
                                public void success() {
                                    User.addMedium(Consts.KEY_TWITTER);
                                    setButtonState(true, m_twitterButton);
                                }
                            }
                    );

                    oAuthFragment.show(MainActivity.getInstance().getSupportFragmentManager(), null);
                }
            }
        }

        @OnClick(R.id.btn_tumblr)
        void tapOnTumblr() {
            if (m_tumblrButton.isActivated()) {
                setButtonState(false, m_tumblrButton);
            } else {
                if (User.current.hasMedium(Consts.KEY_TUMBLR)) {
                    setButtonState(true, m_tumblrButton);
                } else {
                    OAuthFragment oAuthFragment = new OAuthFragment();
                    oAuthFragment.initialize(
                            Consts.KEY_TUMBLR, new AsyncCallback() {
                                @Override
                                public void success() {
                                    User.addMedium(Consts.KEY_TUMBLR);
                                    setButtonState(true, m_tumblrButton);
                                }
                            }
                    );

                    oAuthFragment.show(MainActivity.getInstance().getSupportFragmentManager(), null);
                }
            }
        }

        @OnClick(R.id.btn_reddit)
        void tapOnReddit() {
            if (m_redditButton.isActivated()) {
                setButtonState(false, m_redditButton);
            } else {
                if (User.current.hasMedium(Consts.KEY_REDDIT)) {
                    setButtonState(true, m_redditButton);
                } else {
                    OAuthFragment oAuthFragment = new OAuthFragment();
                    oAuthFragment.initialize(
                            Consts.KEY_REDDIT, new AsyncCallback() {
                                @Override
                                public void success() {
                                    User.addMedium(Consts.KEY_REDDIT);
                                    setButtonState(true, m_redditButton);
                                }
                            }
                    );

                    oAuthFragment.show(MainActivity.getInstance().getSupportFragmentManager(), null);
                }
            }
        }

        @OnClick(R.id.btn_invite)
        void invite() {
            List<String> types = new ArrayList<>();
            if (m_facebookButton.isActivated()) {
                types.add(Consts.KEY_FACEBOOK);
            }

            if (m_twitterButton.isActivated()) {
                types.add(Consts.KEY_TWITTER);
            }

            if (m_twitterButton.isActivated()) {
                types.add(Consts.KEY_TUMBLR);
            }

            if (types.size() == 0) {
                if (m_redditButton.isActivated()) {
                    ShareToReddit();
                } else {
                    Toast.makeText(m_context, "You need to choose at least one social medium", Toast.LENGTH_SHORT).show();
                }

            } else {
                m_lobbyService.invite(
                        m_lobby, types, new ServiceCallback() {
                            @Override
                            public void success(final Object value) {
                                if (m_redditButton.isActivated()) {
                                    ShareToReddit();
                                } else {
                                    Toast.makeText(m_context, "Share Succeeded", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
            }
        }

        private void ShareToReddit() {
            RedditShareFragment fragment = new RedditShareFragment();
            fragment.initialize(m_lobby, new AsyncCallback() {
                @Override
                public void success() {
                    Toast.makeText(m_context, "Share Succeeded", Toast.LENGTH_SHORT).show();
                }
            });

            fragment.show(MainActivity.getInstance().getSupportFragmentManager(), "Reddit");
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
