#version 120

uniform sampler2D texture;
uniform sampler2D texture2;
uniform vec2 texelSize;
uniform vec2 direction;
uniform vec3 color;
uniform bool separateTextures;
uniform float exposure;
uniform float radius;
uniform float weights[256];

void main() {
    if (separateTextures && direction.y == 1 && texture2D(texture2, gl_TexCoord[0].st).a != 0.0) {
        discard;
    }

    float alpha = texture2D(texture, gl_TexCoord[0].st).a * weights[0];
    vec2 offset = direction * texelSize;

    for (float f = 1.0; f <= radius; f ++) {
        alpha += texture2D(texture, gl_TexCoord[0].st + offset * f).a * weights[int(f)];
        alpha += texture2D(texture, gl_TexCoord[0].st - offset * f).a * weights[int(f)];
    }

    gl_FragColor = vec4(color, mix(alpha, 1.0 - exp(-alpha * exposure), step(0.0, direction.y)));
}