package org.fixprotocol.fix;

public class FIXField implements Comparable<FIXField>, Cloneable {

	public static final String UNDEFINED = "UNDEFINED";
	public static final int TAG_ZERO = 0;

	public static final FIXField NULL_FIELD = new FIXField();

	private final String name;
	private final int tag;
	private Object value;

	public FIXField() {
		this(UNDEFINED, TAG_ZERO, UNDEFINED);
	}

	public FIXField(String name, int tag, Object value) {
		this.name = name;
		this.tag = tag;
		this.value = value;
	}

	public FIXField(String name, Object value) {
		this(name, TAG_ZERO, value);
	}

	public FIXField(int tag, Object value) {
		this(UNDEFINED, tag, value);
	}

	@Override
	public String toString() {
		return name + "(" + tag + ")=" + value;
	}

	@Override
	public int hashCode() {
		return (tag + value.toString()).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this.hashCode() == obj.hashCode();
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int compareTo(FIXField o) {
		if (this.tag != o.tag) { return this.tag - o.tag; }

		return ((Comparable)this.value).compareTo(o.value);
	}

	@Override
	public FIXField clone() {
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
	
	/*
	 * Script friendly methods
	 */
	
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

	public void set(Object value) {
		this.value = value;
	}

}
