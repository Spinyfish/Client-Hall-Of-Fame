package wtf.automn;

import lombok.Getter;
import org.lwjgl.opengl.Display;
import viamcp.ViaMCP;
import wtf.automn.command.CommandManager;
import wtf.automn.discord.DiscordRPC;
import wtf.automn.gui.alt.management.AccountManager;
import wtf.automn.module.Manager;
import wtf.automn.module.ModuleManager;
import wtf.automn.utils.io.FileUtil;
import wtf.automn.utils.player.RotationManager;
import wtf.automn.utils.player.YawPitchHelper;

import java.awt.font.TextHitInfo;

@Getter
public class Automn {
  public static final String NAME = "Automn";
  public static final String DEVELOPER = "LCA_MODZ,CommandJo";
  public static final String BUILD = "Beta 0.1";

  @Getter
  private static Automn instance;
  private Manager manager;
  private CommandManager commandManager;
  private ModuleManager moduleManager;
  private AccountManager accountManager;
  private RotationManager rotationManager;
  private YawPitchHelper yawPitchHelper;
  private DiscordRPC discordRPC;


  public void onStartUp() {
    instance = this;
    Display.setTitle(NAME + " " + BUILD + " by " + DEVELOPER);
    try {
      new ViaMCP().getInstance().start();
    } catch (final Exception e) {
      System.err.println("Viaversion couldn't be loaded!");
    }
    FileUtil.init();
    init();
    // test
  }

  public void init() {
    this.commandManager = new CommandManager();

    this.manager = new Manager();

    this.moduleManager = new ModuleManager();

    this.accountManager = new AccountManager();
    this.accountManager.init();

    this.rotationManager = new RotationManager();

    this.yawPitchHelper = new YawPitchHelper();

        this.discordRPC = new DiscordRPC();
        this.discordRPC.start();
  }
}
