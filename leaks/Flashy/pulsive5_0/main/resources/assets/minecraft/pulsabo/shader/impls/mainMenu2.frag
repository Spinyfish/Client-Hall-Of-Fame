/*
 * Original shader from: https://www.shadertoy.com/view/NscXR8
 */

#ifdef GL_ES
precision mediump float;
#endif

// glslsandbox uniforms
uniform float time;
uniform vec2 resolution;

// shadertoy emulation
#define iTime time
#define iResolution vec3(resolution,1.)

// --------[ Original ShaderToy begins here ]---------- //
// 240 chars by Xor (with aspect ratio fix)
void mainImage(out vec4 O, vec2 I)
{
    vec3 p=iResolution,d = -.5*vec3(I+I-p.xy,p)/p.x,c = d-d, i=c;
    for(int x=0;x<100;++x) {
        if (i.x>=1.) break;
        p = c,
        p.z -= iTime+(i.x+=.01),
        p.xy *= mat2(sin((p.z*=.1)+vec4(0,1,33,0)));
        c += length(sin(p.yx)+cos(p.xz+iTime))*d;
    }
    O = vec4(10,6,9.5,14)/length(c);
}

// ORIGINAL: 258 chars
/*void mainImage(out vec4 O, in vec2 I) {
    vec3 d = .5-vec3(I,1)/iResolution, p, c;
    for(float i=0.;i<99.;i++) {
        p = c;
        p.z -= iTime+i*.01;
        p.z *= .1;
        p.xy *= mat2(sin(p.z),-cos(p.z),cos(p.z),sin(p.z));
        c += length(sin(p.yx)+cos(p.xz+iTime))*d;
    }
    O.rgb = vec3(5./length(c))*vec3(2.,.0,.5);
}*/
// --------[ Original ShaderToy ends here ]---------- //

void main(void)
{
    mainImage(gl_FragColor, gl_FragCoord.xy);
    gl_FragColor.a = 1.;
}