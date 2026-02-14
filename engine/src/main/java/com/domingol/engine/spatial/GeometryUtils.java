package com.domingol.engine.spatial;

/**
 * Geometric utility functions for 2D space operations.
 *
 * Provides common geometric calculations needed for football simulation:
 * - Distance calculations (point-to-line, point-to-segment)
 * - Intersection tests (line-line, segment-segment)
 * - Containment tests (point in rectangle, point in circle)
 * - Projection and clamping operations
 *
 * All methods are static and use Vector2D for positions and directions.
 *
 * Based on concepts from Mat Buckland's "Programming Game AI by Example"
 * and computational geometry algorithms.
 *
 * @author Domingol Team
 * @version 0.1.0
 */
public final class GeometryUtils {

    private static final double EPSILON = 0.0001;

    // Prevent instantiation
    private GeometryUtils() {
        throw new AssertionError("GeometryUtils is a utility class and should not be instantiated");
    }

    // ==================== DISTANCE CALCULATIONS ====================

    /**
     * Calculates the shortest distance from a point to an infinite line.
     *
     * The line is defined by two points on it.
     *
     * @param point the point to measure from
     * @param linePointA first point on the line
     * @param linePointB second point on the line
     * @return perpendicular distance from point to line
     */
    public static double distanceToLine(Vector2D point, Vector2D linePointA, Vector2D linePointB) {
        Vector2D lineVector = linePointB.subtract(linePointA);
        Vector2D pointVector = point.subtract(linePointA);

        double lineMagSq = lineVector.magnitudeSquared();
        if (lineMagSq < EPSILON * EPSILON) {
            // Line points are the same - return distance to that point
            return point.distanceTo(linePointA);
        }

        // Use cross product to get perpendicular distance
        // distance = |cross(lineVector, pointVector)| / |lineVector|
        double crossProduct = Math.abs(lineVector.cross(pointVector));
        return crossProduct / Math.sqrt(lineMagSq);
    }

    /**
     * Calculates the shortest distance from a point to a line segment.
     *
     * Unlike distanceToLine, this considers the segment endpoints.
     * If the perpendicular from the point doesn't intersect the segment,
     * returns distance to the nearest endpoint.
     *
     * USAGE: Pass interception checks, positioning near field lines.
     *
     * @param point the point to measure from
     * @param segmentStart start of the line segment
     * @param segmentEnd end of the line segment
     * @return shortest distance from point to segment
     */
    public static double distanceToLineSegment(Vector2D point, Vector2D segmentStart, Vector2D segmentEnd) {
        Vector2D closest = closestPointOnLineSegment(point, segmentStart, segmentEnd);
        return point.distanceTo(closest);
    }

    /**
     * Calculates the squared distance from a point to a line segment.
     *
     * PERFORMANCE: More efficient than distanceToLineSegment when you only
     * need to compare distances (avoids sqrt).
     *
     * @param point the point to measure from
     * @param segmentStart start of the line segment
     * @param segmentEnd end of the line segment
     * @return squared distance from point to segment
     */
    public static double distanceSquaredToLineSegment(Vector2D point, Vector2D segmentStart, Vector2D segmentEnd) {
        Vector2D closest = closestPointOnLineSegment(point, segmentStart, segmentEnd);
        return point.distanceSquaredTo(closest);
    }

    /**
     * Finds the closest point on a line segment to a given point.
     *
     * If the perpendicular from the point falls outside the segment,
     * returns the nearest endpoint.
     *
     * USAGE: Finding optimal positioning, interception points.
     *
     * @param point the point to measure from
     * @param segmentStart start of the line segment
     * @param segmentEnd end of the line segment
     * @return closest point on the segment
     */
    public static Vector2D closestPointOnLineSegment(Vector2D point, Vector2D segmentStart, Vector2D segmentEnd) {
        Vector2D segmentVector = segmentEnd.subtract(segmentStart);
        Vector2D pointVector = point.subtract(segmentStart);

        double segmentLengthSq = segmentVector.magnitudeSquared();

        if (segmentLengthSq < EPSILON * EPSILON) {
            // Segment is essentially a point
            return segmentStart;
        }

        // Project point onto segment line
        // t = dot(pointVector, segmentVector) / |segmentVector|²
        double t = pointVector.dot(segmentVector) / segmentLengthSq;

        // Clamp t to [0, 1] to stay within segment
        t = Math.max(0.0, Math.min(1.0, t));

        // Return point at parameter t along segment
        return segmentStart.add(segmentVector.multiply(t));
    }

