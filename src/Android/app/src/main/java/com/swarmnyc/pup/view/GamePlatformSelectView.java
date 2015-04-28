package com.swarmnyc.pup.view;

import android.content.Context;
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
		setOrientation( VERTICAL );

		final LayoutInflater infalter = (LayoutInflater) getContext().getSystemService(
			Context.LAYOUT_INFLATER_SERVICE
		);
		final View view = infalter.inflate( R.layout.view_system_select, this, true );
		ButterKnife.inject( this, view );

		final SystemOnClickListener systemOnClickListener = new SystemOnClickListener();
		m_pcButton.setOnClickListener( systemOnClickListener );
		m_steamButton.setOnClickListener( systemOnClickListener );
		m_xbox360Button.setOnClickListener( systemOnClickListener );
		m_xboxOneButton.setOnClickListener( systemOnClickListener );
		m_ps3Button.setOnClickListener( systemOnClickListener );
		m_ps4Button.setOnClickListener( systemOnClickListener );


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

	private  class SystemOnClickListener implements OnClickListener
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
}
