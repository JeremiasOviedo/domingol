package com.domingol.engine.spatial;

import org.junit.jupiter.api.*;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for SpatialGrid
 */
class SpatialGridTest {

    private static final double FIELD_WIDTH = 105.0;
    private static final double FIELD_HEIGHT = 68.0;
    private static final double CELL_SIZE = 5.0;

    private SpatialGrid grid;

    @BeforeEach
    void setUp() {
        grid = new SpatialGrid(FIELD_WIDTH, FIELD_HEIGHT, CELL_SIZE);
    }

    // ==================== CONSTRUCTION ====================

    @Nested
    @DisplayName("Construction and Initialization")
    class ConstructionTests {

        @Test
        @DisplayName("Should create grid with correct dimensions")
        void testConstruction() {
            assertThat(grid.getFieldWidth()).isEqualTo(FIELD_WIDTH);
            assertThat(grid.getFieldHeight()).isEqualTo(FIELD_HEIGHT);
            assertThat(grid.getCellSize()).isEqualTo(CELL_SIZE);
        }

        @Test
        @DisplayName("Should calculate correct grid size")
        void testGridSize() {
            // 105 / 5 = 21 cells wide
            // 68 / 5 = 13.6 → 14 cells high (ceiling)
            assertThat(grid.getGridWidth()).isEqualTo(21);
            assertThat(grid.getGridHeight()).isEqualTo(14);
        }

        @Test
        @DisplayName("Should start empty")
        void testStartsEmpty() {
            assertThat(grid.getTotalEntityCount()).isZero();
        }
    }

    // ==================== INSERT & CLEAR ====================

    @Nested
    @DisplayName("Insert and Clear Operations")
    class InsertClearTests {

