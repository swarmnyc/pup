package com.swarmnyc.pup.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.swarmnyc.pup.R;

import java.util.ArrayList;
import java.util.List;

public class HorizontalSpinner extends HorizontalScrollView
{
	private static String TAG = HorizontalSpinner.class.getSimpleName();
	private String[]       source;
	private int            itemWidth;
	private int            itemMiddleWidth;
	private int            itemHeight;
	private float          itemTextSize;
	private float          itemSelectedTextSize;
	private int            itemTextColor;
	private int            itemSelectedTextColor;
	private List<TextView> itemViews;
	private LinearLayout   itemContainer;
	private int            selectedPosition;
	private boolean        alreadyInitialized;
	private Handler stopHandler = new Handler()
	{
		@Override
		public void handleMessage( final Message msg )
		{
			super.handleMessage( msg );

			int x = HorizontalSpinner.this.getScrollX();
			int position = x / itemWidth;

			if ( x % itemWidth >= itemMiddleWidth ) { position++; }
			setSelectedPosition( position );
		}
	};

	public HorizontalSpinner( Context context )
	{
		super( context );
		init();
	}

	public HorizontalSpinner( Context context, AttributeSet attrs )
	{
		super( context, attrs );
		init();
	}

	private void init()
	{
		//TODO: Make configable
		setHorizontalScrollBarEnabled( false );
		itemWidth = (int) TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics()
		);
		itemHeight = (int) TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics()
		);

		itemMiddleWidth = itemWidth / 2;
		itemTextSize = TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_SP, 6, getResources().getDisplayMetrics() );
		itemSelectedTextSize = itemTextSize * 2f;

		itemTextColor = getResources().getColor( R.color.text_disabled );
		itemSelectedTextColor = getResources().getColor( R.color.pup_aqua );
		itemContainer = new LinearLayout( this.getContext() );
		itemContainer.setPadding(
			0, 0, 0, (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()
			)
		);

		itemContainer.setLayoutParams(
			new LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
			)
		);
		this.addView( itemContainer );

		itemViews = new ArrayList<>();
	}

	public void setSource( String[] source )
	{
		//TODO: Make configable

		if ( source == null ) { throw new IllegalArgumentException( "source" ); }

		this.source = source;
		for ( String o : source )
		{
			TextView tv = new TextView( this.getContext() );
			tv.setWidth( itemWidth );
			tv.setHeight( itemHeight );
			tv.setGravity( Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM );
			tv.setText( o );
			tv.setTextSize( itemTextSize );
			itemViews.add( tv );
			itemContainer.addView( tv );
		}
	}

	public void setSelectedPosition( int position )
	{
		this.selectedPosition = position;
		if ( !alreadyInitialized ) { return; }

		for ( int i = 0; i < itemViews.size(); i++ )
		{
			TextView itemView = itemViews.get( i );
			if ( i == position )
			{
				itemView.setTextSize( itemSelectedTextSize );
				itemView.setTextColor( itemSelectedTextColor );
			}
			else
			{
				itemView.setTextSize( itemTextSize );
				itemView.setTextColor( itemTextColor );
			}
		}

		int p = position * itemWidth;
		this.scrollTo( p, 0 );
	}

	protected void onLayout( final boolean changed, final int l, final int t, final int r, final int b )
	{
		super.onLayout( changed, l, t, r, b );
		if ( !alreadyInitialized )
		{
			alreadyInitialized = true;
			final int middlePoint = this.getMeasuredWidth() / 2;
			final int margin = middlePoint - ( itemMiddleWidth );
			itemContainer.setPadding( margin, 0, margin, 0 );

			setSelectedPosition( this.selectedPosition );
		}
	}


	@Override
	protected void onScrollChanged( final int x, final int y, final int oldx, final int oldy )
	{
		super.onScrollChanged( x, y, oldx, oldy );

		int position = x / itemWidth;
		int where = x % itemWidth;
		boolean isLeft = where >= itemMiddleWidth;
		if ( isLeft ) { position++; }

		Log.d( TAG, "X:" + x + ", Selection:" + position + ", where:" + where );

		float distancePercent;
		TextView view = itemViews.get( position );

		if ( isLeft )
		{
			distancePercent = 1 + ( where / (float) itemWidth );

			view.setTextColor( this.itemSelectedTextColor );
			view.setTextSize( this.itemTextSize * distancePercent );

			distancePercent = 1 + ( ( itemWidth - where ) / (float) itemWidth );

			view = itemViews.get( position - 1 );

			view.setTextColor( this.itemTextColor );
			view.setTextSize( this.itemTextSize * distancePercent );
		}
		else
		{
			distancePercent = 1 + ( ( itemWidth - where ) / (float) itemWidth );

			view.setTextColor( this.itemSelectedTextColor );
			view.setTextSize( this.itemTextSize * distancePercent );

			if ( position < source.length - 1 )
			{
				distancePercent = 1 + ( where / (float) itemWidth );
				view = itemViews.get( position + 1 );

				view.setTextColor( this.itemTextColor );
				view.setTextSize( this.itemTextSize * distancePercent );
			}
		}

		stopHandler.removeMessages( 0 );
		stopHandler.sendEmptyMessageDelayed( 0, 100 );
	}
}
