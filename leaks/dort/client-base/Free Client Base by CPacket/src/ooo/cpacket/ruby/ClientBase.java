package ooo.cpacket.ruby;

import java.net.InetSocketAddress;
import java.net.Proxy;

import me.arithmo.gui.altmanager.FileManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import ooo.cpacket.lemongui.settings.SettingsManager;
import ooo.cpacket.ruby.api.ClientInfo;
import ooo.cpacket.ruby.manager.CommandManager;
import ooo.cpacket.ruby.manager.ModuleManager;

public enum ClientBase {
	INSTANCE(ClientInfo.get());
	public String currSubnet = "a";
	private FileManager fman;
	private ModuleManager moduleManager;
	public SettingsManager setmgr;
	public CommandManager cmds;
	public ClientInfo cinfo;
	public Proxy proxy = Proxy.NO_PROXY;
	private ClientBase(ClientInfo cInfo) {
		this.cinfo = cInfo;
		this.setmgr = new SettingsManager();
	}
	{
		cmds = new CommandManager();
	}
	public ModuleManager getModuleManager() {
		if (this.moduleManager == null) {
			this.moduleManager = new ModuleManager();
			this.moduleManager.setup();
		}
		return this.moduleManager;
	}

	public void chat(String msg) {
		Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("\u00A7f[\u00A7c\u00A7lRUBY\u00A7f]\u00A7r " + msg));
	}

	public FileManager getFileMgr() {
		if (fman == null) {
			fman = new FileManager();
			fman.loadFiles();
		}
		return this.fman;
	}
	public int prx;
	public int getProxies() {
		return prx++;
	}

	public String get64() {
		return "a";
	}

	public String get56() {
		return this.currSubnet;
	}
	
}
