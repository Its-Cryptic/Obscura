package dev.cryptic.obscura.core.shaders;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;
import static org.lwjgl.opengl.GL40.GL_TESS_CONTROL_SHADER;
import static org.lwjgl.opengl.GL40.GL_TESS_EVALUATION_SHADER;
import static org.lwjgl.opengl.GL43.GL_COMPUTE_SHADER;

public class ShaderManager {
    private int programId;
    private int vertexShaderId;
    private int fragmentShaderId;

    public ShaderManager() throws Exception {
        programId = glCreateProgram();
        if (programId == 0) throw new Exception("Could not create Shader");
    }

    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderId = createShader(shaderCode, ProgramType.VERTEX.getGlConst());
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderId = createShader(shaderCode, ProgramType.FRAGMENT.getGlConst());
    }

    public int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) throw new Exception("Error creating shader. Type: " + ProgramType.getProgramType(shaderType).getName());

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new Exception("Error compiling Shader code of type: " + ProgramType.getProgramType(shaderType).getName() + ", Info: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    public void link() throws Exception {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

        if (vertexShaderId != 0) glDetachShader(programId, vertexShaderId);

        if (fragmentShaderId != 0) glDetachShader(programId, fragmentShaderId);

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == GL_FALSE) {
            throw new Exception("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
        }
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (programId != 0) glDeleteProgram(programId);
    }

    public enum ProgramType {

        VERTEX("vertex", "vsh", GL_VERTEX_SHADER),
        FRAGMENT("fragment", "fsh", GL_FRAGMENT_SHADER),
        GEOMETRY("geometry", "gsh", GL_GEOMETRY_SHADER),
        TESS_CONTROL("tess_control", "tcsh", GL_TESS_CONTROL_SHADER),
        TESS_EVALUATION("tess_evaluation", "tesh", GL_TESS_EVALUATION_SHADER),
        COMPUTE("compute", "csh", GL_COMPUTE_SHADER);

        private final String name;
        private final String fileExtension;
        private final int glConst;

        ProgramType(String name, String fileExtension, int glConst) {
            this.name = name;
            this.fileExtension = fileExtension;
            this.glConst = glConst;
        }

        public String getName() {
            return name;
        }

        public String getFileExtension() {
            return fileExtension;
        }

        public int getGlConst() {
            return glConst;
        }

        public static ProgramType getProgramType(int glConst) {
            for (ProgramType programType : values()) {
                if (programType.getGlConst() == glConst) {
                    return programType;
                }
            }
            return null;
        }
    }

}
