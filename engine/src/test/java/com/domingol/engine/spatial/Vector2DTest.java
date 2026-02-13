package com.domingol.engine.spatial;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.Random;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive unit tests for Vector2D class.
 * Tests all vector operations including basic arithmetic,
 * geometric operations, and specialized methods.
 */
class Vector2DTest {

    private static final double DELTA = 0.0001;

    // ==================== BASIC OPERATIONS ====================

    @Nested
    @DisplayName("Constructor and Constants")
    class ConstructorTests {

        @Test
        @DisplayName("Constructor should create vector with correct coordinates")
        void testConstructor() {
            Vector2D v = new Vector2D(3, 4);

            assertThat(v.x).isEqualTo(3.0);
            assertThat(v.y).isEqualTo(4.0);
        }

        @Test
        @DisplayName("Constant vectors should be correct")
        void testConstants() {
            assertThat(Vector2D.ZERO.x).isZero();
            assertThat(Vector2D.ZERO.y).isZero();

            assertThat(Vector2D.UP.x).isZero();
            assertThat(Vector2D.UP.y).isEqualTo(1.0);

            assertThat(Vector2D.DOWN.x).isZero();
            assertThat(Vector2D.DOWN.y).isEqualTo(-1.0);

            assertThat(Vector2D.LEFT.x).isEqualTo(-1.0);
            assertThat(Vector2D.LEFT.y).isZero();

            assertThat(Vector2D.RIGHT.x).isEqualTo(1.0);
            assertThat(Vector2D.RIGHT.y).isZero();
        }

        @Test
        @DisplayName("fromPolar should create vector from polar coordinates")
        void testFromPolar() {
            // 5 units at 0° (right)
            Vector2D v1 = Vector2D.fromPolar(5.0, 0);
            assertThat(v1.x).isCloseTo(5.0, within(DELTA));
            assertThat(v1.y).isCloseTo(0.0, within(DELTA));

            // 10 units at 90° (up)
            Vector2D v2 = Vector2D.fromPolar(10.0, Math.PI / 2);
            assertThat(v2.x).isCloseTo(0.0, within(DELTA));
            assertThat(v2.y).isCloseTo(10.0, within(DELTA));

            // 1 unit at 45°
            Vector2D v3 = Vector2D.fromPolar(1.0, Math.PI / 4);
            assertThat(v3.x).isCloseTo(Math.sqrt(2) / 2, within(DELTA));
            assertThat(v3.y).isCloseTo(Math.sqrt(2) / 2, within(DELTA));
        }

        @Test
        @DisplayName("randomUnit should create unit vector")
        void testRandomUnit() {
            Random rng = new Random(12345);

            for (int i = 0; i < 10; i++) {
                Vector2D v = Vector2D.randomUnit(rng);
                assertThat(v.magnitude()).isCloseTo(1.0, within(DELTA));
            }
        }
    }

    // ==================== MAGNITUDE & NORMALIZATION ====================

    @Nested
    @DisplayName("Magnitude and Normalization")
    class MagnitudeTests {

        @Test
        @DisplayName("Magnitude should calculate correctly")
        void testMagnitude() {
            Vector2D v = new Vector2D(3, 4);
            assertThat(v.magnitude()).isCloseTo(5.0, within(DELTA));

            Vector2D v2 = new Vector2D(0, 0);
            assertThat(v2.magnitude()).isZero();
        }

        @Test
        @DisplayName("MagnitudeSquared should be faster alternative")
        void testMagnitudeSquared() {
            Vector2D v = new Vector2D(3, 4);
            assertThat(v.magnitudeSquared()).isEqualTo(25.0);

            // Should equal magnitude²
            assertThat(v.magnitudeSquared()).isCloseTo(
                    v.magnitude() * v.magnitude(),
                    within(DELTA)
            );
        }

        @Test
        @DisplayName("Normalize should create unit vector")
        void testNormalize() {
            Vector2D v = new Vector2D(3, 4);
            Vector2D normalized = v.normalize();

            assertThat(normalized.magnitude()).isCloseTo(1.0, within(DELTA));
            assertThat(normalized.x).isCloseTo(0.6, within(DELTA));
            assertThat(normalized.y).isCloseTo(0.8, within(DELTA));
        }

