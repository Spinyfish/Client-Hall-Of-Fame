// Fork of https://glslsandbox.com/e#82368.0
// Removed shader toy emulation
// Relocated R3P definition
// Changed colour from green to blue

#ifdef GL_ES
precision mediump float;
#endif

uniform float time;
uniform vec2 resolution;

#define R3P 1.22074408460575947536

void mainImage(out vec4 fragColor, in vec2 fragCoord)
{
    vec2 uv = (fragCoord*2.-resolution.xy)/resolution.y;

    float s = 0.;
    for (float p = 0.; p < 100.; p++) {
        vec3 q = fract(.5+p*vec3(1./R3P, 1./(R3P*R3P), 1./(R3P*R3P*R3P)));
        float a = p*.001+time*(.02+q.z*.1);
        vec2 x = q.xy*mat2(sin(a*2.1), sin(a*4.13), sin(a*8.13), sin(a*4.18));
        float l = length(x-uv.xy);
        s += sin((l-q.z)*10.)/(1.+max(0., l-.01)*200.);
    }
    fragColor = mix(vec4(.0, .08, .2, 1), vec4(0, .15, .7, 1), max(0., (s*.9)+0.1));
}

void main(void)
{
    mainImage(gl_FragColor, gl_FragCoord.xy);
}