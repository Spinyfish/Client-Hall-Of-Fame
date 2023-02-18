/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL20
 */
package vip.astroline.client.storage.utils.render;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class ShaderUtil {
    private final int programID;
    static Minecraft mc = Minecraft.getMinecraft();
    private final String roundedRectGradient = "#version 120\n\nuniform vec2 location, rectSize;\nuniform vec4 color1, color2, color3, color4;\nuniform float radius;\n\n#define NOISE .5/255.0\n\nfloat roundSDF(vec2 p, vec2 b, float r) {\n    return length(max(abs(p) - b , 0.0)) - r;\n}\n\nvec3 createGradient(vec2 coords, vec3 color1, vec3 color2, vec3 color3, vec3 color4){\n    vec3 color = mix(mix(color1.rgb, color2.rgb, coords.y), mix(color3.rgb, color4.rgb, coords.y), coords.x);\n    //Dithering the color\n    // from https://shader-tutorial.dev/advanced/color-banding-dithering/\n    color += mix(NOISE, -NOISE, fract(sin(dot(coords.xy, vec2(12.9898, 78.233))) * 43758.5453));\n    return color;\n}\n\nvoid main() {\n    vec2 st = gl_TexCoord[0].st;\n    vec2 halfSize = rectSize * .5;\n    \n    float smoothedAlpha =  (1.0-smoothstep(0.0, 2., roundSDF(halfSize - (gl_TexCoord[0].st * rectSize), halfSize - radius - 1., radius))) * color1.a;\n    gl_FragColor = vec4(createGradient(st, color1.rgb, color2.rgb, color3.rgb, color4.rgb), smoothedAlpha);\n}";
    private final String roundedRectOutline = "#version 120\n\nuniform vec2 location, rectSize;\nuniform vec4 color, outlineColor;\nuniform float radius, outlineThickness;\n\nfloat roundedSDF(vec2 centerPos, vec2 size, float radius) {\n    return length(max(abs(centerPos) - size + radius, 0.0)) - radius;\n}\n\nvoid main() {\n    float distance = roundedSDF(gl_FragCoord.xy - location - (rectSize * .5), (rectSize * .5) + (outlineThickness *.5) - 1.0, radius);\n\n    float blendAmount = smoothstep(0., 2., abs(distance) - (outlineThickness * .5));\n\n    vec4 insideColor = (distance < 0.) ? color : vec4(outlineColor.rgb,  0.0);\n    gl_FragColor = mix(outlineColor, insideColor, blendAmount);\n\n}\n";
    private String roundedRect = "#version 120\n\nuniform vec2 location, rectSize;\nuniform vec4 color;\nuniform float rounding;\nuniform bool blur;\n\nfloat roundSDF(vec2 p, vec2 b, float r) {\n    return length(max(abs(p) - b, 0.0)) - r;\n}\n\n\nvoid main() {\n    vec2 rectHalf = rectSize * .5;\n    // Smooth the result (free antialiasing).\n    float smoothedAlpha =  (1.0-smoothstep(0.0, 1.0, roundSDF(rectHalf - (gl_TexCoord[0].st * rectSize), rectHalf - rounding - 1., rounding))) * color.a;\n    gl_FragColor = vec4(color.rgb, smoothedAlpha);// mix(quadColor, shadowColor, 0.0);\n\n}";

    public ShaderUtil(String fragmentShaderLoc, String vertexShaderLoc) {
        int program = GL20.glCreateProgram();
        try {
            int fragmentShaderID = this.createShader(mc.getResourceManager().getResource(new ResourceLocation(fragmentShaderLoc)).getInputStream(), 35632);
            GL20.glAttachShader((int)program, (int)fragmentShaderID);
            int vertexShaderID = this.createShader(mc.getResourceManager().getResource(new ResourceLocation(vertexShaderLoc)).getInputStream(), 35633);
            GL20.glAttachShader((int)program, (int)vertexShaderID);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        GL20.glLinkProgram((int)program);
        int status = GL20.glGetProgrami((int)program, (int)35714);
        if (status == 0) {
            throw new IllegalStateException("Shader failed to link!");
        }
        this.programID = program;
    }

    public ShaderUtil(String fragmentShaderLoc) {
        this(fragmentShaderLoc, "astoline/Shaders/vertex.vsh");
    }

    public void init() {
        GL20.glUseProgram((int)this.programID);
    }

    public void unload() {
        GL20.glUseProgram((int)0);
    }

    public int getUniform(String name) {
        return GL20.glGetUniformLocation((int)this.programID, (CharSequence)name);
    }

    public void setUniformf(String name, float ... args) {
        int loc = GL20.glGetUniformLocation((int)this.programID, (CharSequence)name);
        switch (args.length) {
            case 1: {
                GL20.glUniform1f((int)loc, (float)args[0]);
                break;
            }
            case 2: {
                GL20.glUniform2f((int)loc, (float)args[0], (float)args[1]);
                break;
            }
            case 3: {
                GL20.glUniform3f((int)loc, (float)args[0], (float)args[1], (float)args[2]);
                break;
            }
            case 4: {
                GL20.glUniform4f((int)loc, (float)args[0], (float)args[1], (float)args[2], (float)args[3]);
                break;
            }
        }
    }

    public void setUniformi(String name, int ... args) {
        int loc = GL20.glGetUniformLocation((int)this.programID, (CharSequence)name);
        if (args.length > 1) {
            GL20.glUniform2i((int)loc, (int)args[0], (int)args[1]);
        } else {
            GL20.glUniform1i((int)loc, (int)args[0]);
        }
    }

    public static void drawQuads(float x, float y, float width, float height) {
        if (ShaderUtil.mc.gameSettings.ofFastRender) {
            return;
        }
        GL11.glBegin((int)7);
        GL11.glTexCoord2f((float)0.0f, (float)0.0f);
        GL11.glVertex2f((float)x, (float)y);
        GL11.glTexCoord2f((float)0.0f, (float)1.0f);
        GL11.glVertex2f((float)x, (float)(y + height));
        GL11.glTexCoord2f((float)1.0f, (float)1.0f);
        GL11.glVertex2f((float)(x + width), (float)(y + height));
        GL11.glTexCoord2f((float)1.0f, (float)0.0f);
        GL11.glVertex2f((float)(x + width), (float)y);
        GL11.glEnd();
    }

    public static void drawQuads() {
        if (ShaderUtil.mc.gameSettings.ofFastRender) {
            return;
        }
        ScaledResolution sr = new ScaledResolution(mc);
        float width = sr.getScaledWidth();
        float height = sr.getScaledHeight();
        GL11.glBegin((int)7);
        GL11.glTexCoord2f((float)0.0f, (float)1.0f);
        GL11.glVertex2f((float)0.0f, (float)0.0f);
        GL11.glTexCoord2f((float)0.0f, (float)0.0f);
        GL11.glVertex2f((float)0.0f, (float)height);
        GL11.glTexCoord2f((float)1.0f, (float)0.0f);
        GL11.glVertex2f((float)width, (float)height);
        GL11.glTexCoord2f((float)1.0f, (float)1.0f);
        GL11.glVertex2f((float)width, (float)0.0f);
        GL11.glEnd();
    }

    private int createShader(InputStream inputStream, int shaderType) {
        int shader = GL20.glCreateShader((int)shaderType);
        GL20.glShaderSource((int)shader, (CharSequence)this.readInputStream(inputStream));
        GL20.glCompileShader((int)shader);
        if (GL20.glGetShaderi((int)shader, (int)35713) != 0) return shader;
        System.out.println(GL20.glGetShaderInfoLog((int)shader, (int)4096));
        throw new IllegalStateException(String.format("Shader (%s) failed to compile!", shaderType));
    }

    public String readInputStream(InputStream inputStream) {
        StringBuffer buffer = new StringBuffer();
        try {
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append('\n');
            }
            return buffer.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
}
