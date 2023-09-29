package de.lirium.base.background;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.lirium.Client;
import de.lirium.base.feature.Manager;
import de.lirium.util.interfaces.Logger;
import de.lirium.util.render.shader.ShaderProgram;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class BackgroundManager implements Manager<Background> {

    private final ArrayList<Background> backgrounds = new ArrayList<>();

    public Background current = null;

    @SneakyThrows
    public void init() {
        final File dir = new File(Client.DIR, "backgrounds");
        boolean createdDir = false;
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                Logger.print("Created \"background\" folder");
                createdDir = true;
            }
        }

        Arrays.stream(Objects.requireNonNull(dir.listFiles())).forEach(file -> {
            if (file.getName().endsWith(".glsl")) {
                final String name = file.getName().replaceFirst("[.][^.]+$", "");
                try {
                    final BufferedReader reader = new BufferedReader(new FileReader(file));
                    final StringJoiner joiner = new StringJoiner("\n");
                    reader.lines().forEach(joiner::add);
                    add(name, joiner.toString());
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if(createdDir || backgrounds.isEmpty()) {
            final String defaultShader = loadDefault();
            if (defaultShader != null)
                add("Default", defaultShader);
        }

        final File file = new File(Client.DIR, "data/background.txt");
        if (file.exists()) {
            final BufferedReader reader = new BufferedReader(new FileReader(file));
            final String line = reader.readLine();
            final Background background = backgrounds.stream().filter(bckg -> bckg.getName().equalsIgnoreCase(line)).findAny().orElse(null);
            reader.close();
            if (background != null) {
                current = background;
            } else
                current = new Background("Default", new ShaderProgram("vertex/vertex.vsh", "fragment/background.glsl"));

        } else {
            if (backgrounds.size() > 0)
            current = backgrounds.get(0);
            else {
                current = new Background("Default", new ShaderProgram("vertex/vertex.vsh", "fragment/background.glsl"));
            }
        }
    }

    private String loadDefault() {
        final ResourceLocation location = new ResourceLocation("lirium/shaders/fragment/background.glsl");
        try {
            final InputStreamReader inputStreamReader = new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream());
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            final StringJoiner joiner = new StringJoiner("\n");
            String line;
            while ((line = bufferedReader.readLine()) != null)
                joiner.add(line);
            bufferedReader.close();
            inputStreamReader.close();
            return joiner.toString();
        } catch (IOException e) {
            Logger.print("Default background shader made something shitty i am suicidal rofl emoji");
            e.printStackTrace();
        }
        return null;
    }

    public Background nextBackground() {
        Background current;
        final int index = backgrounds.indexOf(this.current) + 1;
        if (index > backgrounds.size() - 1) {
            current = backgrounds.get(0);
        } else {
            current = backgrounds.get(index);
        }
        return current;
    }

    public Background previusBackground() {
        Background current;
        final int index = backgrounds.indexOf(this.current) - 1;
        if (index < 0) {
            current = backgrounds.get(backgrounds.size() - 1);
        } else {
            current = backgrounds.get(index);
        }
        return current;
    }

    @SneakyThrows
    public void add(String name, String fragment) {
        final File dir = new File(Client.DIR, "backgrounds");
        final File file = new File(dir, name + ".glsl");
        if (!file.exists()) {
            if(file.createNewFile()) {
                Logger.print(name + " shader file successful created!");
            }
        }
        final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(fragment);
        writer.close();

        final ShaderProgram program = new ShaderProgram("vertex/vertex.vsh", fragment);
        final Background background = new Background(name, program);
        this.backgrounds.add(background);
    }

    @Override
    public ArrayList<Background> getFeatures() {
        return backgrounds;
    }

    @Override
    public <U extends Background> U get(Class<U> clazz) {
        return null;
    }

    @Override
    public Background get(Type type) {
        return null;
    }
}
