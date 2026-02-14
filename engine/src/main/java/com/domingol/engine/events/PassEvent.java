package com.domingol.engine.events;

import com.domingol.engine.entities.Player;
import lombok.Getter;

/**
 * Event: A pass was attempted.
 */
@Getter
public class PassEvent extends Event {

    private final Player passer;
    private final Player receiver;
    private final boolean success;

    public PassEvent(int tick, int minute, int second,
                     Player passer, Player receiver, boolean success) {
        super(tick, minute, second);
        this.passer = passer;
        this.receiver = receiver;
        this.success = success;
    }

    @Override
    public String toText() {
        if (success) {
            return String.format("Min %d: %s passes to %s... COMPLETED",
                    getMinute(), passer.getName(), receiver.getName());
        } else {
            return String.format("Min %d: %s attempts pass to %s... INTERCEPTED",
                    getMinute(), passer.getName(), receiver.getName());
        }
    }
}