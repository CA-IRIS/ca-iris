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
import us.mn.state.dot.map.Symbol;
import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.DeviceStyle;
import us.mn.state.dot.tms.GeoLoc;
import us.mn.state.dot.tms.GeoLocHelper;
import us.mn.state.dot.tms.RampMeter;
import us.mn.state.dot.tms.RampMeterHelper;
import us.mn.state.dot.tms.RampMeterLock;
import us.mn.state.dot.tms.RampMeterQueue;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.GeoLocManager;
import us.mn.state.dot.tms.client.proxy.PropertiesAction;
import us.mn.state.dot.tms.client.proxy.ProxyManager;
import us.mn.state.dot.tms.client.proxy.ProxyTheme;
import us.mn.state.dot.tms.client.proxy.StyleListModel;
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
	static protected final MeterMarker MARKER = new MeterMarker();

	/** Color to display available meters */
	static protected final Color COLOR_AVAILABLE = new Color(64, 64, 192);

	/** Color to display warning meters */
	static protected final Color COLOR_WARNING = new Color(192, 96, 0);

	/** Color to display deployed meters */
	static protected final Color COLOR_DEPLOYED = new Color(192, 192, 0);

	/** Color to display metering meters */
	static protected final Color COLOR_METERING = new Color(0, 192, 0);

	/** Test if a meter is available */
	static protected boolean isAvailable(RampMeter proxy) {
		return RampMeterHelper.isActive(proxy) &&
		       !RampMeterHelper.isFailed(proxy) &&
		       !isMetering(proxy) &&
		       !needsMaintenance(proxy);
	}

	/** Test if a meter needs maintenance */
	static protected boolean needsMaintenance(RampMeter proxy) {
		RampMeterLock lck = RampMeterLock.fromOrdinal(proxy.getMLock());
		return lck == RampMeterLock.POLICE_PANEL ||
		       lck == RampMeterLock.KNOCK_DOWN;
	}

	/** Test if a meter is metering */
	static protected boolean isMetering(RampMeter proxy) {
		return proxy.getRate() != null &&
		       !RampMeterHelper.isFailed(proxy);
	}

	/** Test if a meter has a queue */
	static protected boolean queueExists(RampMeter proxy) {
		return RampMeterHelper.isActive(proxy) &&
		       !RampMeterHelper.isFailed(proxy) &&
		       proxy.getQueue() == RampMeterQueue.EXISTS.ordinal();
	}

	/** Test if a meter has a full queue */
	static protected boolean queueFull(RampMeter proxy) {
		return RampMeterHelper.isActive(proxy) &&
		       !RampMeterHelper.isFailed(proxy) &&
		       proxy.getQueue() == RampMeterQueue.FULL.ordinal();
	}

	/** User session */
	protected final Session session;

	/** Create a new meter manager */
	public MeterManager(Session s, TypeCache<RampMeter> c,
		GeoLocManager lm)
	{
		super(c, lm);
		session = s;
		cache.addProxyListener(this);
	}

	/** Create a style list model for the given symbol */
	protected StyleListModel<RampMeter> createStyleListModel(Symbol s) {
		return new MeterStyleModel(this, s.getLabel(),
			session.getSonarState().getConCache().getControllers());
	}

	/** Get the proxy type name */
	public String getProxyType() {
		return I18N.get("ramp.meter");
	}

	/** Get longer proxy type name for display */
	public String getLongProxyType() {
		return I18N.get("ramp.meter.long");
	}

	/** Get the shape for a given proxy */
	protected Shape getShape(AffineTransform at) {
		return MARKER.createTransformedShape(at);
	}

	/** Create a theme for ramp meters */
	protected ProxyTheme<RampMeter> createTheme() {
		ProxyTheme<RampMeter> theme = new ProxyTheme<RampMeter>(this,
			MARKER);
		theme.addStyle(DeviceStyle.AVAILABLE, COLOR_AVAILABLE);
		theme.addStyle(DeviceStyle.QUEUE_FULL, COLOR_WARNING);
		theme.addStyle(DeviceStyle.QUEUE_EXISTS, COLOR_DEPLOYED);
		theme.addStyle(DeviceStyle.METERING, COLOR_METERING);
		theme.addStyle(DeviceStyle.LOCKED, null,
			ProxyTheme.OUTLINE_LOCKED);
		theme.addStyle(DeviceStyle.MAINTENANCE,
			ProxyTheme.COLOR_UNAVAILABLE);
		theme.addStyle(DeviceStyle.FAILED, ProxyTheme.COLOR_FAILED);
		theme.addStyle(DeviceStyle.INACTIVE, ProxyTheme.COLOR_INACTIVE,
			ProxyTheme.OUTLINE_INACTIVE);
		theme.addStyle(DeviceStyle.ALL);
		return theme;
	}

	/** Check the style of the specified proxy */
	public boolean checkStyle(DeviceStyle ds, RampMeter proxy) {
		switch(ds) {
		case AVAILABLE:
			return isAvailable(proxy);
		case QUEUE_FULL:
			return queueFull(proxy);
		case QUEUE_EXISTS:
			return queueExists(proxy);
		case METERING:
			return isMetering(proxy);
		case LOCKED:
			return proxy.getMLock() != null;
		case MAINTENANCE:
			return needsMaintenance(proxy);
		case FAILED:
			return RampMeterHelper.isFailed(proxy);
		case NO_CONTROLLER:
			return proxy.getController() == null;
		case INACTIVE:
			return !RampMeterHelper.isActive(proxy);
		case ALL:
			return true;
		default:
			return false;
		}
	}

	/** Show the properties form for the selected proxy */
	public void showPropertiesForm() {
		if(s_model.getSelectedCount() == 1) {
			for(RampMeter meter: s_model.getSelected())
				showPropertiesForm(meter);
		}
	}

	/** Show the properteis form for the given proxy */
	protected void showPropertiesForm(RampMeter meter) {
		SmartDesktop desktop = session.getDesktop();
		desktop.show(new RampMeterProperties(session, meter));
	}

	/** Create a popup menu for the selected proxy object(s) */
	protected JPopupMenu createPopup() {
		int n_selected = s_model.getSelectedCount();
		if(n_selected < 1)
			return null;
		if(n_selected == 1) {
			for(RampMeter meter: s_model.getSelected())
				return createSinglePopup(meter);
		}
		JPopupMenu p = new JPopupMenu();
		p.add(new JLabel("" + n_selected + " " + I18N.get(
			"ramp.meters.short")));
		p.addSeparator();
		// FIXME: add turn on/off all actions
		return p;
	}

	/** Create a popup menu for a single ramp meter selection */
	protected JPopupMenu createSinglePopup(final RampMeter meter) {
		JPopupMenu p = new JPopupMenu();
		p.add(makeMenuLabel(getDescription(meter)));
		p.addSeparator();
		if(isMetering(meter)) {
			p.add(new ShrinkQueueAction(meter));
			p.add(new GrowQueueAction(meter));
			p.add(new TurnOffAction(meter));
		} else
			p.add(new TurnOnAction(meter));
		if(TeslaAction.isConfigured()) {
			p.addSeparator();
			p.add(new TeslaAction<RampMeter>(meter));
		}
		p.addSeparator();
		p.add(new PropertiesAction<RampMeter>(meter) {
			protected void do_perform() {
				showPropertiesForm(meter);
			}
		});
		return p;
	}

	/** Find the map geo location for a proxy */
	protected GeoLoc getGeoLoc(RampMeter proxy) {
		return proxy.getGeoLoc();
	}

	/** Get the description of a proxy */
	public String getDescription(RampMeter proxy) {
		return proxy.getName() + " - " +
			GeoLocHelper.getOnRampDescription(getGeoLoc(proxy));
	}

	/** Get the layer zoom visibility threshold */
	protected int getZoomThreshold() {
		return 15;
	}
}
