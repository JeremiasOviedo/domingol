package com.domingol.engine.spatial;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for GeometryUtils
 */
class GeometryUtilsTest {

    private static final double EPSILON = 0.0001;

    // ==================== CONSTRUCTION ====================

    @Test
    @DisplayName("Should not allow instantiation")
    void testCannotInstantiate() {
        assertThatThrownBy(() -> {
            var constructor = GeometryUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }).hasCauseInstanceOf(AssertionError.class);
    }

    // ==================== DISTANCE TO LINE ====================

    @Nested
    @DisplayName("Distance to Line Tests")
    class DistanceToLineTests {

        @Test
        @DisplayName("Should calculate perpendicular distance to horizontal line")
        void testDistanceToHorizontalLine() {
            Vector2D point = new Vector2D(50, 30);
            Vector2D lineStart = new Vector2D(0, 20);
            Vector2D lineEnd = new Vector2D(100, 20);

            double distance = GeometryUtils.distanceToLine(point, lineStart, lineEnd);

            assertThat(distance).isCloseTo(10.0, within(EPSILON));
        }

        @Test
        @DisplayName("Should calculate perpendicular distance to vertical line")
        void testDistanceToVerticalLine() {
            Vector2D point = new Vector2D(50, 30);
            Vector2D lineStart = new Vector2D(40, 0);
            Vector2D lineEnd = new Vector2D(40, 100);

            double distance = GeometryUtils.distanceToLine(point, lineStart, lineEnd);

            assertThat(distance).isCloseTo(10.0, within(EPSILON));
        }

        @Test
        @DisplayName("Should calculate distance to diagonal line")
        void testDistanceToDiagonalLine() {
            Vector2D point = new Vector2D(5, 5);
            Vector2D lineStart = new Vector2D(0, 0);
            Vector2D lineEnd = new Vector2D(10, 0);

            double distance = GeometryUtils.distanceToLine(point, lineStart, lineEnd);

            assertThat(distance).isCloseTo(5.0, within(EPSILON));
        }

        @Test
        @DisplayName("Should return zero when point is on line")
        void testPointOnLine() {
            Vector2D point = new Vector2D(50, 30);
            Vector2D lineStart = new Vector2D(0, 30);
            Vector2D lineEnd = new Vector2D(100, 30);

            double distance = GeometryUtils.distanceToLine(point, lineStart, lineEnd);

            assertThat(distance).isCloseTo(0.0, within(EPSILON));
        }

        @Test
        @DisplayName("Should handle degenerate line (two identical points)")
        void testDegenerateLine() {
            Vector2D point = new Vector2D(10, 10);
            Vector2D linePoint = new Vector2D(5, 5);

            double distance = GeometryUtils.distanceToLine(point, linePoint, linePoint);

            // Should return distance to that single point
            assertThat(distance).isCloseTo(point.distanceTo(linePoint), within(EPSILON));
        }
    }

    // ==================== DISTANCE TO LINE SEGMENT ====================

    @Nested
    @DisplayName("Distance to Line Segment Tests")
    class DistanceToSegmentTests {

        @Test
        @DisplayName("Should calculate distance to segment when perpendicular falls within segment")
        void testDistanceWithinSegment() {
            Vector2D point = new Vector2D(50, 30);
            Vector2D segmentStart = new Vector2D(40, 20);
            Vector2D segmentEnd = new Vector2D(60, 20);

            double distance = GeometryUtils.distanceToLineSegment(point, segmentStart, segmentEnd);

            assertThat(distance).isCloseTo(10.0, within(EPSILON));
        }

        @Test
        @DisplayName("Should return distance to nearest endpoint when perpendicular falls outside")
        void testDistanceToEndpoint() {
            Vector2D point = new Vector2D(10, 10);
            Vector2D segmentStart = new Vector2D(50, 20);
            Vector2D segmentEnd = new Vector2D(60, 20);

            double distance = GeometryUtils.distanceToLineSegment(point, segmentStart, segmentEnd);

            // Should be distance to segmentStart
            assertThat(distance).isCloseTo(point.distanceTo(segmentStart), within(EPSILON));
        }

        @Test
        @DisplayName("Should handle point on segment")
        void testPointOnSegment() {
            Vector2D point = new Vector2D(50, 20);
            Vector2D segmentStart = new Vector2D(40, 20);
            Vector2D segmentEnd = new Vector2D(60, 20);

            double distance = GeometryUtils.distanceToLineSegment(point, segmentStart, segmentEnd);

            assertThat(distance).isCloseTo(0.0, within(EPSILON));
        }

