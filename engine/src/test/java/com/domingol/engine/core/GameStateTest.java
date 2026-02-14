package com.domingol.engine.core;

import com.domingol.engine.entities.Ball;
import com.domingol.engine.entities.Player;
import com.domingol.engine.entities.PlayerStats;
import com.domingol.engine.entities.PlayerStatsGenerator;
import com.domingol.engine.entities.Position;
import com.domingol.engine.entities.Team;
import com.domingol.engine.spatial.Vector2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;

@DisplayName("GameState")
class GameStateTest {

    private static final long TEST_SEED = 12345L;

    private Team homeTeam;
    private Team awayTeam;
    private Ball ball;
    private GameState gameState;
    private PlayerStatsGenerator statsGenerator;

    @BeforeEach
    void setUp() {
        statsGenerator = new PlayerStatsGenerator(new Random(TEST_SEED));

        // Create minimal valid teams for testing (11 players each)
        homeTeam = createTestTeam("Home Team", "H");
        awayTeam = createTestTeam("Away Team", "A");

        ball = new Ball(new Vector2D(52.5, 34.0));
        gameState = new GameState(homeTeam, awayTeam, ball);
    }

    /**
     * Creates a valid test team with 11 players.
     * Uses PlayerStatsGenerator for realistic stats.
     */
    private Team createTestTeam(String teamName, String playerPrefix) {
        List<Player> players = new ArrayList<>();

        // 1 GK
        players.add(createPlayer(playerPrefix + "1", Position.GK,
                statsGenerator.generateGoalkeeper()));

        // 4 Defenders (2 CB, 1 LB, 1 RB)
        players.add(createPlayer(playerPrefix + "2", Position.CB,
                statsGenerator.generateCenterBack()));
        players.add(createPlayer(playerPrefix + "3", Position.CB,
                statsGenerator.generateCenterBack()));
        players.add(createPlayer(playerPrefix + "4", Position.LB,
                statsGenerator.generateFullback()));
        players.add(createPlayer(playerPrefix + "5", Position.RB,
                statsGenerator.generateFullback()));

        // 4 Midfielders (2 CM, 1 CDM, 1 CAM)
        players.add(createPlayer(playerPrefix + "6", Position.CDM,
                statsGenerator.generateDefensiveMidfielder()));
        players.add(createPlayer(playerPrefix + "7", Position.CM,
                statsGenerator.generateMidfielder()));
        players.add(createPlayer(playerPrefix + "8", Position.CM,
                statsGenerator.generateMidfielder()));
        players.add(createPlayer(playerPrefix + "9", Position.CAM,
                statsGenerator.generateAttackingMidfielder()));

        // 2 Forwards (1 LW, 1 ST)
        players.add(createPlayer(playerPrefix + "10", Position.LW,
                statsGenerator.generateWinger()));
        players.add(createPlayer(playerPrefix + "11", Position.ST,
                statsGenerator.generateStriker()));

        Team team = Team.builder()
                .id(playerPrefix + "_TEAM")
                .name(teamName)
                .players(players)
                .build();

        // Set bidirectional team reference
        players.forEach(p -> p.setTeam(team));

        return team;
    }

    /**
     * Creates a single player with given stats.
     */
    private Player createPlayer(String id, Position position, PlayerStats stats) {
        return new Player(
                id,
                "Player " + id,
                stats,
                position,
                new Vector2D(0, 0),  // Will be set by formation later
                Vector2D.ZERO,
                null  // Team set after Team creation
        );
    }

    @Nested
    @DisplayName("Initialization")
    class Initialization {

        @Test
        @DisplayName("Should start with score 0-0")
        void shouldStartWithZeroScore() {
            assertThat(gameState.getHomeScore()).isZero();
            assertThat(gameState.getAwayScore()).isZero();
        }

        @Test
        @DisplayName("Should start at tick 0")
        void shouldStartAtTickZero() {
            assertThat(gameState.getCurrentTick()).isZero();
        }

        @Test
        @DisplayName("Should start in first half")
        void shouldStartInFirstHalf() {
            assertThat(gameState.getPhase()).isEqualTo(GameState.MatchPhase.FIRST_HALF);
        }

        @Test
        @DisplayName("Should not be over at start")
        void shouldNotBeOverAtStart() {
            assertThat(gameState.isMatchOver()).isFalse();
        }

        @Test
        @DisplayName("Should reference correct teams")
        void shouldReferenceCorrectTeams() {
            assertThat(gameState.getHomeTeam()).isEqualTo(homeTeam);
            assertThat(gameState.getAwayTeam()).isEqualTo(awayTeam);
        }

        @Test
        @DisplayName("Should reference ball")
        void shouldReferenceBall() {
            assertThat(gameState.getBall()).isEqualTo(ball);
        }
    }

