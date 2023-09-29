#version 120

uniform sampler2D inTexture, textureToCheck;
uniform vec2 texelSize, direction;
uniform float radius;
uniform float weights[256];

//https://github.com/CesiumGS/cesium/blob/main/Source/Shaders/Builtin/Functions/saturation.glsl
vec4 czm_saturation(vec3 rgb, float adjustment)
{
    // Algorithm from Chapter 16 of OpenGL Shading Language
    const vec3 W = vec3(0.2125, 0.7154, 0.0721);
    vec3 intensity = vec3(dot(rgb, W));
    return vec4(mix(intensity, rgb, adjustment), 1.0);
}

void main() {
    vec2 texCoord = gl_TexCoord[0].st;
    if (direction.x == 0.0)
    if (texture2D(textureToCheck, texCoord).a > 0.0) discard;

    vec4 color = texture2D(inTexture, texCoord);
    color.rgb *= color.a;
    color *= weights[0];
    for (float radiusF = -radius; radiusF <= radius; radiusF++) {
        vec2 offset = radiusF * texelSize * direction;
        vec2 leftDirection = texCoord - offset;
        vec2 rightDirection = texCoord + offset;
        vec4 leftTexture = texture2D(inTexture, leftDirection);
        vec4 rightTexture = texture2D(inTexture, rightDirection);
        leftTexture.rgb *= leftTexture.a;
        rightTexture.rgb *= rightTexture.a;

        color += (leftTexture + rightTexture) * weights[int(radiusF)];
    }

    gl_FragColor = vec4(color.rgb / color.a, color.a);
}