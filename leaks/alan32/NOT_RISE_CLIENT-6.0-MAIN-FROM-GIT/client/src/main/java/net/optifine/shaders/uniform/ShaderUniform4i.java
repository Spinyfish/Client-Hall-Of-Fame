package net.optifine.shaders.uniform;

import org.lwjgl.opengl.ARBShaderObjects;

public class ShaderUniform4i extends ShaderUniformBase {
    private int[][] programValues;
    private static final int VALUE_UNKNOWN = Integer.MIN_VALUE;

    public ShaderUniform4i(final String name) {
        super(name);
        this.resetValue();
    }

    public void setValue(final int v0, final int v1, final int v2, final int v3) {
        final int i = this.getProgram();
        final int[] aint = this.programValues[i];

        if (aint[0] != v0 || aint[1] != v1 || aint[2] != v2 || aint[3] != v3) {
            aint[0] = v0;
            aint[1] = v1;
            aint[2] = v2;
            aint[3] = v3;
            final int j = this.getLocation();

            if (j >= 0) {
                ARBShaderObjects.glUniform4iARB(j, v0, v1, v2, v3);
                this.checkGLError();
            }
        }
    }

    public int[] getValue() {
        final int i = this.getProgram();
        final int[] aint = this.programValues[i];
        return aint;
    }

    protected void onProgramSet(final int program) {
        if (program >= this.programValues.length) {
            final int[][] aint = this.programValues;
            final int[][] aint1 = new int[program + 10][];
            System.arraycopy(aint, 0, aint1, 0, aint.length);
            this.programValues = aint1;
        }

        if (this.programValues[program] == null) {
            this.programValues[program] = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
        }
    }

    protected void resetValue() {
        this.programValues = new int[][]{{Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE}};
    }
}
