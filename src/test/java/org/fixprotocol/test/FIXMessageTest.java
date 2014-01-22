package org.fixprotocol.test;

import java.util.Arrays;
import java.util.Collections;

import org.fixprotocol.test.FIXField;
import org.fixprotocol.test.FIXMessage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class FIXMessageTest {

	private FIXMessage fixmsg;

	@Before
	public void before() {
		FIXField[] fields = new FIXField[] {
				new FIXField("BeginString", 8, "FIX.4.4"),
				new FIXField("SenderCompID", 49, "JOSE"),
				new FIXField("TargetCompID", 56, "KCG"),
				new FIXField("Qty", 38, "1"),
				new FIXField("Price", 44, "11.94"),
				new FIXField("ExDestination", 100, "NYSE"),
				new FIXField("Symbol", 55, "KCG") };
		fixmsg = new FIXMessage(Arrays.asList(fields));
	}

	@Test
	public void iterateThruFields() {
		FIXField[] fields = fixmsg.getAll().toArray(new FIXField[] {});
		Assert.assertEquals("Fields returned is not correct", 7, fields.length);
	}

	@Test
	public void binarySearchTest() {
		String[] list = { "B", "D", "F" };
		int res = Collections.binarySearch(Arrays.asList(list), "A");
		System.out.println("A=" + res);

		res = Collections.binarySearch(Arrays.asList(list), "B");
		System.out.println("B=" + res);

		res = Collections.binarySearch(Arrays.asList(list), "C");
		System.out.println("C=" + res);

		res = Collections.binarySearch(Arrays.asList(list), "G");
		System.out.println("G=" + res);
	}

}
