/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2016  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.incident;

import java.awt.Color;
import java.util.HashMap;
import us.mn.state.dot.map.MapObject;
import us.mn.state.dot.map.Outline;
import us.mn.state.dot.map.Style;
import us.mn.state.dot.map.Symbol;
import us.mn.state.dot.map.VectorSymbol;
import us.mn.state.dot.tms.Incident;
import us.mn.state.dot.tms.ItemStyle;
import us.mn.state.dot.tms.client.proxy.ProxyTheme;
import static us.mn.state.dot.tms.client.widget.Widgets.UI;

/**
 * Theme for incident objects on the map.
 *
 * @author Douglas Lau
 */
public class IncidentTheme extends ProxyTheme<Incident> {

	/** Outline for unconfirmed styles */
	static public final Outline UN_OUTLINE = Outline.createSolid(
		Color.WHITE, 1);

	/** Unconfirmed crash style */
	static private final Style UN_CRASH = new Style(
		ItemStyle.UNCONFIRMED + " " + ItemStyle.CRASH, UN_OUTLINE,
		new Color(255, 128, 128, 128));

	/** Unconfirmed stall style */
	static private final Style UN_STALL = new Style(
		ItemStyle.UNCONFIRMED + " " + ItemStyle.STALL, UN_OUTLINE,
		new Color(255, 128, 255, 128));

	/** Unconfirmed roadwork style */
	static private final Style UN_ROADWORK = new Style(
		ItemStyle.UNCONFIRMED + " " + ItemStyle.ROADWORK, UN_OUTLINE,
		new Color(255, 208, 128, 128));

	/** Unconfirmed hazard style */
	static private final Style UN_HAZARD = new Style(
		ItemStyle.UNCONFIRMED + " " + ItemStyle.HAZARD, UN_OUTLINE,
		new Color(255, 255, 128, 128));

	/** Incident marker */
	static private final IncidentMarker MARKER = new IncidentMarker();

	/** Unconfirmed symbols */
	private final HashMap<String, Symbol> unconfirmed_syms =
		new HashMap<String, Symbol>();

	/** Create a new incident theme */
	public IncidentTheme(IncidentManager man) {
		super(man, MARKER);
		storeSymbol(UN_CRASH);
		storeSymbol(UN_STALL);
		storeSymbol(UN_ROADWORK);
		storeSymbol(UN_HAZARD);
	}

	/** Store one symbol */
	private void storeSymbol(Style sty) {
		unconfirmed_syms.put(sty.getLabel(), createSymbol(sty));
	}

	/** Create a symbol */
	private Symbol createSymbol(Style sty) {
		return new VectorSymbol(sty, MARKER, UI.scaled(22));
	}

	/** Get an appropriate style for the given map object */
	@Override
	public Style getStyle(MapObject mo) {
		if (mo instanceof IncidentGeoLoc) {
			IncidentGeoLoc loc = (IncidentGeoLoc)mo;
			return getStyle(loc.getIncident());
		}
		return dstyle;
	}

	/** Get an appropriate style for the given proxy object */
	@Override
	public Style getStyle(Incident inc) {
		if (manager.checkStyle(ItemStyle.UNCONFIRMED, inc)) {
			if (manager.checkStyle(ItemStyle.CLEARED, inc))
				return super.getStyle(inc);
			if (manager.checkStyle(ItemStyle.CRASH, inc))
				return UN_CRASH;
			if (manager.checkStyle(ItemStyle.STALL, inc))
				return UN_STALL;
			if (manager.checkStyle(ItemStyle.ROADWORK, inc))
				return UN_ROADWORK;
			if (manager.checkStyle(ItemStyle.HAZARD, inc))
				return UN_HAZARD;
		}
		return super.getStyle(inc);
	}

	/** Get a symbol by label */
	@Override
	public Symbol getSymbol(String label) {
		Symbol sym = unconfirmed_syms.get(label);
		return (sym != null) ? sym : super.getSymbol(label);
	}
}
