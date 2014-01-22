package org.fixprotocol.test;

public class FIXField implements Comparable<FIXField>, Cloneable {

	public static final String UNDEFINED = "UNDEF";
	public static final int UNDEF_TAG = 0;

	public static final FIXField NON_NULL = new FIXField();

	private String name;
	private int tag;
	private Object value;

	public FIXField() {
		this(UNDEFINED, UNDEF_TAG, UNDEFINED);
	}

	public FIXField(String name, int tag, Object value) {
		this.name = name;
		this.tag = tag;
		this.value = value;
	}

	public FIXField(String name, Object value) {
		this(name, UNDEF_TAG, value);
	}

	public FIXField(int tag, Object value) {
		this(UNDEFINED, tag, value);
	}

	@Override
	public String toString() {
		return name + "(" + tag + ")=" + value;
	}

	public String fix() {
		return tag + "=" + value;
	}

	public String readable() {
		return name + "=" + value;
	}

	public boolean is(String value) {
		return this.value.equals(value);
	}

	public void set(String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		return (tag + value.toString()).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this.hashCode() == obj.hashCode();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int compareTo(FIXField o) {
		if (this.tag != o.tag) { return this.tag - o.tag; }

		return ((Comparable)this.value).compareTo(o.value);
	}

	@Override
	public Object clone() {
		return new FIXField(this.name, this.tag, this.value);
	}

	public String getName() {
		return name;
	}

	public int getTag() {
		return tag;
	}

	public Object getValue() {
		return value;
	}

}
