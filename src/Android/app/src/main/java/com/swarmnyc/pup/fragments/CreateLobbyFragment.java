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
import com.squareup.picasso.Picasso;
import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.Services.Filter.GameFilter;
import com.swarmnyc.pup.Services.GameService;
import com.swarmnyc.pup.Services.LobbyService;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.StringUtils;
import com.swarmnyc.pup.activities.MainActivity;
import com.swarmnyc.pup.adapters.AutoCompleteForPicturedModelAdapter;
import com.swarmnyc.pup.components.Action;
import com.swarmnyc.pup.components.DialogHelper;
import com.swarmnyc.pup.components.Navigator;
import com.swarmnyc.pup.models.*;
import com.swarmnyc.pup.view.GamePlatformSelectView;
import com.swarmnyc.pup.view.HorizontalSpinner;

import javax.inject.Inject;
import java.util.*;

public class CreateLobbyFragment extends Fragment
	implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener
{
	@Inject
	GameService gameService;

	@Inject
	LobbyService lobbyService;

	@InjectView( R.id.img_game )
	ImageView gameImageView;

	@InjectView( R.id.text_name )
	AutoCompleteTextView gameNameTextEdit;

	@InjectView( R.id.platform_select )
	GamePlatformSelectView platformSelect;

	@InjectView( R.id.spinner_play_style )
	HorizontalSpinner playStyleSpinner;

	@InjectView( R.id.spinner_gamer_skill )
	HorizontalSpinner gamerSkillSpinner;

	@InjectView( R.id.text_date )
	TextView dateText;

	@InjectView( R.id.text_time )
	TextView timeText;

	@InjectView( R.id.text_description )
	EditText descriptionText;

	@InjectView( R.id.btn_submit )
	Button submitButton;

	GameFilter gameFilter = new GameFilter();
	Game                                      selectedGame;
	Calendar                                  selectedDate;
	int                                       dateOffset;
	boolean                                   customDateTime;
	AutoCompleteForPicturedModelAdapter<Game> gameAdapter;
	int                                       timeOffset;

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

		gameAdapter = new AutoCompleteForPicturedModelAdapter<Game>( this.getActivity() );

		gameAdapter.setSearchAction(
			new Action<CharSequence>()
			{
				@Override
				public void call( CharSequence constraint )
				{
					gameFilter.setSearch( constraint.toString() );
					gameService.getGames(
						gameFilter, new ServiceCallback<List<Game>>()
						{
							@Override
							public void success( List<Game> value )
							{
								gameAdapter.finishSearch( value );
							}
						}
					);
				}
			}
		);

		gameNameTextEdit.setAdapter( gameAdapter );

		gameNameTextEdit.setOnItemClickListener(
			new AdapterView.OnItemClickListener()
			{
				@Override
				public void onItemClick( AdapterView<?> parent, View view, int position, long id )
				{
					selectedGame = gameAdapter.getItem( position );
					if ( StringUtils.isNotEmpty( selectedGame.getThumbnailPictureUrl() ) )
					{
						Picasso.with( getActivity() )
						       .load( selectedGame.getThumbnailPictureUrl() )
						       .centerCrop()
						       .fit()
						       .into( gameImageView );
					}

					platformSelect.setAvailablePlatforms( selectedGame.getPlatforms() );
					valid();
				}
			}
		);

		platformSelect.setPlatformSelectionChangedListener(
			new GamePlatformSelectView.OnPlatformSelectionChangedListener()
			{
				@Override
				public void onPlatformSelectionChanged( final View v )
				{
					valid();
				}
			}
		);

		gameNameTextEdit.requestFocus();

		playStyleSpinner.setSource( this.getResources().getStringArray( R.array.play_styles ) );
		playStyleSpinner.setSelectedPosition( 1 );

		gamerSkillSpinner.setSource( this.getResources().getStringArray( R.array.gamer_skills ) );
		gamerSkillSpinner.setSelectedPosition( 1 );

		dateText.setOnClickListener(
			new View.OnClickListener()
			{
				@Override
				public void onClick( final View v )
				{
					DatePickerDialog datePickerDialog = new DatePickerDialog(
						getActivity(), CreateLobbyFragment.this, selectedDate.get( Calendar.YEAR ), selectedDate.get(
						Calendar.MONTH
					), selectedDate.get( Calendar.DAY_OF_MONTH )
					);

					Calendar range = Calendar.getInstance();
					removeTime( range );

					datePickerDialog.getDatePicker().setMaxDate(
						range.getTimeInMillis() + 7 * DateUtils.DAY_IN_MILLIS
					);
					datePickerDialog.getDatePicker().setMinDate( range.getTimeInMillis() );
					datePickerDialog.show();
				}
			}
		);