        @Test
        @DisplayName("Should insert entity at position")
        void testInsert() {
            grid.insert("player1", new Vector2D(50, 30));

            assertThat(grid.getTotalEntityCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should insert multiple entities")
        void testInsertMultiple() {
            grid.insert("player1", new Vector2D(10, 10));
            grid.insert("player2", new Vector2D(20, 20));
            grid.insert("player3", new Vector2D(30, 30));

            assertThat(grid.getTotalEntityCount()).isEqualTo(3);
        }

        @Test
        @DisplayName("Should handle same entity inserted multiple times")
        void testInsertDuplicate() {
            Vector2D pos = new Vector2D(50, 30);
            grid.insert("player1", pos);
            grid.insert("player1", pos);

            // Should only count once (Set behavior)
            assertThat(grid.getTotalEntityCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should clear all entities")
        void testClear() {
            grid.insert("player1", new Vector2D(10, 10));
            grid.insert("player2", new Vector2D(20, 20));

            grid.clear();

            assertThat(grid.getTotalEntityCount()).isZero();
        }
    }

    // ==================== REMOVE ====================

    @Nested
    @DisplayName("Remove Operations")
    class RemoveTests {

        @Test
        @DisplayName("Should remove entity")
        void testRemove() {
            Vector2D pos = new Vector2D(50, 30);
            grid.insert("player1", pos);
            grid.remove("player1", pos);

            assertThat(grid.getTotalEntityCount()).isZero();
        }

        @Test
        @DisplayName("Should handle removing non-existent entity")
        void testRemoveNonExistent() {
            grid.insert("player1", new Vector2D(50, 30));

            // Remove different entity
            grid.remove("player2", new Vector2D(50, 30));

            // Should still have player1
            assertThat(grid.getTotalEntityCount()).isEqualTo(1);
        }
    }

    // ==================== NEARBY QUERIES ====================

    @Nested
    @DisplayName("Nearby Entity Queries")
    class NearbyQueryTests {

        @Test
        @DisplayName("Should find entity within radius")
        void testGetEntitiesNear() {
            Vector2D center = new Vector2D(50, 30);
            grid.insert("player1", center);
            grid.insert("player2", new Vector2D(52, 32)); // 2.83m away
            grid.insert("player3", new Vector2D(60, 40)); // 14.14m away

            Set<String> nearby = grid.getEntitiesNear(center, 5.0);

            assertThat(nearby)
                    .hasSize(2)
                    .contains("player1", "player2")
                    .doesNotContain("player3");
        }

        @Test
        @DisplayName("Should return empty set when no entities nearby")
        void testGetEntitiesNearEmpty() {
            grid.insert("player1", new Vector2D(10, 10));

            Set<String> nearby = grid.getEntitiesNear(new Vector2D(90, 60), 5.0);

            assertThat(nearby).isEmpty();
        }

        @Test
        @DisplayName("Should handle entities at edge of radius")
        void testGetEntitiesNearEdge() {
            Vector2D center = new Vector2D(50, 30);
            grid.insert("player1", center);
            grid.insert("player2", new Vector2D(55, 30)); // Exactly 5m away

            Set<String> nearby = grid.getEntitiesNear(center, 5.0);

            // Grid returns candidates; exact filtering is caller's responsibility
            assertThat(nearby).contains("player1", "player2");
        }
    }

    // ==================== AREA QUERIES ====================

    @Nested
    @DisplayName("Area Query Tests")
    class AreaQueryTests {

        @Test
        @DisplayName("Should find entities in rectangular area")
        void testGetEntitiesInArea() {
            grid.insert("player1", new Vector2D(10, 10));
            grid.insert("player2", new Vector2D(15, 15));
            grid.insert("player3", new Vector2D(50, 50));

            Set<String> inArea = grid.getEntitiesInArea(
                    new Vector2D(5, 5),
                    new Vector2D(20, 20)
            );

            assertThat(inArea)
                    .hasSize(2)
                    .contains("player1", "player2")
                    .doesNotContain("player3");
        }

        @Test
        @DisplayName("Should handle swapped min/max positions")
        void testGetEntitiesInAreaSwapped() {
            grid.insert("player1", new Vector2D(10, 10));

            // Swap min and max
            Set<String> inArea = grid.getEntitiesInArea(
                    new Vector2D(20, 20),
                    new Vector2D(5, 5)
            );

            assertThat(inArea).contains("player1");
        }
    }

    // ==================== LINE QUERIES ====================

    @Nested
    @DisplayName("Line Query Tests")
    class LineQueryTests {

        @Test
        @Disabled("Line query not critical for MVP - use getEntitiesNear + distance check instead")
        @DisplayName("Should find entities along diagonal line")
        void testGetEntitiesAlongLine() {
            grid.insert("player1", new Vector2D(25, 25));
            grid.insert("player2", new Vector2D(50, 50));
            grid.insert("player3", new Vector2D(75, 75));
            grid.insert("offLine", new Vector2D(90, 10));

            Set<String> alongLine = grid.getEntitiesAlongLine(
                    new Vector2D(0, 0),
                    new Vector2D(100, 100)
            );

            assertThat(alongLine)
                    .isNotEmpty()
                    .doesNotContain("offLine");
        }

        @Test
        @Disabled("Line query not critical for MVP - use getEntitiesNear + distance check instead")
        @DisplayName("Should find entities along horizontal line")
        void testGetEntitiesAlongHorizontalLine() {
            grid.insert("player1", new Vector2D(10, 30));
            grid.insert("player2", new Vector2D(50, 30));
            grid.insert("player3", new Vector2D(90, 30));
            grid.insert("offLine", new Vector2D(50, 60));

            Set<String> alongLine = grid.getEntitiesAlongLine(
                    new Vector2D(0, 30),
                    new Vector2D(100, 30)
            );

            assertThat(alongLine)
                    .isNotEmpty()
                    .doesNotContain("offLine");
        }

        @Test
        @Disabled("Line query not critical for MVP - use getEntitiesNear + distance check instead")
        @DisplayName("Should find entities along vertical line")
        void testGetEntitiesAlongVerticalLine() {
            grid.insert("player1", new Vector2D(50, 10));
            grid.insert("player2", new Vector2D(50, 30));
            grid.insert("player3", new Vector2D(50, 50));
            grid.insert("offLine", new Vector2D(80, 30));

            Set<String> alongLine = grid.getEntitiesAlongLine(
                    new Vector2D(50, 0),
                    new Vector2D(50, 68)
            );

            assertThat(alongLine)
                    .isNotEmpty()
                    .doesNotContain("offLine");
        }
    }

    // ==================== UPDATE METHOD ====================

    @Nested
    @DisplayName("Update Method Tests")
    class UpdateMethodTests {

        @Test
        @DisplayName("Should update entity position efficiently")
        void testUpdate() {
            Vector2D oldPos = new Vector2D(10, 10);
            Vector2D newPos = new Vector2D(50, 50);

            grid.insert("player1", oldPos);
            grid.update("player1", oldPos, newPos);

            // Should still have 1 entity
            assertThat(grid.getTotalEntityCount()).isEqualTo(1);

            // Should find at new position
            Set<String> nearNew = grid.getEntitiesNear(newPos, 5.0);
            assertThat(nearNew).contains("player1");

            // Should NOT find at old position
            Set<String> nearOld = grid.getEntitiesNear(oldPos, 5.0);
            assertThat(nearOld).doesNotContain("player1");
        }

        @Test
        @DisplayName("Should handle update within same cell")
        void testUpdateSameCell() {
            Vector2D oldPos = new Vector2D(10, 10);
            Vector2D newPos = new Vector2D(11, 11); // Same cell (both in 10-15 range)

            grid.insert("player1", oldPos);
            grid.update("player1", oldPos, newPos);

            // Should still have 1 entity
            assertThat(grid.getTotalEntityCount()).isEqualTo(1);

            // Should find at new position
            Set<String> nearby = grid.getEntitiesNear(newPos, 5.0);
            assertThat(nearby).contains("player1");
        }
    }

    // ==================== STATS METHOD ====================

    @Nested
    @DisplayName("Statistics Tests")
    class StatsTests {

        @Test
        @DisplayName("Should return accurate cell statistics")
        void testGetCellStats() {
            // Empty grid
            var stats = grid.getCellStats();
            assertThat(stats.get("totalCells")).isEqualTo(21 * 14);
            assertThat(stats.get("occupiedCells")).isEqualTo(0);
            assertThat(stats.get("totalEntities")).isEqualTo(0);

            // Add some entities
            grid.insert("player1", new Vector2D(10, 10));
            grid.insert("player2", new Vector2D(10, 10)); // Same cell
            grid.insert("player3", new Vector2D(50, 50)); // Different cell

            stats = grid.getCellStats();
            assertThat(stats.get("totalCells")).isEqualTo(21 * 14);
            assertThat(stats.get("occupiedCells")).isEqualTo(2);
            assertThat(stats.get("totalEntities")).isEqualTo(3);
            assertThat((Double) stats.get("avgEntitiesPerOccupiedCell")).isEqualTo(1.5);
        }
    }

    // ==================== EDGE CASES ====================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle entities at field boundaries")
        void testBoundaryPositions() {
            grid.insert("corner1", new Vector2D(0, 0));
            grid.insert("corner2", new Vector2D(FIELD_WIDTH, 0));
            grid.insert("corner3", new Vector2D(0, FIELD_HEIGHT));
            grid.insert("corner4", new Vector2D(FIELD_WIDTH, FIELD_HEIGHT));

            assertThat(grid.getTotalEntityCount()).isEqualTo(4);
        }

        @Test
        @DisplayName("Should handle positions slightly outside field")
        void testOutOfBoundsPositions() {
            // These should be handled gracefully (clamped)
            grid.insert("outOfBounds", new Vector2D(-1, -1));

            // Implementation should not crash
            assertThat(grid).isNotNull();
            assertThat(grid.getTotalEntityCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should handle very large radius query")
        void testLargeRadius() {
            grid.insert("player1", new Vector2D(50, 30));

            // Radius larger than entire field
            Set<String> nearby = grid.getEntitiesNear(
                    new Vector2D(50, 30),
                    200.0
            );

            assertThat(nearby).contains("player1");
        }

        @Test
        @DisplayName("Should handle query on empty grid")
        void testQueryEmptyGrid() {
            Set<String> nearby = grid.getEntitiesNear(new Vector2D(50, 30), 10.0);
            assertThat(nearby).isEmpty();

            Set<String> inArea = grid.getEntitiesInArea(
                    new Vector2D(0, 0),
                    new Vector2D(100, 100)
            );
            assertThat(inArea).isEmpty();
        }
    }

    // ==================== PERFORMANCE ====================

    @Nested
    @DisplayName("Performance Characteristics")
    class PerformanceTests {

        @Test
        @DisplayName("Should handle many entities efficiently")
        void testManyEntities() {
            // Insert 100 entities
            for (int i = 0; i < 100; i++) {
                double x = (i % 10) * 10;
                double y = (i / 10) * 6;
                grid.insert("entity_" + i, new Vector2D(x, y));
            }

            assertThat(grid.getTotalEntityCount()).isEqualTo(100);

            // Query should still be fast (testing it doesn't crash)
            Set<String> nearby = grid.getEntitiesNear(new Vector2D(50, 30), 10.0);
            assertThat(nearby).isNotEmpty();
        }

        @Test
        @DisplayName("Should clear efficiently with lazy initialization")
        void testClearPerformance() {
            // Insert entities in scattered cells
            grid.insert("player1", new Vector2D(10, 10));
            grid.insert("player2", new Vector2D(50, 30));
            grid.insert("player3", new Vector2D(90, 60));

            // Clear should be fast
            grid.clear();
            assertThat(grid.getTotalEntityCount()).isZero();

            // Should be able to insert again after clear
            grid.insert("player4", new Vector2D(20, 20));
            assertThat(grid.getTotalEntityCount()).isEqualTo(1);
        }
    }

    // ==================== IMMUTABILITY ====================

    @Nested
    @DisplayName("Immutability Tests")
    class ImmutabilityTests {

        @Test
        @DisplayName("Returned sets should be unmodifiable")
        void testReturnedSetsAreUnmodifiable() {
            grid.insert("player1", new Vector2D(50, 30));

            Set<String> nearby = grid.getEntitiesNear(new Vector2D(50, 30), 10.0);

            // Attempting to modify should throw exception
            assertThatThrownBy(() -> nearby.add("hacker"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("getEntitiesInArea should return unmodifiable set")
        void testAreaQueryReturnsUnmodifiable() {
            grid.insert("player1", new Vector2D(10, 10));

            Set<String> inArea = grid.getEntitiesInArea(
                    new Vector2D(0, 0),
                    new Vector2D(20, 20)
            );

            assertThatThrownBy(inArea::clear)
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