    @Nested
    @DisplayName("Time Management")
    class TimeManagement {

        @Test
        @DisplayName("Should advance tick correctly")
        void shouldAdvanceTickCorrectly() {
            gameState.advanceTick();
            assertThat(gameState.getCurrentTick()).isEqualTo(1);

            gameState.advanceTick();
            assertThat(gameState.getCurrentTick()).isEqualTo(2);
        }

        @Test
        @DisplayName("Should calculate current minute correctly")
        void shouldCalculateCurrentMinuteCorrectly() {
            // Tick 0 = minute 0
            assertThat(gameState.getCurrentMinute()).isZero();

            // Tick 120 = minute 1 (120 ticks / 120 ticks per minute)
            for (int i = 0; i < 120; i++) gameState.advanceTick();
            assertThat(gameState.getCurrentMinute()).isEqualTo(1);

            // Tick 600 = minute 5
            for (int i = 0; i < 480; i++) gameState.advanceTick();
            assertThat(gameState.getCurrentMinute()).isEqualTo(5);
        }

        @Test
        @DisplayName("Should calculate current second correctly")
        void shouldCalculateCurrentSecondCorrectly() {
            // Tick 0 = second 0
            assertThat(gameState.getCurrentSecond()).isZero();

            // Tick 2 = second 1 (2 ticks / 2 ticks per second)
            gameState.advanceTick();
            gameState.advanceTick();
            assertThat(gameState.getCurrentSecond()).isEqualTo(1);

            // Tick 120 = second 0 (new minute)
            for (int i = 0; i < 118; i++) gameState.advanceTick();
            assertThat(gameState.getCurrentSecond()).isZero();
        }
    }

    @Nested
    @DisplayName("Match Phases")
    class MatchPhases {

        @Test
        @DisplayName("Should transition to half time after first half")
        void shouldTransitionToHalfTime() {
            // Advance to tick 2701 (first half ends at 2700)
            for (int i = 0; i < 2701; i++) gameState.advanceTick();

            assertThat(gameState.getPhase()).isEqualTo(GameState.MatchPhase.HALF_TIME);
        }

        @Test
        @DisplayName("Should transition to second half after half time")
        void shouldTransitionToSecondHalf() {
            // Advance to tick 2821 (half time ends at 2820)
            for (int i = 0; i < 2821; i++) gameState.advanceTick();

            assertThat(gameState.getPhase()).isEqualTo(GameState.MatchPhase.SECOND_HALF);
        }

        @Test
        @DisplayName("Should transition to full time after second half")
        void shouldTransitionToFullTime() {
            // Advance to tick 5401 (second half ends at 5400)
            for (int i = 0; i < 5401; i++) gameState.advanceTick();

            assertThat(gameState.getPhase()).isEqualTo(GameState.MatchPhase.FULL_TIME);
            assertThat(gameState.isMatchOver()).isTrue();
        }

        @Test
        @DisplayName("Should stay in full time after match ends")
        void shouldStayInFullTimeAfterMatchEnds() {
            for (int i = 0; i < 6000; i++) gameState.advanceTick();

            assertThat(gameState.getPhase()).isEqualTo(GameState.MatchPhase.FULL_TIME);
            assertThat(gameState.isMatchOver()).isTrue();
        }
    }

    @Nested
    @DisplayName("Score Management")
    class ScoreManagement {

        @Test
        @DisplayName("Should increment home score")
        void shouldIncrementHomeScore() {
            gameState.incrementHomeScore();
            assertThat(gameState.getHomeScore()).isEqualTo(1);

            gameState.incrementHomeScore();
            assertThat(gameState.getHomeScore()).isEqualTo(2);
        }

        @Test
        @DisplayName("Should increment away score")
        void shouldIncrementAwayScore() {
            gameState.incrementAwayScore();
            assertThat(gameState.getAwayScore()).isEqualTo(1);

            gameState.incrementAwayScore();
            assertThat(gameState.getAwayScore()).isEqualTo(2);
        }

        @Test
        @DisplayName("Should increment score by team reference")
        void shouldIncrementScoreByTeamReference() {
            gameState.incrementScore(homeTeam);
            assertThat(gameState.getHomeScore()).isEqualTo(1);
            assertThat(gameState.getAwayScore()).isZero();

            gameState.incrementScore(awayTeam);
            assertThat(gameState.getHomeScore()).isEqualTo(1);
            assertThat(gameState.getAwayScore()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should throw exception for unknown team")
        void shouldThrowExceptionForUnknownTeam() {
            // Create a team that's not in the match
            Team unknownTeam = createTestTeam("Unknown Team", "U");

            assertThatThrownBy(() -> gameState.incrementScore(unknownTeam))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not part of this match");
        }
    }
}