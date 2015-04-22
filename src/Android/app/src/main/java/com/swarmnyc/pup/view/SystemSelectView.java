package com.swarmnyc.pup.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.swarmnyc.pup.R;

/**
 * TODO: document your custom view class.
 */
public class SystemSelectView extends LinearLayout
{

	@InjectView( R.id.btn_pc )       Button m_pcButton;
	@InjectView( R.id.btn_steam )    Button m_steamButton;
	@InjectView( R.id.btn_xbox_360 ) Button m_xbox360Button;
	@InjectView( R.id.btn_xbox_one ) Button m_xboxOneButton;
	@InjectView( R.id.btn_ps3 )      Button m_ps3Button;
	@InjectView( R.id.btn_ps4 )      Button m_ps4Button;




	public SystemSelectView( Context context )
	{
		super( context );
		init( null, 0 );
	}

	public SystemSelectView( Context context, AttributeSet attrs )
	{
		super( context, attrs );
		init( attrs, 0 );
	}

	public SystemSelectView( Context context, AttributeSet attrs, int defStyle )
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


	private  class SystemOnClickListener implements OnClickListener
	{
		@Override public void onClick( final View v )
		{

			if (v.isSelected())
			{
				v.setSelected( false );
			}
			else
			{
				m_pcButton.setSelected( false );
				m_steamButton.setSelected( false );
				m_xbox360Button.setSelected( false );
				m_xboxOneButton.setSelected( false );
				m_ps3Button.setSelected( false );
				m_ps4Button.setSelected( false );

				v.setSelected( true );
			}

		}
	}
}
