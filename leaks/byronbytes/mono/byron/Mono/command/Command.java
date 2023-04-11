package byron.Mono.command;

public class Command {

    private String name;
    private String alias;



    private String help;
    private String prefix = ".";

    public Command(String name, String alias, String help) {
        this.name = name;
        this.alias = alias;
        this.help = help;
    }

    public void onCommand(String[] args) {

    }

    public String getHelp() {
        return help;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }


}
