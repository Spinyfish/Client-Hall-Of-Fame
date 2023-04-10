/*
 * Decompiled with CFR 0_122.
 */
package arithmo.gui.altmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
public class Alts
extends FileManager.CustomFile {
    public Alts(String name, boolean Module2, boolean loadOnStart) {
        super(name, Module2, loadOnStart);
    }

    @Override
    public void loadFile() throws IOException {
        String line;
        BufferedReader variable9 = new BufferedReader(new FileReader(this.getFile()));
        while ((line = variable9.readLine()) != null) {
            String[] arguments = line.split(":");
            if (arguments.length > 2) {
                AltManager.registry.add(new Alt(arguments[0], arguments[1], arguments[2]));
                continue;
            }
            AltManager.registry.add(new Alt(arguments[0], arguments[1]));
        }
        variable9.close();
        this.isloaded = true;
        System.out.println("Loaded " + this.getName() + " File!");
    }

    @Override
    public void saveFile() throws IOException {
        PrintWriter alts = new PrintWriter(new FileWriter(this.getFile()));
        for (Alt alt : AltManager.registry) {
            if (alt.getMask().equals("")) {
                alts.println(String.valueOf(alt.getUsername()) + ":" + alt.getPassword());
                continue;
            }
            alts.println(String.valueOf(alt.getUsername()) + ":" + alt.getPassword() + ":" + alt.getMask());
        }
        alts.close();
    }
}