    // ==================== INTERSECTION TESTS ====================

    /**
     * Tests if two line segments intersect.
     *
     * USAGE: Pass interception checks, collision detection.
     *
     * @param seg1Start start of first segment
     * @param seg1End end of first segment
     * @param seg2Start start of second segment
     * @param seg2End end of second segment
     * @return true if segments intersect
     */
    public static boolean lineSegmentsIntersect(
            Vector2D seg1Start, Vector2D seg1End,
            Vector2D seg2Start, Vector2D seg2End) {

        Vector2D d1 = seg1End.subtract(seg1Start);
        Vector2D d2 = seg2End.subtract(seg2Start);
        Vector2D startDiff = seg2Start.subtract(seg1Start);

        double cross = d1.cross(d2);

        // Parallel or coincident lines
        if (Math.abs(cross) < EPSILON) {
            return false;
        }

        double t1 = startDiff.cross(d2) / cross;
        double t2 = startDiff.cross(d1) / cross;

        // Check if intersection point is within both segments
        return t1 >= 0.0 && t1 <= 1.0 && t2 >= 0.0 && t2 <= 1.0;
    }

    /**
     * Finds the intersection point of two line segments.
     *
     * Returns null if segments don't intersect.
     *
     * USAGE: Calculating exact interception points for passes.
     *
     * @param seg1Start start of first segment
     * @param seg1End end of first segment
     * @param seg2Start start of second segment
     * @param seg2End end of second segment
     * @return intersection point, or null if no intersection
     */
    public static Vector2D lineSegmentIntersectionPoint(
            Vector2D seg1Start, Vector2D seg1End,
            Vector2D seg2Start, Vector2D seg2End) {

        Vector2D d1 = seg1End.subtract(seg1Start);
        Vector2D d2 = seg2End.subtract(seg2Start);
        Vector2D startDiff = seg2Start.subtract(seg1Start);

        double cross = d1.cross(d2);

        // Parallel or coincident lines
        if (Math.abs(cross) < EPSILON) {
            return null;
        }

        double t1 = startDiff.cross(d2) / cross;
        double t2 = startDiff.cross(d1) / cross;

        // Check if intersection point is within both segments
        if (t1 >= 0.0 && t1 <= 1.0 && t2 >= 0.0 && t2 <= 1.0) {
            return seg1Start.add(d1.multiply(t1));
        }

        return null;
    }

    // ==================== CONTAINMENT TESTS ====================

    /**
     * Tests if a point is inside a circle.
     *
     * USAGE: Range checks, passing range tests.
     *
     * @param point point to test
     * @param center center of circle
     * @param radius radius of circle
     * @return true if point is inside or on the circle
     */
    public static boolean isPointInCircle(Vector2D point, Vector2D center, double radius) {
        return point.distanceSquaredTo(center) <= radius * radius;
    }

    /**
     * Tests if a point is inside an axis-aligned rectangle.
     *
     * USAGE: Checking if player is in penalty area, offside zone checks.
     *
     * @param point point to test
     * @param rectMin bottom-left corner of rectangle
     * @param rectMax top-right corner of rectangle
     * @return true if point is inside or on the rectangle boundary
     */
    public static boolean isPointInRectangle(Vector2D point, Vector2D rectMin, Vector2D rectMax) {
        return point.x >= rectMin.x && point.x <= rectMax.x &&
               point.y >= rectMin.y && point.y <= rectMax.y;
    }

