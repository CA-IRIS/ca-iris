/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2007-2012  Minnesota Department of Transportation
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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Proxy selector for HTTP clients
 *
 * @author Tim Johnson
 * @author Douglas Lau
 */
public class HTTPProxySelector extends ProxySelector {

	/** Ports to be proxied */
	static protected final int[] PROXY_PORTS = {80, 8080};

	/** Check if the port of a URI should be proxied */
	static protected boolean isProxyPort(int p) {
		if(p == -1)
			return true;
		for(int i: PROXY_PORTS) {
			if(p == i)
				return true;
		}
		return false;
	}

	/** List of proxies */
	protected final List<Proxy> proxies;

	/** Array of hosts to skip proxy */
	protected final String[] no_proxy_hosts;

	/** Create a new HTTP proxy selector */
	public HTTPProxySelector(Properties props) {
		proxies = createProxyList(props);
		no_proxy_hosts = createNoProxyHosts(props);
	}

	/** Create a Proxy list from a set of properties */
	protected List<Proxy> createProxyList(Properties props) {
		LinkedList<Proxy> plist = new LinkedList<Proxy>();
		String h = props.getProperty("proxy.host");
		String p = props.getProperty("proxy.port");
		if(h != null && p != null) {
			SocketAddress sa = new InetSocketAddress(h,
				Integer.valueOf(p));
			plist.add(new Proxy(Proxy.Type.HTTP, sa));
		}
		return plist;
	}

	/** Create an array of hosts to skip proxy */
	protected String[] createNoProxyHosts(Properties props) {
		String hosts = props.getProperty("no.proxy.hosts");
		if(hosts != null)
			return hosts.split(",");
		else
			return new String[0];
	}

	/** Handle a failed connection to a proxy server */
	public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
		// FIXME: implement this method
	}

	/** Select available proxy servers based on a URI */
	public List<Proxy> select(URI uri) {
		if(uri != null && shouldUseProxy(uri))
			return proxies;
		else {
			LinkedList<Proxy> pl = new LinkedList<Proxy>();
			pl.add(Proxy.NO_PROXY);
			return pl;
		}
	}

	/** Check if a proxy server should be used for a URI */
	protected boolean shouldUseProxy(URI uri) {
		String host = uri.getHost();
		try {
			InetAddress addr = InetAddress.getByName(host);
			String hip = addr.getHostAddress();
			for(String h: no_proxy_hosts) {
				if(hip.startsWith(h))
					return false;
			}
			return isProxyPort(uri.getPort());
		}
		catch(UnknownHostException uhe) {
			return true;
		}
	}

	/** Check if the selector has defined proxy servers */
	public boolean hasProxies() {
		return proxies.size() > 0;
	}
}
