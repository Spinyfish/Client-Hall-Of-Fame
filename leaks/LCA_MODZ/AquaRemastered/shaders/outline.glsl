#version 120

varying vec2 uv;

uniform sampler2D texture;

uniform vec2 texelSize;
uniform vec2 direction;
uniform float outline_width;
uniform float alpha;
uniform vec3 mixColor = vec3(0.6, 0.15, 0.71);
#define step texelSize * direction

void main() {

    for (float x = -outline_width; x <= outline_width; x++) {
        for (float y = -outline_width; y <= outline_width; y++) {
            if (texture2D(texture, uv + vec2(x, y) * texelSize).a > 0.0) {
                gl_FragColor = vec4(mixColor, alpha);
            }
        }
    }
}