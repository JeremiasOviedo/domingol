package com.domingol.engine.core;

import com.domingol.engine.entities.Team;
import com.domingol.engine.events.Event;
import lombok.Getter;

import java.util.List;

/**
 * Result of a simulated match.
 * Immutable DTO containing final score and events.
 */
@Getter
public class MatchResult {

    private final Team homeTeam;
    private final Team awayTeam;
    private final int homeScore;
    private final int awayScore;
    private final List<Event> events;

    public MatchResult(GameState finalState, List<Event> events) {
        this.homeTeam = finalState.getHomeTeam();
        this.awayTeam = finalState.getAwayTeam();
        this.homeScore = finalState.getHomeScore();
        this.awayScore = finalState.getAwayScore();
        this.events = events;
    }

    /**
     * Formats match result as text.
     */
    public String toText() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== MATCH RESULT ===\n");
        sb.append(String.format("%s %d - %d %s\n\n",
                homeTeam.getName(), homeScore,
                awayScore, awayTeam.getName()));

        return sb.toString();
    }
}
