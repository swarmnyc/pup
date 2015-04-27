package com.swarmnyc.pup.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.squareup.picasso.Picasso;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.StringUtils;
import com.swarmnyc.pup.components.Action;
import com.swarmnyc.pup.models.PicturedModel;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteForPicturedModelAdapter<T extends PicturedModel> extends BaseAdapter implements Filterable
{
	private List<T> data = new ArrayList<>();
	private Context              context;
	private InternalFilter       filter;
	private Action<CharSequence> searchAction;

	public AutoCompleteForPicturedModelAdapter( Context context )
	{
		this.context = context;
		this.filter = new InternalFilter();
	}

	@Override
	public int getCount()
	{
		return this.data.size();
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{
		if ( convertView == null )
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			convertView = inflater.inflate( R.layout.item_auto_complete_picturedmodel, parent, false );


		}
		PicturedModel model = this.data.get( position );
		if ( StringUtils.isNotEmpty( model.getThumbnailPictureUrl() ) )
		{
			Picasso.with( context )
			       .load( model.getThumbnailPictureUrl() )
			       .centerCrop()
			       .fit()
			       .into( ( (ImageView) convertView.findViewById( R.id.image_view ) ) );
		}

		( (TextView) convertView.findViewById( R.id.text_view ) ).setText( model.getName() );
		return convertView;
	}

	@Override
	public T getItem( int index )
	{
		return this.data.get( index );
	}

	@Override
	public long getItemId( int position )
	{
		return position;
	}

	@Override
	public Filter getFilter()
	{
		return filter;
	}

	public void setSearchAction( Action<CharSequence> searchAction )
	{
		this.searchAction = searchAction;
	}

	public void finishSearch( List<T> value )
	{
		data = value;
		filter.setFinishCallback();
	}

	private class InternalFilter extends Filter
	{
		@Override
		protected FilterResults performFiltering( CharSequence constraint )
		{
			if ( constraint != null )
			{
				//TODO: Cache result if just few word changes
				AutoCompleteForPicturedModelAdapter.this.searchAction.call( constraint );
			}

			return null;
		}

		@Override
		public CharSequence convertResultToString( Object resultValue )
		{
			return ( (PicturedModel) resultValue ).getName();
		}

		@Override
		protected void publishResults( CharSequence constraint, FilterResults results )
		{
			if ( constraint != null && results == null )
			{
				return;
			}

			Log.d( "AutoComplete", "publishResults:" + results );
			if ( results != null && results.count > 0 )
			{
				notifyDataSetChanged();
			}
			else
			{
				notifyDataSetInvalidated();
			}
		}

		private void setFinishCallback()
		{
			FilterResults filterResults = new FilterResults();
			filterResults.values = data;
			filterResults.count = data.size();
			publishResults( null, filterResults );
		}
	}
}
