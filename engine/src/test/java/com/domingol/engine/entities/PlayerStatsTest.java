package com.domingol.engine.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PlayerStats")
class PlayerStatsTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationTests {

        @Test
        @DisplayName("Should create valid outfield player stats")
        void createValidOutfieldPlayer() {
            PlayerStats stats = PlayerStats.builder()
                    .pace(15)
                    .stamina(18)
                    .shooting(12)
                    .passing(16)
                    .tackling(10)
                    .positioning(14)
                    .goalkeeping(0)
                    .build();

            assertThat(stats.getPace()).isEqualTo(15);
            assertThat(stats.getStamina()).isEqualTo(18);
            assertThat(stats.getShooting()).isEqualTo(12);
            assertThat(stats.getPassing()).isEqualTo(16);
            assertThat(stats.getTackling()).isEqualTo(10);
            assertThat(stats.getPositioning()).isEqualTo(14);
            assertThat(stats.getGoalkeeping()).isEqualTo(0);
        }

        @Test
        @DisplayName("Should create valid goalkeeper stats")
        void createValidGoalkeeper() {
            PlayerStats stats = PlayerStats.builder()
                    .pace(8)
                    .stamina(12)
                    .shooting(3)
                    .passing(10)
                    .tackling(12)
                    .positioning(16)
                    .goalkeeping(18)
                    .build();

            assertThat(stats.getGoalkeeping()).isEqualTo(18);
            assertThat(stats.getPace()).isEqualTo(8);
        }

        @Test
        @DisplayName("Should accept minimum valid values (all 1s, goalkeeping 0)")
        void createMinimumStats() {
            PlayerStats stats = PlayerStats.builder()
                    .pace(1)
                    .stamina(1)
                    .shooting(1)
                    .passing(1)
                    .tackling(1)
                    .positioning(1)
                    .goalkeeping(0)
                    .build();

            assertThat(stats.getPace()).isEqualTo(1);
            assertThat(stats.getGoalkeeping()).isEqualTo(0);
        }

