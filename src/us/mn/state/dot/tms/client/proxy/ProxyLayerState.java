/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2013  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.proxy;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JPopupMenu;
import us.mn.state.dot.map.LayerChange;
import us.mn.state.dot.map.LayerState;
import us.mn.state.dot.map.MapBean;
import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.MapSearcher;
import us.mn.state.dot.sonar.SonarObject;

/**
 * Base class for all SONAR proxy map layer states.
 *
 * @author Douglas Lau
 */
public class ProxyLayerState<T extends SonarObject> extends LayerState {

	/** Proxy manager */
	protected final ProxyManager<T> manager;

	/** Proxy selection model */
	protected final ProxySelectionModel<T> model;

	/** Get the proxy selection model */
	public ProxySelectionModel<T> getSelectionModel() {
		return model;
	}

	/** Listener for proxy selection events */
	protected final ProxySelectionListener<T> listener;

	/** Create a new sonar proxy layer state */
	public ProxyLayerState(ProxyLayer<T> layer, MapBean mb) {
		super(layer, mb);
		manager = layer.getManager();
		model = manager.getSelectionModel();
		listener = new ProxySelectionListener<T>() {
			public void selectionAdded(T proxy) {
				setSelection();
			}
			public void selectionRemoved(T proxy) {
				setSelection();
			}
		};
		model.addProxySelectionListener(listener);
	}

	/** Set the selection */
	protected void setSelection() {
		List<T> proxies = model.getSelected();
		LinkedList<MapGeoLoc> sel = new LinkedList<MapGeoLoc>();
		for(T proxy: proxies) {
			MapGeoLoc loc = manager.findGeoLoc(proxy);
			if(loc != null)
				sel.add(loc);
		}
		setSelections(sel.toArray(new MapGeoLoc[0]));
	}

	/** Dispose of the layer state */
	public void dispose() {
		super.dispose();
		model.removeProxySelectionListener(listener);
	}

	/** Flag to indicate the tab is selected */
	protected boolean tab_selected = false;

	/** Set the tab selected flag */
	public void setTabSelected(boolean ts) {
		if(tab_selected && !ts)
			model.clearSelection();
		tab_selected = ts;
		if(visible == null)
			notifyLayerChangedListeners(LayerChange.visibility);
	}

	/** Get the visibility flag */
	public boolean isVisible() {
		if(visible == null)
			return tab_selected || isZoomVisible();
		else
			return visible;
	}

	/** Is the layer visible at the current zoom level? */
	protected boolean isZoomVisible() {
		return manager.isVisible(
			map.getModel().getZoomLevel().ordinal());
	}

	/** Iterate through all shapes in the layer */
	public MapObject forEach(MapSearcher s) {
		return manager.forEach(s, getScale());
	}

	/** Do mouse click event processing */
	protected void doClick(MouseEvent e, MapObject o) {
		T proxy = manager.findProxy(o);
		if(proxy != null) {
			int m = e.getModifiersEx();
			if((m & InputEvent.CTRL_DOWN_MASK) != 0)
				model.addSelected(proxy);
			else
				model.setSelected(proxy);
		} else
			model.clearSelection();
		setSelection();
	}

	/** Do left-click event processing */
	protected void doLeftClick(MouseEvent e, MapObject o) {
		doClick(e, o);
	}

	/** Do right-click event processing */
	protected void doRightClick(MouseEvent e, MapObject o) {
		doClick(e, o);
		manager.showPopupMenu(e);
	}
}
