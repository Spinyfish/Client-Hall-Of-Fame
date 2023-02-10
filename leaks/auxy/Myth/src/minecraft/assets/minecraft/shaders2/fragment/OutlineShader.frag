#version 120

uniform vec2 texelSize, direction;
uniform sampler2D texture;
uniform float radius;
uniform vec3 color;

void main() {
    float tex = texture2D(texture, gl_TexCoord[0].xy).a;
    float alpha = tex;
    for (float r = -radius; r <= radius; r++) {
        float additional = texture2D(texture, gl_TexCoord[0].xy + direction * texelSize * r).a;
        float opposide = texture2D(texture, gl_TexCoord[0].xy - direction * texelSize * r).a;

        alpha += additional + opposide;
    }
    gl_FragColor = vec4(color, alpha) * step(0.0, -tex);
}