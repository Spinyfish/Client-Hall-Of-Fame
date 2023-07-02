package me.eldodebug.soar.utils.interfaces;

import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;

public interface IMixinMinecraft {
	Timer getTimer();
    void setSession(Session session);
    DefaultResourcePack getMcDefaultResourcePack();
    boolean isRunning();
    Entity getRenderViewEntity();
}
