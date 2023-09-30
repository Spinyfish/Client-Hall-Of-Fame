package me.jinthium.scripting.api;
import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.api.notification.NotificationType;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.game.TickEvent;
import me.jinthium.straight.impl.event.movement.PlayerMoveEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.movement.WorldEvent;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.event.render.Render2DEvent;
import me.jinthium.straight.impl.event.render.ShaderEvent;
import me.jinthium.straight.impl.event.world.SafeWalkEvent;
import me.jinthium.straight.impl.manager.NotificationManager;
import org.openjdk.nashorn.api.scripting.JSObject;

import java.io.File;
import java.util.HashMap;

public class ScriptModule extends Module {

    private HashMap<String, JSObject> eventMap;

    private final File file;

    private boolean reloadable = true;

    public boolean isReloadable() {
        return reloadable;
    }

    public void setReloadable(boolean reloadable) {
        this.reloadable = reloadable;
    }

    public File getFile() {
        return file;
    }

    public ScriptModule(String name, HashMap<String, JSObject> events, String author, File file) {
        super(name, Category.SCRIPTS);
        this.eventMap = events;
        this.file = file;
        setAuthor(author);
    }

    @Callback
    final EventCallback<Render2DEvent> render2DEventEventCallback = event -> {
        if (eventMap.containsKey("render")) {
            try {
                eventMap.get("render").call(null, event);
            } catch (Exception e) {
                //ChatUtil.scriptError(this, "in render2D event");
                e.printStackTrace();
                //ChatUtil.print(false, e.getMessage());
                eventMap.remove("render");
                Client.INSTANCE.getNotificationManager().post("Render2D event unloaded", "\"" + getName() + "\" Script", NotificationType.WARNING, 7);
            }
        }
    };

    @Callback
    final EventCallback<ShaderEvent> shaderEventEventCallback = event -> {
        if (eventMap.containsKey("shader")) {
            try {
                eventMap.get("shader").call(null, event);
            } catch (Exception e) {
                e.printStackTrace();
                eventMap.remove("shader");
                Client.INSTANCE.getNotificationManager().post("Shader event unloaded", "\"" + getName() + "\" Script", NotificationType.WARNING, 7);
            }
        }
    };

    @Callback
    final EventCallback<SafeWalkEvent> safeWalkEventEventCallback = event -> {
        if (eventMap.containsKey("safewalk")) {
            try {
                eventMap.get("safewalk").call(null, event);
            } catch (Exception e) {
                e.printStackTrace();
                eventMap.remove("safewalk");
                Client.INSTANCE.getNotificationManager().post("Safewalk event unloaded", "\"" + getName() + "\" Script", NotificationType.WARNING, 7);
            }
        }
    };


    @Callback
    final EventCallback<WorldEvent> worldEventEventCallback = event -> {
        if(event instanceof WorldEvent.Load) {
            if (eventMap.containsKey("worldLoad")) {
                try {
                    eventMap.get("worldLoad").call(null, event);
                } catch (Exception e) {
                    e.printStackTrace();
                    eventMap.remove("worldLoad");
                    Client.INSTANCE.getNotificationManager().post("WorldLoad event unloaded", "\"" + getName() + "\" Script", NotificationType.WARNING, 7);
                }
            }
        }
    };

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventEventCallback = event -> {
        setSuffix(getAuthor());
        if (eventMap.containsKey("motion")) {
            try {
                eventMap.get("motion").call(null, event);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
                Client.INSTANCE.getNotificationManager().post("Motion event unloaded", "\"" + getName() + "\" Script", NotificationType.WARNING, 7);
                eventMap.remove("motion");
            }
        }
    };

    @Callback
    final EventCallback<PlayerMoveEvent> playerMoveEventEventCallback = event -> {
        if (eventMap.containsKey("move")) {
            try {
                eventMap.get("move").call(null, event);
            } catch (Exception e) {
                e.printStackTrace();
                eventMap.remove("move");
                Client.INSTANCE.getNotificationManager().post("Move event unloaded", "\"" + getName() + "\" Script", NotificationType.WARNING, 7);
            }
        }
    };

    @Callback
    final EventCallback<TickEvent> tickEventEventCallback = event -> {
        if (eventMap.containsKey("tick")) {
            try {
                eventMap.get("tick").call(null, event);
            } catch (Exception e) {
                e.printStackTrace();
                eventMap.remove("tick");
                Client.INSTANCE.getNotificationManager().post("Tick event unloaded", "\"" + getName() + "\" Script", NotificationType.WARNING, 7);
            }
        }
    };

    @Callback
    final EventCallback<PacketEvent> packetEventEventCallback = event -> {
        if (eventMap.containsKey("packetSend") && event.getPacketState() == PacketEvent.PacketState.SENDING) {
            try {
                eventMap.get("packetSend").call(null, event);
            } catch (Exception e) {
                e.printStackTrace();
                eventMap.remove("packetSend");
                Client.INSTANCE.getNotificationManager().post("SendPacket event unloaded", "\"" + getName() + "\" Script", NotificationType.WARNING, 7);
            }
        }
        
        if (eventMap.containsKey("packetReceive") && event.getPacketState() == PacketEvent.PacketState.RECEIVING) {
            try {
                eventMap.get("packetReceive").call(null, event);
            } catch (Exception e) {
                e.printStackTrace();
                eventMap.remove("packetReceive");
                Client.INSTANCE.getNotificationManager().post("ReceivePacket event unloaded", "\"" + getName() + "\" Script", NotificationType.WARNING, 7);
            }
        }
    };

    @Override
    public void onEnable() {
        if (eventMap.containsKey("enable")) {
            try {
                eventMap.get("enable").call(null);
            } catch (Exception e) {
                e.printStackTrace();
                eventMap.remove("enable");
                Client.INSTANCE.getNotificationManager().post("Enable event unloaded", "\"" + getName() + "\" Script", NotificationType.WARNING, 7);
            }
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (eventMap.containsKey("disable")) {
            try {
                eventMap.get("disable").call(null);
            } catch (Exception e) {
                e.printStackTrace();
                eventMap.remove("disable");
                Client.INSTANCE.getNotificationManager().post("Disable event unloaded", "\"" + getName() + "\" Script", NotificationType.WARNING, 7);
            }
        }
        super.onDisable();
    }
}
