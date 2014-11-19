/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2014  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.lcs;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.Comparator;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;
import us.mn.state.dot.geokit.Position;
import us.mn.state.dot.map.Symbol;
import us.mn.state.dot.sched.Job;
import us.mn.state.dot.sonar.client.ProxyListener;
import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.CorridorBase;
import us.mn.state.dot.tms.DMS;
import us.mn.state.dot.tms.DMSHelper;
import us.mn.state.dot.tms.GeoLoc;
import us.mn.state.dot.tms.GeoLocHelper;
import us.mn.state.dot.tms.ItemStyle;
import us.mn.state.dot.tms.LaneConfiguration;
import us.mn.state.dot.tms.LCS;
import us.mn.state.dot.tms.LCSArray;
import us.mn.state.dot.tms.LCSArrayHelper;
import static us.mn.state.dot.tms.R_Node.MAX_LANES;
import static us.mn.state.dot.tms.client.IrisClient.WORKER;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.GeoLocManager;
import us.mn.state.dot.tms.client.proxy.MapAction;
import us.mn.state.dot.tms.client.proxy.PropertiesAction;
import us.mn.state.dot.tms.client.proxy.ProxyJList;
import us.mn.state.dot.tms.client.proxy.ProxyManager;
import us.mn.state.dot.tms.client.proxy.ProxyTheme;
import us.mn.state.dot.tms.client.proxy.StyleListModel;
import us.mn.state.dot.tms.client.proxy.TeslaAction;
import us.mn.state.dot.tms.client.widget.SmartDesktop;
import us.mn.state.dot.tms.utils.I18N;

/**
 * The LCSArrayManager class provides proxies for LCSArray objects.
 *
 * @author Douglas Lau
 */
public class LCSArrayManager extends ProxyManager<LCSArray> {

	/** LCS array map object marker */
	static private final LcsMarker MARKER = new LcsMarker();

	/** Listener for LCS proxy events */
	private final ProxyListener<LCS> lcs_listener = new ProxyListener<LCS>()
	{
		public void proxyAdded(final LCS proxy) {
			WORKER.addJob(new Job() {
				public void perform() {
					proxyAddedSlow(proxy.getArray());
				}
			});
		}
		public void enumerationComplete() { }
		public void proxyRemoved(LCS proxy) { }
		public void proxyChanged(LCS proxy, String a) { }
	};

	/** Action to blank the selected LCS array */
	private BlankLcsAction blankAction;

	/** Set the blank LCS action */
	public void setBlankAction(BlankLcsAction a) {
		blankAction = a;
	}

	/** Create a new LCS array manager */
	public LCSArrayManager(Session s, GeoLocManager lm) {
		super(s, lm);
	}

	/** Initialize the proxy manager */
	@Override
	public void initialize() {
		super.initialize();
		getLCSCache().addProxyListener(lcs_listener);
	}

	/** Dispose of the proxy manager */
	@Override
	public void dispose() {
		getLCSCache().removeProxyListener(lcs_listener);
		super.dispose();
	}

	/** Get the proxy type name */
	@Override
	public String getProxyType() {
		return "lcs";
	}

	/** Get the LCS array cache */
	@Override
	public TypeCache<LCSArray> getCache() {
		LcsCache cache = session.getSonarState().getLcsCache();
		return cache.getLCSArrays();
	}

	/** Get the LCS cache */
	private TypeCache<LCS> getLCSCache() {
		LcsCache cache = session.getSonarState().getLcsCache();
		return cache.getLCSs();
	}

	/** Create an LCS map tab */
	public LcsTab createTab() {
		return new LcsTab(session, this);
	}

	/** Check if user can read LCS arrays */
	public boolean canRead() {
		return session.canRead(LCSArray.SONAR_TYPE);
	}

	/** Get the shape for a given proxy */
	@Override
	protected Shape getShape(AffineTransform at) {
		return MARKER.createTransformedShape(at);
	}

	/** Create a theme for LCS arrays */
	@Override
	protected ProxyTheme<LCSArray> createTheme() {
		ProxyTheme<LCSArray> theme = new ProxyTheme<LCSArray>(this,
			MARKER);
		theme.addStyle(ItemStyle.AVAILABLE, ProxyTheme.COLOR_AVAILABLE);
		theme.addStyle(ItemStyle.DEPLOYED, ProxyTheme.COLOR_DEPLOYED);
		theme.addStyle(ItemStyle.SCHEDULED, ProxyTheme.COLOR_SCHEDULED);
		theme.addStyle(ItemStyle.MAINTENANCE,
			ProxyTheme.COLOR_UNAVAILABLE);
		theme.addStyle(ItemStyle.FAILED, ProxyTheme.COLOR_FAILED);
		theme.addStyle(ItemStyle.ALL);
		return theme;
	}

	/** Create a list cell renderer */
	@Override
	public ListCellRenderer createCellRenderer() {
		return new LCSArrayCellRenderer(this);
	}

