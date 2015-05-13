package com.swarmnyc.pup.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.models.GamePlatform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * TODO: document your custom view class.
 */
public class GamePlatformSelectView extends LinearLayout
{

	@InjectView( R.id.btn_pc )       Button m_pcButton;
	@InjectView( R.id.btn_steam )    Button m_steamButton;
	@InjectView( R.id.btn_xbox_360 ) Button m_xbox360Button;
	@InjectView( R.id.btn_xbox_one ) Button m_xboxOneButton;
	@InjectView( R.id.btn_ps3 )      Button m_ps3Button;
	@InjectView( R.id.btn_ps4 )      Button m_ps4Button;

	private boolean m_allowMultiSelect = false;

	public interface OnPlatformSelectionChangedListener
	{
		void onPlatformSelectionChanged(final View v);
	}

	private OnPlatformSelectionChangedListener m_platformSelectionChangedListener;

	public GamePlatformSelectView( Context context )
	{
		super( context );
		init( null, 0 );
	}

	public GamePlatformSelectView( Context context, AttributeSet attrs )
	{
		super( context, attrs );
		init( attrs, 0 );
	}

	public GamePlatformSelectView( Context context, AttributeSet attrs, int defStyle )
	{
		super( context, attrs, defStyle );
		init( attrs, defStyle );
	}

	private void init( AttributeSet attrs, int defStyle )
	{
		TypedArray a = getContext().obtainStyledAttributes( attrs, R.styleable.GamePlatformSelectView );

		try
		{
			m_allowMultiSelect =  a.getBoolean( R.styleable.GamePlatformSelectView_allowMultiSelect, false );
			String s  =  a.getString( R.styleable.GamePlatformSelectView_allowMultiSelect );
		}
		finally
		{
			a.recycle();
		}

		setOrientation( VERTICAL );

		final LayoutInflater infalter = (LayoutInflater) getContext().getSystemService(
			Context.LAYOUT_INFLATER_SERVICE
		);
		final View view = infalter.inflate( R.layout.view_system_select, this, true );
		ButterKnife.inject( this, view );

		final OnClickListener onClickListener = m_allowMultiSelect ?  new MultiSelectOnClickListener() : new SingleSelectOnClickListener();

		m_pcButton.setOnClickListener( onClickListener );
		m_steamButton.setOnClickListener( onClickListener );
		m_xbox360Button.setOnClickListener( onClickListener );
		m_xboxOneButton.setOnClickListener( onClickListener );
		m_ps3Button.setOnClickListener( onClickListener );
		m_ps4Button.setOnClickListener( onClickListener );
	}

	public List<GamePlatform> getSelectedGamePlatforms()
	{
		final ArrayList<GamePlatform> gamePlatforms = new ArrayList<>();

		if (m_pcButton.isSelected())
		{
			gamePlatforms.add( GamePlatform.PC );
		}
		if (m_steamButton.isSelected())
		{
			gamePlatforms.add( GamePlatform.Steam );
		}
		if (m_xbox360Button.isSelected())
		{
			gamePlatforms.add( GamePlatform.Xbox360 );
		}
		if (m_xboxOneButton.isSelected())
		{
			gamePlatforms.add( GamePlatform.XboxOne );
		}
		if (m_ps3Button.isSelected())
		{
			gamePlatforms.add( GamePlatform.PS3 );
		}
		if (m_ps4Button.isSelected())
		{
			gamePlatforms.add( GamePlatform.PS4 );
		}

		return gamePlatforms;
	}

	public void setSelectedGamePlatforms(final Collection<GamePlatform> platforms)
	{
		for ( GamePlatform platform : platforms )
		{
			switch ( platform )
			{
				case PC:
					m_pcButton.setSelected( true );
					break;
				case Steam:
					m_steamButton.setSelected( true );
					break;
				case Xbox360:
					m_xbox360Button.setSelected( true );
					break;
				case XboxOne:
					m_xboxOneButton.setSelected( true );
					break;
				case PS3:
					m_ps3Button.setSelected( true );
					break;
				case PS4:
					m_ps4Button.setSelected( true );
					break;
			}
		}
	}

	public void setAvailablePlatforms( final List<GamePlatform> platforms )
	{
		resetEnablement();
		resetSelection();
		for ( GamePlatform platform : platforms )
		{
			switch ( platform )
			{
				case PC:
					m_pcButton.setEnabled( true );
					break;
				case Steam:
					m_steamButton.setEnabled( true );
					break;
				case Xbox360:
					m_xbox360Button.setEnabled( true );
					break;
				case XboxOne:
					m_xboxOneButton.setEnabled( true );
					break;
				case PS3:
					m_ps3Button.setEnabled( true );
					break;
				case PS4:
					m_ps4Button.setEnabled( true );
					break;
			}
		}
	}

	@Override
	protected void onRestoreInstanceState( final Parcelable state )
	{
		Log.d( "GamePlatformSelectView", String.format( "onRestoreInstanceState (%s)", state ) );
		final Bundle bundle = (Bundle) state;
		final ArrayList<Integer> selection = bundle.getIntegerArrayList( "selection" );

		List<GamePlatform> platforms = new ArrayList<>(  );

		for ( Integer integer : selection )
		{
			platforms.add( GamePlatform.values()[integer] );
		}

		setSelectedGamePlatforms( platforms );

		super.onRestoreInstanceState( bundle.getParcelable( "super" ) );
	}

	@Override
	protected Parcelable onSaveInstanceState()
	{
		final Parcelable parcelable = super.onSaveInstanceState();
		Bundle bundle = new Bundle(  );
		bundle.putParcelable( "super", parcelable );
		final ArrayList<Integer> selectedGamePlatforms = new ArrayList<>(  );
		for ( GamePlatform gamePlatform : getSelectedGamePlatforms() )
		{
			selectedGamePlatforms.add( gamePlatform.ordinal() );
		}

		bundle.putIntegerArrayList( "selection", selectedGamePlatforms );
		return bundle;
	}

	public OnPlatformSelectionChangedListener getPlatformSelectionChangedListener()
	{
		return m_platformSelectionChangedListener;
	}

	public void setPlatformSelectionChangedListener(
		final OnPlatformSelectionChangedListener platformSelectionChangedListener
	)
	{
		m_platformSelectionChangedListener = platformSelectionChangedListener;
	}

	private  class MultiSelectOnClickListener implements OnClickListener
	{
		@Override public void onClick( final View v )
		{

			v.setSelected( !v.isSelected() );

			if (null != m_platformSelectionChangedListener )
			{
				m_platformSelectionChangedListener.onPlatformSelectionChanged(v);
			}

		}
	}

	private  class SingleSelectOnClickListener implements OnClickListener
	{
		@Override public void onClick( final View v )
		{

			if (v.isSelected())
			{
				v.setSelected( false );
			}
			else
			{
				resetSelection();

				v.setSelected( true );
			}

			if (null != m_platformSelectionChangedListener )
			{
				m_platformSelectionChangedListener.onPlatformSelectionChanged(v);
			}

		}
	}

	private void resetEnablement()
	{
		m_pcButton.setEnabled( false );
		m_steamButton.setEnabled( false );
		m_xbox360Button.setEnabled( false );
		m_xboxOneButton.setEnabled( false );
		m_ps3Button.setEnabled( false );
		m_ps4Button.setEnabled( false );
	}

	private void resetSelection()
	{
		m_pcButton.setSelected( false );
		m_steamButton.setSelected( false );
		m_xbox360Button.setSelected( false );
		m_xboxOneButton.setSelected( false );
		m_ps3Button.setSelected( false );
		m_ps4Button.setSelected( false );
	}
}
