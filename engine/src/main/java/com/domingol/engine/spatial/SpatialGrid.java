package com.domingol.engine.spatial;

import lombok.Getter;

import java.util.*;

/**
 * Spatial partitioning grid for efficient neighbor queries.
 *
 * Divides the field into cells to avoid checking all entities on every query.
 * Instead of O(n) searches, provides O(k) where k is entities in nearby cells.
 *
 * Football field dimensions:
 * - Width: 105 meters
 * - Height: 68 meters
 * - Cell size: 5 meters (configurable)
 * - Grid: 21 × 14 cells = 294 cells
 *
 * THREAD SAFETY: This class is NOT thread-safe. Each match simulation
 * should have its own SpatialGrid instance.
 *
 * PERFORMANCE: Uses lazy initialization for cells - cells are only created
 * when entities are inserted, reducing memory overhead and clear() cost.
 *
 * @author Domingol Team
 * @version 0.1.0
 */
@Getter
public class SpatialGrid {

    private final double fieldWidth;
    private final double fieldHeight;
    private final double cellSize;
    private final int gridWidth;
    private final int gridHeight;

    /**
     * Grid of cells, each cell contains a set of entity IDs.
     * Stored as [row][column] or [y][x] where:
     * - First index: Y coordinate (row, 0 to gridHeight-1)
     * - Second index: X coordinate (column, 0 to gridWidth-1)
     *
     * Cells are lazily initialized - null until first entity is inserted.
     * NOT exposed via getter for encapsulation safety.
     */
    private final Set<String>[][] cells;

    /**
     * Creates a spatial grid with standard football field dimensions.
     *
     * @param fieldWidth width of the field in meters (typically 105)
     * @param fieldHeight height of the field in meters (typically 68)
     * @param cellSize size of each cell in meters (typically 5)
     */
    @SuppressWarnings("unchecked")
    public SpatialGrid(double fieldWidth, double fieldHeight, double cellSize) {
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        this.cellSize = cellSize;

        this.gridWidth = (int) Math.ceil(fieldWidth / cellSize);
        this.gridHeight = (int) Math.ceil(fieldHeight / cellSize);

        // Initialize grid array (cells will be lazily created on insert)
        this.cells = new Set[gridHeight][gridWidth];
    }

