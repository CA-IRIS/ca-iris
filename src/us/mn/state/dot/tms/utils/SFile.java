/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2015  AHMCT, University of California
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.SecurityException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * File convienence methods.
 * Merge remnant, only used by AWS.
 * @author Michael Darter
 */
public class SFile
{
	/** Write a string to a file, terminated w/ \r\n. */
	public static boolean writeLineToFile(String fname, String s,
		boolean append)
	{
		return(SFile.writeStringToFile(fname, s + "\r\n", append));
	}

	/** Write a string to a file.
	 * @param fname File name, may be null.
	 * @param s String to write, may be null.
	 * @param append False to overwrite existing file else true. */
	public static boolean writeStringToFile(String fname,
		String s, boolean append)
	{
		s = (s == null ? "" : s);
		FileWriter f = null;
		boolean ok = true;
		try {
			f = new FileWriter(fname, append);
			f.write(s);
		} catch(Exception ex) {
			Log.warning(
				"Warning: writeStringToFile(): fname=" +
					fname + ", ex=" + ex);
			ok = false;
		} finally {
			try {
				f.close();
			} catch(Exception ex) {}
		}
		return ok;
	}

	/** Read the specified URL and return a byte array or null on error.
	 * @param surl URL which may be null.
	 * @return Null on error else bytes read. */
	public static byte[] readUrl(String surl) {
		if(surl == null)
			return null;
		URL url = null;
		try {
			url = new URL(surl);
		} catch(MalformedURLException ex) {
			Log.warning("SFile.readUrl(), malformed URL: " + ex);
			return null;
		}
		InputStream in = null;
		byte[] ret = new byte[0];
		try {
			URLConnection c = url.openConnection();
			in = c.getInputStream();
			ret = readUrl(in);
		} catch(UnknownHostException e) {
			Log.config("SFile.readUrl(): ignored bogus url:" + e);
			return null;
		} catch(Exception ex) {
			Log.config("SFile.readUrl(), exception: " + ex);
			return null;
		} finally {
			close(in);
		}
		return ret;
	}

	/** Close input stream */
	public static void close(InputStream is) {
		try {
			if(is != null)
				is.close();
		} catch(Exception ex) {}
	}

	/** Read the specified input stream and return a byte array
	 * or null on error.
	 * @param is Input stream. May be null.
	 * @return Null on error else bytes read. */
	public static byte[] readUrl(InputStream is) {
		if(is == null)
			return null;
		byte[] ret = new byte[0];
		try {
			// read until eof
			ArrayList<Byte> al = new ArrayList();
			while(true) {
				int b = is.read(); // throws IOException
				if(b < 0)
					break; // eof
				al.add(new Byte((byte)b));
			}

			// create byte[]
			ret = new byte[al.size()];
			for(int i = 0; i < ret.length; ++i)
				ret[i] = (byte)(al.get(i));

			//_debugDisplay(ret);
		} catch(IOException e) {
			Log.config("SFile.readUrl(), ex: " + e);
			return null;
		}
		return ret;
	}

	/** Display array */
	private static void _debugDisplay(byte[] ret) {
		for(int i = 0; i < ret.length; ++i )
			System.err.print(ret[i] + " " + (char)ret[i] + ",");
		Log.finest(" ");
		Log.finest("SFile.readUrl(), read " + ret.length);
	}

	/** Return an absolute file path.
	 * @param fn File name. Can be null. If "", then "" is returned.
	 * @throws SecurityException */
	public static String getAbsolutePath(String fn) {
		// on Windows, getAbsolutePath("") returns "C:\"
		if(fn == null || fn.isEmpty())
			return "";
		File fh = new File(fn);
		return fh.getAbsolutePath();
	}

	/** Return true if specified file exists else false.
	 * @param fn File name, may be null. */
	public static boolean fileExists(String fn) {
		if(fn == null)
			return false;
		boolean exists = true;
		try {
			exists = new File(fn).exists();
		} catch(Exception ex) {
			exists = false;
		}
		return exists;
	}

	/** Delete file */
	public static boolean delete(String fn) {
		if(!fileExists(fn))
			return true;
		boolean ok = false;
		try {
			File fd = new File(fn);
			ok = fd.delete();
		} catch(Exception ex) {
			ok = false;
			Log.fine("SFile.delete() ex=" + ex);
		}
		return ok;
	}
}
