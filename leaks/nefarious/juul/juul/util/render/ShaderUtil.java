package juul.util.render;


import juul.util.Utils;
import juul.util.objects.ScaledRes;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class ShaderUtil implements Utils {
    private final int programID;

    public ShaderUtil(String fragmentShaderLoc) {
        int program = glCreateProgram();

        int fragmentShaderID = switch (fragmentShaderLoc) {
            case "roundRectTexture" -> createShader(roundRectTexture, GL_FRAGMENT_SHADER);
            case "roundRectOutline" -> createShader(roundRectOutline, GL_FRAGMENT_SHADER);
            case "roundedRect" -> createShader(roundedRect, GL_FRAGMENT_SHADER);
            case "roundedRectGradient" -> createShader(roundedRectGradient, GL_FRAGMENT_SHADER);
            case "gradientMask" -> createShader(gradientMask, GL_FRAGMENT_SHADER);
            case "gradient" -> createShader(gradient, GL_FRAGMENT_SHADER);
            default -> 0;
        };

        glAttachShader(program, fragmentShaderID);

        int vertexShaderID = createShader(vertex, GL_VERTEX_SHADER);
        glAttachShader(program, vertexShaderID);


        glLinkProgram(program);
        int status = glGetProgrami(program, GL_LINK_STATUS);

        if (status == 0) {
            throw new IllegalStateException("Shader failed to link!");
        }
        this.programID = program;
    }

    public void init() {
        glUseProgram(programID);
    }

    public void unload() {
        glUseProgram(0);
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
        if (mc.getGameSettings().fastRender()) return;
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
        if (mc.getGameSettings().fastRender()) return;
        ScaledRes sr = new ScaledRes(mc);
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

    public static void drawQuads(float width, float height) {
        if (mc.getGameSettings().fastRender()) return;
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

    private int createShader(String shaderString, int shaderType) {
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, shaderString);
        glCompileShader(shader);


        if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
            System.out.println(glGetShaderInfoLog(shader, 4096));
            throw new IllegalStateException(String.format("Shader (%s) failed to compile!", shaderType));
        }

        return shader;
    }

    private String vertex = """
            #version 120

            void main() {
                gl_TexCoord[0] = gl_MultiTexCoord0;
                gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
            }""";

    private String roundRectTexture = """
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

    private String roundRectOutline = """
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

    private String gradientMask = """
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


    private String gradient = """
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

    private String roundedRectGradient = """
            #version 120

            uniform vec2 location, rectSize;
            uniform vec4 color1, color2, color3, color4;
            uniform float radius;

            #define NOISE .5/255.0

            float roundSDF(vec2 p, vec2 b, float r) {
                return length(max(abs(p) - b , 0.0)) - r;
            }

            vec3 createGradient(vec2 coords, vec3 color1, vec3 color2, vec3 color3, vec3 color4){
                vec3 color = mix(mix(color1.rgb, color2.rgb, coords.y), mix(color3.rgb, color4.rgb, coords.y), coords.x);
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
                float smoothedAlpha =  (1.0-smoothstep(0.0, 2., roundSDF(halfSize - (gl_TexCoord[0].st * rectSize), halfSize - radius - 1., radius))) * color2.a;
                gl_FragColor = vec4(createGradient(st, color1.rgb, color2.rgb, color3.rgb, color4.rgb), smoothedAlpha);
            }""";


    private String roundedRect = """
            #version 120

            uniform vec2 location, rectSize;
            uniform vec4 color;
            uniform float radius;
            uniform bool blur;

            float roundSDF(vec2 p, vec2 b, float r) {
                return length(max(abs(p) - b, 0.0)) - r;
            }


            void main() {
                vec2 rectHalf = rectSize * .5;
                // Smooth the result (free antialiasing).
                float smoothedAlpha =  (1.0-smoothstep(0.0, 1.0, roundSDF(rectHalf - (gl_TexCoord[0].st * rectSize), rectHalf - radius - 1., radius))) * color.a;
                gl_FragColor = vec4(color.rgb, smoothedAlpha);// mix(quadColor, shadowColor, 0.0);

            }""";

}
