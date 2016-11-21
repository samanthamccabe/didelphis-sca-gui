package org.didelphis.toolbox.frontend.components;

/**
 * Created by samantha on 11/11/16.
 * Used to interface with Ace editor annotations
 */
public class Annotation {

	private final int row;
	private final int end;
	private final String text;
	private final String html;
	private final Type type;
	
	public static Annotation errorHTML(int row, int end, String html) {
		return new Annotation(row, end, null, html, Type.error);
	}

	public static Annotation warnHTML(int row, int end, String html) {
		return new Annotation(row, end, null, html, Type.warn);
	}

	public static Annotation infoHTML(int row, int end, String html) {
		return new Annotation(row, end, null, html, Type.info);
	}

	private Annotation(int row, int rowSpan, String text, String html, Type type) {
		this.row = row;
		this.text = text;
		this.html = html;
		this.type = type;
		this.end = rowSpan;
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
		info
	}

	public int getEnd() {
		return end;
	}
}
