package org.fixprotocol.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericScriptEngineJRE implements ScenarioScriptEngine {

	static final Logger log = LoggerFactory.getLogger(GenericScriptEngineJRE.class);

	private ScriptEngine scriptEngine;
	private Bindings bindings;

	public GenericScriptEngineJRE() {
		this("javascript");
		String script = "/scripts/Default.js";
		try {
			scriptEngine.eval(new FileReader(script), this.bindings);
		} catch (FileNotFoundException e) {
			log.error("Could not find script " + script, e);
		} catch (ScriptException e) {
			log.error("An error occurred while executing script " + script, e);
		}
	}

	public GenericScriptEngineJRE(String scriptEngineName) {
		ScriptEngineManager manager = new ScriptEngineManager();
		scriptEngine = manager.getEngineByName(scriptEngineName);
		bindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put("engine", scriptEngine);
	}

	public void setBindings(Map<String, Object> objBindings) {
		bindings.putAll(objBindings);
	}

	public void setBinding(String name, Object obj) {
		bindings.put(name, obj);
	}

	public void run(File script, FIXMessage... messages) {
		run(script, Arrays.asList(messages));
	}

	public void run(File script, List<FIXMessage> messages) {
		for (FIXMessage fixMessage : messages) {
			bindings.put("template", fixMessage);
			try {
				scriptEngine.eval(new FileReader(script), this.bindings);
			} catch (FileNotFoundException e) {
				log.error("Could not find script " + script, e);
			} catch (ScriptException e) {
				log.error("An error occurred while executing script " + script, e);
			}
		}
	}

}
