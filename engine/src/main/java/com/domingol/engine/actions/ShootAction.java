package com.domingol.engine.actions;

import com.domingol.engine.core.Action;
import com.domingol.engine.core.GameState;
import com.domingol.engine.entities.Player;
import com.domingol.engine.events.Event;
import com.domingol.engine.events.GoalEvent;
import com.domingol.engine.events.ShotEvent;
import lombok.Getter;

import java.util.Random;

/**
 * Action: Player attempts to shoot at goal.
 * <p>
 * Success is determined by:
 * - Shooter's shooting stat
 * - Distance to goal (future)
 * - Shooting angle (future)
 * - Goalkeeper skill (future)
 * - Random roll
 * <p>
 * For MVP: Simple probability based on shooting stat only.
 */
@Getter
public class ShootAction implements Action {

    private final Player shooter;

    public ShootAction(Player shooter) {
        this.shooter = shooter;
    }

    @Override
    public Event execute(GameState state, Random rng) {
        int tick = state.getCurrentTick();
        int minute = state.getCurrentMinute();
        int second = state.getCurrentSecond();

        // Calculate shot chance (simple for MVP)
        double baseChance = shooter.getStats().getShooting() / 20.0;

        // Future: Add context modifiers
        // double distance = calculateDistanceToGoal(shooter);
        // double angle = calculateShootingAngle(shooter);
        // double distanceMod = calculateDistanceMod(distance);
        // double angleMod = calculateAngleMod(angle);
        // double finalChance = baseChance * distanceMod * angleMod;

        // For now: 50% of base chance = goal chance
        double goalChance = baseChance * 0.5;
        double onTargetChance = baseChance * 0.8;

        boolean goal = rng.nextDouble() < goalChance;
        boolean onTarget = goal || rng.nextDouble() < onTargetChance;

        if (goal) {
            // GOAL!
            state.incrementScore(shooter.getTeam());
            state.getBall().setPossessor(null); // Ball goes to center for kickoff

            // Create both ShotEvent and GoalEvent
            return new GoalEvent(tick, minute, second, shooter, shooter.getTeam());
        } else {
            // Shot missed or saved
            state.getBall().setPossessor(null); // Ball becomes loose

            return new ShotEvent(tick, minute, second, shooter, onTarget, false);
        }
    }
}
