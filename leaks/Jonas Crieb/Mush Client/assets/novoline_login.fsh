#version 130

precision mediump float;
uniform float time;
uniform vec2  mouse;
uniform vec2  resolution;

#define PI 3.14159265359

mat2 rotate2d(float angle){
	return mat2(cos(angle), -sin(angle), sin(angle), cos(angle));
}

void main(void){
   	vec2 p = (gl_FragCoord.xy * 2.0 - resolution) / min(resolution.x, resolution.y);
	p = rotate2d(time / 1.5 * PI) * p;
	float t = 0.02 / abs(abs(sin(time)) - length(p));
    	gl_FragColor = vec4(vec3(t) * vec3(0.0, 0.6, 1.0), 1.0);
}
	
	
	




	
	
	


