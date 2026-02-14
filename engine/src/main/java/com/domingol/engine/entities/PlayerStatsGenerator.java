package com.domingol.engine.entities;

import java.util.Random;

/**
 * Generates random player statistics for different positions.
 * <p>
 * Uses realistic stat ranges based on player roles:
 * <ul>
 *   <li><b>Strikers:</b> High pace, shooting; Low tackling</li>
 *   <li><b>Defenders:</b> High tackling, positioning; Low shooting</li>
 *   <li><b>Midfielders:</b> Balanced stats, high stamina</li>
 *   <li><b>Goalkeepers:</b> High goalkeeping; Low pace, shooting</li>
 * </ul>
 * <p>
 * Generation is deterministic when using seeded Random for reproducibility.
 *
 * <h3>Example Usage:</h3>
 * <pre>
 * Random rng = new Random(12345);  // Deterministic
 * PlayerStatsGenerator generator = new PlayerStatsGenerator(rng);
 *
 * PlayerStats striker = generator.generateStriker();
 * PlayerStats defender = generator.generateCenterBack();
 * </pre>
 *
 * <h3>Future Enhancements (Phase 2+):</h3>
 * <ul>
 *   <li>Quality levels (amateur, professional, elite)</li>
 *   <li>Player archetypes (poacher, target man, playmaker)</li>
 *   <li>Age-based stat generation</li>
 * </ul>
 */
public class PlayerStatsGenerator {

    private final Random rng;

    /**
     * Creates a generator with specified Random instance.
     *
     * @param rng Random instance (use seeded for deterministic generation)
     */
    public PlayerStatsGenerator(Random rng) {
        this.rng = rng;
    }

    // === ATTACKING POSITIONS ===

    /**
     * Generates stats for a striker.
     * <p>
     * Characteristics:
     * <ul>
     *   <li>High: pace (15-20), shooting (15-20)</li>
     *   <li>Medium: stamina (12-18), passing (10-15), positioning (12-18)</li>
     *   <li>Low: tackling (1-8)</li>
     * </ul>
     *
     * @return randomly generated striker stats
     */
    public PlayerStats generateStriker() {
        return PlayerStats.builder()
                .pace(randomInRange(15, 20))
                .stamina(randomInRange(12, 18))
                .shooting(randomInRange(15, 20))
                .passing(randomInRange(10, 15))
                .tackling(randomInRange(1, 8))
                .positioning(randomInRange(12, 18))
                .goalkeeping(0)
                .build();
    }

    /**
     * Generates stats for a winger (wide forward).
     * <p>
     * Characteristics:
     * <ul>
     *   <li>High: pace (17-20), stamina (15-19)</li>
     *   <li>Medium: shooting (12-17), passing (11-16), positioning (10-15)</li>
     *   <li>Low: tackling (1-10)</li>
     * </ul>
     *
     * @return randomly generated winger stats
     */
    public PlayerStats generateWinger() {
        return PlayerStats.builder()
                .pace(randomInRange(17, 20))
                .stamina(randomInRange(15, 19))
                .shooting(randomInRange(12, 17))
                .passing(randomInRange(11, 16))
                .tackling(randomInRange(1, 10))
                .positioning(randomInRange(10, 15))
                .goalkeeping(0)
                .build();
    }

    // === MIDFIELD POSITIONS ===

    /**
     * Generates stats for a central midfielder.
     * <p>
     * Characteristics:
     * <ul>
     *   <li>High: stamina (16-20), passing (14-19)</li>
     *   <li>Medium: All other outfield stats (10-16)</li>
     *   <li>Balanced player</li>
     * </ul>
     *
     * @return randomly generated midfielder stats
     */
    public PlayerStats generateMidfielder() {
        return PlayerStats.builder()
                .pace(randomInRange(10, 16))
                .stamina(randomInRange(16, 20))
                .shooting(randomInRange(10, 16))
                .passing(randomInRange(14, 19))
                .tackling(randomInRange(11, 17))
                .positioning(randomInRange(12, 18))
                .goalkeeping(0)
                .build();
    }

