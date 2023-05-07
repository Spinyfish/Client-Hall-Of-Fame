#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 resolution;
uniform float time;

const float Pi = 3.14159;

const int   complexity      = 30;    // More points of color.
const float fluid_speed     = 20000.0;  // Drives speed, higher number will make it slower.
const float color_intensity = 0.3;
const float red = 0.1;
const float green = 0.2;
const float blue = 0.3;

void main()
{
  vec2 p=(2.0*gl_FragCoord.xy-resolution)/max(resolution.x,resolution.y);
  for(int i=1;i<complexity;i++)
  {
    vec2 newp=p + time*0.0003;
    newp.x+=0.6/float(i)*sin(float(i)*p.y+time/fluid_speed*float(i+2000)) + 0.5;
    newp.y+=0.6/float(i)*sin(float(i)*p.x+time/fluid_speed*float(i+1909)) - 0.5;
    p=newp;
  }
  vec3 col=vec3(color_intensity*sin(3.0*p.x)+red,color_intensity*sin(3.0*p.x)+green,color_intensity*sin(3.0*p.x)+blue);
  gl_FragColor=vec4(col, 1.0);
}