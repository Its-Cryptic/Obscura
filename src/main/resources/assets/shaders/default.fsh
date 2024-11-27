#version 330 core

in vec4 vertexColor;
in vec3 pos;
in vec4 vertexNormalView;
in vec2 texCoord;

layout (location = 0) out vec4 fragColor;
layout (location = 1) out vec4 viewNormal;



void main() {
    viewNormal = vertexNormalView;
    fragColor = vertexColor;
}