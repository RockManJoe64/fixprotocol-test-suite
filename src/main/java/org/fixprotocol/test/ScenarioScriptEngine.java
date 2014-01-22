package org.fixprotocol.test;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface ScenarioScriptEngine {

	public abstract void setBindings(Map<String, Object> objBindings);

	public abstract void setBinding(String name, Object obj);

	public abstract void run(File script, FIXMessage... messages);

	public abstract void run(File script, List<FIXMessage> messages);

}