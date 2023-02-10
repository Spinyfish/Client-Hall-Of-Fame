#version 120

uniform sampler2D inTexture, textureToCheck;
uniform vec2 texelSize, direction;
uniform float radius;
uniform float weights[256];
uniform vec3 color;
void main() {
    vec2 texCoord = gl_TexCoord[0].st;
    if(direction.y == 1)
    if (texture2D(textureToCheck, texCoord).a != 0.0) return;

    vec4 blurred_color = vec4(0.0);
    for(float r = -radius; r <= radius; r++) {
        blurred_color += texture2D(inTexture, gl_TexCoord[0].st + r * texelSize * direction) * (weights[int(abs(r))]);
    }
    gl_FragColor = vec4(color.rgb, blurred_color.a * 1.8);
}