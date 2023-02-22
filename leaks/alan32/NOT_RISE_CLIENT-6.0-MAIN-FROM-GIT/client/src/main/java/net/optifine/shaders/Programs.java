package net.optifine.shaders;

import java.util.ArrayList;
import java.util.List;

public class Programs {
    private final List<Program> programs = new ArrayList();
    private final Program programNone = this.make("", ProgramStage.NONE, true);

    public Program make(final String name, final ProgramStage programStage, final Program backupProgram) {
        final int i = this.programs.size();
        final Program program = new Program(i, name, programStage, backupProgram);
        this.programs.add(program);
        return program;
    }

    private Program make(final String name, final ProgramStage programStage, final boolean ownBackup) {
        final int i = this.programs.size();
        final Program program = new Program(i, name, programStage, ownBackup);
        this.programs.add(program);
        return program;
    }

    public Program makeGbuffers(final String name, final Program backupProgram) {
        return this.make(name, ProgramStage.GBUFFERS, backupProgram);
    }

    public Program makeComposite(final String name) {
        return this.make(name, ProgramStage.COMPOSITE, this.programNone);
    }

    public Program makeDeferred(final String name) {
        return this.make(name, ProgramStage.DEFERRED, this.programNone);
    }

    public Program makeShadow(final String name, final Program backupProgram) {
        return this.make(name, ProgramStage.SHADOW, backupProgram);
    }

    public Program makeVirtual(final String name) {
        return this.make(name, ProgramStage.NONE, true);
    }

    public Program[] makeComposites(final String prefix, final int count) {
        final Program[] aprogram = new Program[count];

        for (int i = 0; i < count; ++i) {
            final String s = i == 0 ? prefix : prefix + i;
            aprogram[i] = this.makeComposite(s);
        }

        return aprogram;
    }

    public Program[] makeDeferreds(final String prefix, final int count) {
        final Program[] aprogram = new Program[count];

        for (int i = 0; i < count; ++i) {
            final String s = i == 0 ? prefix : prefix + i;
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

    public Program getProgram(final String name) {
        if (name == null) {
            return null;
        } else {
            for (int i = 0; i < this.programs.size(); ++i) {
                final Program program = this.programs.get(i);
                final String s = program.getName();

                if (s.equals(name)) {
                    return program;
                }
            }

            return null;
        }
    }

    public String[] getProgramNames() {
        final String[] astring = new String[this.programs.size()];

        for (int i = 0; i < astring.length; ++i) {
            astring[i] = this.programs.get(i).getName();
        }

        return astring;
    }

    public Program[] getPrograms() {
        final Program[] aprogram = this.programs.toArray(new Program[this.programs.size()]);
        return aprogram;
    }

    public Program[] getPrograms(final Program programFrom, final Program programTo) {
        int i = programFrom.getIndex();
        int j = programTo.getIndex();

        if (i > j) {
            final int k = i;
            i = j;
            j = k;
        }

        final Program[] aprogram = new Program[j - i + 1];

        for (int l = 0; l < aprogram.length; ++l) {
            aprogram[l] = this.programs.get(i + l);
        }

        return aprogram;
    }

    public String toString() {
        return this.programs.toString();
    }
}
