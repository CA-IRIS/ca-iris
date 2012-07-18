/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2011-2012  Minnesota Department of Transportation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package us.mn.state.dot.tms.utils;

import java.io.IOException;
import java.io.Reader;

/**
 * Simple text line reader.
 * This class should be used instead of BufferedReader.readLine whenever input
 * is untrusted, because if no line seperator is found, BufferedReader will
 * happily keep reading text until all memory is exhausted.
 *
 * @author Douglas Lau
 */
public class LineReader {

	/** Test if a character is a line seperator */
	static private boolean isLineSeperator(char c) {
		return c == '\r' || c == '\n';
	}

	/** Underlying reader */
	private final Reader reader;

	/** Buffer to read text */
	private final char[] buffer;

	/** Number of chars in buffer */
	private int n_chars = 0;

	/** Seperator char for most recent line */
	private char sep = 0;

	/** Create a new line reader */
	public LineReader(Reader r, int max_line) {
		reader = r;
		buffer = new char[max_line];
	}

	/** Read a line of text */
	public String readLine() throws IOException {
		int eol = endOfLine();
		while(eol < 0 && n_chars < buffer.length) {
			int n = reader.read(buffer, n_chars, buffer.length -
				n_chars);
			if(n < 0) {
				if(n_chars > 0)
					return bufferedLine(n_chars);
				else
					return null;
			} else {
				n_chars += n;
				eol = endOfLine();
			}
		}
		if(eol >= 0)
			return bufferedLine(eol);
		else
			throw new IOException("LineReader buffer full");
	}

	/** Find the next end of line character */
	private int endOfLine() {
		for(int i = crlf(0); i < n_chars; i++) {
			if(isLineSeperator(buffer[i]))
				return i;
		}
		return -1;
	}

	/** Get the next buffered line of text */
	private String bufferedLine(int eol) {
		assert n_chars >= eol;
		int off = crlf(0);
		String line = new String(buffer, off, eol - off);
		eol = nextLine(eol);
		n_chars -= eol;
		if(n_chars > 0)
			System.arraycopy(buffer, eol, buffer, 0, n_chars);
		return line;
	}

	/** Get index to first character in next line */
	private int nextLine(int pos) {
		if(n_chars > pos && isLineSeperator(buffer[pos])) {
			sep = buffer[pos];
			pos++;
			pos = crlf(pos);
			sep = buffer[pos - 1];
		}
		return pos;
	}

	/** Skip Windows-style line seperators */
	private int crlf(int pos) {
		if(sep == '\r' && n_chars > pos && buffer[pos] == '\n')
			return pos + 1;
		else
			return pos;
	}
}
