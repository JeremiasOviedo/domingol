package com.domingol.engine.entities;

/**
 * Represents an action a player wants to perform.
 * <p>
 * <b>STUB:</b> Minimal implementation to allow Player class to compile.
 * Full implementation will be done in Phase 3 (AI & Decision Making).
 * <p>
 * Will contain:
 * <ul>
 *   <li>Action type (PASS, SHOOT, DRIBBLE, HOLD)</li>
 *   <li>Target player (for passes)</li>
 *   <li>Target position (for movement)</li>
 *   <li>Additional parameters (power, direction, etc.)</li>
 * </ul>
 */
public class PlayerAction {
    // TODO: Full implementation in Phase 3

    public enum ActionType {
        PASS,
        SHOOT,
        DRIBBLE,
        HOLD
    }

    private ActionType type;
    private Player target;  // For passes

    public PlayerAction(ActionType type) {
        this.type = type;
    }

    public PlayerAction(ActionType type, Player target) {
        this.type = type;
        this.target = target;
    }

    public ActionType getType() {
        return type;
    }

    public Player getTarget() {
        return target;
    }
}
