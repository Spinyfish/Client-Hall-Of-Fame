package club.mineman.antigamingchair.check.impl.aimassist;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.checks.RotationCheck;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.event.PlayerUpdateRotationEvent;
import club.mineman.antigamingchair.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

public class AimAssistD extends RotationCheck {
    private final Deque<Double> notOverSpeeds;
    private final Deque<Double> overSpeeds;

    public AimAssistD(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData);
        this.notOverSpeeds = new LinkedList<>();
        this.overSpeeds = new LinkedList<>();
    }

    @Override
    public void handleCheck(final Player player, final PlayerUpdateRotationEvent event) {
        final Location from = event.getFrom();
        final Location to = event.getTo();
        if (from.getYaw() != to.getYaw()) {
            final double speed = Math.hypot(from.getYaw(), to.getYaw());
            if (MathUtil.isMouseOverEntity(player)) {
                this.overSpeeds.addLast(speed);
            } else {
                this.notOverSpeeds.addLast(speed);
            }
            final int total = this.overSpeeds.size() + this.notOverSpeeds.size();
            if (total == 1000) {
                double averageNotOver = 0.0;
                for (Double d : this.notOverSpeeds) {
                    averageNotOver += d;
                }
                averageNotOver /= this.notOverSpeeds.size();
                double averageOver = 0.0;
                for (Double d2 : this.overSpeeds) {
                    averageOver += d2;
                }
                averageOver /= this.overSpeeds.size();
                double vl = this.playerData.getCheckVl(this);
                if (averageNotOver > averageOver) {
                    vl += 1.25;
                    if (vl > 5.0) {
                        this.plugin.getAlertsManager().forceAlert("failed Aim Assist Check D (Development). " + averageNotOver + " :: " + averageOver + ". VL " + vl + ".", player);
                    }
                } else {
                    vl -= 0.4;
                }
                this.playerData.setCheckVl(vl, this);
                this.notOverSpeeds.clear();
                this.overSpeeds.clear();
            }
        }
    }
}
