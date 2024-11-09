#version 330 core

layout (location = 0) in vec3 aPos;

uniform mat4 ModelMat;
uniform mat4 ViewMat;
uniform mat4 ProjMat;

out vec4 vertexColor;

void main() {
    gl_Position = ProjMat * ViewMat * ModelMat * vec4(aPos.x, aPos.y, aPos.z, 1.0);
    vec4 colors[3] = vec4[3](
    vec4(1.0, 0.0, 0.0, 1.0),  // Red for gl_VertexID == 0
    vec4(0.0, 1.0, 0.0, 1.0),  // Green for gl_VertexID == 1
    vec4(0.0, 0.0, 1.0, 1.0)   // Blue for gl_VertexID == 2
    );
    vertexColor = colors[gl_VertexID];
}