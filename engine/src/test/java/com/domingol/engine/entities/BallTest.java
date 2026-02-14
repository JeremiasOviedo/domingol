package com.domingol.engine.entities;

import com.domingol.engine.spatial.Vector2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Ball")
class BallTest {

    private Team testTeam;
    private PlayerStats testStats;

    @BeforeEach
    void setUp() {
        testTeam = Team.builder()
                .id("team1")
                .name("Test Team")
                .players(List.of())
                .build();
        testStats = PlayerStats.builder()
                .pace(15).stamina(15).shooting(15)
                .passing(15).tackling(15).positioning(15)
                .goalkeeping(0).build();
    }

    @Nested
    @DisplayName("Creation")
    class CreationTests {

        @Test
        @DisplayName("Should create ball with position and velocity")
        void createWithPositionAndVelocity() {
            Ball ball = new Ball(new Vector2D(52.5, 34), new Vector2D(2, 0));

            assertThat(ball.getPosition()).isEqualTo(new Vector2D(52.5, 34));
            assertThat(ball.getVelocity()).isEqualTo(new Vector2D(2, 0));
            assertThat(ball.getPossessor()).isNull();
            assertThat(ball.isLoose()).isTrue();
        }

        @Test
        @DisplayName("Should create stationary ball")
        void createStationary() {
            Ball ball = new Ball(new Vector2D(52.5, 34));

            assertThat(ball.getPosition()).isEqualTo(new Vector2D(52.5, 34));
            assertThat(ball.getVelocity()).isEqualTo(Vector2D.ZERO);
            assertThat(ball.isLoose()).isTrue();
        }

        @Test
        @DisplayName("Should clamp velocity to max speed on creation")
        void clampVelocityOnCreation() {
            // Create ball with excessive velocity (50 m/s)
            Ball ball = new Ball(new Vector2D(50, 34), new Vector2D(50, 0));

            // Should be clamped to MAX_BALL_SPEED (30 m/s)
            assertThat(ball.getVelocity().magnitude()).isCloseTo(Ball.MAX_BALL_SPEED, within(0.01));
        }

