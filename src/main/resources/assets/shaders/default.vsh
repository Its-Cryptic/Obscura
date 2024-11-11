#version 330 core

layout (location = 0) in vec3 Position;
layout (location = 1) in vec3 Normal;
layout (location = 2) in vec2 TexCoord;

uniform mat4 ModelMat;
uniform mat4 ViewMat;
uniform mat4 ProjMat;

out vec4 vertexColor;
out vec3 pos;
out vec3 vertexNormal;
out vec2 texCoord;

void main() {
    gl_Position = ProjMat * ViewMat * ModelMat * vec4(Position.x, Position.y, Position.z, 1.0);
    pos = Position;
    vec4 colors[3] = vec4[3](
    vec4(1.0, 0.0, 0.0, 1.0),  // Red for gl_VertexID == 0
    vec4(0.0, 1.0, 0.0, 1.0),  // Green for gl_VertexID == 1
    vec4(0.0, 0.0, 1.0, 1.0)   // Blue for gl_VertexID == 2
    );
    //vertexColor = colors[gl_VertexID % 3];
    vertexColor = vec4(Normal + 0.5, 1.0) * colors[gl_VertexID % 3];
    vertexNormal = Normal;
    texCoord = TexCoord;
}