package de.lirium.base.changelog;

import de.lirium.Client;
import de.lirium.base.feature.Manager;
import de.lirium.util.interfaces.IMinecraft;
import god.buddy.aot.BCompiler;
import lombok.SneakyThrows;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class ChangelogManager implements Manager<Changelog>, IMinecraft {

    private final ArrayList<Changelog> changelogs = new ArrayList<>();

    @Override
    public ArrayList<Changelog> getFeatures() {
        return changelogs;
    }

    @SneakyThrows
    public void init() {
        getChangelogs().stream().filter(s -> !s.isEmpty() && Integer.parseInt(s.replace(".", "")) <= Integer.parseInt(Client.VERSION.replace(".", ""))).forEach(version -> {
            final Changelog changelog = new Changelog(version);
            this.changelogs.add(changelog);

            try {
                final URL url = new URL("https://raw.githubusercontent.com/Lirium-Team/Changelog/main/" + version);
                final Scanner scanner = new Scanner(url.openStream(), "UTF-8");
                boolean stop = false;
                while (scanner.hasNext()) {
                    final String line = scanner.nextLine();
                    if (line.startsWith("==") && line.endsWith("==")) {
                        if (Client.SNAPSHOT == null) continue;
                        final String snapshot = line.replace("==", "");
                        final String[] data = snapshot.split("w");
                        final String[] currentData = Client.SNAPSHOT.split("w");
                        final int snapshotYear = Integer.parseInt(data[0]);
                        final int snapshotWeek = Integer.parseInt(data[1].substring(0, data[1].length() - 1));
                        final int year = Integer.parseInt(currentData[0]);
                        final int week = Integer.parseInt(currentData[1].substring(0, data[1].length() - 1));
                        if (year < snapshotYear || (year == snapshotYear && week < snapshotWeek))
                            break;
                    }
                    changelog.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public static List<String> getChangelogs() {
        URL url = null;
        final List<String> result = new ArrayList<>();
        try {
            url = new URL("https://raw.githubusercontent.com/Lirium-Team/Changelog/main/list");
            final Scanner sc = new Scanner(url.openStream());
            String line;
            while (sc.hasNext()) {
                line = sc.nextLine();
                result.add(line);
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public <U extends Changelog> U get(Class<U> clazz) {
        return null;
    }

    public <U extends Changelog> U get(String version) {
        return (U) changelogs.stream().filter(changelog -> changelog.getName().equalsIgnoreCase(version)).findAny().orElse(null);
    }

    @Override
    public Changelog get(Type type) {
        return null;
    }

}
