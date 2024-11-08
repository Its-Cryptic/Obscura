package dev.cryptic.obscura.core.shaders;

import org.joml.*;
import org.lwjgl.system.MemoryStack;

import java.util.HashMap;
import java.util.Map;

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
    private int geometryShaderId;
    private int fragmentShaderId;

    private final Map<String, Integer> uniforms;

    public ShaderManager() throws Exception {
        programId = glCreateProgram();
        if (programId == 0) throw new Exception("Could not create Shader");

        uniforms = new HashMap<>();
    }

    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = glGetUniformLocation(programId, uniformName);
        if (uniformLocation < 0) throw new Exception("Could not find uniform: " + uniformName);
        uniforms.put(uniformName, uniformLocation);
    }

    public void setUniform(String uniformName, Matrix2f matrix2f) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix2fv(uniforms.get(uniformName), false, matrix2f.get(stack.mallocFloat(4)));
        }
    }

    public void setUniform(String uniformName, Matrix3f matrix3f) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix3fv(uniforms.get(uniformName), false, matrix3f.get(stack.mallocFloat(9)));
        }
    }

    public void setUniform(String uniformName, Matrix4f matrix4f) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(uniforms.get(uniformName), false, matrix4f.get(stack.mallocFloat(16)));
        }
    }

    public void setUniform(String uniformName, float value) {
        glUniform1f(uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, int value) {
        glUniform1i(uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, Vector2f vector2f) {
        glUniform2f(uniforms.get(uniformName), vector2f.x, vector2f.y);
    }

    public void setUniform(String uniformName, Vector2i vector2i) {
        glUniform2i(uniforms.get(uniformName), vector2i.x, vector2i.y);
    }

    public void setUniform(String uniformName, Vector3f vector3f) {
        glUniform3f(uniforms.get(uniformName), vector3f.x, vector3f.y, vector3f.z);
    }

    public void setUniform(String uniformName, Vector3i vector3i) {
        glUniform3i(uniforms.get(uniformName), vector3i.x, vector3i.y, vector3i.z);
    }

    public void setUniform(String uniformName, Vector4f vector4f) {
        glUniform4f(uniforms.get(uniformName), vector4f.x, vector4f.y, vector4f.z, vector4f.w);
    }

    public void setUniform(String uniformName, Vector4i vector4i) {
        glUniform4i(uniforms.get(uniformName), vector4i.x, vector4i.y, vector4i.z, vector4i.w);
    }

    public void setUniform(String uniformName, boolean value) {
        glUniform1i(uniforms.get(uniformName), value ? 1 : 0);
    }

    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderId = createShader(shaderCode, ProgramType.VERTEX.getGlConst());
    }

    public void createGeometryShader(String shaderCode) throws Exception {
        geometryShaderId = createShader(shaderCode, ProgramType.GEOMETRY.getGlConst());
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

        if (geometryShaderId != 0) glDetachShader(programId, geometryShaderId);

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
