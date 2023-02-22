#version 120

uniform sampler2D u_diffuse_sampler;
uniform sampler2D u_other_sampler;
uniform vec2 u_texel_size;
uniform vec2 u_direction;
uniform float u_radius;
uniform float u_kernel[128];

void main()
{
    vec2 uv = gl_TexCoord[0].st;

    float alpha = texture2D(u_other_sampler, uv).a;
    if (u_direction.x == 0.0 && alpha == 0.0) {
        discard;
    }

    float half_radius = u_radius / 2.0;
    vec4 pixel_color = texture2D(u_diffuse_sampler, uv) * u_kernel[0];

    for (float f = 1; f <= u_radius; f++) {
        vec2 offset = f * u_texel_size * u_direction;
        pixel_color += texture2D(u_diffuse_sampler, uv - offset) * u_kernel[int(f)];
        pixel_color += texture2D(u_diffuse_sampler, uv + offset) * u_kernel[int(f)];
    }

    gl_FragColor = vec4(pixel_color.rgb, u_direction.x == 0.0 ? alpha : 1.0);
}