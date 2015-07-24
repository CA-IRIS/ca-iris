/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2014-2015  AHMCT, University of California
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
package us.mn.state.dot.tms.server;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import us.mn.state.dot.sonar.Namespace;
import us.mn.state.dot.tms.Camera;
import us.mn.state.dot.tms.PresetAlias;
import us.mn.state.dot.tms.TMSException;

/**
 * PresetAliasImpl represents a single CCTV alias:preset# mapping.
 *
 * @author Travis Swanston
 */
public class PresetAliasImpl extends BaseObjectImpl implements PresetAlias {

	/** Load all the preset alias mappings */
	static protected void loadAll() throws TMSException {
		namespace.registerType(SONAR_TYPE, PresetAliasImpl.class);
		store.query("SELECT name, camera, alias, preset_num " +
			"FROM iris." + SONAR_TYPE + ";", new ResultFactory()
		{
			public void create(ResultSet row) throws Exception {
				namespace.addObject(new PresetAliasImpl(
					namespace,
					row.getString(1),	// name
					row.getString(2),	// camera
					row.getInt(3),		// alias
					row.getInt(4)		// preset_num
				));
			}
		});
	}

	/** Get a mapping of the columns */
	public Map<String, Object> getColumns() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("camera", camera);
		map.put("alias", alias);
		map.put("preset_num", preset_num);
		return map;
	}

	/** Get the database table name */
	public String getTable() {
		return "iris." + SONAR_TYPE;
	}

	/** Get the SONAR type name */
	public String getTypeName() {
		return SONAR_TYPE;
	}

	/** Create a new camera preset with a string name */
	public PresetAliasImpl(String n) throws TMSException {
		super(n);
	}

	/** Create a camera preset */
	protected PresetAliasImpl(String n, CameraImpl c, int a, int p) {
		super(n);
		camera = c;
		alias = a;
		preset_num = p;
	}

	/** Create a camera preset */
	protected PresetAliasImpl(Namespace ns, String n, String c, int a,
		int p)
	{
		this(n, (CameraImpl)ns.lookupObject(Camera.SONAR_TYPE, c), a,
			p);
	}

	/** CCTV Camera */
	private CameraImpl camera;

	/** Get the CCTV camera */
	@Override
	public Camera getCamera() {
		return camera;
	}

	/** Preset alias */
	private int alias;

	/** Get the preset alias */
	@Override
	public int getPresetAliasName() {
		return alias;
	}

	/** Preset number */
	private int preset_num;

	/** Set the preset number */
	@Override
	public void setPresetNum(int p) {
		preset_num = p;
	}

	/** Set the preset number */
	public void doSetPresetNum(int p) throws TMSException {
		if (p != preset_num) {
			store.update(this, "preset_num", p);
			setPresetNum(p);
		}
	}

	/** Get the preset number */
	@Override
	public int getPresetNum() {
		return preset_num;
	}

}
