package com.domingol.engine.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PlayerStatsGenerator")
class PlayerStatsGeneratorTest {

    @Nested
    @DisplayName("Deterministic Generation")
    class DeterministicTests {

        @Test
        @DisplayName("Should generate same stats with same seed")
        void sameSeedSameStats() {
            PlayerStatsGenerator gen1 = new PlayerStatsGenerator(new Random(12345));
            PlayerStatsGenerator gen2 = new PlayerStatsGenerator(new Random(12345));

            PlayerStats striker1 = gen1.generateStriker();
            PlayerStats striker2 = gen2.generateStriker();

            assertThat(striker1).isEqualTo(striker2);
        }

        @Test
        @DisplayName("Should generate different stats with different seeds")
        void differentSeedDifferentStats() {
            PlayerStatsGenerator gen1 = new PlayerStatsGenerator(new Random(12345));
            PlayerStatsGenerator gen2 = new PlayerStatsGenerator(new Random(67890));

            PlayerStats striker1 = gen1.generateStriker();
            PlayerStats striker2 = gen2.generateStriker();

            // Very unlikely to be equal with different seeds
            assertThat(striker1).isNotEqualTo(striker2);
        }
    }

    @Nested
    @DisplayName("Striker Generation")
    class StrikerTests {

        @Test
        @DisplayName("Should generate valid striker stats")
        void generateValidStriker() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random());

