//zajchu#8565
#version 120

uniform sampler2D texture;
uniform vec2 resolution;
const float kernel = 7.0;
const float weight = 1.0;

void main()
{
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec3 sum = vec3(0);
    float pixelSize = 1.0 / resolution.y;

    // Vertical Blur
    vec3 accumulation = vec3(0);
    vec3 weightsum = vec3(0);
    for (float i = -kernel; i <= kernel; i++){
        accumulation += texture2D(texture, uv + vec2(0.0, i * pixelSize)).xyz * weight;
        weightsum += weight;
    }

    sum = accumulation / weightsum;

    gl_FragColor = vec4(sum, 1.0);
}