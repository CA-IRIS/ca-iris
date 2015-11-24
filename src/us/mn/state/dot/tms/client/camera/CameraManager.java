/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2015  Minnesota Department of Transportation
 * Copyright (C) 2014-2015  AHMCT, University of California
 * Copyright (C) 2016       Southwest Research Institute
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
package us.mn.state.dot.tms.client.camera;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.HashSet;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;

import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.Camera;
import us.mn.state.dot.tms.ControllerHelper;
import us.mn.state.dot.tms.Direction;
import us.mn.state.dot.tms.GeoLoc;
import us.mn.state.dot.tms.GeoLocHelper;
import us.mn.state.dot.tms.ItemStyle;
import us.mn.state.dot.tms.SiteDataHelper;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.client.MapTab;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.GeoLocManager;
import us.mn.state.dot.tms.client.proxy.MapAction;
import us.mn.state.dot.tms.client.proxy.MapGeoLoc;
import us.mn.state.dot.tms.client.proxy.PropertiesAction;
import us.mn.state.dot.tms.client.proxy.ProxyManager;
import us.mn.state.dot.tms.client.proxy.ProxyTheme;
import us.mn.state.dot.tms.client.proxy.TeslaAction;
import us.mn.state.dot.tms.client.widget.SmartDesktop;
import us.mn.state.dot.tms.utils.I18N;

/**
 * A camera manager is a container for SONAR camera objects.
 *
 * @author Douglas Lau
 * @author Travis Swanston
 * @author Jacob Barde
 */
public class CameraManager extends ProxyManager<Camera> {

	/** Camera map object marker */
	static private final CameraMarker MARKER = new CameraMarker();

	/** Color for active camera style */
	static private final Color COLOR_ACTIVE = new Color(0, 192, 255);

	/** Color for occupied camera style */
	static private final Color COLOR_OCCUPIED = new Color(177, 0, 0);

	/** camera direction if override present */
	private Double overrideDirectionRadians = null;

	/** is there a override */
	private Boolean isOverrideDirection = null;

	/** Set of cameras in the playlist */
	private final HashSet<Camera> playlist = new HashSet<Camera>();

	/** Create a new camera manager */
	public CameraManager(Session s, GeoLocManager lm) {
		super(s, lm, ItemStyle.ALL);
	}

	/**
	 * override the camera direction if settings are so.
	 *
	 * CAMERA_DIRECTION_OVERRIDE values of NORTH, EAST, SOUTH or WEST
	 * will result in all icons facing desired cardinal direction.
	 * Numerical values between 0 and 360 will be honored. Any other values
	 * will result in default behavior.
	 */
	@Override
	public Double getTangentAngle(MapGeoLoc loc) {

		if (Boolean.TRUE.equals(isOverrideDirection)
			&& overrideDirectionRadians != null)
			return overrideDirectionRadians;
		else if (Boolean.FALSE.equals(isOverrideDirection))
			return super.getTangentAngle(loc);

		/*
		 * the remainder of this method should only execute once.
		 * values will be set so this isn't recomputed anymore.
		 */
		isOverrideDirection = false;
		Double dirRad = null;

		try {
			// check if the override is compass degrees
			int dir = Integer.parseInt(
				SystemAttrEnum.CAMERA_DIRECTION_OVERRIDE
					.getString());

			if (dir < 0)
				throw new Exception("CAMERA_DIRECTION_OVERRIDE"
					+ " value less than zero.");
			if (dir > 360)
				throw new Exception("CAMERA_DIRECTION_OVERRIDE"
					+ " value greater than 360");
			if (dir == 360)
				dir = 0;

			dir = 360 - (dir + 90); // compass to trig degrees
			if (dir >= 360)
				dir -= 360;

			dirRad = Math.toRadians(dir);
			isOverrideDirection = true;

		} catch (Exception e) {

			// check if the override is a cardinal direction
			Direction so = Direction.parseString(
				SystemAttrEnum.CAMERA_DIRECTION_OVERRIDE
					.getString(), Direction.UNKNOWN);

			switch (so) {
			case NORTH:
				dirRad = MapGeoLoc.RAD_EAST;
				break;
			case EAST:
				dirRad = MapGeoLoc.RAD_SOUTH;
				break;
			case SOUTH:
				dirRad = MapGeoLoc.RAD_WEST;
				break;
			case WEST:
				dirRad = MapGeoLoc.RAD_NORTH;
				break;
			}

			if (dirRad != null)
				isOverrideDirection = true;

		} finally {
			if (isOverrideDirection)
				overrideDirectionRadians = dirRad;
			else
				dirRad = super.getTangentAngle(loc);
		}

		return dirRad;
	}

	/** Get the sonar type name */
	@Override
	public String getSonarType() {
		return Camera.SONAR_TYPE;
	}

	/** Get the camera cache */
	@Override
	public TypeCache<Camera> getCache() {
		return session.getSonarState().getCamCache().getCameras();
	}

	/** Create a camera map tab */
	@Override
	public CameraTab createTab() {
		return new CameraTab(session, this);
	}

