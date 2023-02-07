package org.crystalpvp.anticheat.data.location;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.StringJoiner;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

@Getter
@Setter
@AllArgsConstructor
public class CrystalLocation {


	private final long timestamp = System.currentTimeMillis();

	private String world;

	private double x;
	private double y;
	private double z;

	private float yaw;
	private float pitch;

	public CrystalLocation(double x, double y, double z) {
		this(x, y, z, 0.0F, 0.0F);
	}

	public CrystalLocation(String world, double x, double y, double z) {
		this(world, x, y, z, 0.0F, 0.0F);
	}

	public CrystalLocation(double x, double y, double z, float yaw, float pitch) {
		this("world", x, y, z, yaw, pitch);
	}

	public static CrystalLocation fromBukkitLocation(org.bukkit.Location location) {
		return new CrystalLocation(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(),
				location.getYaw(), location.getPitch());
	}

	public static CrystalLocation stringToLocation(String string) {
		String[] split = string.split(", ");

		double x = Double.parseDouble(split[0]);
		double y = Double.parseDouble(split[1]);
		double z = Double.parseDouble(split[2]);

		CrystalLocation crystalLocation = new CrystalLocation(x, y, z);

		if (split.length == 4) {
			crystalLocation.setWorld(split[3]);
		} else if (split.length >= 5) {
			crystalLocation.setYaw(Float.parseFloat(split[3]));
			crystalLocation.setPitch(Float.parseFloat(split[4]));

			if (split.length >= 6) {
				crystalLocation.setWorld(split[5]);
			}
		}

		return crystalLocation;
	}


	public static String locationToString(CrystalLocation loc) {
		StringJoiner joiner = new StringJoiner(", ");
		joiner.add(Double.toString(loc.getX()));
		joiner.add(Double.toString(loc.getY()));
		joiner.add(Double.toString(loc.getZ()));

		if (loc.getYaw() == 0.0f && loc.getPitch() == 0.0f) {
			if (loc.getWorld().equals("world")) {
				return joiner.toString();
			} else {
				joiner.add(loc.getWorld());
				return joiner.toString();
			}
		} else {
			joiner.add(Float.toString(loc.getYaw()));
			joiner.add(Float.toString(loc.getPitch()));

			if (loc.getWorld().equals("world")) {
				return joiner.toString();
			} else {
				joiner.add(loc.getWorld());
				return joiner.toString();
			}
		}
	}

	public org.bukkit.Location toBukkitLocation() {
		return new org.bukkit.Location(this.toBukkitWorld(), this.x, this.y, this.z, this.yaw, this.pitch);
	}

	public double getGroundDistanceTo(CrystalLocation crystalLocation) {
		return Math.sqrt(Math.pow(this.x - crystalLocation.x, 2) + Math.pow(this.z - crystalLocation.z, 2));
	}

	public double getDistanceTo(CrystalLocation crystalLocation) {
		return Math.sqrt(Math.pow(this.x - crystalLocation.x, 2) + Math.pow(this.y - crystalLocation.y, 2) + Math.pow(this.z - crystalLocation.z, 2));
	}

	public World toBukkitWorld() {
		if (this.world == null) {
			return Bukkit.getServer().getWorlds().get(0);
		} else {
			return Bukkit.getServer().getWorld(this.world);
		}
	}

	public static String getLocation(Player player) {
		return player.getWorld().getName() + ", " + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CrystalLocation)) {
			return false;
		}

		CrystalLocation crystalLocation = (CrystalLocation) obj;
		return crystalLocation.x == this.x && crystalLocation.y == this.y && crystalLocation.z == this.z
				&& crystalLocation.pitch == this.pitch && crystalLocation.yaw == this.yaw;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("x", this.x)
				.append("y", this.y)
				.append("z", this.z)
				.append("yaw", this.yaw)
				.append("pitch", this.pitch)
				.append("world", this.world)
				.append("timestamp", this.timestamp)
				.toString();
	}

}
