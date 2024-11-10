package dev.cryptics.obscura.model.data;

import dev.cryptics.obscura.model.ObjParser;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4i;

public class Vertex {
    private final FallbackPair<Vector3f> position;
    private final FallbackPair<Vector3f> normal;
    private final FallbackPair<Vector2f> uv;
    private final FallbackPair<Vector4i> color;

    public Vertex(IndexedVertex indexedVertex, ObjParser.Builder builder) {
        this.position = FallbackPair.ofDefault(builder.positions.get(indexedVertex.positionIndex()));
        this.normal = FallbackPair.ofDefault(builder.normals.get(indexedVertex.normalIndex()));
        this.uv = FallbackPair.ofDefault(builder.uvs.get(indexedVertex.uvIndex()));
        this.color = FallbackPair.ofDefault(new Vector4i(255, 255, 255, 255));
    }

    public Vertex(Vector3f position, Vector3f normal, Vector2f uv, Vector4i color) {
        this.position = FallbackPair.ofDefault(position);
        this.normal = FallbackPair.ofDefault(normal);
        this.uv = FallbackPair.ofDefault(uv);
        this.color = FallbackPair.ofDefault(color);
    }

    public Vector3f getPosition() {
        return this.position.get();
    }

    public Vector3f getNormal() {
        return this.normal.get();
    }

    public Vector2f getUv() {
        return this.uv.get();
    }

    public Vector4i getColor() {
        return this.color.get();
    }

    public Float[] getPositionArray() {
        return new Float[] {this.position.get().x, this.position.get().y, this.position.get().z};
    }

    public Float[] getNormalArray() {
        return new Float[] {this.normal.get().x, this.normal.get().y, this.normal.get().z};
    }

    public Float[] getUvArray() {
        return new Float[] {this.uv.get().x, this.uv.get().y};
    }

    public Integer[] getColorArray() {
        return new Integer[] {this.color.get().x, this.color.get().y, this.color.get().z, this.color.get().w};
    }

    public void clearOverrides() {
        this.position.clearOverride();
        this.normal.clearOverride();
        this.uv.clearOverride();
        this.color.clearOverride();
    }

    public String toString() {
        return "Vertex{" +
                "position=" + position.get() +
                ", normal=" + normal.get() +
                ", uv=" + uv.get() +
                ", color=" + color.get() +
                '}';
    }
}
