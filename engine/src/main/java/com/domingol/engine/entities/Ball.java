package com.domingol.engine.entities;

import com.domingol.engine.spatial.Vector2D;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Represents the football/ball in the simulation.
 * <p>
 * The ball has physical properties (position, velocity) and game state (possessor).
 * It experiences friction when moving and can be possessed by a player or be loose.
 * <p>
 * <b>Design decisions:</b>
 * <ul>
 *   <li>Ball is the single source of truth for possession (not duplicated in Player)</li>
 *   <li>Friction applies when ball is loose (slows down over time)</li>
 *   <li>No friction when possessed (player controls ball velocity)</li>
 *   <li>Velocity clamped to max ball speed (realistic physics)</li>
 * </ul>
 *
 * <h3>Physics Model:</h3>
 * <pre>
 * When loose:
 *   position += velocity * deltaTime
 *   velocity *= frictionFactor^deltaTime  (exponential decay)
 *
 * When possessed:
 *   position = possessor.fieldPosition  (ball follows player)
 *   velocity = possessor.velocity       (matches player movement)
 * </pre>
 *
 * <h3>Usage:</h3>
 * <pre>
 * // Create ball at center
 * Ball ball = new Ball(new Vector2D(52.5, 34), Vector2D.ZERO);
 *
 * // During simulation tick
 * ball.updatePhysics(0.5);  // Applies friction if loose
 *
 * // Player gains possession
 * ball.setPossessor(player);
 *
 * // Kick ball (pass/shot)
 * ball.kick(direction, power);
 * ball.setPossessor(null);  // Now loose
 * </pre>
 *
 * @see Player for possession management
 */
@Getter
@Setter
public class Ball {

    // === CONSTANTS ===

    /**
     * Maximum ball speed in meters per second.
     * <p>
     * Based on professional football:
     * - Average pass: 15-20 m/s
     * - Hard shot: 25-30 m/s
     * - Record shots: 50+ m/s (very rare)
     * <p>
     * We cap at 30 m/s for realistic gameplay (108 km/h).
     */
    public static final double MAX_BALL_SPEED = 30.0;

    /**
     * Friction factor applied per second when ball is loose.
     * <p>
     * Formula: velocity *= FRICTION^deltaTime
     * <p>
     * With FRICTION = 0.9:
     * - After 1 second: velocity = 90% of original
     * - After 2 seconds: velocity = 81% of original
     * - After 5 seconds: velocity = 59% of original
     * <p>
     * Ball slows down gradually but noticeably.
     */
    public static final double FRICTION = 0.9;

    /**
     * Minimum velocity magnitude before ball is considered stopped.
     * <p>
     * When velocity drops below this threshold, set to zero.
     * Prevents infinitesimal velocities from accumulating.
     */
    public static final double MIN_VELOCITY = 0.1;

    // === STATE ===

    /**
     * Current position on the field in meters.
     * <p>
     * Coordinates are absolute:
     * <ul>
     *   <li>X: 0 to 105 (field length)</li>
     *   <li>Y: 0 to 68 (field width)</li>
     * </ul>
     */
    @NonNull
    private Vector2D position;

    /**
     * Current velocity in meters per second.
     * <p>
     * Magnitude should not exceed {@link #MAX_BALL_SPEED}.
     * Direction indicates where ball is moving.
     */
    @NonNull
    private Vector2D velocity;

    /**
     * Player who currently possesses the ball.
     * <p>
     * null if ball is loose (no one has it).
     * <p>
     * This is the single source of truth for possession.
     * To check if a player has the ball: {@code ball.getPossessor() == player}
     */
    private Player possessor;  // Can be null

    // === CONSTRUCTORS ===

    /**
     * Create ball at specified position with initial velocity.
     *
     * @param position initial position on field
     * @param velocity initial velocity (e.g., Vector2D.ZERO for stationary)
     */
    public Ball(@NonNull Vector2D position, @NonNull Vector2D velocity) {
        this.position = position;
        this.velocity = clampVelocity(velocity);
        this.possessor = null;
    }

    /**
     * Create stationary ball at specified position.
     *
     * @param position initial position on field
     */
    public Ball(@NonNull Vector2D position) {
        this(position, Vector2D.ZERO);
    }

