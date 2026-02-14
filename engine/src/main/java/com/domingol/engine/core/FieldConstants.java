package com.domingol.engine.core;

import com.domingol.engine.spatial.Vector2D;

/**
 * Constants for football field dimensions and key positions.
 * <p>
 * Standard field: 105m x 68m
 * Goals: 7.32m wide, centered at y=34m
 */
public final class FieldConstants {

    // Prevent instantiation
    private FieldConstants() {}

    // === FIELD DIMENSIONS ===

    public static final double FIELD_LENGTH = 105.0;  // meters
    public static final double FIELD_WIDTH = 68.0;    // meters

    public static final Vector2D FIELD_CENTER = new Vector2D(52.5, 34.0);

    // === GOALS ===

    public static final double GOAL_WIDTH = 7.32;  // meters
    public static final double GOAL_Y_CENTER = 34.0;
    public static final double GOAL_Y_MIN = GOAL_Y_CENTER - (GOAL_WIDTH / 2);  // 30.34
    public static final double GOAL_Y_MAX = GOAL_Y_CENTER + (GOAL_WIDTH / 2);  // 37.66

    // Home team attacks right (towards x=105)
    public static final Vector2D HOME_GOAL_CENTER = new Vector2D(0, GOAL_Y_CENTER);
    public static final Vector2D HOME_GOAL_LEFT_POST = new Vector2D(0, GOAL_Y_MIN);
    public static final Vector2D HOME_GOAL_RIGHT_POST = new Vector2D(0, GOAL_Y_MAX);

    // Away team attacks left (towards x=0)
    public static final Vector2D AWAY_GOAL_CENTER = new Vector2D(FIELD_LENGTH, GOAL_Y_CENTER);
    public static final Vector2D AWAY_GOAL_LEFT_POST = new Vector2D(FIELD_LENGTH, GOAL_Y_MIN);
    public static final Vector2D AWAY_GOAL_RIGHT_POST = new Vector2D(FIELD_LENGTH, GOAL_Y_MAX);

    // === PENALTY AREAS ===

    public static final double PENALTY_AREA_LENGTH = 16.5;  // meters from goal line
    public static final double PENALTY_AREA_WIDTH = 40.32;  // meters

    // Home penalty area (defending x=0)
    public static final Vector2D HOME_PENALTY_AREA_MIN = new Vector2D(0, (FIELD_WIDTH - PENALTY_AREA_WIDTH) / 2);
    public static final Vector2D HOME_PENALTY_AREA_MAX = new Vector2D(PENALTY_AREA_LENGTH, (FIELD_WIDTH + PENALTY_AREA_WIDTH) / 2);

    // Away penalty area (defending x=105)
    public static final Vector2D AWAY_PENALTY_AREA_MIN = new Vector2D(FIELD_LENGTH - PENALTY_AREA_LENGTH, (FIELD_WIDTH - PENALTY_AREA_WIDTH) / 2);
    public static final Vector2D AWAY_PENALTY_AREA_MAX = new Vector2D(FIELD_LENGTH, (FIELD_WIDTH + PENALTY_AREA_WIDTH) / 2);
}
