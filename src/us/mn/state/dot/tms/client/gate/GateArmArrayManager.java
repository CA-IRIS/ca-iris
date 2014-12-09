/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2013-2014  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.gate;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.GeoLoc;
import us.mn.state.dot.tms.GeoLocHelper;
import us.mn.state.dot.tms.ItemStyle;
import us.mn.state.dot.tms.GateArm;
import us.mn.state.dot.tms.GateArmArray;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.GeoLocManager;
import us.mn.state.dot.tms.client.proxy.MapAction;
import us.mn.state.dot.tms.client.proxy.PropertiesAction;
import us.mn.state.dot.tms.client.proxy.ProxyManager;
import us.mn.state.dot.tms.client.proxy.ProxyTheme;
import us.mn.state.dot.tms.client.proxy.TeslaAction;
import us.mn.state.dot.tms.client.widget.SmartDesktop;
import us.mn.state.dot.tms.utils.I18N;

/**
 * The GateArmArrayManager class provides proxies for GateArmArray objects.
 *
 * @author Douglas Lau
 */
public class GateArmArrayManager extends ProxyManager<GateArmArray> {

	/** Gate arm map object marker */
	static private final GateArmMarker MARKER = new GateArmMarker();

	/** Create a new gate arm array manager */
	public GateArmArrayManager(Session s, GeoLocManager lm) {
		super(s, lm);
	}

	/** Get the sonar type name */
	@Override
	public String getSonarType() {
		return GateArmArray.SONAR_TYPE;
	}

	/** Get the proxy type name */
	@Override
	public String getProxyType() {
		return "gate.arm.array";
	}

	/** Get the gate arm array cache */
	@Override
	public TypeCache<GateArmArray> getCache() {
		return session.getSonarState().getGateArmArrays();
	}

	/** Create a gate arm map tab */
	@Override
	public GateArmTab createTab() {
		return new GateArmTab(session, this);
	}

	/** Check if user can read gate arms + arrays */
	@Override
	public boolean canRead() {
		return super.canRead()
		    && session.canRead(GateArm.SONAR_TYPE);
	}

	/** Get the shape for a given proxy */
	@Override
	protected Shape getShape(AffineTransform at) {
		return MARKER.createTransformedShape(at);
	}

	/** Create a theme for gate arms */
	@Override
	protected ProxyTheme<GateArmArray> createTheme() {
		ProxyTheme<GateArmArray> theme = new ProxyTheme<GateArmArray>(
			this, MARKER);
		theme.addStyle(ItemStyle.CLOSED, ProxyTheme.COLOR_AVAILABLE);
		theme.addStyle(ItemStyle.MOVING, ProxyTheme.COLOR_SCHEDULED);
		theme.addStyle(ItemStyle.OPEN, ProxyTheme.COLOR_DEPLOYED);
		theme.addStyle(ItemStyle.MAINTENANCE,
			ProxyTheme.COLOR_UNAVAILABLE);
		theme.addStyle(ItemStyle.FAILED, ProxyTheme.COLOR_FAILED);
		theme.addStyle(ItemStyle.ALL);
		return theme;
	}

	/** Check the style of the specified proxy */
	@Override
	public boolean checkStyle(ItemStyle is, GateArmArray proxy) {
		long styles = proxy.getStyles();
		for(ItemStyle s: ItemStyle.toStyles(styles)) {
			if(s == is)
				return true;
		}
		return false;
	}

	/** Create a properties form for the specified proxy */
	@Override
	protected GateArmArrayProperties createPropertiesForm(GateArmArray ga) {
		return new GateArmArrayProperties(session, ga);
	}

	/** Create a popup menu for a single gate arm selection */
	@Override
	protected JPopupMenu createPopupSingle(GateArmArray ga) {
		SmartDesktop desktop = session.getDesktop();
		JPopupMenu p = new JPopupMenu();
		p.add(makeMenuLabel(getDescription(ga)));
		p.addSeparator();
		p.add(new MapAction(desktop.client, ga, ga.getGeoLoc()));
		p.addSeparator();
		if(TeslaAction.isConfigured()) {
			p.addSeparator();
			p.add(new TeslaAction<GateArmArray>(ga));
		}
		p.addSeparator();
		p.add(new PropertiesAction<GateArmArray>(this, ga));
		return p;
	}

	/** Create a popup menu for multiple objects */
	@Override
	protected JPopupMenu createPopupMulti(int n_selected) {
		JPopupMenu p = new JPopupMenu();
		p.add(new JLabel("" + n_selected + " " + I18N.get(
			"gate.arm.arrays")));
		p.addSeparator();
		return p;
	}

	/** Find the map geo location for a proxy */
	@Override
	protected GeoLoc getGeoLoc(GateArmArray proxy) {
		return proxy.getGeoLoc();
	}

	/** Get the description of a proxy */
	@Override
	public String getDescription(GateArmArray proxy) {
		return proxy.getName() + " - " +
			GeoLocHelper.getDescription(getGeoLoc(proxy));
	}

	/** Get the layer zoom visibility threshold */
	@Override
	protected int getZoomThreshold() {
		return 15;
	}
}
