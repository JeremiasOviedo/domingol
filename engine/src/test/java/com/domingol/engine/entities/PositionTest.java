package com.domingol.engine.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Position")
class PositionTest {

    @Nested
    @DisplayName("Display Names")
    class DisplayNameTests {

        @Test
        @DisplayName("Should have human-readable display names")
        void hasDisplayNames() {
            assertThat(Position.GK.getDisplayName()).isEqualTo("Goalkeeper");
            assertThat(Position.CB.getDisplayName()).isEqualTo("Center Back");
            assertThat(Position.LB.getDisplayName()).isEqualTo("Left Back");
            assertThat(Position.RB.getDisplayName()).isEqualTo("Right Back");
            assertThat(Position.CDM.getDisplayName()).isEqualTo("Defensive Midfielder");
            assertThat(Position.CM.getDisplayName()).isEqualTo("Central Midfielder");
            assertThat(Position.CAM.getDisplayName()).isEqualTo("Attacking Midfielder");
            assertThat(Position.LW.getDisplayName()).isEqualTo("Left Winger");
            assertThat(Position.RW.getDisplayName()).isEqualTo("Right Winger");
            assertThat(Position.ST.getDisplayName()).isEqualTo("Striker");
        }

        @Test
        @DisplayName("Should have exactly 10 positions")
        void hasTenPositions() {
            assertThat(Position.values()).hasSize(10);
        }
    }

    @Nested
    @DisplayName("Position Type Checks")
    class TypeCheckTests {

        @Test
        @DisplayName("Should identify goalkeeper")
        void identifyGoalkeeper() {
            assertThat(Position.GK.isGoalkeeper()).isTrue();

            // All other positions are not goalkeepers
            assertThat(Position.CB.isGoalkeeper()).isFalse();
            assertThat(Position.CM.isGoalkeeper()).isFalse();
            assertThat(Position.ST.isGoalkeeper()).isFalse();
        }

        @Test
        @DisplayName("Should identify defenders")
        void identifyDefenders() {
            assertThat(Position.CB.isDefender()).isTrue();
            assertThat(Position.LB.isDefender()).isTrue();
            assertThat(Position.RB.isDefender()).isTrue();

            // Non-defenders
            assertThat(Position.GK.isDefender()).isFalse();
            assertThat(Position.CDM.isDefender()).isFalse();
            assertThat(Position.ST.isDefender()).isFalse();
        }

        @Test
        @DisplayName("Should identify midfielders")
        void identifyMidfielders() {
            assertThat(Position.CDM.isMidfielder()).isTrue();
            assertThat(Position.CM.isMidfielder()).isTrue();
            assertThat(Position.CAM.isMidfielder()).isTrue();

            // Non-midfielders
            assertThat(Position.CB.isMidfielder()).isFalse();
            assertThat(Position.LW.isMidfielder()).isFalse();
        }

        @Test
        @DisplayName("Should identify attackers")
        void identifyAttackers() {
            assertThat(Position.LW.isAttacker()).isTrue();
            assertThat(Position.RW.isAttacker()).isTrue();
            assertThat(Position.ST.isAttacker()).isTrue();

            // Non-attackers
            assertThat(Position.CM.isAttacker()).isFalse();
            assertThat(Position.CB.isAttacker()).isFalse();
        }

        @Test
        @DisplayName("Each position belongs to exactly one category")
        void eachPositionHasOneCategory() {
            for (Position pos : Position.values()) {
                int categoryCount = 0;

                if (pos.isGoalkeeper()) categoryCount++;
                if (pos.isDefender()) categoryCount++;
                if (pos.isMidfielder()) categoryCount++;
                if (pos.isAttacker()) categoryCount++;

                assertThat(categoryCount)
                        .withFailMessage("Position %s belongs to %d categories (should be 1)", pos, categoryCount)
                        .isEqualTo(1);
            }
        }
    }

    @Nested
    @DisplayName("Categories")
    class CategoryTests {

        @Test
        @DisplayName("Should return correct category for goalkeeper")
        void goalkeeperCategory() {
            assertThat(Position.GK.getCategory()).isEqualTo("Goalkeeper");
        }

        @Test
        @DisplayName("Should return correct category for defenders")
        void defenderCategory() {
            assertThat(Position.CB.getCategory()).isEqualTo("Defense");
            assertThat(Position.LB.getCategory()).isEqualTo("Defense");
            assertThat(Position.RB.getCategory()).isEqualTo("Defense");
        }

