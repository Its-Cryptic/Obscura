package dev.cryptics.obscura.core.consts;

import static org.lwjgl.opengl.GL41.*;

public enum TextureFormat {
    RED(GL_RED),
    RG(GL_RG),
    RGB(GL_RGB),
    RGBA(GL_RGBA),
    DEPTH(GL_DEPTH_COMPONENT),
    DEPTH_STENCIL(GL_DEPTH_STENCIL),
    R8(GL_R8),
    R8_SNORM(GL_R8_SNORM),
    R16(GL_R16),
    R16_SNORM(GL_R16_SNORM),
    RG8(GL_RG8),
    RG8_SNORM(GL_RG8_SNORM),
    RG16(GL_RG16),
    RG16_SNORM(GL_RG16_SNORM),
    R3_G3_B2(GL_R3_G3_B2),
    RGB4(GL_RGB4),
    RGB5(GL_RGB5),
    RGB565(GL_RGB565),
    RGB8(GL_RGB8),
    RGB8_SNORM(GL_RGB8_SNORM),
    RGB10(GL_RGB10),
    RGB12(GL_RGB12),
    RGB16(GL_RGB16),
    RGB16_SNORM(GL_RGB16_SNORM),
    RGBA2(GL_RGBA2),
    RGBA4(GL_RGBA4),
    RGB5_A1(GL_RGB5_A1),
    RGBA8(GL_RGBA8),
    RGBA8_SNORM(GL_RGBA8_SNORM),
    RGB10_A2(GL_RGB10_A2),
    RGB10_A2UI(GL_RGB10_A2UI),
    RGBA12(GL_RGBA12),
    RGBA16(GL_RGBA16),
    RGBA16_SNORM(GL_RGBA16_SNORM),
    SRGB8(GL_SRGB8),
    SRGB8_ALPHA8(GL_SRGB8_ALPHA8),
    R16F(GL_R16F),
    RG16F(GL_RG16F),
    RGB16F(GL_RGB16F),
    RGBA16F(GL_RGBA16F);

    public final int internalFormat;
    TextureFormat(int internalFormat) {
        this.internalFormat = internalFormat;
    }

    public int getInternalFormat() {
        return internalFormat;
    }
}
