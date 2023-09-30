#extension GL_OES_standard_derivatives : enable

#ifdef GL_ES
precision highp float;
#endif

#define OCTAVES 3

uniform vec2 resolution;
uniform vec2 mouse;
uniform float time;

float random(in vec2 st)
{
	return fract(cos(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

float noise(in vec2 st)
{
	vec2 i = floor(st);
	vec2 f = fract(st);
	
	float a = random(i);
	float b = random(i + vec2(1.0, 0.0));
	float c = random(i + vec2(0.0, 1.0));
	float d = random(i + vec2(1.0, 1.0));
	
	vec2 u = f * f * (3.0 - 2.0 * f);
	
	return mix(a, b, u.x) + (c - a)* u.y * (1.0 - u.x) + (d - b) * u.x * u.y;
}

float fbm(in vec2 st)
{
	float val = 0.0;
	float amp = 0.5;
	
	for (int i = 0; i < OCTAVES; i++)
	{
		val += amp * noise(st);
		st *= 2.0;
		amp *= 0.5;
	}
	return val;
}

void main()
{
	vec2 st = gl_FragCoord.xy / resolution.xy;
	st.x *= resolution.x / resolution.y;
	
	vec3 color = vec3(0,0,0);
	vec3 color1 = vec3(0,0,1);
	color.b += fbm(st * 2.0);
	
	gl_FragColor = vec4(color, 1);
}