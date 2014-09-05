package org.fixprotocol.test.script;

import java.io.File;
import java.util.Arrays;

import org.fixprotocol.fix.FIXField;
import org.fixprotocol.fix.FIXMessage;
import org.fixprotocol.script.JavaScriptEngineRhino;
import org.fixprotocol.script.ScenarioScriptEngine;
import org.junit.Test;


public class JavascriptExecutorTest {

	@Test
	public void callExampleScript() {
		FIXField[] fields = new FIXField[] {
				new FIXField("BeginString", 8, "FIX.4.4"),
				new FIXField("SenderCompID", 49, "JOSE"),
				new FIXField("TargetCompID", 56, "KCG"),
				new FIXField("Qty", 38, "1"),
				new FIXField("Price", 44, "11.94"),
				new FIXField("ExDestination", 100, "NYSE"),
				new FIXField("Symbol", 55, "KCG") };
		FIXMessage[] messages = new FIXMessage[] {
				new FIXMessage(Arrays.asList(fields)) };
		ScenarioScriptEngine ex = new JavaScriptEngineRhino();
		ex.run(new File("src/main/resources/scripts/Example.js"), messages);
	}

}
