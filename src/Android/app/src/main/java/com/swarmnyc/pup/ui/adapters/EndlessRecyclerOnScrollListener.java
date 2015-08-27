package com.swarmnyc.pup.ui.adapters;

/**
 * Created by somya on 6/5/15.
 */

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener
{
	public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();

	private int     previousTotal    = 0; // The total number of items in the dataset after the last load
	private boolean loading          = true; // True if we are still waiting for the last set of data to load.
	private int     visibleThreshold = 5; // The minimum amount of items to have below your current scroll position
	// before loading more.
	int firstVisibleItem, visibleItemCount, totalItemCount;

	private int current_page = 0;

	private LinearLayoutManager mLinearLayoutManager;

	public EndlessRecyclerOnScrollListener( LinearLayoutManager linearLayoutManager )
	{
		this.mLinearLayoutManager = linearLayoutManager;
	}

	@Override
	public void onScrolled( RecyclerView recyclerView, int dx, int dy )
	{
		super.onScrolled( recyclerView, dx, dy );

		visibleItemCount = recyclerView.getChildCount();
		totalItemCount = mLinearLayoutManager.getItemCount();
		firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

		if ( loading )
		{
			if ( totalItemCount > previousTotal )
			{
				loading = false;
				previousTotal = totalItemCount;
				onLoadComplete(current_page);
			}
		}

		//BUGGY for example totalItemCount = 3, visibleItemCount = 3, firstVisibleItem = 0, visibleThreshold = 5, so 0 <= 5, always loadMore.
		if ( !loading && ( totalItemCount - visibleItemCount ) <= ( firstVisibleItem + visibleThreshold ) )
		{
			// End has been reached

			// Do something
			current_page++;

			onLoadMore( current_page );

			loading = true;
		}
	}


	public abstract void onLoadComplete( final int current_page );


	public abstract void onLoadMore( int current_page );

	public int getCurrentPage()
	{
		return current_page;
	}

	public boolean isLoading()
	{
		return loading;
	}

	public void reset()
	{
		current_page = 0;
	}
}