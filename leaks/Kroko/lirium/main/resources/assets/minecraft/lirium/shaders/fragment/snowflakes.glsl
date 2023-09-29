uniform float time;
uniform vec2 resolution;
#define iResolution resolution
#define iGlobalTime time
const float pi = 3.141592653589793;
float t = 0.0;

vec2 hash(in vec2 p) {
    return cos(t + sin(mat2(17.0, 5.0, 3.0, 257.0) * p - p) * 1234.5678);
}

float noise( in vec2 p )
{
	const float K1	= (sqrt(3.)-1.0)/2.0;
	const float K2	= (3.0-sqrt(3.0))/6.0;
	vec2 i 		= floor(p + (p.x + p.y) * K1);
	vec2 a 		= p - i + (i.x + i.y)*K2;
    vec2 o 		= (a.x > a.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
    vec2 b 		= a - o + K2;
    vec2 c 		= a - 1.0 + 2.0 * K2;
    vec3 h 		= (0.5 - vec3(dot(a, a), dot(b, b), dot(c, c))) * 3.0;
    vec3 n 		= vec3(dot(a, hash(i)), dot(b, hash(i + o)), dot(c, hash(i+1.0)));
    return dot(n, h*h*h*h*h) * 0.5 + 0.5;
}

float snow(vec2 uv,float scale)
{
	float w = smoothstep(1.0 , 0.0 , -uv.y*(scale/40.0));

	uv+=t/scale;
	uv.y+=t/scale;
	uv.x+=sin(uv.y + t * 0.25)/scale;

	uv*=scale;
	vec2 s=floor(uv), f=fract(uv), p;
	float k = 4.0, d;
	p= 0.5 + 0.3 * sin(11. * fract(sin((s+scale)*mat2(7,3,6,5))*5.0)) - f;
	d = length(p);
	k = min(d, k);
	k=smoothstep(0.0,k,sin(f.x+f.y)*0.01);
    return w * k;
}

void main( void ) {
	t = time * 0.75;
	gl_FragColor = vec4(0, 0, 0, 1);
	vec2 uv=(gl_FragCoord.xy*2.0-resolution.xy)/min(resolution.x,resolution.y);
	vec3 finalColor=vec3(0);
	float c = 0.0;

	c+=snow(uv, 30.0);
	c+=snow(uv, 15.0);
	c+=snow(uv, 10.0);
	c+=snow(uv, 5.0);

	finalColor=(vec3(c * 0.6));
	gl_FragColor += vec4(finalColor,1);
	gl_FragColor *= vec4(1, 1, 1, 0.4);

    vec2 v = ((gl_FragCoord.xy / resolution)-vec2(0.5, 0.5));
	gl_FragColor *= (1.0 - sqrt((v.x*v.x)+(v.y*v.y))) * 2.5;

	{
		vec2 p 	= gl_FragCoord.xy / resolution;
		p = 2.0 * p - 2.0;
		p.x *= resolution.x / resolution.y;

		p.x -= time * 0.125;

		float a = 0.5;
		float n = (gl_FragCoord.y / resolution.y);
		n *= n;
		n *= noise(p * 2.0) * a;

	    gl_FragColor.xyz += vec3(n) * 1.2;
	    gl_FragColor.a = n.r;
	}
}