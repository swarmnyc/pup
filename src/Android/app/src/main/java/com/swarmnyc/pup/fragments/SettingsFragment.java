package com.swarmnyc.pup.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;
import com.swarmnyc.pup.*;
import com.swarmnyc.pup.Services.UserService;
import com.swarmnyc.pup.activities.MainActivity;
import com.swarmnyc.pup.components.DialogHelper;
import com.swarmnyc.pup.components.FacebookHelper;
import com.swarmnyc.pup.components.Screen;

import javax.inject.Inject;
import java.io.File;

public class SettingsFragment extends Fragment implements Screen
{
	@Inject
	UserService m_userService;

	@InjectView( R.id.text_name )
	EditText m_nameText;

	@InjectView( R.id.img_portrait )
	ImageView m_portrait;

	@InjectView( R.id.switch_facebook )
	Switch m_fbSwitch;

	@Override
	public String toString()
	{
		return "Settings";
	}

	@Override
	public void onActivityResult( final int requestCode, final int resultCode, final Intent data )
	{
		if ( requestCode == Consts.CODE_PHOTO && resultCode == Activity.RESULT_OK )
		{
			Uri destination = Uri.fromFile( new File( getActivity().getCacheDir(), "cropped" ) );
			Crop.of( data.getData(), destination ).asSquare().withMaxSize( 1000, 1000 ).start(
				getActivity(), this
			);

		}
		else if ( requestCode == Crop.REQUEST_CROP && resultCode == Activity.RESULT_OK )
		{
			Uri uri = Crop.getOutput( data );

			if ( uri != null )
			{
				String path;

				m_portrait.setImageURI( uri );

				Cursor cursor = this.getActivity().getContentResolver().query(
					uri, null, null, null, null
				);

				if ( cursor == null )
				{
					path = uri.getPath();
				}
				else
				{
					cursor.moveToFirst();
					int idx = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
					path = cursor.getString( idx );
				}

				m_userService.updatePortrait( path, null );
			}
		}
		else
		{
			super.onActivityResult( requestCode, resultCode, data );
		}
	}

	@Override
	public View onCreateView(
		LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
	)
	{
		return inflater.inflate( R.layout.fragment_settings, container, false );
	}

	@Override
	public void onViewCreated( View view, Bundle savedInstanceState )
	{
		super.onViewCreated( view, savedInstanceState );
		ButterKnife.inject( this, view );
		PuPApplication.getInstance().getComponent().inject( this );
		MainActivity.getInstance().getToolbar().setTitle( R.string.label_settings );
		MainActivity.getInstance().getToolbar().setSubtitle( null );

		if ( StringUtils.isNotEmpty( User.current.getPortraitUrl() ) )
		{
			Picasso.with( this.getActivity() ).load( User.current.getPortraitUrl() ).into( m_portrait );
		}

		m_nameText.setText( User.current.getUserName() );

		m_fbSwitch.setChecked( User.current.hasMedium( Consts.KEY_FACEBOOK ) );
	}

	@Override
	public void onStart()
	{
		super.onStart();
		MainDrawerFragment.getInstance().highLight( Consts.KEY_SETTINGS );
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

	@OnClick( R.id.switch_facebook )
	void connectToFacebook()
	{
		if ( m_fbSwitch.isChecked() )
		{
			FacebookHelper.getAndSubmitToken( null );
		}
		else
		{
			User.removeMedium( Consts.KEY_FACEBOOK );
		}
	}
}
