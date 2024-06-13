#version 400 core

uniform mat4 ModelMat;

in vec3 position;
in vec2 textureCoord;

out vec2 texCoord;

void main() {
    gl_Position = ModelMat * vec4(position, 1.0);
    texCoord = textureCoord;
}