package dev.africa.pandaware.manager.script;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.event.interfaces.EventListenable;
import dev.africa.pandaware.api.interfaces.Initializable;
import dev.africa.pandaware.impl.container.Container;
import dev.africa.pandaware.impl.event.game.*;
import dev.africa.pandaware.impl.event.player.*;
import dev.africa.pandaware.impl.event.render.NameTagsEvent;
import dev.africa.pandaware.impl.event.render.RenderEvent;
import dev.africa.pandaware.impl.script.Script;
import dev.africa.pandaware.impl.ui.clickgui.ClickGUI;
import dev.africa.pandaware.utils.client.Printer;
import jdk.nashorn.api.scripting.JSObject;
import lombok.Getter;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Stream;

@Getter
public class ScriptManager extends Container<Script> implements Initializable, EventListenable {
    private File scriptFolder;

    private boolean initialized;

    @Override
    public void init() {
        this.scriptFolder = new File(Client.getInstance().getFileManager().getRootFolder(), "scripts");

        if (this.scriptFolder.mkdir()) {
            Printer.consoleInfo("Creating scripts folder...");
            return;
        }

        if (this.scriptFolder.listFiles().length <= 0) {
            Client.getInstance().getEventDispatcher().unsubscribe(this);

            return;
        }

        if (!this.initialized) {
            Client.getInstance().getEventDispatcher().subscribe(this);
        }

        Stream<File> scripts = Arrays.stream(this.scriptFolder.listFiles())
                .filter(file -> file.getName().endsWith(".js"));

        scripts.forEach(file -> {
            try {
                Printer.consoleInfo("Loading script: " + file.getName() + "...");

                new Script().init(file);
            } catch (Exception e) {
                e.printStackTrace();

                Printer.consoleError("Failed to evaluate script: " + file.getAbsolutePath());
            }
        });

        this.initialized = !Client.getInstance().isKillSwitch();
    }

    public void reloadScripts() {
        this.getItems().forEach(Script::destroy);

        this.getItems().clear();
        this.init();

        Client.getInstance().setClickGUI(new ClickGUI());
        Client.getInstance().getClickGUI().init();
    }

    public void invokeEvent(String name, Event event) {
        this.getItems().stream()
                .filter(script -> script.getModule() != null && script.getModule().getData().isEnabled())
                .forEach(script -> {
                    JSObject jsEvent = script.getEventManager().getMap().get(name);

                    if (jsEvent == null) return;

                    jsEvent.call(jsEvent, event);
                });
    }

    @EventHandler
    EventCallback<UpdateEvent> onUpdate = event -> this.invokeEvent("update", event);

    @EventHandler
    EventCallback<MoveEvent> onMove = event -> this.invokeEvent("move", event);

    @EventHandler
    EventCallback<TickEvent> onTick = event -> this.invokeEvent("tick", event);

    @EventHandler
    EventCallback<KeyEvent> onKeyboard = event -> this.invokeEvent("keyboard", event);

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (event.getEventState() == Event.EventState.PRE) {
            this.invokeEvent("motionPre", event);
        } else {
            this.invokeEvent("motionPost", event);
        }
    };

    @EventHandler
    EventCallback<CollisionEvent> onCollision = event -> this.invokeEvent("collision", event);

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> this.invokeEvent("packet", event);

    @EventHandler
    EventCallback<MouseEvent> onMouse = event -> this.invokeEvent("mouse", event);

    @EventHandler
    EventCallback<AttackEvent> onAttack = event -> {
        if (event.getEventState() == Event.EventState.PRE) {
            this.invokeEvent("attackPre", event);
        } else {
            this.invokeEvent("attackPost", event);
        }
    };

    @EventHandler
    EventCallback<ChatEvent> onChat = event -> this.invokeEvent("chat", event);

    @EventHandler
    EventCallback<NameTagsEvent> onNameTags = event -> this.invokeEvent("nameTags", event);

    @EventHandler
    EventCallback<GameLoopEvent> onGameLoop = event -> this.invokeEvent("gameLoop", event);

    @EventHandler
    EventCallback<JumpEvent> onJump = event -> this.invokeEvent("jump", event);

    @EventHandler
    EventCallback<SafeWalkEvent> onSafeWalk = event -> this.invokeEvent("safeWalk", event);

    @EventHandler
    EventCallback<ServerJoinEvent> onServerJoin = event -> this.invokeEvent("serverJoin", event);

    @EventHandler
    EventCallback<StepEvent> onStep = event -> {
        if (event.getState() == Event.EventState.PRE) {
            this.invokeEvent("stepPre", event);
        } else {
            this.invokeEvent("stepPost", event);
        }
    };

    @EventHandler
    EventCallback<RenderEvent> onRender = event -> {
        switch (event.getType()) {
            case RENDER_2D:
                this.invokeEvent("render2D", event);
                break;

            case RENDER_3D:
                this.invokeEvent("render3D", event);
                break;

            case RENDER_SCREEN:
                this.invokeEvent("renderScreen", event);
                break;
        }
    };
}