        @Test
        @DisplayName("Should accept maximum valid values (all 20s)")
        void createMaximumStats() {
            PlayerStats stats = PlayerStats.builder()
                    .pace(20)
                    .stamina(20)
                    .shooting(20)
                    .passing(20)
                    .tackling(20)
                    .positioning(20)
                    .goalkeeping(20)
                    .build();

            assertThat(stats.getPace()).isEqualTo(20);
            assertThat(stats.getGoalkeeping()).isEqualTo(20);
        }
    }

    @Nested
    @DisplayName("Validation - Pace")
    class PaceValidationTests {

        @Test
        @DisplayName("Should reject pace < 1")
        void rejectPaceTooLow() {
            assertThatThrownBy(() ->
                    PlayerStats.builder()
                            .pace(0)
                            .stamina(15)
                            .shooting(15)
                            .passing(15)
                            .tackling(15)
                            .positioning(15)
                            .goalkeeping(0)
                            .build()
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Pace")
                    .hasMessageContaining("1-20");
        }

        @Test
        @DisplayName("Should reject pace > 20")
        void rejectPaceTooHigh() {
            assertThatThrownBy(() ->
                    PlayerStats.builder()
                            .pace(21)
                            .stamina(15)
                            .shooting(15)
                            .passing(15)
                            .tackling(15)
                            .positioning(15)
                            .goalkeeping(0)
                            .build()
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Pace")
                    .hasMessageContaining("1-20");
        }
    }

    @Nested
    @DisplayName("Validation - Stamina")
    class StaminaValidationTests {

        @Test
        @DisplayName("Should reject stamina < 1")
        void rejectStaminaTooLow() {
            assertThatThrownBy(() ->
                    PlayerStats.builder()
                            .pace(15)
                            .stamina(0)
                            .shooting(15)
                            .passing(15)
                            .tackling(15)
                            .positioning(15)
                            .goalkeeping(0)
                            .build()
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Stamina")
                    .hasMessageContaining("1-20");
        }

        @Test
        @DisplayName("Should reject stamina > 20")
        void rejectStaminaTooHigh() {
            assertThatThrownBy(() ->
                    PlayerStats.builder()
                            .pace(15)
                            .stamina(999)
                            .shooting(15)
                            .passing(15)
                            .tackling(15)
                            .positioning(15)
                            .goalkeeping(0)
                            .build()
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Stamina")
                    .hasMessageContaining("1-20");
        }
    }

    @Nested
    @DisplayName("Validation - Shooting")
    class ShootingValidationTests {

        @Test
        @DisplayName("Should reject shooting < 1")
        void rejectShootingTooLow() {
            assertThatThrownBy(() ->
                    PlayerStats.builder()
                            .pace(15)
                            .stamina(15)
                            .shooting(-5)
                            .passing(15)
                            .tackling(15)
                            .positioning(15)
                            .goalkeeping(0)
                            .build()
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Shooting")
                    .hasMessageContaining("1-20");
        }

        @Test
        @DisplayName("Should reject shooting > 20")
        void rejectShootingTooHigh() {
            assertThatThrownBy(() ->
                    PlayerStats.builder()
                            .pace(15)
                            .stamina(15)
                            .shooting(25)
                            .passing(15)
                            .tackling(15)
                            .positioning(15)
                            .goalkeeping(0)
                            .build()
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Shooting")
                    .hasMessageContaining("1-20");
        }
    }

    @Nested
    @DisplayName("Validation - Passing")
    class PassingValidationTests {

        @Test
        @DisplayName("Should reject passing < 1")
        void rejectPassingTooLow() {
            assertThatThrownBy(() ->
                    PlayerStats.builder()
                            .pace(15)
                            .stamina(15)
                            .shooting(15)
                            .passing(0)
                            .tackling(15)
                            .positioning(15)
                            .goalkeeping(0)
                            .build()
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Passing")
                    .hasMessageContaining("1-20");
        }

        @Test
        @DisplayName("Should reject passing > 20")
        void rejectPassingTooHigh() {
            assertThatThrownBy(() ->
                    PlayerStats.builder()
                            .pace(15)
                            .stamina(15)
                            .shooting(15)
                            .passing(100)
                            .tackling(15)
                            .positioning(15)
                            .goalkeeping(0)
                            .build()
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Passing")
                    .hasMessageContaining("1-20");
        }
    }

    @Nested
    @DisplayName("Validation - Tackling")
    class TacklingValidationTests {

        @Test
        @DisplayName("Should reject tackling < 1")
        void rejectTacklingTooLow() {
            assertThatThrownBy(() ->
                    PlayerStats.builder()
                            .pace(15)
                            .stamina(15)
                            .shooting(15)
                            .passing(15)
                            .tackling(0)
                            .positioning(15)
                            .goalkeeping(0)
                            .build()
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Tackling")
                    .hasMessageContaining("1-20");
        }

        @Test
        @DisplayName("Should reject tackling > 20")
        void rejectTacklingTooHigh() {
            assertThatThrownBy(() ->
                    PlayerStats.builder()
                            .pace(15)
                            .stamina(15)
                            .shooting(15)
                            .passing(15)
                            .tackling(50)
                            .positioning(15)
                            .goalkeeping(0)
                            .build()
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Tackling")
                    .hasMessageContaining("1-20");
        }
    }

    @Nested
    @DisplayName("Validation - Positioning")
    class PositioningValidationTests {

        @Test
        @DisplayName("Should reject positioning < 1")
        void rejectPositioningTooLow() {
            assertThatThrownBy(() ->
                    PlayerStats.builder()
                            .pace(15)
                            .stamina(15)
                            .shooting(15)
                            .passing(15)
                            .tackling(15)
                            .positioning(0)
                            .goalkeeping(0)
                            .build()
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Positioning")
                    .hasMessageContaining("1-20");
        }

        @Test
        @DisplayName("Should reject positioning > 20")
        void rejectPositioningTooHigh() {
            assertThatThrownBy(() ->
                    PlayerStats.builder()
                            .pace(15)
                            .stamina(15)
                            .shooting(15)
                            .passing(15)
                            .tackling(15)
                            .positioning(30)
                            .goalkeeping(0)
                            .build()
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Positioning")
                    .hasMessageContaining("1-20");
        }
    }

    @Nested
    @DisplayName("Validation - Goalkeeping")
    class GoalkeepingValidationTests {

        @Test
        @DisplayName("Should accept goalkeeping = 0 (outfield player)")
        void acceptGoalkeepingZero() {
            PlayerStats stats = PlayerStats.builder()
                    .pace(15)
                    .stamina(15)
                    .shooting(15)
                    .passing(15)
                    .tackling(15)
                    .positioning(15)
                    .goalkeeping(0)
                    .build();

            assertThat(stats.getGoalkeeping()).isEqualTo(0);
        }

        @Test
        @DisplayName("Should reject goalkeeping < 0")
        void rejectGoalkeepingNegative() {
            assertThatThrownBy(() ->
                    PlayerStats.builder()
                            .pace(15)
                            .stamina(15)
                            .shooting(15)
                            .passing(15)
                            .tackling(15)
                            .positioning(15)
                            .goalkeeping(-1)
                            .build()
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Goalkeeping")
                    .hasMessageContaining("0-20");
        }

        @Test
        @DisplayName("Should reject goalkeeping > 20")
        void rejectGoalkeepingTooHigh() {
            assertThatThrownBy(() ->
                    PlayerStats.builder()
                            .pace(15)
                            .stamina(15)
                            .shooting(15)
                            .passing(15)
                            .tackling(15)
                            .positioning(15)
                            .goalkeeping(21)
                            .build()
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Goalkeeping")
                    .hasMessageContaining("0-20");
        }
    }

    @Nested
    @DisplayName("Speed Calculation")
    class SpeedCalculationTests {

        @Test
        @DisplayName("Should calculate max speed from pace = 1 (slowest)")
        void calculateMinSpeed() {
            PlayerStats stats = PlayerStats.builder()
                    .pace(1)
                    .stamina(15)
                    .shooting(15)
                    .passing(15)
                    .tackling(15)
                    .positioning(15)
                    .goalkeeping(0)
                    .build();

            // Formula: 5.0 + (pace / 20.0) * 5.0
            // pace=1: 5.0 + (1/20) * 5.0 = 5.0 + 0.25 = 5.25
            assertThat(stats.calculateMaxSpeed()).isCloseTo(5.25, within(0.01));
        }

        @Test
        @DisplayName("Should calculate max speed from pace = 20 (fastest)")
        void calculateMaxSpeed() {
            PlayerStats stats = PlayerStats.builder()
                    .pace(20)
                    .stamina(15)
                    .shooting(15)
                    .passing(15)
                    .tackling(15)
                    .positioning(15)
                    .goalkeeping(0)
                    .build();

            // Formula: 5.0 + (pace / 20.0) * 5.0
            // pace=20: 5.0 + (20/20) * 5.0 = 5.0 + 5.0 = 10.0
            assertThat(stats.calculateMaxSpeed()).isCloseTo(10.0, within(0.01));
        }

        @Test
        @DisplayName("Should calculate max speed from pace = 10 (medium)")
        void calculateMediumSpeed() {
            PlayerStats stats = PlayerStats.builder()
                    .pace(10)
                    .stamina(15)
                    .shooting(15)
                    .passing(15)
                    .tackling(15)
                    .positioning(15)
                    .goalkeeping(0)
                    .build();

            // Formula: 5.0 + (pace / 20.0) * 5.0
            // pace=10: 5.0 + (10/20) * 5.0 = 5.0 + 2.5 = 7.5
            assertThat(stats.calculateMaxSpeed()).isCloseTo(7.5, within(0.01));
        }
    }

    @Nested
    @DisplayName("Immutability")
    class ImmutabilityTests {

        @Test
        @DisplayName("Should be immutable - creates new instance with improvements")
        void immutableImprovement() {
            PlayerStats original = PlayerStats.builder()
                    .pace(15)
                    .stamina(15)
                    .shooting(15)
                    .passing(15)
                    .tackling(15)
                    .positioning(15)
                    .goalkeeping(0)
                    .build();

            PlayerStats improved = original.withImprovedPace(2);

            // Original unchanged
            assertThat(original.getPace()).isEqualTo(15);

            // New instance has improvement
            assertThat(improved.getPace()).isEqualTo(17);

            // Other stats unchanged
            assertThat(improved.getStamina()).isEqualTo(15);
        }

        @Test
        @DisplayName("Should cap improvements at 20")
        void capImprovementsAt20() {
            PlayerStats stats = PlayerStats.builder()
                    .pace(18)
                    .stamina(15)
                    .shooting(15)
                    .passing(15)
                    .tackling(15)
                    .positioning(15)
                    .goalkeeping(0)
                    .build();

            PlayerStats improved = stats.withImprovedPace(5);  // Would be 23

            // Capped at 20
            assertThat(improved.getPace()).isEqualTo(20);
        }
    }

}