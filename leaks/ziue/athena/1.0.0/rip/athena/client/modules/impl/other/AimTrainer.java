package rip.athena.client.modules.impl.other;

import rip.athena.client.config.*;
import java.awt.*;
import net.minecraft.client.entity.*;
import net.minecraft.util.*;
import rip.athena.client.gui.hud.*;
import rip.athena.client.modules.*;
import rip.athena.client.*;
import net.minecraft.client.gui.*;
import rip.athena.client.utils.font.*;
import rip.athena.client.utils.render.*;
import rip.athena.client.events.types.entity.*;
import rip.athena.client.events.*;
import rip.athena.client.events.types.input.*;
import rip.athena.client.utils.*;
import com.mojang.authlib.*;
import net.minecraft.entity.*;
import rip.athena.client.events.types.render.*;
import java.util.*;

public class AimTrainer extends Module
{
    @ConfigValue.List(name = "Display Mode", values = { "Circle", "Modern", "Fade", "Old" }, description = "Chose display of background")
    private String backgroundMode;
    @ConfigValue.Boolean(name = "Move")
    private boolean move;
    @ConfigValue.Float(name = "Speed", min = 1.0f, max = 10.0f)
    private float speed;
    @ConfigValue.Float(name = "Distance", min = 1.0f, max = 10.0f)
    private float distance;
    @ConfigValue.Boolean(name = "Background")
    private boolean backGround;
    @ConfigValue.Color(name = "Background Color")
    private Color background;
    @ConfigValue.Color(name = "Color")
    private Color color;
    @ConfigValue.Boolean(name = "Custom Font")
    private boolean customFont;
    @ConfigValue.Boolean(name = "Static Chroma")
    private boolean isUsingStaticChroma;
    @ConfigValue.Boolean(name = "Wave Chroma")
    private boolean isUsingWaveChroma;
    public List<EntityOtherPlayerMP> bots;
    public EntityOtherPlayerMP bot;
    public int hitCount;
    public int failedHit;
    public float accuracy;
    public Vec3 position;
    private HUDElement hud;
    private int width;
    private int height;
    
    public AimTrainer() {
        super("Aim Trainer", Category.RENDER, "Athena/gui/mods/time.png");
        this.backgroundMode = "Circle";
        this.move = true;
        this.speed = 4.0f;
        this.distance = 3.0f;
        this.backGround = true;
        this.background = new Color(0, 0, 0, 150);
        this.color = Color.WHITE;
        this.customFont = false;
        this.isUsingStaticChroma = false;
        this.isUsingWaveChroma = false;
        this.bots = new ArrayList<EntityOtherPlayerMP>();
        this.hitCount = 0;
        this.failedHit = 0;
        this.width = 56;
        this.height = 18;
        (this.hud = new HUDElement("time", this.width, this.height) {
            @Override
            public void onRender() {
                AimTrainer.this.render();
            }
        }).setX(1);
        this.hud.setY(190);
        this.addHUD(this.hud);
    }
    
