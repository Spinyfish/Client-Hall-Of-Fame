package club.pulsive.altmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import club.pulsive.api.main.Pulsive;
import org.apache.logging.log4j.LogManager;



public class AltManager {
    private final Path dataFile;
    public ArrayList<String> save = new ArrayList<>();
    public ArrayList<Alt> alts = new ArrayList<>();
    public String status;

    public AltManager() {
        Path altManagerFolder = Paths.get(Pulsive.INSTANCE.getClientDir().toString(), "altManager");

        dataFile = Paths.get(altManagerFolder.toString(), "alts.txt");

        //creating folder
        if(!altManagerFolder.toFile().exists() && altManagerFolder.toFile().mkdir())
            LogManager.getLogger().info("[Pulsive] Created alt manager folder.");

        //creating txt file containing alts
        if (!altManagerFolder.toFile().exists()) {
            altManagerFolder.toFile().mkdir();
        }
        
        if (!dataFile.toFile().exists()) {
            try {
                if (dataFile.toFile().mkdir())
                    LogManager.getLogger().info("[Pulsive] Created alts file.");
            } catch (Exception e) {
                LogManager.getLogger().error("[Pulsive] Failed to create alts file:");
                e.printStackTrace();
            }
        }
    }

    public void addLine(Alt alt) {
        save.add(alt.getUsername() + ":" + alt.getPassword() + ":" + alt.getCreationdate());
    }

    //saving alts, call this on client shutdown
    public void save() {
        save.clear();

        for (Alt alt : alts) {
            addLine(alt);
        }

        try {
            PrintWriter printWriter = new PrintWriter(dataFile.toFile());
            for (String str : save) {
                printWriter.println(str);
            }
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //loading alts, call this on client starting
    public void load() {
        alts.clear();
        ArrayList<String> lines = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(dataFile.toFile()));
            String line = reader.readLine();

            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String string : lines) {
            String[] args = string.split(":");

            if (args[0] != null && args[1] != null && args[2] != null) {
                try {
                    addAlt(new Alt(args[0], args[1], Long.parseLong(args[2])));
                } catch (Exception ignored) {

                }
            }
        }
    }

    public ArrayList<Alt> getAlts() {
        return alts;
    }

    public void addAlt(Alt alt) {
        alts.add(alt);
    }

    public boolean isValidCrackedAlt(String username) {
        return !username.equals("") && !username.contains("&") && !username.contains("-")
                && !username.contains("+") && !username.contains("/") && !username.contains("\\") &&
                !username.contains(".") && !username.contains("@") && username.length() <= 16 && username.length() >= 3;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}