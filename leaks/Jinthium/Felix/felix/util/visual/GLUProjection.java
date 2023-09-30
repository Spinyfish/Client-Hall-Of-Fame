package felix.util.visual;

import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.BufferUtils;
import java.nio.IntBuffer;
import java.nio.FloatBuffer;


public final class GLUProjection
{
    private static GLUProjection instance;
    private final FloatBuffer coords = BufferUtils.createFloatBuffer(3);
    private IntBuffer viewport;
    private FloatBuffer modelview;
    private FloatBuffer projection;
    private Vector3D frustumPos;
    private Vector3D[] frustum;
    private Vector3D[] invFrustum;
    private Vector3D viewVec;
    private double displayWidth;
    private double displayHeight;
    private double widthScale;
    private double heightScale;
    private double bra;
    private double bla;
    private double tra;
    private double tla;
    private Line tb;
    private Line bb;
    private Line lb;
    private Line rb;
    private float fovY;
    private float fovX;
    private Vector3D lookVec;

    public static GLUProjection getInstance()
    {
        if (instance == null) {
            instance = new GLUProjection();
        }
        return instance;
    }

    public void updateMatrices(IntBuffer viewport, FloatBuffer modelview, FloatBuffer projection, double widthScale, double heightScale)
    {
        this.viewport = viewport;
        this.modelview = modelview;
        this.projection = projection;
        this.widthScale = widthScale;
        this.heightScale = heightScale;

        float fov = (float)Math.toDegrees(Math.atan(1.0D / this.projection.get(5)) * 2.0D);
        this.fovY = fov;
        this.displayWidth = this.viewport.get(2);
        this.displayHeight = this.viewport.get(3);
        this.fovX = ((float)Math.toDegrees(2.0D * Math.atan(this.displayWidth / this.displayHeight * Math.tan(Math.toRadians(this.fovY) / 2.0D))));

        Vector3D ft = new Vector3D(this.modelview.get(12), this.modelview.get(13), this.modelview.get(14));
        Vector3D lv = new Vector3D(this.modelview.get(0), this.modelview.get(1), this.modelview.get(2));
        Vector3D uv = new Vector3D(this.modelview.get(4), this.modelview.get(5), this.modelview.get(6));
        Vector3D fv = new Vector3D(this.modelview.get(8), this.modelview.get(9), this.modelview.get(10));

        Vector3D nuv = new Vector3D(0.0D, 1.0D, 0.0D);
        Vector3D nlv = new Vector3D(1.0D, 0.0D, 0.0D);
        Vector3D nfv = new Vector3D(0.0D, 0.0D, 1.0D);

        double yaw = Math.toDegrees(Math.atan2(nlv.cross(lv).length(), nlv.dot(lv))) + 180.0D;
        if (fv.x < 0.0D) {
            yaw = 360.0D - yaw;
        }
        double pitch = 0.0D;
        if (((-fv.y > 0.0D) && (yaw >= 90.0D) && (yaw < 270.0D)) || ((fv.y > 0.0D) && ((yaw < 90.0D) || (yaw >= 270.0D)))) {
            pitch = Math.toDegrees(Math.atan2(nuv.cross(uv).length(), nuv.dot(uv)));
        } else {
            pitch = -Math.toDegrees(Math.atan2(nuv.cross(uv).length(), nuv.dot(uv)));
        }
        this.lookVec = getRotationVector(yaw, pitch);

        Matrix4f modelviewMatrix = new Matrix4f();
        modelviewMatrix.load(this.modelview.asReadOnlyBuffer());
        modelviewMatrix.invert();

        this.frustumPos = new Vector3D(modelviewMatrix.m30, modelviewMatrix.m31, modelviewMatrix.m32);
        this.frustum = getFrustum(this.frustumPos.x, this.frustumPos.y, this.frustumPos.z, yaw, pitch, fov, 1.0D, this.displayWidth / this.displayHeight);
        this.invFrustum = getFrustum(this.frustumPos.x, this.frustumPos.y, this.frustumPos.z, yaw - 180.0D, -pitch, fov, 1.0D, this.displayWidth / this.displayHeight);

        this.viewVec = getRotationVector(yaw, pitch).normalized();

        this.bra = Math.toDegrees(Math.acos(this.displayHeight * heightScale / Math.sqrt(this.displayWidth * widthScale * this.displayWidth * widthScale + this.displayHeight * heightScale * this.displayHeight * heightScale)));
        this.bla = (360.0D - this.bra);
        this.tra = (this.bla - 180.0D);
        this.tla = (this.bra + 180.0D);

        this.rb = new Line(this.displayWidth * this.widthScale, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D);
        this.tb = new Line(0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D);
        this.lb = new Line(0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D);
        this.bb = new Line(0.0D, this.displayHeight * this.heightScale, 0.0D, 1.0D, 0.0D, 0.0D);
    }

