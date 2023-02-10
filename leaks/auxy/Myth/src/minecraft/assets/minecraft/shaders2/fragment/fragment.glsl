#version 120

uniform vec2 resolution;
uniform vec3 fistColor;
uniform vec3 secondColor;

void main() {

    vec3 color = vec3(firstColor.xyz);

    vec2 gradient = gl_FragCoord.xy / resolution.xy;

    color.xyz = firstColor.xyz / secondColor.xyz - 0.005 + gradient.x;

    gl_FragColor = vec4(color, 1.0);
}
