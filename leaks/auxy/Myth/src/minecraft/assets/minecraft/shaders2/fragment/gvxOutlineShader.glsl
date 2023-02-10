#version 120

uniform sampler2D u_texture;
uniform vec2 u_texelSize;
uniform vec2 u_direction;
uniform float u_radius;
uniform vec3 u_color;
uniform int u_option;
uniform int u_pass;

void main() {
    vec4 center = texture2D(u_texture, gl_TexCoord[0].st);
    if (center.a != 0) {
        gl_FragColor = vec4(0.0);
        return;
    }
    float totalAlpha = 0;
    /*float rad = u_radius * 2;
    for (float x = -rad; x <= rad; x++) {
        for (float y = -rad; y <= rad; y++) {
            vec4 texture = texture2D(u_texture, gl_TexCoord[0].st + vec2(x * u_texelSize.x, y * u_texelSize.y));
            if (texture.a != 0) {
                totalAlpha += clamp((rad * rad) - (x * x + y * y), 0, rad) / 100;
            }
        }
    }
    */
    for (float x = -u_radius; x <= u_radius; x++) {
        vec4 all = texture2D(u_texture, gl_TexCoord[0].st + u_texelSize * x * u_direction);
        totalAlpha += all.a;
    }
    gl_FragColor = vec4(u_color, totalAlpha);
}