package com.swarmnyc.pup.fragments;


import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.TempFragmentDialog;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.swarmnyc.pup.*;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.Services.UserService;
import com.swarmnyc.pup.helpers.DialogHelper;
import com.swarmnyc.pup.helpers.PhotoHelper;
import com.swarmnyc.pup.models.CurrentUserInfo;

import javax.inject.Inject;

public class RegisterDialogFragment extends TempFragmentDialog
{
	@Inject
	UserService m_userService;
	@InjectView( R.id.text_email )
	EditText    m_emailText;
	@InjectView( R.id.text_name )
	EditText    m_nameText;
	@InjectView( R.id.img_portrait )
	ImageView   m_portrait;
	Uri m_portraitUri;
	private boolean goHome = true;
	private AlertDialog m_dialog;

	public void setGoHomeAfterLogin( boolean goHome )
	{
		this.goHome = goHome;
	}

	@Override
	public Dialog onCreateDialog( final Bundle savedInstanceState )
	{
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate( R.layout.dialog_register, null );
		PuPApplication.getInstance().getComponent().inject( this );
		ButterKnife.inject( this, view );

		AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
		builder.setCustomTitle( inflater.inflate( R.layout.title_join_lobby, null ) );
		builder.setView( view );
		m_dialog = builder.create();

		return m_dialog;
	}

	@OnClick( R.id.btn_cancel )
	void cancel()
	{
		this.dismiss();
	}

	@OnClick( R.id.btn_join )
	void join()
	{
		if ( vaild() )
		{
			userRegister();
		}
	}

	private boolean vaild()
	{
		boolean result = StringUtils.isNotEmpty( this.m_emailText.getText().toString() )
		                 && StringUtils.isNotEmpty( this.m_nameText.getText().toString() )
		                 && android.util.Patterns.EMAIL_ADDRESS.matcher( this.m_emailText.getText() ).matches();

		return result;
	}

	private void userRegister()
	{
		String path = null;
		if ( m_portraitUri != null )
		{
			Uri selectedImageUri = m_portraitUri;
			Cursor cursor = this.getActivity().getContentResolver().query( selectedImageUri, null, null, null, null );
			if ( cursor == null )
			{
				path = selectedImageUri.getPath();
			}
			else
			{
				cursor.moveToFirst();
				int idx = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
				path = cursor.getString( idx );
			}
		}

		DialogHelper.showProgressDialog(getActivity(), R.string.message_processing );
		m_userService.register(
			m_emailText.getText().toString(),
			m_nameText.getText().toString(),
			path,
			new ServiceCallback<CurrentUserInfo>()
			{
				@Override
				public void success( final CurrentUserInfo value )
				{
					m_dialog.dismiss();
					DialogHelper.hide();
					User.login( value, goHome );
				}
			}
		);
	}

	@OnClick( {R.id.img_camera, R.id.img_portrait} )
	void choosePortrait()
	{
		PhotoHelper.startPhotoIntent(
			this, new AsyncCallback<Uri>()
			{
				@Override
				public void success( final Uri uri )
				{
					m_portraitUri = uri;
					m_portrait.setImageURI( m_portraitUri );
				}
			}
		);
	}

	@Override
	public void onActivityResult( final int requestCode, final int resultCode, final Intent data )
	{
		super.onActivityResult( requestCode, resultCode, data );

		PhotoHelper.catchPhotoIntent( requestCode, resultCode, data );
	}

	@Override
	public void onDestroy()
	{
		PhotoHelper.close();
		super.onDestroy();
	}
}
