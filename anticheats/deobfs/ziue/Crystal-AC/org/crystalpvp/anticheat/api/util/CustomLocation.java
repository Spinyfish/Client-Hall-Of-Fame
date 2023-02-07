package org.crystalpvp.anticheat.api.util;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.beans.ConstructorProperties;
import java.util.StringJoiner;

@Getter
@Setter
public class CustomLocation {
	@Getter
	private final long timestamp = System.currentTimeMillis();
	@Getter
	private String world;
	@Getter
	private double x;
	private double y;
	private double z;
	@Getter
	private float yaw;
	private float pitch;

	public CustomLocation(double x, double y, double z) {
		this(x, y, z, 0.0f, 0.0f);
	}

	public CustomLocation(String world, double x, double y, double z) {
		this(world, x, y, z, 0.0f, 0.0f);
	}

	public CustomLocation(double x, double y, double z, float yaw, float pitch) {
		this("world", x, y, z, yaw, pitch);
	}

	@ConstructorProperties(value = {"world", "x", "y", "z", "yaw", "pitch"})
	public CustomLocation(String world, double x, double y, double z, float yaw, float pitch) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public static CustomLocation fromBukkitLocation(Location location) {
		return new CustomLocation(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}

	public static CustomLocation stringToLocation(String string) {
		String[] split = string.split(", ");
		double x = Double.parseDouble(split[0]);
		double y = Double.parseDouble(split[1]);
		double z = Double.parseDouble(split[2]);
		CustomLocation customLocation = new CustomLocation(x, y, z);
		if (split.length == 4) {
			customLocation.setWorld(split[3]);
		} else if (split.length >= 5) {
			customLocation.setYaw(Float.parseFloat(split[3]));
			customLocation.setPitch(Float.parseFloat(split[4]));
			if (split.length >= 6) {
				customLocation.setWorld(split[5]);
			}
		}
		return customLocation;
	}

	public static String locationToString(CustomLocation loc) {
		StringJoiner joiner = new StringJoiner(", ");
		joiner.add(Double.toString(loc.getX()));
		joiner.add(Double.toString(loc.getY()));
		joiner.add(Double.toString(loc.getZ()));
		if (loc.getYaw() == 0.0f && loc.getPitch() == 0.0f) {
			if (loc.getWorld().equals("world")) {
				return joiner.toString();
			}
			joiner.add(loc.getWorld());
			return joiner.toString();
		}
		joiner.add(Float.toString(loc.getYaw()));
		joiner.add(Float.toString(loc.getPitch()));
		if (loc.getWorld().equals("world")) {
			return joiner.toString();
		}
		joiner.add(loc.getWorld());
		return joiner.toString();
	}

	public Location toBukkitLocation() {
		return new Location(this.toBukkitWorld(), this.x, this.y, this.z, this.yaw, this.pitch);
	}

	public double getGroundDistanceTo(CustomLocation location) {
		return Math.sqrt(Math.pow(this.x - location.x, 2.0) + Math.pow(this.z - location.z, 2.0));
	}

	public double getDistanceTo(CustomLocation location) {
		return Math.sqrt(Math.pow(this.x - location.x, 2.0) + Math.pow(this.y - location.y, 2.0) + Math.pow(this.z - location.z, 2.0));
	}

	public World toBukkitWorld() {
		if (this.world == null) {
			return (World) Bukkit.getServer().getWorlds().get(0);
		}
		return Bukkit.getServer().getWorld(this.world);
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof CustomLocation)) {
			return false;
		}
		CustomLocation location = (CustomLocation) obj;
		return location.x == this.x && location.y == this.y && location.z == this.z && location.pitch == this.pitch && location.yaw == this.yaw;
	}

	public String toString() {
		return new ToStringBuilder((Object) this).append("x", this.x).append("y", this.y).append("z", this.z).append("yaw", this.yaw).append("pitch", this.pitch).append("world", (Object) this.world).append("timestamp", this.timestamp).toString();
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public String getWorld() {
		return this.world;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	public double getX() {
		return this.x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return this.y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return this.z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public float getYaw() {
		return this.yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return this.pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
}

