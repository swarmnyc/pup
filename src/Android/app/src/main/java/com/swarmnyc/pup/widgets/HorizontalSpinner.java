package com.swarmnyc.pup.widgets;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.swarmnyc.pup.R;

import java.util.ArrayList;
import java.util.List;

public class HorizontalSpinner extends HorizontalScrollView
{
	private static final int SCROLL_HANDLER_SELECT = 0;
	private static final int SCROLL_HANDLER_MOVE   = 1;

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

	private int     selectedPosition;
	private boolean alreadyInitialized;
	private Handler scrollHandler;

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
		setSmoothScrollingEnabled( true );
		itemWidth = (int) TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics()
		);
		itemHeight = (int) TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics()
		);

		itemMiddleWidth = itemWidth / 2;
		itemTextSize = TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_SP, 6, getResources().getDisplayMetrics() );
		itemSelectedTextSize = itemTextSize * 2f;

		itemTextColor = getResources().getColor( R.color.text_disabled );
		itemSelectedTextColor = getResources().getColor( R.color.pup_teal );
		itemContainer = new LinearLayout( this.getContext() );
		itemContainer.setLayoutParams(
			new LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
			)
		);
		this.addView( itemContainer );

		itemViews = new ArrayList<>();

		if ( this.isInEditMode() )
		{
			setSource( new String[]{"Text 1", "Text 2", "Text 3"} );
		}

		scrollHandler = new Handler()
		{
			@Override
			public void handleMessage( final Message msg )
			{
				super.handleMessage( msg );

				int x = HorizontalSpinner.this.getScrollX();

				if ( msg.what == SCROLL_HANDLER_SELECT )
				{
					int position = x / itemWidth;

					if ( x % itemWidth >= itemMiddleWidth ) { position++; }

					setSelectedPosition( position, true );
				}
				else if ( msg.what == SCROLL_HANDLER_MOVE )
				{
					int goal = ( selectedPosition * itemWidth ) - x;

					if ( goal == 0 )
					{
						return;
					}

					if ( goal > 0 )
					{
						goal = Math.min( goal, 50 );
					}
					else
					{
						goal = Math.max( goal, -50 );
					}
					scrollBy( goal, 0 );
					scrollHandler.sendEmptyMessageDelayed( SCROLL_HANDLER_MOVE, 50 );
				}
			}
		};
	}

	public void setSource( String[] source )
	{
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

	public int getSelectedPosition()
	{
		return selectedPosition;
	}

	public void setSelectedPosition( int position )
	{
		setSelectedPosition( position, false );
	}

	public void setSelectedPosition( int position, boolean animation )
	{
		this.selectedPosition = position;
		if ( !alreadyInitialized ) { return; }

		if ( animation )
		{
			scrollHandler.sendEmptyMessage( SCROLL_HANDLER_MOVE );
		}
		else
		{
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
			scrollTo( p, 0 );
		}
	}

	protected void onLayout( final boolean changed, final int l, final int t, final int r, final int b )
	{
		if ( !alreadyInitialized )
		{
			alreadyInitialized = true;
			final int middlePoint = this.getMeasuredWidth() / 2;
			final int margin = middlePoint - ( itemMiddleWidth );
			itemContainer.setPadding( margin, 0, margin, 0 );

			setSelectedPosition( this.selectedPosition, true );
		}
		super.onLayout( changed, l, t, r, b );
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
	}

	@Override
	public boolean onTouchEvent( final MotionEvent ev )
	{
		// Log.d( TAG, "MotionEvent" + ev );
		scrollHandler.removeMessages( 0 );
		if ( ev.getAction() == MotionEvent.ACTION_UP )
		{
			scrollHandler.sendEmptyMessageDelayed( 0, 100 );
		}
		else
		{
			scrollHandler.sendEmptyMessageDelayed( 0, 1000 );
		}

		return super.onTouchEvent( ev );
	}
}