        @Test
        @DisplayName("Squared distance should match distance²")
        void testSquaredDistance() {
            Vector2D point = new Vector2D(50, 30);
            Vector2D segmentStart = new Vector2D(40, 20);
            Vector2D segmentEnd = new Vector2D(60, 20);

            double distance = GeometryUtils.distanceToLineSegment(point, segmentStart, segmentEnd);
            double distanceSq = GeometryUtils.distanceSquaredToLineSegment(point, segmentStart, segmentEnd);

            assertThat(distanceSq).isCloseTo(distance * distance, within(EPSILON));
        }
    }

    // ==================== CLOSEST POINT ON SEGMENT ====================

    @Nested
    @DisplayName("Closest Point on Segment Tests")
    class ClosestPointTests {

        @Test
        @DisplayName("Should find closest point when perpendicular is within segment")
        void testClosestPointWithin() {
            Vector2D point = new Vector2D(50, 30);
            Vector2D segmentStart = new Vector2D(40, 20);
            Vector2D segmentEnd = new Vector2D(60, 20);

            Vector2D closest = GeometryUtils.closestPointOnLineSegment(point, segmentStart, segmentEnd);

            assertThat(closest.x).isCloseTo(50.0, within(EPSILON));
            assertThat(closest.y).isCloseTo(20.0, within(EPSILON));
        }

        @Test
        @DisplayName("Should return start point when closest point is before segment")
        void testClosestPointBeforeSegment() {
            Vector2D point = new Vector2D(10, 10);
            Vector2D segmentStart = new Vector2D(50, 50);
            Vector2D segmentEnd = new Vector2D(60, 50);

            Vector2D closest = GeometryUtils.closestPointOnLineSegment(point, segmentStart, segmentEnd);

            assertThat(closest).isEqualTo(segmentStart);
        }

        @Test
        @DisplayName("Should return end point when closest point is after segment")
        void testClosestPointAfterSegment() {
            Vector2D point = new Vector2D(100, 100);
            Vector2D segmentStart = new Vector2D(50, 50);
            Vector2D segmentEnd = new Vector2D(60, 50);

            Vector2D closest = GeometryUtils.closestPointOnLineSegment(point, segmentStart, segmentEnd);

            assertThat(closest).isEqualTo(segmentEnd);
        }

        @Test
        @DisplayName("Should handle degenerate segment")
        void testDegenerateSegment() {
            Vector2D point = new Vector2D(10, 10);
            Vector2D segmentPoint = new Vector2D(5, 5);

            Vector2D closest = GeometryUtils.closestPointOnLineSegment(point, segmentPoint, segmentPoint);

            assertThat(closest).isEqualTo(segmentPoint);
        }
    }

    // ==================== LINE SEGMENT INTERSECTION ====================

    @Nested
    @DisplayName("Line Segment Intersection Tests")
    class SegmentIntersectionTests {

        @Test
        @DisplayName("Should detect intersection of crossing segments")
        void testIntersectingSegments() {
            Vector2D seg1Start = new Vector2D(0, 0);
            Vector2D seg1End = new Vector2D(10, 10);
            Vector2D seg2Start = new Vector2D(0, 10);
            Vector2D seg2End = new Vector2D(10, 0);

            boolean intersects = GeometryUtils.lineSegmentsIntersect(seg1Start, seg1End, seg2Start, seg2End);

            assertThat(intersects).isTrue();
        }

        @Test
        @DisplayName("Should detect no intersection for non-crossing segments")
        void testNonIntersectingSegments() {
            Vector2D seg1Start = new Vector2D(0, 0);
            Vector2D seg1End = new Vector2D(10, 0);
            Vector2D seg2Start = new Vector2D(0, 10);
            Vector2D seg2End = new Vector2D(10, 10);

            boolean intersects = GeometryUtils.lineSegmentsIntersect(seg1Start, seg1End, seg2Start, seg2End);

            assertThat(intersects).isFalse();
        }

        @Test
        @DisplayName("Should detect no intersection for parallel segments")
        void testParallelSegments() {
            Vector2D seg1Start = new Vector2D(0, 0);
            Vector2D seg1End = new Vector2D(10, 0);
            Vector2D seg2Start = new Vector2D(0, 5);
            Vector2D seg2End = new Vector2D(10, 5);

            boolean intersects = GeometryUtils.lineSegmentsIntersect(seg1Start, seg1End, seg2Start, seg2End);

            assertThat(intersects).isFalse();
        }

