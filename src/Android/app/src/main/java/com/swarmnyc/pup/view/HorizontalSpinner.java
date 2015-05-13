package com.swarmnyc.pup.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
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
import com.swarmnyc.pup.components.Typefaces;

import java.util.ArrayList;
import java.util.List;

public class HorizontalSpinner extends HorizontalScrollView
{
	private static final int SCROLL_HANDLER_SELECT = 0;
	private static final int SCROLL_HANDLER_MOVE   = 1;

	private static String TAG = HorizontalSpinner.class.getSimpleName();
	private Typeface       itemTypeface;
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
	private float   itemSizeGap;
	private boolean isSpecificItemWidth;
	private boolean isSpecificItemHeight;

	public HorizontalSpinner( Context context )
	{
		super( context );
		init();
	}

	public HorizontalSpinner( Context context, AttributeSet attrs )
	{
		super( context, attrs );

		TypedArray typedArray = context.obtainStyledAttributes( attrs, R.styleable.HorizontalSpinner );

		if ( typedArray.hasValue( R.styleable.HorizontalSpinner_itemColor ) )
		{ itemTextColor = typedArray.getColor( R.styleable.HorizontalSpinner_itemColor, 0 ); }

		if ( typedArray.hasValue( R.styleable.HorizontalSpinner_itemSelectColor ) )
		{ itemSelectedTextColor = typedArray.getColor( R.styleable.HorizontalSpinner_itemSelectColor, 0 ); }

		if ( typedArray.hasValue( R.styleable.HorizontalSpinner_itemTextSize ) )
		{ itemTextSize = typedArray.getDimension( R.styleable.HorizontalSpinner_itemTextSize, 0 ); }

		if ( typedArray.hasValue( R.styleable.HorizontalSpinner_itemSelectTextSize ) )
		{ itemSelectedTextSize = typedArray.getDimension( R.styleable.HorizontalSpinner_itemSelectTextSize, 0 ); }

		if ( typedArray.hasValue( R.styleable.HorizontalSpinner_itemWidth ) )
		{
			isSpecificItemWidth = true;
			itemWidth = (int) typedArray.getDimension( R.styleable.HorizontalSpinner_itemWidth, 0 );
			itemMiddleWidth = itemWidth / 2;
		}

		if ( typedArray.hasValue( R.styleable.HorizontalSpinner_itemHeight ) )
		{
			isSpecificItemHeight = true;
			itemHeight = (int) typedArray.getDimension( R.styleable.HorizontalSpinner_itemHeight, 0 );
		}

		if ( !isInEditMode() && typedArray.hasValue( R.styleable.HorizontalSpinner_itemTextFont ) )
		{
			String font = typedArray.getString( R.styleable.HorizontalSpinner_itemTextFont );
			itemTypeface = Typefaces.get( font );
		}
		init();
	}

	private void init()
	{
		setHorizontalScrollBarEnabled( false );
		setSmoothScrollingEnabled( true );

		if ( itemTextSize == 0 )
		{
			itemTextSize = TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()
			);
		}

		if ( itemSelectedTextSize == 0 )
		{
			itemSelectedTextSize = itemTextSize * 2f;
		}

		if ( itemTextColor == 0 )
		{
			itemTextColor = getResources().getColor( R.color.text_disabled );
		}

		if ( itemSelectedTextColor == 0 )
		{
			itemSelectedTextColor = getResources().getColor( R.color.pup_teal );
		}