    public Projection project(double x, double y, double z, ClampMode clampModeOutside, boolean extrudeInverted)
    {
        if ((this.viewport != null) && (this.modelview != null) && (this.projection != null))
        {
            Vector3D posVec = new Vector3D(x, y, z);
            boolean[] frustum = doFrustumCheck(this.frustum, this.frustumPos, x, y, z);
            final boolean outsideFrustum = frustum[0] || frustum[1] || frustum[2] || frustum[3];
            if (outsideFrustum)
            {
                boolean opposite = posVec.sub(this.frustumPos).dot(this.viewVec) <= 0.0D;

                boolean[] invFrustum = doFrustumCheck(this.invFrustum, this.frustumPos, x, y, z);
                final boolean outsideInvertedFrustum = invFrustum[0] || invFrustum[1] || invFrustum[2] || invFrustum[3];        if (((extrudeInverted) && (!outsideInvertedFrustum)) || ((outsideInvertedFrustum) && (clampModeOutside != ClampMode.NONE)))
            {
                if (((extrudeInverted) && (!outsideInvertedFrustum)) || ((clampModeOutside == ClampMode.DIRECT) && (outsideInvertedFrustum)))
                {
                    double vecX = 0.0D;
                    double vecY = 0.0D;
                    if (GLU.gluProject((float)x, (float)y, (float)z, this.modelview, this.projection, this.viewport, this.coords))
                    {
                        if (opposite)
                        {
                            vecX = this.displayWidth * this.widthScale - this.coords.get(0) * this.widthScale - this.displayWidth * this.widthScale / 2.0D;
                            vecY = this.displayHeight * this.heightScale - (this.displayHeight - this.coords.get(1)) * this.heightScale - this.displayHeight * this.heightScale / 2.0D;
                        }
                        else
                        {
                            vecX = this.coords.get(0) * this.widthScale - this.displayWidth * this.widthScale / 2.0D;
                            vecY = (this.displayHeight - this.coords.get(1)) * this.heightScale - this.displayHeight * this.heightScale / 2.0D;
                        }
                    }
                    else {
                        return new Projection(0.0D, 0.0D, GLUProjection.Projection.Type.FAIL);
                    }
                    Vector3D vec = new Vector3D(vecX, vecY, 0.0D).snormalize();
                    vecX = vec.x;
                    vecY = vec.y;

                    Line vectorLine = new Line(this.displayWidth * this.widthScale / 2.0D, this.displayHeight * this.heightScale / 2.0D, 0.0D, vecX, vecY, 0.0D);

                    double angle = Math.toDegrees(Math.acos(vec.y / Math.sqrt(vec.x * vec.x + vec.y * vec.y)));
                    if (vecX < 0.0D) {
                        angle = 360.0D - angle;
                    }
                    Vector3D intersect = new Vector3D(0.0D, 0.0D, 0.0D);
                    if ((angle >= this.bra) && (angle < this.tra)) {
                        intersect = this.rb.intersect(vectorLine);
                    } else if ((angle >= this.tra) && (angle < this.tla)) {
                        intersect = this.tb.intersect(vectorLine);
                    } else if ((angle >= this.tla) && (angle < this.bla)) {
                        intersect = this.lb.intersect(vectorLine);
                    } else {
                        intersect = this.bb.intersect(vectorLine);
                    }
                    return new Projection(intersect.x, intersect.y, outsideInvertedFrustum ? GLUProjection.Projection.Type.OUTSIDE : GLUProjection.Projection.Type.INVERTED);
                }
                if ((clampModeOutside == ClampMode.ORTHOGONAL) && (outsideInvertedFrustum))
                {
                    if (GLU.gluProject((float)x, (float)y, (float)z, this.modelview, this.projection, this.viewport, this.coords))
                    {
                        double guiX = this.coords.get(0) * this.widthScale;
                        double guiY = (this.displayHeight - this.coords.get(1)) * this.heightScale;
                        if (opposite)
                        {
                            guiX = this.displayWidth * this.widthScale - guiX;
                            guiY = this.displayHeight * this.heightScale - guiY;
                        }
                        if (guiX < 0.0D) {
                            guiX = 0.0D;
                        } else if (guiX > this.displayWidth * this.widthScale) {
                            guiX = this.displayWidth * this.widthScale;
                        }
                        if (guiY < 0.0D) {
                            guiY = 0.0D;
                        } else if (guiY > this.displayHeight * this.heightScale) {
                            guiY = this.displayHeight * this.heightScale;
                        }
                        return new Projection(guiX, guiY, outsideInvertedFrustum ? GLUProjection.Projection.Type.OUTSIDE : GLUProjection.Projection.Type.INVERTED);
                    }
                    return new Projection(0.0D, 0.0D, GLUProjection.Projection.Type.FAIL);
                }
            }
            else
            {
                if (GLU.gluProject((float)x, (float)y, (float)z, this.modelview, this.projection, this.viewport, this.coords))
                {
                    double guiX = this.coords.get(0) * this.widthScale;
                    double guiY = (this.displayHeight - this.coords.get(1)) * this.heightScale;
                    if (opposite)
                    {
                        guiX = this.displayWidth * this.widthScale - guiX;
                        guiY = this.displayHeight * this.heightScale - guiY;
                    }
                    return new Projection(guiX, guiY, outsideInvertedFrustum ? GLUProjection.Projection.Type.OUTSIDE : GLUProjection.Projection.Type.INVERTED);
                }
                return new Projection(0.0D, 0.0D, GLUProjection.Projection.Type.FAIL);
            }
            }
            else
            {
                if (GLU.gluProject((float)x, (float)y, (float)z, this.modelview, this.projection, this.viewport, this.coords))
                {
                    double guiX = this.coords.get(0) * this.widthScale;
                    double guiY = (this.displayHeight - this.coords.get(1)) * this.heightScale;
                    return new Projection(guiX, guiY, GLUProjection.Projection.Type.INSIDE);
                }
                return new Projection(0.0D, 0.0D, GLUProjection.Projection.Type.FAIL);
            }
        }
        return new Projection(0.0D, 0.0D, GLUProjection.Projection.Type.FAIL);
    }

