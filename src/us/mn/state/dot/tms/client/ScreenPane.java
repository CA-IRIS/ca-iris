/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2007-2012  Minnesota Department of Transportation
 * Copyright (C) 2010  AHMCT, University of California
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import us.mn.state.dot.geokit.Position;
import us.mn.state.dot.geokit.SphericalMercatorPosition;
import us.mn.state.dot.geokit.ZoomLevel;
import us.mn.state.dot.map.LayerState;
import us.mn.state.dot.map.MapBean;
import us.mn.state.dot.map.MapToolBar;
import us.mn.state.dot.sonar.SonarObject;
import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.MapExtent;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.client.proxy.ProxyLayerState;
import us.mn.state.dot.tms.client.proxy.ProxySelectionListener;
import us.mn.state.dot.tms.client.proxy.ProxySelectionModel;
import us.mn.state.dot.tms.client.roads.SegmentLayerState;
import us.mn.state.dot.tms.client.toolbar.IrisToolBar;
import static us.mn.state.dot.tms.client.widget.Widgets.UI;

/**
 * A screen pane is a pane which contains all components for one screen on
 * the IRIS client.
 *
 * @author Douglas Lau
 * @author Michael Darter
 */
public class ScreenPane extends JPanel {

	/** Side panel for tabs and menu */
	private final JPanel side_panel;

	/** Tabbed side pane */
	protected final JTabbedPane tab_pane;

	/** Map to be displayed on the screen pane */
	protected final MapBean map;

	/** Get the map */
	public MapBean getMap() {
		return map;
	}

	/** Map tool bar */
	protected final MapToolBar map_bar;

	/** Map panel */
	protected final JPanel map_panel;

	/** IRIS tool bar */
	protected final IrisToolBar tool_bar;

	/** List of tab switchers */
	protected final LinkedList<TabSwitcher> switchers =
		new LinkedList<TabSwitcher>();

