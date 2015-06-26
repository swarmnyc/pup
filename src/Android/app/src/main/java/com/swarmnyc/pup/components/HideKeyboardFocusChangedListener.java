package com.swarmnyc.pup.components;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


public class HideKeyboardFocusChangedListener implements View.OnFocusChangeListener {
    private Activity m_activity;

    public HideKeyboardFocusChangedListener( Activity activity ) {
        m_activity = activity;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        //Log.d("FocusChangeListener", "hasFocus=" + hasFocus);

        if (hasFocus == false) {
            //Hide Keyboard
            if (v.getWindowToken() == null) {
                return;
            }

            InputMethodManager imm = (InputMethodManager) m_activity.getSystemService(
                    Context.INPUT_METHOD_SERVICE
            );

            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}
