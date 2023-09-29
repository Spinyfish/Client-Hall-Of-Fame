package wtf.automn.gui.alt.management;

import com.google.gson.annotations.Expose;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import wtf.automn.gui.alt.utils.MicrosoftLoginWindow;
import wtf.automn.utils.io.JsonUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.UUID;

public class Account {

  @Expose
  private String uuid = "None";
  @Expose
  private String owner = "None";
  @Expose
  private String token = "None";
  @Expose
  private String lastUpdated = "Never";
  @Expose
  private Type type = Type.MICROSOFT;

  private ResourceLocation headTexture = null;

  public Account(Session s) {
    this.uuid = s.getPlayerID();
    this.owner = s.getUsername();
    this.token = s.getToken();

    this.writeImage(this.owner);
  }

  public Account(String uuid, File dir) {
    Account fromJsonAcc = loadFromFile(dir, uuid);
    if (fromJsonAcc == null) return;
    this.uuid = fromJsonAcc.uuid;
    this.owner = fromJsonAcc.owner;
    this.token = fromJsonAcc.token;
    this.lastUpdated = fromJsonAcc.lastUpdated;
    this.type = fromJsonAcc.type;

    this.writeImage(this.owner);
  }

  public void writeToFile(File dir) {
    JsonUtil.writeObjectToFile(this, new File(dir, uuid + ".json"));
  }

  public Account loadFromFile(File dir, String uuid) {
    return JsonUtil.getObject(Account.class, new File(dir, uuid + ".json"));
  }

  public UUID getUUID() {
    return UUID.fromString(uuid);
  }

  public String getUUIDString() {
    return uuid;
  }

  public String getOwner() {
    return owner;
  }

  public String getToken() {
    return token;
  }

  public ResourceLocation getHeadTexture() {
    return headTexture;
  }

  public String getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(String lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public String getTypeString() {
    return type.toString().substring(0, 1).toUpperCase() + type.toString().substring(1).toLowerCase();
  }

  public void login() {
    MicrosoftLoginWindow.loginWithToken(token, uuid, owner);
  }

  private boolean writeImage(String name) {
    try {
      String url = "https://minotar.net/helm/" + name + "/16.png";
      BufferedImage img = ImageIO.read(new URL(url));
      DynamicTexture texture = new DynamicTexture(img);
      ResourceLocation res = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("", texture);
      this.headTexture = res;
      return true;
    } catch (Exception e) {
      this.headTexture = new ResourceLocation("squid/gui/altmanager/empty.png");
      return false;
    }
  }

  public enum Type {
    MOJANG, MICROSOFT
  }

}
