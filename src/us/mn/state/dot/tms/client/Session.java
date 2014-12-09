/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2014  Minnesota Department of Transportation
 * Copyright (C) 2014  AHMCT, University of California
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import us.mn.state.dot.map.MapBean;
import us.mn.state.dot.map.MapModel;
import us.mn.state.dot.map.TileLayer;
import us.mn.state.dot.sonar.Connection;
import us.mn.state.dot.sonar.Name;
import us.mn.state.dot.sonar.Namespace;
import us.mn.state.dot.sonar.SonarObject;
import us.mn.state.dot.sonar.User;
import us.mn.state.dot.tms.client.beacon.BeaconManager;
import us.mn.state.dot.tms.client.camera.CameraManager;
import us.mn.state.dot.tms.client.comm.ControllerManager;
import us.mn.state.dot.tms.client.dms.DMSManager;
import us.mn.state.dot.tms.client.gate.GateArmArrayManager;
import us.mn.state.dot.tms.client.incident.IncidentManager;
import us.mn.state.dot.tms.client.lcs.LCSArrayManager;
import us.mn.state.dot.tms.client.lcs.LCSIManager;
import us.mn.state.dot.tms.client.marking.LaneMarkingManager;
import us.mn.state.dot.tms.client.meter.MeterManager;
import us.mn.state.dot.tms.client.proxy.GeoLocManager;
import us.mn.state.dot.tms.client.proxy.ProxyManager;
import us.mn.state.dot.tms.client.roads.R_NodeManager;
import us.mn.state.dot.tms.client.roads.SegmentLayer;
import us.mn.state.dot.tms.client.schedule.PlanManager;
import us.mn.state.dot.tms.client.weather.WeatherSensorManager;
import us.mn.state.dot.tms.client.widget.SmartDesktop;

/**
 * A session is one IRIS login session.
 *
 * @author Douglas Lau
 * @author Travis Swanston
 */
public class Session {

	/** Session User */
	private final User user;

	/** Get the currently logged-in user */
	public User getUser() {
		return user;
	}

	/** "Edit" mode */
	private boolean edit_mode = false;

	/** Set the edit mode */
	public void setEditMode(boolean m) {
		edit_mode = m;
	}

	/** SONAR state */
	private final SonarState state;

	/** Get the SONAR state */
	public SonarState getSonarState() {
		return state;
	}

	/** SONAR namespace */
	private final Namespace namespace;

	/** Desktop used by this session */
	private final SmartDesktop desktop;

	/** Get the desktop */
	public SmartDesktop getDesktop() {
		return desktop;
	}

	/** Client properties */
	private final Properties props;

	/** Get the client properties */
	public Properties getProperties() {
		return props;
	}

	/** Mutable user properties stored on client workstation */
	private final UserProperties user_props;

	/** Get the user properties */
	public UserProperties getUserProperties() {
		return user_props;
	}

	/** Location manager */
	private final GeoLocManager loc_manager;

	/** List of proxy managers */
	private final LinkedList<ProxyManager<?>> managers;

	/** R_Node manager */
	private final R_NodeManager r_node_manager;

	/** Get the r_node manager */
	public R_NodeManager getR_NodeManager() {
		return r_node_manager;
	}

	/** Camera manager */
	private final CameraManager cam_manager;

	/** Get the camera manager */
	public CameraManager getCameraManager() {
		return cam_manager;
	}

	/** DMS manager */
	private final DMSManager dms_manager;

	/** Get the DMS manager */
	public DMSManager getDMSManager() {
		return dms_manager;
	}

	/** LCS array manager */
	private final LCSArrayManager lcs_array_manager;

	/** Get the LCS array manager */
	public LCSArrayManager getLCSArrayManager() {
		return lcs_array_manager;
	}

	/** List of all tabs */
	private final List<MapTab> tabs = new LinkedList<MapTab>();

	/** Get a list of all tabs */
	public List<MapTab> getTabs() {
		return tabs;
	}

	/** Segment layer */
	private final SegmentLayer seg_layer;

	/** Tile layer */
	private final TileLayer tile_layer;

