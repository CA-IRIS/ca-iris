/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2015  Minnesota Department of Transportation
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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import us.mn.state.dot.sonar.SonarObject;

/**
 * A model for tracking selected proxy objects.
 *
 * @author Douglas Lau
 */
public class ProxySelectionModel<T extends SonarObject> {

	/** Currently selected proxy objects */
	private final Set<T> selected = new HashSet<T>();

	/** The listeners of this model */
	private final List<ProxySelectionListener<T>> lsnrs =
		new LinkedList<ProxySelectionListener<T>>();

	/** Add a proxy to the selection */
	public void addSelected(T proxy) {
		if (selected.add(proxy))
			fireSelectionAdded(proxy);
	}

	/** Remove a proxy from the selection */
	public void removeSelected(T proxy) {
		if (selected.remove(proxy))
			fireSelectionRemoved(proxy);
	}

	/** Set a proxy to be a single selection */
	public void setSelected(T proxy) {
		List<T> sel = getSelected();
		selected.clear();
		addSelected(proxy);
		for (T _proxy: sel) {
			if (_proxy != proxy)
				fireSelectionRemoved(_proxy);
		}
	}

	/** Clear the proxy selection */
	public void clearSelection() {
		List<T> sel = getSelected();
		selected.clear();
		for (T proxy: sel)
			fireSelectionRemoved(proxy);
	}

	/** Get a list of the selected proxies */
	public List<T> getSelected() {
		return new LinkedList<T>(selected);
	}

	/** Test if a proxy is selected */
	public boolean isSelected(T proxy) {
		return selected.contains(proxy);
	}

	/** Get the count of selected objects */
	public int getSelectedCount() {
		return selected.size();
	}

	/** Get a single selected proxy (if only one is selected) */
	public T getSingleSelection() {
		if (selected.size() == 1) {
			for (T proxy: selected)
				return proxy;
		}
		return null;
	}

	/** Add a proxy selection listener to the model */
	public void addProxySelectionListener(ProxySelectionListener<T> l) {
		lsnrs.add(l);
	}

	/** Remove a proxy selection listener from the model */
	public void removeProxySelectionListener(ProxySelectionListener<T> l) {
		lsnrs.remove(l);
	}

	/** Fire a selection added event to all listeners */
	private void fireSelectionAdded(T proxy) {
		for (ProxySelectionListener<T> l: lsnrs)
			l.selectionAdded(proxy);
	}

	/** Fire a selection removed event to all listeners */
	private void fireSelectionRemoved(T proxy) {
		for (ProxySelectionListener l: lsnrs)
			l.selectionRemoved(proxy);
	}

	/** Dispose of the proxy selection model */
	public void dispose() {
		lsnrs.clear();
		selected.clear();
	}
}
