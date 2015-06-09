/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2014-2015  AHMCT, University of California
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
package us.mn.state.dot.tms.client.roads;

import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.util.HashMap;
import us.mn.state.dot.sched.TimeSteward;
import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.County;
import us.mn.state.dot.tms.GeoLoc;
import us.mn.state.dot.tms.SiteData;
import us.mn.state.dot.tms.SiteDataHelper;
import us.mn.state.dot.tms.client.IrisClient;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.SonarState;
import us.mn.state.dot.tms.client.proxy.ProxyView;
import us.mn.state.dot.tms.client.proxy.ProxyWatcher;
import us.mn.state.dot.tms.client.widget.IAction;
import us.mn.state.dot.tms.client.widget.IPanel;

/**
 * SiteDataPanel is a Swing panel for viewing and editing SiteData entities.
 *
 * @author Travis Swanston
 */
public class SiteDataPanel extends IPanel implements ProxyView<SiteData> {

	/** User session */
	private final Session session;

	/** Iris client */
	private final IrisClient client;

	/** Sonar state object */
	private final SonarState state;

	/** SiteData TypeCache */
	private final TypeCache<SiteData> cache;

	/** Proxy watcher */
	private final ProxyWatcher<SiteData> watcher;

	/** SiteData object */
	private SiteData site_data;

	/** Set the current SiteData */
	public void setGeoLoc(GeoLoc gl) {
		SiteData sd = null;
		if (gl != null) {
			sd = SiteDataHelper.lookupByGeoLoc(gl);
			if (sd == null) {
				sd = createObject(gl);
			}
		}
		site_data = sd;
		watcher.setProxy(sd);
	}

	/** Name field */
	private final JTextField sitedata_name_txt = new JTextField();

	/** Format field */
	private final JTextField sitedata_format_txt = new JTextField();

	/** County field */
	private final JComboBox sitedata_county_cbx = new JComboBox(County.getNames());

	/** Create a new site data panel */
	public SiteDataPanel(Session s) {
		session = s;
		client = s.getDesktop().client;
		state = s.getSonarState();
		cache = state.getSiteData();
		watcher = new ProxyWatcher<SiteData>(cache, this, false);
	}

	/** Initialize the panel */
	@Override
	public void initialize() {
		super.initialize();
		setBorder(BorderFactory.createEmptyBorder());
		add("location.sitedata.sitename");
		add(sitedata_name_txt, Stretch.FULL);
		add("location.sitedata.format");
		add(sitedata_format_txt, Stretch.FULL);
		add("location.sitedata.county");
		add(sitedata_county_cbx, Stretch.FULL);
		createJobs();
		watcher.initialize();
	}

	/** Dispose of the panel */
	@Override
	public void dispose() {
		watcher.dispose();
		super.dispose();
	}

	/** Create the jobs */
	private void createJobs() {
		sitedata_name_txt.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				String sn = sitedata_name_txt.getText();
				setSiteName(sn.trim());
			}
		});
		sitedata_format_txt.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				String f = sitedata_format_txt.getText();
				setFormat(f.trim());
			}
		});
		sitedata_county_cbx.setAction(new IAction("location.sitedata.county") {
			protected void doActionPerformed(ActionEvent e) {
				String s = (String)sitedata_county_cbx.getSelectedItem();
				if ("".equals(s))		// MAGIC
					setCounty(null);
				else
					setCounty(s);
			}
		});
	}

	private void setSiteName(String sn) {
		if ((sn != null) && (sn.trim().equals("")))
			sn = null;
		SiteData sd = site_data;
		if (sd != null)
			sd.setSiteName(sn);
	}
	private void setFormat(String f) {
		if ((f != null) && (f.trim().equals("")))
			f = null;
		SiteData sd = site_data;
		if (sd != null)
			sd.setFormat(f);
	}
	private void setCounty(String c) {
		if ((c != null) && (c.trim().equals("")))
			c = null;
		SiteData sd = site_data;
		if (sd != null)
			sd.setCounty(c);
	}

	/** Update the edit mode */
	public void updateEditMode() {
		SiteData sd = site_data;
		sitedata_name_txt.setEnabled(canUpdate(sd, "site_name"));
		sitedata_format_txt.setEnabled(canUpdate(sd, "format"));
		sitedata_county_cbx.setEnabled(canUpdate(sd, "county"));
	}

	/** Update one attribute (from ProxyView). */
	@Override
	public void update(SiteData sd, String a) {
		if (a == null) {
			site_data = sd;
			updateEditMode();
		}
		if (a == null || a.equals("site_name")) {
			String sn = sd.getSiteName();
			sitedata_name_txt.setText(sn);
		}
		if (a == null || a.equals("format")) {
			String f = sd.getFormat();
			sitedata_format_txt.setText(f);
		}
		if (a == null || a.equals("county")) {
			String c = sd.getCounty();
			if (c == null)
				c = "";
			sitedata_county_cbx.setSelectedItem(c);
		}
	}

	/** Test if the user can update an attribute */
	private boolean canUpdate(SiteData l, String a) {
		return session.canUpdate(l, a);
	}

	/** Clear all attributes (from ProxyView). */
	@Override
	public void clear() {
		site_data = null;
		sitedata_name_txt.setEnabled(false);
		sitedata_name_txt.setText("");
		sitedata_format_txt.setEnabled(false);
		sitedata_format_txt.setText("");
		sitedata_county_cbx.setEnabled(false);
		sitedata_county_cbx.setSelectedItem(County.lookup(""));
	}

	/**
	 * Create a new SiteData entity for a GeoLoc.
	 * Ideally, this type of thing would be done server-side to
	 * prevent races.
	 * @param gl The GeoLoc
	 */
	private SiteData createObject(GeoLoc gl) {
		if (gl == null)
			return null;
		String gln = gl.getName();
		if (gln == null)
			return null;
		// this is nasty and also subject to race/collision
		String name = null;
		for (int uid = 1; uid <= 100000000; uid++) {
			String n = "sd_" + uid;
			if (cache.lookupObject(n) == null) {
				name = n;
				break;
			}
		}
		if (name == null)
			return null;
		HashMap<String, Object> attrs = new HashMap<String, Object>();
		attrs.put("geo_loc", gln);
		attrs.put("county", "");
		attrs.put("site_name", "");
		attrs.put("format", "");
		cache.createObject(name, attrs);

		// FIXME: nasty kludge to wait for object creation.
		// do this some other way.
		SiteData sd = null;
		for (int i=0; i<50; ++i) {
			TimeSteward.sleep_well(50);
			sd = cache.lookupObject(name);
			if (sd != null)
				break;
		}
		sd.setCounty(null);
		sd.setSiteName(null);
		sd.setFormat(null);
		return sd;
	}

}

