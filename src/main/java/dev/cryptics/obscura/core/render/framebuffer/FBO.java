package dev.cryptics.obscura.core.render.framebuffer;

import dev.cryptics.obscura.Obscura;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.*;

public class FBO {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final List<FBO> fbos = new ArrayList<>();
    private int fbo;
    private int albedoID;
    private int normalID;
    private int depthID;
    private int width, height;
    private boolean autoResize;

    public FBO(boolean autoResize) {
        fbos.add(this);
        this.autoResize = autoResize;
        this.fbo = -1;
        this.albedoID = -1;
        this.normalID = -1;
        this.depthID = -1;
    }

    public void init() {
        this.createBuffers(Obscura.getWindow().getWidth(), Obscura.getWindow().getHeight());
    }

    public void createBuffers(int width, int height) {
        this.width = width;
        this.height = height;

        this.fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, this.fbo);

        this.albedoID = glGenTextures();
        this.normalID = glGenTextures();
        this.depthID = glGenTextures();

        // Bind Depth First
        glBindTexture(GL_TEXTURE_2D, this.depthID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, this.depthID, 0);

        // Bind Albedo
        glBindTexture(GL_TEXTURE_2D, this.albedoID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.albedoID, 0);

        // Bind Normal
        glBindTexture(GL_TEXTURE_2D, this.normalID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, this.normalID, 0);

        int[] drawBuffers = {GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1};
        glDrawBuffers(drawBuffers);

        this.checkFboStatus();
    }

    private void destroyBuffers() {
        if (this.depthID > -1) {
            glDeleteTextures(this.depthID);
            this.depthID = -1;
        }

        if (this.albedoID > -1) {
            glDeleteTextures(this.albedoID);
            this.albedoID = -1;
        }

        if (this.normalID > -1) {
            glDeleteTextures(this.normalID);
            this.normalID = -1;
        }

        if (this.fbo > -1) {
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            glDeleteFramebuffers(this.fbo);
            this.fbo = -1;
        }
    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, this.fbo);
    }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    private void checkFboStatus() {
        switch (glCheckFramebufferStatus(GL_FRAMEBUFFER)) {
            case GL_FRAMEBUFFER_COMPLETE:
                LOGGER.info("Framebuffer is complete!");
                break;
            case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
                LOGGER.error("Framebuffer incomplete attachment!");
                break;
            case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
                LOGGER.error("Framebuffer incomplete missing attachment!");
                break;
            case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
                LOGGER.error("Framebuffer incomplete draw buffer!");
                break;
            case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
                LOGGER.error("Framebuffer incomplete read buffer!");
                break;
            case GL_FRAMEBUFFER_UNSUPPORTED:
                LOGGER.error("Framebuffer unsupported!");
                break;
            case GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE:
                LOGGER.error("Framebuffer incomplete multisample!");
                break;
            case GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS:
                LOGGER.error("Framebuffer incomplete layer targets!");
                break;
            default:
                LOGGER.error("Framebuffer incomplete!");
                break;
        }
    }

    public void resize(int width, int height) {
        if (this.fbo >= 0) {
            this.destroyBuffers();
        }
        this.createBuffers(width, height);
    }

    public void cleanup() {
        glDeleteFramebuffers(this.fbo);
        glDeleteTextures(this.albedoID);
        glDeleteTextures(this.normalID);
        glDeleteTextures(this.depthID);
    }

    public boolean isAutoResize() {
        return this.autoResize;
    }

    public int getFbo() {
        return this.fbo;
    }

    public int getAlbedoID() {
        return this.albedoID;
    }

    public int getNormalID() {
        return this.normalID;
    }

    public int getDepthID() {
        return this.depthID;
    }
}
