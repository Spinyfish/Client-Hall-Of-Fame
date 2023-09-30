package me.jinthium.straight.api.shader;

import me.jinthium.straight.api.util.MinecraftInstance;
import me.jinthium.straight.impl.utils.file.FileUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class ShaderUtil implements MinecraftInstance {
    private final int programID;

    public ShaderUtil(String fragmentShaderLoc, String vertexShaderLoc) {
        int program = glCreateProgram();
        try {
            int fragmentShaderID;
            String glowShader = """
                    #version 120

                    uniform sampler2D textureIn, textureToCheck;
                    uniform vec2 texelSize, direction;
                    uniform vec3 color;
                    uniform bool avoidTexture;
                    uniform float exposure, radius;
                    uniform float weights[256];

                    #define offset direction * texelSize

                    void main() {
                        if (direction.y == 1 && avoidTexture) {
                            if (texture2D(textureToCheck, gl_TexCoord[0].st).a != 0.0) discard;
                        }
                        vec4 innerColor = texture2D(textureIn, gl_TexCoord[0].st);
                        innerColor.rgb *= innerColor.a;
                        innerColor *= weights[0];
                        for (float r = 1.0; r <= radius; r++) {
                            vec4 colorCurrent1 = texture2D(textureIn, gl_TexCoord[0].st + offset * r);
                            vec4 colorCurrent2 = texture2D(textureIn, gl_TexCoord[0].st - offset * r);

                            colorCurrent1.rgb *= colorCurrent1.a;
                            colorCurrent2.rgb *= colorCurrent2.a;

                            innerColor += (colorCurrent1 + colorCurrent2) * weights[int(r)];
                        }

                        gl_FragColor = vec4(innerColor.rgb / innerColor.a, mix(innerColor.a, 1.0 - exp(-innerColor.a * exposure), step(0.0, direction.y)));
                    }
                    """;
            String chams = """
                    #version 120

                    uniform sampler2D textureIn;
                    uniform vec4 color;
                    void main() {
                        float alpha = texture2D(textureIn, gl_TexCoord[0].st).a;
                        gl_FragColor = vec4(color.rgb, color.a * mix(0.0, alpha, step(0.0, alpha)));
                    }
                    """;
            String roundRectTexture = """
                    #version 120

                    uniform vec2 location, rectSize;
                    uniform sampler2D textureIn;
                    uniform float radius, alpha;

                    float roundedBoxSDF(vec2 centerPos, vec2 size, float radius) {
                        return length(max(abs(centerPos) -size, 0.)) - radius;
                    }


                    void main() {
                        float distance = roundedBoxSDF((rectSize * .5) - (gl_TexCoord[0].st * rectSize), (rectSize * .5) - radius - 1., radius);
                        float smoothedAlpha =  (1.0-smoothstep(0.0, 2.0, distance)) * alpha;
                        gl_FragColor = vec4(texture2D(textureIn, gl_TexCoord[0].st).rgb, smoothedAlpha);
                    }""";
            String roundRectOutline = """
                    #version 120

                    uniform vec2 location, rectSize;
                    uniform vec4 color, outlineColor;
                    uniform float radius, outlineThickness;

                    float roundedSDF(vec2 centerPos, vec2 size, float radius) {
                        return length(max(abs(centerPos) - size + radius, 0.0)) - radius;
                    }

                    void main() {
                        float distance = roundedSDF(gl_FragCoord.xy - location - (rectSize * .5), (rectSize * .5) + (outlineThickness *.5) - 1.0, radius);

                        float blendAmount = smoothstep(0., 2., abs(distance) - (outlineThickness * .5));

                        vec4 insideColor = (distance < 0.) ? color : vec4(outlineColor.rgb,  0.0);
                        gl_FragColor = mix(outlineColor, insideColor, blendAmount);

                    }""";
            String kawaseUpBloom = """
                    #version 120

                    uniform sampler2D inTexture, textureToCheck;
                    uniform vec2 halfpixel, offset, iResolution;
                    uniform int check;

                    void main() {
                      //  if(check && texture2D(textureToCheck, gl_TexCoord[0].st).a > 0.0) discard;
                        vec2 uv = vec2(gl_FragCoord.xy / iResolution);

                        vec4 sum = texture2D(inTexture, uv + vec2(-halfpixel.x * 2.0, 0.0) * offset);
                        sum.rgb *= sum.a;
                        vec4 smpl1 =  texture2D(inTexture, uv + vec2(-halfpixel.x, halfpixel.y) * offset);
                        smpl1.rgb *= smpl1.a;
                        sum += smpl1 * 2.0;
                        vec4 smp2 = texture2D(inTexture, uv + vec2(0.0, halfpixel.y * 2.0) * offset);
                        smp2.rgb *= smp2.a;
                        sum += smp2;
                        vec4 smp3 = texture2D(inTexture, uv + vec2(halfpixel.x, halfpixel.y) * offset);
                        smp3.rgb *= smp3.a;
                        sum += smp3 * 2.0;
                        vec4 smp4 = texture2D(inTexture, uv + vec2(halfpixel.x * 2.0, 0.0) * offset);
                        smp4.rgb *= smp4.a;
                        sum += smp4;
                        vec4 smp5 = texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset);
                        smp5.rgb *= smp5.a;
                        sum += smp5 * 2.0;
                        vec4 smp6 = texture2D(inTexture, uv + vec2(0.0, -halfpixel.y * 2.0) * offset);
                        smp6.rgb *= smp6.a;
                        sum += smp6;
                        vec4 smp7 = texture2D(inTexture, uv + vec2(-halfpixel.x, -halfpixel.y) * offset);
                        smp7.rgb *= smp7.a;
                        sum += smp7 * 2.0;
                        vec4 result = sum / 12.0;
                        gl_FragColor = vec4(result.rgb / result.a, mix(result.a, result.a * (1.0 - texture2D(textureToCheck, gl_TexCoord[0].st).a),check));
                    }""";
            String kawaseDownBloom = """
                    #version 120

                    uniform sampler2D inTexture;
                    uniform vec2 offset, halfpixel, iResolution;

                    void main() {
                        vec2 uv = vec2(gl_FragCoord.xy / iResolution);
                        vec4 sum = texture2D(inTexture, gl_TexCoord[0].st);
                        sum.rgb *= sum.a;
                        sum *= 4.0;
                        vec4 smp1 = texture2D(inTexture, uv - halfpixel.xy * offset);
                        smp1.rgb *= smp1.a;
                        sum += smp1;
                        vec4 smp2 = texture2D(inTexture, uv + halfpixel.xy * offset);
                        smp2.rgb *= smp2.a;
                        sum += smp2;
                        vec4 smp3 = texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset);
                        smp3.rgb *= smp3.a;
                        sum += smp3;
                        vec4 smp4 = texture2D(inTexture, uv - vec2(halfpixel.x, -halfpixel.y) * offset);
                        smp4.rgb *= smp4.a;
                        sum += smp4;
                        vec4 result = sum / 8.0;
                        gl_FragColor = vec4(result.rgb / result.a, result.a);
                    }""";
            String kawaseUp = """
                    #version 120

                    uniform sampler2D inTexture, textureToCheck;
                    uniform vec2 halfpixel, offset, iResolution;
                    uniform int check;

                    void main() {
                        vec2 uv = vec2(gl_FragCoord.xy / iResolution);
                        vec4 sum = texture2D(inTexture, uv + vec2(-halfpixel.x * 2.0, 0.0) * offset);
                        sum += texture2D(inTexture, uv + vec2(-halfpixel.x, halfpixel.y) * offset) * 2.0;
                        sum += texture2D(inTexture, uv + vec2(0.0, halfpixel.y * 2.0) * offset);
                        sum += texture2D(inTexture, uv + vec2(halfpixel.x, halfpixel.y) * offset) * 2.0;
                        sum += texture2D(inTexture, uv + vec2(halfpixel.x * 2.0, 0.0) * offset);
                        sum += texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset) * 2.0;
                        sum += texture2D(inTexture, uv + vec2(0.0, -halfpixel.y * 2.0) * offset);
                        sum += texture2D(inTexture, uv + vec2(-halfpixel.x, -halfpixel.y) * offset) * 2.0;

                        gl_FragColor = vec4(sum.rgb /12.0, mix(1.0, texture2D(textureToCheck, gl_TexCoord[0].st).a, check));
                    }
                    """;
            String kawaseDown = """
                    #version 120

                    uniform sampler2D inTexture;
                    uniform vec2 offset, halfpixel, iResolution;

                    void main() {
                        vec2 uv = vec2(gl_FragCoord.xy / iResolution);
                        vec4 sum = texture2D(inTexture, gl_TexCoord[0].st) * 4.0;
                        sum += texture2D(inTexture, uv - halfpixel.xy * offset);
                        sum += texture2D(inTexture, uv + halfpixel.xy * offset);
                        sum += texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset);
                        sum += texture2D(inTexture, uv - vec2(halfpixel.x, -halfpixel.y) * offset);
                        gl_FragColor = vec4(sum.rgb * .125, 1.0);
                    }
                    """;
            String gradientMask = """
                    #version 120

                    uniform vec2 location, rectSize;
                    uniform sampler2D tex;
                    uniform vec3 color1, color2, color3, color4;
                    uniform float alpha;

                    #define NOISE .5/255.0

                    vec3 createGradient(vec2 coords, vec3 color1, vec3 color2, vec3 color3, vec3 color4){
                        vec3 color = mix(mix(color1.rgb, color2.rgb, coords.y), mix(color3.rgb, color4.rgb, coords.y), coords.x);
                        //Dithering the color from https://shader-tutorial.dev/advanced/color-banding-dithering/
                        color += mix(NOISE, -NOISE, fract(sin(dot(coords.xy, vec2(12.9898,78.233))) * 43758.5453));
                        return color;
                    }

                    void main() {
                        vec2 coords = (gl_FragCoord.xy - location) / rectSize;
                        float texColorAlpha = texture2D(tex, gl_TexCoord[0].st).a;
                        gl_FragColor = vec4(createGradient(coords, color1, color2, color3, color4), texColorAlpha * alpha);
                    }""";
            String mask = """
                    #version 120

                    uniform vec2 location, rectSize;
                    uniform sampler2D u_texture, u_texture2;
                    void main() {
                        vec2 coords = (gl_FragCoord.xy - location) / rectSize;
                        float texColorAlpha = texture2D(u_texture, gl_TexCoord[0].st).a;
                        vec3 tex2Color = texture2D(u_texture2, gl_TexCoord[0].st).rgb;
                        gl_FragColor = vec4(tex2Color, texColorAlpha);
                    }""";
            String gradient = """
                    #version 120

                    uniform vec2 location, rectSize;
                    uniform sampler2D tex;
                    uniform vec4 color1, color2, color3, color4;
                    #define NOISE .5/255.0

                    vec4 createGradient(vec2 coords, vec4 color1, vec4 color2, vec4 color3, vec4 color4){
                        vec4 color = mix(mix(color1, color2, coords.y), mix(color3, color4, coords.y), coords.x);
                        //Dithering the color
                        // from https://shader-tutorial.dev/advanced/color-banding-dithering/
                        color += mix(NOISE, -NOISE, fract(sin(dot(coords.xy, vec2(12.9898, 78.233))) * 43758.5453));
                        return color;
                    }

                    void main() {
                        vec2 coords = (gl_FragCoord.xy - location) / rectSize;
                        gl_FragColor = createGradient(coords, color1, color2, color3, color4);
                    }""";
            String roundedRectGradient = """
                    #version 120

                    uniform vec2 location, rectSize;
                    uniform vec4 color1, color2, color3, color4;
                    uniform float radius;

                    #define NOISE .5/255.0

                    float roundSDF(vec2 p, vec2 b, float r) {
                        return length(max(abs(p) - b , 0.0)) - r;
                    }

                    vec4 createGradient(vec2 coords, vec4 color1, vec4 color2, vec4 color3, vec4 color4){
                        vec4 color = mix(mix(color1, color2, coords.y), mix(color3, color4, coords.y), coords.x);
                        //Dithering the color
                        // from https://shader-tutorial.dev/advanced/color-banding-dithering/
                        color += mix(NOISE, -NOISE, fract(sin(dot(coords.xy, vec2(12.9898, 78.233))) * 43758.5453));
                        return color;
                    }

                    void main() {
                        vec2 st = gl_TexCoord[0].st;
                        vec2 halfSize = rectSize * .5;
                       \s
                       // use the bottom leftColor as the alpha
                        float smoothedAlpha =  (1.0-smoothstep(0.0, 2., roundSDF(halfSize - (gl_TexCoord[0].st * rectSize), halfSize - radius - 1., radius)));
                        vec4 gradient = createGradient(st, color1, color2, color3, color4);    gl_FragColor = vec4(gradient.rgb, gradient.a * smoothedAlpha);
                    }""";
            String roundedRect = """
                    #version 120
                                        
                    uniform vec2 u_size;
                    uniform float u_radius;
                    uniform vec4 u_color;
                                        
                    void main(void)
                    {
                        gl_FragColor = vec4(u_color.rgb, u_color.a * smoothstep(1.0, 0.0, length(max((abs(gl_TexCoord[0].st - 0.5) + 0.5) * u_size - u_size + u_radius, 0.0)) - u_radius + 0.5));
                    }""";
            String kawaseUpGlow = """
                    #version 120

                    uniform sampler2D inTexture, textureToCheck;
                    uniform vec2 halfpixel, offset, iResolution;
                    uniform bool check;
                    uniform float lastPass;
                    uniform float exposure;

                    void main() {
                        if(check && texture2D(textureToCheck, gl_TexCoord[0].st).a != 0.0) discard;
                        vec2 uv = vec2(gl_FragCoord.xy / iResolution);

                        vec4 sum = texture2D(inTexture, uv + vec2(-halfpixel.x * 2.0, 0.0) * offset);
                        sum.rgb *= sum.a;
                        vec4 smpl1 =  texture2D(inTexture, uv + vec2(-halfpixel.x, halfpixel.y) * offset);
                        smpl1.rgb *= smpl1.a;
                        sum += smpl1 * 2.0;
                        vec4 smp2 = texture2D(inTexture, uv + vec2(0.0, halfpixel.y * 2.0) * offset);
                        smp2.rgb *= smp2.a;
                        sum += smp2;
                        vec4 smp3 = texture2D(inTexture, uv + vec2(halfpixel.x, halfpixel.y) * offset);
                        smp3.rgb *= smp3.a;
                        sum += smp3 * 2.0;
                        vec4 smp4 = texture2D(inTexture, uv + vec2(halfpixel.x * 2.0, 0.0) * offset);
                        smp4.rgb *= smp4.a;
                        sum += smp4;
                        vec4 smp5 = texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset);
                        smp5.rgb *= smp5.a;
                        sum += smp5 * 2.0;
                        vec4 smp6 = texture2D(inTexture, uv + vec2(0.0, -halfpixel.y * 2.0) * offset);
                        smp6.rgb *= smp6.a;
                        sum += smp6;
                        vec4 smp7 = texture2D(inTexture, uv + vec2(-halfpixel.x, -halfpixel.y) * offset);
                        smp7.rgb *= smp7.a;
                        sum += smp7 * 2.0;
                        vec4 result = sum / 12.0;
                        gl_FragColor = vec4(result.rgb / result.a, mix(result.a, 1.0 - exp(-result.a * exposure), step(0.0, lastPass)));
                    }""";
            String backgroundShader = """
                    /*
                     * Original shader from: https://www.shadertoy.com/view/7dBfDt
                     */


                    #ifdef GL_ES
                    precision highp float;
                    #endif

                    // glslsandbox uniforms
                    uniform float time;
                    uniform vec2 resolution;

                    // shadertoy emulation
                    #define iTime time
                    #define iResolution resolution

                    // Emulate some GLSL ES 3.x
                    float tanh(float x) {
                        float ex = exp(2.0 * x);
                        return ((ex - 1.) / (ex + 1.));
                    }

                    // --------[ Original ShaderToy begins here ]---------- //
                    #define pi 3.14159

                    #define thc(a,b) tanh(a*cos(b))/tanh(a)
                    #define ths(a,b) tanh(a*sin(b))/tanh(a)
                    #define sabs(x) sqrt(x*x+1e-2)
                    //#define sabs(x, k) sqrt(x*x+k)-0.1

                    #define Rot(a) mat2(cos(a), -sin(a), sin(a), cos(a))

                    float cc(float a, float b) {
                        float f = thc(a, b);
                        return sign(f) * pow(abs(f), 0.25);
                    }

                    float cs(float a, float b) {
                        float f = ths(a, b);
                        return sign(f) * pow(abs(f), 0.25);
                    }

                    vec3 pal(in float t, in vec3 a, in vec3 b, in vec3 c, in vec3 d) {
                        return a + b*cos( 6.28318*(c*t+d) );
                    }

                    float h21(vec2 a) {
                        return fract(sin(dot(a.xy, vec2(12.9898, 78.233))) * 43758.5453123);
                    }

                    float mlength(vec2 uv) {
                        return max(abs(uv.x), abs(uv.y));
                    }

                    float mlength(vec3 uv) {
                        return max(max(abs(uv.x), abs(uv.y)), abs(uv.z));
                    }

                    float smin(float a, float b) {
                        float k = 0.12;
                        float h = clamp(0.5 + 0.5 * (b-a) / k, 0.0, 1.0);
                        return mix(b, a, h) - k * h * (1.0 - h);
                    }

                    float h21 (vec2 uv, float sc) {
                        uv = mod(uv, sc);
                        return fract(sin(dot(uv, vec2(12.9898, 78.233)))*43758.5453123);
                    }

                    float line(vec2 uv, float width) {
                        return max(-uv.y + width, abs(uv.x));
                    }

                    float curve(vec2 uv) {
                        return abs(length(uv-0.5) - 0.5);
                    }

                    float shape(vec2 uv, vec4 h, float width) {
                        if (h.x + h.y + h.z + h.w == 0.)
                            return 0.;
                            \s
                        // center circle to round off line segments
                        float d = length(uv);    \s
                            \s
                        // draw line segment from center to edge (-width offset for center circle)
                        d = mix(d, min(d, line(uv,     width)), h.x);
                        d = mix(d, min(d, line(uv.yx,  width)), h.y);
                        d = mix(d, min(d, line(-uv,    width)), h.z);
                        d = mix(d, min(d, line(-uv.yx, width)), h.w);
                       \s
                        // draw quarter circle between 2 edges
                        d = mix(d, min(d, curve(vec2(uv.x, uv.y))),   h.x * h.y);
                        d = mix(d, min(d, curve(vec2(uv.x, -uv.y))),  h.y * h.z);
                        d = mix(d, min(d, curve(vec2(-uv.x, -uv.y))), h.z * h.w);
                        d = mix(d, min(d, curve(vec2(-uv.x, uv.y))),  h.w * h.x);
                       \s
                        float k = 6./iResolution.y;
                        return smoothstep(-k, k, -d + width);
                    }

                    void mainImage( out vec4 fragColor, in vec2 fragCoord )
                    {
                        vec2 uv = (fragCoord - 0.5 * iResolution.xy) / iResolution.y;
                           \s
                        // change me!
                        float width = 0.25;
                        float threshold = 0.5; \s
                       \s
                        float sc = 18.;
                        // uv.x = abs(uv.x);
                        // uv += floor(iTime)/sc;
                           \s
                        uv.x += 10. + 0.1 * iTime;   \s
                           \s
                        vec2 ipos = floor(sc * uv) + 0.;
                        vec2 fpos = fract(sc * uv) - 0.5;
                       \s
                        // arbitrary values - hash repetition, offset
                        float rep = 302.;\s
                        float val = 0.01;  \s
                       \s
                        // Checkerboard pattern:
                        // black cells choose edges for themselves + white cells
                        // white cells look at black cells to find edges
                        // construct pattern for cell based on edge configuration
                        float s = 0.;
                        if (mod(ipos.x + ipos.y, 2.) == 0.) {
                            vec4 h = vec4(h21(ipos,            rep),  // up
                                          h21(ipos + val,      rep),  // right
                                          h21(ipos + 2. * val, rep),  // down
                                          h21(ipos + 3. * val, rep)); // left ( I think* )
                            h = step(h, vec4(threshold));
                            s = shape(fpos, h, width);
                        } else {
                            vec4 h = vec4(h21(ipos + vec2(0,1) + 2. * val, rep),  // up's down
                                          h21(ipos + vec2(1,0) + 3. * val, rep),  // right's left
                                          h21(ipos - vec2(0,1),            rep),  // down's up
                                          h21(ipos - vec2(1,0) + val,      rep)); // left's right
                            h = step(h, vec4(threshold));
                            s = shape(fpos, h, width);       \s
                        }
                       \s
                        vec3 e = vec3(1);
                        vec3 col = s * pal(0.63 + 0.2 * uv.y, e, e, e, 0.15 * vec3(0,1,2)/3.);
                        col += (1.-s) * pal(0.57 + 0.23 * uv.y, e, e, e, 0.15 * vec3(0,1,2)/3.);
                       // col = vec3(s,0,0.22);
                       \s
                        fragColor = vec4(col,1.0);
                    }
                    // --------[ Original ShaderToy ends here ]---------- //

                    void main(void)
                    {
                        mainImage(gl_FragColor, gl_FragCoord.xy);
                    }""";
            String outlineShader = """
                    #version 120

                    uniform vec2 texelSize, direction;
                    uniform sampler2D textureIn;
                    uniform float radius;

                    #define offset direction * texelSize

                    void main() {
                        vec4 center = texture2D(textureIn, gl_TexCoord[0].st);
                        center.rgb *= center.a;
                        if (center.a > 0) discard;
                        for (float r = 1.0; r <= radius; r++) {
                            vec4 alphaCurrent1 = texture2D(textureIn, gl_TexCoord[0].st + offset * r);
                            vec4 alphaCurrent2 = texture2D(textureIn, gl_TexCoord[0].st - offset * r);
                            alphaCurrent1.rgb *= alphaCurrent1.a;
                            alphaCurrent2.rgb *= alphaCurrent2.a;
                            center += alphaCurrent1 + alphaCurrent2;
                        }

                        gl_FragColor = vec4(center.rgb / center.a, center.a);

                    }""";
            String gaussianBlur = """
                    #version 120

                    uniform sampler2D textureIn;
                    uniform vec2 texelSize, direction;
                    uniform float radius, weights[256];

                    #define offset texelSize * direction

                    void main() {
                        vec3 color = texture2D(textureIn, gl_TexCoord[0].st).rgb * weights[0];
                        float totalWeight = weights[0];

                        for (float f = 1.0; f <= radius; f++) {
                            color += texture2D(textureIn, gl_TexCoord[0].st + f * offset).rgb * (weights[int(abs(f))]);
                            color += texture2D(textureIn, gl_TexCoord[0].st - f * offset).rgb * (weights[int(abs(f))]);

                            totalWeight += (weights[int(abs(f))]) * 2.0;
                        }

                        gl_FragColor = vec4(color / totalWeight, 1.0);
                    }""";
            String newGlowShader = """
                    #version 120

                    uniform sampler2D u_diffuse_sampler;
                    uniform sampler2D u_other_sampler;
                    uniform vec2 u_texel_size;
                    uniform vec2 u_direction;
                    uniform float u_radius;
                    uniform float u_kernel[128];

                    void main(void)
                    {
                        vec2 uv = gl_TexCoord[0].st;

                        if (u_direction.x == 0.0) {
                            float alpha = texture2D(u_other_sampler, uv).a;
                            if (alpha > 0.0) discard;
                        }

                        float half_radius = u_radius / 2.0;
                        vec4 pixel_color = texture2D(u_diffuse_sampler, uv);
                        pixel_color.rgb *= pixel_color.a;
                        pixel_color *= u_kernel[0];

                        for (float f = 1; f <= u_radius; f++) {
                            vec2 offset = f * u_texel_size * u_direction;
                            vec4 left = texture2D(u_diffuse_sampler, uv - offset);
                            vec4 right = texture2D(u_diffuse_sampler, uv + offset);

                            left.rgb *= left.a;
                            right.rgb *= right.a;
                            pixel_color += (left + right) * u_kernel[int(f)];
                        }

                        gl_FragColor = vec4(pixel_color.rgb / pixel_color.a, pixel_color.a);
                    }""";
            String circleArcShader = """
                    #version 120
                                        
                    #define PI 3.14159265359
                                        
                    uniform float radialSmoothness, radius, borderThickness, progress;
                    uniform int change;
                    uniform vec4 color;
                    uniform vec2 pos;
                                        
                    void main() {
                        vec2 st = gl_FragCoord.xy - (pos + radius + borderThickness);
                      //  vec2 rp = st * 2. - 1.;
                                        
                        float circle = sqrt(dot(st,st));
                                        
                        //Radius minus circle to get just the outline
                        float smoothedAlpha = 1.0 - smoothstep(borderThickness, borderThickness + 3., abs(radius-circle));
                        vec4 circleColor = vec4(color.rgb, smoothedAlpha * color.a);
                                        
                        gl_FragColor = mix(vec4(circleColor.rgb, 0.0), circleColor, smoothstep(0., radialSmoothness, change * (atan(st.y,st.x) - (progress-.5) * PI * 2.5)));
                    }""";
            fragmentShaderID = switch (fragmentShaderLoc) {
                case "circleArcShader" ->
                        createShader(new ByteArrayInputStream(circleArcShader.getBytes()), GL_FRAGMENT_SHADER);
                case "newGlow" -> createShader(new ByteArrayInputStream(newGlowShader.getBytes()), GL_FRAGMENT_SHADER);
                case "kawaseUpGlow" ->
                        createShader(new ByteArrayInputStream(kawaseUpGlow.getBytes()), GL_FRAGMENT_SHADER);
                case "glow" -> createShader(new ByteArrayInputStream(glowShader.getBytes()), GL_FRAGMENT_SHADER);
                case "chams" -> createShader(new ByteArrayInputStream(chams.getBytes()), GL_FRAGMENT_SHADER);
                case "roundRectTexture" ->
                        createShader(new ByteArrayInputStream(roundRectTexture.getBytes()), GL_FRAGMENT_SHADER);
                case "roundRectOutline" ->
                        createShader(new ByteArrayInputStream(roundRectOutline.getBytes()), GL_FRAGMENT_SHADER);
                case "kawaseUpBloom" ->
                        createShader(new ByteArrayInputStream(kawaseUpBloom.getBytes()), GL_FRAGMENT_SHADER);
                case "kawaseDownBloom" ->
                        createShader(new ByteArrayInputStream(kawaseDownBloom.getBytes()), GL_FRAGMENT_SHADER);
                case "kawaseUp" -> createShader(new ByteArrayInputStream(kawaseUp.getBytes()), GL_FRAGMENT_SHADER);
                case "kawaseDown" -> createShader(new ByteArrayInputStream(kawaseDown.getBytes()), GL_FRAGMENT_SHADER);
                case "gradientMask" ->
                        createShader(new ByteArrayInputStream(gradientMask.getBytes()), GL_FRAGMENT_SHADER);
                case "mask" -> createShader(new ByteArrayInputStream(mask.getBytes()), GL_FRAGMENT_SHADER);
                case "gradient" -> createShader(new ByteArrayInputStream(gradient.getBytes()), GL_FRAGMENT_SHADER);
                case "roundedRect" ->
                        createShader(new ByteArrayInputStream(roundedRect.getBytes()), GL_FRAGMENT_SHADER);
                case "roundedRectGradient" ->
                        createShader(new ByteArrayInputStream(roundedRectGradient.getBytes()), GL_FRAGMENT_SHADER);
                case "backgroundShader" ->
                        createShader(new ByteArrayInputStream(backgroundShader.getBytes()), GL_FRAGMENT_SHADER);
                case "gaussian" -> createShader(new ByteArrayInputStream(gaussianBlur.getBytes()), GL_FRAGMENT_SHADER);
                case "outline" -> createShader(new ByteArrayInputStream(outlineShader.getBytes()), GL_FRAGMENT_SHADER);
                default ->
                        createShader(mc.getResourceManager().getResource(new ResourceLocation(fragmentShaderLoc)).getInputStream(), GL_FRAGMENT_SHADER);
            };
            glAttachShader(program, fragmentShaderID);

            int vertexShaderID = createShader(mc.getResourceManager().getResource(new ResourceLocation(vertexShaderLoc)).getInputStream(), GL_VERTEX_SHADER);
            glAttachShader(program, vertexShaderID);


        } catch (IOException e) {
            e.printStackTrace();
        }

        glLinkProgram(program);
        int status = glGetProgrami(program, GL_LINK_STATUS);

        if (status == 0) {
            throw new IllegalStateException("Shader failed to link!");
        }
        this.programID = program;
    }

    public ShaderUtil(String fragmentShadersrc, boolean notUsed) {
        int program = glCreateProgram();
        int fragmentShaderID = createShader(new ByteArrayInputStream(fragmentShadersrc.getBytes()), GL_FRAGMENT_SHADER);
        int vertexShaderID;
        try {
            vertexShaderID = createShader(mc.getResourceManager().getResource(new ResourceLocation("straight/shaders/vertex/vertex.vsh")).getInputStream(), GL_VERTEX_SHADER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        glAttachShader(program, fragmentShaderID);
        glAttachShader(program, vertexShaderID);


        glLinkProgram(program);
        int status = glGetProgrami(program, GL_LINK_STATUS);
        if (status == 0) {
            throw new IllegalStateException("Shader failed to link!");
        }
        this.programID = program;

    }

    public ShaderUtil(String fragmentShaderLoc) {
        this(fragmentShaderLoc, "straight/shaders/vertex/vertex.vsh");
    }


    public void init() {
        glUseProgram(programID);
    }

    public void unload() {
        glUseProgram(0);
    }

    public int getProgramID() {
        return programID;
    }

    public int getUniform(String name) {
        return glGetUniformLocation(programID, name);
    }


    public void setUniformf(String name, float... args) {
        int loc = glGetUniformLocation(programID, name);
        switch (args.length) {
            case 1 -> glUniform1f(loc, args[0]);
            case 2 -> glUniform2f(loc, args[0], args[1]);
            case 3 -> glUniform3f(loc, args[0], args[1], args[2]);
            case 4 -> glUniform4f(loc, args[0], args[1], args[2], args[3]);
        }
    }

    public void setUniformi(String name, int... args) {
        int loc = glGetUniformLocation(programID, name);
        if (args.length > 1) glUniform2i(loc, args[0], args[1]);
        else glUniform1i(loc, args[0]);
    }

    public static void drawQuads(float x, float y, float width, float height) {
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(x, y);
        glTexCoord2f(0, 1);
        glVertex2f(x, y + height);
        glTexCoord2f(1, 1);
        glVertex2f(x + width, y + height);
        glTexCoord2f(1, 0);
        glVertex2f(x + width, y);
        glEnd();
    }

    public static void drawQuads() {
        ScaledResolution sr = ScaledResolution.fetchResolution(mc);
        float width = (float) sr.getScaledWidth_double();
        float height = (float) sr.getScaledHeight_double();
        glBegin(GL_QUADS);
        glTexCoord2f(0, 1);
        glVertex2f(0, 0);
        glTexCoord2f(0, 0);
        glVertex2f(0, height);
        glTexCoord2f(1, 0);
        glVertex2f(width, height);
        glTexCoord2f(1, 1);
        glVertex2f(width, 0);
        glEnd();
    }

    public static void drawQuad(final double x, final double y, final double width, final double height) {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex2d(x, y + height);
        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex2d(x + width, y + height);
        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex2d(x + width, y);
        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex2d(x, y);
        GL11.glEnd();
    }

    public static void drawQuad() {
        final ScaledResolution scaledResolution = ScaledResolution.fetchResolution(mc);
        drawQuad(0.0, 0.0, scaledResolution.getScaledWidth_double(), scaledResolution.getScaledHeight_double());
    }

    public static void drawQuads(float width, float height) {
        glBegin(GL_QUADS);
        glTexCoord2f(0, 1);
        glVertex2f(0, 0);
        glTexCoord2f(0, 0);
        glVertex2f(0, height);
        glTexCoord2f(1, 0);
        glVertex2f(width, height);
        glTexCoord2f(1, 1);
        glVertex2f(width, 0);
        glEnd();
    }

    private int createShader(InputStream inputStream, int shaderType) {
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, FileUtils.readInputStream(inputStream));
        glCompileShader(shader);


        if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
            System.out.println(glGetShaderInfoLog(shader, 4096));
            throw new IllegalStateException(String.format("Shader (%s) failed to compile!", shaderType));
        }

        return shader;
    }


}