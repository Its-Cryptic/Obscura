package dev.cryptics.obscura.core.util;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;

public class TrianglePointDistribution {
    /*
    Rotation 180 degrees around the z-axis + translation by (1, 1, 0)
    -1 0  0  1   x  -x + 1   1-x
    0 -1  0  1   y  -y + 1   1-y
    0  0  1  0 * 0 = 0     =  0
    0  0  0  1   1   1        1
     */

    public static Matrix4fc generateTransform(Vector3f v0, Vector3f v1, Vector3f v2, boolean flattenZ) {
        Matrix4f transform = new Matrix4f();
        transform.m00(v1.x-v0.x).m10(v2.x-v0.x).m20(0).m30(v0.x);
        transform.m01(v1.y-v0.y).m11(v2.y-v0.y).m12(0).m31(v0.y);
        transform.m02(v1.z-v0.z).m12(v2.z-v0.z).m22(flattenZ ? 0 : 1).m32(v0.z);
        transform.m03(0).m13(0).m23(0).m33(1);
        return transform;
    }

    public static Vector3f[] distributePoints(Vector3f v0, Vector3f v1, Vector3f v2, int numPoints) {
        Vector3f[] points = new Vector3f[numPoints];
        Matrix4fc transform = generateTransform(v0, v1, v2, true);
        for (int i = 0; i < numPoints; i++) {
            float x = (float) Math.random();
            float y = (float) Math.random();
            Vector3f point = new Vector3f(x, y, 0);
            if (point.x() + point.y() > 1) {
                point.set(1 - point.x(), 1 - point.y(), 0);
            }
            transform.transformPosition(point);
            points[i] = point;
        }
        return points;
    }
}