    public boolean[] doFrustumCheck(Vector3D[] frustumCorners, Vector3D frustumPos, double x, double y, double z)
    {
        Vector3D point = new Vector3D(x, y, z);
        boolean c1 = crossPlane(new Vector3D[] { frustumPos, frustumCorners[3], frustumCorners[0] }, point);
        boolean c2 = crossPlane(new Vector3D[] { frustumPos, frustumCorners[0], frustumCorners[1] }, point);
        boolean c3 = crossPlane(new Vector3D[] { frustumPos, frustumCorners[1], frustumCorners[2] }, point);
        boolean c4 = crossPlane(new Vector3D[] { frustumPos, frustumCorners[2], frustumCorners[3] }, point);
        return new boolean[] { c1, c2, c3, c4 };
    }

    public boolean crossPlane(Vector3D[] plane, Vector3D point)
    {
        Vector3D z = new Vector3D(0.0D, 0.0D, 0.0D);
        Vector3D e0 = plane[1].sub(plane[0]);
        Vector3D e1 = plane[2].sub(plane[0]);
        Vector3D normal = e0.cross(e1).snormalize();
        double D = z.sub(normal).dot(plane[2]);
        double dist = normal.dot(point) + D;
        return dist >= 0.0D;
    }

    public Vector3D[] getFrustum(double x, double y, double z, double rotationYaw, double rotationPitch, double fov, double farDistance, double aspectRatio)
    {
        Vector3D viewVec = getRotationVector(rotationYaw, rotationPitch).snormalize();
        double hFar = 2.0D * Math.tan(Math.toRadians(fov / 2.0D)) * farDistance;
        double wFar = hFar * aspectRatio;
        Vector3D view = getRotationVector(rotationYaw, rotationPitch).snormalize();
        Vector3D up = getRotationVector(rotationYaw, rotationPitch - 90.0D).snormalize();
        Vector3D right = getRotationVector(rotationYaw + 90.0D, 0.0D).snormalize();
        Vector3D camPos = new Vector3D(x, y, z);
        Vector3D view_camPos_product = view.add(camPos);
        Vector3D fc = new Vector3D(view_camPos_product.x * farDistance, view_camPos_product.y * farDistance, view_camPos_product.z * farDistance);
        Vector3D topLeftfrustum = new Vector3D(fc.x + up.x * hFar / 2.0D - right.x * wFar / 2.0D, fc.y + up.y * hFar / 2.0D - right.y * wFar / 2.0D, fc.z + up.z * hFar / 2.0D - right.z * wFar / 2.0D);
        Vector3D downLeftfrustum = new Vector3D(fc.x - up.x * hFar / 2.0D - right.x * wFar / 2.0D, fc.y - up.y * hFar / 2.0D - right.y * wFar / 2.0D, fc.z - up.z * hFar / 2.0D - right.z * wFar / 2.0D);
        Vector3D topRightfrustum = new Vector3D(fc.x + up.x * hFar / 2.0D + right.x * wFar / 2.0D, fc.y + up.y * hFar / 2.0D + right.y * wFar / 2.0D, fc.z + up.z * hFar / 2.0D + right.z * wFar / 2.0D);
        Vector3D downRightfrustum = new Vector3D(fc.x - up.x * hFar / 2.0D + right.x * wFar / 2.0D, fc.y - up.y * hFar / 2.0D + right.y * wFar / 2.0D, fc.z - up.z * hFar / 2.0D + right.z * wFar / 2.0D);
        return new Vector3D[] { topLeftfrustum, downLeftfrustum, downRightfrustum, topRightfrustum };
    }

