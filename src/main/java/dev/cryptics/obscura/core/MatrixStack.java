package dev.cryptics.obscura.core;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayDeque;
import java.util.Deque;

public class MatrixStack {
    private final Deque<Matrix4f> stack = new ArrayDeque<>();

    public MatrixStack() {
        this.stack.push(new Matrix4f());
    }

    public void translate(double x, double y, double z) {
        this.translate((float) x, (float) y, (float) z);
    }

    public void translate(Vector3f vector3f) {
        this.translate(vector3f.x, vector3f.y, vector3f.z);
    }

    public void translate(float x, float y, float z) {
        Matrix4f matrix4f = this.stack.getLast();
        matrix4f.translate(x, y, z);
    }

    public void scale(Vector3f vector3f) {
        this.scale(vector3f.x, vector3f.y, vector3f.z);
    }

    public void scale(float x, float y, float z) {
        Matrix4f matrix4f = this.stack.getLast();
        matrix4f.scale(x, y, z);
    }

    public void rotate(Quaternionf quaternionf) {
        this.stack.getLast().rotate(quaternionf);
    }

    public void rotateAround(Quaternionf quaternionf, float x, float y, float z) {
        this.stack.getLast().rotateAround(quaternionf, x, y, z);
    }

    public void push() {
        this.stack.addLast(new Matrix4f(this.stack.getLast()));
    }

    public void pop() {
        this.stack.removeLast();
    }

    public boolean clear() {
        return this.stack.size() == 1;
    }

    public Matrix4f getMatrix() {
        return this.stack.getLast();
    }
}
