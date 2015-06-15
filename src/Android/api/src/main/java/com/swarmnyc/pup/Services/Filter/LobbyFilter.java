package com.swarmnyc.pup.Services.Filter;

import com.swarmnyc.pup.StringUtils;
import com.swarmnyc.pup.models.Game;
import com.swarmnyc.pup.models.PlayStyle;
import com.swarmnyc.pup.models.SkillLevel;

import java.util.*;

public class LobbyFilter extends GameFilter {
	private Game            game;
	private Set<SkillLevel> levels;
	private Set<PlayStyle>  styles;
	private Date            startTime;

	public LobbyFilter()
	{
		this.levels = new HashSet<>();
		this.styles = new HashSet<>();
	}

	public Game getGame()
	{
		return  game;
	}

	public void setGame( final Game game )
	{
		this.game = game;
	}

	public void addSkillLevel( SkillLevel level) {
        levels.add(level);
    }

    public void removeSkillLevel(SkillLevel level) {
        levels.remove(level);
    }

    public void addPlayStyle(PlayStyle style) {
        styles.add(style);
    }

    public void removePlayStyle(PlayStyle style) {
        styles.remove(style);
    }

    public Set<SkillLevel> getLevels() {
        return levels;
    }

    public Set<PlayStyle> getStyles() {
        return styles;
    }

	public Date getStartTime()
	{
		return startTime;
	}

	public void setStartTime( final Date startTime )
	{
		this.startTime = startTime;
	}

	@Override
	public void setPageIndex( final int pageIndex )
	{
		if (pageIndex == 0)
		{
			setStartTime( new Date(  ) ); // set DAte = now
		}
		super.setPageIndex( pageIndex );
	}

	@Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        if ( game != null) {
            map.put("gameId",  game.getId());
        }

	    if (startTime != null) {
		    map.put( "startTimeUtc", StringUtils.toDateString( startTime ) );
	    }

        return map;
    }
}
