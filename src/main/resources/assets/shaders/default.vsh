#version 330 core

layout (location = 0) in vec3 aPos;

out vec4 color;

void main() {
    gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);
    color = vec4(aPos.x + 0.5, aPos.y + 0.5, aPos.z + 0.5, 1.0);
}