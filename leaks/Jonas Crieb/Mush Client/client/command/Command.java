package client.command;

import client.Client;
import client.util.chat.ChatUtil;
import client.util.interfaces.InstanceAccess;
import lombok.Getter;
import org.atteo.classindex.IndexSubclasses;

@Getter
@IndexSubclasses
public abstract class Command implements InstanceAccess {

    private final String description;
    private final String[] expressions;

    public Command(final String description, final String... expressions) {
        this.description = description;
        this.expressions = expressions;
    }

    public abstract void execute(String[] args);

    protected final void error() {
        ChatUtil.display("Invalid command arguments.");
    }

    protected final void error(final String usage) {
        ChatUtil.display("Invalid command arguments. " + usage);
    }
}
