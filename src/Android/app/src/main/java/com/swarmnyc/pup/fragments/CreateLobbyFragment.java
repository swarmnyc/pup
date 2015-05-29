package com.swarmnyc.pup.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.swarmnyc.pup.*;
import com.swarmnyc.pup.Services.Filter.GameFilter;
import com.swarmnyc.pup.Services.GameService;
import com.swarmnyc.pup.Services.LobbyService;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.activities.MainActivity;
import com.swarmnyc.pup.adapters.AutoCompleteForPicturedModelAdapter;
import com.swarmnyc.pup.components.Action;
import com.swarmnyc.pup.components.DialogHelper;
import com.swarmnyc.pup.components.Navigator;
import com.swarmnyc.pup.events.UserChangedEvent;
import com.swarmnyc.pup.models.*;
import com.swarmnyc.pup.view.GamePlatformSelectView;
import com.swarmnyc.pup.view.HorizontalSpinner;

import javax.inject.Inject;
import java.util.*;

public class CreateLobbyFragment extends Fragment
	implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener
{
	@Inject
	GameService m_gameService;

	@Inject
	LobbyService m_lobbyService;

	@InjectView( R.id.img_game )
	ImageView m_gameImageView;

	@InjectView( R.id.text_name )
	AutoCompleteTextView m_gameNameTextEdit;

	@InjectView( R.id.platform_select )
	GamePlatformSelectView m_gamePlatformSelectView;

	@InjectView( R.id.spinner_play_style )
	HorizontalSpinner m_playStyleSpinner;

	@InjectView( R.id.spinner_gamer_skill )
	HorizontalSpinner m_gamerSkillSpinner;

	@InjectView( R.id.text_date )
	TextView m_dateText;

	@InjectView( R.id.text_time )
	TextView m_timeText;

	@InjectView( R.id.text_description )
	EditText m_descriptionText;

	@InjectView( R.id.btn_submit )
	Button m_submitButton;

	GameFilter m_gameFilter = new GameFilter();
	Game                                      m_selectedGame;
	Calendar                                  m_selectedDate;
	int                                       m_dateOffset;
	boolean                                   m_customDateTime;
	AutoCompleteForPicturedModelAdapter<Game> m_gameAdapter;
	int                                       m_timeOffset;

	@Override
	public View onCreateView(
		LayoutInflater inflater,
		@Nullable
		ViewGroup container,
		@Nullable
		Bundle savedInstanceState
	)
	{
		return inflater.inflate( R.layout.fragment_lobby_create, container, false );
	}

