package dev.tenacity.module.impl.render;

import dev.tenacity.event.impl.game.world.TickEvent;
import dev.tenacity.event.impl.network.PacketReceiveEvent;
import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

import java.util.Random;

public class Ambience extends Module {

    private final NumberSetting time = new NumberSetting("Time", 12000, 24000, 0, 1000);
    private final ModeSetting weather = new ModeSetting("Weather", "Clear", "Rain", "Thunder", "Clear", "Snow", "Don't Change");
    public static boolean overrideSnow = false;

    public Ambience() {
        super("Ambience", Category.RENDER, "world time");
        this.addSettings(time, weather);
    }

    //A random value was used in the clear weather command so here's one to use
    private final int randomValue = (300 + (new Random()).nextInt(600)) * 20;

    String mode = "";
    public void onMotionEvent(MotionEvent event) {this.setSuffix((time.getValue().toString()));}



    @Override
    public void onTickEvent(TickEvent event) {
        if (mc.theWorld != null) {

            WorldInfo worldinfo = mc.theWorld.getWorldInfo();
            if (mc.isSingleplayer()) {
                World world = MinecraftServer.getServer().worldServers[0];
                worldinfo = world.getWorldInfo();
            }

            mc.theWorld.setWorldTime(time.getValue().longValue());

            switch (weather.getMode()) {
                case "Clear":
                    worldinfo.setCleanWeatherTime(randomValue);
                    worldinfo.setRainTime(0);
                    worldinfo.setThunderTime(0);
                    worldinfo.setRaining(false);
                    worldinfo.setThundering(false);

                    break;
                case "Rain":
                    //  worldinfo.setCleanWeatherTime(0);
                    worldinfo.setRainTime(Integer.MAX_VALUE);
                    worldinfo.setThunderTime(Integer.MAX_VALUE);
                    worldinfo.setRaining(true);
                    worldinfo.setThundering(false);


                    break;
                case "Thunder":
                    worldinfo.setCleanWeatherTime(0);
                    worldinfo.setRainTime(Integer.MAX_VALUE);
                    worldinfo.setThunderTime(Integer.MAX_VALUE);
                    worldinfo.setRaining(true);
                    worldinfo.setThundering(true);


                    break;
                case "Don't Change":
                    mode = "Don't Change";
                    break;
                case "Snow":
                    worldinfo.setCleanWeatherTime(0);
                    worldinfo.setRainTime(Integer.MAX_VALUE);
                    worldinfo.setThunderTime(Integer.MAX_VALUE);
                    worldinfo.setRaining(true);
                    worldinfo.setThundering(false);

                    break;
            }

            overrideSnow = weather.is("Snow");


        }
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent e) {
        if (e.getPacket() instanceof S03PacketTimeUpdate) {
            e.cancel();
        }
    }

    @Override
    public void onDisable() {
        overrideSnow = false;
        super.onDisable();
    }
}
