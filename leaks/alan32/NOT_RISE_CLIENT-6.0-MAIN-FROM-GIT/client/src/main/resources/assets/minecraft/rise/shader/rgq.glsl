#version 120

uniform vec2 u_size;
uniform float u_radius;
uniform vec4 u_first_color;
uniform vec4 u_second_color;
uniform int u_direction;

void main(void)
{
    vec2 tex_coord = gl_TexCoord[0].st;
    vec4 color = mix(u_first_color, u_second_color, u_direction > 0.0 ? tex_coord.y : tex_coord.x);
    gl_FragColor = vec4(color.rgb, color.a * smoothstep(1.0, 0.0, length(max((abs(tex_coord - 0.5) + 0.5) * u_size - u_size + u_radius, 0.0)) - u_radius + 0.5));
}