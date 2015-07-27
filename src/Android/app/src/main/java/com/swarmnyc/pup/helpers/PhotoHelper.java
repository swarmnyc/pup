package com.swarmnyc.pup.helpers;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import com.soundcloud.android.crop.Crop;
import com.swarmnyc.pup.AsyncCallback;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoHelper
{
	public static final int CODE_CAMERA = 74;
	public static final int CODE_PHOTO  = 75;
	private static AsyncCallback<Uri> m_callback;
	private static Fragment             m_fragment;
	private static Uri                  m_photo;

	public static void startPhotoIntent( Fragment fragment, AsyncCallback<Uri> callback )
	{
		m_fragment = fragment;
		m_callback = callback;

		DialogHelper.showOptions(fragment.getActivity(),
				new String[]{"Take a new photo", "Choose from gallery"}, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						if (which == 0) {
							try {
								Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
								m_photo = Uri.fromFile(createImageFile());

								takePicture.putExtra(MediaStore.EXTRA_OUTPUT, m_photo);
								m_fragment.startActivityForResult(takePicture, CODE_CAMERA);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							Intent pickPhoto = new Intent(
									Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
							);
							m_fragment.startActivityForResult(pickPhoto, CODE_PHOTO);
						}
					}
				}
		);
	}

	private static File createImageFile() throws IOException
	{
		// Create an image file name
		String timeStamp = new SimpleDateFormat( "yyyyMMdd_HHmmss" ).format( new Date() );
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment.getExternalStoragePublicDirectory(
			Environment.DIRECTORY_PICTURES
		);
		File image = null;

		image = File.createTempFile(
			imageFileName,  /* prefix */
			".jpg",         /* suffix */
			storageDir      /* directory */
		);

		return image;
	}

	public static void catchPhotoIntent( final int requestCode, final int resultCode, final Intent data )
	{
		if ( ( requestCode == CODE_CAMERA || requestCode == CODE_PHOTO ) && resultCode == Activity.RESULT_OK )
		{
			if ( requestCode == CODE_PHOTO )
			{
				m_photo = data.getData();
			}

			Uri destination = Uri.fromFile( new File( m_fragment.getActivity().getCacheDir(), "cropped" ) );
			Crop.of( m_photo, destination ).asSquare().withMaxSize( 500, 500 ).start(
				m_fragment.getActivity(), m_fragment
			);
		}
		else if ( requestCode == Crop.REQUEST_CROP && resultCode == Activity.RESULT_OK )
		{
			Uri uri = Crop.getOutput( data );

			if ( uri != null && m_callback != null )
			{
				m_callback.success( uri );
			}
		}
	}

	public static void close()
	{
		m_callback = null;
		m_fragment = null;
		m_photo = null;
	}
}
