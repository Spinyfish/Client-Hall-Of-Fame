#version 120

uniform sampler2D texture;
uniform vec2 texelSize;
uniform vec2 direction;
uniform float radius;
uniform vec3 color;

void main() {
    float center = texture2D(texture, gl_TexCoord[0].xy).a;
    float alpha = center;

    vec2 offset = direction * texelSize;

    for (float f = 1.0; f <= radius; f++) {
        float current1 = texture2D(texture, gl_TexCoord[0].xy + offset * f).a;
        float current2 = texture2D(texture, gl_TexCoord[0].xy - offset * f).a;

        alpha += current1 + current2;
    }

    gl_FragColor = vec4(color, alpha) * step(0.0, -center);
}