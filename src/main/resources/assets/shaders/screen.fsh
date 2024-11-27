#version 330 core
out vec4 fragColor;

in vec2 TexCoords;

uniform sampler2D gAlbedoSpec;
uniform sampler2D gNormal;

uniform vec2 Resolution;

const vec3 lightDir = normalize(vec3(-1.0, -1.0, 0.0));

vec3 blurNormal(sampler2D normalMap, vec2 texCoords, vec2 oneTexel, int radius) {
    vec3 normalSum = vec3(0.0);
    int count = 0;

    for (int y = -radius; y <= radius; ++y) {
        for (int x = -radius; x <= radius; ++x) {
            vec2 offset = vec2(x, y) * oneTexel;
            normalSum += texture(normalMap, texCoords + offset).rgb;
            count++;
        }
    }

    return normalize(normalSum / float(count));
}

void main() {
    vec3 col = texture(gAlbedoSpec, TexCoords).rgb;
    //vec3 viewSpaceNormals = texture(gNormal, TexCoords).rgb;
    vec3 viewSpaceNormals = blurNormal(gNormal, TexCoords, 1.0 / Resolution, 1);
    float intensity = max(dot(viewSpaceNormals, -lightDir), 0.0);
    fragColor = vec4(col*intensity, 1.0);
}