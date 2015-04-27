package com.swarmnyc.pup.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.Services.Filter.GameFilter;
import com.swarmnyc.pup.Services.GameService;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.StringUtils;
import com.swarmnyc.pup.activities.MainActivity;
import com.swarmnyc.pup.adapters.AutoCompleteForPicturedModelAdapter;
import com.swarmnyc.pup.components.Action;
import com.swarmnyc.pup.models.Game;
import com.swarmnyc.pup.widgets.HorizontalSpinner;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CreateLobbyFragment extends Fragment {
	@Inject
	GameService gameService;

	@InjectView(R.id.img_game)
	ImageView gameImageView;

	@InjectView(R.id.text_name)
	AutoCompleteTextView gameNameTextEdit;

	@InjectView(R.id.spinner_play_style)
	HorizontalSpinner playStyleSpinner;

	GameFilter gameFilter = new GameFilter();

	Game selectedGame;

	AutoCompleteForPicturedModelAdapter<Game> gameAdapter;

	@Override
	public View onCreateView( LayoutInflater inflater,
	                          @Nullable
	                          ViewGroup container,
	                          @Nullable
	                          Bundle savedInstanceState
	)
	{
		return inflater.inflate( R.layout.fragment_lobby_create, container, false );
	}

	@Override
	public void onViewCreated( View view,
	                           @Nullable
	                           Bundle savedInstanceState
	)
	{
		super.onViewCreated( view, savedInstanceState );

		PuPApplication.getInstance().getComponent().inject( this );
		ButterKnife.inject( this, view );

		MainActivity.getInstance().hideToolbar();

		gameAdapter = new AutoCompleteForPicturedModelAdapter<Game>( this.getActivity() );

		gameAdapter.setSearchAction(
			new Action<CharSequence>()
			{
				@Override
				public void call( CharSequence constraint )
				{
					gameFilter.setSearch( constraint.toString() );
					gameService.getGames(
						gameFilter, new ServiceCallback<List<Game>>()
						{
							@Override
							public void success( List<Game> value )
							{
								gameAdapter.finishSearch( value );
							}
						}
					);
				}
			}
		);

		gameNameTextEdit.setAdapter( gameAdapter);

        gameNameTextEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedGame = gameAdapter.getItem(position);
                if (StringUtils.isNotEmpty(selectedGame.getThumbnailPictureUrl())) {
                    Picasso.with(getActivity()).load(selectedGame.getThumbnailPictureUrl()).centerCrop().fit().into(gameImageView);
                }
            }
        });

        playStyleSpinner.setSource(this.getResources().getStringArray(R.array.play_styles));
        playStyleSpinner.setSelectedPosition(1);


		//temp
		gameImageView.setImageDrawable( new ColorDrawable( Color.BLACK ) );
    }

}
