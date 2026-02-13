package com.domingol.engine.spatial;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Random;

/**
 * Immutable 2D vector for representing positions, velocities, and forces.
 *
 * Coordinate system:
 * - X: 0 (own goal line) → 105 (opponent goal line)
 * - Y: 0 (left sideline) → 68 (right sideline)
 *
 * Based on concepts from Mat Buckland's "Programming Game AI by Example"
 * but with original implementation for Domingol.
 *
 * @author Domingol Team
 * @version 0.1.0
 */
public final class Vector2D {

    public static final Vector2D ZERO = new Vector2D(0, 0);
    public static final Vector2D UP = new Vector2D(0, 1);
    public static final Vector2D DOWN = new Vector2D(0, -1);
    public static final Vector2D LEFT = new Vector2D(-1, 0);
    public static final Vector2D RIGHT = new Vector2D(1, 0);

    private static final double EPSILON = 0.0001;

    public final double x;
    public final double y;

    @JsonCreator
    public Vector2D(@JsonProperty("x") double x, @JsonProperty("y") double y) {
        this.x = x;
        this.y = y;
    }

    public static Vector2D fromPolar(double magnitude, double angleRadians) {
        return new Vector2D(
                magnitude * Math.cos(angleRadians),
                magnitude * Math.sin(angleRadians)
        );
    }

    public static Vector2D randomUnit(Random random) {
        double angle = random.nextDouble() * 2 * Math.PI;
        return fromPolar(1.0, angle);
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public double magnitudeSquared() {
        return x * x + y * y;
    }

    public Vector2D normalize() {
        double mag = magnitude();
        if (mag > EPSILON) {
            return new Vector2D(x / mag, y / mag);
        }
        return ZERO;
    }

    public Vector2D add(Vector2D other) {
        return new Vector2D(x + other.x, y + other.y);
    }

    public Vector2D subtract(Vector2D other) {
        return new Vector2D(x - other.x, y - other.y);
    }

    public Vector2D multiply(double scalar) {
        return new Vector2D(x * scalar, y * scalar);
    }

    public Vector2D divide(double scalar) {
        if (Math.abs(scalar) < EPSILON) {
            throw new IllegalArgumentException("Cannot divide by zero or near-zero value");
        }
        return new Vector2D(x / scalar, y / scalar);
    }

    public double dot(Vector2D other) {
        return x * other.x + y * other.y;
    }

    public double cross(Vector2D other) {
        return x * other.y - y * other.x;
    }

    public Vector2D projectOnto(Vector2D onto) {
        double ontoMagSq = onto.magnitudeSquared();
        if (ontoMagSq < EPSILON * EPSILON) {
            return ZERO;
        }
        double projection = this.dot(onto) / ontoMagSq;
        return onto.multiply(projection);
    }

    public double scalarProjection(Vector2D onto) {
        double ontoMag = onto.magnitude();
        if (ontoMag < EPSILON) {
            return 0.0;
        }
        return this.dot(onto) / ontoMag;
    }

    public Vector2D perpendicular() {
        return new Vector2D(-y, x);
    }

    public Vector2D perpendicularClockwise() {
        return new Vector2D(y, -x);
    }

    public Vector2D reflect(Vector2D normal) {
        return this.subtract(normal.multiply(2 * this.dot(normal)));
    }

    public double distanceTo(Vector2D other) {
        double dx = x - other.x;
        double dy = y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double distanceSquaredTo(Vector2D other) {
        double dx = x - other.x;
        double dy = y - other.y;
        return dx * dx + dy * dy;
    }

    public double angle() {
        return Math.atan2(y, x);
    }

    public double angleTo(Vector2D other) {
        double dot = this.normalize().dot(other.normalize());
        dot = Math.max(-1.0, Math.min(1.0, dot));
        return Math.acos(dot);
    }

    public double signedAngleTo(Vector2D other) {
        return Math.atan2(this.cross(other), this.dot(other));
    }

    public Vector2D rotate(double angleRadians) {
        double cos = Math.cos(angleRadians);
        double sin = Math.sin(angleRadians);
        return new Vector2D(
                x * cos - y * sin,
                x * sin + y * cos
        );
    }

    public Vector2D lerp(Vector2D target, double t) {
        return new Vector2D(
                x + (target.x - x) * t,
                y + (target.y - y) * t
        );
    }

    public Vector2D clamp(double maxMagnitude) {
        double mag = magnitude();
        if (mag > maxMagnitude) {
            return normalize().multiply(maxMagnitude);
        }
        return this;
    }

    public boolean isApproximately(Vector2D other) {
        return Math.abs(x - other.x) < EPSILON && Math.abs(y - other.y) < EPSILON;
    }

    public boolean isZero() {
        return magnitudeSquared() < EPSILON * EPSILON;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector2D other = (Vector2D) obj;
        return Double.compare(other.x, x) == 0 && Double.compare(other.y, y) == 0;
    }

    @Override
    public int hashCode() {
        long xBits = Double.doubleToLongBits(x);
        long yBits = Double.doubleToLongBits(y);
        return (int)(xBits ^ (xBits >>> 32) ^ yBits ^ (yBits >>> 32));
    }

    @Override
    public String toString() {
        return String.format(java.util.Locale.US, "Vector2D(%.2f, %.2f)", x, y);
    }
}
