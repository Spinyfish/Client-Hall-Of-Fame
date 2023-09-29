#version 120

uniform vec2 u_size;
uniform float u_radius;
uniform float u_border_size;
uniform vec4 u_color;

void main(void)
{
    vec2 position = (abs(gl_TexCoord[0].st - 0.5) + 0.5) * u_size;
    float distance = length(max(position - u_size + u_radius + u_border_size, 0.0)) - u_radius + 0.5;
    gl_FragColor = vec4(u_color.rgb, u_color.a * (smoothstep(0.0, 1.0, distance) - smoothstep(0.0, 1.0, distance - u_border_size)));
}