        @Test
        @DisplayName("Normalize of zero vector should return ZERO")
        void testNormalizeZero() {
            Vector2D v = new Vector2D(0, 0);
            Vector2D normalized = v.normalize();

            assertThat(normalized).isEqualTo(Vector2D.ZERO);
        }

        @Test
        @DisplayName("Normalize should preserve direction")
        void testNormalizePreservesDirection() {
            Vector2D v = new Vector2D(10, 0);
            Vector2D normalized = v.normalize();

            assertThat(normalized.x).isCloseTo(1.0, within(DELTA));
            assertThat(normalized.y).isCloseTo(0.0, within(DELTA));
        }
    }

    // ==================== ARITHMETIC OPERATIONS ====================

    @Nested
    @DisplayName("Arithmetic Operations")
    class ArithmeticTests {

        @Test
        @DisplayName("Add should sum vectors correctly")
        void testAdd() {
            Vector2D v1 = new Vector2D(1, 2);
            Vector2D v2 = new Vector2D(3, 4);
            Vector2D result = v1.add(v2);

            assertThat(result.x).isEqualTo(4.0);
            assertThat(result.y).isEqualTo(6.0);
        }

        @Test
        @DisplayName("Subtract should subtract vectors correctly")
        void testSubtract() {
            Vector2D v1 = new Vector2D(5, 7);
            Vector2D v2 = new Vector2D(2, 3);
            Vector2D result = v1.subtract(v2);

            assertThat(result.x).isEqualTo(3.0);
            assertThat(result.y).isEqualTo(4.0);
        }

        @Test
        @DisplayName("Multiply should multiply by scalar correctly")
        void testMultiply() {
            Vector2D v = new Vector2D(2, 3);
            Vector2D result = v.multiply(2.5);

            assertThat(result.x).isEqualTo(5.0);
            assertThat(result.y).isEqualTo(7.5);
        }

        @Test
        @DisplayName("Divide should divide by scalar correctly")
        void testDivide() {
            Vector2D v = new Vector2D(10, 20);
            Vector2D result = v.divide(2.0);

            assertThat(result.x).isEqualTo(5.0);
            assertThat(result.y).isEqualTo(10.0);
        }

