/* November.lol © 2023 */
package lol.november.scripting;

import static javax.script.ScriptContext.ENGINE_SCOPE;
import static lol.november.scripting.ScriptManager.API_VERSION;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.script.*;
import lol.november.November;
import lol.november.scripting.wrapper.module.LuaModule;
import lol.november.utility.fs.FileUtils;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.script.LuaScriptEngine;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Getter
@Log4j2
public class Script {

  private static final String NAME_HEADER = "--name:";

  private final LuaScriptEngine scriptEngine;

  private final File file;
  private final String content;
  private final String name;

  private final List<LuaModule> modules = new ArrayList<>();

  public Script(File file) {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("luaj");
    if (!(engine instanceof LuaScriptEngine luaScriptEngine)) {
      throw new RuntimeException("Lua script engine could not be initialized");
    }

    scriptEngine = luaScriptEngine;
    populateEngine();

    this.file = file;

    content = FileUtils.readFile(file);
    if (content == null || content.isEmpty()) throw new RuntimeException(
      "Script needs to have a name header!"
    );

    String discoveredName = null;
    for (String line : content.split("\n")) {
      if (line.startsWith(NAME_HEADER)) {
        discoveredName = line.substring(NAME_HEADER.length()).trim();
        break;
      }
    }

    if (discoveredName == null) throw new RuntimeException(
      "Script needs to have a name header!"
    );

    name = discoveredName;
  }

  public void load() {
    try {
      scriptEngine.eval(content);
    } catch (ScriptException e) {
      log.error("Failed to evaluate script {}", name);
      e.printStackTrace();
    }
  }

  public void unload() {
    for (LuaModule luaModule : getModules()) {
      November.instance().modules().removeLuaModule(luaModule);
    }
  }

  private void populateEngine() {
    Bindings bindings = new SimpleBindings();
    bindings.put("apiVersion", API_VERSION);
    scriptEngine.setBindings(bindings, ENGINE_SCOPE);

    scriptEngine.put(
      "createModule",
      new LibFunction() {
        @Override
        public LuaValue call(
          LuaValue name,
          LuaValue description,
          LuaValue category
        ) {
          if (
            !name.isstring() || !description.isstring() || !category.isstring()
          ) {
            return LuaValue.error(
              "name, description, and category need to be strings!"
            );
          }

          LuaModule luaModule = new LuaModule(
            name.tojstring(),
            description.tojstring(),
            category.tojstring()
          );
          modules.add(luaModule);

          November.instance().modules().addLuaModule(luaModule);
          return CoerceJavaToLua.coerce(luaModule);
        }
      }
    );
  }
}
