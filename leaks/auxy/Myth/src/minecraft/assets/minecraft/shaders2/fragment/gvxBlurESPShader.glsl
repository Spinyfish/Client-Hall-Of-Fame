#version 120

uniform sampler2D u_texture;
uniform vec2 u_texelSize;
uniform vec2 u_direction;
uniform float u_radius;
uniform vec3 u_color;
uniform float u_sigma;
uniform float u_kernelsize;

 
float gauss(float x) {
    float sq = x / u_sigma;
    return 1.0 / (abs(u_sigma) * 2.50662827) * exp(-0.5 * sq * sq);
}
void main() {
    float Pi = 6.28318530718;
    float Directions = u_kernelsize;
    float Quality = u_sigma;
    float totalAlpha = 0;

    vec4 center = texture2D(u_texture, gl_TexCoord[0].st);
      vec4 color = vec4(0);
    if (center.a != 0) {
        gl_FragColor = vec4(0.0);
        return;
    }
    for (float d = 0; d < Pi; d += Pi / Directions) {
        for (float i = 1 / Quality; i < 1; i += 1 / Quality) {
            totalAlpha += texture2D(u_texture, gl_TexCoord[0].st + u_texelSize * vec2(cos(d), sin(d)) * u_radius * i).a;
          // color += texture2D(u_texture, gl_TexCoord[0].st + vec2(u_texelSize.x * vec2(cos(d), sin(d))) * u_radius) * gauss(i);
        }
    }

    totalAlpha /= Quality * Directions - 9;

    gl_FragColor = vec4(u_color, totalAlpha);
}