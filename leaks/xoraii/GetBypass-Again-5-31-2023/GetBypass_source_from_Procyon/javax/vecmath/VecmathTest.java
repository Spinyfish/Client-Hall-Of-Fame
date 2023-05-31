// 
// Decompiled by Procyon v0.5.36
// 

package javax.vecmath;

import java.io.IOException;

public class VecmathTest
{
    public static String NL;
    public static float epsilon;
    
    public static boolean equals(final double m1, final double m2) {
        return Math.abs(m1 - m2) < VecmathTest.epsilon;
    }
    
    public static boolean equals(final Matrix3d m1, final Matrix3d m2) {
        return m1.epsilonEquals(m2, VecmathTest.epsilon);
    }
    
    public static boolean equals(final Matrix4d m1, final Matrix4d m2) {
        return m1.epsilonEquals(m2, (double)VecmathTest.epsilon);
    }
    
    public static boolean equals(final Tuple4d m1, final Tuple4d m2) {
        return m1.epsilonEquals(m2, VecmathTest.epsilon);
    }
    
    public static boolean equals(final Tuple3d m1, final Tuple3d m2) {
        return m1.epsilonEquals(m2, VecmathTest.epsilon);
    }
    
    public static boolean equals(final Matrix3f m1, final Matrix3f m2) {
        return m1.epsilonEquals(m2, VecmathTest.epsilon);
    }
    
    public static boolean equals(final Matrix4f m1, final Matrix4f m2) {
        return m1.epsilonEquals(m2, VecmathTest.epsilon);
    }
    
    public static boolean equals(final GMatrix m1, final GMatrix m2) {
        return m1.epsilonEquals(m2, (double)VecmathTest.epsilon);
    }
    
    public static boolean equals(final GVector v1, final GVector v2) {
        return v1.epsilonEquals(v2, VecmathTest.epsilon);
    }
    
    public static boolean equals(final Tuple4f m1, final Tuple4f m2) {
        return m1.epsilonEquals(m2, VecmathTest.epsilon);
    }
    
    public static boolean equals(final Tuple3f m1, final Tuple3f m2) {
        return m1.epsilonEquals(m2, VecmathTest.epsilon);
    }
    
    public static boolean equals(final AxisAngle4d a1, final AxisAngle4d a2) {
        if (0.0 < a1.x * a2.x + a1.y * a2.y + a1.z * a2.z) {
            return equals(a1.y * a2.z - a1.z * a2.y, 0.0) && equals(a1.z * a2.x - a1.x * a2.z, 0.0) && equals(a1.x * a2.y - a1.y * a2.x, 0.0) && equals(a1.angle, a2.angle);
        }
        return equals(a1.y * a2.z - a1.z * a2.y, 0.0) && equals(a1.z * a2.x - a1.x * a2.z, 0.0) && equals(a1.x * a2.y - a1.y * a2.x, 0.0) && (equals(a1.angle, -a2.angle) || equals(a1.angle + a2.angle, 6.283185307179586) || equals(a1.angle + a2.angle, -6.283185307179586));
    }
    
    public static boolean equals(final AxisAngle4f a1, final AxisAngle4f a2) {
        if (0.0f < a1.x * a2.x + a1.y * a2.y + a1.z * a2.z) {
            return equals(a1.y * a2.z - a1.z * a2.y, 0.0) && equals(a1.z * a2.x - a1.x * a2.z, 0.0) && equals(a1.x * a2.y - a1.y * a2.x, 0.0) && equals(a1.angle, a2.angle);
        }
        return equals(a1.y * a2.z - a1.z * a2.y, 0.0) && equals(a1.z * a2.x - a1.x * a2.z, 0.0) && equals(a1.x * a2.y - a1.y * a2.x, 0.0) && (equals(a1.angle, -a2.angle) || equals(a1.angle + a2.angle, 6.283185307179586) || equals(a1.angle + a2.angle, -6.283185307179586));
    }
    
    public static void ASSERT(final boolean condition) {
        if (!condition) {
            throw new InternalError("Vecmath Test Failed!");
        }
    }
    
    public static void ASSERT(final boolean condition, final String comment) {
        if (!condition) {
            throw new InternalError("Vecmath Test Failed!: " + comment);
        }
    }
    
