package dev.africa.pandaware.utils.java;

import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import dev.africa.pandaware.utils.math.random.RandomUtils;
import lombok.experimental.UtilityClass;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.RandomStringUtils;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@UtilityClass
public class FileUtils implements MinecraftInstance {

    String randomLine;

    public File openFilePicker(boolean betterGui) throws Exception {
        if (mc.isFullScreen()) {
            mc.toggleFullscreen();
        }
        if (betterGui) {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }

        JFileChooser fileChooser = new JFileChooser();
        JFrame frame = new JFrame();

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        frame.setVisible(true);
        frame.toFront();
        frame.setVisible(false);

        frame.dispose();

        return (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION ? fileChooser.getSelectedFile() : null);
    }

    public void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
                byte[] bytesIn = new byte[4096];
                int read;
                while ((read = zipIn.read(bytesIn)) != -1) {
                    bos.write(bytesIn, 0, read);
                }
                bos.close();
            } else {
                File dir = new File(filePath);
                dir.mkdirs();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }

    public String getStringFromResource(ResourceLocation resource) {
        return new BufferedReader(new InputStreamReader(getInputStreamFromResource(resource), StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining(System.getProperty("line.separator")));
    }

    public InputStream getInputStreamFromResource(ResourceLocation resource) {
        try {
            return mc.getResourceManager().getResource(resource).getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    public void writeToFile(String content, File file) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readFromFile(File file) {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public String getRandomTitleLine() {
        String[] lines = {
                "Try death!",
                "Now on a computer near you!",
                "*Insert Dead Joke*",
                "KA is Overpowered",
                "The Return of Tenebrous!",
                "Hypixel: The Documentary.",
                "Brainless monkey moment.",
                "NCP , Verus , AAC",
                "The Grass is Greener on This Side",
                "Cheats: Not for Children Under the Age of 13.",
                "Bro...",
                "What the fuck?",
                "You have been banned from this server for 364d 23h 59m 59s!",
                "A staff member witnessed the use of cheats.",
                "The Water Fall Of Content!",
                "CRACKED BY xAnalPvP69x",
                "Novoline Ain't Got Nuthin on Me",
                "All's Well That Ends Well",
                "*Casual Reference to Your Mother*",
                "Yes, but no",
                "Freak.",
                "So when's the last time you showered huh?",
                "Ok.",
                "Now with more things to kill you!",
                "Now compatible with the IBM Model M.",
                "I Pity the Tools...",
                "The ban hammer has SPOKEN!",
                "So remember when I asked?",
                "Subscribe to CallumUncensored",
                "Pandas... yeah, cool animals right?",
                "Coming soon to a computer near you",
                "Dividing by zero!",
                "Now with EPIC BLUR AND GLOW!",
                "Press alt-f4",
                "I Pity the skidders.",
                "You ok bro?",
                "NOT THE BEES!!!",
                "Legend of Novoline",
                "Also try Terraria!",
                "Shut Up and Play!",
                "2: Electric Boogaloo",
                "Now with more shitcode",
                "Not skidded from Tenacity",
                "Sentinel caught you cheating (Anticheat)",
                "Now with more else-if chains!",
                "Cracked by coinful & kant",
                "Roy Hwang edition",
                "Ryan Davies edition",
                "مثل دسار ديك أسود",
                "ching chong",
                "アストルフォ抱き枕を送ってください",
                "Developed at 2710 English Ivy Ct. Longwood, Florida",
                "no autoblock?",
                "Do you like my ascii penis?",
                "Moist Man > You",
                "If it crashes, it's a user error",
                "UID: " + RandomStringUtils.random(100),
                "Your UID is too high for this.",
                "Panda says 'Hello User, thanks for your token!'",
                "Remember when Panda Meow'd on stream?",
                "Remember when Hypixel was fun to cheat on? I do.",
                "Remember when you weren't fat?",
                "Remember when your parents loved you?",
                "Remember when you was actually passing maths instead of sitting on this stupid fucking client? Yeah, I do too, the difference is, I'm actually fucking passing maths, unlike your dumb ass, which isn't, so go fucking study.",
                "RIP your chances at losing your virginity",
                "It's currently",
                "想象一下没有 pandaware 摇头",
                "Zambo78 绕过 Minecraft 色情性强奸绕过 100% 工作无诈骗无陷阱",
                "Dort Humor",
                "Developed in Taumatawhakatangihangakoauauotamateaturipukakapiki-maungahoronukupokaiwhenuakitnatahu",
                "TESTED IN LLANFAIRPWLLGWYNGYLLGOGERYCHWYRNDROBWLLLLANTYSILIOGOGOGOCH",
                "*Insert r/funny/ Joke*",
                String.valueOf(Math.E),
                String.valueOf(Math.PI),
                "---- Minecraft Crash Report ----",
                "https://www.youtube.com/watch?v=xvFZjo5PgG0",
                "https://youtu.be/7zpxgyG7eGk?t=29",
                "当您因 Boosting、Exploiting、Blacklisted Modifications 或 Watchdog Ban 而被禁止时,您将执行 适用于整个服务器的正常禁令。之后, 你将被禁止玩被视为竞技游戏的游戏。"
                };

        return lines[RandomUtils.nextInt(0, lines.length)];
    }
}
