package dev.cryptic.obscura.core.utils;

import dev.cryptic.obscura.core.entity.Entity;
import org.joml.Matrix4f;

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
}