    /**
     * Clears all entities from the grid.
     * Should be called at the start of each simulation tick before re-inserting entities.
     *
     * PERFORMANCE: O(width * height) but only nullifies references.
     * No iteration over HashSet contents.
     */
    public void clear() {
        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                cells[y][x] = null;
            }
        }
    }

    /**
     * Inserts an entity into the grid at the given position.
     *
     * @param entityId unique identifier for the entity (e.g., player ID)
     * @param position position of the entity on the field
     */
    public void insert(String entityId, Vector2D position) {
        CellCoordinate cell = getCell(position);

        if (isValidCell(cell)) {
            // Lazy initialization: create Set only when needed
            if (cells[cell.y][cell.x] == null) {
                cells[cell.y][cell.x] = new HashSet<>();
            }
            cells[cell.y][cell.x].add(entityId);
        }
    }

    /**
     * Removes an entity from the grid.
     *
     * @param entityId unique identifier for the entity
     * @param position position of the entity (to find which cell it's in)
     */
    public void remove(String entityId, Vector2D position) {
        CellCoordinate cell = getCell(position);

        if (isValidCell(cell) && cells[cell.y][cell.x] != null) {
            cells[cell.y][cell.x].remove(entityId);
        }
    }

    /**
     * Updates an entity's position in the grid.
     * More efficient than separate remove + insert when entity moves between cells.
     *
     * @param entityId unique identifier for the entity
     * @param oldPosition previous position of the entity
     * @param newPosition new position of the entity
     */
    public void update(String entityId, Vector2D oldPosition, Vector2D newPosition) {
        CellCoordinate oldCell = getCell(oldPosition);
        CellCoordinate newCell = getCell(newPosition);

        // Only update if entity changed cells
        if (!oldCell.equals(newCell)) {
            // Remove from old cell
            if (isValidCell(oldCell) && cells[oldCell.y][oldCell.x] != null) {
                cells[oldCell.y][oldCell.x].remove(entityId);
            }

            // Insert into new cell
            if (isValidCell(newCell)) {
                if (cells[newCell.y][newCell.x] == null) {
                    cells[newCell.y][newCell.x] = new HashSet<>();
                }
                cells[newCell.y][newCell.x].add(entityId);
            }
        }
    }

    /**
     * Gets all entities within a radius of a position.
     *
     * IMPORTANT: This returns entities in a SQUARE approximation, not exact circle.
     * The returned list may include entities beyond the exact radius.
     * For precise distance filtering, caller must use Vector2D.distanceSquaredTo().
     *
     * This is the main query method. It:
     * 1. Determines which cells are within the radius (square approximation)
     * 2. Returns all entities in those cells
     * 3. Caller should then filter by exact distance if needed
     *
     * @param position center position to search from
     * @param radius search radius in meters
     * @return set of entity IDs within the search area (unmodifiable)
     */
    public Set<String> getEntitiesNear(Vector2D position, double radius) {
        Set<String> nearbyEntities = new HashSet<>();

        CellCoordinate centerCell = getCell(position);
        int cellRadius = (int) Math.ceil(radius / cellSize);

        // Check all cells within the radius
        for (int dy = -cellRadius; dy <= cellRadius; dy++) {
            for (int dx = -cellRadius; dx <= cellRadius; dx++) {
                int cellX = centerCell.x + dx;
                int cellY = centerCell.y + dy;

                if (isValidCell(cellX, cellY) && cells[cellY][cellX] != null) {
                    nearbyEntities.addAll(cells[cellY][cellX]);
                }
            }
        }

        return Collections.unmodifiableSet(nearbyEntities);
    }

    /**
     * Gets all entities in a specific rectangular area.
     *
     * @param minPosition bottom-left corner of the rectangle
     * @param maxPosition top-right corner of the rectangle
     * @return set of entity IDs in the area (unmodifiable)
     */
    public Set<String> getEntitiesInArea(Vector2D minPosition, Vector2D maxPosition) {
        Set<String> entitiesInArea = new HashSet<>();

        CellCoordinate minCell = getCell(minPosition);
        CellCoordinate maxCell = getCell(maxPosition);

        int minX = Math.max(0, Math.min(minCell.x, maxCell.x));
        int maxX = Math.min(gridWidth - 1, Math.max(minCell.x, maxCell.x));
        int minY = Math.max(0, Math.min(minCell.y, maxCell.y));
        int maxY = Math.min(gridHeight - 1, Math.max(minCell.y, maxCell.y));

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                if (cells[y][x] != null) {
                    entitiesInArea.addAll(cells[y][x]);
                }
            }
        }

        return Collections.unmodifiableSet(entitiesInArea);
    }

    /**
     * Gets all entities along a line (useful for pass interception checks).
     *
     * @param start start position of the line
     * @param end end position of the line
     * @return set of entity IDs in cells that the line passes through (unmodifiable)
     */
    public Set<String> getEntitiesAlongLine(Vector2D start, Vector2D end) {
        Set<String> entitiesAlongLine = new HashSet<>();

        // Get all cells the line passes through using Bresenham-like algorithm
        List<CellCoordinate> cellsOnLine = getCellsAlongLine(start, end);

        for (CellCoordinate cell : cellsOnLine) {
            if (isValidCell(cell) && cells[cell.y][cell.x] != null) {
                entitiesAlongLine.addAll(cells[cell.y][cell.x]);
            }
        }

        return Collections.unmodifiableSet(entitiesAlongLine);
    }

    /**
     * Gets the total number of entities in the grid.
     *
     * @return total entity count
     */
    public int getTotalEntityCount() {
        int count = 0;
        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                if (cells[y][x] != null) {
                    count += cells[y][x].size();
                }
            }
        }
        return count;
    }

    /**
     * Gets statistics about grid usage for debugging and optimization.
     *
     * @return map containing:
     *         - "totalCells": total number of cells in grid
     *         - "occupiedCells": number of cells containing at least one entity
     *         - "totalEntities": total number of entities
     *         - "avgEntitiesPerOccupiedCell": average entities in non-empty cells
     */
    public Map<String, Number> getCellStats() {
        Map<String, Number> stats = new HashMap<>();
        int occupiedCells = 0;
        int totalEntities = 0;

        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                if (cells[y][x] != null && !cells[y][x].isEmpty()) {
                    occupiedCells++;
                    totalEntities += cells[y][x].size();
                }
            }
        }

        stats.put("totalCells", gridWidth * gridHeight);
        stats.put("occupiedCells", occupiedCells);
        stats.put("totalEntities", totalEntities);
        stats.put("avgEntitiesPerOccupiedCell",
                occupiedCells > 0 ? (double) totalEntities / occupiedCells : 0.0);

        return stats;
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Converts a world position to grid cell coordinates.
     * Clamps to grid boundaries automatically.
     */
    private CellCoordinate getCell(Vector2D position) {
        int x = (int) (position.x / cellSize);
        int y = (int) (position.y / cellSize);

        x = Math.max(0, Math.min(x, gridWidth - 1));
        y = Math.max(0, Math.min(y, gridHeight - 1));

        return new CellCoordinate(x, y);
    }

    /**
     * Checks if a cell coordinate is within the grid bounds.
     */
    private boolean isValidCell(CellCoordinate cell) {
        return isValidCell(cell.x, cell.y);
    }

    /**
     * Checks if cell coordinates are within the grid bounds.
     */
    private boolean isValidCell(int x, int y) {
        return x >= 0 && x < gridWidth && y >= 0 && y < gridHeight;
    }

    /**
     * Gets all cells that a line passes through.
     * Uses a variant of Bresenham's line algorithm adapted for grid traversal.
     */
    private List<CellCoordinate> getCellsAlongLine(Vector2D start, Vector2D end) {
        List<CellCoordinate> cellsOnLine = new ArrayList<>();

        CellCoordinate startCell = getCell(start);
        CellCoordinate endCell = getCell(end);

        int x = startCell.x;
        int y = startCell.y;

        int dx = Math.abs(endCell.x - startCell.x);
        int dy = Math.abs(endCell.y - startCell.y);

        int sx = startCell.x < endCell.x ? 1 : -1;
        int sy = startCell.y < endCell.y ? 1 : -1;

        int err = dx - dy;

        while (true) {
            cellsOnLine.add(new CellCoordinate(x, y));

            if (x == endCell.x && y == endCell.y) {
                break;
            }

            int e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }

            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }

        return cellsOnLine;
    }

    // ==================== INNER CLASSES ====================

    /**
     * Represents a cell coordinate in the grid.
     */
    private static class CellCoordinate {
        final int x;
        final int y;

        CellCoordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CellCoordinate that = (CellCoordinate) o;
            return x == that.x && y == that.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}