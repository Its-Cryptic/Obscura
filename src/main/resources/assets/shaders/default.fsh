#version 330 core

in vec4 vertexColor;
in vec3 pos;
in vec3 vertexNormal;

out vec4 fragColor;


void main() {
    fragColor = vertexColor;
}