package me.levansj01.verus.api.manager.impl;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import me.levansj01.verus.api.check.Check;
import me.levansj01.verus.api.check.InvalidCheckException;
import me.levansj01.verus.api.log.Ban;
import me.levansj01.verus.api.log.Log;
import me.levansj01.verus.api.log.StorageNotConnectedException;
import me.levansj01.verus.api.manager.DataNotFoundException;
import me.levansj01.verus.api.manager.VerusManager;
import org.bukkit.entity.Player;

public class WrappedManager implements VerusManager {

    private final Supplier<VerusManager> supplier;

    public WrappedManager(Supplier<VerusManager> supplier) {
        this.supplier = supplier;
    }

    public boolean isEnabled() {
        return this.supplier.get().isEnabled();
    }

    public boolean isConnected() {
        return this.supplier.get().isConnected();
    }

    public List<Check> getChecks() {
        return this.supplier.get().getChecks();
    }

    public Optional<Check> getCheck(String type, String subType) {
        return this.supplier.get().getCheck(type, subType);
    }

    public boolean isValid(Check check) {
        return this.supplier.get().isValid(check);
    }

    public boolean isAlert(Check check) throws InvalidCheckException {
        return this.supplier.get().isAlert(check);
    }

    public boolean isPunish(Check check) throws InvalidCheckException {
        return this.supplier.get().isPunish(check);
    }

    public OptionalInt getPunishViolations(Check check) throws InvalidCheckException {
        return this.supplier.get().getPunishViolations(check);
    }

    public boolean setAlert(Check check, boolean enabled) throws InvalidCheckException {
        return this.supplier.get().setAlert(check, enabled);
    }

    public boolean setPunish(Check check, boolean enabled) throws InvalidCheckException {
        return this.supplier.get().setPunish(check, enabled);
    }

    public int getViolations(Player player, Check check) throws InvalidCheckException, DataNotFoundException {
        return this.supplier.get().getViolations(player, check);
    }

    public void resetViolations(Player player, Check check, boolean minimize) throws InvalidCheckException, DataNotFoundException {
        this.supplier.get().resetViolations(player, check, minimize);
    }

    public int getTotalViolations(Player player) throws DataNotFoundException {
        return this.supplier.get().getTotalViolations(player);
    }

    public void resetViolations(Player player, boolean minimize) throws DataNotFoundException {
        this.supplier.get().resetViolations(player, minimize);
    }

    public boolean isAlerts(Player player) throws DataNotFoundException {
        return this.supplier.get().isAlerts(player);
    }

    public void fetchLogs(UUID uuid, int limit, Consumer<Iterable<Log>> consumer) throws StorageNotConnectedException {
        this.supplier.get().fetchLogs(uuid, limit, consumer);
    }

    public void fetchBans(UUID uuid, int limit, Consumer<Iterable<Ban>> consumer) throws StorageNotConnectedException {
        this.supplier.get().fetchBans(uuid, limit, consumer);
    }

    public boolean isGameFrozen(Player player, TimeUnit unit, long time) throws DataNotFoundException {
        return this.supplier.get().isGameFrozen(player, unit, time);
    }

    public Optional<String> getBrand(Player player) throws DataNotFoundException {
        return this.supplier.get().getBrand(player);
    }

    public int getTransactionPing(Player player) throws DataNotFoundException {
        return this.supplier.get().getTransactionPing(player);
    }

    public int getKeepAlivePing(Player player) throws DataNotFoundException {
        return this.supplier.get().getKeepAlivePing(player);
    }
}