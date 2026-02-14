package com.domingol.engine.events;

import com.domingol.engine.entities.Player;
import lombok.Getter;

/**
 * Event: A shot was taken.
 */
@Getter
public class ShotEvent extends Event {

    private final Player shooter;
    private final boolean onTarget;
    private final boolean goal;

    public ShotEvent(int tick, int minute, int second,
                     Player shooter, boolean onTarget, boolean goal) {
        super(tick, minute, second);
        this.shooter = shooter;
        this.onTarget = onTarget;
        this.goal = goal;
    }

    @Override
    public String toText() {
        if (goal) {
            return String.format("Min %d: %s shoots... GOAL!",
                    getMinute(), shooter.getName());
        } else if (onTarget) {
            return String.format("Min %d: %s shoots... SAVED!",
                    getMinute(), shooter.getName());
        } else {
            return String.format("Min %d: %s shoots... WIDE!",
                    getMinute(), shooter.getName());
        }
    }
}
