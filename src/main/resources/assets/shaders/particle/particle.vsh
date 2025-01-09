#version 460
struct Particle {
    vec3 position;
    vec3 velocity;
};

layout(std430, binding = 0) readonly restrict buffer Particles {
    Particle particles[];
};

layout (location = 0) in vec3 Position;
layout (location = 1) in vec3 Normal;
layout (location = 2) in vec2 TexCoord;

uniform mat4 ModelMat;
uniform mat4 ViewMat;
uniform mat4 ProjMat;

out vec4 vertexColor;
out vec2 texCoord;

float calculateDiffuseStrength(vec3 lightDir, vec3 normal) {
    return max(dot(-lightDir, normal), 0.0);
}

void main() {
    Particle particle = particles[gl_InstanceID];
    gl_Position = ProjMat * ViewMat * ModelMat * vec4(Position+particle.position, 1.0);
    vec4 colors[3] = vec4[3](
    vec4(1.0, 0.0, 0.0, 1.0),  // Red for gl_VertexID == 0
    vec4(0.0, 1.0, 0.0, 1.0),  // Green for gl_VertexID == 1
    vec4(0.0, 0.0, 1.0, 1.0)   // Blue for gl_VertexID == 2
    );
    vertexColor = colors[gl_InstanceID % 3];
    vertexColor *= calculateDiffuseStrength(normalize(vec3(-1.0, -1.0, -1.0)), normalize(Normal));
    texCoord = TexCoord;
}