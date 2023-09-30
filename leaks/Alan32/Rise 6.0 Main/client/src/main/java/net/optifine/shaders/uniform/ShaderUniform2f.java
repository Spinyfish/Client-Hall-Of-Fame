package net.optifine.shaders.uniform;

import org.lwjgl.opengl.ARBShaderObjects;

public class ShaderUniform2f extends ShaderUniformBase {
    private float[][] programValues;
    private static final float VALUE_UNKNOWN = -3.4028235E38F;

    public ShaderUniform2f(final String name) {
        super(name);
        this.resetValue();
    }

    public void setValue(final float v0, final float v1) {
        final int i = this.getProgram();
        final float[] afloat = this.programValues[i];

        if (afloat[0] != v0 || afloat[1] != v1) {
            afloat[0] = v0;
            afloat[1] = v1;
            final int j = this.getLocation();

            if (j >= 0) {
                ARBShaderObjects.glUniform2fARB(j, v0, v1);
                this.checkGLError();
            }
        }
    }

    public float[] getValue() {
        final int i = this.getProgram();
        final float[] afloat = this.programValues[i];
        return afloat;
    }

    protected void onProgramSet(final int program) {
        if (program >= this.programValues.length) {
            final float[][] afloat = this.programValues;
            final float[][] afloat1 = new float[program + 10][];
            System.arraycopy(afloat, 0, afloat1, 0, afloat.length);
            this.programValues = afloat1;
        }

        if (this.programValues[program] == null) {
            this.programValues[program] = new float[]{-3.4028235E38F, -3.4028235E38F};
        }
    }

    protected void resetValue() {
        this.programValues = new float[][]{{-3.4028235E38F, -3.4028235E38F}};
    }
}
