package ooo.cpacket.ruby.manager;

import java.util.ArrayList;
import java.util.List;

import ooo.cpacket.ruby.command.Command;
import ooo.cpacket.ruby.command.commands.Friend;
import ooo.cpacket.ruby.command.commands.VClip;
import ooo.cpacket.ruby.command.commands.crashplex;

public class CommandManager {
	
	public List<Command> cmds = new ArrayList<>();
	
	public CommandManager() {
		this.cmds.add(new VClip());
		this.cmds.add(new crashplex());
		this.cmds.add(new Friend());
	}
	
}