		itemSizeGap = itemSelectedTextSize - itemTextSize;
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
						goal = Math.min( goal, 20 );
					}
					else
					{
						goal = Math.max( goal, -20 );
					}
					scrollBy( goal, 0 );
					scrollHandler.sendEmptyMessage( SCROLL_HANDLER_MOVE );
				}
				else if ( msg.what == 2 )
				{
					int p = selectedPosition * itemWidth;
					scrollTo( p, 0 );
					if ( HorizontalSpinner.this.getScrollX() != p )
					{ scrollHandler.sendEmptyMessageDelayed( 2, 100 ); }
				}
			}
		};
	}

	public void setSource( String[] source )
	{
		if ( source == null ) { throw new IllegalArgumentException( "source" ); }
		this.source = source;
		resetItemSize( source );

		this.itemViews.clear();
		this.itemContainer.removeAllViews();

		for ( String o : source )
		{
			TextView tv = new TextView( this.getContext() );
			tv.setWidth( itemWidth );
			tv.setHeight( itemHeight );
			tv.setGravity( Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM );
			tv.setText( o );
			tv.setPadding( 0, 0, 0, 0 );
			tv.setBackgroundColor( Color.TRANSPARENT );
			tv.setTextSize( TypedValue.COMPLEX_UNIT_PX, itemTextSize );
			if ( itemTypeface != null )
			{
				tv.setTypeface( itemTypeface );
			}
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
					itemView.setTextSize( TypedValue.COMPLEX_UNIT_PX, itemSelectedTextSize );
					itemView.setTextColor( itemSelectedTextColor );
				}
				else
				{
					itemView.setTextSize( TypedValue.COMPLEX_UNIT_PX, itemTextSize );
					itemView.setTextColor( itemTextColor );
				}
			}

			scrollHandler.sendEmptyMessageDelayed( 2, 100 );
		}
	}

	@Override
	protected void onSizeChanged( final int w, final int h, final int oldw, final int oldh )
	{
		super.onSizeChanged( w, h, oldw, oldh );

		if ( !alreadyInitialized && !isInEditMode() )
		{
			alreadyInitialized = true;
			int middlePoint = this.getMeasuredWidth() / 2;
			int margin = middlePoint - itemMiddleWidth;
			itemContainer.setPadding( margin, 0, margin, 0 );

			setSelectedPosition( this.selectedPosition, false );
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

		float scale;
		TextView view = itemViews.get( position );

		if ( isLeft )
		{
			scale = itemSizeGap * ( where / (float) itemWidth );

			view.setTextColor( this.itemSelectedTextColor );
			view.setTextSize( TypedValue.COMPLEX_UNIT_PX, this.itemTextSize + scale );

			scale = itemSizeGap * ( ( itemWidth - where ) / (float) itemWidth );

			view = itemViews.get( position - 1 );

			view.setTextColor( this.itemTextColor );
			view.setTextSize( TypedValue.COMPLEX_UNIT_PX, this.itemTextSize + scale );
		}
		else
		{
			scale = itemSizeGap * ( ( itemWidth - where ) / (float) itemWidth );

			view.setTextColor( this.itemSelectedTextColor );
			view.setTextSize( TypedValue.COMPLEX_UNIT_PX, this.itemTextSize + scale );

			if ( position < source.length - 1 )
			{
				scale = itemSizeGap * ( where / (float) itemWidth );
				view = itemViews.get( position + 1 );

				view.setTextColor( this.itemTextColor );
				view.setTextSize( TypedValue.COMPLEX_UNIT_PX, this.itemTextSize + scale );
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

	private void resetItemSize( final String[] source )
	{
		if ( !isSpecificItemWidth || !isSpecificItemHeight )
		{
			TextPaint tp = new TextPaint();
			tp.setTextSize( itemSelectedTextSize );
			if ( itemTypeface != null )
			{
				tp.setTypeface( itemTypeface );
			}

			float largest = 0;
			for ( String s : source )
			{
				float size = tp.measureText( s );
				if ( size > largest )
				{ largest = size; }
			}

			if ( !isSpecificItemHeight )
			{ itemHeight = (int) ( ( tp.getTextSize() + tp.descent() ) ); }

			if ( !isSpecificItemWidth )
			{ itemWidth = Math.max( 300, (int) ( largest * 1.2 ) ); }

			itemMiddleWidth = itemWidth / 2;
		}
	}
}
