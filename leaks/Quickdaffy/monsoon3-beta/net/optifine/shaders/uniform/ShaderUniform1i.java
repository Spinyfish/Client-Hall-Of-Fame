/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.ARBShaderObjects
 */
package net.optifine.shaders.uniform;

import net.optifine.shaders.uniform.ShaderUniformBase;
import org.lwjgl.opengl.ARBShaderObjects;

public class ShaderUniform1i
extends ShaderUniformBase {
    private int[] programValues;
    private static final int VALUE_UNKNOWN = Integer.MIN_VALUE;

    public ShaderUniform1i(String name) {
        super(name);
        this.resetValue();
    }

    public void setValue(int valueNew) {
        int i = this.getProgram();
        int j = this.programValues[i];
        if (valueNew != j) {
            this.programValues[i] = valueNew;
            int k = this.getLocation();
            if (k >= 0) {
                ARBShaderObjects.glUniform1iARB((int)k, (int)valueNew);
                this.checkGLError();
            }
        }
    }

    public int getValue() {
        int i = this.getProgram();
        int j = this.programValues[i];
        return j;
    }

    @Override
    protected void onProgramSet(int program) {
        if (program >= this.programValues.length) {
            int[] aint = this.programValues;
            int[] aint1 = new int[program + 10];
            System.arraycopy(aint, 0, aint1, 0, aint.length);
            for (int i = aint.length; i < aint1.length; ++i) {
                aint1[i] = Integer.MIN_VALUE;
            }
            this.programValues = aint1;
        }
    }

    @Override
    protected void resetValue() {
        this.programValues = new int[]{Integer.MIN_VALUE};
    }
}