    // === PHYSICS ===

    /**
     * Update ball physics for one simulation tick.
     * <p>
     * <b>If possessed:</b> Ball follows player (position and velocity match player).
     * <b>If loose:</b> Ball continues moving, friction slows it down.
     * <p>
     * Called every tick by simulation engine.
     *
     * @param deltaTime time step in seconds (typically 0.5s for 2 ticks/second)
     */
    public void updatePhysics(double deltaTime) {
        if (possessor != null) {
            // Ball is possessed - follows player
            position = possessor.getFieldPosition();
            velocity = possessor.getVelocity();
        } else {
            // Ball is loose - apply physics

            // Apply friction FIRST (affects current movement)
            double frictionFactor = Math.pow(FRICTION, deltaTime);
            velocity = velocity.multiply(frictionFactor);

            // Stop ball if velocity too small
            if (velocity.magnitude() < MIN_VELOCITY) {
                velocity = Vector2D.ZERO;
            }

            // Update position based on friction-reduced velocity
            position = position.add(velocity.multiply(deltaTime));

            // Clamp to field boundaries
            position = clampToField(position);
        }
    }

    /**
     * Kick the ball in a direction with specified power.
     * <p>
     * Used for passes and shots. Ball becomes loose after kick.
     * <p>
     * Power determines initial velocity magnitude:
     * <ul>
     *   <li>Power 0.5 → 15 m/s (soft pass)</li>
     *   <li>Power 1.0 → 30 m/s (hard shot)</li>
     * </ul>
     *
     * @param direction unit vector indicating kick direction
     * @param power kick power (0.0 to 1.0)
     */
    public void kick(Vector2D direction, double power) {
        // Calculate velocity from direction and power
        double speed = power * MAX_BALL_SPEED;
        Vector2D kickVelocity = direction.normalize().multiply(speed);

        // Set ball velocity and release from possessor
        this.velocity = clampVelocity(kickVelocity);
        this.possessor = null;
    }

    /**
     * Kick ball towards a target position with specified power.
     * <p>
     * Convenience method that calculates direction automatically.
     *
     * @param target target position to kick towards
     * @param power kick power (0.0 to 1.0)
     */
    public void kickTowards(Vector2D target, double power) {
        Vector2D direction = target.subtract(position);
        kick(direction, power);
    }

    // === HELPER METHODS ===

    /**
     * Check if ball is currently loose (no possessor).
     *
     * @return true if ball is loose
     */
    public boolean isLoose() {
        return possessor == null;
    }

    /**
     * Check if ball is currently possessed by a player.
     *
     * @return true if ball is possessed
     */
    public boolean isPossessed() {
        return possessor != null;
    }

    /**
     * Calculate distance from ball to a point.
     *
     * @param point target position
     * @return distance in meters
     */
    public double distanceTo(Vector2D point) {
        return position.distanceTo(point);
    }

    /**
     * Calculate distance from ball to a player.
     *
     * @param player target player
     * @return distance in meters
     */
    public double distanceTo(Player player) {
        return position.distanceTo(player.getFieldPosition());
    }

    // === PRIVATE HELPERS ===

    /**
     * Clamp velocity to maximum ball speed.
     *
     * @param velocity unclamped velocity
     * @return velocity with magnitude <= MAX_BALL_SPEED
     */
    private Vector2D clampVelocity(Vector2D velocity) {
        double magnitude = velocity.magnitude();
        if (magnitude > MAX_BALL_SPEED) {
            return velocity.normalize().multiply(MAX_BALL_SPEED);
        }
        return velocity;
    }

    /**
     * Clamp position to field boundaries.
     * <p>
     * Field dimensions: 105m x 68m
     * <p>
     * If ball goes out of bounds, it's clamped to the edge.
     * (Future: Generate out-of-bounds events for throw-ins, corners, etc.)
     *
     * @param position unclamped position
     * @return position within field boundaries
     */
    private Vector2D clampToField(Vector2D position) {
        double x = Math.max(0, Math.min(105, position.x));
        double y = Math.max(0, Math.min(68, position.y));
        return new Vector2D(x, y);
    }
}