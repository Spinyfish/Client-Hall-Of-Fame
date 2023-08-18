package me.jinthium.straight.impl.ui;

import best.azura.irc.utils.RandomUtil;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mojang.util.UUIDTypeAdapter;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import me.jinthium.straight.api.ms.Microsoft2OpenAuth;
import me.jinthium.straight.api.shader.ShaderUtil;
import me.jinthium.straight.api.util.Util;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.ui.components.Button;
import me.jinthium.straight.impl.ui.components.altmanager.AddAltGui;
import me.jinthium.straight.impl.ui.components.altmanager.Alt;
import me.jinthium.straight.impl.utils.Scroll;
import me.jinthium.straight.impl.utils.math.MathUtils;
import me.jinthium.straight.impl.utils.render.RenderUtil;
import me.jinthium.straight.impl.utils.render.TaskUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.Session;
import org.apache.commons.lang3.RandomUtils;
import org.json.JSONObject;
import org.lwjglx.input.Mouse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class AltManager extends GuiScreen implements Util {

    private final ShaderUtil shaderUtil = new ShaderUtil("backgroundShader");
    private final File altsFile = new File(Client.INSTANCE.getClientDir(), "alts.txt"), cookieAlts = new File(Client.INSTANCE.getClientDir(), "cookiealts.txt");
    private long initTime;
    private boolean canBeDragged;
    public String mode = "Email";
    public final CopyOnWriteArrayList<Alt> alts = new CopyOnWriteArrayList<>();
    private final Scroll scroll = new Scroll();

    @Override
    public void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        switch (button.id) {
            case 0 -> {
                if (mode.equals("Cookie")) {
                    try {
                        TaskUtil.INSTANCE.sendNotification(TaskUtil.MessageType.INFO, "Alt Manager", "Check your fucking clipboard :)");
                        Microsoft2OpenAuth microsoft2OpenAuth = new Microsoft2OpenAuth();
                        MicrosoftAuthResult result = microsoft2OpenAuth.getAccessToken();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return;
                }
                mc.displayGuiScreen(new AddAltGui(this));
            }
            case 1 -> alts.remove(alts.size() - 1);
            case 2 -> {
                if (mode.equals("Cookie"))
                    mode = "Email";
                else
                    mode = "Cookie";
            }
            case 3 -> mc.session = new Session(String.format("Straight_%s", RandomUtils.nextInt(10000, 99999)), "", "", "cracked");
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//        this.drawDefaultBackground();

        ScaledResolution scaledResolution = ScaledResolution.fetchResolution(mc);
        shaderUtil.init();
        setupUniforms(initTime);
        ShaderUtil.drawQuads();
        shaderUtil.unload();

        normalFont22.drawStringWithShadow(String.format("Current Mode: %s", mode), scaledResolution.getScaledWidth() - 85
                - normalFont22.getStringWidth(String.format("Current Mode: %s", mode)), 6, -1);

        normalFont22.drawStringWithShadow(mc.getSession().username, 4, 4, -1);
        Gui.drawRect2(5, 5 + normalFont22.getHeight(), scaledResolution.getScaledWidth() - 90, scaledResolution.getScaledHeight() - 20, 0x90000000);
        RenderUtil.scissor(5, (5 + normalFont22.getHeight()), scaledResolution.getScaledWidth() - 90, scaledResolution.getScaledHeight() - 20, () -> {
        float y = 5 + normalFont22.getHeight();
        scroll.onScroll(25);
        for(Alt alt : alts){
            alt.renderAlt(mouseX, mouseY, 5, (float)(y + MathUtils.roundToHalf(scroll.getScroll())));
            y += normalFont22.getHeight() + 71;
            canBeDragged = y > scaledResolution.getScaledHeight() - 20;
        }
    });

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() {
        alts.forEach(alt -> {
            if(alt.head == null){
                alt.getHead();
            }
        });
        super.updateScreen();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        alts.forEach(alt -> {
            if(alt.isHovered(mouseX, mouseY)){
                alt.login();
            }
        });
    }

    public void initGui() {
        ScaledResolution scaledResolution = ScaledResolution.fetchResolution(mc);
        initTime = System.currentTimeMillis();
        this.buttonList.add(new Button(0, scaledResolution.getScaledWidth() - 80, 6, 75, 40, "Add Alt"));
        this.buttonList.add(new Button(1, scaledResolution.getScaledWidth() - 80, 56, 75, 40, "Remove Alt"));
        this.buttonList.add(new Button(2, scaledResolution.getScaledWidth() - 80, 106, 75, 40, "Switch Mode"));
        this.buttonList.add(new Button(3, scaledResolution.getScaledWidth() - 80, 156, 75, 40, "Gen Cracked"));
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        scroll.setRawScroll(0);
        buttonList.forEach(button -> button.animation.reset());
        super.onGuiClosed();
    }

    public void setupUniforms(long initTime){
        shaderUtil.setUniformf("time", (System.currentTimeMillis() - initTime) / 1000f);
        shaderUtil.setUniformf("resolution", mc.displayWidth, mc.displayHeight);
    }

    public void saveAlts(){
        try{
            if (!altsFile.exists()) {
                if (altsFile.getParentFile().mkdirs()) {
                    altsFile.createNewFile();
                }
            }

            FileWriter fileWriter = new FileWriter(altsFile.getAbsoluteFile());
            alts.forEach(alt -> {
                if(alt.getAltType() == Alt.ALT_TYPE.COOKIE)
                    return;

                try {
                    fileWriter.append(String.format("%s:%s:%s\n", alt.getName(), alt.getEmail(), alt.getPassword()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            fileWriter.close();

            //Files.write(altsFile.toPath(), new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create().toJson(alts.toArray(new Alt[0])).getBytes(StandardCharsets.UTF_8));
        }catch (IOException io){
            io.printStackTrace();
        }

        try{
            if (!cookieAlts.exists()) {
                if (cookieAlts.getParentFile().mkdirs()) {
                    cookieAlts.createNewFile();
                }
            }

            FileWriter fileWriter = new FileWriter(cookieAlts.getAbsoluteFile());
            alts.forEach(alt -> {
                if(alt.getAltType() == Alt.ALT_TYPE.EMAIL_PASS)
                    return;

                try {
                    fileWriter.append(String.format("%s:%s:%s\n", alt.getName(), alt.getUuid(), alt.getAccessToken()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            fileWriter.close();

            //Files.write(altsFile.toPath(), new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create().toJson(alts.toArray(new Alt[0])).getBytes(StandardCharsets.UTF_8));
        }catch (IOException io){
            io.printStackTrace();
        }
    }

    public void loadAlts(){
        try{
            if(altsFile.exists()){
                Scanner scanner = new Scanner(altsFile.getAbsoluteFile());

                while(scanner.hasNextLine()){
                    String data = scanner.nextLine();
                    if(data.contains(":")){
                        Alt alt = new Alt(data.split(":")[1], data.split(":")[2].split(" ")[0], Alt.ALT_TYPE.EMAIL_PASS);
                        alt.setName(data.split(":")[0]);
                        alt.setUuid(getUUID(alt.getName()));
                        this.alts.add(alt);
                    }
                }
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }

        try{
            if(cookieAlts.exists()){
                Scanner scanner = new Scanner(cookieAlts.getAbsoluteFile());

                while(scanner.hasNextLine()){
                    String data = scanner.nextLine();
                    if(data.contains(":")){
                        String[] currData = data.split(":");
                        this.alts.add(new Alt(currData[0], currData[1], currData[2].split(" ")[0], Alt.ALT_TYPE.COOKIE));
                    }
                }

            }
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public static String getUUID(String playerName) {
        String uuid = null;
        HttpResponse<String> response;
        try {
            response = Unirest.get("https://api.mojang.com/users/profiles/minecraft/" + playerName).asString();
            JSONObject jsonResponse = new JSONObject(response.getBody());
            uuid = jsonResponse.get("id").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uuid;
    }
}