    public static void exit() {
        System.out.println("java.vecmath all test passed successfully.");
        System.out.print("Quit ?");
        try {
            System.in.read();
        }
        catch (IOException ex) {}
    }
    
    public static void main(final String[] v) {
        System.out.print("Vector3d ...");
        Vector3dTest();
        System.out.println("ok.");
        System.out.print("Vector3f ...");
        Vector3fTest();
        System.out.println("ok.");
        System.out.print("Matrix3d with Quat4d, AxisAngle4d, Point/Vector3d interaction ...");
        Matrix3dTest();
        System.out.println("ok.");
        System.out.print("Matrix3f with Quat4f, AxisAngle4f, Point/Vector3f interaction ...");
        Matrix3fTest();
        System.out.println("ok.");
        System.out.print("Matrix4d with Quat4d, AxisAngle4d, Point/Vector3d interaction ...");
        Matrix4dTest();
        System.out.println("ok.");
        System.out.print("Matrix4f with Quat4f, AxisAngle4f, Point/Vector3f interaction ...");
        Matrix4fTest();
        System.out.println("ok.");
        System.out.print("GMatrix with GVector interaction ...");
        GMatrixTest();
        System.out.println("ok.");
        System.out.print("SVD test ...");
        SVDTest();
        System.out.println("ok.");
        exit();
    }
    
    public static void Vector3dTest() {
        final Vector3d zeroVector = new Vector3d();
        final Vector3d v1 = new Vector3d(2.0, 3.0, 4.0);
        final Vector3d v2 = new Vector3d(2.0, 5.0, -8.0);
        final Vector3d v3 = new Vector3d();
        v3.cross(v1, v2);
        ASSERT(equals(v3.dot(v1), 0.0));
        ASSERT(equals(v3.dot(v2), 0.0));
        v1.cross(v1, v2);
        ASSERT(equals(v1, new Vector3d(-44.0, 24.0, 4.0)));
        ASSERT(equals(v2.lengthSquared(), 93.0));
        ASSERT(equals(v2.length(), Math.sqrt(93.0)));
        v1.set(v2);
        v2.normalize();
        ASSERT(equals(v2.length(), 1.0));
        v1.cross(v2, v1);
        ASSERT(equals(v1, zeroVector));
        v1.set(1.0, 2.0, 3.0);
        v2.set(-1.0, -6.0, -3.0);
        double ang = v1.angle(v2);
        ASSERT(equals(v1.length() * v2.length() * Math.cos(ang), v1.dot(v2)));
        v1.set(v2);
        ang = v1.angle(v2);
        ASSERT(equals(ang, 0.0));
        ASSERT(equals(v1.length() * v2.length() * Math.cos(ang), v1.dot(v2)));
        v1.set(1.0, 2.0, 3.0);
        v2.set(1.0, 2.0, 3.00001);
        ang = v1.angle(v2);
        ASSERT(equals(v1.length() * v2.length() * Math.cos(ang), v1.dot(v2)));
        v1.set(1.0, 2.0, 3.0);
        v2.set(-1.0, -2.0, -3.00001);
        ang = v1.angle(v2);
        ASSERT(equals(v1.length() * v2.length() * Math.cos(ang), v1.dot(v2)));
    }
    
    public static void Vector3fTest() {
        final Vector3f zeroVector = new Vector3f();
        final Vector3f v1 = new Vector3f(2.0f, 3.0f, 4.0f);
        final Vector3f v2 = new Vector3f(2.0f, 5.0f, -8.0f);
        final Vector3f v3 = new Vector3f();
        v3.cross(v1, v2);
        ASSERT(equals(v3.dot(v1), 0.0));
        ASSERT(equals(v3.dot(v2), 0.0));
        v1.cross(v1, v2);
        ASSERT(equals(v1, new Vector3f(-44.0f, 24.0f, 4.0f)));
        ASSERT(equals(v2.lengthSquared(), 93.0));
        ASSERT(equals(v2.length(), Math.sqrt(93.0)));
        v1.set(v2);
        v2.normalize();
        ASSERT(equals(v2.length(), 1.0));
        v1.cross(v2, v1);
        ASSERT(equals(v1, zeroVector));
        v1.set(1.0f, 2.0f, 3.0f);
        v2.set(-1.0f, -6.0f, -3.0f);
        double ang = v1.angle(v2);
        ASSERT(equals(v1.length() * v2.length() * Math.cos(ang), v1.dot(v2)));
        v1.set(v2);
        ang = v1.angle(v2);
        ASSERT(equals(ang, 0.0));
        ASSERT(equals(v1.length() * v2.length() * Math.cos(ang), v1.dot(v2)));
    }
    