        @Test
        @DisplayName("Should find intersection point of crossing segments")
        void testIntersectionPoint() {
            Vector2D seg1Start = new Vector2D(0, 0);
            Vector2D seg1End = new Vector2D(10, 10);
            Vector2D seg2Start = new Vector2D(0, 10);
            Vector2D seg2End = new Vector2D(10, 0);

            Vector2D intersection = GeometryUtils.lineSegmentIntersectionPoint(
                    seg1Start, seg1End, seg2Start, seg2End);

            assertThat(intersection).isNotNull();
            assertThat(intersection.x).isCloseTo(5.0, within(EPSILON));
            assertThat(intersection.y).isCloseTo(5.0, within(EPSILON));
        }

        @Test
        @DisplayName("Should return null for non-intersecting segments")
        void testNoIntersectionPoint() {
            Vector2D seg1Start = new Vector2D(0, 0);
            Vector2D seg1End = new Vector2D(10, 0);
            Vector2D seg2Start = new Vector2D(0, 10);
            Vector2D seg2End = new Vector2D(10, 10);

            Vector2D intersection = GeometryUtils.lineSegmentIntersectionPoint(
                    seg1Start, seg1End, seg2Start, seg2End);

            assertThat(intersection).isNull();
        }
    }

    // ==================== CONTAINMENT TESTS ====================

    @Nested
    @DisplayName("Containment Tests")
    class ContainmentTests {

        @Test
        @DisplayName("Should detect point inside circle")
        void testPointInCircle() {
            Vector2D point = new Vector2D(5, 5);
            Vector2D center = new Vector2D(0, 0);
            double radius = 10.0;

            boolean inside = GeometryUtils.isPointInCircle(point, center, radius);

            assertThat(inside).isTrue();
        }

        @Test
        @DisplayName("Should detect point outside circle")
        void testPointOutsideCircle() {
            Vector2D point = new Vector2D(20, 20);
            Vector2D center = new Vector2D(0, 0);
            double radius = 10.0;

            boolean inside = GeometryUtils.isPointInCircle(point, center, radius);

            assertThat(inside).isFalse();
        }

        @Test
        @DisplayName("Should detect point on circle edge")
        void testPointOnCircleEdge() {
            Vector2D point = new Vector2D(10, 0);
            Vector2D center = new Vector2D(0, 0);
            double radius = 10.0;

            boolean inside = GeometryUtils.isPointInCircle(point, center, radius);

            assertThat(inside).isTrue();
        }

        @Test
        @DisplayName("Should detect point inside rectangle")
        void testPointInRectangle() {
            Vector2D point = new Vector2D(50, 30);
            Vector2D min = new Vector2D(0, 0);
            Vector2D max = new Vector2D(100, 60);

            boolean inside = GeometryUtils.isPointInRectangle(point, min, max);

            assertThat(inside).isTrue();
        }

        @Test
        @DisplayName("Should detect point outside rectangle")
        void testPointOutsideRectangle() {
            Vector2D point = new Vector2D(150, 30);
            Vector2D min = new Vector2D(0, 0);
            Vector2D max = new Vector2D(100, 60);

            boolean inside = GeometryUtils.isPointInRectangle(point, min, max);

            assertThat(inside).isFalse();
        }

        @Test
        @DisplayName("Should detect point on rectangle boundary")
        void testPointOnRectangleBoundary() {
            Vector2D point = new Vector2D(100, 30);
            Vector2D min = new Vector2D(0, 0);
            Vector2D max = new Vector2D(100, 60);

            boolean inside = GeometryUtils.isPointInRectangle(point, min, max);

            assertThat(inside).isTrue();
        }

        @Test
        @DisplayName("Should detect point inside triangle")
        void testPointInTriangle() {
            Vector2D point = new Vector2D(5, 5);
            Vector2D v1 = new Vector2D(0, 0);
            Vector2D v2 = new Vector2D(10, 0);
            Vector2D v3 = new Vector2D(5, 10);

            boolean inside = GeometryUtils.isPointInTriangle(point, v1, v2, v3);

            assertThat(inside).isTrue();
        }

        @Test
        @DisplayName("Should detect point outside triangle")
        void testPointOutsideTriangle() {
            Vector2D point = new Vector2D(20, 20);
            Vector2D v1 = new Vector2D(0, 0);
            Vector2D v2 = new Vector2D(10, 0);
            Vector2D v3 = new Vector2D(5, 10);

            boolean inside = GeometryUtils.isPointInTriangle(point, v1, v2, v3);

            assertThat(inside).isFalse();
        }
    }

