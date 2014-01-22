package org.fixprotocol.test;

import java.util.ArrayList;

public class Condition {

	private FIXMessage expected = new FIXMessage();
	private FIXMessage actual = new FIXMessage();

	public void expected(FIXMessage expected) {
		this.expected = expected;
	}

	public void actual(FIXMessage actual) {
		this.actual = actual;
	}

	public boolean isMet() {
		boolean met = true;
		ArrayList<FIXField> exp = expected.getAll();
		for (FIXField fixField : exp) {
			met = met && this.actual.has(fixField);
		}

		return met;
	}

	public FIXMessage diff() {
		ArrayList<FIXField> exp = expected.getAll();
		ArrayList<FIXField> diff = new ArrayList<FIXField>();
		for (FIXField fixField : exp) {
			diff.add(actual.get(fixField.getTag()));
		}

		return new FIXMessage(diff);
	}

}