	/** Create a new screen pane */
	public ScreenPane() {
		setLayout(new BorderLayout());
		side_panel = new JPanel(new BorderLayout());
		side_panel.setMinimumSize(UI.dimension(500, 200));
		side_panel.setPreferredSize(UI.dimension(500, 200));
		tab_pane = new JTabbedPane(JTabbedPane.TOP);
		side_panel.add(tab_pane, BorderLayout.CENTER);
		add(side_panel, BorderLayout.WEST);
		map = new MapBean(true);
		map.setBackground(new Color(208, 216, 208));
		map_bar = createMapToolBar();
		tool_bar = new IrisToolBar(map);
		tool_bar.setFloatable(false);
		map_panel = createMapPanel();
		add(map_panel, BorderLayout.CENTER);
		tab_pane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				map.addPointSelector(null);
				setSelectedLayer(getSelectedHomeLayer());
				storeSelectedTabIndex();
			}
		});
	}

	/** Get the currently selected tab's home layer */
	private ProxyLayerState getSelectedHomeLayer() {
		Component tab = tab_pane.getSelectedComponent();
		if(tab instanceof MapTab)
			return getHomeProxyLayerState((MapTab)tab);
		else
			return null;
	}

	/** Get the home proxy layer state for a map tab */
	private ProxyLayerState getHomeProxyLayerState(MapTab mt) {
		LayerState ls = mt.getHomeLayer();
		if(ls instanceof ProxyLayerState)
			return (ProxyLayerState)ls;
		else
			return null;
	}

	/** Most recently selected layer */
	protected ProxyLayerState sel_layer;

	/** Set the selected layer for the screen pane */
	private void setSelectedLayer(ProxyLayerState sel) {
		if(sel_layer != null && sel != sel_layer)
			sel_layer.setTabSelected(false);
		if(sel != null)
			sel.setTabSelected(true);
		sel_layer = sel;
	}

	/** Last selected tab, which stores the index of last user selected
	 * tab. A field is used to track this, rather than dynamically
	 * calling tab_pane.getSelectedIndex() because if the method is 
	 * called during app shutdown, it can erroneously return a -1. */
	private int sel_tab;

	/** Store the currently selected tab index. If no tab is 
	 *  selected (e.g. during tear down), the change is ignored. */
	private void storeSelectedTabIndex() {
		int sel = tab_pane.getSelectedIndex();
		if(sel >= 0)
			sel_tab = sel;
	}

	/** Get the index of the currently selected tab */
	public int getSelectedTabIndex() {
		return sel_tab;
	}

	/** Set the currently selected tab */
	public void setSelectedTabIndex(int i) {
		if(i >= 0 && i < tab_pane.getTabCount())
			tab_pane.setSelectedIndex(i);
	}

	/** Add a tab to the screen pane */
	public void addTab(MapTab mt) {
		tab_pane.addTab(mt.getName(), null, mt, mt.getTip());
		mt.setMap(map);
		ProxyLayerState pls = getHomeProxyLayerState(mt);
		if(pls != null) {
			if(tab_pane.getTabCount() == 1)
				setSelectedLayer(pls);
			TabSwitcher ts = new TabSwitcher(mt,
				pls.getSelectionModel());
			switchers.add(ts);
		}
	}

	/** Class to listen for proxy selection events and select tabs */
	protected class TabSwitcher implements ProxySelectionListener {
		protected final MapTab tab;
		protected final ProxySelectionModel model;
		protected TabSwitcher(MapTab mt, ProxySelectionModel psm) {
			tab = mt;
			model = psm;
			model.addProxySelectionListener(this);
		}
		protected void dispose() {
			model.removeProxySelectionListener(this);
		}
		public void selectionAdded(SonarObject proxy) {
			tab_pane.setSelectedComponent(tab);
		}
		public void selectionRemoved(SonarObject proxy) { }
	}

	/** Remove all the tabs */
	public void removeTabs() {
		for(TabSwitcher ts: switchers)
			ts.dispose();
		switchers.clear();
		tab_pane.removeAll();
	}

	/** Set the menu bar */
	public void setMenuBar(IMenuBar bar) {
		side_panel.removeAll();
		if(bar != null)
			side_panel.add(bar, BorderLayout.NORTH);
		side_panel.add(tab_pane, BorderLayout.CENTER);
	}

	/** Create the map panel */
	protected JPanel createMapPanel() {
		JPanel p = new JPanel(new BorderLayout());
		p.setBorder(BorderFactory.createBevelBorder(
			BevelBorder.LOWERED));
		p.add(map, BorderLayout.CENTER);
		JPanel mp = new JPanel(new BorderLayout());
		mp.add(map_bar, BorderLayout.NORTH);
		mp.add(p, BorderLayout.CENTER);
		mp.add(tool_bar, BorderLayout.SOUTH);
		return mp;
	}

	/** Create a map tool bar with appropriate view buttons */
	protected MapToolBar createMapToolBar() {
		MapToolBar b = new MapToolBar(map);
		b.setFloatable(false);
		return b;
	}

	/** Create the tool panels */
	public void createToolPanels(Session s) {
		TypeCache<MapExtent> tc = s.getSonarState().getMapExtents();
		for(MapExtent me: tc) {
			map_bar.addButton(createMapButton(me));
		}
		tool_bar.createToolPanels(s);
	}

	/** Clear the tool panels */
	public void clearToolPanels() {
		map_bar.clear();
		tool_bar.clear();
	}

	/** Create a map extent button */
	protected JButton createMapButton(final MapExtent me) {
		JButton b = new JButton(me.getName());
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				setMapExtent(me);
			}
		});
		return b;
	}

	/** Set the map extent */
	public void setMapExtent(MapExtent me) {
		Point2D ctr = createCenter(me);
		ZoomLevel zoom = ZoomLevel.fromOrdinal(me.getZoom());
		if(ctr != null && zoom != null)
			map.getModel().setExtent(ctr, zoom);
	}

	/** Create a center point in spherical mercator units */
	static protected Point2D createCenter(MapExtent me) {
		float lat = me.getLat();
		float lon = me.getLon();
		SphericalMercatorPosition c = SphericalMercatorPosition.convert(
			new Position(lat, lon));
		return new Point2D.Double(c.getX(), c.getY());
	}
}
