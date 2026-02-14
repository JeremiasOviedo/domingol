package com.domingol.engine.events;

import com.domingol.engine.entities.Player;
import com.domingol.engine.entities.Team;
import lombok.Getter;

/**
 * Event: A goal was scored.
 */
@Getter
public class GoalEvent extends Event {

    private final Player scorer;
    private final Team team;

    public GoalEvent(int tick, int minute, int second, Player scorer, Team team) {
        super(tick, minute, second);
        this.scorer = scorer;
        this.team = team;
    }

    @Override
    public String toText() {
        return String.format("Min %d: %s scores! %s %d-%d",
                getMinute(),
                scorer.getName(),
                team.getName(),
                // Score is in GameState, we'll handle this later
                0, 0  // Placeholder
        );
    }
}
