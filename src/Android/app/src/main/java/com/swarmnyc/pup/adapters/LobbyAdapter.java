package com.swarmnyc.pup.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.components.Action;
import com.swarmnyc.pup.components.Navigator;
import com.swarmnyc.pup.models.Lobby;
import com.swarmnyc.pup.view.LobbyListItemView;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class LobbyAdapter extends SectionedRecyclerViewAdapter<LobbyAdapter.BaseViewHolder, Lobby>
{
	private final long m_nextWeekTime;
	private boolean m_isLoading = false;
	private Context        m_context;
	private LayoutInflater m_layoutInflater;
	private Action         m_reachEndAction;

	private static class ItemViewType
	{
		static final int RegularView = 0;
		static final int SectionView = 1;
		static final int LoadingView = 2;
	}

	public LobbyAdapter( final Context context )
	{
		m_context = context;

		m_layoutInflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

		addSection( context.getString( R.string.text_happening_soon ) );
		addSection( context.getString( R.string.text_later_this_week ) );

		Calendar c = new GregorianCalendar();
		c.add( Calendar.DAY_OF_MONTH, 2 ); // Two days
		m_nextWeekTime = c.getTimeInMillis();
	}

	public void endLoading()
	{
		if ( m_isLoading )
		{
			m_isLoading = false;
			notifyItemRemoved( super.getItemCount() );
		}
	}

	public void startLoading()
	{
		m_isLoading = true;
		notifyItemInserted( super.getItemCount() );
	}

	public Lobby getLastItem()
	{
		if ( getSections().size() ==0 )
		{
			return null;
		}

		Lobby lobby = null;
		for ( Section<Lobby> lobbySection : getSections() )
		{
			int size =lobbySection.getItems().size();
			if ( size > 0 ){
				lobby = lobbySection.getItems().get( lobbySection.getItems().size()-1 );
			}
		}

		return lobby;
	}

	public void setReachEndAction( Action action )
	{
		m_reachEndAction = action;
	}

	@Override
	public BaseViewHolder onCreateViewHolder(
		final ViewGroup viewGroup, final int viewType
	)
	{
		switch ( viewType )
		{
			case ItemViewType.RegularView:
				return new LobbyViewHolder( new LobbyListItemView( m_context ) );
			case ItemViewType.SectionView:
				return new SectionViewHolder( m_layoutInflater.inflate( R.layout.item_lobby_section, null ) );
			case ItemViewType.LoadingView:
				return new LoadingMoreViewHolder( m_layoutInflater.inflate( R.layout.item_loading, null ) );
		}
		return null;
	}

	@Override
	public void onBindViewHolder( final BaseViewHolder viewHolder, final int i )
	{
		if ( viewHolder instanceof LobbyViewHolder )
		{
			( (LobbyViewHolder) viewHolder ).m_lobbyListItemView.setLobby( getItem( i ) );
		}
		else if ( viewHolder instanceof SectionViewHolder )
		{
			( (SectionViewHolder) viewHolder ).setSection( getSection( i ) );
		}
		/*else if ( viewHolder instanceof LoadingMoreViewHolder )
		{
		}*/

		if ( m_reachEndAction!=null && !m_isLoading && i == getItemCount()-1 ){
			m_reachEndAction.call( null );
		}
	}

	@Override
	public int getItemCount()
	{
		if ( m_isLoading )
		{
			return super.getItemCount() + 1;
		}

		return super.getItemCount();
	}

	@Override
	public int getItemViewType( final int position )
	{
		if ( m_isLoading && position >= super.getItemCount() ){
			return ItemViewType.LoadingView;
		} else {
			Section section = getSection( position );
			return section == null ? ItemViewType.RegularView : ItemViewType.SectionView;
		}
	}

	@Override
	protected int computeLocalSection( final Lobby data )
	{
		return data.getStartTime().getTime() < m_nextWeekTime ? 0 : 1;
	}


	public abstract class BaseViewHolder extends RecyclerView.ViewHolder
	{
		public BaseViewHolder( final View itemView )
		{
			super( itemView );
		}
	}

	public class LoadingMoreViewHolder extends BaseViewHolder
	{
		// each data item is just a string in this case
		public View m_progressBar;

		public LoadingMoreViewHolder( final View view )
		{
			super( view );
			m_progressBar = view;
			view.setLayoutParams(
				new RecyclerView.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
				)
			);
		}
	}

	public class SectionViewHolder extends BaseViewHolder
	{
		public SectionViewHolder( final View view )
		{
			super( view );
		}

		public void setSection( Section<Lobby> section )
		{
			( (TextView) itemView ).setText( section.getTitle() + " (" + section.getItems().size() + ")" );
		}
	}

	public class LobbyViewHolder extends BaseViewHolder
	{
		// each data item is just a string in this case
		public LobbyListItemView m_lobbyListItemView;

		public LobbyViewHolder( final LobbyListItemView lobbyListItemView )
		{
			super( lobbyListItemView );
			m_lobbyListItemView = lobbyListItemView;
			m_lobbyListItemView.setOnClickListener(
				new View.OnClickListener()
				{
					@Override
					public void onClick( final View v )
					{
						Navigator.ToLobby(
							m_context, lobbyListItemView.getLobby()
						);
					}
				}
			);
		}
	}
}
