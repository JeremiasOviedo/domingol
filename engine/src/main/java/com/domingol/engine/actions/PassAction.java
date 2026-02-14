package com.domingol.engine.actions;

import com.domingol.engine.core.Action;
import com.domingol.engine.core.GameState;
import com.domingol.engine.entities.Player;
import com.domingol.engine.events.Event;
import com.domingol.engine.events.PassEvent;
import lombok.Getter;

import java.util.Random;

/**
 * Action: Player attempts to pass the ball to a teammate.
 * <p>
 * Success is determined by:
 * - Passer's passing stat
 * - Distance to receiver (future: context modifiers)
 * - Random roll
 * <p>
 * For MVP: Simple probability based on passing stat only.
 * Post-MVP: Add distance, pressure, angle modifiers.
 */
@Getter
public class PassAction implements Action {

    private final Player passer;
    private final Player receiver;

    public PassAction(Player passer, Player receiver) {
        this.passer = passer;
        this.receiver = receiver;
    }

    @Override
    public Event execute(GameState state, Random rng) {
        int tick = state.getCurrentTick();
        int minute = state.getCurrentMinute();
        int second = state.getCurrentSecond();

        // Calculate success chance (simple for MVP)
        double baseChance = passer.getStats().getPassing() / 20.0;

        // Future: Add context modifiers
        // double distance = passer.getFieldPosition().distanceTo(receiver.getFieldPosition());
        // double distanceMod = calculateDistanceMod(distance);
        // double finalChance = baseChance * distanceMod;

        // For now: just base chance
        boolean success = rng.nextDouble() < baseChance;

        if (success) {
            // Pass completed: receiver gets ball
            state.getBall().setPossessor(receiver);
        } else {
            // Pass failed: ball becomes loose
            state.getBall().setPossessor(null);
            // Ball position stays at passer's position for now
        }

        return new PassEvent(tick, minute, second, passer, receiver, success);
    }
}
