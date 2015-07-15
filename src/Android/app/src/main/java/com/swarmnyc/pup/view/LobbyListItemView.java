package com.swarmnyc.pup.view;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.squareup.picasso.Picasso;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.StringUtils;
import com.swarmnyc.pup.components.GamePlatformUtils;
import com.swarmnyc.pup.models.Lobby;

/**
 * TODO: document your custom view class.
 */
public class LobbyListItemView extends FrameLayout {

    @InjectView(R.id.img_game)
    ImageView m_gameImage;
    @InjectView(R.id.txt_game_name)
    TextView m_gameName;

    @InjectView(R.id.txt_game_time)
    TextView m_gameTime;
    @InjectView(R.id.txt_description)
    TextView m_description;
    @InjectView(R.id.txt_platform)
    TextView m_platform;
    @InjectView(R.id.txt_gamer_style)
    TextView m_gamerStyle;
    private Lobby m_lobby;

    public LobbyListItemView(Context context) {
        super(context);
        init(null, 0);
    }

    private void init(AttributeSet attrs, int defStyle) {

        final LayoutInflater infalter = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
        );
        final View view = infalter.inflate(R.layout.item_lobby, this, true);
        ButterKnife.inject(this, view);
    }

    public LobbyListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public LobbyListItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public Lobby getLobby() {
        return m_lobby;
    }

    public void setLobby(final Lobby lobby) {
        m_lobby = lobby;

        if (StringUtils.isNotEmpty(lobby.getThumbnailPictureUrl())) {
            Picasso.with(getContext()).load(lobby.getThumbnailPictureUrl()).centerCrop().fit().into(m_gameImage);
        }

        m_gameName.setText(lobby.getName());
        m_gameTime.setText(DateUtils.getRelativeTimeSpanString(getContext(), lobby.getStartTime().getTime()));
        m_description.setText(lobby.getDescription());
        m_platform.setText(GamePlatformUtils.labelResIdForPlatform(lobby.getPlatform()));
        m_gamerStyle.setText(lobby.getPlayStyle().name() + " | " + lobby.getSkillLevel().name());

        switch (lobby.getPlatform()) {
            case PC:
            case Steam:
                m_platform.setBackgroundResource(R.color.game_pc);
                break;
            case XboxOne:
                m_platform.setBackgroundResource(R.color.game_xbox_one);
                break;
            case Xbox360:
                m_platform.setBackgroundResource(R.color.game_xbox_360);
                break;
            case PS4:
                m_platform.setBackgroundResource(R.color.game_ps4);
                break;
            case PS3:
                m_platform.setBackgroundResource(R.color.game_ps3);
                break;
        }
    }
}
