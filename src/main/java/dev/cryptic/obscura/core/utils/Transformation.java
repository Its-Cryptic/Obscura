package dev.cryptic.obscura.core.utils;

import dev.cryptic.obscura.core.Camera;
import dev.cryptic.obscura.core.entity.Entity;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transformation {

    public static Matrix4f createModelMatrix(Entity entity) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(entity.getPosition());
        matrix.rotateX((float) Math.toRadians(entity.getRotation().x));
        matrix.rotateY((float) Math.toRadians(entity.getRotation().y));
        matrix.rotateZ((float) Math.toRadians(entity.getRotation( ).z));
        matrix.scale(entity.getScale());
        return matrix;
    }

    public static Matrix4f getViewMatrix(Camera camera) {
        Vector3f cameraPos = camera.getPosition();
        Vector3f rotation = camera.getRotation();
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.identity();
        viewMatrix.rotateX((float) Math.toRadians(rotation.x));
        viewMatrix.rotateY((float) Math.toRadians(rotation.y));
        viewMatrix.rotateZ((float) Math.toRadians(rotation.z));
        viewMatrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        return viewMatrix;
    }
}
