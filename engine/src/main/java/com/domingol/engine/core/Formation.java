package com.domingol.engine.core;

import com.domingol.engine.entities.Team;

/**
 * Represents a team formation that positions players on the field.
 * <p>
 * Formations define where each player starts and their general area of play.
 * Examples: 4-4-2, 4-3-3, 3-5-2
 * <p>
 * Convention:
 * - Home team attacks RIGHT (towards x=105, away goal)
 * - Away team attacks LEFT (towards x=0, home goal)
 */
public interface Formation {

    /**
     * Applies this formation to a team.
     *
     * @param team team to position
     * @param attackingRight true if team attacks right (home), false if left (away)
     */
    void apply(Team team, boolean attackingRight);

    /**
     * Gets formation name (e.g., "4-4-2", "4-3-3").
     */
    String getName();
}
