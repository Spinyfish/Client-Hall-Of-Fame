#version 120

uniform sampler2D texture;
uniform vec2 texelSize;
uniform vec2 direction;

uniform float sigma;

uniform vec3 mixColor = vec3(.2, .9, 0.0);
uniform float ratio = 0.025;

varying vec2 uv;

#define step texelSize * direction
#define radius 1.75 * sigma

float gauss(float x, float sigma) {
    float sq = x / sigma;
    return 1.0 / (abs(sigma) * 2.50662827) * exp(-0.5 * sq * sq);
}

void main(){
    vec4 color = texture2D(texture, uv) * gauss(0, sigma);
    for(float i = 0.0; i <= radius; i++){
        vec4 smplA = texture2D(texture, uv + step * i);
        vec4 smplB = texture2D(texture, uv - step * i);
        color += (smplA + smplB) * gauss(i, sigma);
    }

    gl_FragColor = vec4(mix(color.rgb, mixColor, ratio), 1.0);
}