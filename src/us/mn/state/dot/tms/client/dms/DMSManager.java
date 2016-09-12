/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2016  Minnesota Department of Transportation
 * Copyright (C) 2010-2015  AHMCT, University of California
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
package us.mn.state.dot.tms.client.dms;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;

import us.mn.state.dot.sonar.client.ProxyListener;
import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.Controller;
import us.mn.state.dot.tms.DMS;
import us.mn.state.dot.tms.DMSHelper;
import us.mn.state.dot.tms.GeoLoc;
import us.mn.state.dot.tms.ItemStyle;
import us.mn.state.dot.tms.RasterGraphic;
import us.mn.state.dot.tms.SignMessageHelper;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.GeoLocManager;
import us.mn.state.dot.tms.client.proxy.MapAction;
import us.mn.state.dot.tms.client.proxy.PropertiesAction;
import us.mn.state.dot.tms.client.proxy.ProxyJList;
import us.mn.state.dot.tms.client.proxy.ProxyManager;
import us.mn.state.dot.tms.client.proxy.ProxyTheme;
import us.mn.state.dot.tms.client.proxy.TeslaAction;
import us.mn.state.dot.tms.client.widget.SmartDesktop;
import us.mn.state.dot.tms.utils.I18N;

/**
 * A DMS manager is a container for SONAR DMS objects.
 *
 * @author Douglas Lau
 * @author Michael Darter
 * @author Travis Swanston
 * @author Dan Rossiter
 */
public class DMSManager extends ProxyManager<DMS> {

	/** Color definition for AWS controlled style */
	static private final Color COLOR_HELIOTROPE = new Color(1, 0.5f,0.9f);

	/** Mapping of DMS to page one rasters */
	private final HashMap<DMS, RasterGraphic> rasters = new HashMap<>();

	/** Action to blank the selected DMS */
	private BlankDmsAction blankAction;

	/** Detect transition to "power cycle" state for sign */
	private final ProxyListener<Controller> controller_listener = new ProxyListener<Controller>() {

		@Override
		public void proxyAdded(Controller proxy) { }

		@Override
		public void enumerationComplete() { }

		@Override
		public void proxyRemoved(Controller proxy) { }

		@Override
		public void proxyChanged(Controller proxy, String a) {
			String cname = proxy.getName();
			String maint = proxy.getMaint();
			if ("maint".equals(a) && cname != null && maint != null &&
				maint.toLowerCase().contains("power cycle")) {
				for (DMS dms : getCache()) {
					String dcname = dms.getController().getName();
					if (cname.equals(dcname)) {
						JOptionPane.showMessageDialog(null,
                                String.format(I18N.get("dms.notification.attention_required"), dms.getName()));
						break;
					}
				}
			}
		}

	};

	/** Detect transition from AWS message to blank sign */
	private final ProxyListener<DMS> dms_listener = new ProxyListener<DMS>() {

		/** All AWS-deployed signs */
		private final Set<String> aws_signs = new HashSet<>();

		{
			for (DMS dms : getCache()) {
				handleAwsChange(dms);
			}
		}

		@Override
		public void proxyAdded(DMS proxy) {
			handleAwsChange(proxy);
		}

		@Override
		public void enumerationComplete() { }

		@Override
		public synchronized void proxyRemoved(DMS proxy) {
			aws_signs.remove(proxy.getName());
		}

		@Override
		public void proxyChanged(DMS proxy, String a) {
			if ("aws_controlled".equals(a)) {
				handleAwsChange(proxy);
			}
		}

		/** @param proxy The DMS where AWS state has changed. */
		private synchronized void handleAwsChange(DMS proxy) {
			if (DMSHelper.isAwsDeployed(proxy)) {
				aws_signs.add(proxy.getName());
			} else if (aws_signs.remove(proxy.getName()) &&
				SignMessageHelper.isBlank(proxy.getMessageCurrent())) {
				JOptionPane.showMessageDialog(null,
					String.format(I18N.get("dms.notification.attention_required"), proxy.getName()));
			}
		}

	};

	/** Set the blank DMS action */
	public void setBlankAction(BlankDmsAction a) {
		blankAction = a;
	}

	/** Create a new DMS manager */
	public DMSManager(Session s, GeoLocManager lm) {
		super(s, lm, ItemStyle.ALL);
		s_model.setAllowMultiple(true);

		// if user can't do anything about these changes then don't bother them
		if (s.canUpdate(DMS.SONAR_TYPE)) {
			getCache().addProxyListener(dms_listener);
			session.getSonarState().getConCache().getControllers().addProxyListener(controller_listener);
		}
	}

	/** Get the sonar type name */
	@Override
	public String getSonarType() {
		return DMS.SONAR_TYPE;
	}

	/** Get the DMS cache */
	@Override
	public TypeCache<DMS> getCache() {
		return session.getSonarState().getDmsCache().getDMSs();
	}

	/** Create a DMS map tab */
	@Override
	public DMSTab createTab() {
		return new DMSTab(session, this);
	}