    // ==================== CLAMPING ====================

    @Nested
    @DisplayName("Clamping Tests")
    class ClampingTests {

        @Test
        @DisplayName("Should clamp point to rectangle")
        void testClampToRectangle() {
            Vector2D point = new Vector2D(150, 80);
            Vector2D min = new Vector2D(0, 0);
            Vector2D max = new Vector2D(105, 68);

            Vector2D clamped = GeometryUtils.clampToRectangle(point, min, max);

            assertThat(clamped.x).isEqualTo(105.0);
            assertThat(clamped.y).isEqualTo(68.0);
        }

        @Test
        @DisplayName("Should not modify point already inside rectangle")
        void testClampInsideRectangle() {
            Vector2D point = new Vector2D(50, 30);
            Vector2D min = new Vector2D(0, 0);
            Vector2D max = new Vector2D(105, 68);

            Vector2D clamped = GeometryUtils.clampToRectangle(point, min, max);

            assertThat(clamped).isEqualTo(point);
        }

        @Test
        @DisplayName("Should clamp point to circle edge")
        void testClampToCircle() {
            Vector2D point = new Vector2D(20, 0);
            Vector2D center = new Vector2D(0, 0);
            double radius = 10.0;

            Vector2D clamped = GeometryUtils.clampToCircle(point, center, radius);

            assertThat(clamped.x).isCloseTo(10.0, within(EPSILON));
            assertThat(clamped.y).isCloseTo(0.0, within(EPSILON));
        }

        @Test
        @DisplayName("Should not modify point already inside circle")
        void testClampInsideCircle() {
            Vector2D point = new Vector2D(5, 0);
            Vector2D center = new Vector2D(0, 0);
            double radius = 10.0;

            Vector2D clamped = GeometryUtils.clampToCircle(point, center, radius);

            assertThat(clamped).isEqualTo(point);
        }
    }

    // ==================== ANGLE CALCULATIONS ====================

    @Nested
    @DisplayName("Angle Calculation Tests")
    class AngleTests {

        @Test
        @DisplayName("Should calculate angle at right angle")
        void testRightAngle() {
            Vector2D pointA = new Vector2D(0, 10);
            Vector2D pointB = new Vector2D(0, 0);
            Vector2D pointC = new Vector2D(10, 0);

            double angle = GeometryUtils.angleBetweenPoints(pointA, pointB, pointC);

            assertThat(angle).isCloseTo(Math.PI / 2, within(EPSILON));
        }

        @Test
        @DisplayName("Should calculate signed angle")
        void testSignedAngle() {
            Vector2D pointA = new Vector2D(10, 0);
            Vector2D pointB = new Vector2D(0, 0);
            Vector2D pointC = new Vector2D(0, 10);

            double angle = GeometryUtils.signedAngleBetweenPoints(pointA, pointB, pointC);

            // Counter-clockwise rotation should be positive
            assertThat(angle).isGreaterThan(0);
        }

        @Test
        @DisplayName("Should calculate straight angle")
        void testStraightAngle() {
            Vector2D pointA = new Vector2D(-10, 0);
            Vector2D pointB = new Vector2D(0, 0);
            Vector2D pointC = new Vector2D(10, 0);

            double angle = GeometryUtils.angleBetweenPoints(pointA, pointB, pointC);

            assertThat(angle).isCloseTo(Math.PI, within(EPSILON));
        }
    }

    // ==================== TRIANGLE OPERATIONS ====================

    @Nested
    @DisplayName("Triangle Operation Tests")
    class TriangleTests {

        @Test
        @DisplayName("Should calculate triangle area")
        void testTriangleArea() {
            Vector2D v1 = new Vector2D(0, 0);
            Vector2D v2 = new Vector2D(10, 0);
            Vector2D v3 = new Vector2D(5, 10);

            double area = GeometryUtils.triangleArea(v1, v2, v3);

            assertThat(area).isCloseTo(50.0, within(EPSILON));
        }

        @Test
        @DisplayName("Should detect collinear points")
        void testCollinearPoints() {
            Vector2D p1 = new Vector2D(0, 0);
            Vector2D p2 = new Vector2D(5, 5);
            Vector2D p3 = new Vector2D(10, 10);

            boolean collinear = GeometryUtils.areCollinear(p1, p2, p3);

            assertThat(collinear).isTrue();
        }

