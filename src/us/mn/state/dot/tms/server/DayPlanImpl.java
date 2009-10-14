/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009  Minnesota Department of Transportation
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
import java.util.TreeSet;
import us.mn.state.dot.sonar.Namespace;
import us.mn.state.dot.tms.ChangeVetoException;
import us.mn.state.dot.tms.DayPlan;
import us.mn.state.dot.tms.Holiday;
import us.mn.state.dot.tms.HolidayHelper;
import us.mn.state.dot.tms.TMSException;

/**
 * Day plan for scheduling time actions.
 *
 * @author Douglas lau
 */
public class DayPlanImpl extends BaseObjectImpl implements DayPlan {

	/** DayPlan / Holiday table mapping */
	static protected TableMapping mapping;

	/** Load all the day plans */
	static public void loadAll() throws TMSException {
		System.err.println("Loading day plans...");
		mapping = new TableMapping(store, "iris", "day_plan","holiday");
		store.query("SELECT name FROM iris." + SONAR_TYPE + ";",
			new ResultFactory()
		{
			public void create(ResultSet row) throws Exception {
				namespace.addObject(new DayPlanImpl(namespace,
					row.getString(1)	// name
				));
			}
		});
	}

	/** Get a mapping of the columns */
	public Map<String, Object> getColumns() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
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

	/** Create a new day plan */
	public DayPlanImpl(String n) {
		super(n);
	}

	/** Create a day plan from database lookup */
	protected DayPlanImpl(Namespace ns, String n) throws TMSException {
		this(n);
		TreeSet<HolidayImpl> hset = new TreeSet<HolidayImpl>();
		for(Object o: mapping.lookup(SONAR_TYPE, this))
			hset.add((HolidayImpl)HolidayHelper.lookup((String)o));
		holidays = hset.toArray(new HolidayImpl[0]);
	}

	/** Holidays for the day plan */
	protected HolidayImpl[] holidays = new HolidayImpl[0];

	/** Set the holidays assigned to the day plan */
	public void setHolidays(Holiday[] hs) {
		HolidayImpl[] _hs = new HolidayImpl[hs.length];
		for(int i = 0; i < hs.length; i++) {
			if(hs[i] instanceof HolidayImpl)
				_hs[i] = (HolidayImpl)hs[i];
		}
		holidays = _hs;
	}

	/** Set the holidays assigned to the day plan */
	public void doSetHolidays(Holiday[] hs) throws TMSException {
		TreeSet<Storable> hset = new TreeSet<Storable>();
		for(Holiday h: hs) {
			if(h instanceof HolidayImpl)
				hset.add((HolidayImpl)h);
			else
				throw new ChangeVetoException("Invalid hday");
		}
		mapping.update(SONAR_TYPE, this, hset);
		setHolidays(hs);
	}

	/** Get the holidays assigned to the day plan */
	public Holiday[] getHolidays() {
		return holidays;
	}
}
