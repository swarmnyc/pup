package com.swarmnyc.pup.fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.Services.UserService;
import com.swarmnyc.pup.User;
import com.swarmnyc.pup.activities.MainActivity;
import com.swarmnyc.pup.viewmodels.UserInfo;

import javax.inject.Inject;

public class SignupFragment extends DialogFragment
{
	@Inject
	UserService m_userService;

	@InjectView( R.id.text_email )
	EditText emailText;

	@InjectView( R.id.text_name )
	EditText nameText;

	@Override
	public View onCreateView(
		LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
	)
	{
		MainActivity.getInstance().getToolbar().setTitle( R.string.text_signup );
		MainActivity.getInstance().getToolbar().setSubtitle( null);
		View view = inflater.inflate( R.layout.fragment_sign_up, container, false );
		PuPApplication.getInstance().getComponent().inject( this );
		ButterKnife.inject( this, view );

		return view;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog( final Bundle savedInstanceState )
	{
		View view = getLayoutInflater( savedInstanceState ).inflate( R.layout.fragment_sign_up, null );
		PuPApplication.getInstance().getComponent().inject( this );
		ButterKnife.inject( this, view );

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView( view );

		return builder.create();
	}

	@OnClick( R.id.btn_join )
	public void userRegister()
	{
		m_userService.register(
			emailText.getText().toString(), nameText.getText().toString(), new ServiceCallback<UserInfo>()
			{
				@Override
				public void success( final UserInfo value )
				{
					User.login( value );
				}
			}
		);
	}
}
