package org.didelphis.toolbox.components;

/**
 * Created by samantha on 11/11/16.
 * Used to interface with Ace editor annotations
 */
public class Annotation {

	private final int row;
	private final String text;
	private final String html;
	private final Type type;

	public static Annotation errorHTML(int row, String html) {
		return new Annotation(row, null, html, Type.error);
	}

	public static Annotation warnHTML(int row, String html) {
		return new Annotation(row, null, html, Type.warn);
	}

	public static Annotation infoHTML(int row, String html) {
		return new Annotation(row, null, html, Type.info);
	}

	private Annotation(int row, String text, String html, Type type) {
		this.row = row;
		this.text = text;
		this.html = html;
		this.type = type;
	}

	public int getRow() {
		return row;
	}

	public String getText() {
		return text;
	}

	public String getHtml() {
		return html;
	}

	public Type getType() {
		return type;
	}

	private enum Type {
		error,
		warn,
		info;

	}
}