	@Override
	public void onViewCreated(
		View view,
		@Nullable
		Bundle savedInstanceState
	)
	{
		super.onViewCreated( view, savedInstanceState );

		PuPApplication.getInstance().getComponent().inject( this );
		ButterKnife.inject( this, view );
		EventBus.getBus().register( this );

		m_gameAdapter = new AutoCompleteForPicturedModelAdapter<Game>( this.getActivity() );

		m_gameAdapter.setSearchAction(
			new Action<CharSequence>()
			{
				@Override
				public void call( CharSequence constraint )
				{
					m_gameFilter.setSearch( constraint.toString() );
					m_gameService.getGames(
						m_gameFilter, new ServiceCallback<List<Game>>()
						{
							@Override
							public void success( List<Game> value )
							{
								m_gameAdapter.finishSearch( value );
							}
						}
					);
				}
			}
		);

		m_gameNameTextEdit.setAdapter( m_gameAdapter );

		m_gameNameTextEdit.setOnItemClickListener(
			new AdapterView.OnItemClickListener()
			{
				@Override
				public void onItemClick( AdapterView<?> parent, View view, int position, long id )
				{
					m_selectedGame = m_gameAdapter.getItem( position );
					if ( StringUtils.isNotEmpty( m_selectedGame.getPictureUrl() ) )
					{
						Picasso.with( getActivity() ).load( m_selectedGame.getPictureUrl() ).centerCrop().fit().into(
							m_gameImageView
						);
					}

					m_gamePlatformSelectView.setAvailablePlatforms( m_selectedGame.getPlatforms() );
					valid();
				}
			}
		);

		m_gamePlatformSelectView.setPlatformSelectionChangedListener(
			new GamePlatformSelectView.OnPlatformSelectionChangedListener()
			{
				@Override
				public void onPlatformSelectionChanged( final View v )
				{
					valid();
				}
			}
		);

		m_gameNameTextEdit.requestFocus();

		m_playStyleSpinner.setSource( this.getResources().getStringArray( R.array.play_styles ) );
		m_playStyleSpinner.setSelectedPosition( 1 );

		m_gamerSkillSpinner.setSource( this.getResources().getStringArray( R.array.gamer_skills ) );
		m_gamerSkillSpinner.setSelectedPosition( 1 );

		m_dateText.setOnClickListener(
			new View.OnClickListener()
			{
				@Override
				public void onClick( final View v )
				{
					DatePickerDialog datePickerDialog = new DatePickerDialog(
						getActivity(), CreateLobbyFragment.this, m_selectedDate.get( Calendar.YEAR ), m_selectedDate.get(
						Calendar.MONTH
					), m_selectedDate.get( Calendar.DAY_OF_MONTH )
					);

					Calendar range = Calendar.getInstance();
					removeTime( range );

					datePickerDialog.getDatePicker().setMaxDate(
						range.getTimeInMillis() + 8 * DateUtils.DAY_IN_MILLIS - 1
					);
					datePickerDialog.getDatePicker().setMinDate( range.getTimeInMillis() );
					datePickerDialog.show();
				}
			}
		);

		m_timeText.setOnClickListener(
			new View.OnClickListener()
			{
				@Override
				public void onClick( final View v )
				{
					TimePickerDialog timePickerDialog = new TimePickerDialog(
						getActivity(),
						CreateLobbyFragment.this,
						m_selectedDate.get( Calendar.HOUR_OF_DAY ),
						m_selectedDate.get( Calendar.MINUTE ),
						false
					);

					timePickerDialog.show();
				}
			}
		);

		setDate( 0 );

		m_selectedDate = Calendar.getInstance();
		setTime( m_selectedDate.get( Calendar.HOUR_OF_DAY ), m_selectedDate.get( Calendar.MINUTE ) + 20 );
	}

	@Override
	public void onStart()
	{
		super.onStart();
		MainActivity.getInstance().hideToolbar();
		MainDrawerFragment.getInstance().highLight( Consts.KEY_LOBBIES );
	}

	@Override
	public void onStop()
	{
		super.onStop();
		MainActivity.getInstance().showToolbar();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		EventBus.getBus().unregister( this );
	}

	private boolean valid()
	{
		boolean isValid = true;
		if ( m_selectedGame == null )
		{
			isValid = false;
		}

		if ( m_gamePlatformSelectView.getSelectedGamePlatforms().size() == 0 )
		{
			isValid = false;
		}

		this.m_submitButton.setEnabled( isValid );

		return isValid;
	}

	private void removeTime( final Calendar date )
	{
		date.set( Calendar.HOUR_OF_DAY, 0 );
		date.set( Calendar.MINUTE, 0 );
		date.set( Calendar.SECOND, 0 );
		date.set( Calendar.MILLISECOND, 0 );
	}

	private void setDate( final int dateOffset )
	{
		this.m_dateOffset = dateOffset;
		String s;
		switch ( dateOffset )
		{
			case 0:
				s = this.getActivity().getString( R.string.text_today );
				break;
			case 1:
				s = this.getActivity().getString( R.string.text_tomorrow );
				break;
			default:
				s = String.format(
					"%s %d", m_selectedDate.getDisplayName( Calendar.MONTH, Calendar.SHORT, Locale.US ), m_selectedDate
						.get(
							Calendar.DAY_OF_MONTH
						)
				);
				break;
		}

		m_dateText.setText( s );
	}

