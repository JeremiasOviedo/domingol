package com.domingol.engine.entities;

import com.domingol.engine.spatial.Vector2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Player")
class PlayerTest {

    private PlayerStats strikerStats;
    private PlayerStats goalkeeperStats;
    private Team teamA;

    @BeforeEach
    void setUp() {
        // Create test stats
        strikerStats = PlayerStats.builder()
                .pace(17)
                .stamina(14)
                .shooting(18)
                .passing(12)
                .tackling(6)
                .positioning(15)
                .goalkeeping(0)
                .build();

        goalkeeperStats = PlayerStats.builder()
                .pace(8)
                .stamina(12)
                .shooting(3)
                .passing(10)
                .tackling(12)
                .positioning(16)
                .goalkeeping(18)
                .build();

        // Create test team
        teamA = Team.builder()
                .id("team1")
                .name("Manchester City")
                .players(List.of())
                .build();
    }

    @Nested
    @DisplayName("Creation and Validation")
    class CreationTests {

        @Test
        @DisplayName("Should create valid outfield player")
        void createValidOutfieldPlayer() {
            Player striker = new Player(
                    "p1",
                    "Harry Kane",
                    strikerStats,
                    Position.ST,
                    new Vector2D(52.5, 34),
                    Vector2D.ZERO,
                    teamA
            );

            assertThat(striker.getId()).isEqualTo("p1");
            assertThat(striker.getName()).isEqualTo("Harry Kane");
            assertThat(striker.getStats()).isEqualTo(strikerStats);
            assertThat(striker.getPosition()).isEqualTo(Position.ST);
            assertThat(striker.getFieldPosition()).isEqualTo(new Vector2D(52.5, 34));
            assertThat(striker.getVelocity()).isEqualTo(Vector2D.ZERO);
            assertThat(striker.getTeam()).isEqualTo(teamA);
        }

        @Test
        @DisplayName("Should create valid goalkeeper")
        void createValidGoalkeeper() {
            Player gk = new Player(
                    "p1",
                    "Ederson",
                    goalkeeperStats,
                    Position.GK,
                    new Vector2D(5, 34),
                    Vector2D.ZERO,
                    teamA
            );

            assertThat(gk.getId()).isEqualTo("p1");
            assertThat(gk.getPosition()).isEqualTo(Position.GK);
            assertThat(gk.isGoalkeeper()).isTrue();
        }

