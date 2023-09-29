package net.minecraft.client.renderer.culling;

public class ClippingHelper {
    public float[][] frustum = new float[6][4];
    public float[] projectionMatrix = new float[16];
    public float[] modelviewMatrix = new float[16];
    public float[] clippingMatrix = new float[16];
    public boolean disabled = false;

    private float dot(final float[] p_dot_1_, final float p_dot_2_, final float p_dot_3_, final float p_dot_4_) {
        return p_dot_1_[0] * p_dot_2_ + p_dot_1_[1] * p_dot_3_ + p_dot_1_[2] * p_dot_4_ + p_dot_1_[3];
    }

    /**
     * Returns true if the box is inside all 6 clipping planes, otherwise returns false.
     */
    public boolean isBoxInFrustum(final double p_78553_1_, final double p_78553_3_, final double p_78553_5_, final double p_78553_7_, final double p_78553_9_, final double p_78553_11_) {
        if (this.disabled) {
            return true;
        } else {
            final float f = (float) p_78553_1_;
            final float f1 = (float) p_78553_3_;
            final float f2 = (float) p_78553_5_;
            final float f3 = (float) p_78553_7_;
            final float f4 = (float) p_78553_9_;
            final float f5 = (float) p_78553_11_;

            for (int i = 0; i < 6; ++i) {
                final float[] afloat = this.frustum[i];
                final float f6 = afloat[0];
                final float f7 = afloat[1];
                final float f8 = afloat[2];
                final float f9 = afloat[3];

                if (f6 * f + f7 * f1 + f8 * f2 + f9 <= 0.0F && f6 * f3 + f7 * f1 + f8 * f2 + f9 <= 0.0F && f6 * f + f7 * f4 + f8 * f2 + f9 <= 0.0F && f6 * f3 + f7 * f4 + f8 * f2 + f9 <= 0.0F && f6 * f + f7 * f1 + f8 * f5 + f9 <= 0.0F && f6 * f3 + f7 * f1 + f8 * f5 + f9 <= 0.0F && f6 * f + f7 * f4 + f8 * f5 + f9 <= 0.0F && f6 * f3 + f7 * f4 + f8 * f5 + f9 <= 0.0F) {
                    return false;
                }
            }

            return true;
        }
    }

    public boolean isBoxInFrustumFully(final double p_isBoxInFrustumFully_1_, final double p_isBoxInFrustumFully_3_, final double p_isBoxInFrustumFully_5_, final double p_isBoxInFrustumFully_7_, final double p_isBoxInFrustumFully_9_, final double p_isBoxInFrustumFully_11_) {
        if (this.disabled) {
            return true;
        } else {
            final float f = (float) p_isBoxInFrustumFully_1_;
            final float f1 = (float) p_isBoxInFrustumFully_3_;
            final float f2 = (float) p_isBoxInFrustumFully_5_;
            final float f3 = (float) p_isBoxInFrustumFully_7_;
            final float f4 = (float) p_isBoxInFrustumFully_9_;
            final float f5 = (float) p_isBoxInFrustumFully_11_;

            for (int i = 0; i < 6; ++i) {
                final float[] afloat = this.frustum[i];
                final float f6 = afloat[0];
                final float f7 = afloat[1];
                final float f8 = afloat[2];
                final float f9 = afloat[3];

                if (i < 4) {
                    if (f6 * f + f7 * f1 + f8 * f2 + f9 <= 0.0F || f6 * f3 + f7 * f1 + f8 * f2 + f9 <= 0.0F || f6 * f + f7 * f4 + f8 * f2 + f9 <= 0.0F || f6 * f3 + f7 * f4 + f8 * f2 + f9 <= 0.0F || f6 * f + f7 * f1 + f8 * f5 + f9 <= 0.0F || f6 * f3 + f7 * f1 + f8 * f5 + f9 <= 0.0F || f6 * f + f7 * f4 + f8 * f5 + f9 <= 0.0F || f6 * f3 + f7 * f4 + f8 * f5 + f9 <= 0.0F) {
                        return false;
                    }
                } else if (f6 * f + f7 * f1 + f8 * f2 + f9 <= 0.0F && f6 * f3 + f7 * f1 + f8 * f2 + f9 <= 0.0F && f6 * f + f7 * f4 + f8 * f2 + f9 <= 0.0F && f6 * f3 + f7 * f4 + f8 * f2 + f9 <= 0.0F && f6 * f + f7 * f1 + f8 * f5 + f9 <= 0.0F && f6 * f3 + f7 * f1 + f8 * f5 + f9 <= 0.0F && f6 * f + f7 * f4 + f8 * f5 + f9 <= 0.0F && f6 * f3 + f7 * f4 + f8 * f5 + f9 <= 0.0F) {
                    return false;
                }
            }

            return true;
        }
    }
}