	/** Create a new session */
	public Session(SonarState st, SmartDesktop d, Properties p,
		UserProperties up)
	{
		state = st;
		user = state.getUser();
		namespace = state.getNamespace();
		desktop = d;
		props = p;
		user_props = up;
		loc_manager = new GeoLocManager(this);
		r_node_manager = new R_NodeManager(this, loc_manager);
		cam_manager = new CameraManager(this, loc_manager);
		dms_manager = new DMSManager(this, loc_manager);
		lcs_array_manager = new LCSArrayManager(this, loc_manager);
		managers = new LinkedList<ProxyManager<?>>();
		managers.add(r_node_manager);
		managers.add(new ControllerManager(this, loc_manager));
		managers.add(cam_manager);
		managers.add(new MeterManager(this, loc_manager));
		managers.add(new GateArmArrayManager(this, loc_manager));
		managers.add(dms_manager);
		managers.add(lcs_array_manager);
		managers.add(new LCSIManager(this, loc_manager));
		managers.add(new LaneMarkingManager(this,loc_manager));
		managers.add(new BeaconManager(this, loc_manager));
		managers.add(new WeatherSensorManager(this, loc_manager));
		managers.add(new IncidentManager(this, loc_manager));
		managers.add(new PlanManager(this, loc_manager));
		seg_layer = r_node_manager.getSegmentLayer();
		tile_layer = createTileLayer(props.getProperty("map.tile.url"));
	}

	/** Create the tile layer */
	private TileLayer createTileLayer(String url) {
		if (url != null)
			return new TileLayer("Base map", url, 1000);
		else
			return null;
	}

	/** Initialize the session */
	public void initialize() throws IOException, SAXException,
		ParserConfigurationException
	{
		initializeManagers();
		addTabs();
		seg_layer.start(props);
		if (tile_layer != null)
			tile_layer.initialize();
	}

	/** Initialize all the proxy managers */
	private void initializeManagers() {
		loc_manager.initialize();
		for (ProxyManager<?> man: managers)
			man.initialize();
	}

	/** Add the tabs in the order specified by user_props file */
	private void addTabs() {
		HashMap<String, MapTab> tm = createTabs();
		for (String t : user_props.getTabList()) {
			MapTab tab = tm.get(t);
			if (tab != null)
				tabs.add(tab);
		}
	}

	/** Create a mapping of text ids to map tabs */
	private HashMap<String, MapTab> createTabs() {
		HashMap<String, MapTab> tm = new HashMap<String, MapTab>();
		for (ProxyManager<?> man: managers) {
			if (man.canRead()) {
				MapTab<?> tab = man.createTab();
				if (tab != null)
					tm.put(tab.getTextId(), tab);
			}
		}
		return tm;
	}

	/** Create the layer states.  The map bean and model must be seperate
	 * parameters so that the model can be built before calling setModel
	 * on the map bean.
	 * @param mb Map bean to render the layer states.
	 * @param mm Map model to contain layer states. */
	public void createLayers(MapBean mb, MapModel mm) {
		if (tile_layer != null)
			mm.addLayer(tile_layer.createState(mb));
		mm.addLayer(seg_layer.createState(mb));
		for (ProxyManager<?> man: managers) {
			if (man.hasLayer())
				mm.addLayer(man.createState(mb));
		}
	}

	/** Check if the user can add an object.
	 * @param name Name of object to add.
	 * @param can_edit Flag to allow editing.
	 * @return true if user can add the object */
	private boolean canAdd(Name name, boolean can_edit) {
		return can_edit && namespace.canAdd(name, user);
	}

	/** Check if the user can add an object.
	 * @param tname Type name of object to add.
	 * @param oname Name of object to add.
	 * @param can_edit Flag to allow editing.
	 * @return true if user can add the object */
	private boolean canAdd(String tname, String oname, boolean can_edit) {
		return oname != null && canAdd(new Name(tname,oname), can_edit);
	}

	/** Check if the user can add an object.
	 * @param tname Type name of object to add.
	 * @param oname Name of object to add.
	 * @return true if user can add the object */
	public boolean canAdd(String tname, String oname) {
		return canAdd(tname, oname, edit_mode);
	}

	/** Check if the user can add an object.
	 * @param tname Type name of object to add.
	 * @return true if user can add the object */
	public boolean canAdd(String tname) {
		return canAdd(tname, "oname");
	}

	/** Check if the user is permitted to add an object, regardless of
	 * EDIT mode.
	 * @param tname Type name of object to add.
	 * @param oname Name of object to add.
	 * @return true if user can add the object */
	public boolean isAddPermitted(String tname, String oname) {
		return canAdd(new Name(tname, oname), true);
	}

	/** Check if the user is permitted to add an object, regardless of
	 * EDIT mode.
	 * @param tname Type name of object to add.
	 * @return true if user can add the object */
	public boolean isAddPermitted(String tname) {
		return canAdd(tname, "oname", true);
	}

	/** Check if the user can read a type */
	public boolean canRead(String tname) {
		return namespace.canRead(new Name(tname), user);
	}

	/** Check if the user can update an attribute.
	 * @param name Name of object/attribute to update.
	 * @param can_edit Flag to allow editing.
	 * @return true if user can update the attribute */
	private boolean canUpdate(Name name, boolean can_edit) {
		return can_edit && namespace.canUpdate(name, user);
	}

