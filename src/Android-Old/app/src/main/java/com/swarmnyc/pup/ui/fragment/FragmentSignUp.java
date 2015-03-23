package com.swarmnyc.pup.ui.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.swarmnyc.pup.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSignUp extends Fragment
{

	@InjectView( R.id.txt_email )     TextView m_email;
	@InjectView( R.id.txt_password )  TextView m_password;
	@InjectView( R.id.txt_gamer_tag ) TextView m_gamerTag;
	@InjectView( R.id.btn_pc )        Button   m_pcButton;
	@InjectView( R.id.btn_xbox_one )  Button   m_xboxOneButton;
	@InjectView( R.id.btn_xbox_360 )  Button   m_xBox360Button;
	@InjectView( R.id.btn_ps4 )       Button   m_ps4Button;
	@InjectView( R.id.btn_ps3 )       Button   m_ps3Button;
	@InjectView( R.id.btn_wii_u )     Button   m_wiiUButton;

	public FragmentSignUp()
	{
		// Required empty public constructor
	}


	@Override
	public View onCreateView(
		LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
	)
	{
		// Inflate the layout for this fragment
		final View view = inflater.inflate( R.layout.fragment_sign_up, container, false );
		ButterKnife.inject( this, view );

		return view;
	}

	@Override public void onViewCreated( final View view, final Bundle savedInstanceState )
	{
		super.onViewCreated( view, savedInstanceState );

		final View.OnClickListener systemButtonClickHandler = new View.OnClickListener()
		{
			@Override public void onClick( final View v )
			{
				v.setSelected( true );
			}
		};

		m_pcButton.setOnClickListener( systemButtonClickHandler );
		m_xboxOneButton.setOnClickListener( systemButtonClickHandler );
		m_xBox360Button.setOnClickListener( systemButtonClickHandler );
		m_ps4Button.setOnClickListener( systemButtonClickHandler );
		m_ps3Button.setOnClickListener( systemButtonClickHandler );
		m_wiiUButton.setOnClickListener( systemButtonClickHandler );


	}
}
