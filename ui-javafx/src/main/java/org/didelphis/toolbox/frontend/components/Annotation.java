package org.didelphis.toolbox.frontend.components;

import org.apache.commons.lang3.StringEscapeUtils;
import org.didelphis.soundchange.ErrorLogger;

/**
 * Created by samantha on 11/11/16.
 * Used to interface with Ace editor annotations
 */
public final class Annotation {

	private final int row;
	private final int end;
	private final String html;
	private final Type type;
	
	public static Annotation error(ErrorLogger.Error error) {
		String html4 = StringEscapeUtils.escapeHtml4(error.getMessage());
		int length = error.getData().split("\r\n?|\n").length - 1;
		return new Annotation(error.getLine(), length, html4, Type.error);
	}
	
	public static Annotation error(int row, int end, String html) {
		return new Annotation(row, end, html, Type.error);
	}

	public static Annotation warn(int row, int end, String html) {
		return new Annotation(row, end, html, Type.warn);
	}

	public static Annotation info(int row, int end, String html) {
		return new Annotation(row, end, html, Type.info);
	}

	private Annotation(int row, int rowSpan, String html, Type type) {
		this.row = row;
		this.html = html;
		this.type = type;
		this.end = row + rowSpan;
	}

	public int getRow() {
		return row;
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

	@Override
	public String toString() {
		return "Annotation{" +
				"row=" + row +
				", end=" + end +
				", html='" + html + '\'' +
				", type=" + type +
				'}';
	}
}
