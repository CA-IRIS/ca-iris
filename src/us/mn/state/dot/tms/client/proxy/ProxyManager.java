/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2015  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.proxy;

import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Collection;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;
import us.mn.state.dot.map.Layer;
import us.mn.state.dot.map.LayerState;
import us.mn.state.dot.map.MapBean;
import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.MapSearcher;
import us.mn.state.dot.map.Symbol;
import us.mn.state.dot.sonar.SonarObject;
import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.GeoLoc;
import us.mn.state.dot.tms.GeoLocHelper;
import us.mn.state.dot.tms.ItemStyle;
import us.mn.state.dot.tms.SiteDataHelper;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.client.MapTab;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.widget.Invokable;
import static us.mn.state.dot.tms.client.widget.SwingRunner.runQueued;

/**
 * A proxy manager is a container for SONAR proxy objects. It places each
 * proxy into an appropriate style list model.
 *
 * @author Douglas Lau
 * @author Michael Darter
 * @author Travis Swanston
 */
abstract public class ProxyManager<T extends SonarObject> {

	/** Make a menu label */
	static protected Box makeMenuLabel(String id) {
		Box b = Box.createHorizontalBox();
		b.add(Box.createHorizontalStrut(6));
		b.add(Box.createHorizontalGlue());
		b.add(new JLabel(id));
		b.add(Box.createHorizontalGlue());
		b.add(Box.createHorizontalStrut(6));
		return b;
	}

	/** Limit the map scale based on system attributes.
	 * @param scale Map scale in user coordinates per pixel.
	 * @return Adjusted map scale in user coordinates per pixel. */
	static public float adjustScale(final float scale) {
		float sc_min = scale / 4.0f;
		float sc_max = getIconSizeScaleMax();
		return (sc_max > 0) ?
			Math.max(Math.min(scale, sc_max), sc_min) : scale;
	}

	/** Get the map icon maximum size scale */
	static private float getIconSizeScaleMax() {
		return SystemAttrEnum.MAP_ICON_SIZE_SCALE_MAX.getFloat();
	}

	/** User session */
	protected final Session session;

	/** Geo location manager */
	private final GeoLocManager loc_manager;

	/** Listener for proxy events */
	private final SwingProxyAdapter<T> listener =
		new SwingProxyAdapter<T>()
	{
		protected void proxyAddedSwing(T proxy) {
			ProxyManager.this.proxyAddedSwing(proxy);
		}
		protected void enumerationCompleteSwing(Collection<T> proxies) {
			ProxyManager.this.enumerationCompleteSwing(proxies);
			updateExtent();
		}
		protected void proxyRemovedSwing(T proxy) {
			ProxyManager.this.proxyRemovedSwing(proxy);
		}
		protected void proxyChangedSwing(T proxy, String attr) {
			ProxyManager.this.proxyChangedSwing(proxy, attr);
		}
		protected boolean checkAttributeChange(String attr) {
			return ProxyManager.this.checkAttributeChange(attr);
		}
	};

	/** Selection model */
	protected final ProxySelectionModel<T> s_model =
		new ProxySelectionModel<T>();

	/** Theme for drawing objects on a map layer */
	private final ProxyTheme<T> theme;

	/** Cache of MapObject to proxy */
	private final ProxyMapCache<T> map_cache = new ProxyMapCache<T>();

	/** Map layer for the proxy type */
	private final ProxyLayer<T> layer;

	/** Default style */
	private final ItemStyle def_style;

	/** Style summary */
	private StyleSummary<T> style_summary;

	/** Create a new proxy manager */
	protected ProxyManager(Session s, GeoLocManager lm, ItemStyle ds) {
		session = s;
		loc_manager = lm;
		def_style = ds;
		theme = createTheme();
		layer = hasLayer() ? createLayer() : null;
	}

	/** Create a new proxy manager */
	protected ProxyManager(Session s, GeoLocManager lm) {
		this(s, lm, ItemStyle.ALL);
	}

	/** Initialize the proxy manager. This cannot be done in the constructor
	 * because subclasses may not be fully constructed. */
	public void initialize() {
		getCache().addProxyListener(listener);
	}

	/** Dispose of the proxy manager */
	public void dispose() {
		s_model.dispose();
		map_cache.dispose();
		getCache().removeProxyListener(listener);
	}

	/** Create a layer for this proxy type */
	protected ProxyLayer<T> createLayer() {
		return new ProxyLayer<T>(this);
	}

