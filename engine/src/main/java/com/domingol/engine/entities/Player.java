package com.domingol.engine.entities;

import com.domingol.engine.spatial.Vector2D;
import lombok.Data;
import lombok.NonNull;

/**
 * Represents a football player in the simulation.
 * <p>
 * A player has both permanent attributes (stats, position) and dynamic match state
 * (field position, velocity) that changes during simulation.
 * <p>
 * <b>Design decisions:</b>
 * <ul>
 *   <li>Stats are immutable during a match (no fatigue modifying base stats)</li>
 *   <li>Field position and velocity are mutable (updated each tick)</li>
 *   <li>Ball possession is managed by Ball class (single source of truth)</li>
 *   <li>Team reference is bidirectional (Player knows Team, Team has Players)</li>
 * </ul>
 *
 * <h3>Usage in Simulation:</h3>
 * <pre>
 * // Create player
 * PlayerStats stats = PlayerStats.builder()...build();
 * Player striker = new Player("p1", "Kane", stats, Position.ST,
 *                             new Vector2D(50, 34), Vector2D.ZERO, team);
 *
 * // During match tick
 * striker.setFieldPosition(newPosition);
 * striker.setVelocity(newVelocity);
 *
 * // AI decision making
 * if (ball.getPossessor() == striker) {
 *     PlayerAction action = striker.decideActionWithBall(...);
 * }
 * </pre>
 *
 * @see PlayerStats for player attributes
 * @see Position for player roles
 * @see Team for team management
 */
@Data
public class Player {

    // === IDENTITY ===

    /**
     * Unique identifier for the player.
     * <p>
     * Used for referencing in events, spatial grid, and game state.
     * Format: "p1", "p2", etc. for simplicity in MVP.
     */
    @NonNull
    private String id;

    /**
     * Player's display name.
     * <p>
     * Used in match events and reports.
     * Example: "Harry Kane", "Lionel Messi"
     */
    @NonNull
    private String name;

    // === ATTRIBUTES (immutable during match) ===

    /**
     * Player's base statistics.
     * <p>
     * These values do not change during a match. Modifiers (fatigue, pressure, etc.)
     * are applied when calculating effective stats in events, but base stats remain constant.
     * <p>
     * Training and development (future) will modify these between matches.
     */
    @NonNull
    private PlayerStats stats;

    /**
     * Player's position/role on the field.
     * <p>
     * Indicates where the player should play (GK, CB, ST, etc.).
     * <p>
     * <b>MVP:</b> Fixed for the match (player plays in assigned position).
     * <b>Phase 2/3:</b> Will support position familiarity system where playing
     * out of natural position applies performance penalties.
     */
    @NonNull
    private Position position;

    // === MATCH STATE (mutable during match) ===

    /**
     * Current position on the field in meters.
     * <p>
     * Coordinates are absolute:
     * <ul>
     *   <li>X: 0 to 105 (field length)</li>
     *   <li>Y: 0 to 68 (field width)</li>
     * </ul>
     * <p>
     * Updated every tick based on velocity and steering behaviors.
     * <p>
     * Note: Named "fieldPosition" to distinguish from "position" (the role enum).
     */
    @NonNull
    private Vector2D fieldPosition;

    /**
     * Current velocity in meters per second.
     * <p>
     * Magnitude should not exceed {@link #getMaxSpeed()}.
     * Direction indicates where player is moving.
     * <p>
     * Updated by steering behaviors (Phase 2).
     */
    @NonNull
    private Vector2D velocity;

    // === RELATIONSHIPS ===

    /**
     * Team this player belongs to.
     * <p>
     * Bidirectional relationship: Player knows Team, Team has List of Players.
     * <p>
     * Used for:
     * <ul>
     *   <li>Identifying teammates vs opponents in AI decisions</li>
     *   <li>Team tactics and formation management</li>
     *   <li>Match events and statistics</li>
     * </ul>
     * <p>
     * Note: Can be null temporarily during construction to solve circular dependency.
     * Must be set before match simulation starts.
     */
    private Team team;

    // === CONSTRUCTOR ===

