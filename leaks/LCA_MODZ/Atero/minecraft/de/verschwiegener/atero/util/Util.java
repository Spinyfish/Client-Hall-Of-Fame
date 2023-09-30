package de.verschwiegener.atero.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;

import javax.imageio.ImageIO;

import com.darkmagician6.eventapi.events.callables.PlayerMoveEvent;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.mojang.authlib.GameProfile;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.design.font.Font;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.util.chat.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.event.ClickEvent;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class Util {

	static Minecraft mc = Minecraft.getMinecraft();

	/**
	 * Sorts the Modules after the Name length
	 *
	 * @param modules
	 */
	public static void sortModuleList(ArrayList<Module> modules, final Font font) {
		Collections.sort(modules, new Comparator<Module>() {

			@Override
			public int compare(Module o1, Module o2) {
				if (font.getFontrenderer().getStringWidth(o1.getExtraTag()) > font.getFontrenderer()
						.getStringWidth(o2.getExtraTag())) {
					return -1;
				}
				if (font.getFontrenderer().getStringWidth(o1.getExtraTag()) < font.getFontrenderer()
						.getStringWidth(o2.getExtraTag())) {
					return 1;
				}
				return 0;
			}
		});
	}

	/**
	 * @throws IOException Gets the IP for Router Reconnect
	 *                     https://stackoverflow.com/questions/2939218/getting-the-external-ip-address-in-java
	 */
	public static String getIpv4() throws IOException {
		URL whatismyip = new URL("https://ipv4.icanhazip.com/");
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			String ip = in.readLine();
			return ip;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String getIpv6() throws IOException {
		URL whatismyip = new URL("https://ipv6.icanhazip.com/");
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			String ip = in.readLine();
			return ip;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static int getColor(int red, int green, int blue, int opacity) {
		int color = MathHelper.clamp_int(opacity, 0, 255) << 24;
		color |= MathHelper.clamp_int(red, 0, 255) << 16;
		color |= MathHelper.clamp_int(green, 0, 255) << 8;
		color |= MathHelper.clamp_int(blue, 0, 255);
		return color;
	}

	public static GameProfile getGameProfileFromName(String name) {
		return TileEntitySkull.updateGameprofile(new GameProfile(null, name));
	}

	public static ResourceLocation getSkin(String name) {
		NetworkPlayerInfo networkinfo = new NetworkPlayerInfo(getGameProfileFromName(name));
		return networkinfo.getLocationSkin();
	}

	public static void setSpeed(double speed) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		double yaw = (double) player.rotationYaw;
		boolean isMoving = player.moveForward != 0.0F || player.moveStrafing != 0.0F;
		boolean isMovingForward = player.moveForward > 0.0F;
		boolean isMovingBackward = player.moveForward < 0.0F;
		boolean isMovingRight = player.moveStrafing > 0.0F;
		boolean isMovingLeft = player.moveStrafing < 0.0F;
		boolean isMovingSideways = isMovingLeft || isMovingRight;
		boolean isMovingStraight = isMovingForward || isMovingBackward;
		if (isMoving) {
			if (isMovingForward && !isMovingSideways) {
				yaw += 0.0D;
			} else if (isMovingBackward && !isMovingSideways) {
				yaw += 180.0D;
			} else if (isMovingForward && isMovingLeft) {
				yaw += 45.0D;
			} else if (isMovingForward) {
				yaw -= 45.0D;
			} else if (!isMovingStraight && isMovingLeft) {
				yaw += 90.0D;
			} else if (!isMovingStraight && isMovingRight) {
				yaw -= 90.0D;
			} else if (isMovingBackward && isMovingLeft) {
				yaw += 135.0D;
			} else if (isMovingBackward) {
				yaw -= 135.0D;
			}
			yaw = Math.toRadians(yaw);
			player.motionX = -Math.sin(yaw) * speed;
			player.motionZ = Math.cos(yaw) * speed;
		}

	}

	public static String getCLUsername() {
		return System.getProperty("clname", "none");
	}

	public static int getClientRole() {
		return Integer.parseInt(System.getProperty("clrole", "0"));
	}

	public static java.awt.Font getFontByName(final String name) {
		try {
			return java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, Management.class.getResourceAsStream(
					"/assets/minecraft/" + Management.instance.CLIENT_NAME.toLowerCase() + "/fonts/" + name + ".ttf"));
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static IntBuffer pixelBuffer;
	private static int[] pixelValues;

	public static void saveFramebuffer(Framebuffer frameBuffer) {
		int width = mc.displayWidth;
		int height = mc.displayHeight;
		if (OpenGlHelper.isFramebufferEnabled()) {
			width = frameBuffer.framebufferTextureWidth;
			height = frameBuffer.framebufferTextureHeight;
		}
		int targetCapacity = width * height;
		if (pixelBuffer == null || pixelBuffer.capacity() < targetCapacity) {
			pixelBuffer = BufferUtils.createIntBuffer(targetCapacity);
			pixelValues = new int[targetCapacity];
		}
		GL11.glPixelStorei(3333, 1);
		GL11.glPixelStorei(3317, 1);
		pixelBuffer.clear();
		if (OpenGlHelper.isFramebufferEnabled()) {
			GlStateManager.bindTexture(frameBuffer.framebufferTexture);
			GL11.glGetTexImage(3553, 0, 32993, 33639, pixelBuffer);
		} else {
			GL11.glReadPixels(0, 0, width, height, 32993, 33639, pixelBuffer);
		}
		pixelBuffer.get(pixelValues);
		TextureUtil.processPixelValues(pixelValues, width, height);
		int[] pixelCopy = new int[pixelValues.length];
		System.arraycopy(pixelValues, 0, pixelCopy, 0, pixelValues.length);

		int[] pixels = pixelCopy;
		String captureTime = (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date());
		BufferedImage image = null;
		if (OpenGlHelper.isFramebufferEnabled()) {
			image = new BufferedImage(frameBuffer.framebufferWidth, frameBuffer.framebufferHeight, 1);
			int diff = frameBuffer.framebufferTextureHeight - frameBuffer.framebufferHeight;
			for (int i = diff; i < frameBuffer.framebufferTextureHeight; i++) {
				for (int j = 0; j < frameBuffer.framebufferWidth; j++) {
					int pixel = pixels[i * frameBuffer.framebufferTextureWidth + j];
					image.setRGB(j, i - diff, pixel);
				}
			}
		} else {
			image = new BufferedImage(width, height, 1);
			image.setRGB(0, 0, width, height, pixels, 0, width);
		}
		File ssDir = new File("screenshots");
		File ssFile = new File("screenshots", captureTime + ".png");
		int iterator = 0;
		while (ssFile.exists()) {
			iterator++;
			ssFile = new File("screenshots", captureTime + "_" + iterator + ".png");
		}

		try {
			ssDir.mkdirs();
			ImageIO.write(image, "png", ssFile);
			IChatComponent ichatcomponent = new ChatComponentText(ssFile.getName());
			ichatcomponent.getChatStyle()
					.setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, ssFile.getAbsolutePath()));
			ichatcomponent.getChatStyle().setUnderlined(Boolean.valueOf(true));

			//ChatUtil.addIChatComponent(
			//new ChatComponentTranslation("screenshot.success", new Object[] { ichatcomponent }));
		} catch (IOException e) {
			e.printStackTrace();
			//ChatUtil.addIChatComponent(
			// new ChatComponentTranslation("screenshot.failure", new Object[] { e.getMessage() }));
		}
	}
	public static float toRadians(float value) {
		return (float) (value / 180.0F * Math.PI);
	}
	public static void setSpeed1(PlayerMoveEvent moveEvent, double moveSpeed) {
		setSpeed1(moveEvent, moveSpeed, mc.thePlayer.rotationYaw, mc.thePlayer.movementInput.moveStrafe, mc.thePlayer.movementInput.moveForward);
	}

	public static void setSpeed1(PlayerMoveEvent moveEvent, double moveSpeed, float pseudoYaw, double pseudoStrafe, double pseudoForward) {
		double forward = pseudoForward;
		double strafe = pseudoStrafe;
		float yaw = pseudoYaw;
		if (forward != 0.0D) {
			if (strafe > 0.0D) {
				yaw += ((forward > 0.0D) ? -45 : 45);
			} else if (strafe < 0.0D) {
				yaw += ((forward > 0.0D) ? 45 : -45);
			}
			strafe = 0.0D;
			if (forward > 0.0D) {
				forward = 1.0D;
			} else if (forward < 0.0D) {
				forward = -1.0D;
			}
		}
		if (strafe > 0.0D) {
			strafe = 1.0D;
		} else if (strafe < 0.0D) {
			strafe = -1.0D;
		}
		double mx = Math.cos(Math.toRadians((yaw + 90.0F)));
		double mz = Math.sin(Math.toRadians((yaw + 90.0F)));
		moveEvent.setX((forward * moveSpeed * mx + strafe * moveSpeed * mz));
		moveEvent.setZ((forward * moveSpeed * mz - strafe * moveSpeed * mx));
	}
	public static void drawRect(float g, float h, float i, float j, Color col2) {
	    GL11.glColor4f(col2.getRed() / 255.0F, col2.getGreen() / 255.0F, col2.getBlue() / 255.0F, col2.getAlpha() / 255.0F);

		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(2848);

		GL11.glPushMatrix();
		GL11.glBegin(7);
		GL11.glVertex2d(i, h);
		GL11.glVertex2d(g, h);
		GL11.glVertex2d(g, j);
		GL11.glVertex2d(i, j);
		GL11.glEnd();
		GL11.glPopMatrix();

		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glDisable(2848);
	}
}
