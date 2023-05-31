// 
// Decompiled by Procyon v0.5.36
// 

package org.newdawn.slick.geom;

public strictfp class Circle extends Ellipse
{
    public float radius;
    
    public Circle(final float centerPointX, final float centerPointY, final float radius) {
        this(centerPointX, centerPointY, radius, 50);
    }
    
    public Circle(final float centerPointX, final float centerPointY, final float radius, final int segmentCount) {
        super(centerPointX, centerPointY, radius, radius, segmentCount);
        this.x = centerPointX - radius;
        this.y = centerPointY - radius;
        this.radius = radius;
        this.boundingCircleRadius = radius;
    }
    
    public strictfp float getCenterX() {
        return this.getX() + this.radius;
    }
    
    public strictfp float getCenterY() {
        return this.getY() + this.radius;
    }
    
    public strictfp void setRadius(final float radius) {
        if (radius != this.radius) {
            this.pointsDirty = true;
            this.setRadii(this.radius = radius, radius);
        }
    }
    
    public strictfp float getRadius() {
        return this.radius;
    }
    
    public strictfp boolean intersects(final Shape shape) {
        if (shape instanceof Circle) {
            final Circle other = (Circle)shape;
            float totalRad2 = this.getRadius() + other.getRadius();
            if (Math.abs(other.getCenterX() - this.getCenterX()) > totalRad2) {
                return false;
            }
            if (Math.abs(other.getCenterY() - this.getCenterY()) > totalRad2) {
                return false;
            }
            totalRad2 *= totalRad2;
            final float dx = Math.abs(other.getCenterX() - this.getCenterX());
            final float dy = Math.abs(other.getCenterY() - this.getCenterY());
            return totalRad2 >= dx * dx + dy * dy;
        }
        else {
            if (shape instanceof Rectangle) {
                return this.intersects((Rectangle)shape);
            }
            return super.intersects(shape);
        }
    }
    
    public strictfp boolean contains(final float x, final float y) {
        return this.intersects(new Circle(x, y, 0.0f));
    }
    
    protected strictfp void findCenter() {
        (this.center = new float[2])[0] = this.x + this.radius;
        this.center[1] = this.y + this.radius;
    }
    
    protected strictfp void calculateRadius() {
        this.boundingCircleRadius = this.radius;
    }
    
    private strictfp boolean intersects(final Rectangle other) {
        final Rectangle box = other;
        final Circle circle = this;
        if (box.contains(this.x, this.y)) {
            return true;
        }
        final float x1 = box.getX();
        final float y1 = box.getY();
        final float x2 = box.getX() + box.getWidth();
        final float y2 = box.getY() + box.getHeight();
        final Line[] lines = { new Line(x1, y1, x2, y1), new Line(x2, y1, x2, y2), new Line(x2, y2, x1, y2), new Line(x1, y2, x1, y1) };
        final float r2 = circle.getRadius() * circle.getRadius();
        final Vector2f pos = new Vector2f(circle.getCenterX(), circle.getCenterY());
        for (int i = 0; i < 4; ++i) {
            final float dis = lines[i].distanceSquared(pos);
            if (dis < r2) {
                return true;
            }
        }
        return false;
    }
}
