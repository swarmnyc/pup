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
	private boolean m_needCount;

	public LobbyFilter()
	{
		this.levels = new HashSet<>();
		this.styles = new HashSet<>();
	}

	public Game getGame()
	{
		return game;
	}

	public void setGame( final Game game )
	{
		this.game = game;
	}

	public void addSkillLevel( SkillLevel level )
	{
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

	public void setNeedCount( final boolean needCount )
	{
		m_needCount = needCount;
	}

	public boolean isNeedCount()
	{
		return m_needCount;
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

		if (m_needCount) {
			map.put( "needCount", true );
			TimeZone  c = new GregorianCalendar(  ).getTimeZone();
			long time = c.getRawOffset();
			if ( c.useDaylightTime() ){
				time += c.getDSTSavings();
			}
			map.put( "timeZone", time / 3600000 );
		}

        return map;
    }
}
