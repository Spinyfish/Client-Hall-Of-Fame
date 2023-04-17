package club.mineman.antigamingchair.check.checks;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.AbstractCheck;
import club.mineman.antigamingchair.check.ICheck;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.event.PlayerUpdateRotationEvent;

public abstract class RotationCheck extends AbstractCheck<PlayerUpdateRotationEvent> {
    public RotationCheck(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData, PlayerUpdateRotationEvent.class);
    }
}
