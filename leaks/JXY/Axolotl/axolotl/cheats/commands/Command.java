package axolotl.cheats.commands;

public abstract class Command {

    public String name, description;

    public Command(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Command{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public abstract String onCommand(String[] args, String message);

}
