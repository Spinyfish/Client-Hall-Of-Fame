package intent.AquaDev.aqua.command;

public class Command {
   private String name;

   public Command(String name) {
      this.name = name;
   }

   public void execute(String[] args) {
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
