package com.swarmnyc.pup.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.swarmnyc.pup.R;

public class DialogHelper {
    private static Dialog dialog;

    public static void showProgressDialog(Activity activity, int id) {
        ProgressDialog pd = new ProgressDialog(activity);
        pd.setMessage(activity.getString(id));
        pd.setCancelable(false);
        pd.show();

        setDialog(pd);
    }

    public static void hide() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public static void showError(Activity activity, final Exception exception) {
        hide();
        String message;

        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            message = activity.getString(R.string.message_operation_failed);
        } else {
            message = activity.getString(R.string.message_no_connection);
        }

        showError(activity, message);
        Log.e("PuP", "Unexpected Error:" + exception, exception);
    }

    public static void showError(Activity activity, final String message) {
        hide();

        View view = activity.findViewById(R.id.layout_coordinator);
        if (view == null) {
            view = activity.getWindow().getDecorView().getRootView();
        }

        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    public static void setDialog(final Dialog dialog) {
        hide();
        DialogHelper.dialog = dialog;
    }

    public static void showOptions(Activity activity, final String[] strings, AlertDialog.OnClickListener clickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setItems(strings, clickListener);
        setDialog(builder.show());
    }
}
