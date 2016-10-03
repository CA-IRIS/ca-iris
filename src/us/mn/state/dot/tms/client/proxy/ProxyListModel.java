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

	/** List of displayed indices if filtered */
	private final ArrayList<Integer> indices = new ArrayList<Integer>();

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
			int i = doProxyRemoved(proxy);
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
	    int ret = -1;
		if (check(proxy)) {
			int sz = list.size();
			ret = sz;
			for (int i = 0; i < sz; i++) {
				int c = comp.compare(proxy, list.get(i));
				if (c == 0) {
					ret = -1;
					break;
				} else if (c < 0) {
				    ret = i;
                    break;
				}
			}

			if (ret != -1) {
                list.add(ret, proxy);

                // must return index based on current filter
                Filter<T> f = filter;
                if (f != null) {
                    if (f.accept(proxy)) {
                        // add to filtered indices
                        sz = getSize();
                        int i;
                        for (i = 0; i < sz; i++) {
                            int c = comp.compare(proxy, getProxy(i));
                            if (c < 0) break;
                        }
                        indices.add(i, ret);
                        ret = i;

						// increment indices after inserted
						for (int j = i; j < indices.size(); j++) {
							indices.set(j, indices.get(j) + 1);
						}
                    } else {
                        // not visible based on current filter
                        ret = -1;
                    }
                }
            }
		}

		return ret;
	}

	/** Remove a proxy from the model */
	protected int doProxyRemoved(T proxy) {
		int ret = -1;
		for (int i = 0; i < list.size(); i++) {
			if (0 == comp.compare(proxy, list.get(i))) {
				ret = i;
				break;
			}
		}

		if (ret >= 0) {
			list.remove(ret);
            if (filter != null) {
                ret = indices.indexOf(ret);
                if (ret != -1) {
					indices.remove(ret);

					// decrement indices after removed
					for (int i = ret; i < indices.size(); i++) {
						indices.set(i, indices.get(i) - 1);
					}
                }
            }
		}

		return ret;
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
		ArrayList<Integer> oldIndices = new ArrayList<Integer>(indices);
		indices.clear();

		Filter<T> f = filter;
		if (f != null) {
			for (int i = 0; i < list.size(); i++) {
				if (f.accept(list.get(i)))
					indices.add(i);
			}
		}

		if (oldIndices.size() != indices.size() || !IterableUtil.sequenceEqual(oldIndices, indices)) {
			fireContentsChanged(this, 0, getSize() - 1);
		}

		applyingFilter = false;
	}

	/** Gets the size */
	@Override
	public int getSize() {
		return (filter != null) ? indices.size() : list.size();
	}

	/** Get the element at the specified index (for ListModel) */
	@Override
	public Object getElementAt(int index) {
		return list.get((filter != null) ? indices.get(index) : index);
	}

	/** Get the proxy at the specified index */
	public T getProxy(int index) {
		return (T)getElementAt(index);
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