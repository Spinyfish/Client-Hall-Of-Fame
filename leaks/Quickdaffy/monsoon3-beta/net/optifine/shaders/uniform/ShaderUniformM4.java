/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.ARBShaderObjects
 */
package net.optifine.shaders.uniform;

import java.nio.FloatBuffer;
import net.optifine.shaders.uniform.ShaderUniformBase;
import org.lwjgl.opengl.ARBShaderObjects;

public class ShaderUniformM4
extends ShaderUniformBase {
    private boolean transpose;
    private FloatBuffer matrix;

    public ShaderUniformM4(String name) {
        super(name);
    }

    public void setValue(boolean transpose, FloatBuffer matrix) {
        this.transpose = transpose;
        this.matrix = matrix;
        int i = this.getLocation();
        if (i >= 0) {
            ARBShaderObjects.glUniformMatrix4ARB((int)i, (boolean)transpose, (FloatBuffer)matrix);
            this.checkGLError();
        }
    }

    public float getValue(int row, int col) {
        if (this.matrix == null) {
            return 0.0f;
        }
        int i = this.transpose ? col * 4 + row : row * 4 + col;
        float f = this.matrix.get(i);
        return f;
    }

    @Override
    protected void onProgramSet(int program) {
    }

    @Override
    protected void resetValue() {
        this.matrix = null;
    }
}

