#version 400 core

in vec2 texCoord;

uniform sampler2D TextureSampler;

out vec4 fragColor;


void main() {
    vec2 screenPos = gl_FragCoord.xy/vec2(1280, 720);

    fragColor = texture(TextureSampler, texCoord);
    //fragColor = texture(TextureSampler, screenPos);

}