package dev.cryptics.obscura.core.render.framebuffer;

import dev.cryptics.obscura.core.consts.*;

import java.util.function.Consumer;

public class FramebufferAttachment {
    private String name;
    private TextureTarget target;
    private TextureFormat format;
    private int id;
    public FramebufferAttachment(String name, TextureTarget target, TextureFormat format) {
        this.name = name;
        this.target = target;
        this.format = format;
    }

    public static class Builder {
        private String name;
        private TextureTarget target;
        private TextureFormat format;
        private Consumer addAttachment;

        public Builder(String name, TextureTarget target, TextureFormat format) {
            this.name = name;
            this.target = target;
            this.format = format;
        }

        public Builder add(TexParam texParam, TexParamValue texParamValue) {
            return this;
        }

        private FramebufferAttachment build() {
            return new FramebufferAttachment(name, target, format);
        }
    }
}