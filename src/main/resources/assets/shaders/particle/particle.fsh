#version 460 core
layout(location = 0) in vec2 texCoord;
layout(location = 1) in vec4 vertexColor;

layout(location = 0) out vec4 fragColor;

void main() {
    fragColor = vec4(vertexColor.rgb, 1.0);
}