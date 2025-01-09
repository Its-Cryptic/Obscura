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

mat4 calculateModelMat(vec3 particlePos) {
    mat4 modelMat = mat4(1.0);
    modelMat[3] = vec4(particlePos, 1.0);
    return modelMat;
}

mat2 rotate2d(float angle) {
    return mat2(cos(angle), -sin(angle), sin(angle), cos(angle));
}
mat3 rotationMatrix(vec3 axis, float angle) {
    axis = normalize(axis);
    float s = sin(angle);
    float c = cos(angle);
    float oc = 1.0 - c;
    return mat3(oc * axis.x * axis.x + c,           oc * axis.x * axis.y - axis.z * s,  oc * axis.z * axis.x + axis.y * s,
                oc * axis.x * axis.y + axis.z * s,  oc * axis.y * axis.y + c,           oc * axis.y * axis.z - axis.x * s,
                oc * axis.z * axis.x - axis.y * s,  oc * axis.y * axis.z + axis.x * s,  oc * axis.z * axis.z + c);
}

const vec3 cameraPos = vec3(8,12,0);

mat3 scaleMatrix(vec3 scale) {
    return mat3(scale.x, 0, 0,
                0, scale.y, 0,
                0, 0, scale.z);
}

float easeOutExpo(float t) {
    return 1.0 - pow(2.0, -10.0 * t);
}

mat4 getMat(Particle particle) {
    vec3 dir = normalize(particle.velocity);
    vec3 axis = cross(dir, vec3(0,1,0));
    float angle = acos(dot(dir, vec3(0,1,0)));
    mat3 scale = scaleMatrix(vec3(1.0, easeOutExpo(length(particle.velocity))*10, 1.0));
    mat3 rot = rotationMatrix(axis, angle);
    mat4 mat = calculateModelMat(particle.position);
    mat3 rotScale = rot * scale;
    mat[0].xyz = rotScale[0];
    mat[1].xyz = rotScale[1];
    mat[2].xyz = rotScale[2];
    return mat;
}


void main() {
    Particle particle = particles[gl_InstanceID];
    gl_Position = ProjMat * ViewMat * ModelMat * getMat(particle) * vec4(Position, 1.0);
    vec4 colors[3] = vec4[3](
    vec4(1.0, 0.0, 0.0, 1.0),  // Red for gl_VertexID == 0
    vec4(0.0, 1.0, 0.0, 1.0),  // Green for gl_VertexID == 1
    vec4(0.0, 0.0, 1.0, 1.0)   // Blue for gl_VertexID == 2
    );
    vertexColor = colors[gl_InstanceID % 3];
    //vertexColor *= calculateDiffuseStrength(normalize(vec3(-1.0, -1.0, -1.0)), normalize(Normal));
    texCoord = TexCoord;
}