    /**
     * Tests if a point is inside a triangle.
     *
     * Uses barycentric coordinates method.
     *
     * USAGE: Complex zone checks, tactical positioning.
     *
     * @param point point to test
     * @param v1 first vertex of triangle
     * @param v2 second vertex of triangle
     * @param v3 third vertex of triangle
     * @return true if point is inside the triangle
     */
    public static boolean isPointInTriangle(Vector2D point, Vector2D v1, Vector2D v2, Vector2D v3) {
        // Calculate barycentric coordinates
        Vector2D v0 = v3.subtract(v1);
        Vector2D v1v = v2.subtract(v1);
        Vector2D v2v = point.subtract(v1);

        double dot00 = v0.dot(v0);
        double dot01 = v0.dot(v1v);
        double dot02 = v0.dot(v2v);
        double dot11 = v1v.dot(v1v);
        double dot12 = v1v.dot(v2v);

        double invDenom = 1.0 / (dot00 * dot11 - dot01 * dot01);
        double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
        double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

        return (u >= 0) && (v >= 0) && (u + v <= 1);
    }

    // ==================== CLAMPING & PROJECTION ====================

    /**
     * Clamps a point to remain within an axis-aligned rectangle.
     *
     * USAGE: Keeping players within field boundaries.
     *
     * @param point point to clamp
     * @param rectMin bottom-left corner of rectangle
     * @param rectMax top-right corner of rectangle
     * @return clamped point
     */
    public static Vector2D clampToRectangle(Vector2D point, Vector2D rectMin, Vector2D rectMax) {
        double clampedX = Math.max(rectMin.x, Math.min(point.x, rectMax.x));
        double clampedY = Math.max(rectMin.y, Math.min(point.y, rectMax.y));
        return new Vector2D(clampedX, clampedY);
    }

    /**
     * Clamps a point to remain within a circle.
     *
     * If the point is outside the circle, returns the closest point on the circle's edge.
     *
     * @param point point to clamp
     * @param center center of circle
     * @param radius radius of circle
     * @return clamped point
     */
    public static Vector2D clampToCircle(Vector2D point, Vector2D center, double radius) {
        Vector2D offset = point.subtract(center);
        double distSq = offset.magnitudeSquared();

        if (distSq <= radius * radius) {
            return point; // Already inside
        }

        // Project onto circle edge
        double dist = Math.sqrt(distSq);
        return center.add(offset.multiply(radius / dist));
    }

    // ==================== ANGLE CALCULATIONS ====================

    /**
     * Calculates the angle at point B in the triangle ABC.
     *
     * USAGE: Shooting angles, passing angles, field of view calculations.
     *
     * @param pointA first point
     * @param pointB vertex point (angle is measured here)
     * @param pointC third point
     * @return angle in radians at point B
     */
    public static double angleBetweenPoints(Vector2D pointA, Vector2D pointB, Vector2D pointC) {
        Vector2D ba = pointA.subtract(pointB);
        Vector2D bc = pointC.subtract(pointB);
        return ba.angleTo(bc);
    }

    /**
     * Calculates the signed angle from vector BA to vector BC.
     *
     * Returns positive for counter-clockwise rotation, negative for clockwise.
     *
     * @param pointA first point
     * @param pointB vertex point (angle is measured here)
     * @param pointC third point
     * @return signed angle in radians at point B
     */
    public static double signedAngleBetweenPoints(Vector2D pointA, Vector2D pointB, Vector2D pointC) {
        Vector2D ba = pointA.subtract(pointB);
        Vector2D bc = pointC.subtract(pointB);
        return ba.signedAngleTo(bc);
    }

    // ==================== SPECIAL CALCULATIONS ====================

    /**
     * Calculates the area of a triangle defined by three points.
     *
     * Uses the cross product method.
     *
     * @param v1 first vertex
     * @param v2 second vertex
     * @param v3 third vertex
     * @return area of the triangle
     */
    public static double triangleArea(Vector2D v1, Vector2D v2, Vector2D v3) {
        Vector2D edge1 = v2.subtract(v1);
        Vector2D edge2 = v3.subtract(v1);
        return Math.abs(edge1.cross(edge2)) * 0.5;
    }

