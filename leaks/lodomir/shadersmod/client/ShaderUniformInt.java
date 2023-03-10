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

public class ShaderUniformInt
extends ShaderUniformBase {
    private int value = -1;

    public ShaderUniformInt(String name) {
        super(name);
    }

    @Override
    protected void onProgramChanged() {
        this.value = -1;
    }

    public void setValue(int value) {
        if (this.getLocation() >= 0 && this.value != value) {
            ARBShaderObjects.glUniform1iARB((int)this.getLocation(), (int)value);
            Shaders.checkGLError(this.getName());
            this.value = value;
        }
    }

    public int getValue() {
        return this.value;
    }
}