	/** Add a proxy to the manager */
	protected void proxyAddedSwing(T proxy) {
		// NOTE: this also gets called when we "watch" an
		//       object after it is selected.
		cacheMapGeoLoc(proxy);
		updateGeometry();
	}

	/** Cache a MapGeoLoc for one proxy */
	private void cacheMapGeoLoc(T proxy) {
		MapGeoLoc loc = findGeoLoc(proxy);
		if (loc != null) {
			loc.setManager(this);
			loc.doUpdate();
			map_cache.put(loc, proxy);
		}
	}

	/** Enumeraton complete */
	protected void enumerationCompleteSwing(Collection<T> proxies) {
		for (final T p: proxies) {
			runQueued(new Invokable() {
				public void invoke() {
					cacheMapGeoLoc(p);
				}
			});
		}
	}

	/** Remove a proxy from the manager */
	protected void proxyRemovedSwing(T proxy) {
		s_model.removeSelected(proxy);
		map_cache.remove(proxy);
		updateGeometry();
	}

	/** Update layer geometry */
	public final void updateGeometry() {
		if (layer != null) {
			runQueued(new Invokable() {
				public void invoke() {
					layer.updateGeometry();
				}
			});
		}
	}

	/** Update layer extend */
	public final void updateExtent() {
		if (layer != null) {
			runQueued(new Invokable() {
				public void invoke() {
					layer.updateExtent();
				}
			});
		}
	}

	/** Check if an attribute change is interesting */
	protected boolean checkAttributeChange(String attr) {
		return isStyleAttrib(attr);
	}

	/** Called when a proxy has been changed */
	protected void proxyChangedSwing(T proxy, String attr) {
		if (layer != null && isStyleAttrib(attr))
			layer.updateStatus();
	}

	/** Get the tangent angle for the given location */
	public Double getTangentAngle(MapGeoLoc loc) {
		return loc_manager.getTangentAngle(loc);
	}

	/** Get the sonar type name */
	abstract public String getSonarType();

	/** Get the proxy type cache */
	abstract public TypeCache<T> getCache();

	/** Create a map tab for the managed proxies */
	public MapTab<T> createTab() {
		return null;
	}

	/** Check if user can read managed proxies */
	public boolean canRead() {
		return session.canRead(getSonarType());
	}

	/** Create a list cell renderer */
	public ListCellRenderer createCellRenderer() {
		return new ProxyCellRenderer<T>(this);
	}

	/** Create a proxy JList */
	public ProxyJList<T> createList() {
		return new ProxyJList<T>(this);
	}

	/** Create a theme for this type of proxy */
	abstract protected ProxyTheme<T> createTheme();

	/** Get a transformed marker shape */
	abstract protected Shape getShape(AffineTransform at);

	/** Current marker shape */
	private Shape shape;

	/** Get current marker shape */
	public final Shape getShape() {
		return shape;
	}

	/** Set the current marker shape */
	public final void setShape(Shape s) {
		shape = s;
	}

	/** Current cell renderer size */
	private CellRendererSize m_cellSize = CellRendererSize.LARGE;

	/** Set the current cell size */
	public void setCellSize(CellRendererSize size) {
		m_cellSize = size;
	}

	/** Get the current cell size */
	public CellRendererSize getCellSize() {
		return m_cellSize;
	}

	/** Get the theme */
	public ProxyTheme<T> getTheme() {
		return theme;
	}

	/** Test if a layer is the manager's layer */
	public boolean checkLayer(Layer l) {
		return l == layer;
	}

	/** Create layer state for a map bean */
	public final LayerState createState(MapBean mb) {
		return layer.createState(mb);
	}

	/** Get the proxy selection model */
	public ProxySelectionModel<T> getSelectionModel() {
		return s_model;
	}

	/** Gets the style summary for this proxy type, with no cell
	 * renderer size buttons. */
	public StyleSummary<T> getStyleSummary() {
		if (null == style_summary)
			style_summary = new StyleSummary<T>(this, def_style, false);
		return style_summary;
	}

	/** Gets the style summary for this proxy type */
	public StyleSummary<T> getStyleSummary(boolean enableCellSizeBtns) {
		if (null == style_summary)
			style_summary = new StyleSummary<T>(this, def_style, enableCellSizeBtns);
		return style_summary;
	}

