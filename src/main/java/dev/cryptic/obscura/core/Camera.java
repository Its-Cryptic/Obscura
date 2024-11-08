package dev.cryptic.obscura.core;

import org.joml.Vector3f;

public class Camera {
    private Vector3f position, rotation;

    public Camera() {
        this.position = new Vector3f();
        this.rotation = new Vector3f();
    }

    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public void move(float x, float y, float z) {
        if (z != 0) {
            this.position.x += (float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * z;
            this.position.z += (float) Math.cos(Math.toRadians(rotation.y)) * z;
        }
        if (x != 0) {
            this.position.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * x;
            this.position.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * x;
        }
        this.position.y += y;
    }

    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
    }

    public void rotate(float x, float y, float z) {
        this.rotation.add(x, y, z);
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.set(x, y, z);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }
}
