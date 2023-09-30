#version 120

uniform sampler2D originalTexture;
uniform vec2 texelSize, direction;
uniform float radius;

#define precalculated texelSize * direction

float gauss(float x, float sigma) {
    return .4 * exp(-.5 * x * x / (sigma * sigma)) / sigma;
}

void main() {
    vec3 color;
    for(float i = -radius; i <= radius; i++) {
        color += texture2D(originalTexture, gl_TexCoord[0].st + i * precalculated).rgb * gauss(i, radius / 2);
    }
    gl_FragColor = vec4(color, 1.0);
}