package org.fixprotocol.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;

public class JavaScriptEngineRhino implements ScenarioScriptEngine {

	public void setBindings(Map<String, Object> objBindings) {

	}

	public void setBinding(String name, Object obj) {

	}

	public void run(File script, FIXMessage... messages) {
		run(script, new ArrayList<FIXMessage>(Arrays.asList(messages)));
	}

	public void run(File script, List<FIXMessage> messages) {
		Context context = Context.enter();
		try {
			// Scriptable scope = context.initStandardObjects();
			Scriptable scope = new ImporterTopLevel(context);

			Object jso = Context.javaToJS(messages, scope);
			scope.put("fixmsgs", scope, jso);

			Object jsOut = Context.javaToJS(System.out, scope);
			scope.put("out", scope, jsOut);

			context.evaluateReader(scope, new FileReader(script),
					script.getName(), 1, null);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			Context.exit();
		}
	}

}
