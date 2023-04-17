package me.levansj01.verus.api.manager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import me.levansj01.verus.api.check.Check;
import me.levansj01.verus.api.check.InvalidCheckException;
import me.levansj01.verus.api.log.Ban;
import me.levansj01.verus.api.log.Log;
import me.levansj01.verus.api.log.StorageNotConnectedException;
import org.bukkit.entity.Player;

public interface VerusManager {

    default boolean isEnabled() {
        return false;
    }

    default boolean isConnected() {
        return false;
    }

    default List<Check> getChecks() {
        return Collections.emptyList();
    }

    default Optional<Check> getCheck(String type, String subType) {
        return Optional.empty();
    }

    default boolean isValid(Check check) {
        return false;
    }

    default boolean isAlert(Check check) throws InvalidCheckException {
        return false;
    }

    @Deprecated
    default boolean isBan(Check check) throws InvalidCheckException {
        return isPunish(check);
    }

    default boolean isPunish(Check check) throws InvalidCheckException {
        return false;
    }

    default OptionalInt getPunishViolations(Check check) throws InvalidCheckException {
        return OptionalInt.empty();
    }

    default boolean setAlert(Check check, boolean enabled) throws InvalidCheckException {
        return false;
    }

    @Deprecated
    default boolean setBan(Check check, boolean enabled) throws InvalidCheckException {
        return setPunish(check, enabled);
    }

    default boolean setPunish(Check check, boolean enabled) throws InvalidCheckException {
        return false;
    }

    default int getViolations(Player player, Check check) throws InvalidCheckException, DataNotFoundException {
        throw new DataNotFoundException(player.getUniqueId());
    }

    default void resetViolations(Player player, Check check, boolean minimize) throws InvalidCheckException, DataNotFoundException {
        throw new DataNotFoundException(player.getUniqueId());
    }

    default int getTotalViolations(Player player) throws DataNotFoundException {
        throw new DataNotFoundException(player.getUniqueId());
    }

    default void resetViolations(Player player, boolean minimize) throws DataNotFoundException {
        throw new DataNotFoundException(player.getUniqueId());
    }

    default boolean isAlerts(Player player) throws DataNotFoundException {
        throw new DataNotFoundException(player.getUniqueId());
    }

    default void fetchLogs(UUID uuid, int limit, Consumer<Iterable<Log>> consumer) throws StorageNotConnectedException {
        throw new StorageNotConnectedException();
    }

    default void fetchBans(UUID uuid, int limit, Consumer<Iterable<Ban>> consumer) throws StorageNotConnectedException {
        throw new StorageNotConnectedException();
    }

    default boolean isGameFrozen(Player player, TimeUnit unit, long time) throws DataNotFoundException {
        throw new DataNotFoundException(player.getUniqueId());
    }

    default Optional<String> getBrand(Player player) throws DataNotFoundException {
        throw new DataNotFoundException(player.getUniqueId());
    }

    default int getTransactionPing(Player player) throws DataNotFoundException {
        throw new DataNotFoundException(player.getUniqueId());
    }

    default int getKeepAlivePing(Player player) throws DataNotFoundException {
        throw new DataNotFoundException(player.getUniqueId());
    }
}