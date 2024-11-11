package dev.cryptics.obscura.core.render.framebuffer;

import dev.cryptics.obscura.core.consts.TextureFormat;
import dev.cryptics.obscura.core.consts.TextureTarget;

import java.util.function.Consumer;

public class FrameBuffer {
    private String name;
    private boolean useDepth;
    private int id;
    public FrameBuffer() {
    }

    /**
     *         FrameBuffer.builder("gBuffer", builder -> {
     *             builder.addAttachment("depth", TextureTarget.TEXTURE_2D, TextureFormat.DEPTH, parameters -> {
     *                 parameters.add(TexParam.MIN_FILTER, TexParamValue.NEAREST);
     *                 parameters.add(TexParam.MAG_FILTER, TexParamValue.NEAREST);
     *             });
     *             builder.addAttachment("color", TextureTarget.TEXTURE_2D, TextureFormat.RGBA8, parameters -> {
     *                 parameters.add(TexParam.MIN_FILTER, TexParamValue.LINEAR);
     *                 parameters.add(TexParam.MAG_FILTER, TexParamValue.LINEAR);
     *             });
     *             builder.addAttachment("normal", TextureTarget.TEXTURE_2D, TextureFormat.RGBA8, parameters -> {
     *                 parameters.add(TexParam.MIN_FILTER, TexParamValue.LINEAR);
     *                 parameters.add(TexParam.MAG_FILTER, TexParamValue.LINEAR);
     *             });
     *         });
     */
    public static FrameBuffer builder(String gBuffer, Consumer<Builder> builderConsumer) {
        Builder builder = Builder.of(gBuffer);
        builderConsumer.accept(builder);
        return builder.build();
    }

    public void init() {
        if (useDepth) {
            // Create a depth buffer
        }

    }

    public static class Builder {
        private String name;
        private boolean useDepth;

        public static Builder of(String name) {
            return new Builder(name);
        }

        private Builder(String name) {
            this.name = name;
        }

        public Builder addAttachment(String name, TextureTarget target, TextureFormat format, Consumer<FramebufferAttachment.Builder> attachmentConsumer) {
            FramebufferAttachment.Builder builder = new FramebufferAttachment.Builder(name, target, format);
            attachmentConsumer.accept(builder);
            return this;
        }
        protected FrameBuffer build() {
            return new FrameBuffer();
        }
    }
}
