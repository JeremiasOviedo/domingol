package com.domingol.engine.events;

import lombok.Getter;

/**
 * Base class for all match events.
 * <p>
 * Events are the immutable record of what happened during a match.
 * They are created by Action execution and logged for match reports.
 * <p>
 * Design:
 * - Immutable: Created once, never modified
 * - Loggable: Can be formatted to text
 * - Timestamped: Knows when it occurred
 */
@Getter
public abstract class Event {

    private final int tick;
    private final int minute;
    private final int second;

    protected Event(int tick, int minute, int second) {
        this.tick = tick;
        this.minute = minute;
        this.second = second;
    }

    /**
     * Formats this event as human-readable text.
     * Example: "Min 23: Haaland shoots from 18m... GOAL!"
     */
    public abstract String toText();
}
