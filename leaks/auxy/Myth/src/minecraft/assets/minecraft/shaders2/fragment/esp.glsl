#version 120

uniform sampler2D u_texture;
uniform vec3 u_color;
uniform float u_radius;
uniform vec2 u_texelSize;
uniform vec2 u_direction;
uniform float u_alpha;

void main() {
    vec4 texel = texture2D(u_texture, gl_TexCoord[0].xy);

    if (texel.a == 0) {
        float alpha = 0;
        for (float x = -u_radius; x <= u_radius; x++) {
            vec4 rofl = texture2D(u_texture, gl_TexCoord[0].xy + vec2(u_texelSize.x * x * u_direction.x, u_texelSize.y * x * u_direction.y));
            if (rofl.a != 0) {
                alpha += rofl.a;
            }
        }
        gl_FragColor += vec4(u_color.rgb, alpha / (u_radius * 2) * u_alpha);
    } else {
        gl_FragColor = vec4(texel.rgb, 0.0);
    }

}
