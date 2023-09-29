package me.jinthium.straight.impl.manager;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import io.mxngo.echo.core.EventListener;
import me.jinthium.straight.api.client.keyauth.util.HWID;
import me.jinthium.straight.api.config.LocalConfig;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.game.RunGameLoopEvent;
import me.jinthium.straight.impl.event.game.TickEvent;
import me.jinthium.straight.impl.modules.combat.*;
import me.jinthium.straight.impl.modules.exploit.Disabler;
import me.jinthium.straight.impl.modules.exploit.PingSpoof;
import me.jinthium.straight.impl.modules.exploit.Spoofer;
import me.jinthium.straight.impl.modules.ghost.*;
import me.jinthium.straight.impl.modules.misc.EntityCulling;
import me.jinthium.straight.impl.modules.misc.ModuleSounds;
import me.jinthium.straight.impl.modules.misc.StreamerMode;
import me.jinthium.straight.impl.modules.movement.*;
import me.jinthium.straight.impl.modules.player.*;
import me.jinthium.straight.impl.modules.visual.*;
import me.jinthium.straight.impl.modules.visual.packetmonitor.NetworkGraph;
import me.jinthium.straight.impl.ui.UserLogin;
import me.jinthium.straight.impl.utils.file.FileUtils;
import net.minecraft.client.Minecraft;
import org.lwjglx.Sys;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class ModuleManager implements EventListener {
    private final LinkedHashMap<Object, Module> modules = new LinkedHashMap<>();

    public static boolean reloadModules;
    public long lastCheck = System.currentTimeMillis();

    public void registerModules() {
        addModules(
                new Sprint(),
                new ClickGui(),
                new Hud(),
                new ModuleSounds(),
                new NoSlowDown(),
                new ESP2D(),
                new SpeedMine(),
                new AutoTool(),
                new Speed(),
                new PostProcessing(),
                new Longjump(),
                new Breaker(),
                new ItemTransformer(),
                new AimAssist(),
                new AutoClicker(),
                new Backtrack(),
                new ClickAssist(),
                new Eagle(),
                new FastPlace(),
                new Hitbox(),
                new KeepSprint(),
                new NoClickDelay(),
                new Reach(),
                new SafeWalk(),
                new StreamerMode(),
                new WTap(),
                new Velocity(),
                new EntityCulling(),
                new IRC(),
                new Flight(),
                new TargetStrafe(),
                new Disabler(),
                new KillAura(),
                new ChestStealer(),
                new Glint(),
                new BreadCrumbs(),
                new InvManager(),
                new InventoryMove(),
                new TargetHud(),
                new GlowESP(),
                new AutoArmor(),
                new PingSpoof(),
                new AutoPot(),
                new EntityEffects(),
                new Chams(),
                new JumpCircle(),
                new CustomModel(),
                new NetworkGraph(),
                new AntiVoid(),
                new Test(),
                new NoFall(),
                new SpotifyHud(),
                new Scaffold(),
                new Criticals(),
                new Regen(),
                new Spoofer(),
                new SessionInfo()
        );
        Client.INSTANCE.getPubSubEventBus().subscribe(this);
    }

    @Callback
    private final EventCallback<RunGameLoopEvent> runGameLoopEventEventCallback = event -> {
        modules.values().stream()
                .filter(module -> module.getModeSetting() != null
                        && module.getModeSetting().getCurrentMode() != null
                        && module.getCurrentMode() != null)
                .forEach(Module::updateModes);

        if(UserLogin.triedLogin && System.currentTimeMillis() - lastCheck >= 450000){
            if(!Objects.equals(HWID.getHWID(), Client.INSTANCE.getKeyAuth().postCheck)){
                try{
                    HttpResponse<String> funny = Unirest.get("https://jinthium.com/post-check/" + Client.INSTANCE.getUser().getUsername()).asString();
                }catch (UnirestException ex){
                    ex.printStackTrace();
                }
                Minecraft.getMinecraft().shutdownMinecraftApplet();
            }
            lastCheck = System.currentTimeMillis();
        }
    };

    public void addModules(Module... modules) {
        for(Module module: modules){
            this.modules.put(module.getClass(), module);
        }
    }

    public HashMap<Object, Module> getModuleMap() {
        return modules;
    }

    public ArrayList<Module> getModules() {
        return new ArrayList<>(modules.values());
    }


    public <T extends Module> T getModule(Class<? extends Module> clazz) {
        return (T)modules.get(clazz);
    }

    public Module getModuleByName(String name) {
        return this.modules.values().stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<Module> getModulesInCategory(Module.Category category) {
        return this.modules.values().stream().filter(m -> m.getCategory() == category).collect(Collectors.toList());
    }
}