    public static void Matrix3dTest() {
        final Matrix3d O = new Matrix3d();
        final Matrix3d I = new Matrix3d();
        I.setIdentity();
        final Matrix3d m1 = new Matrix3d();
        Matrix3d m2 = new Matrix3d();
        final double[] v = { 2.0, 1.0, 4.0, 1.0, -2.0, 3.0, -3.0, -1.0, 1.0 };
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                m1.setElement(i, j, i * 2 * j + 3);
            }
        }
        for (int k = 0; k < 3; ++k) {
            for (int l = 0; l < 3; ++l) {
                ASSERT(equals(m1.getElement(k, l), k * 2 * l + 3));
            }
        }
        m1.set(v);
        m2 = new Matrix3d(m1);
        m2.mul(O);
        ASSERT(equals(m2, O));
        m2.mul(m1, I);
        ASSERT(equals(m2, m1));
        ASSERT(equals(m1.determinant(), -36.0));
        m2.negate(m1);
        m2.add(m1);
        ASSERT(equals(m2, O));
        m2.negate(m1);
        final Matrix3d m3 = new Matrix3d(m1);
        m3.sub(m2);
        m3.mul(0.5);
        ASSERT(equals(m1, m3));
        m3.invert(m2);
        m3.mul(m2);
        ASSERT(equals(m3, I));
        final Point3d p1 = new Point3d(1.0, 2.0, 3.0);
        final Vector3d v2 = new Vector3d(2.0, -1.0, -4.0);
        p1.set(1.0, 0.0, 0.0);
        m1.rotZ(0.5235987755982988);
        m1.transform(p1);
        ASSERT(equals(p1, new Point3d(Math.cos(0.5235987755982988), Math.sin(0.5235987755982988), 0.0)));
        p1.set(1.0, 0.0, 0.0);
        m1.rotY(1.0471975511965976);
        m1.transform(p1);
        ASSERT(equals(p1, new Point3d(Math.cos(1.0471975511965976), 0.0, -Math.sin(1.0471975511965976))));
        final AxisAngle4d a1 = new AxisAngle4d(0.0, 1.0, 0.0, 1.0471975511965976);
        p1.set(1.0, 0.0, 0.0);
        m1.set(a1);
        m1.transform(p1, p1);
        ASSERT(equals(p1, new Point3d(Math.cos(1.0471975511965976), 0.0, -Math.sin(1.0471975511965976))));
        final Quat4d q1 = new Quat4d();
        p1.set(1.0, 0.0, 0.0);
        q1.set(a1);
        m2.set(q1);
        ASSERT(equals(m1, m2));
        m2.transform(p1, p1);
        ASSERT(equals(p1, new Point3d(Math.cos(1.0471975511965976), 0.0, -Math.sin(1.0471975511965976))));
        a1.set(1.0, 2.0, -3.0, 1.0471975511965976);
        Mat3dQuatAxisAngle(a1);
        a1.set(1.0, 2.0, 3.0, 3.141592653589793);
        Mat3dQuatAxisAngle(a1);
        a1.set(1.0, 0.1, 0.1, 3.141592653589793);
        Mat3dQuatAxisAngle(a1);
        a1.set(0.1, 1.0, 0.1, 3.141592653589793);
        Mat3dQuatAxisAngle(a1);
        a1.set(0.1, 0.1, 1.0, 3.141592653589793);
        Mat3dQuatAxisAngle(a1);
        a1.set(1.0, 1.0, 1.0, 2.0943951023931953);
        m1.set(a1);
        p1.set(1.0, 0.0, 0.0);
        m1.transform(p1);
        ASSERT(equals(p1, new Point3d(0.0, 1.0, 0.0)));
        m1.transform(p1);
        ASSERT(equals(p1, new Point3d(0.0, 0.0, 1.0)));
        m1.transform(p1);
        ASSERT(equals(p1, new Point3d(1.0, 0.0, 0.0)));
        m1.set(a1);
        ASSERT(equals(m1.determinant(), 1.0));
        ASSERT(equals(m1.getScale(), 1.0));
        m2.set(a1);
        m2.normalize();
        ASSERT(equals(m1, m2));
        m2.set(a1);
        m2.normalizeCP();
        ASSERT(equals(m1, m2));
        final double scale = 3.0;
        m2.rotZ(-0.7853981633974483);
        m2.mul(scale);
        ASSERT(equals(m2.determinant(), scale * scale * scale));
        ASSERT(equals(m2.getScale(), scale));
        m2.normalize();
        ASSERT(equals(m2.determinant(), 1.0));
        ASSERT(equals(m2.getScale(), 1.0));
        m2.rotX(1.0471975511965976);
        m2.mul(scale);
        ASSERT(equals(m2.determinant(), scale * scale * scale));
        ASSERT(equals(m2.getScale(), scale));
        m2.normalizeCP();
        ASSERT(equals(m2.determinant(), 1.0));
        ASSERT(equals(m2.getScale(), 1.0));
        m1.set(a1);
        m2.invert(m1);
        m1.transpose();
        ASSERT(equals(m1, m2));
    }
    
    static void Mat3dQuatAxisAngle(final AxisAngle4d a1) {
        final Matrix3d m1 = new Matrix3d();
        final Matrix3d m2 = new Matrix3d();
        final AxisAngle4d a2 = new AxisAngle4d();
        final Quat4d q1 = new Quat4d();
        Quat4d q2 = new Quat4d();
        q1.set(a1);
        a2.set(q1);
        ASSERT(equals(a1, a2));
        q2 = new Quat4d();
        q2.set(a2);
        ASSERT(equals(q1, q2));
        q1.set(a1);
        m1.set(q1);
        q2.set(m1);
        ASSERT(equals(q1, q2));
        m2.set(q2);
        ASSERT(equals(m1, m2));
        m1.set(a1);
        a2.set(m1);
        ASSERT(equals(a1, a2));
        m2.set(a1);
        ASSERT(equals(m1, m2));
        a1.x *= 2.0;
        a1.y *= 2.0;
        a1.z *= 2.0;
        m2.set(a1);
        a1.x = -a1.x;
        a1.y = -a1.y;
        a1.z = -a1.z;
        a1.angle = -a1.angle;
        m2.set(a1);
        ASSERT(equals(m1, m2));
    }
    
    public static void Matrix3fTest() {
    }
    
    static void Mat4dQuatAxisAngle(final AxisAngle4d a1) {
        final Matrix4d m1 = new Matrix4d();
        final Matrix4d m2 = new Matrix4d();
        final AxisAngle4d a2 = new AxisAngle4d();
        final Quat4d q1 = new Quat4d();
        Quat4d q2 = new Quat4d();
        q1.set(a1);
        a2.set(q1);
        ASSERT(equals(a1, a2));
        q2 = new Quat4d();
        q2.set(a2);
        ASSERT(equals(q1, q2));
        q1.set(a1);
        m1.set(q1);
        q2.set(m1);
        ASSERT(equals(q1, q2));
        m2.set(q2);
        ASSERT(equals(m1, m2));
        m1.set(a1);
        a2.set(m1);
        ASSERT(equals(a1, a2));
        m2.set(a1);
        ASSERT(equals(m1, m2));
        a1.x *= 2.0;
        a1.y *= 2.0;
        a1.z *= 2.0;
        m2.set(a1);
        a1.x = -a1.x;
        a1.y = -a1.y;
        a1.z = -a1.z;
        a1.angle = -a1.angle;
        m2.set(a1);
        ASSERT(equals(m1, m2));
    }
    
    public static void Matrix4dTest() {
        final Matrix4d O = new Matrix4d();
        final Matrix4d I = new Matrix4d();
        I.setIdentity();
        Matrix4d m1 = new Matrix4d();
        Matrix4d m2 = new Matrix4d();
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                m1.setElement(i, j, i * 2 * j + 3);
            }
        }
        for (int k = 0; k < 4; ++k) {
            for (int l = 0; l < 4; ++l) {
                ASSERT(equals(m1.getElement(k, l), k * 2 * l + 3));
            }
        }
        m1 = new Matrix4d(2.0, 1.0, 4.0, 1.0, -2.0, 3.0, -3.0, 1.0, -1.0, 1.0, 2.0, 2.0, 0.0, 8.0, 1.0, -10.0);
        m2 = new Matrix4d(m1);
        m2.mul(O);
        ASSERT(equals(m2, O), "O = m2 x O");
        m2.mul(m1, I);
        ASSERT(equals(m2, m1), "m2 = m1 x I");
        m2.negate(m1);
        m2.add(m1);
        ASSERT(equals(m2, O));
        final double[] v = { 5.0, 1.0, 4.0, 0.0, 2.0, 3.0, -4.0, -1.0, 2.0, 3.0, -4.0, -1.0, 1.0, 1.0, 1.0, 1.0 };
        m2.set(v);
        m2.negate(m1);
        final Matrix4d m3 = new Matrix4d(m1);
        m3.sub(m2);
        m3.mul(0.5);
        ASSERT(equals(m1, m3));
        m2 = new Matrix4d(0.5, 1.0, 4.0, 1.0, -2.0, 3.0, -4.0, -1.0, 1.0, 9.0, 100.0, 2.0, -20.0, 2.0, 1.0, 9.0);
        m3.invert(m2);
        m3.mul(m2);
        ASSERT(equals(m3, I));
        m1 = new Matrix4d(-1.0, 2.0, 0.0, 3.0, -1.0, 1.0, -3.0, -1.0, 1.0, 2.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0);
        final Point3d p1 = new Point3d(1.0, 2.0, 3.0);
        final Vector3d v2 = new Vector3d();
        final Vector3d v3 = new Vector3d(1.0, 2.0, 3.0);
        final Vector4d V2 = new Vector4d(2.0, -1.0, -4.0, 1.0);
        ASSERT(m1.toString().equals("[" + VecmathTest.NL + "  [-1.0\t2.0\t0.0\t3.0]" + VecmathTest.NL + "  [-1.0\t1.0\t-3.0\t-1.0]" + VecmathTest.NL + "  [1.0\t2.0\t1.0\t1.0]" + VecmathTest.NL + "  [0.0\t0.0\t0.0\t1.0] ]"));
        m1.transform(p1);
        ASSERT(equals(p1, new Point3d(6.0, -9.0, 9.0)));
        m1.transform(V2, V2);
        ASSERT(equals(V2, new Vector4d(-1.0, 8.0, -3.0, 1.0)));
        p1.set(1.0, 0.0, 0.0);
        m1.rotZ(0.5235987755982988);
        m1.transform(p1);
        ASSERT(equals(p1, new Point3d(Math.cos(0.5235987755982988), Math.sin(0.5235987755982988), 0.0)));
        p1.set(1.0, 0.0, 0.0);
        m1.rotY(1.0471975511965976);
        m1.transform(p1);
        ASSERT(equals(p1, new Point3d(Math.cos(1.0471975511965976), 0.0, -Math.sin(1.0471975511965976))));
        final AxisAngle4d a1 = new AxisAngle4d(0.0, 1.0, 0.0, 1.0471975511965976);
        p1.set(1.0, 0.0, 0.0);
        m1.set(a1);
        m1.transform(p1, p1);
        ASSERT(equals(p1, new Point3d(Math.cos(1.0471975511965976), 0.0, -Math.sin(1.0471975511965976))));
        final Quat4d q1 = new Quat4d();
        p1.set(1.0, 0.0, 0.0);
        q1.set(a1);
        m2.set(q1);
        ASSERT(equals(m1, m2));
        m2.transform(p1, p1);
        ASSERT(equals(p1, new Point3d(Math.cos(1.0471975511965976), 0.0, -Math.sin(1.0471975511965976))));
        a1.set(1.0, 2.0, -3.0, 1.0471975511965976);
        Mat4dQuatAxisAngle(a1);
        a1.set(1.0, 2.0, 3.0, 3.141592653589793);
        Mat4dQuatAxisAngle(a1);
        a1.set(1.0, 0.1, 0.1, 3.141592653589793);
        Mat4dQuatAxisAngle(a1);
        a1.set(0.1, 1.0, 0.1, 3.141592653589793);
        Mat4dQuatAxisAngle(a1);
        a1.set(0.1, 0.1, 1.0, 3.141592653589793);
        Mat4dQuatAxisAngle(a1);
        a1.set(1.0, 1.0, 1.0, 2.0943951023931953);
        m1.set(a1);
        p1.set(1.0, 0.0, 0.0);
        m1.transform(p1);
        ASSERT(equals(p1, new Point3d(0.0, 1.0, 0.0)));
        m1.transform(p1);
        ASSERT(equals(p1, new Point3d(0.0, 0.0, 1.0)));
        m1.transform(p1);
        ASSERT(equals(p1, new Point3d(1.0, 0.0, 0.0)));
        m1.set(a1);
        ASSERT(equals(m1.determinant(), 1.0));
        ASSERT(equals(m1.getScale(), 1.0));
        m2.set(a1);
        m1.set(a1);
        m2.invert(m1);
        m1.transpose();
        ASSERT(equals(m1, m2));
        final Matrix3d n1 = new Matrix3d();
        n1.set(a1);
        final Matrix3d n2 = new Matrix3d();
        v3.set(2.0, -1.0, -1.0);
        m1.set(n1, v3, 0.4);
        m2.set(n1, v3, 0.4);
        final Vector3d v4 = new Vector3d();
        final double s = m1.get(n2, v4);
        ASSERT(equals(n1, n2));
        ASSERT(equals(s, 0.4));
        ASSERT(equals(v3, v4));
        ASSERT(equals(m1, m2));
    }
    
    public static void Matrix4fTest() {
    }
    
    public static void GMatrixTest() {
        final GMatrix I44 = new GMatrix(4, 4);
        final GMatrix O44 = new GMatrix(4, 4);
        O44.setZero();
        final GMatrix O45 = new GMatrix(3, 4);
        O45.setZero();
        final GMatrix m1 = new GMatrix(3, 4);
        final GMatrix m2 = new GMatrix(3, 4);
        final Matrix3d mm1 = new Matrix3d();
        final Matrix3d mm2 = new Matrix3d();
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 4; ++j) {
                m1.setElement(i, j, (i + 1) * (j + 2));
                if (j < 3) {
                    mm1.setElement(i, j, (i + 1) * (j + 2));
                }
            }
        }
        for (int k = 0; k < 3; ++k) {
            for (int l = 0; l < 4; ++l) {
                ASSERT(equals(m1.getElement(k, l), (k + 1) * (l + 2)));
            }
        }
        m1.get(mm2);
        ASSERT(equals(mm1, mm2));
        m2.mul(m1, I44);
        ASSERT(equals(m1, m2));
        m2.mul(m1, O44);
        ASSERT(equals(O45, m2));
        final Matrix4d mm3 = new Matrix4d(1.0, 2.0, 3.0, 4.0, -2.0, 3.0, -1.0, 3.0, -1.0, -2.0, -4.0, 1.0, 1.0, 1.0, -1.0, -2.0);
        final Matrix4d mm4 = new Matrix4d();
        final Matrix4d mm5 = new Matrix4d();
        mm5.set(mm3);
        m1.setSize(4, 4);
        m2.setSize(4, 4);
        m1.set(mm3);
        ASSERT(m1.toString().equals("[" + VecmathTest.NL + "  [1.0\t2.0\t3.0\t4.0]" + VecmathTest.NL + "  [-2.0\t3.0\t-1.0\t3.0]" + VecmathTest.NL + "  [-1.0\t-2.0\t-4.0\t1.0]" + VecmathTest.NL + "  [1.0\t1.0\t-1.0\t-2.0] ]"));
        m2.set(m1);
        m1.invert();
        mm3.invert();
        mm5.mul(mm3);
        ASSERT(equals(mm5, new Matrix4d(1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0)));
        m1.get(mm4);
        ASSERT(equals(mm3, mm4));
        m1.mul(m2);
        ASSERT(equals(m1, I44));
        final Matrix4d mm6 = new Matrix4d(1.0, 2.0, 3.0, 4.0, -2.0, 3.0, -1.0, 3.0, -1.0, -2.0, -4.0, 1.0, 1.0, 1.0, -1.0, -2.0);
        final Vector4d vv1 = new Vector4d(1.0, -1.0, -1.0, 2.0);
        final Vector4d vv2 = new Vector4d();
        final Vector4d vv3 = new Vector4d(4.0, 2.0, 7.0, -3.0);
        mm6.transform(vv1, vv2);
        ASSERT(equals(vv2, vv3));
        m1.set(mm6);
        final GVector x = new GVector(4);
        final GVector v2 = new GVector(4);
        final GVector b = new GVector(4);
        x.set(vv1);
        b.set(vv3);
        final GVector mx = new GVector(4);
        mx.mul(m1, x);
        ASSERT(equals(mx, b));
        final GVector p = new GVector(4);
        m1.LUD(m2, p);
        ASSERT(checkLUD(m1, m2, p));
        final GVector xx = new GVector(4);
        xx.LUDBackSolve(m2, b, p);
        ASSERT(equals(xx, x));
        final GMatrix u = new GMatrix(m1.getNumRow(), m1.getNumRow());
        final GMatrix w = new GMatrix(m1.getNumRow(), m1.getNumCol());
        final GMatrix v3 = new GMatrix(m1.getNumCol(), m1.getNumCol());
        final int rank = m1.SVD(u, w, v3);
        ASSERT(rank == 4);
        ASSERT(checkSVD(m1, u, w, v3));
        xx.SVDBackSolve(u, w, v3, b);
        ASSERT(equals(xx, x));
    }
    
    static boolean checkLUD(final GMatrix m, final GMatrix LU, final GVector permutation) {
        final int n = m.getNumCol();
        boolean ok = true;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                double aij = 0.0;
                for (int min = (i < j) ? i : j, k = 0; k <= min; ++k) {
                    if (i != k) {
                        aij += LU.getElement(i, k) * LU.getElement(k, j);
                    }
                    else {
                        aij += LU.getElement(k, j);
                    }
                }
                if (Math.abs(aij - m.getElement((int)permutation.getElement(i), j)) > VecmathTest.epsilon) {
                    System.out.println("a[" + i + "," + j + "] = " + aij + "(LU)ij ! = " + m.getElement((int)permutation.getElement(i), j));
                    ok = false;
                }
            }
        }
        return ok;
    }
    
    static boolean checkSVD(final GMatrix m, final GMatrix u, final GMatrix w, final GMatrix v) {
        boolean ok = true;
        final int wsize = (w.getNumRow() < w.getNumRow()) ? w.getNumRow() : w.getNumCol();
        for (int i = 0; i < m.getNumRow(); ++i) {
            for (int j = 0; j < m.getNumCol(); ++j) {
                double sum = 0.0;
                for (int k = 0; k < m.getNumCol(); ++k) {
                    sum += u.getElement(i, k) * w.getElement(k, k) * v.getElement(j, k);
                }
                if (VecmathTest.epsilon < Math.abs(m.getElement(i, j) - sum)) {
                    System.out.println("(SVD)ij = " + sum + " != a[" + i + "," + j + "] = " + m.getElement(i, j));
                    ok = false;
                }
            }
        }
        if (!ok) {
            System.out.print("[W] = ");
            System.out.println(w);
            System.out.print("[U] = ");
            System.out.println(u);
            System.out.print("[V] = ");
            System.out.println(v);
        }
        return ok;
    }
    
    public static void SVDTest() {
        final double[] val = { 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 0.0, 8.0, 7.0, 6.0, 5.0, 4.0, 3.0, 2.0, 1.0, 0.0, 1.0 };
        final int m = 5;
        final int n = 4;
        final GMatrix matA = new GMatrix(m, n, val);
        final GMatrix matU = new GMatrix(m, m);
        final GMatrix matW = new GMatrix(m, n);
        final GMatrix matV = new GMatrix(n, n);
        final int rank = matA.SVD(matU, matW, matV);
        final GMatrix matTEMP = new GMatrix(m, n);
        matTEMP.mul(matU, matW);
        matV.transpose();
        matTEMP.mul(matV);
        if (!equals(matTEMP, matA)) {
            System.out.println("matU=" + matU);
            System.out.println("matW=" + matW);
            System.out.println("matV=" + matV);
            System.out.println("matA=" + matA);
            System.out.println("UWV=" + matTEMP);
        }
        ASSERT(equals(matTEMP, matA));
    }
    
    static {
        VecmathTest.NL = System.getProperty("line.separator");
        VecmathTest.epsilon = 1.0E-5f;
    }
}
