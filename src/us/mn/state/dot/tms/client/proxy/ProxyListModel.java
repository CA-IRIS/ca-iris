/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2007-2015  Minnesota Department of Transportation
 * Copyright (C) 2014-2015  AHMCT, University of California
 * Copyright (C) 2015 California Department of Transportation
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
 */
public class ProxyListModel<T extends SonarObject>
	extends AbstractListModel
{
	/** Filter interface to show/hide elements within the model */
	public interface Filter<T extends SonarObject> {
		boolean accept(T element);
	}

	/** Proxy type cache */
	private final TypeCache<T> cache;

	/** Proxy list */
	private final ArrayList<T> list = new ArrayList<T>();

	/** Proxy comparator */
	private final Comparator<T> comp = comparator();

	/** List of displayed filtered_list if filtered */
	private final ArrayList<T> filtered_list = new ArrayList<T>();

	/** The filter to determine which elements should be shown */
	private Filter<T> filter;

	/** Flag used by applyFilter to prevent unintended recursion. */
	private boolean applyingFilter = false;

	/** Get a proxy comparator */
	protected Comparator<T> comparator() {
		return new ProxyComparator<T>();
	}

	/** Proxy listener for SONAR updates */
	private final SwingProxyAdapter<T> swing_listener = new SwingProxyAdapter<T>() {
		protected Comparator<T> comparator() {
			return ProxyListModel.this.comp;
		}
		protected void proxyAddedSwing(T proxy) {
			int i = doProxyAdded(proxy);
			if (i >= 0) {
				applyFilter();
				fireIntervalAdded(this, i, i);
			}
		}
		protected void enumerationCompleteSwing(Collection<T> proxies) {
			for (T proxy: proxies) {
				if (check(proxy))
					list.add(proxy);
			}
			int sz = list.size() - 1;
			if (sz >= 0) {
				applyFilter();
				fireContentsChanged(this, 0, getSize() - 1);

			}
		}
		protected void proxyRemovedSwing(T proxy) {
			int i = doProxyRemoved(proxy);
			if (i >= 0) {
				applyFilter();
				fireIntervalRemoved(this, i, i);
			}
		}
		protected void proxyChangedSwing(T proxy, String attr) {
			ProxyListModel.this.proxyChangedSwing(proxy);
		}
	};

	/** Listens for changes in backing data in order to re-filter as needed */
//	private final ListDataListener filter_listener = new ListDataListener(){
//		@Override
//		public void intervalAdded(ListDataEvent e) {
//			applyFilter();
//		}
//
//		@Override
//		public void intervalRemoved(ListDataEvent e) {
//			applyFilter();
//		}
//
//		@Override
//		public void contentsChanged(ListDataEvent e) {
//			applyFilter();
//		}
//	};

	/** Create a new proxy list model */
	public ProxyListModel(TypeCache<T> c) {
		cache = c;
//		addListDataListener(filter_listener);
	}

	/** Initialize the proxy list model. This cannot be done in the
	 * constructor because subclasses may not be fully constructed. */
	public void initialize() {
		cache.addProxyListener(swing_listener);
	}

	/** Dispose of the proxy model */
	public void dispose() {
//		removeListDataListener(filter_listener);
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
		int i = getIndex(proxy);
		if (i >= 0)
			list.remove(i);
		return i;
	}

	/** Change a proxy in the list model */
	private void proxyChangedSwing(T proxy) {
		int pre = doProxyRemoved(proxy);
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
		fireContentsChanged(this, 0, getSize() - 1);
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
		ArrayList<T> oldIndices = new ArrayList<T>(
			filtered_list);
		filtered_list.clear();

		Filter<T> f = filter;
		if (f != null) {
			for (int i = 0; i < list.size(); i++) {
				T x = list.get(i);
				if (f.accept(x))
					filtered_list.add(x);
			}
		}

		applyingFilter = false;
	}

	/** Gets the size */
	@Override
	public int getSize() {
		return (filter != null) ? filtered_list.size() : list.size();
	}

	/** Get the element at the specified index (for ListModel) */
	@Override
	public T getElementAt(int index) {
		return(filter != null)
			? filtered_list.get(index) : list.get(index);
	}

	/** Get the proxy at the specified index */
	public T getProxy(int index) {
		return getElementAt(index);
	}

	/** Get the index of the given proxy */
	public int getIndex(T proxy) {
		for (int i = 0; i < getSize(); ++i) {
			if (proxy == getProxy(i))
				return i;
		}
		return -1;
	}
}
