package com.swarmnyc.pup.fragments;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.Services.UserService;
import com.swarmnyc.pup.StringUtils;
import com.swarmnyc.pup.User;
import com.swarmnyc.pup.activities.MainActivity;
import com.swarmnyc.pup.viewmodels.UserInfo;

import javax.inject.Inject;

public class SignupFragment extends DialogFragment
{
	private boolean goHome = true;

	@Inject
	UserService m_userService;

	@InjectView( R.id.text_email )
	EditText emailText;

	@InjectView( R.id.text_name )
	EditText nameText;

	Button joinButton;
	View   view;

	@Override
	public View onCreateView(
		LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
	)
	{
		if ( view == null )
		{
			MainActivity.getInstance().getToolbar().setTitle( R.string.text_signup );
			MainActivity.getInstance().getToolbar().setSubtitle( null );
			view = inflater.inflate( R.layout.fragment_sign_up, container, false );
			PuPApplication.getInstance().getComponent().inject( this );
			ButterKnife.inject( this, view );

			joinButton = (Button) view.findViewById( R.id.btn_join );
			joinButton.setOnClickListener(
				new View.OnClickListener()
				{
					@Override
					public void onClick( final View v )
					{
						if ( vaild() ){
							userRegister();
						}
					}
				}
			);

			return view;
		}
		else
		{
			return null;
		}
	}

	public void setGoHomeAfterLogin( boolean goHome )
	{
		this.goHome = goHome;
	}

	@Override
	public Dialog onCreateDialog( final Bundle savedInstanceState )
	{
		LayoutInflater inflater = getActivity().getLayoutInflater();
		view = inflater.inflate( R.layout.dialog_sign_up, null );
		PuPApplication.getInstance().getComponent().inject( this );
		ButterKnife.inject( this, view );

		AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
		builder.setCustomTitle( inflater.inflate( R.layout.title_join_lobby, null ) );
		builder.setView( view );
		final AlertDialog dialog = builder.create();

		view.findViewById( R.id.btn_cancel ).setOnClickListener(
			new View.OnClickListener()
			{
				@Override
				public void onClick( final View v )
				{
					dialog.cancel();
				}
			}
		);

		view.findViewById( R.id.btn_join ).setOnClickListener(
			new View.OnClickListener()
			{
				@Override
				public void onClick( final View v )
				{
					if ( vaild() ){
						userRegister();
						dialog.cancel();
					}
				}
			}
		);

		return dialog;
	}

	private void userRegister()
	{
		m_userService.register(
			emailText.getText().toString(), nameText.getText().toString(), new ServiceCallback<UserInfo>()
			{
				@Override
				public void success( final UserInfo value )
				{
					User.login( value, goHome );
				}
			}
		);
	}

	private boolean vaild()
	{
		boolean result = StringUtils.isNotEmpty( this.emailText.getText().toString() )
		                 || StringUtils.isNotEmpty( this.nameText.getText().toString() );

		if ( joinButton != null )
		{
			joinButton.setEnabled( result );
		}

		//TODO: Email Check;

		return result;
	}
}
