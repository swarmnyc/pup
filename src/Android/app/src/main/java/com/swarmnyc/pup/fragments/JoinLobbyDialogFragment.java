package com.swarmnyc.pup.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import com.swarmnyc.pup.R;

public class JoinLobbyDialogFragment extends DialogFragment
{
	@Override
	public Dialog onCreateDialog( final Bundle savedInstanceState )
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		//builder.setMessage( R.string.dialog_fire_missiles);

		// Create the AlertDialog object and return it
		return builder.create();
	}
}
