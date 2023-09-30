package intentions;

// Java IO
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
// Java Net
import java.net.Proxy;
// Arrays and lists (in the *)
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

// Display
import org.lwjgl.opengl.Display;

// Logins
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

// Command manager
import intentions.command.CommandManager;
// Events
import intentions.events.Event;
import intentions.events.listeners.EventChat;
import intentions.events.listeners.EventKey;
import intentions.events.listeners.EventPacket;
// Modules
import intentions.modules.Module;
import intentions.modules.Module.Category;
import intentions.modules.chat.Spammer;
import intentions.modules.chat.Watermark;
import intentions.modules.combat.AimAssist;
import intentions.modules.combat.AutoArmor;
import intentions.modules.combat.AutoClicker;
import intentions.modules.combat.Criticals;
import intentions.modules.combat.InventoryManager;
import intentions.modules.combat.KillAura;
import intentions.modules.combat.MultiAura;
import intentions.modules.combat.Velocity;
import intentions.modules.movement.AirHop;
import intentions.modules.movement.BHop;
import intentions.modules.movement.BPS;
import intentions.modules.movement.ClickTP;
import intentions.modules.movement.Flight;
import intentions.modules.movement.Hover;
import intentions.modules.movement.LongJump;
import intentions.modules.movement.Speed;
import intentions.modules.movement.Sprint;
import intentions.modules.movement.Step;
import intentions.modules.player.AntiCactus;
import intentions.modules.player.AutoSoup;
import intentions.modules.player.AutoTool;
import intentions.modules.player.Blink;
import intentions.modules.player.InvMove;
import intentions.modules.player.Jesus;
import intentions.modules.player.LiquidInteract;
import intentions.modules.player.NoFall;
import intentions.modules.player.NoSlowdown;
import intentions.modules.player.PingSpoof;
import intentions.modules.player.Rotation;
import intentions.modules.player.Team;
import intentions.modules.render.ClickGui;
import intentions.modules.render.ESP;
import intentions.modules.render.FullBright;
import intentions.modules.render.TabGUI;
import intentions.modules.world.AntiVoid;
import intentions.modules.world.CheatDetector;
import intentions.modules.world.ChestStealer;
import intentions.modules.world.Scaffold;
import intentions.modules.world.TimerHack;
// UI
import intentions.ui.HUD;
import intentions.util.CategoryObject;
// Waypoints
import intentions.waypoints.Waypoint;
// Minecraft
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Session;

public class Client {
	
	public static String name = "SafeGuard", version = "b3.3.4";
	public static CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<Module>();
	public static CopyOnWriteArrayList<Module> toggledModules = new CopyOnWriteArrayList<Module>();
	public static HUD hud = new HUD();
	public static CommandManager commandManager = new CommandManager();
	public static String fullName = name + " " + version;
	