        @Test
        @DisplayName("Divide by zero should throw exception")
        void testDivideByZero() {
            Vector2D v = new Vector2D(10, 20);

            assertThatThrownBy(() -> v.divide(0.0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Cannot divide by zero");
        }
    }

    // ==================== DOT & CROSS PRODUCTS ====================

    @Nested
    @DisplayName("Products")
    class ProductTests {

        @Test
        @DisplayName("Dot product should calculate correctly")
        void testDot() {
            Vector2D v1 = new Vector2D(2, 3);
            Vector2D v2 = new Vector2D(4, 5);

            double dot = v1.dot(v2);

            assertThat(dot).isEqualTo(23.0); // 2*4 + 3*5 = 23
        }

        @Test
        @DisplayName("Dot product of perpendicular vectors should be zero")
        void testDotPerpendicular() {
            Vector2D v1 = new Vector2D(1, 0);
            Vector2D v2 = new Vector2D(0, 1);

            assertThat(v1.dot(v2)).isCloseTo(0.0, within(DELTA));
        }

        @Test
        @DisplayName("Cross product should calculate correctly")
        void testCross() {
            Vector2D v1 = new Vector2D(2, 3);
            Vector2D v2 = new Vector2D(4, 5);

            double cross = v1.cross(v2);

            assertThat(cross).isEqualTo(-2.0); // 2*5 - 3*4 = -2
        }

        @Test
        @DisplayName("Cross product determines left/right")
        void testCrossDirection() {
            Vector2D forward = new Vector2D(1, 0);
            Vector2D left = new Vector2D(0, 1);
            Vector2D right = new Vector2D(0, -1);

            assertThat(forward.cross(left)).isPositive(); // Left is counter-clockwise
            assertThat(forward.cross(right)).isNegative(); // Right is clockwise
        }
    }

    // ==================== PROJECTIONS ====================

    @Nested
    @DisplayName("Projections")
    class ProjectionTests {

        @Test
        @DisplayName("projectOnto should project vector correctly")
        void testProjectOnto() {
            Vector2D v = new Vector2D(3, 4);
            Vector2D onto = new Vector2D(1, 0); // X axis

            Vector2D projected = v.projectOnto(onto);

            assertThat(projected.x).isCloseTo(3.0, within(DELTA));
            assertThat(projected.y).isCloseTo(0.0, within(DELTA));
        }

        @Test
        @DisplayName("projectOnto diagonal should work correctly")
        void testProjectOntoDiagonal() {
            Vector2D v = new Vector2D(2, 0);
            Vector2D onto = new Vector2D(1, 1); // 45° diagonal

            Vector2D projected = v.projectOnto(onto);

            // Projection onto (1,1) should be (1,1)
            assertThat(projected.x).isCloseTo(1.0, within(DELTA));
            assertThat(projected.y).isCloseTo(1.0, within(DELTA));
        }

        @Test
        @DisplayName("scalarProjection should return magnitude")
        void testScalarProjection() {
            Vector2D v = new Vector2D(3, 4);
            Vector2D onto = new Vector2D(1, 0).normalize();

            double scalar = v.scalarProjection(onto);

            assertThat(scalar).isCloseTo(3.0, within(DELTA));
        }

        @Test
        @DisplayName("scalarProjection should be negative for opposite direction")
        void testScalarProjectionNegative() {
            Vector2D v = new Vector2D(-3, 0);
            Vector2D onto = new Vector2D(1, 0);

            double scalar = v.scalarProjection(onto);

            assertThat(scalar).isCloseTo(-3.0, within(DELTA));
        }
    }

    // ==================== PERPENDICULAR & REFLECTION ====================

    @Nested
    @DisplayName("Perpendicular and Reflection")
    class PerpendicularTests {

        @Test
        @DisplayName("perpendicular should rotate 90° counter-clockwise")
        void testPerpendicular() {
            Vector2D v = new Vector2D(1, 0);
            Vector2D perp = v.perpendicular();

            assertThat(perp.x).isCloseTo(0.0, within(DELTA));
            assertThat(perp.y).isCloseTo(1.0, within(DELTA));
        }

        @Test
        @DisplayName("perpendicularClockwise should rotate 90° clockwise")
        void testPerpendicularClockwise() {
            Vector2D v = new Vector2D(1, 0);
            Vector2D perp = v.perpendicularClockwise();

            assertThat(perp.x).isCloseTo(0.0, within(DELTA));
            assertThat(perp.y).isCloseTo(-1.0, within(DELTA));
        }

        @Test
        @DisplayName("perpendicular should be orthogonal")
        void testPerpendicularOrthogonal() {
            Vector2D v = new Vector2D(3, 4);
            Vector2D perp = v.perpendicular();

            // Dot product of perpendicular vectors should be zero
            assertThat(v.dot(perp)).isCloseTo(0.0, within(DELTA));
        }

        @Test
        @DisplayName("reflect should bounce off surface correctly")
        void testReflect() {
            // Ball moving right hits vertical wall (normal pointing left)
            Vector2D velocity = new Vector2D(5, 0);
            Vector2D normal = new Vector2D(-1, 0);

            Vector2D reflected = velocity.reflect(normal);

            assertThat(reflected.x).isCloseTo(-5.0, within(DELTA));
            assertThat(reflected.y).isCloseTo(0.0, within(DELTA));
        }

        @Test
        @DisplayName("reflect at 45° should work correctly")
        void testReflectAngled() {
            // Ball moving diagonally hits horizontal surface
            Vector2D velocity = new Vector2D(3, 4);
            Vector2D normal = new Vector2D(0, -1); // Upward normal

            Vector2D reflected = velocity.reflect(normal);

            assertThat(reflected.x).isCloseTo(3.0, within(DELTA));
            assertThat(reflected.y).isCloseTo(-4.0, within(DELTA)); // Y flips
        }
    }

    // ==================== DISTANCE & ANGLES ====================

    @Nested
    @DisplayName("Distance and Angles")
    class DistanceAngleTests {

        @Test
        @DisplayName("distanceTo should calculate euclidean distance")
        void testDistanceTo() {
            Vector2D v1 = new Vector2D(0, 0);
            Vector2D v2 = new Vector2D(3, 4);

            double distance = v1.distanceTo(v2);

            assertThat(distance).isCloseTo(5.0, within(DELTA));
        }

        @Test
        @DisplayName("distanceSquaredTo should be faster alternative")
        void testDistanceSquaredTo() {
            Vector2D v1 = new Vector2D(1, 2);
            Vector2D v2 = new Vector2D(4, 6);

            double distSq = v1.distanceSquaredTo(v2);

            assertThat(distSq).isEqualTo(25.0); // 3² + 4² = 25
        }

        @Test
        @DisplayName("angle should return correct angle")
        void testAngle() {
            Vector2D right = new Vector2D(1, 0);
            assertThat(right.angle()).isCloseTo(0.0, within(DELTA));

            Vector2D up = new Vector2D(0, 1);
            assertThat(up.angle()).isCloseTo(Math.PI / 2, within(DELTA));

            Vector2D left = new Vector2D(-1, 0);
            assertThat(Math.abs(left.angle())).isCloseTo(Math.PI, within(DELTA));
        }

        @Test
        @DisplayName("angleTo should calculate angle between vectors")
        void testAngleTo() {
            Vector2D v1 = new Vector2D(1, 0);
            Vector2D v2 = new Vector2D(0, 1);

            double angle = v1.angleTo(v2);

            assertThat(angle).isCloseTo(Math.PI / 2, within(DELTA)); // 90°
        }

        @Test
        @DisplayName("angleTo parallel vectors should be zero")
        void testAngleToParallel() {
            Vector2D v1 = new Vector2D(2, 0);
            Vector2D v2 = new Vector2D(5, 0);

            assertThat(v1.angleTo(v2)).isCloseTo(0.0, within(DELTA));
        }

        @Test
        @DisplayName("signedAngleTo should return signed angle")
        void testSignedAngleTo() {
            Vector2D forward = new Vector2D(1, 0);
            Vector2D left = new Vector2D(0, 1);
            Vector2D right = new Vector2D(0, -1);

            // Counter-clockwise = positive
            assertThat(forward.signedAngleTo(left)).isPositive();

            // Clockwise = negative
            assertThat(forward.signedAngleTo(right)).isNegative();
        }
    }

    // ==================== TRANSFORMATIONS ====================

    @Nested
    @DisplayName("Transformations")
    class TransformationTests {

        @Test
        @DisplayName("rotate should rotate vector correctly")
        void testRotate() {
            Vector2D v = new Vector2D(1, 0);
            Vector2D rotated = v.rotate(Math.PI / 2); // 90°

            assertThat(rotated.x).isCloseTo(0.0, within(DELTA));
            assertThat(rotated.y).isCloseTo(1.0, within(DELTA));
        }

        @Test
        @DisplayName("rotate 180° should flip vector")
        void testRotate180() {
            Vector2D v = new Vector2D(3, 4);
            Vector2D rotated = v.rotate(Math.PI);

            assertThat(rotated.x).isCloseTo(-3.0, within(DELTA));
            assertThat(rotated.y).isCloseTo(-4.0, within(DELTA));
        }

        @Test
        @DisplayName("lerp should interpolate linearly")
        void testLerp() {
            Vector2D start = new Vector2D(0, 0);
            Vector2D end = new Vector2D(10, 10);

            Vector2D mid = start.lerp(end, 0.5);
            assertThat(mid.x).isCloseTo(5.0, within(DELTA));
            assertThat(mid.y).isCloseTo(5.0, within(DELTA));

            Vector2D quarter = start.lerp(end, 0.25);
            assertThat(quarter.x).isCloseTo(2.5, within(DELTA));
            assertThat(quarter.y).isCloseTo(2.5, within(DELTA));
        }

        @Test
        @DisplayName("clamp should limit magnitude")
        void testClamp() {
            Vector2D v = new Vector2D(3, 4); // magnitude 5
            Vector2D clamped = v.clamp(3.0);

            assertThat(clamped.magnitude()).isCloseTo(3.0, within(DELTA));

            // Direction should be preserved
            Vector2D normalizedOriginal = v.normalize();
            Vector2D normalizedClamped = clamped.normalize();
            assertThat(normalizedClamped.x).isCloseTo(normalizedOriginal.x, within(DELTA));
            assertThat(normalizedClamped.y).isCloseTo(normalizedOriginal.y, within(DELTA));
        }

        @Test
        @DisplayName("clamp should not change vector if already smaller")
        void testClampNoChange() {
            Vector2D v = new Vector2D(1, 1); // magnitude ~1.41
            Vector2D clamped = v.clamp(5.0);

            assertThat(clamped.x).isEqualTo(v.x);
            assertThat(clamped.y).isEqualTo(v.y);
        }
    }

    // ==================== COMPARISONS ====================

    @Nested
    @DisplayName("Comparisons")
    class ComparisonTests {

        @Test
        @DisplayName("isApproximately should detect nearly equal vectors")
        void testIsApproximately() {
            Vector2D v1 = new Vector2D(1.0, 2.0);
            Vector2D v2 = new Vector2D(1.00001, 2.00001);

            assertThat(v1.isApproximately(v2)).isTrue();
        }

        @Test
        @DisplayName("isApproximately should reject different vectors")
        void testIsApproximatelyFalse() {
            Vector2D v1 = new Vector2D(1.0, 2.0);
            Vector2D v2 = new Vector2D(1.1, 2.1);

            assertThat(v1.isApproximately(v2)).isFalse();
        }

        @Test
        @DisplayName("isZero should detect zero vectors")
        void testIsZero() {
            assertThat(Vector2D.ZERO.isZero()).isTrue();
            assertThat(new Vector2D(0.00001, 0.00001).isZero()).isTrue();
            assertThat(new Vector2D(0.1, 0).isZero()).isFalse();
        }

        @Test
        @DisplayName("equals should compare exactly")
        void testEquals() {
            Vector2D v1 = new Vector2D(1.0, 2.0);
            Vector2D v2 = new Vector2D(1.0, 2.0);
            Vector2D v3 = new Vector2D(1.0, 2.1);

            assertThat(v1).isEqualTo(v2);
            assertThat(v1).isNotEqualTo(v3);
        }

        @Test
        @DisplayName("hashCode should be consistent with equals")
        void testHashCode() {
            Vector2D v1 = new Vector2D(1.0, 2.0);
            Vector2D v2 = new Vector2D(1.0, 2.0);

            assertThat(v1.hashCode()).isEqualTo(v2.hashCode());
        }
    }

    // ==================== EDGE CASES ====================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Operations on ZERO vector should not throw")
        void testZeroVectorOperations() {
            Vector2D zero = Vector2D.ZERO;

            assertThat(zero.magnitude()).isZero();
            assertThat(zero.normalize()).isEqualTo(Vector2D.ZERO);
            assertThat(zero.add(Vector2D.RIGHT)).isEqualTo(Vector2D.RIGHT);
        }

        @Test
        @DisplayName("Very large vectors should not overflow")
        void testLargeVectors() {
            Vector2D large = new Vector2D(1e10, 1e10);

            assertThat(large.magnitude()).isPositive();
            assertThat(large.normalize().magnitude()).isCloseTo(1.0, within(DELTA));
        }

        @Test
        @DisplayName("toString should format nicely")
        void testToString() {
            Vector2D v = new Vector2D(3.14159, 2.71828);
            String str = v.toString();

            assertThat(str).contains("3.14");
            assertThat(str).contains("2.72");
        }
    }
}
