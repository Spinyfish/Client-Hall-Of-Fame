package me.kansio.client.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import me.kansio.client.event.impl.BlockCollisionEvent;
import me.kansio.client.event.impl.MoveEvent;
import me.kansio.client.event.impl.PacketEvent;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.gui.notification.Notification;
import me.kansio.client.gui.notification.NotificationManager;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.modules.impl.movement.flight.FlightMode;
import me.kansio.client.value.value.BooleanValue;
import me.kansio.client.value.value.ModeValue;
import me.kansio.client.value.value.NumberValue;
import me.kansio.client.utils.java.ReflectUtils;
import me.kansio.client.utils.math.Stopwatch;

import me.kansio.client.utils.player.TimerUtil;
import net.minecraft.potion.Potion;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@ModuleData(
        name = "Flight",
        category = ModuleCategory.MOVEMENT,
        description = "Allows you to fly"
)
public class Flight extends Module {

    private final List<? extends FlightMode> modes = ReflectUtils.getReflects(this.getClass().getPackage().getName() + ".flight", FlightMode.class).stream()
            .map(aClass -> {
                try {
                    return aClass.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            })
            .sorted(Comparator.comparing(flyMode -> flyMode != null ? flyMode.getName() : null))
            .collect(Collectors.toList());

    private final ModeValue mode = new ModeValue("Mode", this, modes.stream().map(FlightMode::getName).collect(Collectors.toList()).toArray(new String[]{}));
    private FlightMode currentMode = modes.stream().anyMatch(flyMode -> flyMode.getName().equalsIgnoreCase(mode.getValue())) ? modes.stream().filter(flyMode -> flyMode.getName().equalsIgnoreCase(mode.getValue())).findAny().get() : null;
    private NumberValue<Double> speed = new NumberValue<>("Speed", this, 1d, 0d, 10d, 0.1);
    private BooleanValue antikick = new BooleanValue("AntiKick", this, true, mode, "BridgerLand (TP)");

    private BooleanValue boost = new BooleanValue("Boost", this, true, mode, "Funcraft");
    private BooleanValue extraBoost = new BooleanValue("Extra Boost", this, true, mode, "Funcraft");
    private BooleanValue glide = new BooleanValue("Glide", this, true, mode, "Funcraft");
    private ModeValue boostMode = new ModeValue("Boost Mode", this, mode, new String[]{"Funcraft"}, "Normal", "Damage", "WOWOMG");
    private NumberValue<Double> timer = new NumberValue<>("Timer", this, 1d, 1d, 5d, 0.1, mode, "Mush");
    private BooleanValue blink = new BooleanValue("Blink", this, true, mode, "Mush");
    private BooleanValue viewbob = new BooleanValue("View Bobbing", this, true);

    private Stopwatch stopwatch = new Stopwatch();

    public float ticks = 0;
    public float prevFOV;

    public void onEnable() {
        currentMode = modes.stream().anyMatch(flyMode -> flyMode.getName().equalsIgnoreCase(mode.getValue())) ? modes.stream().filter(flyMode -> flyMode.getName().equalsIgnoreCase(mode.getValue())).findAny().get() : null;
        currentMode.onEnable();
    }

    public void onDisable() {
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionY = 0;
        mc.thePlayer.motionZ = 0;
        TimerUtil.Reset();
        currentMode.onDisable();
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (viewbob.getValue() && mc.thePlayer.isMoving()) {
            mc.thePlayer.cameraYaw = 0.05f;
        } else {
            mc.thePlayer.cameraYaw = 0;
        }
        currentMode.onUpdate(event);
        if (mc.thePlayer.ticksExisted < 5) {
            if (isToggled()) {
                NotificationManager.getNotificationManager().show(new Notification(Notification.NotificationType.INFO, "World Change!", "Flight disabled", 1));
                toggle();
            }
        }
    }

    @Subscribe
    public void onMove(MoveEvent event) {
        currentMode.onMove(event);
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        currentMode.onPacket(event);
    }

    @Subscribe
    public void onCollide(BlockCollisionEvent event) {
        currentMode.onCollide(event);
    }

    private double getBaseMoveSpeed() {
        double n = 0.2873;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            n *= 1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }
        return n;
    }

    @Override
    public String getSuffix() {
        return " " + mode.getValueAsString();
    }
}
