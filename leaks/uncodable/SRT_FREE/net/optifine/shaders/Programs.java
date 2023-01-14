package net.optifine.shaders;

import java.util.ArrayList;
import java.util.List;

public class Programs {
   private final List<Program> programs = new ArrayList<>();
   private final Program programNone = this.make("");

   public Program make(String name, ProgramStage programStage, Program backupProgram) {
      int i = this.programs.size();
      Program program = new Program(i, name, programStage, backupProgram);
      this.programs.add(program);
      return program;
   }

   private Program make(String name) {
      int i = this.programs.size();
      Program program = new Program(i, name, ProgramStage.NONE, true);
      this.programs.add(program);
      return program;
   }

   public Program makeGbuffers(String name, Program backupProgram) {
      return this.make(name, ProgramStage.GBUFFERS, backupProgram);
   }

   public Program makeComposite(String name) {
      return this.make(name, ProgramStage.COMPOSITE, this.programNone);
   }

   public Program makeDeferred(String name) {
      return this.make(name, ProgramStage.DEFERRED, this.programNone);
   }

   public Program makeShadow(String name, Program backupProgram) {
      return this.make(name, ProgramStage.SHADOW, backupProgram);
   }

   public Program makeVirtual(String name) {
      return this.make(name);
   }

   public Program[] makeComposites(String prefix, int count) {
      Program[] aprogram = new Program[count];

      for(int i = 0; i < count; ++i) {
         String s = i == 0 ? prefix : prefix + i;
         aprogram[i] = this.makeComposite(s);
      }

      return aprogram;
   }

   public Program[] makeDeferreds(String prefix, int count) {
      Program[] aprogram = new Program[count];

      for(int i = 0; i < count; ++i) {
         String s = i == 0 ? prefix : prefix + i;
         aprogram[i] = this.makeDeferred(s);
      }

      return aprogram;
   }

   public Program getProgramNone() {
      return this.programNone;
   }

   public int getCount() {
      return this.programs.size();
   }

   public Program getProgram(String name) {
      if (name != null) {
         for(Program program : this.programs) {
            String s = program.getName();
            if (s.equals(name)) {
               return program;
            }
         }
      }

      return null;
   }

   public String[] getProgramNames() {
      String[] astring = new String[this.programs.size()];

      for(int i = 0; i < astring.length; ++i) {
         astring[i] = this.programs.get(i).getName();
      }

      return astring;
   }

   public Program[] getPrograms() {
      return this.programs.toArray(new Program[0]);
   }

   @Override
   public String toString() {
      return this.programs.toString();
   }
}
