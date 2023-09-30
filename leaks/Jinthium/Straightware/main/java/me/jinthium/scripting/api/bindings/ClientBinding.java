package me.jinthium.scripting.api.bindings;

import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.modules.combat.KillAura;
import me.jinthium.straight.impl.utils.misc.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentText;
import org.lwjglx.input.Keyboard;
import org.lwjglx.input.Mouse;

import java.awt.*;

public class ClientBinding {

    public String getClientVersion() {
        return Client.INSTANCE.getClientInfo().getVersion();
    }


    public TimerUtil createTimer() {
        return new TimerUtil();
    }

    public void printClientMsg(String text) {
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(text));
    }

    public float fps() {
        return Minecraft.getDebugFPS();
    }

    public EntityLivingBase getAuraTarget() {
        KillAura killAura = Client.INSTANCE.getModuleManager().getModule(KillAura.class);
        return killAura.target;
    }

    public Color getClientColors() {
        return Client.INSTANCE.getClientColor();
    }

    public boolean leftMouseButtonDown() {
        return Mouse.isButtonDown(0);
    }

    public boolean rightMouseButtonDown() {
        return Mouse.isButtonDown(1);
    }

    public boolean isKeyDown(int key) {
        return Keyboard.isCreated() && Keyboard.isKeyDown(key);
    }


    public Module getModule(String moduleName) {
        Module module = Client.INSTANCE.getModuleManager().getModuleByName(moduleName);
        if (module != null) {
            return module;
        } else {
            throw new NullPointerException("Module " + moduleName + " does not exist.");
        }
    }

}
