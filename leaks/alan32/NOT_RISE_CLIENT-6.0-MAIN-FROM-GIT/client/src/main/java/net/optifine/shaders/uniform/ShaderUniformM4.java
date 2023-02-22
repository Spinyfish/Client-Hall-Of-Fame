package net.optifine.shaders.uniform;

import org.lwjgl.opengl.ARBShaderObjects;

import java.nio.FloatBuffer;

public class ShaderUniformM4 extends ShaderUniformBase {
    private boolean transpose;
    private FloatBuffer matrix;

    public ShaderUniformM4(final String name) {
        super(name);
    }

    public void setValue(final boolean transpose, final FloatBuffer matrix) {
        this.transpose = transpose;
        this.matrix = matrix;
        final int i = this.getLocation();

        if (i >= 0) {
            ARBShaderObjects.glUniformMatrix4ARB(i, transpose, matrix);
            this.checkGLError();
        }
    }

    public float getValue(final int row, final int col) {
        if (this.matrix == null) {
            return 0.0F;
        } else {
            final int i = this.transpose ? col * 4 + row : row * 4 + col;
            final float f = this.matrix.get(i);
            return f;
        }
    }

    protected void onProgramSet(final int program) {
    }

    protected void resetValue() {
        this.matrix = null;
    }
}
