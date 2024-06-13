package dev.cryptic.obscura.core.entity;

import org.joml.Vector3f;

public class Entity {
    private Model model;
    private Vector3f position, rotation;
    private float scale;

    public Entity(Model model, Vector3f position, Vector3f rotation, float scale) {
        this.model = model;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public void incPos(float x, float y, float z) {
        this.position.add(x, y, z);
    }

    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
    }

    public void incRot(float x, float y, float z) {
        this.rotation.add(x, y, z);
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.set(x, y, z);
    }

    public Model getModel() {
        return model;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }
}
