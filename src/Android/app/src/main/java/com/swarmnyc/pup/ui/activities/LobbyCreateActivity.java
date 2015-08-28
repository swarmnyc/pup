package com.swarmnyc.pup.ui.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.swarmnyc.pup.Config;
import com.swarmnyc.pup.Consts;
import com.swarmnyc.pup.EventBus;
import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.module.service.Filter.GameFilter;
import com.swarmnyc.pup.module.service.GameService;
import com.swarmnyc.pup.module.service.LobbyService;
import com.swarmnyc.pup.module.service.ServiceCallback;
import com.swarmnyc.pup.ui.events.UnhandledChatMessageReceiveEvent;
import com.swarmnyc.pup.ui.helpers.ComingMessageHelper;
import com.swarmnyc.pup.utils.StringUtils;
import com.swarmnyc.pup.User;
import com.swarmnyc.pup.ui.adapters.AutoCompleteForPicturedModelAdapter;
import com.swarmnyc.pup.utils.Action;
import com.swarmnyc.pup.ui.Navigator;
import com.swarmnyc.pup.utils.Utility;
import com.swarmnyc.pup.ui.events.UserChangedEvent;
import com.swarmnyc.pup.ui.fragments.RegisterDialogFragment;
import com.swarmnyc.pup.ui.helpers.DialogHelper;
import com.swarmnyc.pup.ui.helpers.SoftKeyboardHelper;
import com.swarmnyc.pup.ui.listeners.HideKeyboardFocusChangedListener;
import com.swarmnyc.pup.module.models.Game;
import com.swarmnyc.pup.module.models.GamePlatform;
import com.swarmnyc.pup.module.models.Lobby;
import com.swarmnyc.pup.module.models.PlayStyle;
import com.swarmnyc.pup.module.models.SkillLevel;
import com.swarmnyc.pup.ui.view.GamePlatformSelectView;
import com.swarmnyc.pup.ui.view.HorizontalSpinner;
import com.uservoice.uservoicesdk.UserVoice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LobbyCreateActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    GameService m_gameService;

    LobbyService m_lobbyService;

    @Bind(R.id.layout_lobby_create)
    ScrollView m_rootView;

    @Bind(R.id.img_game)
    ImageView m_gameImageView;

    @Bind(R.id.text_name)
    AutoCompleteTextView m_gameNameTextEdit;

    @Bind(R.id.platform_select)
    GamePlatformSelectView m_gamePlatformSelectView;

    @Bind(R.id.spinner_play_style)
    HorizontalSpinner m_playStyleSpinner;

    @Bind(R.id.spinner_gamer_skill)
    HorizontalSpinner m_gamerSkillSpinner;

    @Bind(R.id.text_date)
    TextView m_dateText;

    @Bind(R.id.text_time)
    TextView m_timeText;

    @Bind(R.id.text_description)
    EditText m_descriptionText;

    @Bind(R.id.btn_submit)
    Button m_submitButton;

    GameFilter m_gameFilter = new GameFilter();
    Game m_selectedGame;
    Calendar m_selectedDate;
    int m_dateOffset;
    boolean m_customDateTime;
    AutoCompleteForPicturedModelAdapter<Game> m_gameAdapter;
    int m_timeOffset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_create);
        PuPApplication.getInstance().sendScreenToTracker("Create Lobby");

        m_gameService = PuPApplication.getInstance().getModule().provideGameService();
        m_lobbyService = PuPApplication.getInstance().getModule().provideLobbyService();

        ButterKnife.bind(this);

        m_gameAdapter = new AutoCompleteForPicturedModelAdapter<>(this);

        m_gameAdapter.setSearchAction(
                new Action<CharSequence>() {
                    @Override
                    public void call(CharSequence constraint) {
                        m_gameFilter.setSearch(constraint.toString());
                        m_gameService.getGames(
                                m_gameFilter, new ServiceCallback<List<Game>>() {
                                    @Override
                                    public void success(List<Game> value) {
                                        if (value.size() == 0) {
                                            Game game = new Game();
                                            game.setThumbnailPictureUrl(
                                                    Utility.getResourceUri(
                                                            LobbyCreateActivity.this,
                                                            R.drawable.ico_plus
                                                    ).toString()
                                            );
                                            game.setName(getString(R.string.text_request_game));
                                            value.add(game);
                                        }

                                        m_gameAdapter.finishSearch(value);
                                    }
                                }
                        );
                    }
                }
        );

        m_gameNameTextEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (m_gameAdapter.getCount() > 0) {
                        selectGame(0);
                    }

                    return true;
                }
                return false;
            }
        });

        m_gameNameTextEdit.setAdapter(m_gameAdapter);
        m_gameNameTextEdit.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectGame(position);
                    }
                }
        );

        m_gameNameTextEdit.setOnFocusChangeListener(new HideKeyboardFocusChangedListener(LobbyCreateActivity.this));
        m_descriptionText.setOnFocusChangeListener(new HideKeyboardFocusChangedListener(LobbyCreateActivity.this));

        m_gamePlatformSelectView.setPlatformSelectionChangedListener(
                new GamePlatformSelectView.OnPlatformSelectionChangedListener() {
                    @Override
                    public void onPlatformSelectionChanged(final View v) {
                        valid(false);
                    }
                }
        );

        m_gameNameTextEdit.requestFocus();

        m_playStyleSpinner.setSource(this.getResources().getStringArray(R.array.play_styles));
        m_playStyleSpinner.setSelectedPosition(1);

        m_gamerSkillSpinner.setSource(this.getResources().getStringArray(R.array.gamer_skills));
        m_gamerSkillSpinner.setSelectedPosition(1);

        m_dateText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                LobbyCreateActivity.this,
                                LobbyCreateActivity.this,
                                m_selectedDate.get(Calendar.YEAR),
                                m_selectedDate.get(
                                        Calendar.MONTH
                                ),
                                m_selectedDate.get(Calendar.DAY_OF_MONTH)
                        );

                        Calendar range = Calendar.getInstance();
                        removeTime(range);

                        datePickerDialog.getDatePicker().setMaxDate(
                                range.getTimeInMillis() + 14 * DateUtils.DAY_IN_MILLIS - 1
                        );
                        datePickerDialog.getDatePicker().setMinDate(range.getTimeInMillis());
                        datePickerDialog.show();
                    }
                }
        );

        m_timeText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                LobbyCreateActivity.this,
                                LobbyCreateActivity.this,
                                m_selectedDate.get(Calendar.HOUR_OF_DAY),
                                m_selectedDate.get(Calendar.MINUTE),
                                false
                        );

                        timePickerDialog.show();
                    }
                }
        );

        SoftKeyboardHelper.setSoftKeyboardCallback(m_rootView, new Action<Boolean>() {
            @Override
            public void call(Boolean value) {
                if (value && m_descriptionText.hasFocus()) {

                    m_rootView.fullScroll(View.FOCUS_DOWN);
                }
            }
        });

        setDate(0);

        m_selectedDate = Calendar.getInstance();
        setTime(m_selectedDate.get(Calendar.HOUR_OF_DAY), m_selectedDate.get(Calendar.MINUTE) + 20);

        valid(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getBus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getBus().unregister(this);
    }

    @Subscribe
    public void runtimeError(final Exception exception) {
        this.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {

                        DialogHelper.showError(LobbyCreateActivity.this, exception);
                    }
                }
        );
    }

    @Subscribe
    public void receiveMessage(final UnhandledChatMessageReceiveEvent event) {
        m_lobbyService.getLobby(event.getLobbyId(), new ServiceCallback<Lobby>() {
            @Override
            public void success(Lobby lobby) {
                ComingMessageHelper.show(LobbyCreateActivity.this, lobby, event.getMessages().get(event.getMessages().size() - 1));
            }
        });
    }

    @Override
    public void onDateSet(final DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
        m_customDateTime = true;
        m_selectedDate.set(year, monthOfYear, dayOfMonth);
        Calendar date = Calendar.getInstance();

        removeTime(date);

        Calendar date2 = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        long dateOffset = (date2.getTimeInMillis() - date.getTimeInMillis()) / DateUtils.DAY_IN_MILLIS;

        setDate((int) dateOffset);
        setTime(m_selectedDate.get(Calendar.HOUR_OF_DAY), m_selectedDate.get(Calendar.MINUTE));
    }

    @Override
    public void onTimeSet(final TimePicker view, final int hourOfDay, final int minute) {
        m_customDateTime = true;
        setTime(hourOfDay, minute);
    }

    @Subscribe
    public void postUserChanged(UserChangedEvent event) {
        if (User.isLoggedIn()) {
            createLobby();
        }
    }

    @OnClick(R.id.btn_submit)
    public void createLobby() {
        if (!valid(true)) {
            return;
        }

        if (!User.isLoggedIn()) {
            RegisterDialogFragment registerDialogFragment = new RegisterDialogFragment();
            registerDialogFragment.show(this.getSupportFragmentManager(), null);
            return;
        }

        DialogHelper.showProgressDialog(this, R.string.message_lobby_creating);

        List<GamePlatform> platforms = m_gamePlatformSelectView.getSelectedGamePlatforms();

        Lobby lobby = new Lobby();
        lobby.setGameId(m_selectedGame.getId());
        lobby.setPlatform(platforms.get(0));
        lobby.setSkillLevel(SkillLevel.get(m_gamerSkillSpinner.getSelectedPosition()));
        lobby.setPlayStyle(PlayStyle.get(m_playStyleSpinner.getSelectedPosition()));
        lobby.setDescription(m_descriptionText.getText().toString());
        lobby.setStartTime(getStartTime());
        m_lobbyService.create(
                lobby, new ServiceCallback<Lobby>() {
                    @Override
                    public void success(final Lobby value) {
                        DialogHelper.hide();
                        Config.setBool(Consts.KEY_NEED_UPDATE_LIST, true);
                        Config.setBool(Consts.KEY_NEED_UPDATE_MY, true);
                        Navigator.ToLobby(LobbyCreateActivity.this, value);

                        LobbyCreateActivity.this.finish(); // Finish this activity.
                    }
                }
        );
    }

    private void selectGame(int position) {
        SoftKeyboardHelper.hideSoftKeyboard(LobbyCreateActivity.this);

        m_selectedGame = m_gameAdapter.getItem(position);
        if (m_selectedGame.getId() == null) {
            m_gameNameTextEdit.setText("");
            UserVoice.launchPostIdea(LobbyCreateActivity.this);
        } else {
            m_gameNameTextEdit.setText(m_selectedGame.getName());
            m_gameNameTextEdit.dismissDropDown();
            if (StringUtils.isNotEmpty(m_selectedGame.getPictureUrl())) {
                Picasso.with(LobbyCreateActivity.this)
                        .load(m_selectedGame.getPictureUrl())
                        .centerCrop()
                        .fit()
                        .into(
                                m_gameImageView
                        );
            }

            m_gamePlatformSelectView.setAvailablePlatforms(m_selectedGame.getPlatforms());
            valid(false);
        }
    }

    private Date getStartTime() {
        if (m_customDateTime) {
            return m_selectedDate.getTime();
        } else {
            Calendar date = Calendar.getInstance();
            date.add(Calendar.MINUTE, m_timeOffset);

            return date.getTime();
        }
    }

    private boolean valid(boolean showMessage) {
        boolean isValid = true;
        if (m_selectedGame == null) {
            isValid = false;
            if (showMessage)
                Toast.makeText(this, "Please select a game", Toast.LENGTH_SHORT).show();
        } else if (m_gamePlatformSelectView.getSelectedGamePlatforms().size() == 0) {
            isValid = false;
            if (showMessage)
                Toast.makeText(this, "Please select a platform of the game", Toast.LENGTH_SHORT).show();
        }

        this.m_submitButton.setActivated(!isValid);

        return isValid;
    }

    private void removeTime(final Calendar date) {
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
    }

    private void setDate(final int dateOffset) {
        this.m_dateOffset = dateOffset;
        String s;
        switch (dateOffset) {
            case 0:
                s = this.getString(R.string.text_today);
                break;
            case 1:
                s = this.getString(R.string.text_tomorrow);
                break;
            default:
                SimpleDateFormat format = new SimpleDateFormat("MMM dd", Locale.getDefault());
                s = format.format(m_selectedDate.getTime());
                break;
        }

        m_dateText.setText(s);
    }

    private void setTime(int hour, int min) {
        Calendar c = (Calendar) m_selectedDate.clone();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, min);

        m_timeOffset = (int) ((c.getTimeInMillis() - Calendar.getInstance().getTimeInMillis())
                / DateUtils.MINUTE_IN_MILLIS
        );

        if (m_timeOffset < 0) {
            Toast.makeText(this, R.string.message_create_lobby_wrong_chose_time, Toast.LENGTH_LONG)
                    .show();
            return;
        }

        m_selectedDate.set(Calendar.HOUR_OF_DAY, hour);
        m_selectedDate.set(Calendar.MINUTE, min);

        String s;

        m_timeOffset = Math.max(m_timeOffset, 20);
        if (m_dateOffset == 0 && m_timeOffset <= 60) {
            s = this.getString(R.string.time_in_minutes, m_timeOffset);
        } else {
            SimpleDateFormat format = new SimpleDateFormat("h:mm a", Locale.getDefault());
            s = format.format(m_selectedDate.getTime());
        }

        m_timeText.setText(s);
    }
}
