package net.optifine.shaders;

import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.util.MathHelper;

public class ClippingHelperShadow extends ClippingHelper {
    private static final ClippingHelperShadow instance = new ClippingHelperShadow();
    float[] frustumTest = new float[6];
    float[][] shadowClipPlanes = new float[10][4];
    int shadowClipPlaneCount;
    float[] matInvMP = new float[16];
    float[] vecIntersection = new float[4];

    /**
     * Returns true if the box is inside all 6 clipping planes, otherwise returns false.
     */
    public boolean isBoxInFrustum(final double x1, final double y1, final double z1, final double x2, final double y2, final double z2) {
        for (int i = 0; i < this.shadowClipPlaneCount; ++i) {
            final float[] afloat = this.shadowClipPlanes[i];

            if (this.dot4(afloat, x1, y1, z1) <= 0.0D && this.dot4(afloat, x2, y1, z1) <= 0.0D && this.dot4(afloat, x1, y2, z1) <= 0.0D && this.dot4(afloat, x2, y2, z1) <= 0.0D && this.dot4(afloat, x1, y1, z2) <= 0.0D && this.dot4(afloat, x2, y1, z2) <= 0.0D && this.dot4(afloat, x1, y2, z2) <= 0.0D && this.dot4(afloat, x2, y2, z2) <= 0.0D) {
                return false;
            }
        }

        return true;
    }

    private double dot4(final float[] plane, final double x, final double y, final double z) {
        return (double) plane[0] * x + (double) plane[1] * y + (double) plane[2] * z + (double) plane[3];
    }

    private double dot3(final float[] vecA, final float[] vecB) {
        return (double) vecA[0] * (double) vecB[0] + (double) vecA[1] * (double) vecB[1] + (double) vecA[2] * (double) vecB[2];
    }

    public static ClippingHelper getInstance() {
        instance.init();
        return instance;
    }

    private void normalizePlane(final float[] plane) {
        final float f = MathHelper.sqrt_float(plane[0] * plane[0] + plane[1] * plane[1] + plane[2] * plane[2]);
        plane[0] /= f;
        plane[1] /= f;
        plane[2] /= f;
        plane[3] /= f;
    }

    private void normalize3(final float[] plane) {
        float f = MathHelper.sqrt_float(plane[0] * plane[0] + plane[1] * plane[1] + plane[2] * plane[2]);

        if (f == 0.0F) {
            f = 1.0F;
        }

        plane[0] /= f;
        plane[1] /= f;
        plane[2] /= f;
    }

    private void assignPlane(final float[] plane, final float a, final float b, final float c, final float d) {
        final float f = (float) Math.sqrt(a * a + b * b + c * c);
        plane[0] = a / f;
        plane[1] = b / f;
        plane[2] = c / f;
        plane[3] = d / f;
    }

    private void copyPlane(final float[] dst, final float[] src) {
        dst[0] = src[0];
        dst[1] = src[1];
        dst[2] = src[2];
        dst[3] = src[3];
    }

    private void cross3(final float[] out, final float[] a, final float[] b) {
        out[0] = a[1] * b[2] - a[2] * b[1];
        out[1] = a[2] * b[0] - a[0] * b[2];
        out[2] = a[0] * b[1] - a[1] * b[0];
    }

