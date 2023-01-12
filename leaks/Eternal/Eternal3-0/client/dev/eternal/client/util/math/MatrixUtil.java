package dev.eternal.client.util.math;

import org.lwjgl.util.vector.Matrix4f;

import java.nio.FloatBuffer;

public class MatrixUtil {
  public static Matrix4f orthographic(
      float left, float right, float bottom, float top, float near, float far) {
    Matrix4f ortho = new Matrix4f();
    float tx = -(right + left) / (right - left);
    float ty = -(top + bottom) / (top - bottom);
    float tz = -(far + near) / (far - near);

    ortho.m00 = 2f / (right - left);
    ortho.m11 = 2f / (top - bottom);
    ortho.m22 = -2f / (far - near);
    ortho.m03 = tx;
    ortho.m13 = ty;
    ortho.m23 = tz;
    return ortho;
  }

  public static Matrix4f scale(float x, float y, float z) {
    Matrix4f scaling = new Matrix4f();

    scaling.m00 = x;
    scaling.m11 = y;
    scaling.m22 = z;

    return scaling;
  }

  public static FloatBuffer toBuffer(Matrix4f matrix4f, FloatBuffer buffer) {
    buffer.put(matrix4f.m00).put(matrix4f.m10).put(matrix4f.m20).put(matrix4f.m30);
    buffer.put(matrix4f.m01).put(matrix4f.m11).put(matrix4f.m21).put(matrix4f.m31);
    buffer.put(matrix4f.m02).put(matrix4f.m12).put(matrix4f.m22).put(matrix4f.m32);
    buffer.put(matrix4f.m03).put(matrix4f.m13).put(matrix4f.m23).put(matrix4f.m33);
    buffer.flip();
    return buffer;
  }
}
