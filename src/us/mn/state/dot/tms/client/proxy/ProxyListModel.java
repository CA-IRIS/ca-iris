/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2007-2016  Minnesota Department of Transportation
 * Copyright (C) 2014-2015  AHMCT, University of California
 * Copyright (C) 2015 California Department of Transportation
 * Copyright (C) 2015-2016  Southwest Research Institute
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import javax.swing.AbstractListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import us.mn.state.dot.sonar.SonarObject;
import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.utils.IterableUtil;

/**
 * A filterable swing ListModel kept in sync with a SONAR TypeCache.
 *
 * @author Douglas Lau
 * @author Travis Swanston
 * @author Dan Rossiter
 * @author Jacob Barde
 */
public class ProxyListModel<T extends SonarObject>
	extends AbstractListModel<T>
{
	/** Filter interface to show/hide elements within the model */
	public interface Filter<T extends SonarObject> {
		boolean accept(T element);
	}

	/** Proxy type cache */
	private final TypeCache<T> cache;

	/** Proxy list */
	private final ArrayList<T> list;

	/** Proxy comparator */
	private final Comparator<T> comp = comparator();

	/** List of displayed indices if filtered */
	private final ArrayList<Integer> indices = new ArrayList<>();

	/** The filter to determine which elements should be shown */
	private Filter<T> filter;

	/** Flag used by applyFilter to prevent unintended recursion. */
	private boolean applyingFilter = false;

	/** Get a proxy comparator */
	protected Comparator<T> comparator() {
		return new ProxyComparator<>();
	}

	/** Proxy listener for SONAR updates */
	private final SwingProxyAdapter<T> swing_listener = new SwingProxyAdapter<T>() {
		protected Comparator<T> comparator() {
			return ProxyListModel.this.comp;
		}
		protected void proxyAddedSwing(T proxy) {
			int i = doProxyAdded(proxy);
			if (i >= 0)
				fireIntervalAdded(this, i, i);
		}
		protected void enumerationCompleteSwing(Collection<T> proxies) {
			for (T proxy: proxies) {
				if (check(proxy))
					list.add(proxy);
			}
			int sz = list.size() - 1;
			if (sz >= 0)
				fireIntervalAdded(this, 0, sz);
		}
		protected void proxyRemovedSwing(T proxy) {
			int i = doProxyRemoved(proxy, false);
			if (i >= 0)
				fireIntervalRemoved(this, i, i);
		}
		protected void proxyChangedSwing(T proxy, String attr) {
			ProxyListModel.this.proxyChangedSwing(proxy);
		}
	};

	/** Listens for changes in backing data in order to re-filter as needed */
	private final ListDataListener filter_listener = new ListDataListener(){
		@Override
		public void intervalAdded(ListDataEvent e) {
			applyFilter();
		}

		@Override
		public void intervalRemoved(ListDataEvent e) {
			applyFilter();
		}

		@Override
		public void contentsChanged(ListDataEvent e) {
			applyFilter();
		}
	};

	/** Create a new proxy list model */
	public ProxyListModel(TypeCache<T> c) {
		cache = c;
		list = new ArrayList<>();
		addListDataListener(filter_listener);
	}

	/** Initialize the proxy list model. This cannot be done in the
	 * constructor because subclasses may not be fully constructed. */
	public void initialize() {
		cache.addProxyListener(swing_listener);
	}

	/** Dispose of the proxy model */
	public void dispose() {
		removeListDataListener(filter_listener);
		cache.removeProxyListener(swing_listener);
		swing_listener.dispose();
	}

	/** Check if a proxy is included in the list */
	protected boolean check(T proxy) {
		return true;
	}

	/** Add a new proxy to the model */
	private int doProxyAdded(T proxy) {
		if (check(proxy)) {
			int n_size = list.size();
			for (int i = 0; i < n_size; ++i) {
				int c = comp.compare(proxy, list.get(i));
				if (c == 0)
					return -1;
				if (c < 0) {
					list.add(i, proxy);
					return i;
				}
			}
			list.add(proxy);
			return n_size;
		} else
			return -1;
	}

	/** Remove a proxy from the model */
	protected int doProxyRemoved(T proxy) {
		checkRemove(proxy);
		return doProxyRemoved(proxy, true);
	}

	/** remove a proxy from model while defining filtered or not */
	protected int doProxyRemoved(T proxy, boolean useFilteredList) {
		int i = getIndex(proxy, useFilteredList);
		if (i >= 0)
			list.remove(i);
		return i;
	}

	/** Check when proxy is removed */
	protected void checkRemove(T proxy) {
		// subclasses can override
	}

	/** Change a proxy in the list model */
	private void proxyChangedSwing(T proxy) {
		int pre = doProxyRemoved(proxy, false);
		int post = doProxyAdded(proxy);
		if (pre >= 0 && post >= 0) {
			int r0 = Math.min(pre, post);
			int r1 = Math.max(pre, post);
			fireContentsChanged(this, r0, r1);
		} else if (pre >= 0 && post < 0)
			fireIntervalRemoved(this, pre, pre);
		else if (pre < 0 && post >= 0)
			fireIntervalAdded(this, post, post);
	}

	/** Sets the filter to be applied against members */
	public void setFilter(Filter<T> f) {
		filter = f;
		applyFilter();
	}

	/** Gets the filter to be applied against members */
	public Filter<T> getFilter() {
		return filter;
	}

	/** Applies the filter against current members */
	private void applyFilter() {
		// prevent recursing through fireContentsChanged
		if (applyingFilter)
			return;

		applyingFilter = true;
		ArrayList<Integer> oldIndices = new ArrayList<>(indices);
		indices.clear();

		Filter<T> f = filter;
		if (f != null) {
			for (int i = 0; i < list.size(); i++) {
				if (f.accept(list.get(i)))
					indices.add(i);
			}
		}

		if (oldIndices.size() != indices.size() || !IterableUtil.sequenceEqual(oldIndices, indices)) {
			fireContentsChanged(this, 0, getSize(true) - 1);
		}

		applyingFilter = false;
	}

	/** Gets the size */
	@Override
	public int getSize() {
		return getSize(true);
	}

	/** get the size while defining filtered or not */
	public int getSize(boolean useFilteredList) {
		return (useFilteredList && filter != null)
			? indices.size() : list.size();
	}

	/** Get the element at the specified index (for ListModel) */
	@Override
	public T getElementAt(int index) {
		return getElementAt(index, true);
	}

	/** get the element at specified index while defining filtered or not */
	public T getElementAt(int index, boolean useFilteredList) {
		int idx = index;
		if(useFilteredList && filter != null)
			idx = indices.get(index);
		return list.get(idx);
	}

	/** Get the proxy at the specified index */
	public T getProxy(int index) {
		return getProxy(index, true);
	}

	/** get the proxy at specified index while defining filtered or not */
	public T getProxy(int index, boolean useFilteredList) {
		return getElementAt(index, useFilteredList);
	}

	/** Get the index of the given proxy */
	public int getIndex(T proxy) {
		return getIndex(proxy, true);
	}

	/** get index of a given proxy while defining filtered or not */
	public int getIndex(T proxy, boolean useFilteredList) {
		for (int i = 0; i < getSize(useFilteredList); ++i) {
			if (proxy == getProxy(i, useFilteredList))
				return i;
		}
		return -1;
	}
}
