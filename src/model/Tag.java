package model;

import java.io.Serializable;

/**
 * This class manages tagging functionality
 * @author Jake Karasik (jak451)
 */
public class Tag implements Serializable {
	
	/**
	 * ID used for tracking serial version
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Name to identify tag
	 */
	private String name;
	/**
	 * Value to go with key of tag
	 */
	private String value;
	
	/**
     * Creates tag given key and value.
     * @param name Name to identify tag
     * @param value Value of tag
     */
	public Tag(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public boolean equals(Object t) {
		return t != null && (t instanceof Tag) && this.name.equalsIgnoreCase(((Tag)t).name) && this.value.equalsIgnoreCase(((Tag)t).value);
	}
	
	@Override
    public String toString() {
		return this.name + "=" + this.value;
    }
}