	/** Get the specified style list model */
	public StyleListModel<T> getStyleModel(String s) {
		StyleListModel<T> slm = createStyleListModel(s);
		if (slm != null) {
			slm.initialize();
			return slm;
		} else
			return null;
	}

	/** Create a style list model for the given symbol name */
	private StyleListModel<T> createStyleListModel(String s) {
		return createStyleListModel(theme.getSymbol(s));
	}

	/** Create a style list model for the given symbol */
	protected StyleListModel<T> createStyleListModel(Symbol s) {
		return new StyleListModel<T>(this, s.getLabel());
	}

	/** Check if a given attribute affects a proxy style */
	public boolean isStyleAttrib(String a) {
		return "styles".equals(a);
	}

	/** Check the style of the specified proxy */
	public boolean checkStyle(ItemStyle is, T proxy) {
		return false;
	}

	/** Show the properties form for the selected proxy */
	public final void showPropertiesForm() {
		T proxy = s_model.getSingleSelection();
		if (proxy != null)
			showPropertiesForm(proxy);
	}

	/** Show the properties form for the specified proxy */
	public final void showPropertiesForm(T proxy) {
		SonarObjectForm<T> form = createPropertiesForm(proxy);
		if(form != null)
			session.getDesktop().show(form);
	}

	/** Create a properties form for the specified proxy */
	protected SonarObjectForm<T> createPropertiesForm(T proxy) {
		return null;
	}

	/** Show the popup menu for the selected proxy or proxies */
	public void showPopupMenu(MouseEvent e) {
		JPopupMenu popup = createPopup();
		if(popup != null) {
			popup.setInvoker(e.getComponent());
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	/** Create a popup menu for the selected proxy object(s) */
	protected JPopupMenu createPopup() {
		T proxy = s_model.getSingleSelection();
		if (proxy != null)
			return createPopupSingle(proxy);
		else {
			int n_sel = s_model.getSelectedCount();
			return (n_sel > 1) ? createPopupMulti(n_sel) : null;
		}
	}

	/** Create a popup menu for a single object */
	protected JPopupMenu createPopupSingle(T proxy) {
		return null;
	}

	/** Create a popup menu for multiple objects */
	protected JPopupMenu createPopupMulti(int n_selected) {
		return null;
	}

	/** Iterate through all proxy objects */
	public MapObject forEach(MapSearcher ms, float scale) {
		float sc = adjustScale(scale);
		AffineTransform at = new AffineTransform();
		at.setToScale(sc, sc);
		return forEach(ms, at);
	}

	/** Iterate through all proxy objects */
	private MapObject forEach(MapSearcher ms, AffineTransform at) {
		shape = getShape(at);
		synchronized(map_cache) {
			for(MapGeoLoc loc: map_cache) {
				if(isLocationSet(loc)) {
					if(ms.next(loc))
						return loc;
				}
			}
		}
		return null;
	}

	/** Check if the location is set */
	static private boolean isLocationSet(MapGeoLoc loc) {
		return loc != null && !GeoLocHelper.isNull(loc.getGeoLoc());
	}

	/** Find the map geo location for a proxy */
	public MapGeoLoc findGeoLoc(T proxy) {
		GeoLoc loc = getGeoLoc(proxy);
		if(loc != null)
			return loc_manager.findMapGeoLoc(loc);
		else
			return null;
	}

	/** Get the GeoLoc for the specified proxy */
	abstract protected GeoLoc getGeoLoc(T proxy);

	/** Find a proxy matching the given map object */
	public T findProxy(MapObject mo) {
		if(mo instanceof MapGeoLoc)
			return map_cache.lookup((MapGeoLoc)mo);
		else
			return null;
	}

	/** Get the description of a proxy */
	public String getDescription(T proxy) {
		String pn = proxy.getName();
		String sn = SiteDataHelper.getSiteName(pn);
		String desc = GeoLocHelper.getDescription(getGeoLoc(proxy));
		return ( (sn != null) ? sn : pn) + " - " + desc;
	}

	/** Check if the corresponding layer is visible.
	 * @param zoom Current map zoom level.
	 * @return True if the layer should be visible. */
	public boolean isVisible(int zoom) {
		return zoom >= getZoomThreshold();
	}

	/** Get the layer zoom visibility threshold */
	protected int getZoomThreshold() {
		// Zero indicates no layer for proxies
		return 0;
	}

	/** Check if manager has a layer to display */
	public final boolean hasLayer() {
		return canRead() && (getZoomThreshold() > 0);
	}
}
