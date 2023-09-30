package me.wavelength.baseclient.event.events;

import me.wavelength.baseclient.event.Event;







public class EventPreUpdate
  extends Event
{
  private float yaw;
  private float pitch;
  public double y;
  private boolean ground;
  
  public EventPreUpdate(float yaw, float pitch, double y, boolean ground)
  {
    this.yaw = yaw;
    this.pitch = pitch;
    this.y = y;
    this.ground = ground;
  }
  
  public float getYaw() {
    return yaw;
  }
  
  public void setYaw(float yaw) {
    this.yaw = yaw;
  }
  
  public float getPitch() {
    return pitch;
  }

  public void setPitch(float pitch) {
    this.pitch = pitch;
  }
  
  public double getY() {
    return y;
  }
  
  public void setY(double y) {
    this.y = y;
  }
  
  public boolean isOnground() {
    return ground;
  }
  
  public void setOnground(boolean ground) {
    this.ground = ground;
  }
}
