package dev.cryptics.obscura.core.phys;

import org.joml.Vector3f;

public class AABB {
    public Vector3f min;
    public Vector3f max;
    public AABB(Vector3f min, Vector3f max) {
        this.min = min;
        this.max = max;
    }

    public AABB(Vector3f center, float radius) {
        this.min = new Vector3f(center).sub(new Vector3f(radius));
        this.max = new Vector3f(center).add(new Vector3f(radius));
    }

    public AABB inflate(float x, float y, float z) {
        this.min.add(x, y, z);
        this.max.sub(x, y, z);
        return this;
    }

    public boolean containsPoint(Vector3f point) {
        return point.x >= min.x && point.x <= max.x &&
               point.y >= min.y && point.y <= max.y &&
               point.z >= min.z && point.z <= max.z;
    }

    public Vector3f getMax() {
        return max;
    }

    public Vector3f getMin() {
        return min;
    }

    public Vector3f getCenter() {
        return new Vector3f(min).add(max).mul(0.5f);
    }

    public Vector3f getSize() {
        return new Vector3f(max).sub(min);
    }

    public double getVolume() {
        Vector3f size = getSize();
        return (double) size.x * (double) size.y * (double) size.z;
    }

    @Override
    public String toString() {
        return "AABB{" +
                "min=" + min +
                ", max=" + max +
                '}';
    }
}
