package com.domingol.engine.entities;

import com.domingol.engine.spatial.Vector2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Team")
class TeamTest {

    private PlayerStatsGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new PlayerStatsGenerator(new Random(42));
    }

    /**
     * Helper: Create a valid 11-player squad with proper team reference.
     * Solves circular dependency by creating team in two steps.
     */
    private Team createValidTeam(String id, String name) {
        // Step 1: Create temporary squad with null team (will be set after)
        List<Player> squad = new ArrayList<>();

        squad.add(new Player("p1", "Ederson", generator.generateGoalkeeper(), Position.GK,
                new Vector2D(5, 34), Vector2D.ZERO, null));
        squad.add(new Player("p2", "Walker", generator.generateFullback(), Position.RB,
                new Vector2D(20, 10), Vector2D.ZERO, null));
        squad.add(new Player("p3", "Stones", generator.generateCenterBack(), Position.CB,
                new Vector2D(15, 25), Vector2D.ZERO, null));
        squad.add(new Player("p4", "Dias", generator.generateCenterBack(), Position.CB,
                new Vector2D(15, 43), Vector2D.ZERO, null));
        squad.add(new Player("p5", "Ake", generator.generateFullback(), Position.LB,
                new Vector2D(20, 58), Vector2D.ZERO, null));
        squad.add(new Player("p6", "Rodri", generator.generateDefensiveMidfielder(), Position.CDM,
                new Vector2D(35, 34), Vector2D.ZERO, null));
        squad.add(new Player("p7", "De Bruyne", generator.generateMidfielder(), Position.CM,
                new Vector2D(45, 25), Vector2D.ZERO, null));
        squad.add(new Player("p8", "Bernardo", generator.generateMidfielder(), Position.CM,
                new Vector2D(45, 43), Vector2D.ZERO, null));
        squad.add(new Player("p9", "Foden", generator.generateWinger(), Position.LW,
                new Vector2D(60, 10), Vector2D.ZERO, null));
        squad.add(new Player("p10", "Haaland", generator.generateStriker(), Position.ST,
                new Vector2D(70, 34), Vector2D.ZERO, null));
        squad.add(new Player("p11", "Grealish", generator.generateWinger(), Position.RW,
                new Vector2D(60, 58), Vector2D.ZERO, null));

        // Step 2: Create team
        Team team = Team.builder()
                .id(id)
                .name(name)
                .players(squad)
                .build();

        // Step 3: Fix player.team references
        for (Player player : squad) {
            player.setTeam(team);
        }

        return team;
    }

    @Nested
    @DisplayName("Creation and Validation")
    class CreationTests {

        @Test
        @DisplayName("Should create valid team with 11 players")
        void createValidTeam() {
            Team team = TeamTest.this.createValidTeam("team1", "Manchester City");

            assertThat(team.getId()).isEqualTo("team1");
            assertThat(team.getName()).isEqualTo("Manchester City");
            assertThat(team.getPlayers()).hasSize(11);
        }

        @Test
        @DisplayName("Should reject team with less than 11 players")
        void rejectTeamWithTooFewPlayers() {
            List<Player> smallSquad = new ArrayList<>();
            smallSquad.add(new Player("p1", "GK", generator.generateGoalkeeper(), Position.GK,
                    new Vector2D(5, 34), Vector2D.ZERO, null));

            Team badTeam = Team.builder()
                    .id("team1")
                    .name("Bad Team")
                    .players(smallSquad)
                    .build();

            assertThatThrownBy(() -> badTeam.validateForMatch())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("must have exactly 11 players");
        }

        @Test
        @DisplayName("Should reject team with more than 11 players")
        void rejectTeamWithTooManyPlayers() {
            List<Player> largeSquad = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                largeSquad.add(new Player("p" + i, "Player" + i,
                        generator.generateStriker(), Position.ST,
                        new Vector2D(50, 30), Vector2D.ZERO, null));
            }

            Team badTeam = Team.builder()
                    .id("team1")
                    .name("Bad Team")
                    .players(largeSquad)
                    .build();

            assertThatThrownBy(() -> badTeam.validateForMatch())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("must have exactly 11 players");
        }

        @Test
        @DisplayName("Should reject team without goalkeeper")
        void rejectTeamWithoutGoalkeeper() {
            List<Player> noGKSquad = new ArrayList<>();
            for (int i = 0; i < 11; i++) {
                noGKSquad.add(new Player("p" + i, "Player" + i,
                        generator.generateStriker(), Position.ST,
                        new Vector2D(50, 30), Vector2D.ZERO, null));
            }

            Team badTeam = Team.builder()
                    .id("team1")
                    .name("Bad Team")
                    .players(noGKSquad)
                    .build();

            assertThatThrownBy(() -> badTeam.validateForMatch())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("must have exactly 1 goalkeeper");
        }

        @Test
        @DisplayName("Should reject team with multiple goalkeepers")
        void rejectTeamWithMultipleGoalkeepers() {
            List<Player> twoGKSquad = new ArrayList<>();
            twoGKSquad.add(new Player("p1", "GK1", generator.generateGoalkeeper(), Position.GK,
                    new Vector2D(5, 34), Vector2D.ZERO, null));
            twoGKSquad.add(new Player("p2", "GK2", generator.generateGoalkeeper(), Position.GK,
                    new Vector2D(10, 34), Vector2D.ZERO, null));
            for (int i = 2; i < 11; i++) {
                twoGKSquad.add(new Player("p" + i, "Player" + i,
                        generator.generateStriker(), Position.ST,
                        new Vector2D(50, 30), Vector2D.ZERO, null));
            }

            Team badTeam = Team.builder()
                    .id("team1")
                    .name("Bad Team")
                    .players(twoGKSquad)
                    .build();

            assertThatThrownBy(() -> badTeam.validateForMatch())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("must have exactly 1 goalkeeper");
        }

        @Test
        @DisplayName("Should reject null id")
        void rejectNullId() {
            assertThatThrownBy(() ->
                    Team.builder()
                            .id(null)
                            .name("Test Team")
                            .players(new ArrayList<>())
                            .build()
            ).isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("id is required");
        }

        @Test
        @DisplayName("Should reject null name")
        void rejectNullName() {
            assertThatThrownBy(() ->
                    Team.builder()
                            .id("team1")
                            .name(null)
                            .players(new ArrayList<>())
                            .build()
            ).isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("name is required");
        }

        @Test
        @DisplayName("Should reject null players")
        void rejectNullPlayers() {
            assertThatThrownBy(() ->
                    Team.builder()
                            .id("team1")
                            .name("Test Team")
                            .players(null)
                            .build()
            ).isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("players are required");
        }
    }

    @Nested
    @DisplayName("Player Queries")
    class PlayerQueryTests {

        private Team team;

        @BeforeEach
        void setUp() {
            team = createValidTeam("team1", "Test Team");
        }

        @Test
        @DisplayName("Should get goalkeeper")
        void getGoalkeeper() {
            Player gk = team.getGoalkeeper();

            assertThat(gk).isNotNull();
            assertThat(gk.isGoalkeeper()).isTrue();
            assertThat(gk.getPosition()).isEqualTo(Position.GK);
        }

        @Test
        @DisplayName("Should get outfield players")
        void getOutfieldPlayers() {
            List<Player> outfield = team.getOutfieldPlayers();

            assertThat(outfield).hasSize(10);
            assertThat(outfield).noneMatch(Player::isGoalkeeper);
        }

        @Test
        @DisplayName("Should get defenders")
        void getDefenders() {
            List<Player> defenders = team.getDefenders();

            assertThat(defenders).hasSize(4);
            assertThat(defenders).allMatch(p -> p.getPosition().isDefender());
        }

        @Test
        @DisplayName("Should get midfielders")
        void getMidfielders() {
            List<Player> midfielders = team.getMidfielders();

            assertThat(midfielders).hasSize(3);
            assertThat(midfielders).allMatch(p -> p.getPosition().isMidfielder());
        }

        @Test
        @DisplayName("Should get attackers")
        void getAttackers() {
            List<Player> attackers = team.getAttackers();

            assertThat(attackers).hasSize(3);
            assertThat(attackers).allMatch(p -> p.getPosition().isAttacker());
        }

        @Test
        @DisplayName("Should get player by ID")
        void getPlayerById() {
            Optional<Player> player = team.getPlayerById("p1");

            assertThat(player).isPresent();
            assertThat(player.get().getId()).isEqualTo("p1");
        }

        @Test
        @DisplayName("Should return empty for non-existent player ID")
        void getPlayerByIdNotFound() {
            Optional<Player> player = team.getPlayerById("p999");

            assertThat(player).isEmpty();
        }
    }

    @Nested
    @DisplayName("Ball Possession")
    class BallPossessionTests {

        private Team team;

        @BeforeEach
        void setUp() {
            team = createValidTeam("team1", "Test Team");
        }

        @Test
        @DisplayName("Should identify when team has possession")
        void teamHasPossession() {
            Ball ball = new Ball(new Vector2D(50, 30), Vector2D.ZERO);
            Player player = team.getPlayers().get(5);
            ball.setPossessor(player);

            assertThat(team.hasPossession(ball)).isTrue();
        }

        @Test
        @DisplayName("Should identify when team does not have possession (loose ball)")
        void teamDoesNotHavePossessionLoose() {
            Ball ball = new Ball(new Vector2D(50, 30), Vector2D.ZERO);

            assertThat(team.hasPossession(ball)).isFalse();
        }

        @Test
        @DisplayName("Should get player with ball")
        void getPlayerWithBall() {
            Ball ball = new Ball(new Vector2D(50, 30), Vector2D.ZERO);
            Player player = team.getPlayers().get(5);
            ball.setPossessor(player);

            Optional<Player> withBall = team.getPlayerWithBall(ball);

            assertThat(withBall).isPresent();
            assertThat(withBall.get()).isEqualTo(player);
        }

        @Test
        @DisplayName("Should return empty when no one has ball")
        void getPlayerWithBallWhenLoose() {
            Ball ball = new Ball(new Vector2D(50, 30), Vector2D.ZERO);

            Optional<Player> withBall = team.getPlayerWithBall(ball);

            assertThat(withBall).isEmpty();
        }
    }

    @Nested
    @DisplayName("Team Identity")
    class TeamIdentityTests {

        @Test
        @DisplayName("Should have unique identity based on ID")
        void uniqueIdentity() {
            Team team1 = createValidTeam("team1", "Same Name");
            Team team2 = createValidTeam("team2", "Same Name");

            assertThat(team1).isNotEqualTo(team2);
            assertThat(team1.hashCode()).isNotEqualTo(team2.hashCode());
        }

        @Test
        @DisplayName("Should have same identity for same ID")
        void sameIdentityForSameId() {
            Team team1 = createValidTeam("team1", "Name A");
            Team team2 = createValidTeam("team1", "Name B");  // Different name, same ID

            assertThat(team1).isEqualTo(team2);
            assertThat(team1.hashCode()).isEqualTo(team2.hashCode());
        }

        @Test
        @DisplayName("Should have readable toString")
        void readableToString() {
            Team team = createValidTeam("team1", "Manchester City");
            String str = team.toString();

            assertThat(str).contains("team1");
            assertThat(str).contains("Manchester City");
            assertThat(str).contains("11");
        }
    }

    @Nested
    @DisplayName("Immutability")
    class ImmutabilityTests {

        @Test
        @DisplayName("Should return immutable player list")
        void immutablePlayerList() {
            Team team = createValidTeam("team1", "Test");
            List<Player> players = team.getPlayers();

            assertThatThrownBy(() ->
                    players.add(new Player("p12", "Extra", generator.generateStriker(),
                            Position.ST, new Vector2D(50, 30), Vector2D.ZERO, team))
            ).isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("Should not affect team when original list is modified")
        void originalListModificationDoesNotAffect() {
            List<Player> originalSquad = new ArrayList<>();
            for (int i = 0; i < 11; i++) {
                Position pos = i == 0 ? Position.GK : Position.ST;
                PlayerStats stats = i == 0 ? generator.generateGoalkeeper() : generator.generateStriker();
                Player player = new Player("p" + i, "Player" + i, stats, pos,
                        new Vector2D(50, 30), Vector2D.ZERO, null);
                originalSquad.add(player);
            }

            Team team = Team.builder()
                    .id("team_test")
                    .name("Test")
                    .players(originalSquad)
                    .build();

            // Fix team references
            for (Player p : originalSquad) {
                p.setTeam(team);
            }

            int originalSize = originalSquad.size();

            // Try to modify original list
            originalSquad.clear();

            // Team should still have 11 players (defensive copy)
            assertThat(team.getPlayers()).hasSize(originalSize);
        }
    }
}
