package com.swarmnyc.pup.components;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.activities.MainActivity;

public class DialogHelper
{
	private static Dialog dialog;

	public static void showProgressDialog( Activity activity, int id )
	{
		ProgressDialog pd = new ProgressDialog( activity );
		pd.setMessage( activity.getString( id ) );
		pd.setCancelable( false );
		pd.show();

		setDialog( pd );
	}

	public static void hide()
	{
		if ( dialog != null )
		{
			dialog.dismiss();
			dialog = null;
		}
	}

	public static void showError( Activity activity, final String message )
	{
		AlertDialog.Builder builder = new AlertDialog.Builder( activity );
		builder.setTitle( activity.getString( R.string.message_error_title ) );
		builder.setMessage( message );
		builder.setIcon( android.R.drawable.ic_dialog_alert );
		builder.setPositiveButton( activity.getString( R.string.text_ok ), null );
		setDialog( builder.show() );
	}

	public static void setDialog( final Dialog dialog )
	{
		hide();
		DialogHelper.dialog = dialog;
	}

	public static void showOptions( Activity activity, final String[] strings, AlertDialog.OnClickListener clickListener )
	{
		AlertDialog.Builder builder = new AlertDialog.Builder( activity );
		builder.setItems( strings, clickListener );
		setDialog( builder.show() );
	}
}