    private void addShadowClipPlane(final float[] plane) {
        this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], plane);
    }

    private float length(final float x, final float y, final float z) {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    private float distance(final float x1, final float y1, final float z1, final float x2, final float y2, final float z2) {
        return this.length(x1 - x2, y1 - y2, z1 - z2);
    }

    private void makeShadowPlane(final float[] shadowPlane, final float[] positivePlane, final float[] negativePlane, final float[] vecSun) {
        this.cross3(this.vecIntersection, positivePlane, negativePlane);
        this.cross3(shadowPlane, this.vecIntersection, vecSun);
        this.normalize3(shadowPlane);
        final float f = (float) this.dot3(positivePlane, negativePlane);
        final float f1 = (float) this.dot3(shadowPlane, negativePlane);
        final float f2 = this.distance(shadowPlane[0], shadowPlane[1], shadowPlane[2], negativePlane[0] * f1, negativePlane[1] * f1, negativePlane[2] * f1);
        final float f3 = this.distance(positivePlane[0], positivePlane[1], positivePlane[2], negativePlane[0] * f, negativePlane[1] * f, negativePlane[2] * f);
        final float f4 = f2 / f3;
        final float f5 = (float) this.dot3(shadowPlane, positivePlane);
        final float f6 = this.distance(shadowPlane[0], shadowPlane[1], shadowPlane[2], positivePlane[0] * f5, positivePlane[1] * f5, positivePlane[2] * f5);
        final float f7 = this.distance(negativePlane[0], negativePlane[1], negativePlane[2], positivePlane[0] * f, positivePlane[1] * f, positivePlane[2] * f);
        final float f8 = f6 / f7;
        shadowPlane[3] = positivePlane[3] * f4 + negativePlane[3] * f8;
    }

    public void init() {
        final float[] afloat = this.projectionMatrix;
        final float[] afloat1 = this.modelviewMatrix;
        final float[] afloat2 = this.clippingMatrix;
        System.arraycopy(Shaders.faProjection, 0, afloat, 0, 16);
        System.arraycopy(Shaders.faModelView, 0, afloat1, 0, 16);
        SMath.multiplyMat4xMat4(afloat2, afloat1, afloat);
        this.assignPlane(this.frustum[0], afloat2[3] - afloat2[0], afloat2[7] - afloat2[4], afloat2[11] - afloat2[8], afloat2[15] - afloat2[12]);
        this.assignPlane(this.frustum[1], afloat2[3] + afloat2[0], afloat2[7] + afloat2[4], afloat2[11] + afloat2[8], afloat2[15] + afloat2[12]);
        this.assignPlane(this.frustum[2], afloat2[3] + afloat2[1], afloat2[7] + afloat2[5], afloat2[11] + afloat2[9], afloat2[15] + afloat2[13]);
        this.assignPlane(this.frustum[3], afloat2[3] - afloat2[1], afloat2[7] - afloat2[5], afloat2[11] - afloat2[9], afloat2[15] - afloat2[13]);
        this.assignPlane(this.frustum[4], afloat2[3] - afloat2[2], afloat2[7] - afloat2[6], afloat2[11] - afloat2[10], afloat2[15] - afloat2[14]);
        this.assignPlane(this.frustum[5], afloat2[3] + afloat2[2], afloat2[7] + afloat2[6], afloat2[11] + afloat2[10], afloat2[15] + afloat2[14]);
        final float[] afloat3 = Shaders.shadowLightPositionVector;
        final float f = (float) this.dot3(this.frustum[0], afloat3);
        final float f1 = (float) this.dot3(this.frustum[1], afloat3);
        final float f2 = (float) this.dot3(this.frustum[2], afloat3);
        final float f3 = (float) this.dot3(this.frustum[3], afloat3);
        final float f4 = (float) this.dot3(this.frustum[4], afloat3);
        final float f5 = (float) this.dot3(this.frustum[5], afloat3);
        this.shadowClipPlaneCount = 0;

        if (f >= 0.0F) {
            this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[0]);

            if (f > 0.0F) {
                if (f2 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[0], this.frustum[2], afloat3);
                }

                if (f3 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[0], this.frustum[3], afloat3);
                }

                if (f4 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[0], this.frustum[4], afloat3);
                }

                if (f5 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[0], this.frustum[5], afloat3);
                }
            }
        }

        if (f1 >= 0.0F) {
            this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[1]);

            if (f1 > 0.0F) {
                if (f2 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[1], this.frustum[2], afloat3);
                }

                if (f3 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[1], this.frustum[3], afloat3);
                }

                if (f4 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[1], this.frustum[4], afloat3);
                }

                if (f5 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[1], this.frustum[5], afloat3);
                }
            }
        }

        if (f2 >= 0.0F) {
            this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[2]);

            if (f2 > 0.0F) {
                if (f < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[2], this.frustum[0], afloat3);
                }

                if (f1 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[2], this.frustum[1], afloat3);
                }

                if (f4 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[2], this.frustum[4], afloat3);
                }

                if (f5 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[2], this.frustum[5], afloat3);
                }
            }
        }

        if (f3 >= 0.0F) {
            this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[3]);

            if (f3 > 0.0F) {
                if (f < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[3], this.frustum[0], afloat3);
                }

                if (f1 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[3], this.frustum[1], afloat3);
                }

                if (f4 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[3], this.frustum[4], afloat3);
                }

                if (f5 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[3], this.frustum[5], afloat3);
                }
            }
        }

        if (f4 >= 0.0F) {
            this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[4]);

            if (f4 > 0.0F) {
                if (f < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[4], this.frustum[0], afloat3);
                }

                if (f1 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[4], this.frustum[1], afloat3);
                }

                if (f2 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[4], this.frustum[2], afloat3);
                }

                if (f3 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[4], this.frustum[3], afloat3);
                }
            }
        }

        if (f5 >= 0.0F) {
            this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[5]);

            if (f5 > 0.0F) {
                if (f < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[5], this.frustum[0], afloat3);
                }

                if (f1 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[5], this.frustum[1], afloat3);
                }

                if (f2 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[5], this.frustum[2], afloat3);
                }

                if (f3 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[5], this.frustum[3], afloat3);
                }
            }
        }
    }
}