    public Vector3D[] getFrustum()
    {
        return this.frustum;
    }

    public float getFovX()
    {
        return this.fovX;
    }

    public float getFovY()
    {
        return this.fovY;
    }

    public Vector3D getLookVector()
    {
        return this.lookVec;
    }

    public Vector3D getRotationVector(double rotYaw, double rotPitch)
    {
        double c = Math.cos(-rotYaw * 0.01745329238474369D - 3.141592653589793D);
        double s = Math.sin(-rotYaw * 0.01745329238474369D - 3.141592653589793D);
        double nc = -Math.cos(-rotPitch * 0.01745329238474369D);
        double ns = Math.sin(-rotPitch * 0.01745329238474369D);
        return new Vector3D(s * nc, ns, c * nc);
    }

    public static enum ClampMode
    {
        ORTHOGONAL,  DIRECT,  NONE;

        private ClampMode() {}
    }

    public static class Line
    {
        public GLUProjection.Vector3D sourcePoint = new GLUProjection.Vector3D(0.0D, 0.0D, 0.0D);
        public GLUProjection.Vector3D direction = new GLUProjection.Vector3D(0.0D, 0.0D, 0.0D);

        public Line(double sx, double sy, double sz, double dx, double dy, double dz)
        {
            this.sourcePoint.x = sx;
            this.sourcePoint.y = sy;
            this.sourcePoint.z = sz;
            this.direction.x = dx;
            this.direction.y = dy;
            this.direction.z = dz;
        }

        public GLUProjection.Vector3D intersect(Line line)
        {
            double a = this.sourcePoint.x;
            double b = this.direction.x;
            double c = line.sourcePoint.x;
            double d = line.direction.x;
            double e = this.sourcePoint.y;
            double f = this.direction.y;
            double g = line.sourcePoint.y;
            double h = line.direction.y;
            double te = -(a * h - c * h - d * (e - g));
            double be = b * h - d * f;
            if (be == 0.0D) {
                return intersectXZ(line);
            }
            double t = te / be;
            GLUProjection.Vector3D result = new GLUProjection.Vector3D(0.0D, 0.0D, 0.0D);
            result.x = (this.sourcePoint.x + this.direction.x * t);
            result.y = (this.sourcePoint.y + this.direction.y * t);
            result.z = (this.sourcePoint.z + this.direction.z * t);
            return result;
        }

        private GLUProjection.Vector3D intersectXZ(Line line)
        {
            double a = this.sourcePoint.x;
            double b = this.direction.x;
            double c = line.sourcePoint.x;
            double d = line.direction.x;
            double e = this.sourcePoint.z;
            double f = this.direction.z;
            double g = line.sourcePoint.z;
            double h = line.direction.z;
            double te = -(a * h - c * h - d * (e - g));
            double be = b * h - d * f;
            if (be == 0.0D) {
                return intersectYZ(line);
            }
            double t = te / be;
            GLUProjection.Vector3D result = new GLUProjection.Vector3D(0.0D, 0.0D, 0.0D);
            result.x = (this.sourcePoint.x + this.direction.x * t);
            result.y = (this.sourcePoint.y + this.direction.y * t);
            result.z = (this.sourcePoint.z + this.direction.z * t);
            return result;
        }

