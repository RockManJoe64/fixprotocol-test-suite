package org.fixprotocol.test.js;

import java.io.FileReader;
import java.util.Date;
import java.util.Map;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class ScriptRunner {

    public static void main(String[] args) throws Exception {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");

        engine.eval(new FileReader("src/main/javascript/Example.js"));

        Compilable compilingEngine = (Compilable) engine;
        CompiledScript script = compilingEngine.compile(new FileReader(
                "src/main/javascript/Function.js"));

        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("date", new Date());
        bindings.put("out", System.out);
        for (Map.Entry me : bindings.entrySet()) {
            System.out.printf("%s: %s\n", me.getKey(),
                    String.valueOf(me.getValue()));
        }
        script.eval(bindings);

        Invocable invocable = (Invocable) script.getEngine();
        invocable.invokeFunction("sayhello", "Jose");
    }

}
