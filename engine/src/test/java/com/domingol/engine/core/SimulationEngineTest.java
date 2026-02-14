package com.domingol.engine.core;

import com.domingol.engine.entities.*;
import com.domingol.engine.spatial.Vector2D;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;

@DisplayName("SimulationEngine")
class SimulationEngineTest {

    private static final long TEST_SEED = 12345L;

    @Test
    @DisplayName("Should simulate complete match without crashing")
    void shouldSimulateCompleteMatch() {
        // Given
        Team homeTeam = createTestTeam("Manchester City", "HOME");
        Team awayTeam = createTestTeam("Luton Town", "AWAY");

        SimulationEngine engine = new SimulationEngine();

        // When
        MatchResult result = engine.simulate(homeTeam, awayTeam, TEST_SEED);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getHomeTeam()).isEqualTo(homeTeam);
        assertThat(result.getAwayTeam()).isEqualTo(awayTeam);

        // Score should be >= 0
        assertThat(result.getHomeScore()).isGreaterThanOrEqualTo(0);
        assertThat(result.getAwayScore()).isGreaterThanOrEqualTo(0);

        // Should have some events
        assertThat(result.getEvents()).isNotEmpty();

        // Print result
        System.out.println(result.toText());
        System.out.println("\nTotal events: " + result.getEvents().size());
    }

    @Test
    @DisplayName("Should produce deterministic results with same seed")
    void shouldBeDeterministic() {
        // Given
        Team homeTeam = createTestTeam("Manchester City", "HOME");
        Team awayTeam = createTestTeam("Luton Town", "AWAY");

        SimulationEngine engine = new SimulationEngine();

        // When: Simulate twice with same seed
        MatchResult result1 = engine.simulate(homeTeam, awayTeam, TEST_SEED);
        MatchResult result2 = engine.simulate(homeTeam, awayTeam, TEST_SEED);

        // Then: Results should be identical
        assertThat(result1.getHomeScore()).isEqualTo(result2.getHomeScore());
        assertThat(result1.getAwayScore()).isEqualTo(result2.getAwayScore());
        assertThat(result1.getEvents()).hasSameSizeAs(result2.getEvents());
    }

    @Test
    @DisplayName("Should produce different results with different seeds")
    void shouldVaryWithDifferentSeeds() {
        // Given
        Team homeTeam = createTestTeam("Manchester City", "HOME");
        Team awayTeam = createTestTeam("Luton Town", "AWAY");

        SimulationEngine engine = new SimulationEngine();

        // When: Simulate 10 times with different seeds
        List<MatchResult> results = new ArrayList<>();
        for (int seed = 0; seed < 10; seed++) {
            results.add(engine.simulate(homeTeam, awayTeam, seed));
        }

        // Then: At least some results should differ
        boolean foundDifferentScores = false;
        int firstHomeScore = results.getFirst().getHomeScore();
        int firstAwayScore = results.getFirst().getAwayScore();

        for (int i = 1; i < results.size(); i++) {
            if (results.get(i).getHomeScore() != firstHomeScore ||
                    results.get(i).getAwayScore() != firstAwayScore) {
                foundDifferentScores = true;
                break;
            }
        }

        assertThat(foundDifferentScores)
                .as("Different seeds should produce different results")
                .isTrue();
    }

    @Test
    @DisplayName("DEBUG: Test different seeds")
    void debugDifferentSeeds() {
        Team homeTeam = createTestTeam("Manchester City", "HOME");
        Team awayTeam = createTestTeam("Luton Town", "AWAY");

        SimulationEngine engine = new SimulationEngine();

        System.out.println("Testing 10 different seeds:\n");

        for (long seed = 0; seed < 10; seed++) {
            MatchResult result = engine.simulate(homeTeam, awayTeam, seed);
            System.out.printf("Seed %d: %s %d - %d %s (Events: %d)%n",
                    seed,
                    result.getHomeTeam().getName(),
                    result.getHomeScore(),
                    result.getAwayScore(),
                    result.getAwayTeam().getName(),
                    result.getEvents().size()
            );
        }
    }

    // Helper: Create test team
    private Team createTestTeam(String teamName, String idPrefix) {
        PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random(TEST_SEED));
        List<Player> players = new ArrayList<>();

        // 1 GK
        players.add(createPlayer(idPrefix + "1", Position.GK,
                gen.generateGoalkeeper()));

        // 4 Defenders
        players.add(createPlayer(idPrefix + "2", Position.CB,
                gen.generateCenterBack()));
        players.add(createPlayer(idPrefix + "3", Position.CB,
                gen.generateCenterBack()));
        players.add(createPlayer(idPrefix + "4", Position.LB,
                gen.generateFullback()));
        players.add(createPlayer(idPrefix + "5", Position.RB,
                gen.generateFullback()));

        // 4 Midfielders
        players.add(createPlayer(idPrefix + "6", Position.CDM,
                gen.generateDefensiveMidfielder()));
        players.add(createPlayer(idPrefix + "7", Position.CM,
                gen.generateMidfielder()));
        players.add(createPlayer(idPrefix + "8", Position.CM,
                gen.generateMidfielder()));
        players.add(createPlayer(idPrefix + "9", Position.CAM,
                gen.generateAttackingMidfielder()));

        // 2 Forwards
        players.add(createPlayer(idPrefix + "10", Position.LW,
                gen.generateWinger()));
        players.add(createPlayer(idPrefix + "11", Position.ST,
                gen.generateStriker()));

        Team team = Team.builder()
                .id(idPrefix + "_TEAM")
                .name(teamName)
                .players(players)
                .build();

        players.forEach(p -> p.setTeam(team));

        return team;
    }

    private Player createPlayer(String id, Position position, PlayerStats stats) {
        return new Player(
                id,
                "Player " + id,
                stats,
                position,
                new Vector2D(0, 0),
                Vector2D.ZERO,
                null
        );
    }
}