        private GLUProjection.Vector3D intersectYZ(Line line)
        {
            double a = this.sourcePoint.y;
            double b = this.direction.y;
            double c = line.sourcePoint.y;
            double d = line.direction.y;
            double e = this.sourcePoint.z;
            double f = this.direction.z;
            double g = line.sourcePoint.z;
            double h = line.direction.z;
            double te = -(a * h - c * h - d * (e - g));
            double be = b * h - d * f;
            if (be == 0.0D) {
                return null;
            }
            double t = te / be;
            GLUProjection.Vector3D result = new GLUProjection.Vector3D(0.0D, 0.0D, 0.0D);
            result.x = (this.sourcePoint.x + this.direction.x * t);
            result.y = (this.sourcePoint.y + this.direction.y * t);
            result.z = (this.sourcePoint.z + this.direction.z * t);
            return result;
        }

        public GLUProjection.Vector3D intersectPlane(GLUProjection.Vector3D pointOnPlane, GLUProjection.Vector3D planeNormal)
        {
            GLUProjection.Vector3D result = new GLUProjection.Vector3D(this.sourcePoint.x, this.sourcePoint.y, this.sourcePoint.z);
            double d = pointOnPlane.sub(this.sourcePoint).dot(planeNormal) / this.direction.dot(planeNormal);
            result.sadd(this.direction.mul(d));
            if (this.direction.dot(planeNormal) == 0.0D) {
                return null;
            }
            return result;
        }
    }

    public static class Vector3D
    {
        public double x;
        public double y;
        public double z;

        public Vector3D(double x, double y, double z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Vector3D add(Vector3D v)
        {
            return new Vector3D(this.x + v.x, this.y + v.y, this.z + v.z);
        }

        public Vector3D add(double x, double y, double z)
        {
            return new Vector3D(this.x + x, this.y + y, this.z + z);
        }

        public Vector3D sub(Vector3D v)
        {
            return new Vector3D(this.x - v.x, this.y - v.y, this.z - v.z);
        }

        public Vector3D sub(double x, double y, double z)
        {
            return new Vector3D(this.x - x, this.y - y, this.z - z);
        }

        public Vector3D normalized()
        {
            double len = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
            return new Vector3D(this.x / len, this.y / len, this.z / len);
        }

        public double dot(Vector3D v)
        {
            return this.x * v.x + this.y * v.y + this.z * v.z;
        }

        public Vector3D cross(Vector3D v)
        {
            return new Vector3D(this.y * v.z - this.z * v.y, this.z * v.x - this.x * v.z, this.x * v.y - this.y * v.x);
        }

        public Vector3D mul(double m)
        {
            return new Vector3D(this.x * m, this.y * m, this.z * m);
        }

        public Vector3D div(double d)
        {
            return new Vector3D(this.x / d, this.y / d, this.z / d);
        }

        public double length()
        {
            return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        }

        public Vector3D sadd(Vector3D v)
        {
            this.x += v.x;
            this.y += v.y;
            this.z += v.z;
            return this;
        }

        public Vector3D sadd(double x, double y, double z)
        {
            this.x += x;
            this.y += y;
            this.z += z;
            return this;
        }

        public Vector3D ssub(Vector3D v)
        {
            this.x -= v.x;
            this.y -= v.y;
            this.z -= v.z;
            return this;
        }

        public Vector3D ssub(double x, double y, double z)
        {
            this.x -= x;
            this.y -= y;
            this.z -= z;
            return this;
        }

        public Vector3D snormalize()
        {
            double len = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
            this.x /= len;
            this.y /= len;
            this.z /= len;
            return this;
        }

        public Vector3D scross(Vector3D v)
        {
            this.x = (this.y * v.z - this.z * v.y);
            this.y = (this.z * v.x - this.x * v.z);
            this.z = (this.x * v.y - this.y * v.x);
            return this;
        }

        public Vector3D smul(double m)
        {
            this.x *= m;
            this.y *= m;
            this.z *= m;
            return this;
        }

        public Vector3D sdiv(double d)
        {
            this.x /= d;
            this.y /= d;
            this.z /= d;
            return this;
        }

        public String toString()
        {
            return "(X: " + this.x + " Y: " + this.y + " Z: " + this.z + ")";
        }
    }

    public static class Projection
    {
        private final double x;
        private final double y;
        private final Type t;

        public Projection(double x, double y, Type t)
        {
            this.x = x;
            this.y = y;
            this.t = t;
        }

        public double getX()
        {
            return this.x;
        }

        public double getY()
        {
            return this.y;
        }

        public Type getType()
        {
            return this.t;
        }

        public boolean isType(Type type)
        {
            return this.t == type;
        }

        public static enum Type
        {
            INSIDE,  OUTSIDE,  INVERTED,  FAIL;

            private Type() {}
        }
    }
}