        @Test
        @DisplayName("Should detect non-collinear points")
        void testNonCollinearPoints() {
            Vector2D p1 = new Vector2D(0, 0);
            Vector2D p2 = new Vector2D(10, 0);
            Vector2D p3 = new Vector2D(5, 10);

            boolean collinear = GeometryUtils.areCollinear(p1, p2, p3);

            assertThat(collinear).isFalse();
        }

        @Test
        @DisplayName("Should calculate triangle centroid")
        void testTriangleCentroid() {
            Vector2D v1 = new Vector2D(0, 0);
            Vector2D v2 = new Vector2D(9, 0);
            Vector2D v3 = new Vector2D(0, 9);

            Vector2D centroid = GeometryUtils.triangleCentroid(v1, v2, v3);

            assertThat(centroid.x).isCloseTo(3.0, within(EPSILON));
            assertThat(centroid.y).isCloseTo(3.0, within(EPSILON));
        }

        @Test
        @DisplayName("Should calculate triangle circumcenter")
        void testTriangleCircumcenter() {
            Vector2D v1 = new Vector2D(0, 0);
            Vector2D v2 = new Vector2D(10, 0);
            Vector2D v3 = new Vector2D(5, 5);

            Vector2D circumcenter = GeometryUtils.triangleCircumcenter(v1, v2, v3);

            assertThat(circumcenter).isNotNull();
            // For this triangle, circumcenter should be equidistant from all vertices
            double dist1 = circumcenter.distanceTo(v1);
            double dist2 = circumcenter.distanceTo(v2);
            double dist3 = circumcenter.distanceTo(v3);

            assertThat(dist1).isCloseTo(dist2, within(EPSILON));
            assertThat(dist2).isCloseTo(dist3, within(EPSILON));
        }

        @Test
        @DisplayName("Should return null circumcenter for collinear points")
        void testCircumcenterCollinear() {
            Vector2D v1 = new Vector2D(0, 0);
            Vector2D v2 = new Vector2D(5, 5);
            Vector2D v3 = new Vector2D(10, 10);

            Vector2D circumcenter = GeometryUtils.triangleCircumcenter(v1, v2, v3);

            assertThat(circumcenter).isNull();
        }
    }

    // ==================== RAYCAST ====================

    @Nested
    @DisplayName("Raycast Tests")
    class RaycastTests {

        @Test
        @DisplayName("Should detect ray hitting segment")
        void testRaycastHit() {
            Vector2D rayOrigin = new Vector2D(0, 5);
            Vector2D rayDirection = new Vector2D(1, 0);
            Vector2D segmentStart = new Vector2D(10, 0);
            Vector2D segmentEnd = new Vector2D(10, 10);

            double distance = GeometryUtils.raycastToSegment(rayOrigin, rayDirection, segmentStart, segmentEnd);

            assertThat(distance).isCloseTo(10.0, within(EPSILON));
        }

        @Test
        @DisplayName("Should detect ray missing segment")
        void testRaycastMiss() {
            Vector2D rayOrigin = new Vector2D(0, 5);
            Vector2D rayDirection = new Vector2D(1, 0);
            Vector2D segmentStart = new Vector2D(10, 20);
            Vector2D segmentEnd = new Vector2D(10, 30);

            double distance = GeometryUtils.raycastToSegment(rayOrigin, rayDirection, segmentStart, segmentEnd);

            assertThat(distance).isEqualTo(Double.POSITIVE_INFINITY);
        }

        @Test
        @DisplayName("Should return intersection point for ray hitting segment")
        void testRaycastIntersectionPoint() {
            Vector2D rayOrigin = new Vector2D(0, 5);
            Vector2D rayDirection = new Vector2D(1, 0);
            Vector2D segmentStart = new Vector2D(10, 0);
            Vector2D segmentEnd = new Vector2D(10, 10);

            Vector2D intersection = GeometryUtils.raycastIntersectionPoint(
                    rayOrigin, rayDirection, segmentStart, segmentEnd);

            assertThat(intersection).isNotNull();
            assertThat(intersection.x).isCloseTo(10.0, within(EPSILON));
            assertThat(intersection.y).isCloseTo(5.0, within(EPSILON));
        }

        @Test
        @DisplayName("Should return null for ray missing segment")
        void testRaycastNoIntersectionPoint() {
            Vector2D rayOrigin = new Vector2D(0, 5);
            Vector2D rayDirection = new Vector2D(1, 0);
            Vector2D segmentStart = new Vector2D(10, 20);
            Vector2D segmentEnd = new Vector2D(10, 30);

            Vector2D intersection = GeometryUtils.raycastIntersectionPoint(
                    rayOrigin, rayDirection, segmentStart, segmentEnd);

            assertThat(intersection).isNull();
        }

