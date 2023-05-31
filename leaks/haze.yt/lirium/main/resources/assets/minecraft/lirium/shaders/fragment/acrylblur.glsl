//Trying to recreate the windows 10 "Acryl" blur -> https://docs.microsoft.com/en-us/windows/apps/design/style/acrylic
//Noise functions from shadertoy -> https://www.shadertoy.com/view/Md2yWh

#version 120

uniform sampler2D currentTexture;
uniform vec2 texelSize;
uniform vec2 coords;
uniform float blurRadius;
uniform float blursigma;
uniform sampler2D texture20;

//Blur function
float CalcGauss(float x)
{

    float sigmaMultiplication = ((blursigma * blursigma));

    if (blursigma < 1) {
        return (exp(-.5 * x * x) * .4);
    } else {
        return (exp(-.5 * x * x / (sigmaMultiplication)) / blursigma) * .4;//bisschen umgeschrieben von der eigendlichen methode, da die eigendliche für einen full solid blur ist
        // (exp(-.5) -> Color correction
    }

}
float hash(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + 19.19);
    return fract((p3.x + p3.y) * p3.z);
}

const vec2 add = vec2(1.0, 0.0);
float noise(vec2 x) {
    vec2 p = floor(x);
    vec2 f = fract(x);
    f = f * f * (3.0 - 2.0 * f);
    float res = mix(mix(hash(p), hash(p + add.xy), f.x),
                    mix(hash(p + add.yx), hash(p + add.xx), f.x), f.y);
    return res;
}

float fnoise(vec2 uv) {
    float f = 0.0;
    mat2 m = mat2(1.6, 1.2, -1.2, 1.6);
    f = 0.5000 * noise(uv); uv = m * uv;
    f += 0.2500 * noise(uv); uv = m * uv;
    f += 0.1250 * noise(uv); uv = m * uv;
    f += 0.0625 * noise(uv); uv = m * uv;
    return f;
}

const float f = 0.5 * (1.0 + sqrt(2.0));
float repeat;
float lerpx(vec2 uv) {
    float v = fnoise(uv + vec2(-repeat * 0.5, 0)) * ((uv.x) / repeat);
    v += fnoise(uv + vec2(repeat * 0.5, 0)) * ((repeat - uv.x) / repeat);
    return mix(v, f * pow(v, f), 4.0 * ((uv.x) / repeat) * ((repeat - uv.x) / repeat));
}

float lerpy(vec2 uv) {
    float v = lerpx(uv + vec2(0, -repeat * 0.5)) * ((uv.y) / repeat);
    v += lerpx(uv + vec2(0, repeat * 0.5)) * ((repeat - uv.y) / repeat);
    return mix(v, f * pow(v, f), 4.0 * ((uv.y) / repeat) * ((repeat - uv.y) / repeat));
}

vec3 noisetile(vec2 uv) {
    return vec3(clamp(lerpy(uv), 0.0, 1.0));
}

//https://github.com/CesiumGS/cesium/blob/main/Source/Shaders/Builtin/Functions/saturation.glsl
vec3 czm_saturation(vec3 rgb, float adjustment)
{
    // Algorithm from Chapter 16 of OpenGL Shading Language
    const vec3 W = vec3(0.2125, 0.7154, 0.0721);
    vec3 intensity = vec3(dot(rgb, W));
    return mix(intensity, rgb, adjustment);
}

void main() {

    vec2 texCoord = gl_TexCoord[0].st;

    float alpha = texture2D(texture20, texCoord).a;
    if (coords.x == 0.0 && alpha == 0.0) {
        discard;
    }

    vec3 color = vec3(0.0);
    for (float radiusF = -blurRadius; radiusF <= blurRadius; radiusF++) {
        color += texture2D(currentTexture, gl_TexCoord[0].st + radiusF * texelSize * coords).rgb * CalcGauss(radiusF);
    }

    const float scale = 49.0;
    float rc = 6.0+1.5* .13;
    float sc = 3.5+0.9* .08;

    repeat = 2.0+(scale-1.0)*pow(rc, 2.0);
    float size = 1.0+scale*pow(sc, 2.0);

    // coords, repeat
    vec2 uv = -1.0+2.0*gl_FragCoord.xy / texelSize.xy;
    uv = mod(uv*size, repeat);

    vec3 colorNoise = noisetile(uv) * .09;

    vec3 blurWithSaturation = czm_saturation(color, 1.1);

    vec3 blurWithSaturationAndNoise = blurWithSaturation;

    gl_FragColor = vec4(blurWithSaturationAndNoise, 1.0);
}
