#version 120

uniform sampler2D u_texture;
uniform sampler2D u_texture2;
uniform vec2 u_texelSize;
uniform vec2 u_direction;
uniform float u_blurRadius;
uniform float u_sigma;
uniform bool avoidTexture;
uniform bool full;
uniform float u_alphaModifier;
uniform vec3 u_color;

float CalcGauss(float x, float sigma)
{

    float sigmaMultiplication =  ((u_sigma * u_sigma));

    if (u_sigma < 1) {
        return (exp(-.5 * x * x) * .4);
    } else {

        return (exp(-.5 * x * x / (sigmaMultiplication))/ u_sigma) * .4;//bisschen umgeschrieben von der eigendlichen methode, da die eigendliche für einen full solid blur ist
        // (exp(-.5) -> Color correction
    }
}



void main() {

    if (u_direction.y == 1 && avoidTexture) {
        if (!full ? false : texture2D(u_texture2, gl_TexCoord[0].st).a != 0.0) discard;
    }

    vec2 texCoord = gl_TexCoord[0].st;
    vec4 color = vec4(0.0);
    float horizontalPass = u_direction.x * u_texelSize.x;
    float verticalPass = u_direction.y * u_texelSize.y;
    for (float x = -u_blurRadius; x <= u_blurRadius; x++) {
        color += texture2D(u_texture, vec2(texCoord.x + x * horizontalPass, texCoord.y + x * verticalPass)) * CalcGauss(x, u_sigma);
    }

    gl_FragColor = vec4(u_color, min(color.a * u_alphaModifier, 1.0));
}