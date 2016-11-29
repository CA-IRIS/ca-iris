/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2016  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import us.mn.state.dot.sonar.client.ProxyListener;
import us.mn.state.dot.tms.GeoLoc;
import us.mn.state.dot.tms.SiteData;
import us.mn.state.dot.tms.SiteDataHelper;

/**
 * SiteDataHelperClient is for...
 * @author Jacob Barde
 */
public class SiteDataHelperClient extends SiteDataHelper {

	/** maps for easy lookup sitedata name by geoloc name */
	static final private Map<String, String> geoLocToSD_name = new HashMap<>();

	/** map for easy lookup sitedata siteName by geoloc name */
	static final private Map<String, String> geoLocToSD_site_name = new HashMap<>();

	/** map for easy lookup geoloc name by sitedata siteName */
	static final private Map<String, String> siteName2geoLoc = new HashMap<>();

	/** locking object for above maps */
	static final private Object hashLock = new Object();

	/** sitedata listener to populate cache with geolocation and sitedata information */
	static final public ProxyListener<SiteData> sdListener = new ProxyListener<SiteData>() {
		@Override
		public void proxyAdded(final SiteData proxy) {
			populateCache(proxy.getGeoLoc(), proxy.getName(), proxy.getSiteName());
		}

		@Override
		public void enumerationComplete() {

		}

		@Override
		public void proxyRemoved(final SiteData proxy) {
			depopulateCache(proxy.getGeoLoc(), proxy.getName(), proxy.getSiteName());
		}

		@Override
		public void proxyChanged(final SiteData proxy, final String a) {
			populateCache(proxy.getGeoLoc(), proxy.getName(), proxy.getSiteName());
		}
	};

	/** geoloc listener to populate cache with geolocation information */
	static final public ProxyListener<GeoLoc> glListener = new ProxyListener<GeoLoc>() {
		@Override
		public void proxyAdded(final GeoLoc proxy) {
			SiteData sd = lookupByGeoLoc(proxy);
			if (null == sd) {
				populateCache(proxy.getName(), null, null);
			}
		}

		@Override
		public void enumerationComplete() {

		}

		@Override
		public void proxyRemoved(final GeoLoc proxy) {
			SiteData sd = lookupByGeoLoc(proxy.getName());
			sd.destroy();
		}

		@Override
		public void proxyChanged(final GeoLoc proxy, final String a) {

		}
	};

	/** Constructor (do not instantiate). */
	protected SiteDataHelperClient() {
		assert false;
	}

	/** Lookup a site data. if it is not in the cache, query sonar for it. */
	static private SiteData lookupByGeoLoc(String geoloc_name) {
		if (geoloc_name == null)
			return null;

		// try to find cached value first
		String site_name;
		String name = getCachedNameByGeoLoc(geoloc_name);
		SiteData sd = lookup(name);

		// perform the expensive operation (and cache the result)
		if (sd == null) {
			Iterator<SiteData> it = iterator();
			name = null;
			site_name = null;
			while (it.hasNext()) {
				SiteData tmp = it.next();
				if (geoloc_name.equals(tmp.getGeoLoc())) {
					sd = tmp;
					name = sd.getName();
					site_name = sd.getSiteName();
					break;
				}
			}

			populateCache(geoloc_name, name, site_name);
		}

		return sd;
	}

	/** get the name by geoloc name */
	static private String getCachedNameByGeoLoc(String geoloc_name) {
		if (null == geoloc_name)
			return null;
		synchronized (hashLock) {
			return geoLocToSD_name.get(geoloc_name);
		}
	}

	/** populate the cache with the values */
	static private void populateCache(String geoloc_name, String name, String site_name) {
		synchronized (hashLock) {
			if (null != geoloc_name && !"".equals(geoloc_name.trim())) {
				geoLocToSD_name.put(geoloc_name, name);
				geoLocToSD_site_name.put(geoloc_name, site_name);
			}
			if (null != site_name && !"".equals(site_name.trim())) {
				siteName2geoLoc.put(site_name, geoloc_name);
			}
		}

	}

	/** populate the cache with the values */
	static private void depopulateCache(String geoloc_name, String name, String site_name) {
		synchronized (hashLock) {
			if (null != geoloc_name && !"".equals(geoloc_name.trim())) {
				geoLocToSD_name.remove(geoloc_name);
				geoLocToSD_site_name.remove(geoloc_name);
			}

			if (null != site_name && !"".equals(site_name.trim())) {
				siteName2geoLoc.remove(site_name);
			}
		}
		if ((null == site_name || "".equals(site_name)) && null != geoloc_name) {
			for (String s : siteName2geoLoc.keySet()) {
				if (geoloc_name.equals(siteName2geoLoc.get(s))) {
					synchronized (hashLock) { siteName2geoLoc.remove(s); }
					break;
				}
			}
		}
	}

	/**
	 * Build a site name string for a SiteData entity.
	 * @param geoloc_name The GeoLoc name corresponding to the SiteData entity.
	 *
	 * @return A site name string, or null if entity not found or if entity doesn't contain a site name
	 */
	static public String getSiteName(String geoloc_name) {
		if (geoloc_name == null)
			return null;

		SiteData sd = null;
		String site_name = null;
		boolean exists;
		synchronized (hashLock) {
			exists = geoLocToSD_site_name.containsKey(geoloc_name);
			site_name = geoLocToSD_site_name.get(geoloc_name);
		}

		if (site_name == null && !exists) {
			sd = lookupByGeoLoc(geoloc_name);
			site_name = sd != null ? sd.getSiteName() : null;
		}
		return !"".equals(sanitize(site_name)) ? site_name : null;
	}

	/** Lookup a SiteData entity */
	static private SiteData lookupBySiteName(String site_name) {
		if (site_name == null)
			return null;

		String geoloc_name;
		String name = getCachedNameBySiteName(site_name);
		SiteData sd = lookup(name);

		if (sd == null) {
			geoloc_name = null;
			name = null;
			Iterator<SiteData> it = iterator();
			while(it.hasNext()) {
				SiteData tmp = it.next();
				if(site_name.equals(tmp.getSiteName())) {
					sd = tmp;
					geoloc_name = sd.getGeoLoc();
					name = sd.getName();
					break;
				}
			}

			populateCache(geoloc_name, name, site_name);
		}

		return sd;
	}

	/** get the cache name for a site-name */
	static private String getCachedNameBySiteName(String site_name) {
		synchronized (hashLock) {
			String geoloc_name = siteName2geoLoc.get(site_name);
			if (geoloc_name != null)
				return geoLocToSD_name.get(geoloc_name);
		}
		return null;
	}

	/** Lookup a SiteData entity by GeoLoc */
	static public SiteData lookupByGeoLoc(GeoLoc gl) {
		return gl != null ? lookupByGeoLoc(gl.getName()) : null;
	}

	/**
	 * Find a GeoLoc name by site name.
	 * @param sn The site name corresponding to the GeoLoc entity.
	 *
	 * @return A GeoLoc name string, or null if site name not found.
	 */
	static public String getGeoLocNameBySiteName(String sn) {
		SiteData sd = lookupBySiteName(sn);
		String gl = sd != null ? sd.getGeoLoc() : null;

		return !"".equals(sanitize(gl)) ? gl : null;
	}

	/** clear map caches */
	static public void clearCache() {
		geoLocToSD_name.clear();
		geoLocToSD_site_name.clear();
		siteName2geoLoc.clear();
	}
}