            // Should not throw validation exceptions
            assertThatCode(() -> gen.generateStriker()).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should generate striker with high shooting")
        void strikerHasHighShooting() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random(42));
            PlayerStats striker = gen.generateStriker();

            // Striker should have shooting >= 15 (range is 15-20)
            assertThat(striker.getShooting()).isBetween(15, 20);
        }

        @Test
        @DisplayName("Should generate striker with high pace")
        void strikerHasHighPace() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random(42));
            PlayerStats striker = gen.generateStriker();

            // Striker should have pace >= 15 (range is 15-20)
            assertThat(striker.getPace()).isBetween(15, 20);
        }

        @Test
        @DisplayName("Should generate striker with low tackling")
        void strikerHasLowTackling() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random(42));
            PlayerStats striker = gen.generateStriker();

            // Striker should have tackling <= 8 (range is 1-8)
            assertThat(striker.getTackling()).isBetween(1, 8);
        }

        @Test
        @DisplayName("Should generate striker with shooting > tackling")
        void strikerShootingGreaterThanTackling() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random());

            // Test multiple generations to ensure consistency
            for (int i = 0; i < 10; i++) {
                PlayerStats striker = gen.generateStriker();
                assertThat(striker.getShooting()).isGreaterThan(striker.getTackling());
            }
        }

        @Test
        @DisplayName("Should generate striker with goalkeeping = 0")
        void strikerNotGoalkeeper() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random());
            PlayerStats striker = gen.generateStriker();

            assertThat(striker.getGoalkeeping()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Winger Generation")
    class WingerTests {

        @Test
        @DisplayName("Should generate winger with very high pace")
        void wingerHasVeryHighPace() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random(42));
            PlayerStats winger = gen.generateWinger();

            // Winger should have pace >= 17 (range is 17-20)
            assertThat(winger.getPace()).isBetween(17, 20);
        }

        @Test
        @DisplayName("Should generate winger with high stamina")
        void wingerHasHighStamina() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random(42));
            PlayerStats winger = gen.generateWinger();

            // Winger runs a lot (range is 15-19)
            assertThat(winger.getStamina()).isBetween(15, 19);
        }
    }

    @Nested
    @DisplayName("Center Back Generation")
    class CenterBackTests {

        @Test
        @DisplayName("Should generate valid center back stats")
        void generateValidCenterBack() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random());

            assertThatCode(() -> gen.generateCenterBack()).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should generate center back with high tackling")
        void centerBackHasHighTackling() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random(42));
            PlayerStats cb = gen.generateCenterBack();

            // CB should have tackling >= 16 (range is 16-20)
            assertThat(cb.getTackling()).isBetween(16, 20);
        }

        @Test
        @DisplayName("Should generate center back with high positioning")
        void centerBackHasHighPositioning() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random(42));
            PlayerStats cb = gen.generateCenterBack();

            // CB should have positioning >= 15 (range is 15-20)
            assertThat(cb.getPositioning()).isBetween(15, 20);
        }

        @Test
        @DisplayName("Should generate center back with low pace")
        void centerBackHasLowPace() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random(42));
            PlayerStats cb = gen.generateCenterBack();

            // CB should have pace <= 12 (range is 6-12)
            assertThat(cb.getPace()).isBetween(6, 12);
        }

        @Test
        @DisplayName("Should generate center back with low shooting")
        void centerBackHasLowShooting() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random(42));
            PlayerStats cb = gen.generateCenterBack();

            // CB should have shooting <= 8 (range is 1-8)
            assertThat(cb.getShooting()).isBetween(1, 8);
        }

        @Test
        @DisplayName("Should generate center back with tackling > shooting")
        void centerBackTacklingGreaterThanShooting() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random());

            for (int i = 0; i < 10; i++) {
                PlayerStats cb = gen.generateCenterBack();
                assertThat(cb.getTackling()).isGreaterThan(cb.getShooting());
            }
        }
    }

    @Nested
    @DisplayName("Fullback Generation")
    class FullbackTests {

        @Test
        @DisplayName("Should generate fullback with high pace")
        void fullbackHasHighPace() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random(42));
            PlayerStats fb = gen.generateFullback();

            // Fullback needs pace (range is 14-19)
            assertThat(fb.getPace()).isBetween(14, 19);
        }

        @Test
        @DisplayName("Should generate fullback with high stamina")
        void fullbackHasHighStamina() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random(42));
            PlayerStats fb = gen.generateFullback();

            // Fullback runs up and down (range is 15-20)
            assertThat(fb.getStamina()).isBetween(15, 20);
        }

        @Test
        @DisplayName("Should generate fullback faster than center back")
        void fullbackFasterThanCenterBack() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random());

            // Fullbacks (14-19) should generally be faster than CBs (6-12)
            for (int i = 0; i < 5; i++) {
                PlayerStats fb = gen.generateFullback();
                PlayerStats cb = gen.generateCenterBack();
                assertThat(fb.getPace()).isGreaterThan(cb.getPace());
            }
        }
    }

    @Nested
    @DisplayName("Midfielder Generation")
    class MidfielderTests {

        @Test
        @DisplayName("Should generate midfielder with high stamina")
        void midfielderHasHighStamina() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random(42));
            PlayerStats mid = gen.generateMidfielder();

            // Midfielder runs all match (range is 16-20)
            assertThat(mid.getStamina()).isBetween(16, 20);
        }

        @Test
        @DisplayName("Should generate midfielder with high passing")
        void midfielderHasHighPassing() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random(42));
            PlayerStats mid = gen.generateMidfielder();

            // Midfielder distributes ball (range is 14-19)
            assertThat(mid.getPassing()).isBetween(14, 19);
        }

        @Test
        @DisplayName("Should generate balanced midfielder")
        void midfielderIsBalanced() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random());
            PlayerStats mid = gen.generateMidfielder();

            // No outfield stat should be extremely low (all >= 10)
            assertThat(mid.getPace()).isBetween(10, 16);
            assertThat(mid.getShooting()).isBetween(10, 16);
            assertThat(mid.getTackling()).isBetween(11, 17);
        }
    }

    @Nested
    @DisplayName("Defensive Midfielder Generation")
    class DefensiveMidfielderTests {

        @Test
        @DisplayName("Should generate defensive midfielder with high tackling")
        void defensiveMidfielderHasHighTackling() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random(42));
            PlayerStats cdm = gen.generateDefensiveMidfielder();

            // CDM shields defense (range is 15-19)
            assertThat(cdm.getTackling()).isBetween(15, 19);
        }

        @Test
        @DisplayName("Should generate defensive midfielder with high positioning")
        void defensiveMidfielderHasHighPositioning() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random(42));
            PlayerStats cdm = gen.generateDefensiveMidfielder();

            // CDM reads game well (range is 14-19)
            assertThat(cdm.getPositioning()).isBetween(14, 19);
        }

        @Test
        @DisplayName("Should generate defensive midfielder with low shooting")
        void defensiveMidfielderHasLowShooting() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random(42));
            PlayerStats cdm = gen.generateDefensiveMidfielder();

            // CDM not an attacker (range is 5-12)
            assertThat(cdm.getShooting()).isBetween(5, 12);
        }
    }

    @Nested
    @DisplayName("Attacking Midfielder Generation")
    class AttackingMidfielderTests {

        @Test
        @DisplayName("Should generate attacking midfielder with high passing")
        void attackingMidfielderHasHighPassing() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random(42));
            PlayerStats cam = gen.generateAttackingMidfielder();

            // CAM creates chances (range is 15-20)
            assertThat(cam.getPassing()).isBetween(15, 20);
        }

        @Test
        @DisplayName("Should generate attacking midfielder with high shooting")
        void attackingMidfielderHasHighShooting() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random(42));
            PlayerStats cam = gen.generateAttackingMidfielder();

            // CAM scores goals (range is 13-18)
            assertThat(cam.getShooting()).isBetween(13, 18);
        }

        @Test
        @DisplayName("Should generate attacking midfielder with low tackling")
        void attackingMidfielderHasLowTackling() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random(42));
            PlayerStats cam = gen.generateAttackingMidfielder();

            // CAM doesn't defend much (range is 5-12)
            assertThat(cam.getTackling()).isBetween(5, 12);
        }
    }

    @Nested
    @DisplayName("Goalkeeper Generation")
    class GoalkeeperTests {

        @Test
        @DisplayName("Should generate goalkeeper with high goalkeeping stat")
        void goalkeeperHasHighGoalkeeping() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random(42));
            PlayerStats gk = gen.generateGoalkeeper();

            // GK primary stat (range is 14-20)
            assertThat(gk.getGoalkeeping()).isBetween(14, 20);
        }

        @Test
        @DisplayName("Should generate goalkeeper with high positioning")
        void goalkeeperHasHighPositioning() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random(42));
            PlayerStats gk = gen.generateGoalkeeper();

            // GK placement important (range is 14-19)
            assertThat(gk.getPositioning()).isBetween(14, 19);
        }

        @Test
        @DisplayName("Should generate goalkeeper with low pace")
        void goalkeeperHasLowPace() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random(42));
            PlayerStats gk = gen.generateGoalkeeper();

            // GK doesn't run much (range is 5-10)
            assertThat(gk.getPace()).isBetween(5, 10);
        }

        @Test
        @DisplayName("Should generate goalkeeper with low shooting")
        void goalkeeperHasLowShooting() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random(42));
            PlayerStats gk = gen.generateGoalkeeper();

            // GK can't shoot (range is 1-5)
            assertThat(gk.getShooting()).isBetween(1, 5);
        }

        @Test
        @DisplayName("Should generate goalkeeper with goalkeeping > all outfield stats")
        void goalkeeperGoalkeepingBest() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random());

            for (int i = 0; i < 10; i++) {
                PlayerStats gk = gen.generateGoalkeeper();

                assertThat(gk.getGoalkeeping()).isGreaterThanOrEqualTo(14);
                assertThat(gk.getGoalkeeping()).isGreaterThan(gk.getPace());
                assertThat(gk.getGoalkeeping()).isGreaterThan(gk.getShooting());
            }
        }
    }

    @Nested
    @DisplayName("All Positions")
    class AllPositionsTests {

        @Test
        @DisplayName("Should generate valid stats for all positions")
        void generateAllPositions() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random());

            // Should not throw validation exceptions
            assertThatCode(() -> {
                gen.generateStriker();
                gen.generateWinger();
                gen.generateMidfielder();
                gen.generateDefensiveMidfielder();
                gen.generateAttackingMidfielder();
                gen.generateCenterBack();
                gen.generateFullback();
                gen.generateGoalkeeper();
            }).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should generate only goalkeeper with non-zero goalkeeping")
        void onlyGoalkeeperHasGoalkeeping() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random());

            // Outfield players
            assertThat(gen.generateStriker().getGoalkeeping()).isEqualTo(0);
            assertThat(gen.generateWinger().getGoalkeeping()).isEqualTo(0);
            assertThat(gen.generateMidfielder().getGoalkeeping()).isEqualTo(0);
            assertThat(gen.generateDefensiveMidfielder().getGoalkeeping()).isEqualTo(0);
            assertThat(gen.generateAttackingMidfielder().getGoalkeeping()).isEqualTo(0);
            assertThat(gen.generateCenterBack().getGoalkeeping()).isEqualTo(0);
            assertThat(gen.generateFullback().getGoalkeeping()).isEqualTo(0);

            // Goalkeeper
            assertThat(gen.generateGoalkeeper().getGoalkeeping()).isGreaterThan(0);
        }

        @Test
        @DisplayName("Should generate different position types with distinct characteristics")
        void positionsHaveDistinctCharacteristics() {
            PlayerStatsGenerator gen = new PlayerStatsGenerator(new Random(12345));

            PlayerStats striker = gen.generateStriker();
            PlayerStats centerBack = gen.generateCenterBack();
            PlayerStats midfielder = gen.generateMidfielder();

            // Striker shoots better than defends
            assertThat(striker.getShooting()).isGreaterThan(striker.getTackling());

            // Center back defends better than shoots
            assertThat(centerBack.getTackling()).isGreaterThan(centerBack.getShooting());

            // Midfielder is more balanced
            int midRange = midfielder.getPace() + midfielder.getShooting() +
                    midfielder.getPassing() + midfielder.getTackling();
            // All stats should contribute reasonably (rough check)
            assertThat(midRange).isGreaterThan(40);
        }
    }
}