	/** Comparator for ordering LCS arrays */
	private final Comparator<LCSArray> lcs_comparator =
		new Comparator<LCSArray>()
	{
		// FIXME: if an LCS array is moved, that will break the sort
		//        and lead to unpredictable results.
		public int compare(LCSArray l0, LCSArray l1) {
			GeoLoc g0 = LCSArrayHelper.lookupGeoLoc(l0);
			GeoLoc g1 = LCSArrayHelper.lookupGeoLoc(l1);
			if(g0 != null && g1 != null) {
				Integer c = compare(g0, g1);
				if(c != null)
					return c;
			}
			return l0.getName().compareTo(l1.getName());
		}
		protected Integer compare(GeoLoc g0, GeoLoc g1) {
			String c0 = GeoLocHelper.getCorridorID(g0);
			String c1 = GeoLocHelper.getCorridorID(g1);
			int c = c0.compareTo(c1);
			if(c != 0)
				return c;
			CorridorBase cb =
				session.getR_NodeManager().lookupCorridor(g0);
			if(cb != null) {
				Float f0 = cb.calculateMilePoint(g0);
				Float f1 = cb.calculateMilePoint(g1);
				if(f0 != null && f1 != null) {
					if(f0 < f1)
						return 1;
					else if(f0 > f1)
						return -1;
					else
						return 0;
				}
			}
			return null;
		}
	};

	/** Create a style list model for the given symbol */
	@Override
	protected StyleListModel<LCSArray> createStyleListModel(Symbol s) {
		return new StyleListModel<LCSArray>(this, s.getLabel()) {
			@Override
			protected Comparator<LCSArray> comparator() {
				return lcs_comparator;
			}
		};
	}

	/** Create a proxy JList */
	@Override
	public ProxyJList<LCSArray> createList() {
		ProxyJList<LCSArray> list = super.createList();
		list.setLayoutOrientation(JList.VERTICAL_WRAP);
		list.setVisibleRowCount(0);
		return list;
	}

	/** Check the style of the specified proxy */
	@Override
	public boolean checkStyle(ItemStyle is, LCSArray proxy) {
		long styles = proxy.getStyles();
		for(ItemStyle s: ItemStyle.toStyles(styles)) {
			if(s == is)
				return true;
		}
		return false;
	}

	/** Create a properties form for the specified proxy */
	@Override
	protected LCSArrayProperties createPropertiesForm(LCSArray la) {
		return new LCSArrayProperties(session, la);
	}

	/** Create a popup menu for a single LCS array selection */
	@Override
	protected JPopupMenu createPopupSingle(LCSArray la) {
		SmartDesktop desktop = session.getDesktop();
		JPopupMenu p = new JPopupMenu();
		p.add(makeMenuLabel(getDescription(la)));
		p.addSeparator();
		p.add(new MapAction(desktop.client, la,
			LCSArrayHelper.lookupGeoLoc(la)));
		p.addSeparator();
		if(LCSArrayHelper.isDeployed(la) && blankAction != null)
			p.add(blankAction);
		if(TeslaAction.isConfigured()) {
			p.addSeparator();
			for(int i = 1; i <= MAX_LANES; i++) {
				DMS dms = LCSArrayHelper.lookupDMS(la, i);
				if(dms != null)
					p.add(new TeslaAction<DMS>(dms));
			}
		}
		p.addSeparator();
		p.add(new PropertiesAction<LCSArray>(this, la));
		return p;
	}

	/** Create a popup menu for multiple objects */
	@Override
	protected JPopupMenu createPopupMulti(int n_selected) {
		JPopupMenu p = new JPopupMenu();
		p.add(new JLabel("" + n_selected + " " +
			I18N.get("lcs.arrays")));
		p.addSeparator();
		return p;
	}

	/** Find the map geo location for a proxy */
	@Override
	protected GeoLoc getGeoLoc(final LCSArray proxy) {
		LCS lcs = LCSArrayHelper.lookupLCS(proxy, 1);
		if(lcs != null) {
			String name = lcs.getName();
			DMS dms = DMSHelper.lookup(name);
			if(dms != null)
				return dms.getGeoLoc();
		}
		return null;
	}

	/** Get the layer zoom visibility threshold */
	@Override
	protected int getZoomThreshold() {
		return 14;
	}

	/** Get the lane configuration at an LCS array */
	public LaneConfiguration laneConfiguration(LCSArray proxy) {
		GeoLoc loc = LCSArrayHelper.lookupGeoLoc(proxy);
		CorridorBase cor = session.getR_NodeManager().lookupCorridor(
			loc);
		if(cor != null) {
			Position pos = GeoLocHelper.getWgs84Position(loc);
			if(pos != null)
				return cor.laneConfiguration(pos);
		}
		return new LaneConfiguration(0, 0);
	}
}