        @Test
        @DisplayName("Should reject null position")
        void rejectNullPosition() {
            assertThatThrownBy(() ->
                    new Ball(null, Vector2D.ZERO)
            ).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should reject null velocity")
        void rejectNullVelocity() {
            assertThatThrownBy(() ->
                    new Ball(new Vector2D(50, 34), null)
            ).isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("Physics - Loose Ball")
    class LooseBallPhysicsTests {

        @Test
        @DisplayName("Should update position based on velocity")
        void updatePosition() {
            Ball ball = new Ball(
                    new Vector2D(50, 30),
                    new Vector2D(4, 0)  // Moving right at 4 m/s
            );

            ball.updatePhysics(0.5);

            // With friction applied first:
            // velocity = 4 * FRICTION^0.5 = 4 * 0.9487 ≈ 3.795 m/s
            // movement = 3.795 * 0.5 ≈ 1.897 m
            // New position: (50, 30) + (1.897, 0) ≈ (51.90, 30)
            assertThat(ball.getPosition().x).isCloseTo(51.90, within(0.01));
            assertThat(ball.getPosition().y).isCloseTo(30.0, within(0.01));
        }

        @Test
        @DisplayName("Should apply friction to velocity")
        void applyFriction() {
            Ball ball = new Ball(
                    new Vector2D(50, 30),
                    new Vector2D(10, 0)  // 10 m/s
            );

            ball.updatePhysics(1.0);  // 1 second

            // Velocity after 1 second with FRICTION=0.9:
            // velocity = 10 * 0.9^1.0 = 9.0 m/s
            assertThat(ball.getVelocity().magnitude()).isCloseTo(9.0, within(0.01));
        }

        @Test
        @DisplayName("Should slow down over multiple ticks")
        void slowDownOverTime() {
            Ball ball = new Ball(
                    new Vector2D(50, 30),
                    new Vector2D(10, 0)
            );

            double initialSpeed = ball.getVelocity().magnitude();

            // Simulate 5 ticks (2.5 seconds)
            for (int i = 0; i < 5; i++) {
                ball.updatePhysics(0.5);
            }

            double finalSpeed = ball.getVelocity().magnitude();

            // Ball should have slowed down
            assertThat(finalSpeed).isLessThan(initialSpeed);

            // After 2.5 seconds with FRICTION=0.9:
            // velocity = 10 * 0.9^2.5 ≈ 7.74 m/s
            assertThat(finalSpeed).isCloseTo(7.74, within(0.1));
        }

        @Test
        @DisplayName("Should stop when velocity drops below minimum")
        void stopWhenVelocityTooSmall() {
            Ball ball = new Ball(
                    new Vector2D(50, 30),
                    new Vector2D(0.05, 0)  // Very slow
            );

            ball.updatePhysics(0.5);

            // Velocity below MIN_VELOCITY (0.1) should become zero
            assertThat(ball.getVelocity()).isEqualTo(Vector2D.ZERO);
        }

        @Test
        @DisplayName("Should handle diagonal movement")
        void diagonalMovement() {
            Ball ball = new Ball(
                    new Vector2D(50, 30),
                    new Vector2D(6, 8)  // Diagonal velocity
            );

            ball.updatePhysics(1.0);

            // New position: (50, 30) + (6, 8) * 1.0 = (56, 38)
            // But with friction: velocity *= 0.9
            // So actual movement is (6*0.9, 8*0.9) = (5.4, 7.2)
            // Position: (50, 30) + (5.4, 7.2) ≈ (55.4, 37.2)

            assertThat(ball.getPosition().x).isCloseTo(55.4, within(0.1));
            assertThat(ball.getPosition().y).isCloseTo(37.2, within(0.1));
        }
    }

    @Nested
    @DisplayName("Physics - Possessed Ball")
    class PossessedBallPhysicsTests {

        @Test
        @DisplayName("Should follow player position when possessed")
        void followPlayerPosition() {
            Player player = new Player(
                    "p1", "Kane", testStats, Position.ST,
                    new Vector2D(50, 30), Vector2D.ZERO, testTeam
            );

            Ball ball = new Ball(new Vector2D(10, 10), Vector2D.ZERO);
            ball.setPossessor(player);

            ball.updatePhysics(0.5);

            // Ball should be at player's position
            assertThat(ball.getPosition()).isEqualTo(player.getFieldPosition());
        }

        @Test
        @DisplayName("Should match player velocity when possessed")
        void matchPlayerVelocity() {
            Player player = new Player(
                    "p1", "Kane", testStats, Position.ST,
                    new Vector2D(50, 30),
                    new Vector2D(3, 2),  // Player moving
                    testTeam
            );

            Ball ball = new Ball(new Vector2D(50, 30), Vector2D.ZERO);
            ball.setPossessor(player);

            ball.updatePhysics(0.5);

            // Ball velocity should match player
            assertThat(ball.getVelocity()).isEqualTo(player.getVelocity());
        }

        @Test
        @DisplayName("Should not apply friction when possessed")
        void noFrictionWhenPossessed() {
            Player player = new Player(
                    "p1", "Kane", testStats, Position.ST,
                    new Vector2D(50, 30),
                    new Vector2D(10, 0),
                    testTeam
            );

            Ball ball = new Ball(new Vector2D(50, 30), Vector2D.ZERO);
            ball.setPossessor(player);

            ball.updatePhysics(1.0);

            // Ball velocity should still be 10 (no friction applied)
            assertThat(ball.getVelocity().magnitude()).isCloseTo(10.0, within(0.01));
        }

        @Test
        @DisplayName("Should track moving player")
        void trackMovingPlayer() {
            Player player = new Player(
                    "p1", "Kane", testStats, Position.ST,
                    new Vector2D(50, 30),
                    new Vector2D(2, 0),
                    testTeam
            );

            Ball ball = new Ball(new Vector2D(50, 30), Vector2D.ZERO);
            ball.setPossessor(player);

            // Player moves
            player.updatePhysics(0.5);  // Player now at (51, 30)
            ball.updatePhysics(0.5);     // Ball should follow

            assertThat(ball.getPosition()).isEqualTo(new Vector2D(51, 30));
        }
    }

    @Nested
    @DisplayName("Kicking")
    class KickingTests {

        @Test
        @DisplayName("Should kick ball in specified direction")
        void kickInDirection() {
            Ball ball = new Ball(new Vector2D(50, 30), Vector2D.ZERO);

            Vector2D direction = new Vector2D(1, 0);  // Right
            ball.kick(direction, 0.5);  // 50% power

            // Velocity: direction.normalize() * (0.5 * MAX_BALL_SPEED)
            // = (1, 0) * (0.5 * 30) = (15, 0)
            assertThat(ball.getVelocity()).isEqualTo(new Vector2D(15, 0));
            assertThat(ball.getPossessor()).isNull();  // Released
        }

        @Test
        @DisplayName("Should normalize kick direction")
        void normalizeKickDirection() {
            Ball ball = new Ball(new Vector2D(50, 30), Vector2D.ZERO);

            // Non-normalized direction
            Vector2D direction = new Vector2D(10, 0);  // Length 10, not 1
            ball.kick(direction, 1.0);  // Full power

            // Should normalize to (1, 0) then multiply by MAX_BALL_SPEED
            assertThat(ball.getVelocity().magnitude()).isCloseTo(Ball.MAX_BALL_SPEED, within(0.01));
        }

        @Test
        @DisplayName("Should kick with different power levels")
        void kickWithDifferentPowers() {
            Ball softKick = new Ball(new Vector2D(50, 30), Vector2D.ZERO);
            Ball hardKick = new Ball(new Vector2D(50, 30), Vector2D.ZERO);

            Vector2D direction = new Vector2D(1, 0);

            softKick.kick(direction, 0.2);  // 20% power
            hardKick.kick(direction, 1.0);  // 100% power

            assertThat(softKick.getVelocity().magnitude()).isCloseTo(6.0, within(0.01));  // 0.2 * 30
            assertThat(hardKick.getVelocity().magnitude()).isCloseTo(30.0, within(0.01)); // 1.0 * 30
        }

        @Test
        @DisplayName("Should kick towards target position")
        void kickTowardsTarget() {
            Ball ball = new Ball(new Vector2D(50, 30), Vector2D.ZERO);

            Vector2D target = new Vector2D(70, 30);  // 20m to the right
            ball.kickTowards(target, 0.5);

            // Direction: (70, 30) - (50, 30) = (20, 0) → normalized to (1, 0)
            // Velocity: (1, 0) * (0.5 * 30) = (15, 0)
            assertThat(ball.getVelocity()).isEqualTo(new Vector2D(15, 0));
        }

        @Test
        @DisplayName("Should release possessor when kicked")
        void releasePossessorWhenKicked() {
            Player player = new Player(
                    "p1", "Kane", testStats, Position.ST,
                    new Vector2D(50, 30), Vector2D.ZERO, testTeam
            );

            Ball ball = new Ball(new Vector2D(50, 30), Vector2D.ZERO);
            ball.setPossessor(player);

            assertThat(ball.isPossessed()).isTrue();

            ball.kick(new Vector2D(1, 0), 0.5);

            assertThat(ball.isLoose()).isTrue();
            assertThat(ball.getPossessor()).isNull();
        }
    }

    @Nested
    @DisplayName("Boundary Clamping")
    class BoundaryTests {

        @Test
        @DisplayName("Should clamp ball to left boundary")
        void clampToLeftBoundary() {
            Ball ball = new Ball(
                    new Vector2D(2, 34),
                    new Vector2D(-10, 0)  // Moving left fast
            );

            ball.updatePhysics(1.0);

            // Would go to x = -8, but clamped to 0
            assertThat(ball.getPosition().x).isEqualTo(0.0);
        }

        @Test
        @DisplayName("Should clamp ball to right boundary")
        void clampToRightBoundary() {
            Ball ball = new Ball(
                    new Vector2D(103, 34),
                    new Vector2D(10, 0)  // Moving right fast
            );

            ball.updatePhysics(1.0);

            // Would go to x = 113, but clamped to 105
            assertThat(ball.getPosition().x).isEqualTo(105.0);
        }

        @Test
        @DisplayName("Should clamp ball to bottom boundary")
        void clampToBottomBoundary() {
            Ball ball = new Ball(
                    new Vector2D(50, 2),
                    new Vector2D(0, -10)  // Moving down fast
            );

            ball.updatePhysics(1.0);

            // Would go to y = -8, but clamped to 0
            assertThat(ball.getPosition().y).isEqualTo(0.0);
        }

        @Test
        @DisplayName("Should clamp ball to top boundary")
        void clampToTopBoundary() {
            Ball ball = new Ball(
                    new Vector2D(50, 66),
                    new Vector2D(0, 10)  // Moving up fast
            );

            ball.updatePhysics(1.0);

            // Would go to y = 76, but clamped to 68
            assertThat(ball.getPosition().y).isEqualTo(68.0);
        }

        @Test
        @DisplayName("Should not clamp ball inside field")
        void noClampInsideField() {
            Ball ball = new Ball(
                    new Vector2D(50, 34),
                    new Vector2D(2, 1)
            );

            ball.updatePhysics(1.0);

            // Ball should move freely inside field
            assertThat(ball.getPosition().x).isGreaterThan(0.0).isLessThan(105.0);
            assertThat(ball.getPosition().y).isGreaterThan(0.0).isLessThan(68.0);
        }
    }

    @Nested
    @DisplayName("Possession State")
    class PossessionStateTests {

        @Test
        @DisplayName("Should correctly report possession state")
        void reportPossessionState() {
            Ball ball = new Ball(new Vector2D(50, 30), Vector2D.ZERO);

            assertThat(ball.isLoose()).isTrue();
            assertThat(ball.isPossessed()).isFalse();

            Player player = new Player(
                    "p1", "Kane", testStats, Position.ST,
                    new Vector2D(50, 30), Vector2D.ZERO, testTeam
            );

            ball.setPossessor(player);

            assertThat(ball.isLoose()).isFalse();
            assertThat(ball.isPossessed()).isTrue();
        }

        @Test
        @DisplayName("Should allow changing possessor")
        void changePossessor() {
            Player p1 = new Player(
                    "p1", "Kane", testStats, Position.ST,
                    new Vector2D(50, 30), Vector2D.ZERO, testTeam
            );

            Player p2 = new Player(
                    "p2", "Son", testStats, Position.ST,
                    new Vector2D(52, 30), Vector2D.ZERO, testTeam
            );

            Ball ball = new Ball(new Vector2D(50, 30), Vector2D.ZERO);

            ball.setPossessor(p1);
            assertThat(ball.getPossessor()).isEqualTo(p1);

            ball.setPossessor(p2);
            assertThat(ball.getPossessor()).isEqualTo(p2);
        }

        @Test
        @DisplayName("Should allow releasing possession")
        void releasePossession() {
            Player player = new Player(
                    "p1", "Kane", testStats, Position.ST,
                    new Vector2D(50, 30), Vector2D.ZERO, testTeam
            );

            Ball ball = new Ball(new Vector2D(50, 30), Vector2D.ZERO);
            ball.setPossessor(player);

            ball.setPossessor(null);

            assertThat(ball.isLoose()).isTrue();
        }
    }

    @Nested
    @DisplayName("Distance Calculations")
    class DistanceCalculationTests {

        @Test
        @DisplayName("Should calculate distance to point")
        void distanceToPoint() {
            Ball ball = new Ball(new Vector2D(50, 30), Vector2D.ZERO);

            Vector2D point = new Vector2D(50, 40);

            assertThat(ball.distanceTo(point)).isCloseTo(10.0, within(0.01));
        }

        @Test
        @DisplayName("Should calculate distance to player")
        void distanceToPlayer() {
            Ball ball = new Ball(new Vector2D(50, 30), Vector2D.ZERO);

            Player player = new Player(
                    "p1", "Kane", testStats, Position.ST,
                    new Vector2D(53, 34), Vector2D.ZERO, testTeam
            );

            // Distance: sqrt((53-50)^2 + (34-30)^2) = sqrt(9 + 16) = 5
            assertThat(ball.distanceTo(player)).isCloseTo(5.0, within(0.01));
        }
    }

    @Nested
    @DisplayName("Constants")
    class ConstantsTests {

        @Test
        @DisplayName("Should have reasonable max speed")
        void maxSpeedReasonable() {
            // 30 m/s = 108 km/h (realistic for hard shots)
            assertThat(Ball.MAX_BALL_SPEED).isEqualTo(30.0);
        }

        @Test
        @DisplayName("Should have friction less than 1")
        void frictionLessThanOne() {
            // Friction should slow ball down (< 1.0)
            assertThat(Ball.FRICTION).isLessThan(1.0).isGreaterThan(0.0);
        }

        @Test
        @DisplayName("Should have positive minimum velocity")
        void minVelocityPositive() {
            assertThat(Ball.MIN_VELOCITY).isGreaterThan(0.0);
        }
    }
}