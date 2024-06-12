#version 400 core

in vec3 color;

out vec4 fragColor;


void main() {
    vec2 screenPos = gl_FragCoord.xy/vec2(1280, 720);

    fragColor = vec4(color, 1.0);

    float dist = distance(screenPos, vec2(0.5, 0.5));

    float fadeStart = 0.15;
    float fadeEnd = 0.25;
    if(dist > fadeStart && dist < fadeEnd) {
        float f = (dist - fadeStart) / (fadeEnd - fadeStart);
        f = -cos(f * 3.14159) * 0.5 + 0.5;
        fragColor = mix(fragColor, vec4(0.0, 0.0, 0.0, 1.0), f);
    } else if(dist >= fadeEnd) {
        fragColor = vec4(0.0, 0.0, 0.0, 1.0);
    }
}