	public static void a(Module m) {
		if(modules.contains(m))return;
		modules.add(m);
		System.out.println("[" + Client.name + "] Loaded " + m.name);
	}

	
	public static void startup() {
		System.out.println("[" + name + "] Starting " + name + " " + version);
		Display.setTitle(name + " " + version);
		
		ClickGui.chat = new CategoryObject(Category.CHAT, 0, 10);
		ClickGui.combat = new CategoryObject(Category.COMBAT, 50, 10);
		ClickGui.movement = new CategoryObject(Category.MOVEMENT, 100, 10);
		ClickGui.player = new CategoryObject(Category.PLAYER, 150, 10);
		ClickGui.render = new CategoryObject(Category.RENDER, 200, 10);
		ClickGui.world = new CategoryObject(Category.WORLD, 250, 10);
		
		// World
		a(new Scaffold());
		
		a(new TimerHack());
		
		a(new ChestStealer());
		
		a(new CheatDetector());
		
		// Movement
		a(new Flight());
		
		a(new Sprint());
		
		a(new Speed());
		
		a(new Hover());
		
		a(new AirHop());
		
		a(new BHop());
		
		a(new BPS());
		
		a(new Step());
		
		a(new LongJump());
		
		a(new ClickTP());
		
		// Player
		a(new Blink());
		
		a(new NoFall());
		
		a(new NoSlowdown());
		
		a(new AntiCactus());
		
		a(new Jesus());
		
		a(new Rotation());
		
		a(new AntiVoid());
		
		a(new AutoSoup());
		
		a(new AutoTool());
		
		a(new LiquidInteract());
		
		a(new InvMove());
		
		a(new Team());
		
		a(new PingSpoof());
		
		// Render
		a(new FullBright());
		
		a(new ESP());
		
		// Combat
		a(new KillAura());
		
		a(new MultiAura());
		
		a(new Velocity());
		
		a(new Criticals());
		
		a(new AutoArmor());
		
		a(new InventoryManager());
		
		a(new AutoClicker());
		
		a(new AimAssist());
		
		// Chat
		a(new Spammer());
		
		a(new Watermark());
		
		// GUI
		a(new ClickGui());
		
		a(new TabGUI());
		
		File file = new File(System.getProperty("user.dir") + "\\SafeGuard\\alt.txt");
		
		if(file.exists()) {
			StringBuilder f = new StringBuilder();
			try {
				  
				  BufferedReader br = new BufferedReader(new FileReader(file));
				  
				  String st;
				  while ((st = br.readLine()) != null) {
					   f.append(st);
				  }
				  br.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			
			String d = f.toString();
			
			final String args[] = d.split(":");
			if(args[0].contains("@") && args[0].contains(".")) {
				final YggdrasilUserAuthentication authentication = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);
				authentication.setUsername(args[0]);
				authentication.setPassword(args[1]);
				try {
					authentication.logIn();
					
					Minecraft.getMinecraft().session = new Session(authentication.getSelectedProfile().getName(), authentication.getSelectedProfile().getId().toString(), authentication.getAuthenticatedToken(), "mojang");	
				} catch (AuthenticationException e) {
					e.printStackTrace();
				}
			} else {
				Minecraft.getMinecraft().session = new Session(d, "", "", "mojang");
			}
		}
		
		file = new File(System.getProperty("user.dir") + "\\SafeGuard\\settings.txt");
		
		if(file.exists()) {
			StringBuilder f = new StringBuilder();
			try {
				  
				  BufferedReader br = new BufferedReader(new FileReader(file));
				  
				  String st;
				  while ((st = br.readLine()) != null) {
					   f.append(st);
				  }
				  br.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			
			String d = f.toString();
			
			String[] args = d.split(";");
			
			
			
			try {
				
				for(Module m : modules) {
					
					if(m.name.equalsIgnoreCase(args[1])) {
						
						boolean tof = args[0].equalsIgnoreCase("true");
						
						m.toggled = tof;
						
						break;
						
					}
					
				}
				
			}catch(Exception e) {}
			
		}
		
	}
	
	public static void onEvent(Event e) {
		if(e instanceof EventChat) {
			commandManager.handleChat((EventChat)e);
		}
		
		for(Module m : modules) {
			if ((!m.toggled && !m.name.equalsIgnoreCase("TabGUI")) || m.name.equalsIgnoreCase("ClickGui"))
				continue;
			m.onEvent(e);
		}
		ClickGui.onEventStatic(e);
	}
	
	public static void keyPress(int key) {
		if(!TabGUI.openTabGUI) return;
		Client.onEvent(new EventKey(key));
		
		for(Module m : modules) {
			if (m.getKey() == key) {
				m.toggle();
				break;
			}
		}
	}
	
	public static List<Module> getModulesByCategory(Category c){
		List<Module> modules = new ArrayList<Module>();
		
		for(Module m : Client.modules) {
			if (m.category == c) {
				modules.add(m);
			}
		}
		
		return modules;
	}
	
	public static void addChatMessage(Object b) {
		b = "\2478[\247c" + name + "\2478] \2477" + b;
		
		if(b.toString().contains("Disabled ")) {
			if (!TabGUI.openTabGUI) {
				return;
			}
		}
		
		Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(b.toString()));
	}
	
	public static void onRender() {
		if(!TabGUI.openTabGUI) return;
		for(Module module : Client.toggledModules) {
			module.onRender();
		}
	}
	
	public static void onTick() {
		if(!TabGUI.openTabGUI) return;
		for (Module module : Client.toggledModules) {
			
			module.onTick();
		}
		Waypoint.onTick();
	}
	
	public static void onUpdate() {
		if(!TabGUI.openTabGUI) return;
		Minecraft mc = Minecraft.getMinecraft();
		for(Module module : Client.modules) {
			if(module.name == "Flight" && (!module.toggled || !((Flight)module).type.getMode().equalsIgnoreCase("Redesky"))) {
				Minecraft.getMinecraft().thePlayer.speedInAir = 0.02f;
			}
			((Module)module).onUpdate();
		}
		
	}


	public static void onLateUpdate() {
		if(!TabGUI.openTabGUI) return;
		for(Module module : Client.toggledModules) {
			((Module)module).onLateUpdate();
		}
	}

	private static LinkedList<Packet> packets = new LinkedList<Packet>();

	public static EventPacket sendPacket(Packet packetIn) {
		if(packets.contains(packetIn))return null;
		packets.add(packetIn);
		EventPacket eventPacket = new EventPacket(packetIn, true);
		for(Module m : Client.toggledModules) {
			m.onSendPacket(eventPacket);
		}
		return eventPacket;
	}


	public static void onBB(AxisAlignedBB bb) {
		if(!TabGUI.openTabGUI) return;
		for(Module module : Client.toggledModules) {
			((Module)module).onBB(bb);
		}
	}
}