        @Test
        @DisplayName("Should return correct category for midfielders")
        void midfielderCategory() {
            assertThat(Position.CDM.getCategory()).isEqualTo("Midfield");
            assertThat(Position.CM.getCategory()).isEqualTo("Midfield");
            assertThat(Position.CAM.getCategory()).isEqualTo("Midfield");
        }

        @Test
        @DisplayName("Should return correct category for attackers")
        void attackerCategory() {
            assertThat(Position.LW.getCategory()).isEqualTo("Attack");
            assertThat(Position.RW.getCategory()).isEqualTo("Attack");
            assertThat(Position.ST.getCategory()).isEqualTo("Attack");
        }

        @Test
        @DisplayName("All positions should have a category")
        void allPositionsHaveCategory() {
            for (Position pos : Position.values()) {
                assertThatCode(() -> pos.getCategory()).doesNotThrowAnyException();
                assertThat(pos.getCategory()).isNotEmpty();
            }
        }
    }

    @Nested
    @DisplayName("Formation Examples")
    class FormationTests {

        @Test
        @DisplayName("Should support 4-4-2 formation (11 players)")
        void formation442() {
            Position[] formation442 = {
                    Position.GK,
                    Position.LB, Position.CB, Position.CB, Position.RB,
                    Position.LW, Position.CM, Position.CM, Position.RW,
                    Position.ST, Position.ST
            };

            assertThat(formation442).hasSize(11);

            // Count by category
            long gk = countByCategory(formation442, "Goalkeeper");
            long def = countByCategory(formation442, "Defense");
            long mid = countByCategory(formation442, "Midfield");
            long att = countByCategory(formation442, "Attack");

            assertThat(gk).isEqualTo(1);
            assertThat(def).isEqualTo(4);
            assertThat(mid).isEqualTo(2);
            assertThat(att).isEqualTo(4);
        }

        @Test
        @DisplayName("Should support 4-3-3 formation (11 players)")
        void formation433() {
            Position[] formation433 = {
                    Position.GK,
                    Position.LB, Position.CB, Position.CB, Position.RB,
                    Position.CDM, Position.CM, Position.CM,
                    Position.LW, Position.ST, Position.RW
            };

            assertThat(formation433).hasSize(11);

            long gk = countByCategory(formation433, "Goalkeeper");
            long def = countByCategory(formation433, "Defense");
            long mid = countByCategory(formation433, "Midfield");
            long att = countByCategory(formation433, "Attack");

            assertThat(gk).isEqualTo(1);
            assertThat(def).isEqualTo(4);
            assertThat(mid).isEqualTo(3);  // CDM, CM, CM
            assertThat(att).isEqualTo(3);  // LW, ST, RW
        }

        @Test
        @DisplayName("Should support 4-2-3-1 formation (11 players)")
        void formation4231() {
            Position[] formation4231 = {
                    Position.GK,
                    Position.LB, Position.CB, Position.CB, Position.RB,
                    Position.CDM, Position.CDM,
                    Position.CAM, Position.LW, Position.RW,
                    Position.ST
            };

            assertThat(formation4231).hasSize(11);

            long gk = countByCategory(formation4231, "Goalkeeper");
            long def = countByCategory(formation4231, "Defense");
            long mid = countByCategory(formation4231, "Midfield");
            long att = countByCategory(formation4231, "Attack");

            assertThat(gk).isEqualTo(1);
            assertThat(def).isEqualTo(4);
            assertThat(mid).isEqualTo(3);  // CDM, CDM, CAM, LW, RW
            assertThat(att).isEqualTo(3);  // ST
        }

        private long countByCategory(Position[] formation, String category) {
            int count = 0;
            for (Position pos : formation) {
                if (pos.getCategory().equals(category)) {
                    count++;
                }
            }
            return count;
        }
    }

    @Nested
    @DisplayName("Enum Behavior")
    class EnumBehaviorTests {

        @Test
        @DisplayName("Should support valueOf for all positions")
        void supportsValueOf() {
            assertThat(Position.valueOf("GK")).isEqualTo(Position.GK);
            assertThat(Position.valueOf("CB")).isEqualTo(Position.CB);
            assertThat(Position.valueOf("ST")).isEqualTo(Position.ST);
        }

        @Test
        @DisplayName("Should throw on invalid valueOf")
        void throwsOnInvalidValueOf() {
            assertThatThrownBy(() -> Position.valueOf("INVALID"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Should have stable ordinal values")
        void hasStableOrdinals() {
            // Ordinals shouldn't change (used in serialization, DB, etc.)
            assertThat(Position.GK.ordinal()).isEqualTo(0);
            assertThat(Position.CB.ordinal()).isEqualTo(1);
            assertThat(Position.ST.ordinal()).isEqualTo(9);
        }
    }
}
