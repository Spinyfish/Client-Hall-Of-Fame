/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.ARBShaderObjects
 */
package shadersmod.client;

import org.lwjgl.opengl.ARBShaderObjects;
import shadersmod.client.ShaderUniformBase;
import shadersmod.client.Shaders;

public class ShaderUniformFloat4
extends ShaderUniformBase {
    private float[] values = new float[4];

    public ShaderUniformFloat4(String name) {
        super(name);
    }

    @Override
    protected void onProgramChanged() {
        this.values[0] = 0.0f;
        this.values[1] = 0.0f;
        this.values[2] = 0.0f;
        this.values[3] = 0.0f;
    }

    public void setValue(float f0, float f1, float f2, float f3) {
        if (this.getLocation() >= 0 && (this.values[0] != f0 || this.values[1] != f1 || this.values[2] != f2 || this.values[3] != f3)) {
            ARBShaderObjects.glUniform4fARB((int)this.getLocation(), (float)f0, (float)f1, (float)f2, (float)f3);
            Shaders.checkGLError(this.getName());
            this.values[0] = f0;
            this.values[1] = f1;
            this.values[2] = f2;
            this.values[3] = f3;
        }
    }

    public float[] getValues() {
        return this.values;
    }
}

