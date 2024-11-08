#version 400 core
#extension GL_EXT_geometry_shader4 : enable

layout (triangles) in;
//layout (line_strip, max_vertices = 6) out;
layout(triangle_strip, max_vertices = 3) out;

out vec2 texCoord;
out vec3 color;
out vec3 Normal;

in DATA {
    vec3 Normal;
    vec3 color;
    vec2 texCoord;
    mat4 ProjectionMatrix;
} data_in[];


void main() {
    for (int i = 0; i < gl_VerticesIn; i++) {
        gl_Position = data_in[i].ProjectionMatrix * gl_in[i].gl_Position;
        color = data_in[i].color;
        texCoord = data_in[i].texCoord;
        Normal = data_in[i].Normal;
        EmitVertex();
    }
    EndPrimitive();
}