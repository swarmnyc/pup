package com.swarmnyc.pup.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.swarmnyc.pup.*;
import com.swarmnyc.pup.module.service.LobbyService;
import com.swarmnyc.pup.module.service.ServiceCallback;
import com.swarmnyc.pup.module.viewmodels.UnreadCounter;
import com.swarmnyc.pup.ui.ViewAnimationUtils;
import com.swarmnyc.pup.ui.adapters.LobbyChatAdapter;
import com.swarmnyc.pup.module.chat.ChatMessage;
import com.swarmnyc.pup.module.chat.ChatRoomService;
import com.swarmnyc.pup.ui.events.ChatMessageReceiveEvent;
import com.swarmnyc.pup.ui.events.UserChangedEvent;
import com.swarmnyc.pup.ui.helpers.DialogHelper;
import com.swarmnyc.pup.ui.helpers.SoftKeyboardHelper;
import com.swarmnyc.pup.ui.listeners.HideKeyboardFocusChangedListener;
import com.swarmnyc.pup.module.models.Lobby;
import com.swarmnyc.pup.module.models.LobbyUserInfo;
import com.swarmnyc.pup.module.chat.QBChatMessage;
import com.swarmnyc.pup.ui.view.DividerItemDecoration;
import com.swarmnyc.pup.ui.view.ShareView;
import com.swarmnyc.pup.utils.Action;
import com.swarmnyc.pup.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class LobbyFragment extends BaseFragment {
    private static final String TAG = LobbyFragment.class.getSimpleName();

    LobbyService m_lobbyService;

    @Bind(R.id.backdrop)
    ImageView m_headerImage;
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout m_collapsingToolbarLayout;
    @Bind(R.id.toolbar)
    Toolbar m_toolbar;
    @Bind(R.id.appbar)
    AppBarLayout m_appbar;
    @Bind(R.id.layout_coordinator)
    CoordinatorLayout m_coordinatorLayout;


    @Bind(R.id.text_panel)
    ViewGroup m_textPanel;

    @Bind(R.id.btn_join)
    TextView m_joinButton;

    @Bind(R.id.edit_message)
    EditText m_messageText;

    @Bind(R.id.btn_send)
    View m_sendButton;

    @Bind(R.id.list_chat)
    RecyclerView m_chatList;

    @Bind(R.id.share_panel)
    ShareView m_sharePanel;

    ChatRoomService m_chatRoomService;
    private String m_lobbyImage;
    private Lobby m_lobby;
    private String m_lobbyName;
    private String m_lobbyId;
    private LobbyChatAdapter m_chatAdapter;
    private LinearLayoutManager m_chatLayoutManager;
    private boolean m_first = true;
    private long m_lastListScrollingTime;
    float m_touchDownX;
    float m_touchDownY;
    private boolean m_hasMoreMessage;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        final Bundle bundle = activity.getIntent().getExtras();
        loadFromBundle(bundle);
    }

    @Override
    public View onCreateView(
            final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_lobby, container, false);
    }

    @Override
    public void onViewCreated(
            final View view,
            @Nullable
            final Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        m_lobbyService = PuPApplication.getInstance().getModule().provideLobbyService();
        ButterKnife.bind(this, view);


        if (StringUtils.isNotEmpty(m_lobbyImage)) {
            Picasso.with(getActivity()).load(m_lobbyImage).centerCrop().fit().into(
                    m_headerImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            ViewAnimationUtils.enterReveal(m_headerImage);
                        }

                        @Override
                        public void onError() {
                        }
                    }
            );

        }

        if (StringUtils.isNotEmpty(m_lobbyName)) {
            m_collapsingToolbarLayout.setTitle(m_lobbyName);
        }

        getAppCompatActivity().setSupportActionBar(m_toolbar);

        // Scroll down when Keyboard up
        SoftKeyboardHelper.setSoftKeyboardCallback(
                m_coordinatorLayout, new Action<Boolean>() {
                    @Override
                    public void call(final Boolean up) {
                        if (System.currentTimeMillis() - m_lastListScrollingTime < 1000) {
                            return;
                        }

                        m_lastListScrollingTime = System.currentTimeMillis();

                        if (up) {
                            Log.d(TAG, "After Keyboard up");
                            m_chatList.scrollToPosition(m_chatAdapter.getItemCount() - 1);
                            collapseAppBar();
                        } else {
                            Log.d(TAG, "After Keyboard down");
                        }
                    }
                }
        );

        //scrolling to end when has focus
        m_messageText.setOnFocusChangeListener(new HideKeyboardFocusChangedListener(getActivity()));

        m_chatLayoutManager = new LinearLayoutManager(getActivity());
        m_chatAdapter = new LobbyChatAdapter(getActivity());
        m_chatAdapter.setReachBeginAction(
                new Action<Object>() {
                    @Override
                    public void call(final Object value) {
                        loadChatHistoryRequire();
                    }
                }
        );

        m_chatList.setAdapter(m_chatAdapter);
        m_chatList.setLayoutManager(m_chatLayoutManager);
        m_chatList.addItemDecoration(
                new DividerItemDecoration(
                        getActivity(), DividerItemDecoration.VERTICAL_LIST
                )
        );

        //Tap to Hide SoftKeyboard
        m_chatList.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            m_touchDownX = event.getX();
                            m_touchDownY = event.getY();
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            //Log.d( TAG, "TouchEvent:" + event );
                            if (Math.abs(m_touchDownX - event.getX()) < Consts.TOUCH_SLOP && Math.abs(
                                    m_touchDownY - event.getY()
                            ) < Consts.TOUCH_SLOP) {
                                //Log.d( TAG, "TouchTap" );
                                SoftKeyboardHelper.hideSoftKeyboard(getActivity());
                            }
                        }

                        return false;
                    }
                }
        );
    }

    @Override
    public void onResume() {
        super.onStart();
        EventBus.getBus().register(this);
    }

    @Override
    public void onPause() {
        super.onStop();
        EventBus.getBus().unregister(this);
    }

    public void onDestroyView() {
        super.onDestroyView();

        SoftKeyboardHelper.removeSoftKeyboardCallback(m_coordinatorLayout);
        ButterKnife.unbind(this);
    }

    public void loadFromBundle(final Bundle args) {
        m_lobbyId = args.getString(Consts.KEY_LOBBY_ID);
        m_lobbyName = args.getString(Consts.KEY_LOBBY_NAME);
        m_lobbyImage = args.getString(Consts.KEY_LOBBY_IMAGE);
    }

    public void setLobby(final Lobby value) {
        m_lobby = value;

        /*//title
        setTitle(m_lobby.getName());

        //subtitle
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

        String subtitle = String.format("%s: %s", GamePlatformUtils.labelForPlatform(getActivity(), m_lobby.getPlatform()), time);

        setSubtitle(subtitle);*/

        initChatRoom();

        UnreadCounter.reset(m_lobby.getId());
    }

    private void initChatRoom() {
        m_first = true;
        m_chatAdapter.clear();
        m_chatAdapter.setReachBeginAction(null);
        m_chatAdapter.setLobby(m_lobby);
        m_chatAdapter.isLoading(true);
        m_chatAdapter.notifyDataSetChanged();
        m_chatLayoutManager.setStackFromEnd(false);
        expandAppBar();

        if (m_lobby.isAliveUser(User.current.getId())) {
            // Get Data
            m_chatRoomService = new ChatRoomService(getActivity(), m_lobby);
            m_chatRoomService.loadChatHistory(0);
        } else {
            // Get DATA by Rest API
            m_lobbyService.getMessages(
                    m_lobbyId, new ServiceCallback<List<QBChatMessage>>() {
                        @Override
                        public void success(final List<QBChatMessage> result) {
                            List<ChatMessage> list = new ArrayList<ChatMessage>();

                            for (QBChatMessage message : result) {
                                list.add(
                                        new ChatMessage(
                                                message.getUserId() == null ? null : new LobbyUserInfo(message.getUserId()),
                                                message.get_id(),
                                                message.getMessage(),
                                                message.getDate_sent()
                                        )
                                );
                            }

                            receiveMessage(new ChatMessageReceiveEvent(m_lobby.getId(), false, list));
                        }
                    }
            );
        }
    }

    private void switchButton() {
        //Show or Hide joinLobby button
        if (User.isLoggedIn()) {
            if (m_lobby.isAliveUser(User.current.getId())) {
                m_joinButton.setVisibility(View.GONE);
                m_textPanel.setVisibility(View.VISIBLE);
            } else {
                m_joinButton.setVisibility(View.VISIBLE);
                m_joinButton.setText(R.string.text_join);
                m_textPanel.setVisibility(View.GONE);
            }
        } else {
            m_joinButton.setVisibility(View.VISIBLE);
            m_joinButton.setText(R.string.text_register_and_join);
            m_textPanel.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btn_send)
    void send() {
        String message = m_messageText.getText().toString().trim();
        if (message.length() > 0) {
            m_chatRoomService.SendMessage(message);
        }

        m_messageText.setText("");
    }

    @OnClick(R.id.btn_join)
    void joinLobby() {
        if (User.isLoggedIn()) {
            DialogHelper.showProgressDialog(getActivity(), R.string.message_processing);
            if (m_lobby.isAliveUser(User.current.getId())) {
                initChatRoom();
                DialogHelper.hide();
            } else {
                m_lobbyService.join(
                        m_lobby.getId(), new ServiceCallback<String>() {
                            @Override
                            public void success(final String value) {
                                m_joinButton.setVisibility(View.GONE); // GONE First
                                // Because user just joins a lobby, he can't get new message before he joins.
                                m_lobbyService.addUser(m_lobby, User.current);
                                initChatRoom();
                                DialogHelper.hide();
                            }
                        }
                );
            }
        } else {
            RegisterDialogFragment registerDialogFragment = new RegisterDialogFragment();
            registerDialogFragment.show(this.getFragmentManager(), null);
        }
    }

    @Subscribe
    public void handleUserChanged(UserChangedEvent event) {
        // After Register or login
        if (User.isLoggedIn()) {
            joinLobby();
        }
    }

    @Subscribe
    public void receiveMessage(final ChatMessageReceiveEvent event) {
        if (event.getLobbyId().equals(m_lobby.getId()) && !this.isDetached()) {
            event.handled = true;

            //After receive history
            if (m_first) {
                m_chatAdapter.isLoading(false);
                switchButton();
            }

            List<ChatMessage> messages = event.getMessages();

            boolean scrollingToEnd = m_first || (m_chatAdapter.getItemCount()
                    - m_chatLayoutManager.findLastVisibleItemPosition() < 3
            );
            m_lastListScrollingTime = System.currentTimeMillis();

            if (event.isNewMessage()) {
                m_chatAdapter.addMessages(messages);
            } else {
                Collections.reverse(messages);
                m_hasMoreMessage = messages.size() == Consts.PAGE_SIZE;
                m_chatAdapter.addMessages(0, messages);
            }

            final int size = m_chatAdapter.getItemCount();

            // SharePanel
            if (event.isNewMessage() || m_first) {
                if (m_chatAdapter.getMessageCount() == 0 && m_lobby.isAliveUser(User.current.getId())) {
                    //no message, show share item
                    m_sharePanel.setVisibility(View.VISIBLE);
                    m_sharePanel.setLobbyService(m_lobbyService);
                    m_sharePanel.setLobby(m_lobby);
                    m_sharePanel.setActivity(this.getActivity());
                } else {
                    m_sharePanel.setVisibility(View.GONE);
                }
            }

            // Scrolling
            if (scrollingToEnd) {
                Log.d(TAG, "Scrolling After Receive Message");

                m_chatList.scrollToPosition(size - 1);
                if (m_first) {
                    initChatListAndAppBar();
                }
            }

            if (!m_first && !m_chatLayoutManager.getStackFromEnd()) {
                m_chatList.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int height = computeHeight();
                        if (height < 0) {
                            Log.d(TAG, "Change to StackFromEnd");
                            m_chatLayoutManager.setStackFromEnd(true);
                        }
                    }
                }, 100);
            }

            m_first = false;
        }
    }

    private void initChatListAndAppBar() {
        m_chatList.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        int height = computeHeight();
                        //Log.d(TAG, "Height:" + height);
                        if (height < 0) {
                            collapseAppBar();
                            m_chatLayoutManager.setStackFromEnd(true);
                        }
                    }
                }, 100
        );
    }

    private int computeHeight() {
        int height = m_chatList.getHeight();
        height -= m_collapsingToolbarLayout.getHeight();
        Log.d(TAG, "Height:" + height);

        for (int i = 0; i < m_chatList.getChildCount(); i++) {
            height -= m_chatList.getChildAt(i).getHeight();
            Log.d(TAG, "Height:" + height);
        }
        return height;
    }

    private void collapseAppBar() {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) m_appbar.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();

        if (behavior != null) {
            // I didn't find only scroll a little.
            behavior.onNestedFling(
                    m_coordinatorLayout, m_appbar, null, 0, m_appbar.getHeight(), true
            );
        }
    }

    private void expandAppBar() {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) m_appbar.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();

        if (behavior != null) {
            // I didn't find only scroll a little.
            behavior.setTopAndBottomOffset(0);
            behavior.onNestedPreScroll(m_coordinatorLayout, m_appbar, null, 0, 1, new int[2]);
        }
    }


    private void loadChatHistoryRequire() {
        if (m_hasMoreMessage) {
            Log.d(TAG, "Load another History");
            if (m_chatAdapter.getMessageCount() != 0) {
                m_chatRoomService.loadChatHistory(m_chatAdapter.getFirstChatMessage().getSentAt());
            }
        }
    }

    @Override
    public String getScreenName() {
        return "Lobby: " + m_lobbyName;
    }
}