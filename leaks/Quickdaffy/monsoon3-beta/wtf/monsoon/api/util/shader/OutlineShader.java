/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL20
 */
package wtf.monsoon.api.util.shader;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL20;

public class OutlineShader {
    private int program;
    public static final String VERTEX_SHADER = "#version 120 \n\nvoid main() {\n    gl_TexCoord[0] = gl_MultiTexCoord0;\n    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n}";
    private Color colour = new Color(0, 0, 0);
    private float width = 1.0f;
    private int fill = 0;
    private int outline = 1;
    private final Map<String, Integer> uniformLocationMap = new HashMap<String, Integer>();
    private static final String OUTLINE_FRAG_SHADER = "#version 120\n\n/*\n * centerCol is the pixel colour, we use rhis to determine whether we are drawing over an entity\n * Uniforms are values we can set externally\n * gl_FragColor is the pixels final colour\n * gl_TexCoord is the texture coordinate\n*/\n\nuniform sampler2D texture;\nuniform vec2 resolution;\n\nuniform vec4 colour;\nuniform float width;\n\nuniform int fill;\nuniform int outline;\n\nvoid main() {\n    vec4 centerCol = texture2D(texture, gl_TexCoord[0].xy);\n\n    if (centerCol.a > 0) {\n        if (fill == 1) {\n            gl_FragColor = vec4(colour.x, colour.y, colour.z, 0.5F);\n        } else {\n            gl_FragColor = vec4(0, 0, 0, 0);\n        }\n    } else if (outline == 1) {\n        float alpha = 0.0F;\n\n        for (float x = -width; x <= width; x++) {\n            for (float y = -width; y <= width; y++) {\n                vec4 pointColour = texture2D(texture, gl_TexCoord[0].xy + vec2(resolution.x * x, resolution.y * y));\n\n                if (pointColour.a > 0) {\n                    alpha = 1.0F;\n                }\n            }\n        }\n\n        gl_FragColor = vec4(colour.x, colour.y, colour.z, alpha);\n    }\n}";

    public OutlineShader() {
        this.program = GL20.glCreateProgram();
        GL20.glAttachShader((int)this.program, (int)OutlineShader.createShader(VERTEX_SHADER, 35633));
        GL20.glAttachShader((int)this.program, (int)OutlineShader.createShader(OUTLINE_FRAG_SHADER, 35632));
        GL20.glLinkProgram((int)this.program);
        int status = GL20.glGetProgrami((int)this.program, (int)35714);
        if (status == 0) {
            this.program = -1;
            return;
        }
        this.setupUniforms();
    }

    public void setColour(Color colour) {
        this.colour = colour;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setFill(int fill) {
        this.fill = fill;
    }

    public void setOutline(int outline) {
        this.outline = outline;
    }

    public void setupUniforms() {
        this.setupUniform("texture");
        this.setupUniform("resolution");
        this.setupUniform("colour");
        this.setupUniform("width");
        this.setupUniform("fill");
        this.setupUniform("outline");
    }

    public void updateUniforms() {
        GL20.glUniform1i((int)this.getUniform("texture"), (int)0);
        GL20.glUniform2f((int)this.getUniform("resolution"), (float)(1.0f / (float)Minecraft.getMinecraft().displayWidth), (float)(1.0f / (float)Minecraft.getMinecraft().displayHeight));
        GL20.glUniform4f((int)this.getUniform("colour"), (float)((float)this.colour.getRed() / 255.0f), (float)((float)this.colour.getGreen() / 255.0f), (float)((float)this.colour.getBlue() / 255.0f), (float)((float)this.colour.getAlpha() / 255.0f));
        GL20.glUniform1f((int)this.getUniform("width"), (float)this.width);
        GL20.glUniform1i((int)this.getUniform("fill"), (int)this.fill);
        GL20.glUniform1i((int)this.getUniform("outline"), (int)this.outline);
    }

    private static int createShader(String source, int type) {
        int shader = GL20.glCreateShader((int)type);
        GL20.glShaderSource((int)shader, (CharSequence)source);
        GL20.glCompileShader((int)shader);
        int status = GL20.glGetShaderi((int)shader, (int)35713);
        if (status == 0) {
            return -1;
        }
        return shader;
    }

    public void startShader() {
        GL20.glUseProgram((int)this.program);
        this.updateUniforms();
    }

    public int getProgram() {
        return this.program;
    }

    public void setupUniform(String uniform) {
        this.uniformLocationMap.put(uniform, GL20.glGetUniformLocation((int)this.program, (CharSequence)uniform));
    }

    public int getUniform(String uniform) {
        return this.uniformLocationMap.get(uniform);
    }
}

