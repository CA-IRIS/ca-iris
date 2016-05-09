/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2013-2015 AHMCT, University of California
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
package us.mn.state.dot.tms.client.camera;

import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import us.mn.state.dot.sched.Job;
import us.mn.state.dot.sched.Scheduler;
import us.mn.state.dot.sched.TimeSteward;
import us.mn.state.dot.tms.Camera;
import us.mn.state.dot.tms.client.Session;


/**
 * Class that manages the state and control of a video wall, via
 * SwitchServer, a CA-only component of the video server.
 * NOTE: THIS IS PURE PROTOTYPE CODE THAT UNFORTUNATELY, DO TO SCHEDULE
 * CONSTRAINTS, BECAME PRODUCTION CODE.  IT NEEDS A FULL REDESIGN AND
 * REWRITE.
 * NOTE: There are some race conditions...
 */
public class VideoWallManager {

	/** Video host property name */
	static private final String VIDEO_HOST = "video.host";

	/** Video port property name */
	static private final String VIDEO_PORT = "video.port";

	//private final SonarState sonar_state;
	private final Properties props;
	private final Session session;
	private final String base_url;

	// current status maps.
	// do not manipulate contents; only change references.
	private volatile Map<String, String> decstat_map
		= new HashMap<String, String>();
	private volatile Map<String, String> grouputil_map
		= new HashMap<String, String>();
	private volatile Map<String, Integer> ccmap
		= new HashMap<String, Integer>();


	/** Scheduler that runs refresh job */
	static private final Scheduler REFRESH
		= new Scheduler("VideoWallManager");

	static private final int REFRESH_PERIOD_SEC = 4;

	private class RefreshJob extends Job {
		private RefreshJob() {
			super(Calendar.SECOND, REFRESH_PERIOD_SEC,
				Calendar.SECOND, 2);
		}
		public void perform() throws Exception {
			updateStatus();
		}
	}

	/** Refresh job */
	private final Job refresh_job = new RefreshJob();

	/** Create a new form */
	public VideoWallManager(Session sess, Properties props) {
		session = sess;
		this.props = props;
		base_url = getBaseUrl();
		initialize();
		// initial update to prevent race with CameraDispatcher
		updateStatus();
	}


	private void initialize() {
		REFRESH.addJob(refresh_job);
	}


	public void dispose() {
		REFRESH.removeJob(refresh_job);
	}


	/** Get the switchserver url. */
	// e.g. "http://10.49.52.49:8080/video/switch"
	private String getBaseUrl() {
		String ip = props.getProperty(VIDEO_HOST);
		if(ip != null) {
			try {
				ip = InetAddress.getByName(ip).getHostAddress();
				String port = props.getProperty(VIDEO_PORT);
				if (port != null)
					return "http://" + ip + ":" + port
						+ "/video/switch";
				else
					return "http://" + ip + "/video/switch";
			}
			catch(UnknownHostException uhe) {
				System.err.println("Invalid video server " +
					uhe.getMessage());
			}
		}
		return null;
	}


	private synchronized void updateStatus() {
		if (base_url == null)
			return;

		String url = null;
		String resp = null;

		Map<String, String> dmap = null;
		Map<String, String> gumap = null;
		Map<String, Integer> cam_count_map = null;

		// get decoder map
		url = base_url + "?cmd=decstat";
		resp = querySwitchServer(url);
		if (resp != null)
			dmap = decodeDecstatResponse(resp.trim());

		// get grouputil map
		url = base_url + "?cmd=grouputil";
		resp = querySwitchServer(url);
		if (resp != null)
			gumap = decodeGrouputilResponse(resp.trim());

		// get camera count map
		url = base_url + "?cmd=camconns";
		resp = querySwitchServer(url);
		if(resp != null)
			cam_count_map = decodeCamconnsResponse(resp.trim());

		// update at once
		if (dmap != null)
			decstat_map = dmap;
		if (gumap != null)
			grouputil_map = gumap;
		if (cam_count_map != null)
			ccmap = cam_count_map;
	}


	public Map<String, String> getDecoderMap() {
		return (Map<String, String>)decstat_map;
	}


	public Map<String, String> getGroupUtilMap() {
		return (Map<String, String>)grouputil_map;
	}

	// synchronous get.  -1 on error.
	public int getNumConns(String cid) {
		if (cid == null)
			return -1;

		if (ccmap == null)
			return -3;

		if (ccmap.isEmpty())
			return 0;

		Integer num = ccmap.get(cid);

		if (num != null)
			return num.intValue();
		else
			return 0;
	}

	public List<String> getInUseCameraList() {
		List<String> rv = new ArrayList<String>();
		for(Map.Entry<String, Integer> entry : ccmap.entrySet())
			if (entry.getValue() != null && entry.getValue() > 0)
				rv.add(entry.getKey());

		return rv;
	}

	public List<String> getCameraList() {
		ArrayList<String> cams = new ArrayList<String>();
		Iterator<Camera> it = session.getSonarState().getCamCache()
			.getCameras().iterator();
		while (it.hasNext())
			cams.add(it.next().getName());
		Collections.sort(cams);
		return cams;
	}

