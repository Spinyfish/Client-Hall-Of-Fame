#ifdef GL_ES
precision mediump float;
#endif

#extension GL_OES_standard_derivatives : enable

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;

void main( void ) {
	vec2 st = gl_FragCoord.xy / resolution.xy;
	vec2 m = mouse + 0.0 / resolution;
	float c = sqrt(distance(m, st));
	gl_FragColor = vec4(st.x, st.y, 1.0, 1.0);
}