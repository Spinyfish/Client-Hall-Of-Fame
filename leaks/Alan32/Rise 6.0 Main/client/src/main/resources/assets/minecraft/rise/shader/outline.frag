#version 120

uniform sampler2D u_texture;
uniform vec2 u_texel_size;
uniform float u_radius;

void main(void)
{
    vec4 i_color, p_color;
    vec2 tex_coord = gl_TexCoord[0].st;
    p_color = texture2D(u_texture, tex_coord);

    if (p_color.a > 0.0) {
        discard;
    }

    float i, j;
    for (i = -u_radius; i <= u_radius; ++i) {
        for (j = -u_radius; j <= u_radius; ++j) {
            i_color = texture2D(u_texture, tex_coord + vec2(i, j) * u_texel_size);
            if (i_color.a > 0.0) {
                gl_FragColor = vec4(p_color.rgb, 1.0);
                return;
            }
        }
    }

    discard;
}