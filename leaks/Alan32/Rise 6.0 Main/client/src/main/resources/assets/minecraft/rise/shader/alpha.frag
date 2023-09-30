#version 120

uniform sampler2D u_diffuse_sampler;
uniform float u_alpha;

void main(void)
{
    vec2 tex_coord = gl_TexCoord[0].st;
    vec4 pixel_color = texture2D(u_diffuse_sampler, tex_coord);
    if (pixel_color.a == 0.0) discard;

    gl_FragColor = vec4(pixel_color.rgb, u_alpha);
}
