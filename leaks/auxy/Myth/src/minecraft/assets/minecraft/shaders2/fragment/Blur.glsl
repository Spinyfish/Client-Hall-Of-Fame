#version 120

uniform sampler2D currentTexture;
uniform vec2 texelSize;
uniform vec2 coords;
uniform float blurRadius;
uniform float blursigma;

//https://stackoverflow.com/questions/11282394/what-kind-of-blurs-can-be-implemented-in-pixel-shaders
//https://thebookofshaders.com/glossary/?search=exp


float CalcGauss(float x, float sigma)
{

    float sigmaMultiplication =  ((blursigma * blursigma));

    if (blursigma < 1) {
        return (exp(-.5 * x * x) * .4);
    } else {

        return (exp(-.5 * x * x / (sigmaMultiplication))/ blursigma) * .4;//bisschen umgeschrieben von der eigendlichen methode, da die eigendliche für einen full solid blur ist
        // (exp(-.5) -> Color correction
    }
}



void main() {

    vec3 color = vec3(0.0);
    for (float radiusF = -blurRadius; radiusF <= blurRadius; radiusF++) {
        color += texture2D(currentTexture, gl_TexCoord[0].st + radiusF * texelSize * coords).rgb * CalcGauss(radiusF, blurRadius / 2);
    }
    gl_FragColor = vec4(color, 1.0);
}