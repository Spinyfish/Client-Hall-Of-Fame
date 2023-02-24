package me.kansio.client.modules.impl.visuals;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.event.impl.PacketEvent;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.value.value.BooleanValue;
import me.kansio.client.value.value.ModeValue;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

@ModuleData(
        name = "Death Effect",
        category = ModuleCategory.VISUALS,
        description = "Custom death effects"
)
public class DeathEffect extends Module {

    public ModeValue mode = new ModeValue("Effect Mode", this, "Lightning");
    public BooleanValue memesound = new BooleanValue("Meme Sounds", this, false);
    public ModeValue mememode = new ModeValue("Meme Mode", this, memesound, "OOF", "N Word1", "N Word2", "Win Error", "Yoda", "GTA");

    public DeathEffect() {
        super("DeathEffect", ModuleCategory.VISUALS);
        register(mode, memesound, mememode);
    }
    
    String[] deathMessage = {
            "foi jogado no void por",
            "morreu para",
            "was slain by",
            "slain by",
            "was killed by",
            "hit the ground too hard thanks to",
            "was shot by"
    };

    @Subscribe
    public void onChat(PacketEvent event) {
        if (!memesound.getValue()) return;
        if (event.getPacket() instanceof S02PacketChat) {

            S02PacketChat packet = event.getPacket();

            final String msg = packet.getChatComponent().getUnformattedText();
            final String[] split = msg.split(" ");
            for (String trigger : deathMessage) {
                if (msg.contains(trigger) && msg.contains(mc.thePlayer.getName()) && !split[0].equalsIgnoreCase(mc.thePlayer.getName())) {
                    effect(mc.thePlayer);
                    playSound(mememode.getValue());
                }
            }
        }
    }

    public void effect(EntityLivingBase target) {
        double x, y, z;
        x = target.posX;
        y = target.posY;
        z = target.posZ;
        World targetWorld = target.getEntityWorld();

        mc.theWorld.addWeatherEffect(new EntityLightningBolt(targetWorld, x, y, z));
    }

    public void playSound(String mode) {

        mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("deatheffect." + mode.toLowerCase().replace(" ", "")), 1.0f));

    }
    
}