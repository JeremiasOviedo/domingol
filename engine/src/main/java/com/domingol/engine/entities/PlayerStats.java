package com.domingol.engine.entities;

import lombok.Builder;
import lombok.Value;

/**
 * Player statistics (1-20 scale, except goalkeeping: 0-20)
 * <p>
 * Immutable class representing a player's attributes.
 * Stats do not change during a match, but can be improved through training (future feature).
 * <p>
 * Categories:
 * <ul>
 *   <li><b>PHYSICAL:</b> pace, stamina</li>
 *   <li><b>ATTACKING:</b> shooting</li>
 *   <li><b>CREATIVE:</b> passing</li>
 *   <li><b>DEFENSIVE:</b> tackling</li>
 *   <li><b>MENTAL:</b> positioning</li>
 *   <li><b>GOALKEEPER:</b> goalkeeping (0 for outfield players)</li>
 * </ul>
 *
 * @see #calculateMaxSpeed() for deriving movement speed from pace
 */
@Value
@Builder(toBuilder = true)
public class PlayerStats {

    // === PHYSICAL ===

    /**
     * Max speed capability (1-20).
     * <p>
     * Used to calculate actual max speed in m/s via {@link #calculateMaxSpeed()}.
     * Formula: 5.0 + (pace / 20.0) * 5.0 → Range: 5.25-10.0 m/s
     */
    int pace;

    /**
     * 90-minute endurance (1-20).
     * <p>
     * Affects fatigue modifier in late game:
     * - High stamina: maintains performance until minute 80+
     * - Low stamina: significant drop after minute 60
     */
    int stamina;

    // === ATTACKING ===

    /**
     * Shot power and accuracy (1-20).
     * <p>
     * Used in shot events to calculate goal probability.
     * Modified by context: distance, angle, pressure, etc.
     */
    int shooting;

    // === CREATIVE ===

    /**
     * Pass accuracy and quality (1-20).
     * <p>
     * Used in pass events to calculate completion probability.
     * Modified by context: distance, pressure, pass difficulty, etc.
     */
    int passing;

    // === DEFENSIVE ===

    /**
     * Ball recovery, interceptions, blocks (1-20).
     * <p>
     * Used in defensive events:
     * - Interceptions: chance to cut passing lanes
     * - Tackles: 1v1 ball recovery
     * - Blocks: shot blocking
     */
    int tackling;

    // === MENTAL ===

    /**
     * Tactical intelligence and decision making (1-20).
     * <p>
     * Affects:
     * - AI decision quality (when to pass vs shoot)
     * - Defensive positioning (interception opportunities)
     * - Attacking runs (off-ball movement)
     */
    int positioning;

    // === GOALKEEPER ===

    /**
     * Shot stopping ability (0-20).
     * <p>
     * Special stat:
     * - 0 for outfield players (not used)
     * - 1-20 for goalkeepers (primary stat)
     * <p>
     * Used in save events to calculate save probability.
     */
    int goalkeeping;

    /**
     * Private constructor called by Lombok builder.
     * Validates all stats are within acceptable ranges.
     *
     * @throws IllegalArgumentException if any stat is out of range
     */
    private PlayerStats(int pace, int stamina, int shooting,
                        int passing, int tackling, int positioning,
                        int goalkeeping) {
        this.pace = validate(pace, "Pace", 1, 20);
        this.stamina = validate(stamina, "Stamina", 1, 20);
        this.shooting = validate(shooting, "Shooting", 1, 20);
        this.passing = validate(passing, "Passing", 1, 20);
        this.tackling = validate(tackling, "Tackling", 1, 20);
        this.positioning = validate(positioning, "Positioning", 1, 20);
        this.goalkeeping = validate(goalkeeping, "Goalkeeping", 0, 20);
    }

    /**
     * Validates a stat is within acceptable range.
     *
     * @param value stat value to validate
     * @param name  stat name (for error message)
     * @param min   minimum acceptable value (inclusive)
     * @param max   maximum acceptable value (inclusive)
     * @return validated value
     * @throws IllegalArgumentException if value is out of range
     */
    private static int validate(int value, String name, int min, int max) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                    String.format("%s must be %d-%d, got: %d", name, min, max, value)
            );
        }
        return value;
    }

    /**
     * Calculate maximum movement speed in meters per second.
     * <p>
     * Formula: 5.0 + (pace / 20.0) * 5.0
     * <ul>
     *   <li>pace = 1  → 5.25 m/s (very slow, typical goalkeeper)</li>
     *   <li>pace = 10 → 7.5 m/s  (average player)</li>
     *   <li>pace = 20 → 10.0 m/s (elite sprinter)</li>
     * </ul>
     * <p>
     * Real-world reference: Elite footballers sprint at ~10 m/s (36 km/h).
     *
     * @return max speed in m/s
     */
    public double calculateMaxSpeed() {
        return 5.0 + (pace / 20.0) * 5.0;
    }

    // === TRAINING IMPROVEMENTS (Future Feature) ===

    /**
     * Create new stats with improved pace (capped at 20).
     * <p>
     * Returns new immutable instance - original unchanged.
     * Used for training/progression systems (future feature).
     *
     * @param increment amount to increase pace (can be negative for decline)
     * @return new PlayerStats with modified pace
     */
    public PlayerStats withImprovedPace(int increment) {
        return toBuilder()
                .pace(Math.min(20, Math.max(1, pace + increment)))
                .build();
    }

    /**
     * Create new stats with improved stamina (capped at 20).
     *
     * @param increment amount to increase stamina
     * @return new PlayerStats with modified stamina
     */
    public PlayerStats withImprovedStamina(int increment) {
        return toBuilder()
                .stamina(Math.min(20, Math.max(1, stamina + increment)))
                .build();
    }

    /**
     * Create new stats with improved shooting (capped at 20).
     *
     * @param increment amount to increase shooting
     * @return new PlayerStats with modified shooting
     */
    public PlayerStats withImprovedShooting(int increment) {
        return toBuilder()
                .shooting(Math.min(20, Math.max(1, shooting + increment)))
                .build();
    }

    /**
     * Create new stats with improved passing (capped at 20).
     *
     * @param increment amount to increase passing
     * @return new PlayerStats with modified passing
     */
    public PlayerStats withImprovedPassing(int increment) {
        return toBuilder()
                .passing(Math.min(20, Math.max(1, passing + increment)))
                .build();
    }

    /**
     * Create new stats with improved tackling (capped at 20).
     *
     * @param increment amount to increase tackling
     * @return new PlayerStats with modified tackling
     */
    public PlayerStats withImprovedTackling(int increment) {
        return toBuilder()
                .tackling(Math.min(20, Math.max(1, tackling + increment)))
                .build();
    }

    /**
     * Create new stats with improved positioning (capped at 20).
     *
     * @param increment amount to increase positioning
     * @return new PlayerStats with modified positioning
     */
    public PlayerStats withImprovedPositioning(int increment) {
        return toBuilder()
                .positioning(Math.min(20, Math.max(1, positioning + increment)))
                .build();
    }

    /**
     * Create new stats with improved goalkeeping (capped at 20).
     *
     * @param increment amount to increase goalkeeping
     * @return new PlayerStats with modified goalkeeping
     */
    public PlayerStats withImprovedGoalkeeping(int increment) {
        return toBuilder()
                .goalkeeping(Math.min(20, Math.max(0, goalkeeping + increment)))
                .build();
    }
}