package com.domingol.engine.core;

import com.domingol.engine.events.Event;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Logs all events that occur during a match.
 * <p>
 * Events are stored chronologically and can be formatted
 * to text for match reports.
 */
public class EventLogger {

    @Getter
    private final List<Event> events;

    public EventLogger() {
        this.events = new ArrayList<>();
    }

    /**
     * Logs an event.
     */
    public void log(Event event) {
        events.add(event);
    }

    /**
     * Returns all events as unmodifiable list.
     */
    public List<Event> getEvents() {
        return Collections.unmodifiableList(events);
    }

    /**
     * Formats all events as text report.
     */
    public String toText() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== MATCH EVENTS ===\n\n");

        for (Event event : events) {
            sb.append(event.toText()).append("\n");
        }

        return sb.toString();
    }
}