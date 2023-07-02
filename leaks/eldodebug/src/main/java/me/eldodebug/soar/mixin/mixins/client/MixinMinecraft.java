package me.eldodebug.soar.mixin.mixins.client;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.gui.GuiSplashScreen;
import me.eldodebug.soar.hooks.MinecraftHook;
import me.eldodebug.soar.management.events.impl.EventKey;
import me.eldodebug.soar.management.events.impl.EventRenderTick;
import me.eldodebug.soar.management.events.impl.EventScrollMouse;
import me.eldodebug.soar.management.events.impl.EventTick;
import me.eldodebug.soar.management.mods.impl.FPSLimiterMod;
import me.eldodebug.soar.management.mods.impl.FPSSpooferMod;
import me.eldodebug.soar.management.mods.impl.HitDelayFixMod;
import me.eldodebug.soar.utils.interfaces.IMixinMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.stream.IStream;
import net.minecraft.entity.Entity;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements IMixinMinecraft{
	
	@Shadow
    public WorldClient theWorld;
	
	@Shadow
	public abstract void displayGuiScreen(GuiScreen guiScreenIn);
	
	@Shadow
    public GuiScreen currentScreen;
	
	@Shadow
    public GameSettings gameSettings;
	
    @Shadow
    public int displayWidth;
    
    @Shadow
    public int displayHeight;
    
    @Shadow
    private Timer timer = new Timer(20.0F);
    
    @Shadow
    @Mutable
    public Session session;

    @Shadow
    @Mutable
    public DefaultResourcePack mcDefaultResourcePack;
    
    @Shadow
    private int leftClickCounter;
    
    @Shadow
    private static int debugFPS;
    
    @Shadow 
    private boolean fullscreen;
    
    @Shadow 
    public EntityRenderer entityRenderer;
    
    @Shadow
    public GuiIngame ingameGUI;
    
	@Override
	@Accessor
	public abstract boolean isRunning();
	
	@Shadow
	private Entity renderViewEntity;
	
    @Inject(method = "startGame", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;ingameGUI:Lnet/minecraft/client/gui/GuiIngame;", shift = At.Shift.AFTER))
    private void startGame(CallbackInfo ci) {
    	Soar.instance.startClient();
    }
    
    @Inject(method = "run", at = @At("HEAD"))
    private void init(CallbackInfo callbackInfo) {
    	
        if (displayWidth < 1100) {
        	displayWidth = 1100;
        }
            
        if (displayHeight < 630) {
        	displayHeight = 630;
        }
    }
    
    @Inject(method = "shutdown", at = @At("HEAD"))
    private void onShutdown(CallbackInfo ci) {
    	Soar.instance.stopClient();
    }
    
    @Inject(method = "runTick", at = @At("TAIL"))
    private void onTick(final CallbackInfo ci) {
    	EventTick event = new EventTick();
    	event.call();
    }
    
    @Inject(method = "displayGuiScreen", at = @At("RETURN"), cancellable = true)
    public void displayGuiScreenInject(GuiScreen guiScreenIn, CallbackInfo ci) {
    	if(guiScreenIn instanceof GuiMainMenu) {
			displayGuiScreen(Soar.instance.guiManager.getGuiMainMenu());
    	}
    }
    
    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;dispatchKeypresses()V", shift = At.Shift.AFTER))
    private void onKey(CallbackInfo ci) {
        if (Keyboard.getEventKeyState() && Minecraft.getMinecraft().currentScreen == null) {
        	EventKey event = new EventKey(Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey());
        	event.call();
        }
    }
    
    @Overwrite
    public void drawSplashScreen(TextureManager textureManagerInstance) {
		GuiSplashScreen.onRender(textureManagerInstance);
    }
	
	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/SkinManager;<init>(Lnet/minecraft/client/renderer/texture/TextureManager;Ljava/io/File;Lcom/mojang/authlib/minecraft/MinecraftSessionService;)V"))
	public void progress1(CallbackInfo ci) {
    	GuiSplashScreen.setProgress(1);
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/storage/AnvilSaveConverter;<init>(Ljava/io/File;)V"))
	public void progress2(CallbackInfo ci) {
    	GuiSplashScreen.setProgress(2);
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/audio/SoundHandler;<init>(Lnet/minecraft/client/resources/IResourceManager;Lnet/minecraft/client/settings/GameSettings;)V"))
	public void progress3(CallbackInfo ci) {
    	GuiSplashScreen.setProgress(3);
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/audio/MusicTicker;<init>(Lnet/minecraft/client/Minecraft;)V"))
	public void progress4(CallbackInfo ci) {
    	GuiSplashScreen.setProgress(4);
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;<init>(Lnet/minecraft/client/settings/GameSettings;Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/renderer/texture/TextureManager;Z)V"))
	public void progress5(CallbackInfo ci) {
    	GuiSplashScreen.setProgress(5);
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MouseHelper;<init>()V"))
	public void progress6(CallbackInfo ci) {
    	GuiSplashScreen.setProgress(6);
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureMap;<init>(Ljava/lang/String;)V"))
	public void progress7(CallbackInfo ci) {
    	GuiSplashScreen.setProgress(7);
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/ModelManager;<init>(Lnet/minecraft/client/renderer/texture/TextureMap;)V"))
	public void progress8(CallbackInfo ci) {
    	GuiSplashScreen.setProgress(8);
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderItem;<init>(Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/client/resources/model/ModelManager;)V"))
	public void progress9(CallbackInfo ci) {
    	GuiSplashScreen.setProgress(9);
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderManager;<init>(Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/client/renderer/entity/RenderItem;)V"))
	public void progress10(CallbackInfo ci) {
    	GuiSplashScreen.setProgress(10);
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;<init>(Lnet/minecraft/client/Minecraft;)V"))
	public void progress11(CallbackInfo ci) {
    	GuiSplashScreen.setProgress(11);
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;<init>(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/resources/IResourceManager;)V"))
	public void progress12(CallbackInfo ci) {
    	GuiSplashScreen.setProgress(12);
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BlockRendererDispatcher;<init>(Lnet/minecraft/client/renderer/BlockModelShapes;Lnet/minecraft/client/settings/GameSettings;)V"))
	public void progress13(CallbackInfo ci) {
    	GuiSplashScreen.setProgress(13);
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;<init>(Lnet/minecraft/client/Minecraft;)V"))
	public void progress14(CallbackInfo ci) {
    	GuiSplashScreen.setProgress(14);
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/achievement/GuiAchievement;<init>(Lnet/minecraft/client/Minecraft;)V"))
	public void progress15(CallbackInfo ci) {
    	GuiSplashScreen.setProgress(15);
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/EffectRenderer;<init>(Lnet/minecraft/world/World;Lnet/minecraft/client/renderer/texture/TextureManager;)V"))
	public void progress16(CallbackInfo ci) {
    	GuiSplashScreen.setProgress(16);
	}
	
    @Inject(method = "startGame", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;ingameGUI:Lnet/minecraft/client/gui/GuiIngame;", shift = At.Shift.AFTER))
	public void replaceGuiIngame(CallbackInfo ci) {
    	GuiSplashScreen.setProgress(17);
	}
	
	@Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventDWheel()I"))
	public int onScroll() {
		int dWheel = Mouse.getEventDWheel();

		EventScrollMouse event = new EventScrollMouse(dWheel);
		event.call();
		
		if(dWheel != 0) {
			if(event.isCancelled()) {
				dWheel = 0;
			}
		}

		return dWheel;
	}
    
    @Inject(method = "setIngameFocus", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MouseHelper;grabMouseCursor()V"))
    private void setIngameFocus(CallbackInfo ci) {
		for(KeyBinding keyBinding : gameSettings.keyBindings) {
			try {
				KeyBinding.setKeyBindState(keyBinding.getKeyCode(), keyBinding.getKeyCode() < 256 && Keyboard.isKeyDown(keyBinding.getKeyCode()));
			}
			catch (IndexOutOfBoundsException error) {}
		}
    }
    
    @Overwrite
    public int getLimitFramerate(){
    	if(Soar.instance.modManager.getModByClass(FPSLimiterMod.class).isToggled()) {
            return this.theWorld == null && this.currentScreen != null ? 60 : (this.currentScreen instanceof Gui ? 60 : this.gameSettings.limitFramerate);
    	}
    	
        return this.theWorld == null && this.currentScreen != null ? 250 : this.gameSettings.limitFramerate;
    }
    
	@Redirect(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Ljava/lang/System;gc()V"))
	public void removeSystemGC() {}
	
	@Redirect(method = "createDisplay", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setTitle(Ljava/lang/String;)V"))
	public void overrideDisplayTitle(String title) {
		Display.setTitle(Soar.instance.getName() + " Client v" + Soar.instance.getVersion() + " for " + title);
	}

	
    @Inject(method = "clickMouse", at = @At("HEAD"))
    private void fixDelay(final CallbackInfo ci) {
    	if(Soar.instance.modManager.getModByClass(HitDelayFixMod.class).isToggled()) {
            leftClickCounter = 0;
    	}
    }
    
	@Overwrite
	public static int getDebugFPS() {
		if(Soar.instance.modManager.getModByClass(FPSSpooferMod.class).isToggled()) {
			return Soar.instance.settingsManager.getSettingByClass(FPSSpooferMod.class, "Multiplication").getValInt() * debugFPS;
		}
		return debugFPS;
	}
	
    @Inject(method = "setInitialDisplayMode", at = @At(value = "HEAD"), cancellable = true)
    private void setInitialDisplayMode(CallbackInfo ci) throws LWJGLException {
    	MinecraftHook.displayFix(ci, fullscreen, displayWidth, displayHeight);
    }

    @Inject(method = "toggleFullscreen", at = @At(value = "INVOKE", remap = false, target = "Lorg/lwjgl/opengl/Display;setVSyncEnabled(Z)V", shift = At.Shift.AFTER))
    private void toggleFullscreen(CallbackInfo ci) throws LWJGLException {
        MinecraftHook.fullScreenFix(fullscreen, displayWidth, displayHeight);
    }
    
	@Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/achievement/GuiAchievement;updateAchievementWindow()V", shift = At.Shift.AFTER))
	public void renderTick(CallbackInfo ci) {
		EventRenderTick event = new EventRenderTick();
		event.call();
	}
    
    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
    private void clearLoadedMaps(WorldClient worldClientIn, String loadingMessage, CallbackInfo ci) {
        if (worldClientIn != this.theWorld) {
            this.entityRenderer.getMapItemRenderer().clearLoadedMaps();
        }
    }
    
    @Redirect(method = "dispatchKeypresses", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;getEventCharacter()C", remap = false))
    private char resolveForeignKeyboards() {
        return (char) (Keyboard.getEventCharacter() + 256);
    }
    
    @Redirect(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/stream/IStream;func_152935_j()V"))
    public void removeTwitch(IStream instance) {}

    @Redirect(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/stream/IStream;func_152922_k()V"))
    public void removeTwitch2(IStream instance) {}
        
    @Override
    public Timer getTimer(){
    	return timer;
    }
    
    @Override
    public void setSession(Session session) {
    	this.session = session;
    }
    
    @Override
    public DefaultResourcePack getMcDefaultResourcePack() {
    	return this.mcDefaultResourcePack;
    }
    
    @Override
    public Entity getRenderViewEntity() {
    	return renderViewEntity;
    }
}
