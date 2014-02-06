package org.fixprotocol.test.fix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fixprotocol.test.ASCII;

import quickfix.Message;

/**
 * Immutable class representing a FIX message.
 *
 * @author Jose
 *
 */
public class FIXMessage implements Comparable<FIXMessage> {

	public static final FIXMessage UNDEFINED = new FIXMessage();

	private ArrayList<FIXField> fields = new ArrayList<FIXField>();
	private Set<String> fieldSet = new HashSet<String>();
	private Set<Integer> tagSet = new HashSet<Integer>();

	private ArrayList<FIXField> sortedByField;
	private ArrayList<FIXField> sortedByTag;

	public static String normalize(String fixMsg) {
		return fixMsg.trim().replaceAll("\r|\n", ASCII.PIPE)
				.replaceAll(ASCII.PIPE, ASCII.SOH);
	}

	public static FIXMessage convert(Message message) {
		return null;
	}

	public static Message convert(FIXMessage fixmessage) {
		return null;
	}

	private final class FIXFieldTagComparator implements Comparator<FIXField> {
		@Override
		public int compare(FIXField o1, FIXField o2) {
			return o1.getTag() - o2.getTag();
		}
	}

	private final class FIXFieldNameComparator implements Comparator<FIXField> {
		@Override
		public int compare(FIXField o1, FIXField o2) {
			return o1.getName().compareTo(o2.getName());
		}
	}

	private final class FIXFieldComparator implements Comparator<FIXField> {
		@Override
		public int compare(FIXField o1, FIXField o2) {
			return o1.compareTo(o2);
		}
	}

	public FIXMessage() {
		sortedByField = new ArrayList<FIXField>();
		sortedByTag = new ArrayList<FIXField>();
	}

	public FIXMessage(List<FIXField> fieldList) {
		initByList(fieldList);
	}

	public FIXMessage(String fixMsg) {
		String normalized = normalize(fixMsg);
		String[] tagValPairs = normalized.split(ASCII.SOH);
		ArrayList<FIXField> list = new ArrayList<FIXField>();
		for (String s : tagValPairs) {
			String[] tagVal = s.split("=");
			int tag = Integer.valueOf(tagVal[0]);
			String value = tagVal[1];
			list.add(new FIXField(tag, value));
		}
		initByList(list);
	}

	private void initByList(List<FIXField> fieldList) {
		for (FIXField field : fieldList) {
			fields.add(field);
			fieldSet.add(field.getName());
			tagSet.add(field.getTag());
		}

		sortedByField = new ArrayList<FIXField>(fields);
		Collections.sort(sortedByField, new FIXFieldNameComparator());

		sortedByTag = new ArrayList<FIXField>(fields);
		Collections.sort(sortedByTag, new FIXFieldTagComparator());
	}

	@Override
	public String toString() {
		String msg = "";
		for (FIXField field : fields) {
			msg += field.fix() + ASCII.SOH;
		}
		return msg;
	}

	/*
	 * Methods for use by a javascript
	 */

	public boolean has(String field) {
		return fieldSet.contains(field);
	}

	public boolean has(FIXField field) {
		int result = Collections.binarySearch(sortedByTag, field,
				new FIXFieldComparator());

		return result >= 0;
	}

	public FIXField get(String field) {
		int result = Collections.binarySearch(sortedByField, new FIXField(
				field, FIXField.TAG_ZERO, FIXField.UNDEFINED),
				new FIXFieldNameComparator());

		if (result >= 0)
			return sortedByField.get(result);

		return FIXField.NULL_FIELD;
	}

	public FIXField get(int tag) {
		int result = Collections.binarySearch(sortedByTag, new FIXField(
				FIXField.UNDEFINED, tag, FIXField.UNDEFINED),
				new FIXFieldTagComparator());

		if (result >= 0)
			return sortedByTag.get(result);

		return FIXField.NULL_FIELD;
	}

	public ArrayList<FIXField> getAll() {
		return fields;
	}

	/*
	 * Addition/removal methods
	 */

	public FIXMessage add(int tag, String value) {
		return add(FIXField.UNDEFINED, tag, value);
	}

	public FIXMessage add(String name, int tag, String value) {
		FIXField f = new FIXField(name, tag, value);
		this.fields.add(f);

		sortedByField.add(f);
		Collections.sort(sortedByField, new FIXFieldNameComparator());

		sortedByTag.add(f);
		Collections.sort(sortedByTag, new FIXFieldTagComparator());

		return this;
	}

	/*
	 * Comparison methods
	 */

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FIXMessage) {
			FIXMessage other = (FIXMessage) obj;
			int sizediff = this.fields.size() - other.fields.size();
			return sizediff == 0 && this.toString().equals(other.toString());
		}

		return false;
	}

	@Override
	public int compareTo(FIXMessage o) {
		int sizediff = this.fields.size() - o.fields.size();
		if (sizediff == 0) {
			return this.toString().compareTo(o.toString());
		}

		return sizediff;
	}

}
