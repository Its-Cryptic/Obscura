#version 400 core

uniform mat4 ModelMatrix;
uniform mat4 ViewMatrix;
uniform mat4 ProjectionMatrix;

in vec3 position;
in vec2 textureCoord;

out vec2 texCoord;

out DATA {
    vec3 Normal;
    vec3 color;
    vec2 texCoord;
    mat4 ProjectionMatrix;
} data_out;

void main() {
    vec4 localSpacePos = vec4(position, 1.0);
    vec4 worldSpacePos = ModelMatrix * localSpacePos;
    vec4 viewSpacePos = ViewMatrix * worldSpacePos;
    vec4 clipSpacePos = ProjectionMatrix * viewSpacePos;
    gl_Position = viewSpacePos;
    texCoord = textureCoord;

    data_out.Normal = vec3(ModelMatrix * vec4(0.0, 0.0, 1.0, 0.0));
    data_out.color = vec3(1.0, 1.0, 0.0);
    data_out.texCoord = textureCoord;
    data_out.ProjectionMatrix = ProjectionMatrix;
}