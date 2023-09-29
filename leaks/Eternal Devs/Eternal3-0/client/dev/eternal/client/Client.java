package dev.eternal.client;

import dev.eternal.EternalBackend;
import dev.eternal.client.command.CommandManager;
import dev.eternal.client.config.ConfigManager;
import dev.eternal.client.config.ConfigRepository;
import dev.eternal.client.event.EventBus;
import dev.eternal.client.module.ModuleManager;
import dev.eternal.client.property.PropertyManager;
import dev.eternal.client.render.engine.Pipeline;
import dev.eternal.client.tracker.Tracker;
import dev.eternal.client.ui.alt.AltManager;
import dev.eternal.client.ui.clickgui.ClickGuiManager;
import dev.eternal.client.ui.notification.Notification;
import dev.eternal.client.ui.notification.NotificationManager;
import dev.eternal.client.ui.notification.NotificationType;
import dev.eternal.client.util.files.FileUtils;
import dev.eternal.structures.User;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ChatComponentText;
import scheme.Scheme;
import viamcp.ViaMCP;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Getter@Setter
public class Client {

  @Getter
  private static Client singleton;
  private long startTime;
  private final int colour;
  private final Scheme scheme;
  private final ClientSettings clientSettings;
  private final Pipeline renderPipeline;
  private final ModuleManager moduleManager;
  private final CommandManager commandManager;
  private final EventBus eventBus;
  private final PropertyManager propertyManager;
  private final AltManager altManager;
  private final ConfigRepository configRepository;
  private final long initTime;
  private final Tracker tracker;
  private final EternalBackend eternalBackend;

  @SneakyThrows
  public Client() {
    singleton = this;
//    this.colour = 0xFF72e241;
    this.colour = 0xFFAAFFAA;
    this. scheme = Scheme.dark(colour);
    this.clientSettings = new ClientSettings("Eternal", "3.0.0", "-");
    this.renderPipeline = new Pipeline();
    this.moduleManager = new ModuleManager();
    this.commandManager = new CommandManager();
    this.eventBus = new EventBus();
    this.propertyManager = new PropertyManager();
    this.altManager = new AltManager();
    this.configRepository = new ConfigRepository();
    this.initTime = System.currentTimeMillis();
    this.tracker = new Tracker();
    this.eternalBackend = new EternalBackend();
  }

  public void start() {
    this.renderPipeline.init();
    this.moduleManager.init();
    this.commandManager.init();
    this.propertyManager.init();
    this.configRepository.init();
    this.altManager.load();
    this.eventBus.register(this);
    this.eventBus.register(tracker);
    ViaMCP.getInstance().start();
    ViaMCP.getInstance().initAsyncSlider();
    Runtime.getRuntime().addShutdownHook(new Thread(this::stop));

    ConfigManager.loadBindsOrElseSave();
    ConfigManager.loadConfigOrElseSave("toggled");
    NotificationManager.pushNotification(new Notification("Client launched!", "", 1, NotificationType.SUCCESS));
  }

  private void stop() {
    altManager.save();
    ConfigManager.saveBinds();
    ConfigManager.saveConfig("toggled");
    ClickGuiManager.getClickGui(1).save();
  }

  public void displayMessage(String text) {
    Minecraft minecraftInstance = Minecraft.getMinecraft();
    EntityPlayerSP playerInstance = minecraftInstance.thePlayer;
    String chatPrefix = "\2479ETERNAL \2478\u00BB\247r ";
    if (playerInstance != null) {
      playerInstance.addChatMessage(new ChatComponentText(chatPrefix + text));
    }
  }
}