	/** Check if the user can update an attribute.
	 * @param tname Type name of attribute to update.
	 * @param aname Name of attribute to update.
	 * @param can_edit Flag to allow editing.
	 * @return true if user can update the attribute */
	private boolean canUpdate(String tname, String aname, boolean can_edit) {
		return canUpdate(new Name(tname, "oname", aname), can_edit);
	}

	/** Check if the user can update an attribute.
	 * @param tname Type name of attribute to update.
	 * @param can_edit Flag to allow editing.
	 * @return true if user can update the attribute */
	private boolean canUpdate(String tname, boolean can_edit) {
		return canUpdate(tname, "aname", can_edit);
	}

	/** Check if the user can update a proxy attribute.
	 * @param proxy Proxy object to check.
	 * @param can_edit Flag to allow editing.
	 * @return true if user can update the attribute */
	private boolean canUpdate(SonarObject proxy, boolean can_edit) {
		return proxy != null && canUpdate(new Name(proxy), can_edit);
	}

	/** Check if the user can update a proxy attribute.
	 * @param proxy Proxy object to check.
	 * @param aname Name of attribute to update.
	 * @param can_edit Flag to allow editing.
	 * @return true if user can update the attribute */
	private boolean canUpdate(SonarObject proxy, String aname,
		boolean can_edit)
	{
		return proxy != null &&
		       canUpdate(new Name(proxy, aname), can_edit);
	}

	/** Check if the user can update an attribute.
	 * @param tname Type name of attribute to update.
	 * @param aname Name of attribute to update.
	 * @return true if user can update the attribute */
	public boolean canUpdate(String tname, String aname) {
		return canUpdate(tname, aname, edit_mode);
	}

	/** Check if the user can update an attribute.
	 * @param tname Type name of attribute to update.
	 * @return true if user can update the attribute */
	public boolean canUpdate(String tname) {
		return canUpdate(tname, edit_mode);
	}

	/** Check if the user can update a proxy attribute.
	 * @param proxy Proxy object to check.
	 * @return true if user can update the attribute */
	public boolean canUpdate(SonarObject proxy) {
		return canUpdate(proxy, edit_mode);
	}

	/** Check if the user can update a proxy attribute.
	 * @param proxy Proxy object to check.
	 * @param aname Name of attribute to update.
	 * @return true if user can update the attribute */
	public boolean canUpdate(SonarObject proxy, String aname) {
		return canUpdate(proxy, aname, edit_mode);
	}

	/** Check if the user is permitted to update an attribute, regardless of
	 * EDIT mode.
	 * @param tname Type name of attribute to update.
	 * @param aname Name of attribute to update.
	 * @return true if user can update the attribute */
	public boolean isUpdatePermitted(String tname, String aname) {
		return canUpdate(new Name(tname, "oname", aname), true);
	}

	/** Check if the user is permitted to update an attribute, regardless of
	 * EDIT mode.
	 * @param tname Type name of attribute to update.
	 * @return true if user can update the attribute */
	public boolean isUpdatePermitted(String tname) {
		return canUpdate(tname, true);
	}

	/** Check if the user is permitted to update a proxy attribute,
	 * regardless of EDIT mode.
	 * @param proxy Proxy object to check.
	 * @return true if user can update the attribute */
	public boolean isUpdatePermitted(SonarObject proxy) {
		return canUpdate(proxy, true);
	}

	/** Check if the user is permitted to update a proxy attribute,
	 * regardless of EDIT mode.
	 * @param proxy Proxy object to check.
	 * @param aname Name of attribute to update.
	 * @return true if user can update the attribute */
	public boolean isUpdatePermitted(SonarObject proxy, String aname) {
		return canUpdate(proxy, aname, true);
	}

	/** Check if the user can remove a proxy */
	private boolean canRemove(Name name, boolean can_edit) {
		return can_edit && namespace.canRemove(name, user);
	}

	/** Check if the user can remove a proxy */
	public boolean canRemove(SonarObject proxy) {
		return proxy != null && canRemove(new Name(proxy), edit_mode);
	}

	/** Check if the user can remove a proxy */
	public boolean canRemove(String tname, String oname) {
		return canRemove(new Name(tname, oname), edit_mode);
	}

	/** Dispose of the session */
	public void dispose() {
		seg_layer.dispose();
		desktop.dispose();
		for (MapTab tab: tabs)
			tab.dispose();
		tabs.clear();
		for (ProxyManager<?> man: managers)
			man.dispose();
		managers.clear();
		loc_manager.dispose();
		state.quit();
	}

	/** Get the session ID */
	public long getSessionId() {
		Connection c = state.lookupConnection();
		return c != null ? c.getSessionId() : 0;
	}
}
