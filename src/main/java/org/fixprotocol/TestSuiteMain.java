package org.fixprotocol;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestSuiteMain {

	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Usage: java com.lionsmith.fixprotocol.test.TestSuiteMain " +
					"<sessionConfig.ini> <messageprototypes.csv> <scenarioscript> [scenarioscript2] ...");
			System.exit(1);
		}

		String sessionfile = args[0];
		String prototypefile = args[1];
		List<String> scenarioscripts = new ArrayList<String>();
		for (int i = 2; i < args.length; i++) {
			scenarioscripts.add(args[i]);
		}

		System.getProperties().put("qfj.configuration", sessionfile);
		System.getProperties().put("prototype.file", prototypefile);
		System.getProperties().put("scripts", scenarioscripts);

		final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"/ApplicationContext.xml");
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				context.close();
			}
		}, "Shutdown"));
		context.registerShutdownHook();
		context.start();
	}

}