	/** Get the shape for a given proxy */
	@Override
	protected Shape getShape(AffineTransform at) {
		return MARKER.createTransformedShape(at);
	}

	/** Create a theme for cameras */
	@Override
	protected ProxyTheme<Camera> createTheme() {
		ProxyTheme<Camera> theme = new ProxyTheme<Camera>(this, MARKER);
		theme.addStyle(ItemStyle.UNPUBLISHED,
			ProxyTheme.COLOR_UNAVAILABLE);
		theme.addStyle(ItemStyle.INACTIVE, ProxyTheme.COLOR_INACTIVE,
			ProxyTheme.OUTLINE_INACTIVE);
		theme.addStyle(ItemStyle.PLAYLIST, ProxyTheme.COLOR_DEPLOYED);
		theme.addStyle(ItemStyle.ACTIVE, COLOR_ACTIVE);
		theme.addStyle(ItemStyle.DEPLOYED, COLOR_OCCUPIED);
		theme.addStyle(ItemStyle.ALL);
		return theme;
	}

	/** Check if a given attribute affects a proxy style */
	@Override
	public boolean isStyleAttrib(String a) {
		return "publish".equals(a);
	}

	/** Check the style of the specified proxy */
	@Override
	public boolean checkStyle(ItemStyle is, Camera proxy) {
		switch (is) {
		case DEPLOYED:
			return ControllerHelper.isActive(proxy.getController())
				&& isCameraInUse(proxy.getName());
		case ACTIVE:
			return ControllerHelper.isActive(proxy.getController());
		case INACTIVE:
			return !ControllerHelper.isActive(
				proxy.getController());
		case UNPUBLISHED:
			return !proxy.getPublish();
		case NO_CONTROLLER:
			return proxy.getController() == null;
		case PLAYLIST:
			return inPlaylist(proxy);
		case ALL:
			return true;
		default:
			return false;
		}
	}

	/** Create a properties form for the specified proxy */
	@Override
	protected CameraProperties createPropertiesForm(Camera cam) {
		return new CameraProperties(session, cam);
	}

	/** Create a popup menu for a single camera selection */
	@Override
	protected JPopupMenu createPopupSingle(Camera c) {
		SmartDesktop desktop = session.getDesktop();
		JPopupMenu p = new JPopupMenu();
		p.add(makeMenuLabel(getDescription(c)));
		p.addSeparator();
		p.add(new MapAction(desktop.client, c, c.getGeoLoc()));
		p.addSeparator();
		p.add(new PublishAction(s_model));
		p.add(new UnpublishAction(s_model));
		p.addSeparator();
		if (inPlaylist(c))
			p.add(new RemovePlaylistAction(this, s_model));
		else
			p.add(new AddPlaylistAction(this, s_model));
		p.addSeparator();
		if (TeslaAction.isConfigured())
			p.add(new TeslaAction<Camera>(c));
		p.add(new PropertiesAction<Camera>(this, c));
		return p;
	}

	/** Create a popup menu for multiple objects */
	@Override
	protected JPopupMenu createPopupMulti(int n_selected) {
		JPopupMenu p = new JPopupMenu();
		p.add(new JLabel(I18N.get("camera.title") + ": " +
			n_selected));
		p.addSeparator();
		p.add(new PublishAction(s_model));
		p.add(new UnpublishAction(s_model));
		p.addSeparator();
		p.add(new AddPlaylistAction(this, s_model));
		p.add(new RemovePlaylistAction(this, s_model));
		return p;
	}

	protected boolean isCameraInUse(String cid) {
		MapTab mt = this.session.lookupTab("camera");
		int count = 0;
		if (mt instanceof CameraTab)
			count = ((CameraTab) mt).getDispatcher()
				.getVideoWallManager().getNumConns(cid);
		return count > 0;
	}

	/** Test if a camera is in the playlist */
	public boolean inPlaylist(Camera c) {
		synchronized (playlist) {
			return playlist.contains(c);
		}
	}

	/** Add a camera to the playlist */
	public void addPlaylist(Camera c) {
		synchronized (playlist) {
			playlist.add(c);
		}
		// FIXME: add server-side playlists
	}

	/** Remove a camera from the playlist */
	public void removePlaylist(Camera c) {
		synchronized (playlist) {
			playlist.remove(c);
		}
		// FIXME: add server-side playlists
	}

	/** Find the map geo location for a proxy */
	@Override
	protected GeoLoc getGeoLoc(Camera proxy) {
		return proxy.getGeoLoc();
	}

	/** Get the layer zoom visibility threshold */
	@Override
	protected int getZoomThreshold() {
		return 13;
	}

	/** Get the description of a proxy */
	@Override
	public String getDescription(Camera c) {
		String pn = c.getName();
		String sn = SiteDataHelper.getSiteName(pn);
		String geoDesc = GeoLocHelper.getDescription(getGeoLoc(c));
		String ret = ((sn != null) ? sn : pn);
		if (SystemAttrEnum.CAMERA_MANAGER_SHOW_LOCATION.getBoolean())
			ret += " - " + geoDesc;
		return ret;
	}

}
