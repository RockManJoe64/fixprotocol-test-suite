package org.fixprotocol.test.script;

import java.util.List;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import org.junit.Test;

public class ListAllEngines {

	@Test
	public void list() {
		ScriptEngineManager manager = new ScriptEngineManager();
		List<ScriptEngineFactory> factories = manager.getEngineFactories();
		for (ScriptEngineFactory factory : factories) {
			System.out.println(String.format("Engine: %s v%s; %s v%s",
					factory.getEngineName(),
					factory.getEngineVersion(),
					factory.getLanguageName(),
					factory.getLanguageVersion()));
			List<String> extensions = factory.getExtensions();
			System.out.println("Extensions:");
			print(extensions);

			List<String> names = factory.getNames();
			System.out.println("Names:");
			print(names);

			List<String> mimeTypes = factory.getMimeTypes();
			System.out.println("MIME Types:");
			print(mimeTypes);
		}
	}

	private void print(List<String> list) {
		for (String s : list) {
			System.out.println("\t" + s);
		}
	}

}
