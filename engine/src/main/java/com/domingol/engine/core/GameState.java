package com.domingol.engine.core;

import com.domingol.engine.entities.Ball;
import com.domingol.engine.entities.Team;
import lombok.Getter;

/**
 * Represents the current state of a football match.
 * Mutable class that changes every simulation tick.
 *
 * <p>Match structure:
 * - 90 minutes = 5,400 ticks (2 ticks/second)
 * - Tick 0-2699: First Half (45 min)
 * - Tick 2700-2819: Half Time (1 min)
 * - Tick 2820-5399: Second Half (45 min)
 * - Tick 5400+: Full Time
 */
@Getter
public class GameState {

    private static final int TICKS_PER_SECOND = 2;
    private static final int FIRST_HALF_END_TICK = 2700;   // 45 min * 60 sec * 2 ticks
    private static final int HALF_TIME_END_TICK = 2820;    // +60 sec
    private static final int SECOND_HALF_END_TICK = 5400;  // +45 min

    private final Team homeTeam;
    private final Team awayTeam;
    private final Ball ball;

    private int homeScore;
    private int awayScore;
    private int currentTick;
    private MatchPhase phase;

    /**
     * Creates initial game state for a match.
     *
     * @param homeTeam home team (must be valid for match)
     * @param awayTeam away team (must be valid for match)
     * @param ball match ball
     */
    public GameState(Team homeTeam, Team awayTeam, Ball ball) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.ball = ball;
        this.homeScore = 0;
        this.awayScore = 0;
        this.currentTick = 0;
        this.phase = MatchPhase.FIRST_HALF;
    }

    /**
     * Advances the match by one tick and updates match phase.
     */
    public void advanceTick() {
        currentTick++;
        updatePhase();
    }

    /**
     * Gets current match minute (0-90+).
     */
    public int getCurrentMinute() {
        return currentTick / (60 * TICKS_PER_SECOND);
    }

    /**
     * Gets current second within the minute (0-59).
     */
    public int getCurrentSecond() {
        int totalSeconds = currentTick / TICKS_PER_SECOND;
        return totalSeconds % 60;
    }

    /**
     * Checks if match is over.
     */
    public boolean isMatchOver() {
        return phase == MatchPhase.FULL_TIME;
    }

    /**
     * Increments home team score.
     */
    public void incrementHomeScore() {
        homeScore++;
    }

    /**
     * Increments away team score.
     */
    public void incrementAwayScore() {
        awayScore++;
    }

    /**
     * Increments score for given team.
     *
     * @param team team that scored
     * @throws IllegalArgumentException if team is not home or away
     */
    public void incrementScore(Team team) {
        if (team == homeTeam) {
            incrementHomeScore();
        } else if (team == awayTeam) {
            incrementAwayScore();
        } else {
            throw new IllegalArgumentException("Team is not part of this match");
        }
    }

    /**
     * Updates match phase based on current tick.
     */
    private void updatePhase() {
        if (currentTick <= FIRST_HALF_END_TICK) {
            phase = MatchPhase.FIRST_HALF;
        } else if (currentTick <= HALF_TIME_END_TICK) {
            phase = MatchPhase.HALF_TIME;
        } else if (currentTick <= SECOND_HALF_END_TICK) {
            phase = MatchPhase.SECOND_HALF;
        } else {
            phase = MatchPhase.FULL_TIME;
        }
    }

    /**
     * Represents the current phase of the match.
     */
    public enum MatchPhase {
        FIRST_HALF,
        HALF_TIME,
        SECOND_HALF,
        FULL_TIME
    }
}
