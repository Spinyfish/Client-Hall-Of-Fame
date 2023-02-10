#extension GL_OES_standard_derivatives : enable

#ifdef GL_ES
precision lowp float;
#endif

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;

vec3 color;
float rect(vec2 p, vec2 c, vec2 rad)
{
  vec2 distance = abs(p - c) - rad;
  return max(distance.x, distance.y);
}

float ring(vec2 p, vec2 c, float r1, float r2)
{
  if(p.x < c.x)
  {
    return 2.;
  }

  return abs(length(p - c) - r1) - r2;
}

float sub(float a, float b)
{
  return max(a, -b);
}

float add(float a, float b)
{
  return min(a, b);
}


float text_crimea(vec2 uv)
{
  float sd=1.;
	
  float w = .16;

  sd = add(sd, rect(uv, vec2(.0, .0), vec2(w, 1.+w)));
  sd = add(sd, rect(uv, vec2(.0, .0), vec2(1.+w, w)));
  
  sd = add(sd, rect(uv, vec2(-1.0, .5+w), vec2(w, 0.5)));
  sd = add(sd, rect(uv, vec2(1.0, -0.5-w), vec2(w, 0.5)));
	
  sd = add(sd, rect(uv, vec2(-0.5-w, -1.), vec2(0.5, w)));
  sd = add(sd, rect(uv, vec2(0.5+w, 1.0), vec2(0.5, w)));
  return sd;
}

vec3 hsv2rgb_smooth( in vec3 c )
{
    vec3 rgb = clamp( abs(mod(c.x*6.0+vec3(0.0,4.0,2.0),6.0)-3.0)-1.0, 0.0, 1.0 );

	rgb = rgb*rgb*(3.0-2.0*rgb); // cubic smoothing	

	return c.z * mix( vec3(1.0), rgb, c.y);
}

float map(in vec3 pos)
{
  vec2 p = pos.xy;
  float d = text_crimea(p);
  float dep = 0.1;
  vec2 e = vec2(d, abs(pos.z) - dep);
  d = min(max(e.x, e.y), 0.0) + length(max(e, 0.0));
  return d;
}

vec3 calc_normal(in vec3 pos)
{
  vec2 e = vec2(1.0, -1.0) * 0.5773;
  const float eps = 0.0005;

  return normalize(
    e.xyy * map(pos + e.xyy * eps) + 
    e.yyx * map(pos + e.yyx * eps) + 
    e.yxy * map(pos + e.yxy * eps) + 
    e.xxx * map(pos + e.xxx * eps)
  );
}

vec3 render(vec2 p)
{
  float an = time * 10.;
  vec3 ro = vec3(2.0, 0.4, 2.0);
  ro.xy *= mat2(cos(an), -sin(an), sin(an), cos(an));
  vec3 ta = vec3(0.0, 0.0, 0.0);
  vec3 ww = normalize(ta - ro);
  vec3 uu = normalize(cross(ww, vec3(0.0, 1.0, 0.0)));
  vec3 vv = normalize(cross(uu, ww));
  vec3 total = vec3(0.0);
  vec3 rd = normalize(p.x * uu + p.y * vv + 1.5 * ww);
  
  const float tmax = 5.0;
  float t = 0.0;
  for (int i = 0; i < 256; i++)
  {
    float h = map(ro + t * rd);
    if (h < 0.0001 || t > tmax)
    {
      break;
    }

    t += h;
  }

  if (t < tmax)
  {
    vec3 normal = calc_normal(ro + t * rd);
    float difference = clamp(dot(normal, vec3(0.57703)), 0.0, 1.0);
    float amb = 0.5 + 0.5 * dot(normal, vec3(0.0, 0.0, 1.0));
    color = hsv2rgb_smooth(vec3(abs(4.-rd.y*3.),0.6,.7)) * amb + vec3(0.8, 0.7, 0.5) * difference;
  }

  total += color;
  return total;
}

vec3 rgb(float r, float g, float b) {
  return vec3(r/255., g/255., b/255.);
}

void main(void)
{
  const float PI = 3.1415926535;

  vec2 p = (gl_FragCoord.xy * 2. - resolution.xy) / resolution.y;
  vec2 uv = p;
  vec2 st = gl_FragCoord.xy / resolution.xy;

  float w = sin(
    (uv.x + uv.y - time * .75 + sin(1.5 * uv.x + 4.5 * uv.y) * PI * .3) 
    * PI * .6
  );

  uv *= 8. + (.99 - .1 * w);
  color = vec3(1, 0, 0);
  float tilt = .01 * sin(10. * st.x + time);
  
  if (st.y > (2. / 3.) + tilt) 
  {
    color = rgb(36., 36., 36.);
  } 
  else if (st.y > (1. / 3.) + tilt) 
  {
    color = rgb(255., 51., 64.);
  } 
  else 
  {
    color = rgb(249., 233., 116.);
  }

  color += w * .225;
  color *= .8;
  gl_FragColor = vec4(render(p), 1.);
}