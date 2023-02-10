#version 120

uniform sampler2D texture, textureToCheck;
uniform vec2 texelSize, direction;
uniform vec3 color;
uniform int isSex, miaKhalifa;
uniform float radius, alpha;

#define step texelSize * direction

float gauss(float x, float sigma) {
    float pow = x / sigma;
    return (1.0 / (abs(sigma) * 2.50662827463) * exp(-0.5 * pow * pow));
}

void main() {
    if (direction.y == 1 && texture2D(textureToCheck, gl_TexCoord[0].st).a != 0.0 && isSex == 1) return;
    vec4 blur = vec4(0.0);
    for (float r = -radius; r <= radius; r++) {
        blur += texture2D(texture, gl_TexCoord[0].xy + r * texelSize * direction) * gauss(r, isSex == 1 ? radius : radius / 2);
    }
    if (isSex == 1) {
        gl_FragColor = vec4(color.rgb, miaKhalifa == 1 ? blur.a * alpha : blur.a);
    } else {
        gl_FragColor = vec4(blur.rgb, 1.0);
    }
}