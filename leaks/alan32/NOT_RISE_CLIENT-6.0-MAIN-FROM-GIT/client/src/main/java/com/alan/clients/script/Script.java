package com.alan.clients.script;

import com.alan.clients.Client;
import com.alan.clients.script.util.ScriptHandler;
import lombok.Data;
import org.apache.commons.io.FileUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.File;
import java.nio.charset.Charset;

/**
 * @author Strikeless
 * @since 14.05.2022
 */
@Data
public final class Script {

    private final String name, author, version, description;

    private String code;

    private final File sourceFile;

    private ScriptEngine engine;

    private ScriptHandler apiHandler;

    private boolean loaded;

    public Script(final String name, final String author, final String version, final String description,
                  final String code) {
        this(name, author, version, description, code, null);
    }


    public Script(final String name, final String author, final String version, final String description,
                  final File sourceFile) {
        this(name, author, version, description, null, sourceFile);
    }

    public Script(final String name, final String author, final String version, final String description,
                  final String code, final File sourceFile) {
        this.name = name;
        this.author = author;
        this.version = version;
        this.description = description;
        this.code = code;
        this.sourceFile = sourceFile;
    }

    /**
     * Loads the script by creating a new ScriptEngine for it, evaluating the code, and lastly notifying the script.
     *
     * @throws ScriptException if the script failed to load properly
     */
    public void load() throws ScriptException {
        try {
            // If the script is already loaded, make sure to unload it
            if (this.loaded) this.unload();

            if (this.sourceFile != null) code = FileUtils.readFileToString(sourceFile, (Charset) null);
            if (this.code == null) throw new ScriptException("Empty script");

            // Create a new ScriptEngine and handler for this script.
            // You could create these just once in the constructor but that would leave old junk after reloading.
            this.engine = Client.INSTANCE.getScriptManager().createEngine();
            this.apiHandler = new ScriptHandler();

            // Add some script-specific bindings, global bindings are managed in ScriptManager
            this.engine.put("script", apiHandler);

            // Evaluate the code thus loading the script.
            this.engine.eval(code);

            // Mark the script as loaded.
            this.loaded = true;

            // Notify the script that it got loaded.
            this.call("onLoad");
        } catch (final ScriptException ex) {
            // Throw the exception directly instead of wrapping it into another ScriptException
            throw ex;
        } catch (final Exception ex) {
            throw new ScriptException(ex);
        }
    }

    /**
     * Unloads the script by notifying it and destroying its ScriptEngine.
     * This method will always destroy the engine and api even if the script fails to unload properly.
     *
     * @throws ScriptException if the script fails during execution of the onUnload function
     */
    public void unload() throws ScriptException {
        try {
            // Notify the script that it's about to be unloaded.
            this.call("onUnload");
        } catch (final Exception ex) {
            throw new ScriptException(ex);
        } finally {
            // Destroy the existing ScriptEngine and API to prevent any leftover junk,
            // we will be making new ones when we load again.
            Client.INSTANCE.getStandardClickGUI().rebuildModuleCache();
            this.engine = null;
            this.apiHandler = null;

            this.loaded = false;
        }
    }

    /**
     * Reloads the script by first unloading it and then loading it again.
     * If the script is not loaded, this will only load it.
     *
     * @throws ScriptException if the unloading or loading of the script fails
     */
    public void reload() throws ScriptException {
        // Don't try to unload if the script has never been loaded previously
        if (this.loaded) this.unload();
        this.load();
    }

    private void call(final String functionName, final Object... parameters) {
        this.apiHandler.call(functionName, parameters);
    }
}
