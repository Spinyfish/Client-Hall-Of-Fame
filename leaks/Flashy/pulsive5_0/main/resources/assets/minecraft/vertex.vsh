//zajchu#8565
#version 120

attribute vec2 uv;
varying vec2 vUV;

void main() {
    gl_TexCoord[0] = gl_MultiTexCoord0;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    vUV = uv;
}