package dev.cryptics.obscura.core;

import dev.cryptics.obscura.Obscura;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Vector3f position = new Vector3f();
    private float pitch;
    private float yaw;
    private float roll;
    private float fov = 90.0f;
    private float zNear = 0.1f;
    private float zFar = 1000.0f;
    private Matrix4f viewMatrix = new Matrix4f();
    private Matrix4f projectionMatrix = new Matrix4f();
    public Camera(float fov, float zNear, float zFar) {
        this.fov = fov;
        this.zNear = zNear;
        this.zFar = zFar;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
        this.updateViewMatrix();
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
        this.updateViewMatrix();
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
        this.updateViewMatrix();
    }

    public void setRoll(float roll) {
        this.roll = roll;
        this.updateViewMatrix();
    }

    public void setFov(float fov) {
        this.fov = fov;
        this.updateProjectionMatrix();
    }

    public void setZNear(float zNear) {
        this.zNear = zNear;
        this.updateProjectionMatrix();
    }

    public void setZFar(float zFar) {
        this.zFar = zFar;
        this.updateProjectionMatrix();
    }

    public void lookAt(Vector3f target) {
        Vector3f direction = target.sub(position, new Vector3f()).normalize();
        this.pitch = (float) Math.toDegrees(Math.asin(direction.y));
        this.yaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        this.updateViewMatrix();
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    public Vector3f getLookVector() {
        return new Vector3f(0, 0, -1).rotateY((float) Math.toRadians(yaw)).rotateX((float) Math.toRadians(pitch));
    }

    public float getFov() {
        return fov;
    }

    public float getZNear() {
        return zNear;
    }

    public float getZFar() {
        return zFar;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f updateViewMatrix() {
        if (this.viewMatrix == null) this.viewMatrix = new Matrix4f();
        this.viewMatrix.identity();
        this.viewMatrix.rotateX((float) Math.toRadians(pitch));
        this.viewMatrix.rotateY((float) Math.toRadians(yaw));
        this.viewMatrix.rotateZ((float) Math.toRadians(roll));
        this.viewMatrix.translate(-position.x, -position.y, -position.z);
        return viewMatrix;
    }

    public Matrix4f updateProjectionMatrix() {
        if (this.projectionMatrix == null) this.projectionMatrix = new Matrix4f();
        Window window = Obscura.getWindow();
        float aspectRatio = (float) window.getWidth() / (float) window.getHeight();
        return projectionMatrix.identity().perspective((float) Math.toRadians(fov), aspectRatio, zNear, zFar);
    }
}
