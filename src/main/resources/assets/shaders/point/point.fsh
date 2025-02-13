#version 330 core

in vec4 vertexColor;
in vec2 texCoord;

layout (location = 0) out vec4 fragColor;

void main() {
    fragColor = vertexColor;
}