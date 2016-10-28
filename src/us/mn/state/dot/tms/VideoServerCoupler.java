/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2013-2015  AHMCT, University of California
 * Copyright (C) 2016       California Department of Transportation
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
package us.mn.state.dot.tms;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * To connect to the video server to get information
 * much of this was refactored out of [client] VideoWallManager so that the IRIS
 * server could get video connection status information.

 * @author Travis Swanston
 * @author Jacob Barde
 */
public class VideoServerCoupler {

	/** Video host property name */
	static protected final String VIDEO_HOST = "video.host";
	/** Video port property name */
	static protected final String VIDEO_PORT = "video.port";

	protected final Properties props;
	protected final String base_url;
	protected volatile Map<String, Integer> ccmap = new HashMap<>();

	public VideoServerCoupler(
		Properties props) {
		this.props = props;
		base_url = getBaseUrl();
	}

	// returns null on error
	static protected HashMap<String, String> decodeDecstatResponse(
		String r) {
		HashMap<String, String> map = new HashMap<String, String>();
		// e.g. DECSTAT\tdec1:,dec3:C002,dec2:,dec4:C007
		String[] fields1 = r.split("\t", -1);
		if (fields1.length < 2)
			return null;
		String[] fields2 = fields1[1].split(",", -1);
		if (fields2.length > 0) {
			for (int i = 0; i < fields2.length; ++i) {
				String[] kv = fields2[i].split(":", 2);
				if (kv.length < 2)
					continue;
				String did = kv[0];
				String cid = kv[1];
				if (cid.trim().equals(""))
					cid = null;
				map.put(did, cid);
			}
		}

		return map;
	}

	// returns null on error
	static protected HashMap<String, String> decodeGrouputilResponse(
		String r) {
		HashMap<String, String> map = new HashMap<String, String>();
		// e.g. UTIL\tISDN:4/12,POTS:3/7
		String[] fields1 = r.split("\t", -1);
		if (fields1.length < 1)
			return null;
		if (fields1.length == 1)        // valid, empty response
			return map;
		String[] fields2 = fields1[1].split(",", -1);
		if (fields2.length > 0) {
			for (int i = 0; i < fields2.length; ++i) {
				String[] kv = fields2[i].split(":", 2);
				if (kv.length < 2)
					continue;
				String group = kv[0];
				String util = kv[1];
				map.put(group, util);
			}
		}

		return map;
	}

	// returns null on error
	static protected HashMap<String, Integer> decodeCamconnsResponse(
		String r) {
		HashMap<String, Integer> map = new HashMap<>();
		// e.g. CAMCONNS\tC002:3,C003:1
		String[] fields1 = r.split("\t", -1);
		if (fields1.length < 1)
			return null;
		if (fields1.length == 1)        // valid, empty response
			return map;
		String[] fields2 = fields1[1].split(",", -1);
		if (fields2.length > 0) {
			for (int i = 0; i < fields2.length; ++i) {
				String[] kv = fields2[i].split(":", 2);
				if (kv.length < 2)
					continue;
				String cid = kv[0];
				Integer conns = null;
				try {
					conns = Integer.valueOf(kv[1]);
				}
				catch (NumberFormatException e) {
					conns = null;
				}
				if (conns != null)
					map.put(cid, conns);
			}
		}

		return map;
	}

	// perform SwitchServer query and return response as string, or null
	// if error or non-200 respcode.
	static protected String querySwitchServer(String u) {
		URL url = null;
		try {
			url = new URL(u);
		}
		catch (MalformedURLException ex) {
			return null;
		}

		InputStream in = null;
		byte[] buf = new byte[0];
		int respCode = -1;
		try {
			HttpURLConnection c = (HttpURLConnection) url
				.openConnection();
			respCode = c.getResponseCode();
			in = c.getInputStream();
			buf = readStream(in);
		}
		catch (UnknownHostException e) {
			return null;
		}
		catch (IOException e) {
			return null;
		}
		finally {
			closeRS(in);
		}

		if (respCode != HttpURLConnection.HTTP_OK)
			return null;

		String resp = "";
		try {
			resp = (new String(buf, "UTF-8"));
		}
		catch (UnsupportedEncodingException e) {
			return null;
		}

		return resp;
	}

	/** Close input stream */
	static protected void closeRS(InputStream is) {
		if (is == null)
			return;

		try {
			is.close();
		}
		catch (Exception ex) {
			// NOP
		}
	}

	/**
	 * Read the an InputStream until EOF.
	 * @param is the InputStream (may be null)
	 *
	 * @return array of bytes read, else null on error
	 */
	static protected byte[] readStream(InputStream is) {
		if (is == null)
			return null;
		byte[] ret = new byte[0];
		try {
			// read until eof
			ArrayList<Byte> al = new ArrayList();
			while (true) {
				int b = is.read();        // throws IOE
				if (b < 0)                // EOF
					break;
				al.add(new Byte((byte) b));
			}
			ret = new byte[al.size()];
			for (int i = 0; i < ret.length; ++i)
				ret[i] = (byte) (al.get(i));
		}
		catch (IOException e) {
			return null;
		}

		return ret;
	}

	/** Get the switchserver url. */
	// e.g. "http://10.49.52.49:8080/video/switch"
	protected String getBaseUrl() {
		String ip = props.getProperty(VIDEO_HOST);
		if (ip != null) {
			try {
				ip = InetAddress.getByName(ip).getHostAddress();
				String port = props.getProperty(VIDEO_PORT);
				if (port != null)
					return "http://" + ip + ":" + port
						+ "/video/switch";
				else
					return "http://" + ip + "/video/switch";
			}
			catch (UnknownHostException uhe) {
				System.err.println("Invalid video server " +
					uhe.getMessage());
			}
		}

		return null;
	}

	protected void updateCameraCounts() {

		Map<String, Integer> cam_count_map = null;

		// get camera count map
		String urlCamConns = base_url + "?cmd=camconns";
		String resp = querySwitchServer(urlCamConns);
		if(resp != null)
			cam_count_map = decodeCamconnsResponse(resp.trim());

		if (cam_count_map != null)
			ccmap = cam_count_map;
	}

	/**
	 * get the cameras in use.
	 * use sparingly, as this will query the video server every time
	 */
	public Map<String, Integer> getCamerasInUse() {
		updateCameraCounts();
		return ccmap;
	}

}
