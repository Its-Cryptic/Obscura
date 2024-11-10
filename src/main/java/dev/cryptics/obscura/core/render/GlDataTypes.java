package dev.cryptics.obscura.core.render;

import static org.lwjgl.opengl.GL11.*;

public enum GlDataTypes {
    FLOAT(GL_FLOAT, Float.BYTES),
    INT(GL_INT, Integer.BYTES),
    BYTE(GL_BYTE, Byte.BYTES),
    SHORT(GL_SHORT, Short.BYTES),
    DOUBLE(GL_DOUBLE, Double.BYTES);

    private int glConst;
    private int byteSize;
    GlDataTypes(int glConst, int byteSize) {
        this.glConst = glConst;
        this.byteSize = byteSize;
    }

    public int getGlConst() {
        return glConst;
    }

    public int getByteSize() {
        return byteSize;
    }
}