        @Test
        @DisplayName("Should reject null id")
        void rejectNullId() {
            assertThatThrownBy(() ->
                    new Player(
                            null,
                            "Kane",
                            strikerStats,
                            Position.ST,
                            new Vector2D(52.5, 34),
                            Vector2D.ZERO,
                            teamA
                    )
            ).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should reject null name")
        void rejectNullName() {
            assertThatThrownBy(() ->
                    new Player(
                            "p1",
                            null,
                            strikerStats,
                            Position.ST,
                            new Vector2D(52.5, 34),
                            Vector2D.ZERO,
                            teamA
                    )
            ).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should reject null stats")
        void rejectNullStats() {
            assertThatThrownBy(() ->
                    new Player(
                            "p1",
                            "Kane",
                            null,
                            Position.ST,
                            new Vector2D(52.5, 34),
                            Vector2D.ZERO,
                            teamA
                    )
            ).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should reject null position")
        void rejectNullPosition() {
            assertThatThrownBy(() ->
                    new Player(
                            "p1",
                            "Kane",
                            strikerStats,
                            null,
                            new Vector2D(52.5, 34),
                            Vector2D.ZERO,
                            teamA
                    )
            ).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should reject null field position")
        void rejectNullFieldPosition() {
            assertThatThrownBy(() ->
                    new Player(
                            "p1",
                            "Kane",
                            strikerStats,
                            Position.ST,
                            null,
                            Vector2D.ZERO,
                            teamA
                    )
            ).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should reject null velocity")
        void rejectNullVelocity() {
            assertThatThrownBy(() ->
                    new Player(
                            "p1",
                            "Kane",
                            strikerStats,
                            Position.ST,
                            new Vector2D(52.5, 34),
                            null,
                            teamA
                    )
            ).isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("Max Speed Calculation")
    class MaxSpeedTests {

        @Test
        @DisplayName("Should calculate max speed from pace stat")
        void calculateMaxSpeed() {
            Player fastPlayer = new Player(
                    "p1", "Mbappe", strikerStats, Position.ST,
                    new Vector2D(50, 34), Vector2D.ZERO, teamA
            );

            Player slowPlayer = new Player(
                    "p2", "Ederson", goalkeeperStats, Position.GK,
                    new Vector2D(5, 34), Vector2D.ZERO, teamA
            );

            // Striker with pace=17: 5.0 + (17/20)*5.0 = 9.25 m/s
            assertThat(fastPlayer.getMaxSpeed()).isCloseTo(9.25, within(0.01));

            // GK with pace=8: 5.0 + (8/20)*5.0 = 7.0 m/s
            assertThat(slowPlayer.getMaxSpeed()).isCloseTo(7.0, within(0.01));
        }
    }

    @Nested
    @DisplayName("Distance Calculations")
    class DistanceTests {

        @Test
        @DisplayName("Should calculate distance to another player")
        void distanceToPlayer() {
            Player p1 = new Player(
                    "p1", "Kane", strikerStats, Position.ST,
                    new Vector2D(50, 30), Vector2D.ZERO, teamA
            );

            Player p2 = new Player(
                    "p2", "Son", strikerStats, Position.ST,
                    new Vector2D(50, 38), Vector2D.ZERO, teamA
            );

            // Vertical distance: 8 meters
            assertThat(p1.distanceTo(p2)).isCloseTo(8.0, within(0.01));
        }

        @Test
        @DisplayName("Should calculate distance to a point")
        void distanceToPoint() {
            Player player = new Player(
                    "p1", "Kane", strikerStats, Position.ST,
                    new Vector2D(50, 30), Vector2D.ZERO, teamA
            );

            Vector2D target = new Vector2D(50, 40);

            assertThat(player.distanceTo(target)).isCloseTo(10.0, within(0.01));
        }

        @Test
        @DisplayName("Should return zero distance to self position")
        void distanceToSelf() {
            Player player = new Player(
                    "p1", "Kane", strikerStats, Position.ST,
                    new Vector2D(50, 30), Vector2D.ZERO, teamA
            );

            assertThat(player.distanceTo(new Vector2D(50, 30))).isCloseTo(0.0, within(0.01));
        }
    }

    @Nested
    @DisplayName("Position Type Checks")
    class PositionTypeTests {

        @Test
        @DisplayName("Should identify goalkeeper")
        void identifyGoalkeeper() {
            Player gk = new Player(
                    "p1", "Ederson", goalkeeperStats, Position.GK,
                    new Vector2D(5, 34), Vector2D.ZERO, teamA
            );

            assertThat(gk.isGoalkeeper()).isTrue();
        }

        @Test
        @DisplayName("Should identify outfield player")
        void identifyOutfieldPlayer() {
            Player striker = new Player(
                    "p1", "Kane", strikerStats, Position.ST,
                    new Vector2D(50, 34), Vector2D.ZERO, teamA
            );

            assertThat(striker.isGoalkeeper()).isFalse();
        }
    }

    @Nested
    @DisplayName("Physics Update")
    class PhysicsUpdateTests {

        @Test
        @DisplayName("Should update position based on velocity")
        void updatePosition() {
            Player player = new Player(
                    "p1", "Kane", strikerStats, Position.ST,
                    new Vector2D(50, 30),
                    new Vector2D(2, 0),  // Moving right at 2 m/s
                    teamA
            );

            // Update with deltaTime = 0.5s
            player.updatePhysics(0.5);

            // New position: (50, 30) + (2, 0) * 0.5 = (51, 30)
            assertThat(player.getFieldPosition()).isEqualTo(new Vector2D(51, 30));
        }

        @Test
        @DisplayName("Should handle diagonal movement")
        void updatePositionDiagonal() {
            Player player = new Player(
                    "p1", "Kane", strikerStats, Position.ST,
                    new Vector2D(50, 30),
                    new Vector2D(4, 3),  // Moving diagonally
                    teamA
            );

            player.updatePhysics(1.0);

            // New position: (50, 30) + (4, 3) * 1.0 = (54, 33)
            assertThat(player.getFieldPosition()).isEqualTo(new Vector2D(54, 33));
        }

        @Test
        @DisplayName("Should not move if velocity is zero")
        void noMovementIfZeroVelocity() {
            Player player = new Player(
                    "p1", "Kane", strikerStats, Position.ST,
                    new Vector2D(50, 30),
                    Vector2D.ZERO,
                    teamA
            );

            player.updatePhysics(0.5);

            // Position unchanged
            assertThat(player.getFieldPosition()).isEqualTo(new Vector2D(50, 30));
        }

        @Test
        @DisplayName("Should accumulate movement over multiple ticks")
        void accumulateMovement() {
            Player player = new Player(
                    "p1", "Kane", strikerStats, Position.ST,
                    new Vector2D(50, 30),
                    new Vector2D(2, 0),
                    teamA
            );

            // Simulate 5 ticks
            for (int i = 0; i < 5; i++) {
                player.updatePhysics(0.5);
            }

            // Total movement: 2 m/s * 0.5s * 5 ticks = 5 meters
            // New position: (50, 30) + (5, 0) = (55, 30)
            assertThat(player.getFieldPosition()).isEqualTo(new Vector2D(55, 30));
        }
    }

    @Nested
    @DisplayName("Mutable State")
    class MutableStateTests {

        @Test
        @DisplayName("Should allow updating field position")
        void updateFieldPosition() {
            Player player = new Player(
                    "p1", "Kane", strikerStats, Position.ST,
                    new Vector2D(50, 30), Vector2D.ZERO, teamA
            );

            player.setFieldPosition(new Vector2D(60, 35));

            assertThat(player.getFieldPosition()).isEqualTo(new Vector2D(60, 35));
        }

        @Test
        @DisplayName("Should allow updating velocity")
        void updateVelocity() {
            Player player = new Player(
                    "p1", "Kane", strikerStats, Position.ST,
                    new Vector2D(50, 30), Vector2D.ZERO, teamA
            );

            player.setVelocity(new Vector2D(5, 3));

            assertThat(player.getVelocity()).isEqualTo(new Vector2D(5, 3));
        }

        @Test
        @DisplayName("Stats should remain immutable during match")
        void statsImmutable() {
            Player player = new Player(
                    "p1", "Kane", strikerStats, Position.ST,
                    new Vector2D(50, 30), Vector2D.ZERO, teamA
            );

            PlayerStats originalStats = player.getStats();

            // Stats reference should not change
            assertThat(player.getStats()).isSameAs(originalStats);

            // Stats values should not change (they're immutable)
            assertThat(player.getStats().getShooting()).isEqualTo(18);
        }
    }

    @Nested
    @DisplayName("Team Relationship")
    class TeamRelationshipTests {

        @Test
        @DisplayName("Should maintain reference to team")
        void maintainTeamReference() {
            Player player = new Player(
                    "p1", "Kane", strikerStats, Position.ST,
                    new Vector2D(50, 30), Vector2D.ZERO, teamA
            );

            assertThat(player.getTeam()).isEqualTo(teamA);
            assertThat(player.getTeam().getName()).isEqualTo("Manchester City");
        }

        @Test
        @DisplayName("Different players can belong to same team")
        void multiplePlayersPerTeam() {
            Player p1 = new Player(
                    "p1", "Kane", strikerStats, Position.ST,
                    new Vector2D(50, 30), Vector2D.ZERO, teamA
            );

            Player p2 = new Player(
                    "p2", "Son", strikerStats, Position.ST,
                    new Vector2D(50, 38), Vector2D.ZERO, teamA
            );

            assertThat(p1.getTeam()).isEqualTo(p2.getTeam());
        }

        @Test
        @DisplayName("Players can belong to different teams")
        void playersDifferentTeams() {
            Team teamB = Team.builder()
                    .id("team1")
                    .name("Test Team A")
                    .players(List.of())  // Lista vacía para tests simples
                    .build();

            Player p1 = new Player(
                    "p1", "Kane", strikerStats, Position.ST,
                    new Vector2D(50, 30), Vector2D.ZERO, teamA
            );

            Team teamA = Team.builder()
                    .id("team2")
                    .name("Test Team B")
                    .players(List.of())
                    .build();


            Player p2 = new Player(
                    "p2", "Saka", strikerStats, Position.RW,
                    new Vector2D(50, 38), Vector2D.ZERO, teamA
            );

            assertThat(p1.getTeam()).isNotEqualTo(p2.getTeam());
        }
    }
}
