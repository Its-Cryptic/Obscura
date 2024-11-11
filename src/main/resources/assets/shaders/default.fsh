#version 330 core

in vec4 vertexColor;
in vec3 pos;
in vec3 vertexNormal;
in vec2 texCoord;

layout (location = 0) out vec4 fragColor;
layout (location = 1) out vec4 gNormal;


void main() {
    //fragColor = vec4(texCoord, 0.0, 1.0);
    gNormal = vec4(normalize(vertexNormal), 1.0);
    fragColor = vertexColor;
}