	/** Create a theme for DMSs */
	@Override
	protected ProxyTheme<DMS> createTheme() {
		// NOTE: the ordering of themes controls which color is used
		//       to render the sign icon on the map
		ProxyTheme<DMS> theme = new ProxyTheme<>(this,
			new DmsMarker());
		theme.addStyle(ItemStyle.AVAILABLE, ProxyTheme.COLOR_AVAILABLE);
		theme.addStyle(ItemStyle.DEPLOYED, ProxyTheme.COLOR_DEPLOYED);
		theme.addStyle(ItemStyle.SCHEDULED, ProxyTheme.COLOR_SCHEDULED);
		if (SystemAttrEnum.DMS_AWS_ENABLE.getBoolean())
			theme.addStyle(ItemStyle.AWS_DEPLOYED, ProxyTheme.COLOR_AWS_DEPLOYED);
		theme.addStyle(ItemStyle.MAINTENANCE,
			ProxyTheme.COLOR_UNAVAILABLE);
		theme.addStyle(ItemStyle.FAILED, ProxyTheme.COLOR_FAILED);
		if (SystemAttrEnum.DMS_AWS_ENABLE.getBoolean())
			theme.addStyle(ItemStyle.AWS_CONTROLLED,
				COLOR_HELIOTROPE);
		// NOTE: If a sign doesn't fit in one of the other themes,
		//       it will be rendered using the ALL theme.
		theme.addStyle(ItemStyle.ALL); //FIXME CA-MN-MERGE , ProxyTheme.COLOR_INACTIVE,	ProxyTheme.OUTLINE_INACTIVE);
		return theme;
	}

	/** Create a list cell renderer */
	@Override
	public ListCellRenderer<DMS> createCellRenderer() {
		return new DmsCellRenderer(getCellSize()) {
			@Override protected RasterGraphic getPageOne(DMS dms) {
				return rasters.get(dms);
			}
		};
	}

	/** Add a proxy to the manager */
	@Override
	protected void proxyAddedSwing(DMS dms) {
		updateRaster(dms);
		super.proxyAddedSwing(dms);
	}

	/** Update one DMS raster */
	private void updateRaster(DMS dms) {
		rasters.put(dms, DMSHelper.getPageOne(dms));
	}

	/** Enumeration complete */
	@Override
	protected void enumerationCompleteSwing(Collection<DMS> proxies) {
		super.enumerationCompleteSwing(proxies);
		for (DMS dms : proxies)
			updateRaster(dms);
	}

	/** Check if an attribute change is interesting */
	@Override
	protected boolean checkAttributeChange(String a) {
		return super.checkAttributeChange(a)
		    || "messageCurrent".equals(a);
	}

	/** Called when a proxy attribute has changed */
	@Override
	protected void proxyChangedSwing(DMS dms, String a) {
		if ("messageCurrent".equals(a))
			updateRaster(dms);
		super.proxyChangedSwing(dms, a);
	}

	/** Check if a DMS style is visible */
	@Override
	protected boolean isStyleVisible(DMS dms) {
		return !DMSHelper.isHidden(dms);
	}

	/** Create a proxy JList */
	@Override
	public ProxyJList<DMS> createList() {
		ProxyJList<DMS> list = super.createList();
		list.setLayoutOrientation(JList.VERTICAL_WRAP);
		list.setVisibleRowCount(0);
		return list;
	}

	/** Create a properties form for the specified proxy */
	@Override
	protected DMSProperties createPropertiesForm(DMS dms) {
		return new DMSProperties(session, dms);
	}

	/** Create a popup menu for a single DMS selection */
	@Override
	protected JPopupMenu createPopupSingle(DMS dms) {
		SmartDesktop desktop = session.getDesktop();
		JPopupMenu p = new JPopupMenu();
		p.add(makeMenuLabel(getDescription(dms)));
		p.addSeparator();
		p.add(new MapAction<>(desktop.client, dms, dms.getGeoLoc()));
		p.addSeparator();
		if (blankAction != null)
			p.add(blankAction);
		if (TeslaAction.isConfigured())
			p.add(new TeslaAction<>(dms));
		p.add(new PropertiesAction<>(this, dms));
		return p;
	}

	/** Create a popup menu for multiple objects */
	@Override
	protected JPopupMenu createPopupMulti(int n_selected) {
		JPopupMenu p = new JPopupMenu();
		p.add(new JLabel(I18N.get("dms.title") + ": " +
			n_selected));
		p.addSeparator();
		if (blankAction != null)
			p.add(blankAction);
		return p;
	}

	/** Find the map geo location for a proxy */
	@Override
	protected GeoLoc getGeoLoc(DMS proxy) {
		return proxy.getGeoLoc();
	}

	/** Check the style of the specified proxy */
	@Override
	public boolean checkStyle(ItemStyle is, DMS proxy) {
		long styles = proxy.getStyles();
		for (ItemStyle s: ItemStyle.toStyles(styles)) {
			if (s == is)
				return true;
		}
		return false;
	}

	/** Get the layer zoom visibility threshold */
	@Override
	protected int getZoomThreshold() {
		return 12;
	}

	/** Dispose of the DMS manager */
	@Override
	public void dispose() {
		super.dispose();
		getCache().removeProxyListener(dms_listener);
		session.getSonarState().getConCache().getControllers().removeProxyListener(controller_listener);
	}
}
