package alos.stella.event.events;

import alos.stella.event.Event;

public class EventFogColor extends Event {
    public float red;
    public float green;
    public float blue;
    public int alpha;

    public EventFogColor(float red, float green, float blue, int alpha)
    {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public float getRed()
    {
        return red;
    }

    public void setRed(float red)
    {
        this.red = red;
    }

    public float getGreen()
    {
        return green;
    }

    public void setGreen(float green)
    {
        this.green = green;
    }

    public float getBlue()
    {
        return blue;
    }

    public void setBlue(float blue)
    {
        this.blue = blue;
    }

    public int getAlpha()
    {
        return alpha;
    }

    public void setAlpha(int alpha)
    {
        this.alpha = alpha;
    }
}