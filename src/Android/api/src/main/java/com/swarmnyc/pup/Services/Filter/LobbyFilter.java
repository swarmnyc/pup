package com.swarmnyc.pup.Services.Filter;

import com.swarmnyc.pup.models.Game;
import com.swarmnyc.pup.models.GamePlatform;
import com.swarmnyc.pup.models.PlayStyle;
import com.swarmnyc.pup.models.SkillLevel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LobbyFilter extends GameFilter {
    private Game m_game;
    private Set<SkillLevel> levels;
    private Set<PlayStyle> styles;

    public LobbyFilter() {
        this.levels = new HashSet<>();
        this.styles = new HashSet<>();

    }

    public Game getGame() {
        return m_game;
    }

    public void setGame(final Game game) {
        m_game = game;
    }

    public void addSkillLevel(SkillLevel level) {
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

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        if (m_game != null) {
            map.put("gameId", m_game.getId());
        }

        return map;
    }
}
