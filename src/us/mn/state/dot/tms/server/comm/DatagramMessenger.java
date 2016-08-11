/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2015  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * A DatagramMessenger is a class which can poll a field controller and get the
 * response using a UDP socket connection.
 *
 * @author Douglas Lau
 * @author Michael Darter
 * @author Travis Swanston
 */
public class DatagramMessenger extends Messenger {

	//FIXME ConnType and the new binding port may conflict. UCD#290
	
	/** Connection type */
	public enum ConnType {
		// bidirectional single host, local or remote
		DEFAULT,

		// receive from multiple hosts on local address
		RECV_MULT_LOCAL
	}

	/** Connection type */
	private final ConnType connect_type;

	/** Local port to bind */
	private final Integer port;

	/** Remote address to connect */
	private final SocketAddress remote;

	/** UDP socket */
	private DatagramSocket socket;

	/** Receive timeout (ms) */
	private int timeout = 750;

	/** Set the receive timeout */
	@Override
	public void setTimeout(int t) throws IOException {
		timeout = t;
		DatagramSocket s = socket;
		if (s != null)
			s.setSoTimeout(t);
	}

	/** Get the receive timeout */
	@Override
	public int getTimeout() {
		return timeout;
	}

	/** Create a new datagram messenger.
	 * @param p Local port (null for any).
	 * @param ra Remote socket address. 
	 */
	public DatagramMessenger(Integer p, SocketAddress ra) {
		this(ConnType.DEFAULT, ra, p);
	}

	/** Create a new datagram messenger.
	 * @param ra Remote socket address. 
	 */
	public DatagramMessenger(SocketAddress ra) {
		this(ConnType.DEFAULT, ra, null);
	}

	/** Create a new datagram messenger 
	 * @param ct ConnType
	 * @param ra Remote socket address. 
	 */
	public DatagramMessenger(ConnType ct, SocketAddress ra) {
		this(ct, ra, null);
	}
	
	/** Create a new datagram messenger 
	 * @param ct ConnType
	 * @param ra  Remote socket address.
	 * @param p Local port (null for any).
	 */
	public DatagramMessenger(ConnType ct, SocketAddress ra, Integer p) {
		connect_type = ct;
		remote = ra;
		port = p;
	}


	/** Open the datagram messenger */
	@Override
	public void open() throws IOException {
	if (connect_type == ConnType.DEFAULT) {
			socket = new DatagramSocket();
			socket.setSoTimeout(timeout);
			socket.connect(remote);
		} else if (connect_type == ConnType.RECV_MULT_LOCAL) {
			socket = new DatagramSocket(null);
			socket.setSoTimeout(timeout);
			socket.bind(remote);
		} else {
			System.err.println("bogus datagram connection type");
			return;
		}		input = new DatagramInputStream();
		output = new DatagramOutputStream();
	}

	/** Create the socket */
	private DatagramSocket createSocket() throws IOException {
		return (port != null)
		      ? new DatagramSocket(port)
		      : new DatagramSocket();
	}

	/** Close the datagram messenger */
	@Override
	public void close() {
		DatagramSocket s = socket;
		if (s != null) {
			s.disconnect();
			s.close();
			socket = null;
		}
		input = null;
		output = null;
	}

	/** Output stream for sending datagrams */
	private class DatagramOutputStream extends OutputStream {

		/** Buffer for assembling packets to send */
		private final ByteBuffer buffer = ByteBuffer.allocate(1024);

		/** Packet to send */
		private final DatagramPacket packet =
			new DatagramPacket(buffer.array(), 1024);

		/** Write a byte to the buffer */
		@Override
		public void write(int b) {
			buffer.put((byte)b);
		}

		/** Flush packet to datagram */
		@Override
		public void flush() throws IOException {
			packet.setSocketAddress(remote);
			packet.setLength(buffer.position());
			buffer.clear();
			DatagramSocket s = socket;
			if (s != null)
				s.send(packet);
		}
	}

	/** Input stream for receiving datagrams */
	public class DatagramInputStream extends InputStream {

		/** Buffer for storing received datagram */
		private final ByteBuffer buffer = ByteBuffer.allocate(1024);

		/* Datagram origin address */
		private InetAddress origin_addr;

		/** Packet to receive */
		private final DatagramPacket packet =
			new DatagramPacket(buffer.array(), 1024);

		/** Create a new datagram input stream */
		private DatagramInputStream() {
			// no data in buffer before a packet is received
			buffer.limit(0);
		}

		/** Get datagram origin address */
		public InetAddress getOriginAddr() {
			return origin_addr;
		}

		/** Read a byte from a received datagram */
		@Override
		public int read() throws IOException {
			try {
				return buffer.get() & 0xFF;
			}
			catch (BufferUnderflowException e) {
				receivePacket();
				try {
					return buffer.get() & 0xFF;
				}
				catch (BufferUnderflowException e2) {
					throw new SocketTimeoutException("DIS");
				}
			}
		}

		/**
		 * Receive and buffer a datagram. This method blocks until the
		 * buffer is full or the read times out.
		 * @throws SocketTimeoutException
		 */
		protected void receivePacket() throws IOException {
			DatagramSocket s = socket;
			if (s != null) {
				packet.setLength(1024);
				s.receive(packet);
				origin_addr = packet.getAddress();
				buffer.position(0);
				buffer.limit(packet.getLength());
			}
		}

		/** Get the number of available bytes */
		@Override
		public int available() {
			return buffer.remaining();
		}

		/** Skip the given number of bytes in the input stream */
		public void skip(int b) {
			if (b >= buffer.remaining()) {
				buffer.clear();
				buffer.limit(0);
			} else
				buffer.position(buffer.position() + b);
		}

		/** Read all bytes in a datagram */
		public byte[] readDatagram() throws IOException {
			read();
			buffer.position(0);
			int size = buffer.limit();
			byte[] ret = new byte[size];
			if (size > 0)
				buffer.get(ret);
			return ret;
		}
	}
}