	private void setTime( int hour, int min )
	{
		String s;
		m_selectedDate.set( Calendar.HOUR_OF_DAY, hour );
		m_selectedDate.set( Calendar.MINUTE, min );
		m_timeOffset = (int) ( ( m_selectedDate.getTimeInMillis() - Calendar.getInstance().getTimeInMillis() )
		                     / DateUtils.MINUTE_IN_MILLIS
		);
		m_timeOffset = Math.max( m_timeOffset, 20 );
		if ( m_dateOffset == 0 && m_timeOffset <= 60 )
		{
			s = this.getActivity().getString( R.string.time_in_minutes, m_timeOffset );
		}
		else
		{
			s = String.format(
				"%d:%d %s",
				hour,
				min,
				hour >= 12
				? this.getActivity().getString( R.string.time_pm )
				: this.getActivity().getString( R.string.time_am )
			);
		}

		m_timeText.setText( s );
	}

	@Override
	public void onDateSet( final DatePicker view, final int year, final int monthOfYear, final int dayOfMonth )
	{
		m_customDateTime = true;
		m_selectedDate.set( year, monthOfYear, dayOfMonth );
		Calendar date = Calendar.getInstance();

		removeTime( date );

		Calendar date2 = new GregorianCalendar( year, monthOfYear, dayOfMonth );
		long dateOffset = ( date2.getTimeInMillis() - date.getTimeInMillis() ) / DateUtils.DAY_IN_MILLIS;

		setDate( (int) dateOffset );
		setTime( m_selectedDate.get( Calendar.HOUR_OF_DAY ), m_selectedDate.get( Calendar.MINUTE ) );
	}

	@Override
	public void onTimeSet( final TimePicker view, final int hourOfDay, final int minute )
	{
		m_customDateTime = true;
		setTime( hourOfDay, minute );
	}

	@Subscribe
	public void postUserChanged( UserChangedEvent event )
	{
		if ( User.isLoggedIn() )
		{
			createLobby();
		}
	}

	@OnClick( R.id.btn_submit )
	void createLobby()
	{
		if ( !valid() )
		{ return; }


		if ( !User.isLoggedIn() )
		{
			RegisterDialogFragment registerDialogFragment = new RegisterDialogFragment();
			registerDialogFragment.setGoHomeAfterLogin( false );
			registerDialogFragment.show( this.getFragmentManager(), null );
			return;
		}


		MainActivity.getInstance().hideIme();

		DialogHelper.showProgressDialog( R.string.message_lobby_creating );

		List<GamePlatform> platforms = m_gamePlatformSelectView.getSelectedGamePlatforms();

		Lobby lobby = new Lobby();
		lobby.setGameId( m_selectedGame.getId() );
		lobby.setPlatform( platforms.get( 0 ) );
		lobby.setSkillLevel( SkillLevel.get( m_gamerSkillSpinner.getSelectedPosition() ) );
		lobby.setPlayStyle( PlayStyle.get( m_playStyleSpinner.getSelectedPosition() ) );
		lobby.setDescription( m_descriptionText.getText().toString() );
		lobby.setStartTime( getStartTime() );
		m_lobbyService.create(
			lobby, new ServiceCallback<Lobby>()
			{
				@Override
				public void success( final Lobby value )
				{
					DialogHelper.hide();
					Navigator.ToLobby( value.getId(), Consts.KEY_LOBBIES, true );
				}
			}
		);
	}

	private Date getStartTime()
	{
		if ( m_customDateTime )
		{
			return m_selectedDate.getTime();
		}
		else
		{
			Calendar date = Calendar.getInstance();
			date.add( Calendar.MINUTE, m_timeOffset );

			return date.getTime();
		}
	}
}
