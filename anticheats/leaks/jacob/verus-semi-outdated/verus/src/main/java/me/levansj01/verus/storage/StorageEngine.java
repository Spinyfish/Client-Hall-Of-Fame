package me.levansj01.verus.storage;

import me.levansj01.verus.storage.config.*;
import me.levansj01.verus.storage.database.*;
import me.levansj01.launcher.*;

public class StorageEngine {
    private VerusConfiguration verusConfig;
    private static StorageEngine engine;
    private Database database;
    private DatabaseType type;

    public static StorageEngine getInstance() {
        StorageEngine engine;
        if (StorageEngine.engine == null) {
            engine = (StorageEngine.engine = new StorageEngine());

        } else {
            engine = StorageEngine.engine;
        }
        return engine;
    }

    public Database getDatabase() {
        return this.database;
    }

    public VerusConfiguration getVerusConfig() {
        return this.verusConfig;
    }

    public void start() {
        if (this.verusConfig.isMongoEnabled()) {
            this.type = DatabaseType.MONGO;

        } else if (this.verusConfig.isSqlEnabled()) {
            this.type = DatabaseType.SQL;
        }
        if (this.type == null) {
            VerusLauncher.getPlugin().getLogger().warning("No storage method found, please enable database storage in the configuration to access important features");

        } else {
            this.database = this.type.create();
            VerusLauncher.getPlugin().getLogger().info("Using storage method " + this.type.name() + " handled by " + this.database.getClass().getName());
            this.database.start();
            this.database.connect(this.verusConfig);
        }
    }

    public void stop() {
        if (this.database != null) {
            this.database.stop();
        }
    }

    public void startConfig() {
        (this.verusConfig = new VerusConfiguration()).enable();
    }

    public void stopConfig() {
        if (this.verusConfig != null) {
            this.verusConfig.disable();
        }
    }

    public DatabaseType getType() {
        return this.type;
    }

    public boolean isConnected() {
        return this.database != null && this.database.isConnected();
    }
}
