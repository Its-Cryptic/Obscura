package dev.cryptics.obscura.core.consts;

import static org.lwjgl.opengl.GL40.*;

public enum TextureTarget {
    TEXTURE_1D(GL_TEXTURE_1D),
    TEXTURE_2D(GL_TEXTURE_2D),
    TEXTURE_3D(GL_TEXTURE_3D),
    TEXTURE_1D_ARRAY(GL_TEXTURE_1D_ARRAY),
    TEXTURE_2D_ARRAY(GL_TEXTURE_2D_ARRAY),
    TEXTURE_RECTANGLE(GL_TEXTURE_RECTANGLE),
    TEXTURE_CUBE_MAP(GL_TEXTURE_CUBE_MAP),
    TEXTURE_CUBE_MAP_ARRAY(GL_TEXTURE_CUBE_MAP_ARRAY),
    TEXTURE_BUFFER(GL_TEXTURE_BUFFER),
    TEXTURE_2D_MULTISAMPLE(GL_TEXTURE_2D_MULTISAMPLE),
    TEXTURE_2D_MULTISAMPLE_ARRAY(GL_TEXTURE_2D_MULTISAMPLE_ARRAY);

    public final int target;
    TextureTarget(int target) {
        this.target = target;
    }

    public int getTarget() {
        return target;
    }
}