    /**
     * Generates stats for a defensive midfielder.
     * <p>
     * Characteristics:
     * <ul>
     *   <li>High: tackling (15-19), positioning (14-19), stamina (15-19)</li>
     *   <li>Medium: passing (12-17)</li>
     *   <li>Low: pace (8-13), shooting (5-12)</li>
     * </ul>
     *
     * @return randomly generated defensive midfielder stats
     */
    public PlayerStats generateDefensiveMidfielder() {
        return PlayerStats.builder()
                .pace(randomInRange(8, 13))
                .stamina(randomInRange(15, 19))
                .shooting(randomInRange(5, 12))
                .passing(randomInRange(12, 17))
                .tackling(randomInRange(15, 19))
                .positioning(randomInRange(14, 19))
                .goalkeeping(0)
                .build();
    }

    /**
     * Generates stats for an attacking midfielder.
     * <p>
     * Characteristics:
     * <ul>
     *   <li>High: passing (15-20), shooting (13-18), positioning (14-19)</li>
     *   <li>Medium: pace (11-16), stamina (13-18)</li>
     *   <li>Low: tackling (5-12)</li>
     * </ul>
     *
     * @return randomly generated attacking midfielder stats
     */
    public PlayerStats generateAttackingMidfielder() {
        return PlayerStats.builder()
                .pace(randomInRange(11, 16))
                .stamina(randomInRange(13, 18))
                .shooting(randomInRange(13, 18))
                .passing(randomInRange(15, 20))
                .tackling(randomInRange(5, 12))
                .positioning(randomInRange(14, 19))
                .goalkeeping(0)
                .build();
    }

    // === DEFENSIVE POSITIONS ===

    /**
     * Generates stats for a center back.
     * <p>
     * Characteristics:
     * <ul>
     *   <li>High: tackling (16-20), positioning (15-20)</li>
     *   <li>Medium: stamina (14-18)</li>
     *   <li>Low: pace (6-12), shooting (1-8), passing (8-14)</li>
     * </ul>
     *
     * @return randomly generated center back stats
     */
    public PlayerStats generateCenterBack() {
        return PlayerStats.builder()
                .pace(randomInRange(6, 12))
                .stamina(randomInRange(14, 18))
                .shooting(randomInRange(1, 8))
                .passing(randomInRange(8, 14))
                .tackling(randomInRange(16, 20))
                .positioning(randomInRange(15, 20))
                .goalkeeping(0)
                .build();
    }

    /**
     * Generates stats for a fullback (LB/RB).
     * <p>
     * Characteristics:
     * <ul>
     *   <li>High: pace (14-19), stamina (15-20), tackling (13-18)</li>
     *   <li>Medium: positioning (12-17), passing (9-15)</li>
     *   <li>Low: shooting (1-10)</li>
     * </ul>
     *
     * @return randomly generated fullback stats
     */
    public PlayerStats generateFullback() {
        return PlayerStats.builder()
                .pace(randomInRange(14, 19))
                .stamina(randomInRange(15, 20))
                .shooting(randomInRange(1, 10))
                .passing(randomInRange(9, 15))
                .tackling(randomInRange(13, 18))
                .positioning(randomInRange(12, 17))
                .goalkeeping(0)
                .build();
    }

    // === GOALKEEPER ===

    /**
     * Generates stats for a goalkeeper.
     * <p>
     * Characteristics:
     * <ul>
     *   <li>High: goalkeeping (14-20), positioning (14-19)</li>
     *   <li>Medium: tackling (10-15) for 1v1 situations</li>
     *   <li>Low: pace (5-10), stamina (10-15), shooting (1-5), passing (8-13)</li>
     * </ul>
     *
     * @return randomly generated goalkeeper stats
     */
    public PlayerStats generateGoalkeeper() {
        return PlayerStats.builder()
                .pace(randomInRange(5, 10))
                .stamina(randomInRange(10, 15))
                .shooting(randomInRange(1, 5))
                .passing(randomInRange(8, 13))
                .tackling(randomInRange(10, 15))
                .positioning(randomInRange(14, 19))
                .goalkeeping(randomInRange(14, 20))
                .build();
    }

    // === UTILITY ===

    /**
     * Generates random stat within range [min, max] inclusive.
     *
     * @param min minimum value (inclusive)
     * @param max maximum value (inclusive)
     * @return random value in range
     */
    private int randomInRange(int min, int max) {
        return min + rng.nextInt(max - min + 1);
    }
}