    /**
     * Tests if three points are collinear (lie on the same line).
     *
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     * @return true if points are collinear
     */
    public static boolean areCollinear(Vector2D p1, Vector2D p2, Vector2D p3) {
        return triangleArea(p1, p2, p3) < EPSILON;
    }

    /**
     * Calculates the centroid (center point) of a triangle.
     *
     * @param v1 first vertex
     * @param v2 second vertex
     * @param v3 third vertex
     * @return centroid point
     */
    public static Vector2D triangleCentroid(Vector2D v1, Vector2D v2, Vector2D v3) {
        double x = (v1.x + v2.x + v3.x) / 3.0;
        double y = (v1.y + v2.y + v3.y) / 3.0;
        return new Vector2D(x, y);
    }

    /**
     * Calculates the circumcenter of a triangle (center of circumscribed circle).
     *
     * Returns null if points are collinear.
     *
     * @param v1 first vertex
     * @param v2 second vertex
     * @param v3 third vertex
     * @return circumcenter point, or null if points are collinear
     */
    public static Vector2D triangleCircumcenter(Vector2D v1, Vector2D v2, Vector2D v3) {
        double d = 2 * (v1.x * (v2.y - v3.y) + v2.x * (v3.y - v1.y) + v3.x * (v1.y - v2.y));

        if (Math.abs(d) < EPSILON) {
            return null; // Points are collinear
        }

        double v1MagSq = v1.magnitudeSquared();
        double v2MagSq = v2.magnitudeSquared();
        double v3MagSq = v3.magnitudeSquared();

        double ux = (v1MagSq * (v2.y - v3.y) + v2MagSq * (v3.y - v1.y) + v3MagSq * (v1.y - v2.y)) / d;
        double uy = (v1MagSq * (v3.x - v2.x) + v2MagSq * (v1.x - v3.x) + v3MagSq * (v2.x - v1.x)) / d;

        return new Vector2D(ux, uy);
    }

    // ==================== RAYCAST ====================

    /**
     * Performs a raycast from a point in a direction, finding intersection with a line segment.
     *
     * Returns the distance to intersection, or Double.POSITIVE_INFINITY if no intersection.
     *
     * USAGE: Line-of-sight checks, shooting trajectory calculations.
     *
     * @param rayOrigin origin point of the ray
     * @param rayDirection direction of the ray (should be normalized for accurate distance)
     * @param segmentStart start of line segment
     * @param segmentEnd end of line segment
     * @return distance to intersection, or POSITIVE_INFINITY if no intersection
     */
    public static double raycastToSegment(
            Vector2D rayOrigin,
            Vector2D rayDirection,
            Vector2D segmentStart,
            Vector2D segmentEnd) {

        Vector2D segmentVector = segmentEnd.subtract(segmentStart);
        Vector2D originToSegment = segmentStart.subtract(rayOrigin);

        double cross = rayDirection.cross(segmentVector);

        if (Math.abs(cross) < EPSILON) {
            // Ray and segment are parallel
            return Double.POSITIVE_INFINITY;
        }

        double t1 = originToSegment.cross(segmentVector) / cross;
        double t2 = originToSegment.cross(rayDirection) / cross;

        // Check if intersection is in front of ray and within segment
        if (t1 >= 0.0 && t2 >= 0.0 && t2 <= 1.0) {
            return t1; // Distance along ray
        }

        return Double.POSITIVE_INFINITY;
    }

    /**
     * Performs a raycast and returns the intersection point if it exists.
     *
     * Returns null if no intersection.
     *
     * @param rayOrigin origin point of the ray
     * @param rayDirection direction of the ray
     * @param segmentStart start of line segment
     * @param segmentEnd end of line segment
     * @return intersection point, or null if no intersection
     */
    public static Vector2D raycastIntersectionPoint(
            Vector2D rayOrigin,
            Vector2D rayDirection,
            Vector2D segmentStart,
            Vector2D segmentEnd) {

        double distance = raycastToSegment(rayOrigin, rayDirection, segmentStart, segmentEnd);

        if (Double.isInfinite(distance)) {
            return null;
        }

        return rayOrigin.add(rayDirection.multiply(distance));
    }
}
