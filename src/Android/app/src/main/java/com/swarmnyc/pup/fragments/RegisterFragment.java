package com.swarmnyc.pup.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.soundcloud.android.crop.Crop;
import com.swarmnyc.pup.*;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.Services.UserService;
import com.swarmnyc.pup.components.DialogHelper;
import com.swarmnyc.pup.viewmodels.UserInfo;

import javax.inject.Inject;
import java.io.File;

public class RegisterFragment extends DialogFragment
{
	private boolean goHome = true;

	@Inject
	UserService m_userService;

	@InjectView( R.id.text_email )
	EditText emailText;

	@InjectView( R.id.text_name )
	EditText nameText;

	@InjectView( R.id.img_portrait )
	ImageView portrait;

	Uri portaritUri;

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

	private void userRegister()
	{
		String path = null;
		if ( portaritUri != null )
		{
			Uri selectedImageUri = portaritUri;
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

		m_userService.register(
			emailText.getText().toString(), nameText.getText().toString(), path, new ServiceCallback<UserInfo>()
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
		                 && StringUtils.isNotEmpty( this.nameText.getText().toString() )
		                 && android.util.Patterns.EMAIL_ADDRESS.matcher( this.emailText.getText() ).matches();

		return result;
	}

	@OnClick( R.id.btn_join )
	void join()
	{
		if ( vaild() )
		{
			userRegister();
			m_dialog.cancel();
		}
	}

	@OnClick( {R.id.img_camera, R.id.img_portrait} )
	void choosePortrait()
	{
		DialogHelper.showOptions(
			new String[]{"Take a new photo", "Choose from gallery"}, new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick( final DialogInterface dialog, final int which )
				{
					if ( which == 0 )
					{
						Intent takePicture = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
						startActivityForResult( takePicture, Consts.CODE_PHOTO );
					}
					else
					{
						Intent pickPhoto = new Intent(
							Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
						);
						startActivityForResult( pickPhoto, Consts.CODE_PHOTO );
					}
				}
			}
		);
	}

	@Override
	public void onActivityResult( final int requestCode, final int resultCode, final Intent data )
	{
		if ( requestCode == Consts.CODE_PHOTO )
		{
			if ( resultCode == Activity.RESULT_OK )
			{
				Uri destination = Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));
				Crop.of(data.getData(), destination ).asSquare().start( getActivity(), this );
			}
		}
		else if(requestCode == Crop.REQUEST_CROP){
			if ( resultCode == Activity.RESULT_OK )
			{
				portaritUri = Crop.getOutput( data );
				portrait.setImageURI( portaritUri );
			}
		}else

		{
			super.onActivityResult( requestCode, resultCode, data );
		}
	}
}