    /**
     * Create a player with all attributes including team.
     * <p>
     * Team can be null temporarily during construction to solve circular dependency
     * (Team needs Players, Player needs Team). Use {@link #setTeam(Team)} to set it after.
     *
     * @param id unique player identifier
     * @param name player display name
     * @param stats player base statistics
     * @param position player's role on field
     * @param fieldPosition initial position on field
     * @param velocity initial velocity
     * @param team team this player belongs to (can be null temporarily)
     */
    public Player(@NonNull String id, @NonNull String name, @NonNull PlayerStats stats,
                  @NonNull Position position, @NonNull Vector2D fieldPosition,
                  @NonNull Vector2D velocity, Team team) {
        this.id = id;
        this.name = name;
        this.stats = stats;
        this.position = position;
        this.fieldPosition = fieldPosition;
        this.velocity = velocity;
        this.team = team;
    }

    // === DERIVED PROPERTIES ===

    /**
     * Calculate maximum movement speed based on pace stat.
     * <p>
     * Formula: 5.0 + (pace / 20.0) * 5.0
     * <ul>
     *   <li>pace = 1  → 5.25 m/s</li>
     *   <li>pace = 10 → 7.5 m/s</li>
     *   <li>pace = 20 → 10.0 m/s</li>
     * </ul>
     * <p>
     * Used by steering behaviors to cap velocity magnitude.
     *
     * @return max speed in meters per second
     */
    public double getMaxSpeed() {
        return stats.calculateMaxSpeed();
    }

    /**
     * Calculate distance to another player.
     * <p>
     * Convenience method for AI and event calculations.
     *
     * @param other target player
     * @return distance in meters
     */
    public double distanceTo(Player other) {
        return fieldPosition.distanceTo(other.fieldPosition);
    }

    /**
     * Calculate distance to a point on the field.
     *
     * @param point target position
     * @return distance in meters
     */
    public double distanceTo(Vector2D point) {
        return fieldPosition.distanceTo(point);
    }

    /**
     * Check if this player is a goalkeeper.
     * <p>
     * Convenience method to avoid repeated position checks.
     *
     * @return true if position is GK
     */
    public boolean isGoalkeeper() {
        return position.isGoalkeeper();
    }

    // === AI METHODS (Phase 3 - Placeholders for now) ===

    /**
     * AI: Decide what action to take when player has the ball.
     * <p>
     * Called by simulation engine when {@code ball.getPossessor() == this}.
     * <p>
     * <b>Phase 3 implementation will evaluate:</b>
     * <ul>
     *   <li>Pass: Find best pass target (open teammate, good angle)</li>
     *   <li>Shoot: Check if in shooting range and angle</li>
     *   <li>Dribble: Move with ball towards goal or space</li>
     * </ul>
     *
     * @param ball current ball state
     * @param teammates list of teammates (for passing options)
     * @param opponents list of opponents (for pressure evaluation)
     * @return decided action (Pass, Shoot, Dribble, Hold)
     */
    public PlayerAction decideActionWithBall(Ball ball,
                                             java.util.List<Player> teammates,
                                             java.util.List<Player> opponents) {
        // TODO: Implement in Phase 3 (AI & Decision Making)
        // For now, placeholder
        throw new UnsupportedOperationException("AI not implemented yet (Phase 3)");
    }

    /**
     * AI: Update positioning when player does not have the ball.
     * <p>
     * Called by simulation engine for players where {@code ball.getPossessor() != this}.
     * <p>
     * <b>Phase 2/3 implementation will handle:</b>
     * <ul>
     *   <li>If teammate has ball: Move to support position, create passing lanes</li>
     *   <li>If opponent has ball: Mark opponent, close down space, intercept</li>
     *   <li>If ball is loose: Move towards ball to gain possession</li>
     * </ul>
     * <p>
     * Uses steering behaviors (Seek, Arrive, Interpose) to update velocity.
     *
     * @param ball current ball state
     * @param possessor player who has the ball (null if loose)
     */
    public void updatePositioningWithoutBall(Ball ball, Player possessor) {
        // TODO: Implement in Phase 2 (Steering) and Phase 3 (AI)
        // For now, placeholder
        // Will use steering behaviors to move towards tactical position
    }

    /**
     * Update player's physical state for one simulation tick.
     * <p>
     * Integrates velocity into position: {@code position += velocity * deltaTime}
     * <p>
     * Called every tick by simulation engine.
     *
     * @param deltaTime time step in seconds (typically 0.5s for 2 ticks/second)
     */
    public void updatePhysics(double deltaTime) {
        // Update position based on current velocity
        fieldPosition = fieldPosition.add(velocity.multiply(deltaTime));

        // TODO Phase 2: Add boundary checks (keep player on field)
        // TODO Phase 2: Apply friction/deceleration if needed
    }
}