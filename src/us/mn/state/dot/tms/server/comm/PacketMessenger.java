/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2015  Minnesota Department of Transportation
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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

/**
 * A PacketMessenger is a class which can poll a field controller and get the
 * response using a UDP socket connection.
 *
 * @author Douglas Lau
 */
public class PacketMessenger extends Messenger {

	/** Local port to bind */
	private final Integer port;

	/** Remote address to connect */
	private final SocketAddress remote;

	/** UDP socket */
	private final DatagramSocket socket;

	/** Receive timeout (ms) */
	private int timeout = 750;

	/** Set the receive timeout */
	@Override
	public void setTimeout(int t) throws IOException {
		timeout = t;
		socket.setSoTimeout(t);
	}

	/** Get the receive timeout */
	@Override
	public int getTimeout() {
		return timeout;
	}

	/** Create a new datagram packet messenger.
	 * @param p Local port.
	 * @param ra Remote socket address. */
	public PacketMessenger(Integer p, SocketAddress ra) throws IOException {
		port = p;
		remote = ra;
		socket = new DatagramSocket(p);
		socket.setSoTimeout(timeout);
		socket.connect(remote);
	}

	/** Open the messenger */
	@Override
	public void open() {
		// need to open in constructor due to async receive
	}

	/** Close the datagram packet messenger */
	@Override
	public void close() {
		socket.disconnect();
		socket.close();
	}

	/** Send one datagram packet */
	public void send(DatagramPacket datagram) throws IOException {
		socket.send(datagram);
	}

	/** Receive one datagram packet */
	public void receive(DatagramPacket datagram) throws IOException {
		datagram.setLength(128);
		socket.receive(datagram);
	}
}
