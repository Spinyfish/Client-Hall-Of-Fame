#version 120

uniform sampler2D u_texture;
uniform vec2 u_texelSize, u_offset;

void main() {
    vec4 sum = vec4(0.0);

    sum += texture2D(u_texture, gl_TexCoord[0].xy) * 4;
    sum += texture2D(u_texture, gl_TexCoord[0].xy + vec2(-u_texelSize.x, 0.0) * u_offset);
    sum += texture2D(u_texture, gl_TexCoord[0].xy + vec2(u_texelSize.x, 0.0) * u_offset);
    sum += texture2D(u_texture, gl_TexCoord[0].xy + vec2(0.0, -u_texelSize.y) * u_offset);
    sum += texture2D(u_texture, gl_TexCoord[0].xy + vec2(0.0, u_texelSize.y) * u_offset);

    gl_FragColor = sum / 8;
}