	// which camera is did connected to?
	public String getCameraByDecoder(String did) {
		if (did == null)
			return null;
		return decstat_map.get(did);
	}

	// which decoder is connected to cid?
	public String getDecoderByCamera(String cid) {
		if (cid == null)
			return null;
		for (String did : decstat_map.keySet()) {
			if (cid.equals(decstat_map.get(did)))
				return did;
		}
		return null;
	}

	// returns false on any detected error, else true.
	public boolean connect(String did, String cid) {
		if ((cid == null) || (did == null))
			return false;
		if (base_url == null)
			return false;
		String url = base_url + "?cmd=conn&did="+ did +"&cid=" + cid;
		String resp = querySwitchServer(url);
		if (resp == null)
			return false;
		if ("OK".equals(resp.trim()))
			return true;
		return false;
	}


	// returns false on any detected error or if camera is not known to be
	// connectd to any decoders, else true.
	public boolean disconnectCam(String cid) {
		if (cid == null)
			return false;
		if (base_url == null)
			return false;
		String url = base_url + "?cmd=disccam&cid=" + cid;
		String resp = querySwitchServer(url);
		if (resp == null)
			return false;
		if ("OK".equals(resp.trim()))
			return true;
		return false;
	}


	// returns false on any detected error, else true.
	public boolean disconnectDec(String did) {
		if (did == null)
			return false;
		if (base_url == null)
			return false;
		String url = base_url + "?cmd=discdec&did=" + did;
		String resp = querySwitchServer(url);
		if (resp == null)
			return false;
		if ("OK".equals(resp.trim()))
			return true;
		return false;
	}


	// returns null on error
	private HashMap<String, String> decodeDecstatResponse(String r) {
		HashMap<String, String> map = new HashMap<String, String>();
		// e.g. DECSTAT\tdec1:,dec3:C002,dec2:,dec4:C007
		String[] fields1 = r.split("\t",-1);
		if (fields1.length < 2)
			return null;
		String[] fields2 = fields1[1].split(",",-1);
		if (fields2.length > 0) {
			for (int i=0; i<fields2.length; ++i) {
				String[] kv = fields2[i].split(":",2);
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
	private HashMap<String, String> decodeGrouputilResponse(String r) {
		HashMap<String, String> map = new HashMap<String, String>();
		// e.g. UTIL\tISDN:4/12,POTS:3/7
		String[] fields1 = r.split("\t",-1);
		if (fields1.length < 1)
			return null;
		if (fields1.length == 1)	// valid, empty response
			return map;
		String[] fields2 = fields1[1].split(",",-1);
		if (fields2.length > 0) {
			for (int i=0; i<fields2.length; ++i) {
				String[] kv = fields2[i].split(":",2);
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
	private HashMap<String, Integer> decodeCamconnsResponse(String r) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		// e.g. CAMCONNS\tC002:3,C003:1
		String[] fields1 = r.split("\t",-1);
		if (fields1.length < 1)
			return null;
		if (fields1.length == 1)	// valid, empty response
			return map;
		String[] fields2 = fields1[1].split(",",-1);
		if (fields2.length > 0) {
			for (int i=0; i<fields2.length; ++i) {
				String[] kv = fields2[i].split(":",2);
				if (kv.length < 2)
					continue;
				String cid = kv[0];
				Integer conns = null;
				try {
					conns = Integer.valueOf(kv[1]);
				}
				catch(NumberFormatException e) {
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
	private static String querySwitchServer(String u) {
		URL url = null;
		try {
			url = new URL(u);
		}
		catch(MalformedURLException ex) {
			return null;
		}
		InputStream in = null;
		byte[] buf = new byte[0];
		int respCode = -1;
		try {
			HttpURLConnection c = (HttpURLConnection)url
				.openConnection();
			respCode = c.getResponseCode();
			in = c.getInputStream();
			buf = readStream(in);
		}
		catch(UnknownHostException e) {
			return null;
		}
		catch(IOException e) {
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
		catch(UnsupportedEncodingException e) {
			return null;
		}

		return resp;
	}


	/** Close input stream */
	private static void closeRS(InputStream is) {
		if (is == null)
			return;
		try {
			is.close();
		}
		catch(Exception ex) {
			// NOP
		}
	}


	/** Read the an InputStream until EOF.
	 * @param is the InputStream (may be null)
	 * @return array of bytes read, else null on error
	 */
	private static byte[] readStream(InputStream is) {
		if (is == null)
			return null;
		byte[] ret = new byte[0];
		try {
			// read until eof
			ArrayList<Byte> al = new ArrayList();
			while(true) {
				int b = is.read();	// throws IOE
				if (b < 0)		// EOF
					break;
				al.add(new Byte((byte)b));
			}
			ret = new byte[al.size()];
			for(int i = 0; i < ret.length; ++i)
				ret[i] = (byte)(al.get(i));
		}
		catch(IOException e) {
			return null;
		}
		return ret;
	}

}

