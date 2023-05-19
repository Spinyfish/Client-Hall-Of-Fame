package club.marsh.bloom;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.lwjgl.opengl.Display;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.command.Command;
import club.marsh.bloom.api.command.CommandManager;
import club.marsh.bloom.api.components.BlinkingComponent;
import club.marsh.bloom.api.config.ConfigManager;
import club.marsh.bloom.api.font.FontManager;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.module.ModuleManager;
import club.marsh.bloom.api.value.ValueManager;
import club.marsh.bloom.impl.events.KeyEvent;
import club.marsh.bloom.impl.events.PacketEvent;
import club.marsh.bloom.impl.ui.hud.HudDesigner;
import club.marsh.bloom.impl.ui.hud.HudDesignerUI;
import club.marsh.bloom.impl.ui.notification.NotificationPublisher;
import club.marsh.bloom.impl.utils.other.HWID;
import club.marsh.bloom.impl.utils.other.Rotation;
import club.marsh.bloom.impl.utils.other.UrMomGayUtil;
import club.marsh.bloom.impl.utils.other.UserData;
import club.marsh.bloom.impl.utils.render.BloomHandler;
import marshscript.ScriptManager;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.ChatComponentText;
import viamcp.ViaMCP;

public enum Bloom
{
	INSTANCE;
	public ModuleManager moduleManager;
	public NotificationPublisher notificationPublisher;
	public EventBus eventBus;
	public String build = "v1.23";
	public ValueManager valueManager;
	public BloomHandler bloomHandler;
	public FontManager fontManager;
	public CommandManager commandManager;
	public Rotation rotation = new Rotation(0,0);
	public HudDesigner hudDesigner;
	public HudDesignerUI hudDesignerUI;
	public ScriptManager scriptManager;
	public boolean firstLoad = false;
	public boolean running = true;
	public String hwid = HWID.getHwid();

	public void initialize() throws IOException, InterruptedException {
		try
		{
			ViaMCP.getInstance().start();
			ViaMCP.getInstance().initAsyncSlider();
		}
		
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		if (!ConfigManager.preventDiscordRPCUsageFile.exists())
		{
			DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) ->
			{
				System.out.println("Authing with " + user.username + "#" + user.discriminator + "!");
		        UserData.INSTANCE.userID = user.userId;
				UserData.INSTANCE.discordName = user.username;
				UserData.INSTANCE.discordDiscriminator = user.discriminator;
				UserData.INSTANCE.tag = user.username + "#" + user.discriminator;
		        running = false;
	        }).build();
			
	        DiscordRPC.discordInitialize("1073891470005764106", handlers, true);
	        
	        new Thread(() ->
	        {
	        	while (this.running)
	        	{
	        		DiscordRPC.discordRunCallbacks();
	        	}
	        }).start();
	        
			while (this.running)
			{
				Thread.sleep(1000);
			}
		}
		
		else
		{
			UserData.INSTANCE.userID = "0";
		}
		
		String randomHwid = "";
		char[] chararray = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
		
		for (int i = 0; i < 64; i++)
		{
			randomHwid += chararray[new Random().nextInt(62)];
		}
		
		if (UrMomGayUtil.ILLIILLLASIOJCNLINULLBBBCCCC(randomHwid))
		{
			// That means it returned true for a fake HWID, that means it's a crack!!!
			// Crack patched! for now
			System.exit(0);
		}
		
        if (!UrMomGayUtil.ILLIILLLASIOJCNLINULLBBBCCCC())
        {
			StringSelection stringSelection = new StringSelection(hwid);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, null);
        	UIManager.put("OptionPane.messageFont", new Font(Font.SANS_SERIF, 1, 20));
    		UIManager.put("OptionPane.buttonFont", new Font(Font.SANS_SERIF, 1, 20));
            JOptionPane jOptionPane = new JOptionPane("Click OK to copy your HWID.", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{});
			JButton jButton = new JButton("OK");
			
			jButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					System.exit(0);;
	            }
			});
			
			jOptionPane.add(jButton);
            JDialog jDialog = jOptionPane.createDialog("HWID Alert");
			jDialog.setAlwaysOnTop(true);
			jDialog.setSize(390, 160);
			jDialog.setVisible(true);
            System.exit(0);
        	return;
        }
        
        Minecraft.getMinecraft().displayGuiScreen(new GuiMainMenu());
		Display.setTitle("Bloom Client | " + build);
		eventBus = new EventBus("Bloom");
		eventBus.register(this);
		initializeManagers();
	}
	
	public void onKey(int key)
	{
		eventBus.post(new KeyEvent(key));
		
        for (Module m : moduleManager.getModules())
        {
        	if (m.getKeybind() == key)
        	{
        		m.setToggled(!m.isToggled());
        	}
        }
	}
	
	public void initializeManagers()
	{
		fontManager = new FontManager("Bloom/fonts");
		valueManager = new ValueManager();
		moduleManager = new ModuleManager();
		commandManager = new CommandManager();
		notificationPublisher = new NotificationPublisher();
		bloomHandler = new BloomHandler();
		hudDesigner = new HudDesigner();
		
		try
		{
			if (!UrMomGayUtil.ILLIILLLASIOJCNLINULLBBBCCCC())
			{
				return;
			}
		} 
		
		catch (Exception e)
		{
			return;
		}
		
		hudDesignerUI = new HudDesignerUI();
		
		try
		{
			ConfigManager.init();
			scriptManager = new ScriptManager();
			scriptManager.load(ConfigManager.scriptDirectory);
		}
		
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		Bloom.INSTANCE.eventBus.register(BlinkingComponent.INSTANCE);
	}
	
	@Subscribe
	public void onPacket(PacketEvent e)
	{
		if (e.getPacket() instanceof C01PacketChatMessage)
		{
			C01PacketChatMessage chat = (C01PacketChatMessage) e.getPacket();
			
			if (chat.getMessage().startsWith("."))
			{
				String substringed = chat.getMessage().substring(1);
				String[] split = substringed.split(" ");
			    String command = split[0];
			    String args = substringed.substring(command.length()).trim();
			    
			    for (Command cmd : commandManager.commands)
			    {
			    	if (cmd.name.equalsIgnoreCase(command))
			    	{
			    		try
			    		{
			    			cmd.onCommand(args, args.split(" "));
			            }
			    		
			    		catch (Exception exception)
			    		{
			    			Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("\\247dBloom \\2479>>\\247a Command not found!"));
			            }
			        }
			    }
			    
				e.setCancelled(true);
			}
		}
	}
}
