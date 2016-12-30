/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2016  Minnesota Department of Transportation
 * Copyright (C) 2012  Iteris Inc.
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
package us.mn.state.dot.tms;

/**
 * Communication protocol enumeration.  The ordinal values correspond to the
 * records in the iris.comm_protocol look-up table.
 *
 * @author Douglas Lau
 * @author Michael Darter
 * @author Dan Rossiter
 */
public enum CommProtocol {

	/** NTCIP Class B (0) */
	NTCIP_B("NTCIP Class B"),

	/** MnDOT 170 4-bit (1) */
	MNDOT_4("MnDOT 170 (4-bit)"),

	/** MnDOT 170 5-bit (2) */
	MNDOT_5("MnDOT 170 (5-bit)"),

	/** Wavetronix SmartSensor 105 (3) */
	SS_105("SmartSensor 105"),

	/** Canoga (4) */
	CANOGA("Canoga"),

	/** Pelco P (5) */
	PELCO_P("Pelco P"),

	/** Pelco D camera control (6) */
	PELCO_D_PTZ("Pelco D PTZ"),

	/** NTCIP Class C (7) */
	NTCIP_C("NTCIP Class C"),

	/** Manchester camera control (8) */
	MANCHESTER_PTZ("Manchester PTZ"),

	/** DMS XML (9) */
	DMSXML("DMS XML"),

	/** Msg Feed (10) */
	MSG_FEED("MSG_FEED"),

	/** NTCIP Class A (11) */
	NTCIP_A("NTCIP Class A"),

	/** Pelco video matrix switcher (12) */
	PELCO_SWITCHER("Pelco Switcher"),

	/** Vicon camera control (13) */
	VICON_PTZ("Vicon PTZ"),

	/** Wavetronix SmartSensor 125 HD (14) */
	SS_125("SmartSensor 125 HD"),

	/** Optical Scientific ORG-815 Precipitation Sensor (15) */
	ORG_815("OSi ORG-815"),

	/** Infinova wrapping Pelco D camera control (16) */
	INFINOVA_D_PTZ("Infinova D PTZ"),

	/** RTMS G4 (17) */
	RTMS_G4("RTMS G4"),

	/** RTMS (18) */
	RTMS("RTMS"),

	/** Infotek Wizard (19) */
	INFOTEK_WIZARD("Infotek Wizard"),

	/** Sensys (20) */
	SENSYS("Sensys"),

	/** PeMS (21) */
	PEMS("PeMS"),

	/** SSI (22) */
	SSI("SSI"),

	/** CHP Incidents (23) */
	CHP_INCIDENTS("CHP Incidents"),

	/** URMS (24) */
	URMS("URMS"),

	/** Digital Loggers Inc DIN Relay (25) */
	DIN_RELAY("DLI DIN Relay"),

	/** Axis 292 Video Decoder (26) */
	AXIS_292("Axis 292"),

	/** Axis PTS (27) */
	AXIS_PTZ("Axis PTZ"),

	/** HySecurity STC gate arm (28) */
	HYSECURITY_STC("HySecurity STC"),

	/** Cohu PTZ (29) */
	COHU_PTZ("Cohu PTZ"),

	/** DR-500 doppler radar (30) */
	DR_500("DR-500"),

	/** ADDCO NodeComm sign control (31) */
	ADDCO("ADDCO"),

	/** TransCore E6 tag reader (32) */
	TRANSCORE_E6("TransCore E6"),

	/** Control By Web (33) */
	CBW("CBW"),

	/** Incident feed (34) */
	INC_FEED("Incident Feed"),

	/** CA RWIS (35) */
	CA_RWIS("CA RWIS"),

	/** TTIP DMS (36) */
	TTIP_DMS("TTIP DMS");

	/** Create a new comm protocol value */
	CommProtocol(String d) {
		description = d;
	}

	/** Protocol description */
	public final String description;

	/** Get the string representation */
	@Override
	public String toString() {
		return description;
	}

	/** Get a comm protocol from an ordinal value */
	static public CommProtocol fromOrdinal(short o) {
		if (o >= 0 && o < values().length)
			return values()[o];
		else
		return null;
	}

}