    public void render() {
        final int width = this.hud.getWidth();
        final int height = this.hud.getHeight();
        if (this.backGround) {
            if (this.backgroundMode.equalsIgnoreCase("Modern")) {
                if (Athena.INSTANCE.getThemeManager().getTheme().isTriColor()) {
                    RoundedUtils.drawGradientRound((float)this.hud.getX(), (float)this.hud.getY(), (float)this.hud.getWidth(), (float)this.hud.getHeight(), 6.0f, Athena.INSTANCE.getThemeManager().getTheme().getFirstColor(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor(), Athena.INSTANCE.getThemeManager().getTheme().getThirdColor(), Athena.INSTANCE.getThemeManager().getTheme().getFirstColor());
                }
                else {
                    RoundedUtils.drawGradientRound((float)this.hud.getX(), (float)this.hud.getY(), (float)this.hud.getWidth(), (float)this.hud.getHeight(), 6.0f, Athena.INSTANCE.getThemeManager().getTheme().getFirstColor(), Athena.INSTANCE.getThemeManager().getTheme().getFirstColor(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor());
                }
            }
            else if (this.backgroundMode.equalsIgnoreCase("Circle")) {
                RoundedUtils.drawGradientRound((float)this.hud.getX(), (float)this.hud.getY(), (float)this.hud.getWidth(), (float)this.hud.getHeight(), 6.0f, ColorUtil.getClientColor(0, 255), ColorUtil.getClientColor(90, 255), ColorUtil.getClientColor(180, 255), ColorUtil.getClientColor(270, 255));
            }
            else if (this.backgroundMode.equalsIgnoreCase("Fade")) {
                RoundedUtils.drawRoundedRect((float)this.hud.getX(), (float)this.hud.getY(), (float)(this.hud.getX() + width), (float)(this.hud.getY() + height), 8.0f, Athena.INSTANCE.getThemeManager().getTheme().getAccentColor().getRGB());
            }
            else {
                Gui.drawRect(this.hud.getX(), this.hud.getY(), this.hud.getX() + width, this.hud.getY() + height, this.background.getRGB());
            }
        }
        final float posY = (float)(this.hud.getY() + 2);
        final float posX = (float)(this.hud.getX() + 9);
        if (this.customFont) {
            this.hud.setWidth(FontManager.getProductSansRegular(25).width(this.accuracy + "%" + 20));
            this.hud.setHeight((int)FontManager.getProductSansRegular(25).height());
            if (this.isUsingStaticChroma) {
                DrawUtils.drawCustomFontChromaString(FontManager.getProductSansRegular(25), this.accuracy + "%", (int)posX, (int)posY, true, true);
            }
            else if (this.isUsingWaveChroma) {
                DrawUtils.drawCustomFontChromaString(FontManager.getProductSansRegular(25), this.accuracy + "%", (int)posX, (int)posY, false, true);
            }
            else {
                FontManager.getProductSansRegular(25).drawString(this.accuracy + "%", (int)posX, (int)posY, this.color.getRGB());
            }
        }
        else {
            this.hud.setWidth(AimTrainer.mc.fontRendererObj.getStringWidth(this.accuracy + "%") + 16);
            this.hud.setHeight(AimTrainer.mc.fontRendererObj.FONT_HEIGHT + 9);
            if (this.isUsingStaticChroma) {
                DrawUtils.drawChromaString(this.accuracy + "%", posX, posY + 3.0f, true, true);
            }
            else if (this.isUsingWaveChroma) {
                DrawUtils.drawChromaString(this.accuracy + "%", posX, posY + 3.0f, false, true);
            }
            else {
                AimTrainer.mc.fontRendererObj.drawStringWithShadow(this.accuracy + "%", posX, posY + 3.0f, this.color.getRGB());
            }
        }
    }
    
    @SubscribeEvent
    public void onAttack(final AttackEntityEvent event) {
        if (event.getTarget() == this.getBot("ziue")) {
            ++this.hitCount;
            this.delBot("ziue");
            this.createBotRandomPosition("ziue");
            Athena.INSTANCE.getLog().info("you have hit the bot");
            event.setCancelled(true);
        }
        this.updateAccuracy();
    }
    
    @SubscribeEvent
    public void onMouseDown(final MouseDownEvent event) {
        if (event.getButton() == 0 && AimTrainer.mc.objectMouseOver != null && AimTrainer.mc.objectMouseOver.entityHit == null) {
            ++this.failedHit;
            Athena.INSTANCE.getLog().info("YOU MISSED A HIT: " + this.failedHit);
            this.updateAccuracy();
        }
    }
    
    private void updateAccuracy() {
        if (this.failedHit > 0) {
            this.accuracy = Float.parseFloat(NumberUtils.getStringValueOfFloat(this.hitCount / (float)this.failedHit * 100.0f, NumberUtils.Format.Hundredths));
        }
        else {
            this.accuracy = 100.0f;
        }
        this.accuracy = Math.min(this.accuracy, 100.0f);
        Athena.INSTANCE.getLog().info("Accuracy: " + this.accuracy + "%");
    }
    
    public void createBot(final String username) {
        if (!this.isBot(username) && AimTrainer.mc.thePlayer != null) {
            final EntityOtherPlayerMP bot = new EntityOtherPlayerMP(AimTrainer.mc.thePlayer.getEntityWorld(), new GameProfile(UUID.randomUUID(), username));
            AimTrainer.mc.theWorld.addEntityToWorld(bot.getEntityId(), bot);
            bot.setPositionAndRotation(AimTrainer.mc.thePlayer.posX, AimTrainer.mc.thePlayer.posY, AimTrainer.mc.thePlayer.posZ, AimTrainer.mc.thePlayer.rotationYaw, AimTrainer.mc.thePlayer.rotationPitch);
            this.bots.add(bot);
        }
    }
    
    public void createBotRandomPosition(final String username) {
        if (!this.isBot(username) && AimTrainer.mc.thePlayer != null) {
            final double x = AimTrainer.mc.thePlayer.posX;
            final double y = AimTrainer.mc.thePlayer.posY;
            final double z = AimTrainer.mc.thePlayer.posZ;
            final double randomX = randomDoubleNumber(false, x - 3.0, x + 3.0);
            final double randomY = randomDoubleNumber(false, y, y + 2.0);
            final double randomZ = randomDoubleNumber(false, z - 3.0, z + 3.0);
            final EntityOtherPlayerMP bot = new EntityOtherPlayerMP(AimTrainer.mc.thePlayer.getEntityWorld(), new GameProfile(UUID.randomUUID(), username));
            AimTrainer.mc.theWorld.addEntityToWorld(bot.getEntityId(), bot);
            bot.setPositionAndRotation(randomX, randomY, randomZ, AimTrainer.mc.thePlayer.rotationYaw, AimTrainer.mc.thePlayer.rotationPitch);
            this.position = new Vec3(bot.posX, bot.posY, bot.posZ);
            this.bots.add(bot);
        }
    }
    
    @SubscribeEvent
    public void onTick(final RenderEvent event) {
        if (event.getRenderType() != RenderType.INGAME_OVERLAY) {
            return;
        }
        this.bot = this.getBot("ziue");
        if (this.bot != null && this.move) {
            this.bot.setPositionAndRotation2(this.position.xCoord - Math.sin(this.bot.ticksExisted * this.speed / 10.0f) * this.distance, this.position.yCoord, this.position.zCoord + Math.cos(this.bot.ticksExisted * this.speed / 10.0f) * this.distance, this.bot.rotationYaw, this.bot.rotationPitch, 3, true);
        }
    }
    
    public void delBot(final String username) {
        if (this.isBot(username)) {
            AimTrainer.mc.theWorld.removeEntityFromWorld(this.getBot(username).getEntityId());
            this.bots.remove(this.getBot(username));
        }
    }
    
    public void botList() {
        if (this.bots.size() != 0) {
            int numberList = 1;
            for (final EntityOtherPlayerMP b : this.bots) {
                ++numberList;
            }
        }
    }
    
    public static double randomDoubleNumber(final boolean round, final double min, final double max) {
        return round ? ((double)Math.round(Math.random() * (max - min) + min)) : (Math.random() * (max - min) + min);
    }
    
    public EntityOtherPlayerMP getBot(final String name) {
        for (final EntityOtherPlayerMP bot : this.bots) {
            if (bot.getName().equalsIgnoreCase(name)) {
                return bot;
            }
        }
        return null;
    }
    
    public boolean isBot(final String name) {
        for (final EntityOtherPlayerMP bot : this.bots) {
            if (bot.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
    
    public void clearBots() {
        for (final EntityOtherPlayerMP bot : this.bots) {
            AimTrainer.mc.theWorld.removeEntityFromWorld(bot.getEntityId());
        }
    }
    
    @Override
    public void onEnable() {
        this.createBot("ziue");
        this.accuracy = 100.0f;
        super.onEnable();
    }
    
    @Override
    public void onDisable() {
        this.clearBots();
        this.bots.clear();
        this.accuracy = 0.0f;
        this.hitCount = 0;
        this.failedHit = 0;
        super.onDisable();
    }
}
