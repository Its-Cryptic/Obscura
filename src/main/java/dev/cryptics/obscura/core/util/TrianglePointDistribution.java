package dev.cryptics.obscura.core.util;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;

public class TrianglePointDistribution {
    private static final Matrix4fc FLIP_ROTATION = new Matrix4f().identity().m00(-1).m03(1).m11(-1).m13(1);

    public static Matrix4f generateTransform(Vector3f v0, Vector3f v1, Vector3f v2) {
        Matrix4f transform = new Matrix4f();
        transform.m00(v1.x-v0.x).m01(v2.x-v0.x).m02(0).m03(v0.x);
        transform.m10(v1.y-v0.y).m11(v2.y-v0.y).m12(0).m13(v0.y);
        transform.m20(v1.z-v0.z).m21(v2.z-v0.z).m22(1).m23(v0.z);
        transform.m30(0).m31(0).m32(0).m33(1);
        return transform;
    }

    public static Vector3f[] distributePoints(Vector3f v0, Vector3f v1, Vector3f v2, int numPoints) {
        Vector3f[] points = new Vector3f[numPoints];
        Matrix4f transform = generateTransform(v0, v1, v2);
        for (int i = 0; i < numPoints; i++) {
            float x = (float) Math.random();
            float y = (float) Math.random();
            Vector3f point = new Vector3f(x, y, 0);
            if (point.x() + point.y() > 1) {
                FLIP_ROTATION.transformPosition(point);
            }
            transform.transformPosition(point);
        }
        return points;
    }
}
