#version 330 core

in vec4 vertexColor;
in vec3 pos;
in vec3 vertexNormal;
in vec2 texCoord;

out vec4 fragColor;


void main() {
    //fragColor = vec4(texCoord, 0.0, 1.0);
    fragColor = vertexColor;
}