		timeText.setOnClickListener(
			new View.OnClickListener()
			{
				@Override
				public void onClick( final View v )
				{
					TimePickerDialog timePickerDialog = new TimePickerDialog(
						getActivity(),
						CreateLobbyFragment.this,
						selectedDate.get( Calendar.HOUR_OF_DAY ),
						selectedDate.get( Calendar.MINUTE ),
						false
					);

					timePickerDialog.show();
				}
			}
		);

		setDate( 0 );

		selectedDate = Calendar.getInstance();
		setTime( selectedDate.get( Calendar.HOUR_OF_DAY ), selectedDate.get( Calendar.MINUTE ) + 20 );
	}

	@Override
	public void onStart()
	{
		super.onStart();
		MainActivity.getInstance().hideToolbar();
	}

	@Override
	public void onStop()
	{
		super.onStop();
		MainActivity.getInstance().showToolbar();
	}

	@Override
	public void onDateSet( final DatePicker view, final int year, final int monthOfYear, final int dayOfMonth )
	{
		customDateTime = true;
		selectedDate.set( year, monthOfYear, dayOfMonth );
		Calendar date = Calendar.getInstance();

		removeTime( date );

		Calendar date2 = new GregorianCalendar( year, monthOfYear, dayOfMonth );
		long dateOffset = ( date2.getTimeInMillis() - date.getTimeInMillis() ) / DateUtils.DAY_IN_MILLIS;

		setDate( (int) dateOffset );
		setTime( selectedDate.get( Calendar.HOUR_OF_DAY ), selectedDate.get( Calendar.MINUTE ) );
	}

	@Override
	public void onTimeSet( final TimePicker view, final int hourOfDay, final int minute )
	{
		customDateTime = true;
		setTime( hourOfDay, minute );
	}

	@OnClick( R.id.btn_submit )
	void onCreateLobby()
	{
		if ( !valid() )
		{ return; }

		DialogHelper.showProgressDialog( R.string.message_lobby_creating );

		List<GamePlatform> platforms = platformSelect.getSelectedGamePlatforms();

		Lobby lobby = new Lobby();
		lobby.setGameId( selectedGame.getId() );
		lobby.setPlatform( platforms.get( 0 ) );
		lobby.setSkillLevel( SkillLevel.get( gamerSkillSpinner.getSelectedPosition() ) );
		lobby.setPlayStyle( PlayStyle.get( playStyleSpinner.getSelectedPosition() ) );
		lobby.setDescription( descriptionText.getText().toString() );
		lobby.setStartTime( getStartTime() );
		lobbyService.create(
			lobby, new ServiceCallback<Lobby>()
			{
				@Override
				public void success( final Lobby value )
				{
					DialogHelper.hide();
					Navigator.ToLobby( value.getId(), true );
				}
			}
		);
	}

	private boolean valid()
	{
		boolean isValid = true;
		if ( selectedGame == null )
		{
			isValid = false;
		}

		if ( platformSelect.getSelectedGamePlatforms().size() == 0 )
		{
			isValid = false;
		}

		this.submitButton.setEnabled( isValid );

		return isValid;
	}

	private Date getStartTime()
	{
		if ( customDateTime )
		{
			return selectedDate.getTime();
		}
		else
		{
			Calendar date = Calendar.getInstance();
			date.add( Calendar.MINUTE, timeOffset );

			return date.getTime();
		}
	}

	private void setDate( final int dateOffset )
	{
		this.dateOffset = dateOffset;
		String s;
		switch ( dateOffset )
		{
			case 0:
				s = this.getActivity().getString( R.string.today );
				break;
			case 1:
				s = this.getActivity().getString( R.string.tomorrow);
				break;
			default:
				s = String.format(
					"%s %d", selectedDate.getDisplayName( Calendar.MONTH, Calendar.SHORT, Locale.US ), selectedDate
						.get(
							Calendar.DAY_OF_MONTH
						)
				);
				break;
		}

		dateText.setText( s );
	}

	private void setTime( int hour, int min )
	{
		String s;
		selectedDate.set( Calendar.HOUR_OF_DAY, hour );
		selectedDate.set( Calendar.MINUTE, min );
		timeOffset = (int) ( ( selectedDate.getTimeInMillis() - Calendar.getInstance().getTimeInMillis() )
		                     / DateUtils.MINUTE_IN_MILLIS
		);
		timeOffset = Math.max( timeOffset, 20 );
		if ( dateOffset == 0 && timeOffset <= 60 )
		{
			s = this.getActivity().getString( R.string.time_in_minutes, timeOffset );
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

		timeText.setText( s );
	}

	private void removeTime( final Calendar date )
	{
		date.set( Calendar.HOUR_OF_DAY, 0 );
		date.set( Calendar.MINUTE, 0 );
		date.set( Calendar.SECOND, 0 );
		date.set( Calendar.MILLISECOND, 0 );
	}
}
