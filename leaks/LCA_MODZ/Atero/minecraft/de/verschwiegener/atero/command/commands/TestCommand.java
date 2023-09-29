package de.verschwiegener.atero.command.commands;

import java.util.Arrays;

import de.verschwiegener.atero.command.Command;
import de.verschwiegener.atero.util.chat.ChatUtil;

public class TestCommand extends Command{

	public TestCommand() {
		super("Test", "Test", new String[]{"<module> <key>"});
	}

	@Override
	public void onCommand(String[] args) {
		System.out.println("Args: " + Arrays.toString(args));
		ChatUtil.addLoadChat2();
		
	}

}
