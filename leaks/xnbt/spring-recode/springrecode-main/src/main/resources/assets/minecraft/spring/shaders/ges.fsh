float seg(in vec2 p, in vec2 a, in vec2 b) {
    vec2 pa = p-a, ba = b-a;
    float h = clamp( dot(pa,ba)/dot(ba,ba), 0.0, 1.0 );
    return length( pa - ba*h );
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 uv = (fragCoord - 0.5 * iResolution.xy) / iResolution.y;
    float a = atan(uv.y, uv.x);
    vec2 p = cos(a + iTime) * vec2(cos(0.5 * iTime), sin(0.3 * iTime));
    vec2 q = (cos(iTime)) * vec2(cos(iTime), sin(iTime));

    float d1 = length(uv - p);
    float d2 = length(uv - 0.);

    vec2 uv2 = 2. * cos(log(length(uv))*0.25 - 0.5 * iTime + log(vec2(d1,d2)/(d1+d2)));///(d1+d2);
    //uv = mix(uv, uv2, exp(-12. * length(uv)));
    //uv = uv2;

    vec2 fpos = fract(4. *  uv2) - 0.5;
    float d = max(abs(fpos.x), abs(fpos.y));
    float k = 5. / iResolution.y;
    float s = smoothstep(-k, k, 0.25 - d);
    vec3 col = vec3(s, 0.5 * s, 0.1-0.1 * s);
    col += 1./cosh(-2.5 * (length(uv - p) + length(uv))) * vec3(1,0.5,0.1);

    float c = cos(10. * length(uv2) + 4. * iTime);
    col += (0.5 + 0.5 * c) * vec3(0.5,1,1) *
    exp(-9. * abs(cos(9. * a + iTime) * uv.x
    + sin(9. * a + iTime) * uv.y
    + 0.1 * c));

    fragColor = vec4(col,1.0);
}