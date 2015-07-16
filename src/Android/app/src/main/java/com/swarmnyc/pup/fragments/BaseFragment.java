package com.swarmnyc.pup.fragments;

import android.app.Application;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.swarmnyc.pup.R;
import com.swarmnyc.pup.StringUtils;

public abstract class BaseFragment extends Fragment {
    public ActionBar getSupportAcitonBar() {
        return getAppCompatActivity().getSupportActionBar();
    }

    public AppCompatActivity getAppCompatActivity() {
        return (AppCompatActivity) getActivity();
    }

    public void updateTitle() {

    }

    public void setTitle(CharSequence title) {
        ((TextView) this.getActivity().findViewById(R.id.toolbar_title)).setText(title);
    }

    public void setTitle(final int title) {
        ((TextView) this.getActivity().findViewById(R.id.toolbar_title)).setText(title);
    }

    public void setSubtitle(CharSequence title) {
        TextView view = ((TextView) this.getActivity().findViewById(R.id.toolbar_subtitle));
        view.setText(title);
        if (StringUtils.isEmpty(title)){
            view.setVisibility(View.GONE);
        }else {
            view.setVisibility(view.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public abstract String getScreenName();
}
