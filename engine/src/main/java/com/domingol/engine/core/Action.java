package com.domingol.engine.core;

import com.domingol.engine.events.Event;
import java.util.Random;

/**
 * Represents an action a player can take during a match.
 * <p>
 * Actions are decisions made by AI that get executed and produce events.
 * Examples: PassAction, ShootAction, TackleAction, DribbleAction.
 * <p>
 * Design:
 * - Immutable: Created by AI, executed once
 * - Deterministic: Same state + seed = same outcome
 * - Event-producing: Always returns an Event (success or failure)
 */
public interface Action {

    /**
     * Executes this action and returns the resulting event.
     *
     * @param state current game state (can be modified by action)
     * @param rng random number generator (for skill checks)
     * @return event describing what happened
     */
    Event execute(GameState state, Random rng);
}
