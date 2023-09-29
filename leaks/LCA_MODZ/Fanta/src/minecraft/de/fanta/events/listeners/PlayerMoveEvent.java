package de.fanta.events.listeners;




import de.fanta.events.Event;
import de.fanta.module.impl.movement.Speed;
import net.minecraft.client.Minecraft;

public class PlayerMoveEvent extends Event {
    private boolean canceled;
    public double x;
    public double y;
    public double z;
    public static PlayerMoveEvent INSTANCE;
    public PlayerMoveEvent(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        INSTANCE = this;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public void setX(double x) {
        this.x = x;
    }

    public  void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }
}
