package com.swarmnyc.pup.fragments;

import android.app.Application;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import com.swarmnyc.pup.PuPApplication;

/**
 * Created by somya on 6/27/15.
 */
public abstract class BaseFragment extends Fragment
{
	public ActionBar getSupportAcitonBar()
	{
		return getAppCompatActivity().getSupportActionBar();
	}

	public AppCompatActivity getAppCompatActivity() {return (AppCompatActivity) getActivity();}

	public void updateTitle()
	{

	}

	public void setTitle(CharSequence title)
	{
		getAppCompatActivity().setTitle( title );
	}

	public void setTitle( final int title )
	{
		getAppCompatActivity().setTitle( title );
	}

	public void setSubtitle(CharSequence title)
	{
		getAppCompatActivity().getSupportActionBar().setSubtitle( title );
	}

	@Override
	public void onStart()
	{
		super.onStart();
		PuPApplication.getInstance().sendScreenToTracker( getScreenName() );
	}

	protected abstract String getScreenName();
}
