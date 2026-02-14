package com.domingol.engine.entities;

/**
 * Player position on the field.
 * <p>
 * Defines the 10 standard positions in football/soccer.
 * <p>
 * <b>MVP Usage:</b> Indicates where a player should play (their role).
 * Players are assigned to positions when creating team formations.
 * <p>
 * <b>Phase 2/3 (Future):</b> Will be used with position familiarity system to apply
 * performance penalties when players are positioned outside their natural position.
 * For example, a Center Back (CB) playing as Striker (ST) would have stats reduced
 * by a familiarity multiplier (e.g., 0.3x).
 *
 * <h3>Position Categories:</h3>
 * <ul>
 *   <li><b>Goalkeeper:</b> GK</li>
 *   <li><b>Defense:</b> CB, LB, RB</li>
 *   <li><b>Midfield:</b> CDM, CM, CAM</li>
 *   <li><b>Attack:</b> LW, RW, ST</li>
 * </ul>
 *
 * <h3>Common Formations:</h3>
 * <pre>
 * 4-4-2:  GK, LB, CB, CB, RB, LW, CM, CM, RW, ST, ST
 * 4-3-3:  GK, LB, CB, CB, RB, CDM, CM, CM, LW, ST, RW
 * 4-2-3-1: GK, LB, CB, CB, RB, CDM, CDM, CAM, LW, RW, ST
 * </pre>
 */
public enum Position {

    /**
     * Goalkeeper - Last line of defense.
     * <p>
     * Primary stats: Goalkeeping, Positioning
     */
    GK("Goalkeeper"),

    /**
     * Center Back - Central defender.
     * <p>
     * Primary stats: Tackling, Positioning
     */
    CB("Center Back"),

    /**
     * Left Back - Left side defender.
     * <p>
     * Primary stats: Tackling, Pace, Stamina
     */
    LB("Left Back"),

    /**
     * Right Back - Right side defender.
     * <p>
     * Primary stats: Tackling, Pace, Stamina
     */
    RB("Right Back"),

    /**
     * Defensive Midfielder - Shields the defense.
     * <p>
     * Primary stats: Tackling, Positioning, Stamina
     */
    CDM("Defensive Midfielder"),

    /**
     * Central Midfielder - Box-to-box midfielder.
     * <p>
     * Primary stats: Passing, Stamina, balanced stats
     */
    CM("Central Midfielder"),

    /**
     * Attacking Midfielder - Creates chances.
     * <p>
     * Primary stats: Passing, Shooting, Positioning
     */
    CAM("Attacking Midfielder"),

    /**
     * Left Winger - Left side attacker.
     * <p>
     * Primary stats: Pace, Shooting, Stamina
     */
    LW("Left Winger"),

    /**
     * Right Winger - Right side attacker.
     * <p>
     * Primary stats: Pace, Shooting, Stamina
     */
    RW("Right Winger"),

    /**
     * Striker - Main goal scorer.
     * <p>
     * Primary stats: Shooting, Pace, Positioning
     */
    ST("Striker");

    private final String displayName;

    Position(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get human-readable name of the position.
     *
     * @return display name (e.g., "Center Back" for CB)
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if position is goalkeeper.
     *
     * @return true if GK, false otherwise
     */
    public boolean isGoalkeeper() {
        return this == GK;
    }

    /**
     * Check if position is defender (CB, LB, RB).
     *
     * @return true if defender position
     */
    public boolean isDefender() {
        return this == CB || this == LB || this == RB;
    }

    /**
     * Check if position is midfielder (CDM, CM, CAM).
     *
     * @return true if midfielder position
     */
    public boolean isMidfielder() {
        return this == CDM || this == CM || this == CAM;
    }

    /**
     * Check if position is attacker (LW, RW, ST).
     *
     * @return true if attacker position
     */
    public boolean isAttacker() {
        return this == LW || this == RW || this == ST;
    }

    /**
     * Get position category for tactical analysis.
     * <p>
     * Used for grouping positions in match statistics and analysis.
     *
     * @return category name: "Goalkeeper", "Defense", "Midfield", or "Attack"
     */
    public String getCategory() {
        if (isGoalkeeper()) return "Goalkeeper";
        if (isDefender()) return "Defense";
        if (isMidfielder()) return "Midfield";
        if (isAttacker()) return "Attack";
        throw new IllegalStateException("Position has no category: " + this);
    }
}
