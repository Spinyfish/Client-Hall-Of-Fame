#ifdef GL_ES
precision mediump float;
#endif

// glslsandbox uniforms
uniform float time;
uniform vec2 resolution;

// shadertoy emulation
#define iTime time
#define iResolution resolution

// --------[ Original ShaderToy begins here ]---------- //
/*Ethan Alexander Shulman 2020 - xaloez.com
3840x2160 60fps video https://www.youtube.com/watch?v=2ISSvdhVfwM
3840x2160 wallpaper xaloez.com/art/2020/EmbersandAshes.jpg*/

void mainImage(out vec4 fragColor, in vec2 fragCoord)
{
    float time = iTime;
    vec2 uv = (fragCoord*2.-iResolution.xy)/iResolution.y;

    float s = 0.;
    for (float p = 0.; p < 100.; p++) {
        #define R3P 1.22074408460575947536
        vec3 q = fract(.5+p*vec3(1./R3P, 1./(R3P*R3P), 1./(R3P*R3P*R3P)));
        float a = p*.001+time*(.02+q.z*.1);
        vec2 x = q.xy*mat2(sin(a*2.1), sin(a*4.13), sin(a*8.13), sin(a*4.18));
        float l = length(x-uv.xy);
        s += sin((l-q.z)*10.)/(1.+max(0., l-.01)*200.);
    }
    fragColor = mix(vec4(.0, .08, .1, 1), vec4(1, .5, .4, 1), max(0., (s*.9)+0.1));
}
// --------[ Original ShaderToy ends here ]---------- //

void main(void)
{
    mainImage(gl_FragColor, gl_FragCoord.xy);
}