#version 330

uniform vec2 resolution;

out vec4 outColor;

void main() {
  vec2 uv = gl_FragCoord.xy / resolution.xy;
  uv -= 0.5;
  uv.x *= resolution.x / resolution.y;
  float r = length(uv);
  float alpha = 1.0;
  if (r > 0.5) {
    discard;
  } else {
    float blur = 1.0 - smoothstep(0.2, 0.5, r);
    alpha = 0.5 + 0.5 * blur;
  }
  outColor = vec4(1, 1, 1, alpha);
}