/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2013  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.meter;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.GeoLoc;
import us.mn.state.dot.tms.GeoLocHelper;
import us.mn.state.dot.tms.ItemStyle;
import us.mn.state.dot.tms.RampMeter;
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
 * The MeterManager class provides proxies for RampMeter objects.
 *
 * @author Douglas Lau
 */
public class MeterManager extends ProxyManager<RampMeter> {

	/** Ramp meter map object marker */
	static private final MeterMarker MARKER = new MeterMarker();

	/** Color to display available meters */
	static private final Color COLOR_AVAILABLE = new Color(64, 64, 192);

	/** Color to display warning meters */
	static private final Color COLOR_WARNING = new Color(192, 96, 0);

	/** Color to display deployed meters */
	static private final Color COLOR_DEPLOYED = new Color(192, 192, 0);

	/** Color to display metering meters */
	static private final Color COLOR_METERING = new Color(0, 192, 0);

	/** Create a new meter manager */
	public MeterManager(Session s, GeoLocManager lm) {
		super(s, lm);
	}

	/** Get the proxy type name */
	@Override
	public String getProxyType() {
		return "ramp.meter";
	}

	/** Get longer proxy type name for display */
	@Override
	public String getLongProxyType() {
		return "ramp.meter.long";
	}

	/** Get the ramp meter cache */
	@Override
	public TypeCache<RampMeter> getCache() {
		return session.getSonarState().getRampMeters();
	}

	/** Create a ramp meter map tab */
	public RampMeterTab createTab() {
		return new RampMeterTab(session, this);
	}

	/** Check if user can read ramp meters */
	public boolean canRead() {
		return session.canRead(RampMeter.SONAR_TYPE);
	}

	/** Get the shape for a given proxy */
	@Override
	protected Shape getShape(AffineTransform at) {
		return MARKER.createTransformedShape(at);
	}

	/** Create a theme for ramp meters */
	@Override
	protected ProxyTheme<RampMeter> createTheme() {
		ProxyTheme<RampMeter> theme = new ProxyTheme<RampMeter>(this,
			MARKER);
		theme.addStyle(ItemStyle.AVAILABLE, COLOR_AVAILABLE);
		theme.addStyle(ItemStyle.QUEUE_FULL, COLOR_WARNING);
		theme.addStyle(ItemStyle.QUEUE_EXISTS, COLOR_DEPLOYED);
		theme.addStyle(ItemStyle.METERING, COLOR_METERING);
		theme.addStyle(ItemStyle.LOCKED, null,
			ProxyTheme.OUTLINE_LOCKED);
		theme.addStyle(ItemStyle.MAINTENANCE,
			ProxyTheme.COLOR_UNAVAILABLE);
		theme.addStyle(ItemStyle.FAILED, ProxyTheme.COLOR_FAILED);
		theme.addStyle(ItemStyle.INACTIVE, ProxyTheme.COLOR_INACTIVE,
			ProxyTheme.OUTLINE_INACTIVE);
		theme.addStyle(ItemStyle.ALL);
		return theme;
	}

	/** Check the style of the specified proxy */
	@Override
	public boolean checkStyle(ItemStyle is, RampMeter proxy) {
		long styles = proxy.getStyles();
		for(ItemStyle s: ItemStyle.toStyles(styles)) {
			if(s == is)
				return true;
		}
		return false;
	}

	/** Create a properties form for the specified proxy */
	@Override
	protected RampMeterProperties createPropertiesForm(RampMeter meter) {
		return new RampMeterProperties(session, meter);
	}

	/** Create a popup menu for a single ramp meter selection */
	@Override
	protected JPopupMenu createPopupSingle(RampMeter meter) {
		SmartDesktop desktop = session.getDesktop();
		JPopupMenu p = new JPopupMenu();
		p.add(makeMenuLabel(getDescription(meter)));
		p.addSeparator();
		p.add(new MapAction(desktop.client, meter, meter.getGeoLoc()));
		if(session.isUpdatePermitted(meter)) {
			p.addSeparator();
			if(meter.getRate() != null) {
				p.add(new ShrinkQueueAction(meter, true));
				p.add(new GrowQueueAction(meter, true));
				p.add(new TurnOffAction(meter, true));
			} else
				p.add(new TurnOnAction(meter, true));
		}
		if(TeslaAction.isConfigured()) {
			p.addSeparator();
			p.add(new TeslaAction<RampMeter>(meter));
		}
		p.addSeparator();
		p.add(new PropertiesAction<RampMeter>(this, meter));
		return p;
	}

	/** Create a popup menu for multiple objects */
	@Override
	protected JPopupMenu createPopupMulti(int n_selected) {
		JPopupMenu p = new JPopupMenu();
		p.add(new JLabel("" + n_selected + " " + I18N.get(
			"ramp.meters.short")));
		p.addSeparator();
		// FIXME: add turn on/off all actions
		return p;
	}

	/** Find the map geo location for a proxy */
	@Override
	protected GeoLoc getGeoLoc(RampMeter proxy) {
		return proxy.getGeoLoc();
	}

	/** Get the description of a proxy */
	@Override
	public String getDescription(RampMeter proxy) {
		return proxy.getName() + " - " +
			GeoLocHelper.getOnRampDescription(getGeoLoc(proxy));
	}

	/** Get the layer zoom visibility threshold */
	@Override
	protected int getZoomThreshold() {
		return 15;
	}
}
