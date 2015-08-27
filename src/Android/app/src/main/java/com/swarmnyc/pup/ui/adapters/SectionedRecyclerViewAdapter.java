package com.swarmnyc.pup.ui.adapters;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class SectionedRecyclerViewAdapter<TView extends RecyclerView.ViewHolder, TItem>
	extends RecyclerView.Adapter<TView>
{
	private int                  m_total;
	private List<Section<TItem>> m_sections;

	public SectionedRecyclerViewAdapter()
	{
		m_sections = new ArrayList<>();
	}

	public void addSection( CharSequence title )
	{
		m_sections.add( new Section<TItem>( title, m_sections.size() ) );
	}

	public List<Section<TItem>> getSections()
	{
		return m_sections;
	}

	public void setItem( final List<TItem> list )
	{
		m_total = 0;
		for ( int i = 0; i < m_sections.size(); i++ )
		{
			m_sections.get( i ).getItems().clear();
		}

		addItem( list );
	}

	public void addItem( TItem data )
	{
		int p = addDataInternal( data );

		updateSections();

		notifyItemInserted( p );
	}

	public void addItem( List<TItem> list )
	{
		if ( list.size() != 0 )
		{
			for ( TItem data : list )
			{
				addDataInternal( data );
			}
		}

		updateSections();

		notifyDataSetChanged();
	}

	public TItem getItem( int i )
	{
		for ( Section<TItem> section : m_sections )
		{
			int size = section.getItems().size();
			if ( i > size )
			{
				if ( size > 0 ){
					i -= size + 1;
				}

				continue;
			}

			return section.getItems().get( i - 1 );
		}

		throw new RuntimeException( "Can't Find the Item at point:" + i );
	}

	@Override
	public int getItemCount()
	{
		return m_total;
	}

	public Section<TItem> getSection( int position )
	{
		for ( Section<TItem> section : m_sections )
		{
			if ( section.getHeaderPosition() == position )
			{
				return section;
			}
		}
		return null;
	}

	protected abstract int computeLocalSection( TItem data );

	private int addDataInternal( final TItem data )
	{
		m_total++;
		int position = computeLocalSection( data );
		Section<TItem> section = m_sections.get( position );
		section.addItem( data );

		if ( section.getItems().size() == 1 )
		{
			m_total++;
		}

		return section.getHeaderPosition() + section.getItems().size();
	}

	private void updateSections()
	{
		int p = 0;
		for ( Section<TItem> section : m_sections )
		{
			if ( section.getItems().size() == 0 )
			{
				section.setHeaderPosition( -1 );
			}
			else
			{
				section.setHeaderPosition( p );
				p += section.getItems().size() + 1;
			}
		}
	}

	public static class Section<D>
	{
		private int          m_header_position;
		private CharSequence m_title;
		private List<D>      m_items;
		private int          m_count;

		public Section( CharSequence title, final int position )
		{
			m_items = new ArrayList<>();
			m_title = title;
			m_header_position = position;
		}

		public CharSequence getTitle()
		{
			return m_title;
		}

		public void setTitle( final CharSequence title )
		{
			m_title = title;
		}

		public int getHeaderPosition()
		{
			return m_header_position;
		}

		public void setHeaderPosition( final int position )
		{
			m_header_position = position;
		}

		public void addItem( D data )
		{
			m_items.add( data );
		}

		public List<D> getItems()
		{
			return m_items;
		}

		public void setStaticCount( final int count )
		{
			m_count = count;
		}

		public int getStaticCount()
		{
			return m_count;
		}
	}
}
