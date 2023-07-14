/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.ARBShaderObjects
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL20
 */
package wtf.monsoon.api.util.shader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class Shader {
    private final int fragID;
    private final int vertID;
    private final int program;
    private final Map<String, Integer> uniformLocationMap = new HashMap<String, Integer>();

    public Shader(ResourceLocation fragment) {
        this.vertID = this.createShader(this.readResourceLocation(new ResourceLocation("monsoon/shader/vertex.vert")), 35633);
        this.fragID = this.createShader(this.readResourceLocation(fragment), 35632);
        if (this.vertID != 0 && this.fragID != 0) {
            this.program = ARBShaderObjects.glCreateProgramObjectARB();
            if (this.program != 0) {
                ARBShaderObjects.glAttachObjectARB((int)this.program, (int)this.vertID);
                ARBShaderObjects.glAttachObjectARB((int)this.program, (int)this.fragID);
                ARBShaderObjects.glLinkProgramARB((int)this.program);
                ARBShaderObjects.glValidateProgramARB((int)this.program);
            }
        } else {
            this.program = -1;
        }
    }

    public void init() {
        GL20.glUseProgram((int)this.getProgram());
    }

    public void bind(float x, float y, float h, float w) {
        GL11.glBegin((int)7);
        GL11.glTexCoord2f((float)0.0f, (float)0.0f);
        GL11.glVertex2f((float)x, (float)y);
        GL11.glTexCoord2f((float)0.0f, (float)1.0f);
        GL11.glVertex2f((float)x, (float)(y + h));
        GL11.glTexCoord2f((float)1.0f, (float)1.0f);
        GL11.glVertex2f((float)(x + w), (float)(y + h));
        GL11.glTexCoord2f((float)1.0f, (float)0.0f);
        GL11.glVertex2f((float)(x + w), (float)y);
        GL11.glEnd();
    }

    public void finish() {
        GL20.glUseProgram((int)0);
    }

    public void setupUniform(String uniform) {
        this.uniformLocationMap.put(uniform, GL20.glGetUniformLocation((int)this.program, (CharSequence)uniform));
    }

    public int getUniform(String uniform) {
        return this.uniformLocationMap.get(uniform);
    }

    String readResourceLocation(ResourceLocation loc) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String line;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(loc).getInputStream()));
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private int createShader(String source, int type) {
        int shader = 0;
        try {
            shader = ARBShaderObjects.glCreateShaderObjectARB((int)type);
            if (shader != 0) {
                ARBShaderObjects.glShaderSourceARB((int)shader, (CharSequence)source);
                ARBShaderObjects.glCompileShaderARB((int)shader);
                if (ARBShaderObjects.glGetObjectParameteriARB((int)shader, (int)35713) == 0) {
                    throw new RuntimeException("Error creating shader: " + ARBShaderObjects.glGetInfoLogARB((int)shader, (int)ARBShaderObjects.glGetObjectParameteriARB((int)shader, (int)35716)));
                }
                return shader;
            }
            return 0;
        }
        catch (Exception e) {
            ARBShaderObjects.glDeleteObjectARB((int)shader);
            e.printStackTrace();
            throw e;
        }
    }

    public int getFragID() {
        return this.fragID;
    }

    public int getVertID() {
        return this.vertID;
    }

    public int getProgram() {
        return this.program;
    }

    public Map<String, Integer> getUniformLocationMap() {
        return this.uniformLocationMap;
    }
}

