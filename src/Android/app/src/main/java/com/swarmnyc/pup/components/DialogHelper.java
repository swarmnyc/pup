package com.swarmnyc.pup.components;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.activities.MainActivity;

public class DialogHelper
{
	private static Dialog dialog;

	public static void showProgressDialog( int id )
	{
		ProgressDialog pd = new ProgressDialog( MainActivity.getInstance() );
		pd.setMessage( MainActivity.getInstance().getString( id ) );
		pd.setCancelable( false );
		pd.show();

		setDialog( pd );
	}

	public static void hide()
	{
		if ( dialog != null )
		{
			dialog.hide();
			dialog = null;
		}
	}

	public static void showError( final String message )
	{
		AlertDialog.Builder builder = new AlertDialog.Builder( MainActivity.getInstance() );
		builder.setTitle( MainActivity.getInstance().getString( R.string.message_error_title ) );
		builder.setMessage( message );
		builder.setIcon( android.R.drawable.ic_dialog_alert );
		builder.setPositiveButton( MainActivity.getInstance().getString( R.string.text_ok ), null );
		setDialog( builder.show() );

	}

	public static void setDialog( final Dialog dialog )
	{
		hide();
		DialogHelper.dialog = dialog;
	}
}
