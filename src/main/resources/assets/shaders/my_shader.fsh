#version 400 core

in vec3 color;

out vec4 fragColor;


void main() {
    vec2 screenPos = gl_FragCoord.xy/vec2(1280, 720);

    fragColor = vec4(color, 1.0);

    float dist = distance(screenPos, vec2(0.5, 0.5));

    if(dist > 0.2) {
        fragColor = vec4(0.0, 0.0, 0.0, 1.0);
    }
}