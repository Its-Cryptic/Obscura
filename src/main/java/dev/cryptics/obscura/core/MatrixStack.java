package dev.cryptics.obscura.core;

import org.joml.Matrix4f;
import org.joml.Quaternionf;

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

    public void translate(float x, float y, float z) {
        Matrix4f matrix4f = this.stack.getLast();
        matrix4f.translate(x, y, z);
    }

    public void scale(float x, float y, float z) {
        Matrix4f matrix4f = this.stack.getLast();
        matrix4f.scale(x, y, z);
    }

    public void rotate(Quaternionf quaternionf) {
        this.stack.getLast().rotate(quaternionf);
    }

    public void push() {
        this.stack.push(new Matrix4f(this.stack.getLast()));
    }

    public void pop() {
        this.stack.pop();
    }

    public Matrix4f getMatrix() {
        return this.stack.getLast();
    }
}
