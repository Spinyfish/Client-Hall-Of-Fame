#ifdef GL_ES
precision highp float;
#endif

uniform vec2 resolution;
uniform float time;

float iTime = time;
vec2 iResolution = resolution;

vec2 UV;
float map(vec3 pos) {

    float ret = 0.0;

    ret += length(pos + vec3(
        (sin(iTime * 0.9 + pos.y * 6.0) - cos(pos.z * 0.823)) * 0.1,
        (cos(iTime * 0.8 + pos.x * 7.3) - cos(pos.x * 0.956)) * 0.1,
        cos(iTime * 2.3 - pos.y * 8.0 + pos.x * 4.4) * 0.05));

    return ret;

}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 uv = fragCoord/iResolution.xy - 0.5;
    uv.x *= (iResolution.x / iResolution.y);

    uv *= 1.75;

    vec3 off = vec3(1.0 / iResolution.xy, 0.0);

    vec3 col = vec3(0.0);

    for (int j=0; j<10; j++) {

        float js = pow(float(j) * 0.075, 2.5);

        vec3 pos = vec3(uv.x, uv.y, js - 0.5);
        float v =
        smoothstep(0.6 + js * 0.55, 0.5, map(pos))
        * smoothstep(0.5, 0.6 + js * 0.25, map(pos + vec3(0.1, 0.2, 0.0)));

        float w =
        smoothstep(0.9 + js * 0.55, 0.8, map(pos))
        * smoothstep(0.6, 1.5 + js * 0.25, map(pos - vec3(0.2, 0.1, 0.0)));


        col += v * vec3(0.5 + js, 0.5 - js, 1.0);

        col += w * vec3(0.05 + js, 0.0, 0.05 + js);

        col -= dot(pos - 15.5, off);
    }

    col /= 10.0;


    fragColor = vec4(smoothstep(0.0, 0.5, col), 1.0);
}
void main() {
    mainImage(gl_FragColor, gl_FragCoord.xy);
}