        @Test
        @DisplayName("Should not detect ray hitting behind origin")
        void testRaycastBehindOrigin() {
            Vector2D rayOrigin = new Vector2D(50, 5);
            Vector2D rayDirection = new Vector2D(1, 0);
            Vector2D segmentStart = new Vector2D(10, 0);
            Vector2D segmentEnd = new Vector2D(10, 10);

            double distance = GeometryUtils.raycastToSegment(rayOrigin, rayDirection, segmentStart, segmentEnd);

            assertThat(distance).isEqualTo(Double.POSITIVE_INFINITY);
        }
    }

    // ==================== FOOTBALL-SPECIFIC SCENARIOS ====================

    @Nested
    @DisplayName("Football Simulation Scenarios")
    class FootballScenarioTests {

        @Test
        @DisplayName("Pass interception: should detect if defender is close to pass line")
        void testPassInterception() {
            Vector2D passer = new Vector2D(30, 34);
            Vector2D receiver = new Vector2D(70, 34);
            Vector2D defender = new Vector2D(50, 36);

            double distanceToPass = GeometryUtils.distanceToLineSegment(defender, passer, receiver);

            // Defender is 2 meters from pass line
            assertThat(distanceToPass).isCloseTo(2.0, within(EPSILON));

            // If defender can reach within 3 meters, might intercept
            assertThat(distanceToPass).isLessThan(3.0);
        }

        @Test
        @DisplayName("Shooting angle: should calculate angle to goal")
        void testShootingAngle() {
            Vector2D shooter = new Vector2D(50, 34);
            Vector2D leftPost = new Vector2D(105, 30.34);  // Goal is 7.32m wide
            Vector2D rightPost = new Vector2D(105, 37.66);

            double shootingAngle = GeometryUtils.angleBetweenPoints(leftPost, shooter, rightPost);

            // Angle should be reasonable (not too narrow)
            assertThat(shootingAngle).isGreaterThan(0);
            assertThat(shootingAngle).isLessThan(Math.PI);
        }

        @Test
        @DisplayName("Offside check: should detect if player is in offside zone")
        void testOffsideZone() {
            Vector2D player = new Vector2D(95, 34);
            Vector2D offsideLine = new Vector2D(90, 0);
            Vector2D offsideLineEnd = new Vector2D(90, 68);

            // Is player beyond offside line?
            boolean isOffside = player.x > offsideLine.x;

            assertThat(isOffside).isTrue();
        }

        @Test
        @DisplayName("Penalty area check: should detect if player is in penalty area")
        void testPenaltyAreaCheck() {
            Vector2D player = new Vector2D(100, 34);
            
            // Standard penalty area: 16.5m from goal line, 40.32m wide (centered)
            Vector2D penaltyMin = new Vector2D(88.5, 13.84);
            Vector2D penaltyMax = new Vector2D(105, 54.16);

            boolean inPenaltyArea = GeometryUtils.isPointInRectangle(player, penaltyMin, penaltyMax);

            assertThat(inPenaltyArea).isTrue();
        }

        @Test
        @DisplayName("Player positioning: should find optimal support position on passing lane")
        void testSupportPositioning() {
            Vector2D ballCarrier = new Vector2D(50, 34);
            Vector2D attackDirection = new Vector2D(105, 34);
            Vector2D supportPlayer = new Vector2D(60, 40);

            // Find where support player should position on the passing lane
            Vector2D optimalPosition = GeometryUtils.closestPointOnLineSegment(
                    supportPlayer, ballCarrier, attackDirection);

            // Should be ahead of ball carrier
            assertThat(optimalPosition.x).isGreaterThan(ballCarrier.x);
        }

        @Test
        @DisplayName("Ball out of bounds: should clamp ball position to field")
        void testBallOutOfBounds() {
            Vector2D ballPosition = new Vector2D(110, 70);
            Vector2D fieldMin = new Vector2D(0, 0);
            Vector2D fieldMax = new Vector2D(105, 68);

            Vector2D clampedPosition = GeometryUtils.clampToRectangle(ballPosition, fieldMin, fieldMax);

            assertThat(clampedPosition.x).isEqualTo(105);
            assertThat(clampedPosition.y).isEqualTo(68);
        }
    }
}
