/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2003-2013  Minnesota Department of Transportation
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

import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import us.mn.state.dot.sched.Job;
import us.mn.state.dot.sched.Scheduler;
import us.mn.state.dot.tms.Camera;
import us.mn.state.dot.tms.CameraHelper;
import us.mn.state.dot.tms.EncoderType;
import us.mn.state.dot.tms.StreamType;
import us.mn.state.dot.tms.utils.HttpUtil;

import static us.mn.state.dot.tms.client.widget.Widgets.UI;

/**
 * A video stream which reads an MJPEG source.
 *
 * @author Douglas Lau
 * @author Timothy Johnson
 * @author Dan Rossiter
 */
public class MJPEGStream implements VideoStream {

	/** Label to display video stream */
	private final JLabel screen = new JLabel();

	/** URL of the data source */
	private final URL url;

	/** Requested video size */
	private final Dimension size;

	/** Input stream to read */
	private InputStream stream;

	/** Length of image to be read */
	private int content_len;

	/** Count of rendered frames */
	private int n_frames = 0;

	/** Flag to continue running stream */
	private boolean running = true;

	/** Stream error message */
	private String error_msg = null;

	/** Stream must be mocked from static snapshots */
	private final boolean is_snapshot;

	/** Anonymous thread to read video stream */
	private final Job job;

	/** Set the stream error message */
	protected void setErrorMsg(String e) {
		if(error_msg == null)
			error_msg = e;
	}

	/** Create a new MJPEG stream */
	public MJPEGStream(Scheduler s, VideoRequest req, Camera c)
		throws IOException
	{
		url = new URL(req.getUrl(c));
		is_snapshot = isSnapshot(c);
		size = UI.dimension(req.getSize().width, req.getSize().height);
		job = getStreamJob();
		s.addJob(job);
	}

	/** Whether we are "streaming" a static snapshot */
	private boolean isSnapshot(Camera c) {
		return CameraHelper.getEncoderType(c) == EncoderType.GENERIC_URL &&
				"image/jpeg".equals(HttpUtil.getContentType(url));
	}

	/** Gets the input stream, initing if necessary */
	private InputStream getStream() throws IOException {
		if (null == stream)
			stream = createInputStream();
		return stream;
	}

	/** Gets background job to retrieving stream frames */
	private Job getStreamJob() {
		int iField = Calendar.MILLISECOND;
		int i = 1;
		if (is_snapshot) {
			iField = Calendar.SECOND;
			i = 30;
		}

		return new Job(iField, i) {
			@Override
			public void perform() {
				if(running)
					readStream();
			}
			public boolean isRepeating() {
				return running;
			}
		};
	}

	/** Create an input stream from an HTTP connection */
	protected InputStream createInputStream() throws IOException {
		HttpURLConnection c = (HttpURLConnection)url.openConnection();
		HttpURLConnection.setFollowRedirects(true);
		c.setConnectTimeout(HttpUtil.TIMEOUT_DIRECT);
		c.setReadTimeout(HttpUtil.TIMEOUT_DIRECT);
		int resp = c.getResponseCode();
		if (resp != HttpURLConnection.HTTP_OK) {
			throw new IOException(c.getResponseMessage());
		}
		content_len = getImageSize(c);
		return c.getInputStream();
	}

	/** Read a video stream */
	private void readStream() {
		try {
			byte[] idata = getImage();
			screen.setIcon(createIcon(idata));
		} catch(IOException e) {
			setErrorMsg(e.getMessage());
			screen.setIcon(null);
			running = false;
		} finally {
			if (is_snapshot && null != stream) {
				try {
					stream.close();
				} catch (IOException e) {}
				stream = null;
			}
		}
	}

	/** Get the next image in the mjpeg stream */
	protected byte[] getImage() throws IOException {
		InputStream stream = getStream();
		byte[] image = new byte[content_len];
		int n_bytes = 0;
		while(n_bytes < content_len) {
			int r = stream.read(image, n_bytes, content_len - n_bytes);
			if(r >= 0)
				n_bytes += r;
			else
				throw new IOException("End of stream");
		}
		n_frames++;
		return image;
	}

	/** Create an image icon from image data */
	protected ImageIcon createIcon(byte[] idata) {
		ImageIcon icon = new ImageIcon(idata);
		if(icon.getIconWidth() == size.width &&
		   icon.getIconHeight() == size.height)
			return icon;
		Image im = icon.getImage().getScaledInstance(size.width,
			size.height, Image.SCALE_FAST);
		return new ImageIcon(im);
	}

	/** Get the length of the next image */
	private int getImageSize(URLConnection c) throws IOException {
		if (is_snapshot) {
			return c.getContentLength();
		} else {
			for (int i = 0; i < 100; i++) {
				String s = readLine();
				if (s.toLowerCase().contains("content-length")) {
					// throw away an empty line after the
					// content-length header
					readLine();
					return parseContentLength(s);
				}
			}
			throw new IOException("Missing content-length");
		}
	}

	/** Parse the content-length header */
	private int parseContentLength(String s) throws IOException {
		s = s.substring(s.indexOf(":") + 1);
		s = s.trim();
		try {
			return Integer.parseInt(s);
		}
		catch(NumberFormatException e) {
			throw new IOException("Invalid content-length");
		}
	}

	/** Read the next line of text */
	private String readLine() throws IOException {
		StringBuilder b = new StringBuilder();
		while(true) {
			int ch = getStream().read();
			if(ch < 0) {
				if(b.length() == 0)
					throw new IOException("End of stream");
				else
					break;
			}
			b.append((char)ch);
			if(ch == '\n')
				break;
		}
		return b.toString();
	}

	/** Get a component for displaying the video stream */
	public JComponent getComponent() {
		return screen;
	}

	/** Get the status of the stream */
	public String getStatus() {
		String e = error_msg;
		if(e != null)
			return e;
		else
			return StreamType.MJPEG.toString();
	}

	/** Test if the video is playing */
	public boolean isPlaying() {
		return running;
	}

	/** Dispose of the video stream */
	public void dispose() {
		running = false;
		if (stream != null) {
			try {
				stream.close();
			}
			catch(IOException e) {
				setErrorMsg(e.getMessage());
			}
		}
		screen.setIcon(null);
	}
}
