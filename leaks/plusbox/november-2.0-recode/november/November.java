/* November.lol © 2023 */
package lol.november;

import static java.lang.String.format;
import static net.minecraft.client.Minecraft.readImageToBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import lol.november.config.ConfigManager;
import lol.november.feature.account.AccountManager;
import lol.november.feature.command.CommandRegistry;
import lol.november.feature.hud.HUDRegistry;
import lol.november.feature.keybind.KeyBindManager;
import lol.november.feature.module.ModuleRegistry;
import lol.november.listener.bus.EventBus;
import lol.november.management.inventory.InventoryManager;
import lol.november.management.rotate.RotationManager;
import lol.november.protect.ws.NovemberWebsocket;
import lol.november.scripting.ScriptManager;
import lol.november.utility.BuildConfig;
import lol.november.utility.render.font.Fonts;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Log4j2
public class November {

  /**
   * The {@link November} class singleton
   */
  private static November singleton;

  private static NovemberWebsocket websocket;

  /**
   * The {@link EventBus} for dispatching and handling events
   */
  private static final EventBus bus = new EventBus();

  private final ModuleRegistry modules;
  private final CommandRegistry commands;
  private final HUDRegistry hud;
  private final ScriptManager scripts;
  private final ConfigManager configs;
  private final KeyBindManager keybinds;
  private final AccountManager accounts;
  private final RotationManager rotations;
  private final InventoryManager inventory;

  /**
   * Creates the {@link November} object
   */
  private November() {
    singleton = this;

    Display.setTitle(format("November %s", fullVersion()));

    try {
      InputStream x16 =
        November.class.getResourceAsStream("/assets/november/logo/16x.png");
      InputStream x32 =
        November.class.getResourceAsStream("/assets/november/logo/32x.png");

      Display.setIcon(
        new ByteBuffer[] { readImageToBuffer(x16), readImageToBuffer(x32) }
      );

      if (x16 != null) x16.close();
      if (x32 != null) x32.close();
    } catch (IOException e) {
      log.error("Failed to set custom icon");
      e.printStackTrace();
    }

    // load custom fonts
    Fonts.init();

    // load general managers
    configs = new ConfigManager();
    keybinds = new KeyBindManager();
    accounts = new AccountManager();
    rotations = new RotationManager();
    inventory = new InventoryManager();

    modules = new ModuleRegistry();
    modules.init();

    commands = new CommandRegistry();
    commands.init();

    hud = new HUDRegistry();
    hud.init();

    scripts = new ScriptManager();
    scripts.init();

    // load client configs
    configs.load();

    Minecraft.getMinecraft().session.setUsername("NovemberOnTop");
  }

  /**
   * Gets the {@link ConfigManager} instance
   *
   * @return the {@link ConfigManager} instance
   */
  public ConfigManager configs() {
    return configs;
  }

  /**
   * Gets the {@link AccountManager} instance
   *
   * @return the {@link AccountManager} instance
   */
  public AccountManager accounts() {
    return accounts;
  }

  /**
   * Gets the {@link RotationManager} instance
   *
   * @return the {@link RotationManager} instance
   */
  public RotationManager rotations() {
    return rotations;
  }

  /**
   * Gets the {@link InventoryManager} instance
   *
   * @return the {@link InventoryManager} instance
   */
  public InventoryManager inventory() {
    return inventory;
  }

  /**
   * Gets the {@link KeyBindManager} instance
   *
   * @return the {@link KeyBindManager} instance
   */
  public KeyBindManager keybinds() {
    return keybinds;
  }

  /**
   * Gets the {@link ModuleRegistry} instance
   *
   * @return the {@link ModuleRegistry} instance
   */
  public ModuleRegistry modules() {
    return modules;
  }

  /**
   * Gets the {@link CommandRegistry} instance
   *
   * @return the {@link CommandRegistry} instance
   */
  public CommandRegistry commands() {
    return commands;
  }

  /**
   * Gets the {@link HUDRegistry} instance
   *
   * @return the {@link HUDRegistry} instance
   */
  public HUDRegistry hud() {
    return hud;
  }

  /**
   * Gets the {@link ScriptManager} instance
   *
   * @return the {@link ScriptManager} instance
   */
  public ScriptManager scripts() {
    return scripts;
  }

  /**
   * Initializes the {@link November} singleton
   */
  public static void init() {
    websocket = new NovemberWebsocket();

    if (singleton == null) {
      log.info("Creating November singleton");
      singleton = new November();
    }
  }

  /**
   * Gets the full version string
   *
   * @return the full version string
   */
  public static String fullVersion() {
    return (
      BuildConfig.VERSION + "+" + BuildConfig.BUILD + "-" + BuildConfig.HASH
    );
  }

  /**
   * Gets the {@link November} singleton
   *
   * @return the {@link November} singleton
   */
  public static November instance() {
    return singleton;
  }

  /**
   * Gets the {@link EventBus}
   *
   * @return the {@link EventBus}
   */
  public static EventBus bus() {
    return bus;
  }

  public static NovemberWebsocket socket() {
    return websocket;
  }
}
