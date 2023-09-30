package com.alan.clients.util.shader.kernel;

public class GaussianKernel {

    private final int size;
    private final float[] kernel;

    public GaussianKernel(final int size) {
        this.size = size;
        this.kernel = new float[size];
    }

    public void compute() {
        final float sigma = this.size / 2.0F;
        float kernelSum = 0.0F;
        for (int i = 0; i < this.size; ++i) {
            final float multiplier = i / sigma;
            this.kernel[i] = 1.0F / (Math.abs(sigma) * 2.50662827463F) * (float) Math.exp(-0.5 * multiplier * multiplier);
            kernelSum += i > 0 ? this.kernel[i] * 2 : this.kernel[0];
        }

        for (int i = 0; i < size; ++i) {
            this.kernel[i] /= kernelSum;
        }
    }

    public int getSize() {
        return size;
    }

    public float[] getKernel() {
        return kernel;
    }
}
