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
package us.mn.state.dot.tms.server.comm.carwis;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.Math;
import java.lang.NumberFormatException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import us.mn.state.dot.tms.units.Distance;
import static us.mn.state.dot.tms.units.Distance.Units.METERS;
import us.mn.state.dot.tms.units.Speed;
import static us.mn.state.dot.tms.units.Speed.Units.KPH;
import us.mn.state.dot.tms.units.Temperature;
import static us.mn.state.dot.tms.units.Temperature.Units.CELSIUS;

/**
 * CA RWIS Feed parser.
 * The ugly XML parsing needs to be refactored.
 *
 * @author Travis Swanston
 */
public class FeedParser {

	// Some NTCIP magic numbers
	private final static int ESS_DIR_UNAVAIL        = 361;
	private final static int ESS_PRECIPRATE_UNAVAIL = 65535;
	private final static int ESS_SPEED_UNAVAIL      = 65535;
	private final static int ESS_TEMP_UNAVAIL       = 1001;
	private final static int ESS_VIS_UNAVAIL        = 1000001;

	/** Grams per liter of water */
	private final static double G_PER_L_H2O = 1000.00028D;

	/** Liters per mm of rainfall */
	private final static double L_PER_MM_RAINFALL = 1.0D;

	/** Seconds per hour */
	private final static int SEC_PER_HOUR = 3600;

	private final InputStream istream;
	private boolean already_parsed = false;

	public FeedParser(InputStream is) {
		istream = is;
	}

	/** Read the feed, updating recs with the new data. */
	public boolean parse(HashMap<String, RwisRec> recs) throws ParseException {
		if (already_parsed)
			return false;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setIgnoringComments(true);

		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			throw new ParseException("PCE", 0);
		}

		Document doc = null;
		try {
			doc = builder.parse(istream);
		}
		catch (IOException e) {
			throw new ParseException("IOE", 0);
		}
		catch (SAXException e) {
			throw new ParseException("SAXE", 0);
		}

		NodeList dataList = doc.getElementsByTagName("data");
		ensure((dataList.getLength() == 1), 1);
		Element eData =(Element)dataList.item(0);
		NodeList nRwisList = eData.getElementsByTagName("rwis");
		for (int ri = 0; ri < nRwisList.getLength(); ++ri) {
            RwisRec rec = null;
		    try {
                Node n = nRwisList.item(ri);
                if (n.getNodeType() != Node.ELEMENT_NODE)
                    continue;

                // stationId
                NodeList nlsid = getElements(n ,"index");
                ensure((nlsid.getLength() == 1), 2);

                NodeList nlsid1 = getKids(nlsid.item(0));
                ensure((nlsid1.getLength() == 1), 3);
                String sid = trimAndNullEmpty((nlsid1.item(0)).getNodeValue());
                rec = new RwisRec(sid);

                // timestamp
                NodeList nlts = getElements(n,"recordTimestamp");
                ensure((nlts.getLength() == 1), 4);
                NodeList nlDate = getElements(nlts.item(0),"recordDate");
                ensure((nlDate.getLength() == 1), 5);
                NodeList nlDateCN = getKids(nlDate.item(0));
                ensure((nlDateCN.getLength() == 1), 6);
                String date = (nlDateCN.item(0)).getNodeValue();
                NodeList nlTime = getElements(nlts.item(0),"recordTime");
                ensure((nlTime.getLength() == 1), 7);
                NodeList nlTimeCN = getKids(nlTime.item(0));
                ensure((nlTimeCN.getLength() == 1), 8);
                String time = (nlTimeCN.item(0)).getNodeValue();

                rec.obs_time = translateTimestamp(date + " " + time,
                        TimeZone.getTimeZone("America/Los_Angeles"),
                        "yyyy-MM-dd HH:mm:ss");

                // rwisData
                NodeList nlrd = getElements(n,"rwisData");
                ensure((nlrd.getLength() == 1), 9);

                // airTemp
                NodeList nlTempData = getElements(nlrd.item(0),"temperatureData");
                ensure((nlTempData.getLength() == 1), 10);

                NodeList nlTempSensEntry = getElements(nlTempData.item(0),"essTemperatureSensorEntry");
                if ((nlTempSensEntry.getLength() > 0)) {

                    // only using first essTemperatureSensorEntry.
                    NodeList nlAirTemp = getElements(nlTempSensEntry.item(0),"essAirTemperature");
                    ensure((nlAirTemp.getLength() == 1), 12);
                    NodeList nlAirTempCN = getKids(nlAirTemp.item(0));
                    if (nlAirTempCN.getLength() != 1)
                        rec.air_temp = null;
                    else
                        rec.air_temp = parseAirTemperature((nlAirTempCN.item(0)).getNodeValue());

                }

                // windData
                NodeList nlWindData = getElements(nlrd.item(0),"windData");
                ensure((nlWindData.getLength() == 1), 13);

                // windSpeed
                NodeList nlAvgWindSpeed = getElements(nlWindData.item(0),"essAvgWindSpeed");
                ensure((nlAvgWindSpeed.getLength() == 1), 14);
                NodeList nlAvgWindSpeedCN = getKids(nlAvgWindSpeed.item(0));
                if (nlAvgWindSpeedCN.getLength() != 1)
                    rec.wind_speed_avg = null;	// allow empty
                else
                    rec.wind_speed_avg = parseAvgWindSpeed((nlAvgWindSpeedCN.item(0)).getNodeValue());

                // windDir
                NodeList nlAvgWindDirection = getElements(nlWindData.item(0),"essAvgWindDirection");
                ensure((nlAvgWindDirection.getLength() == 1), 15);
                NodeList nlAvgWindDirectionCN = getKids(nlAvgWindDirection.item(0));
                if (nlAvgWindDirectionCN.getLength() != 1)
                    rec.wind_dir_avg = null;	// allow empty
                else
                    rec.wind_dir_avg = parseAvgWindDir((nlAvgWindDirectionCN.item(0)).getNodeValue());

                // gustSpeed
                NodeList nlMaxWindGustSpeed = getElements(nlWindData.item(0),"essMaxWindGustSpeed");
                ensure((nlMaxWindGustSpeed.getLength() == 1), 16);
                NodeList nlMaxWindGustSpeedCN = getKids(nlMaxWindGustSpeed.item(0));
                if (nlMaxWindGustSpeedCN.getLength() != 1)
                    rec.wind_speed_gust = null;	// allow empty
                else
                    rec.wind_speed_gust = parseGustSpeed((nlMaxWindGustSpeedCN.item(0)).getNodeValue());

                // gustDir
                NodeList nlMaxWindGustDir = getElements(nlWindData.item(0),"essMaxWindGustDir");
                ensure((nlMaxWindGustDir.getLength() == 1), 17);
                NodeList nlMaxWindGustDirCN = getKids(nlMaxWindGustDir.item(0));
                if (nlMaxWindGustDirCN.getLength() != 1)
                    rec.wind_dir_gust = null;
                else
                    rec.wind_dir_gust = parseGustDir((nlMaxWindGustDirCN.item(0)).getNodeValue());

                // humidityPrecipData
                NodeList nlHPData = getElements(nlrd.item(0),"humidityPrecipData");
                ensure((nlHPData.getLength() == 1), 18);

                // precipRate
                NodeList nlEssPrecipRate = getElements(nlHPData.item(0),"essPrecipRate");
                ensure((nlEssPrecipRate.getLength() == 1), 19);
                NodeList nlEssPrecipRateCN = getKids(nlEssPrecipRate.item(0));
                if (nlEssPrecipRateCN.getLength() != 1)
                    rec.precip_rate = null;		// allow empty
                else
                    rec.precip_rate = parsePrecipRate((nlEssPrecipRateCN.item(0)).getNodeValue());

                // visibilityData
                NodeList nlVisData = getElements(nlrd.item(0),"visibilityData");
                ensure((nlVisData.getLength() == 1), 20);

                // visib
                NodeList nlEssVisibility = getElements(nlVisData.item(0),"essVisibility");
                ensure((nlEssVisibility.getLength() == 1), 21);
                NodeList nlEssVisibilityCN = getKids(nlEssVisibility.item(0));
                if (nlEssVisibilityCN.getLength() != 1)
                    rec.visibility = null;		// allow empty
                else
                    rec.visibility = parseVisibility((nlEssVisibilityCN.item(0)).getNodeValue());

                // pavmentSensorData (sic)
                NodeList nlPSData = getElements(nlrd.item(0),"pavmentSensorData");	// (sic)
                ensure((nlPSData.getLength() <= 1), 22);	// allow 0
                if (nlPSData.getLength() > 0) {

                    // essPavementSensorEntry
                    NodeList nlPSEntry = getElements(nlPSData.item(0),"essPavementSensorEntry");
                    int numPSE = nlPSEntry.getLength();	// don't trust <numEssPavementSensors>
                    for (int i=0; i<numPSE; ++i) {		// allow 0
                        NodeList nlEssSensId = getElements(nlPSEntry.item(i),"essPavementSensorIndex");
                        ensure((nlEssSensId.getLength() == 1), 23);
                        NodeList nlEssSensIdCN = getKids(nlEssSensId.item(0));
                        if (nlEssSensIdCN.getLength() != 1) {
                            // skipping essPavementSensorEntry with no index (workaround for bug in the feed source)
                            continue;
                        }
                        String sensorId = trimAndNullEmpty((nlEssSensIdCN.item(0)).getNodeValue());

                        NodeList nlEssSurfTemp = getElements(nlPSEntry.item(i),"essSurfaceTemperature");
                        ensure((nlEssSurfTemp.getLength() == 1), 24);
                        NodeList nlEssSurfTempCN = getKids(nlEssSurfTemp.item(0));
                        if (nlEssSurfTempCN.getLength() != 1)
                            rec.addSurfaceTemp(null);	// allow empty
                        else
                            rec.addSurfaceTemp(parseSurfaceTemperature((nlEssSurfTempCN.item(0)).getNodeValue()));
                    }

                    // essSubSurfaceSensortEntry (sic)
                    NodeList nlSSSEntry = getElements(nlPSData.item(0),"essSubSurfaceSensortEntry");	// (sic)
                    int numSSSE = nlSSSEntry.getLength();	// don't trust <numEssSubSurfaceSensors>
                    for (int i=0; i<numSSSE; ++i) {		// allow 0
                        NodeList nlEssSSSensId = getElements(nlSSSEntry.item(i),"essSubSurfaceSensorIndex");
                        ensure((nlEssSSSensId.getLength() == 1), 25);
                        NodeList nlEssSSSensIdCN = getKids(nlEssSSSensId.item(0));
                        if (nlEssSSSensIdCN.getLength() != 1) {
                            // skipping essSubSurfaceSensortEntry with no index (workaround for bug in the feed source)
                            continue;
                        }
                        String sensorId = trimAndNullEmpty((nlEssSSSensIdCN.item(0)).getNodeValue());

                        NodeList nlEssSubSurfTemp = getElements(nlSSSEntry.item(i),"essSubSurfaceTemperature");
                        ensure((nlEssSubSurfTemp.getLength() == 1), 26);
                        NodeList nlEssSubSurfTempCN = getKids(nlEssSubSurfTemp.item(0));
                        if (nlEssSubSurfTempCN.getLength() != 1)
                            rec.addSubsurfaceTemp(null);	// allow empty
                        else
                            rec.addSubsurfaceTemp(parseSubSurfaceTemperature((nlEssSubSurfTempCN.item(0)).getNodeValue()));
                    }
                }
                recs.put(rec.getSiteId(), rec);
                CaRwisPoller.log("FeedParser: put rec for " + rec.getSiteId() + " into recs.");
            } catch (Exception e) {
                String recId = rec != null ? " for " + rec.getSiteId() : "";
                CaRwisPoller.log("FeedParser: failed to parse rec" + recId + ": " + e.getMessage());
            }
		}

		already_parsed = true;
		return true;
	}

	public static NodeList getElements(Node n, String tag) {
		if (n instanceof Element)
			return ((Element)n).getElementsByTagName(tag);
		return null;
	}

	public static NodeList getKids(Node n) {
		return n.getChildNodes();
	}

	public void ensure(boolean state, int offset) throws ParseException {
		if (!state)
			throw new ParseException(Integer.toString(offset), offset);
	}

	public static Long translateTimestamp(String ts, TimeZone tz, String fmtIn) {
		if (tz == null)
			return null;
		try {
			SimpleDateFormat sdfIn = new SimpleDateFormat(fmtIn);
			sdfIn.setLenient(false);
			sdfIn.setTimeZone(tz);
			Date pd = sdfIn.parse(ts);
			return Long.valueOf(pd.getTime());
		}
		catch(ParseException e) {
			return null;
		}
	}

	protected static String trimAndNullEmpty(String s) {
		if (s == null)
			return null;
		String trim = s.trim();
		if (trim.equals(""))
			return null;
		return trim;
	}

	protected static Integer parseAirTemperature(String s) {
		String a = trimAndNullEmpty(s);
		if (a == null)
			return null;
		int val;
		try {
			val = Integer.parseInt(a);
		}
		catch (NumberFormatException e) {
			return null;
		}
		if (val == ESS_TEMP_UNAVAIL)
			return null;
		double degC = (((double)val)/10.0D);
		int i = (new Temperature(degC, Temperature.Units.CELSIUS)).round(CELSIUS);
		return Integer.valueOf(i);
	}

	protected static Integer parseAvgWindSpeed(String s) {
		String a = trimAndNullEmpty(s);
		if (a == null)
			return null;
		int val;
		try {
			val = Integer.parseInt(a);
		}
		catch (NumberFormatException e) {
			return null;
		}
		if (val == ESS_SPEED_UNAVAIL)
			return null;
		double mps = ((double)val) / 10.0D;
		double kph = mps*((double)SEC_PER_HOUR/1000.0D);
		int i = (new Speed(kph, Speed.Units.KPH)).round(KPH);
		return Integer.valueOf(i);
	}

	protected static Integer parseAvgWindDir(String s) {
		String a = trimAndNullEmpty(s);
		if (a == null)
			return null;
		int val;
		try {
			val = Integer.parseInt(a);
		}
		catch (NumberFormatException e) {
			return null;
		}
		if (val == ESS_DIR_UNAVAIL)
			return null;
		if (val == 360)
			val = 0;
		return Integer.valueOf(val);
	}


	protected static Integer parseGustSpeed(String s) {
		String a = trimAndNullEmpty(s);
		if (a == null)
			return null;
		int val;
		try {
			val = Integer.parseInt(a);
		}
		catch (NumberFormatException e) {
			return null;
		}
		if (val == ESS_SPEED_UNAVAIL)
			return null;

		double mps = ((double)val) / 10.0D;
		double kph = mps*((double)SEC_PER_HOUR/1000.0D);
		int i = (new Speed(kph, Speed.Units.KPH)).round(KPH);
		return Integer.valueOf(i);
	}

	protected static Integer parseGustDir(String s) {
		String a = trimAndNullEmpty(s);
		if (a == null)
			return null;
		int val;
		try {
			val = Integer.parseInt(a);
		}
		catch (NumberFormatException e) {
			return null;
		}
		if (val == ESS_DIR_UNAVAIL)
			return null;
		if (val == 360)
			val = 0;
		return Integer.valueOf(val);
	}

	// get rain precipitation rate or snow water equivalent
	protected static Integer parsePrecipRate(String s) {
		String a = trimAndNullEmpty(s);
		if (a==null)
			return null;
		int val;
		try {
			val = Integer.parseInt(a);
		}
		catch (NumberFormatException e) {
			return null;
		}
		if (val == ESS_PRECIPRATE_UNAVAIL)
			return null;

		float gramsPerSqMPerSecond = (((float)(val)) / 10.0F);

		float mmPerHr = (
			( gramsPerSqMPerSecond / (float)G_PER_L_H2O)
			/ (float)L_PER_MM_RAINFALL) * (float)SEC_PER_HOUR ;

		return Integer.valueOf(Math.round(mmPerHr));
	}

	protected static Integer parseVisibility(String s) {
		String a = trimAndNullEmpty(s);
		if (a==null)
			return null;
		int val;
		try {
			val = Integer.parseInt(a);
		}
		catch (NumberFormatException e) {
			return null;
		}
		if (val == ESS_VIS_UNAVAIL)
			return null;

		double meters = (((double)val)/10.0D);
		int i = (new Distance(meters, Distance.Units.METERS)).round(Distance.Units.METERS);
		return Integer.valueOf(i);
	}

	protected static Integer parseSurfaceTemperature(String s) {
		String a = trimAndNullEmpty(s);
		if (a==null)
			return null;
		int val;
		try {
			val = Integer.parseInt(a);
		}
		catch (NumberFormatException e) {
			return null;
		}
		if (val == ESS_TEMP_UNAVAIL)
			return null;
		double degC = (((double)val)/10.0D);
		int i = (new Temperature(degC, Temperature.Units.CELSIUS)).round(CELSIUS);
		return Integer.valueOf(i);
	}

	protected static Integer parseSubSurfaceTemperature(String s) {
		String a = trimAndNullEmpty(s);
		if (a==null)
			return null;
		int val;
		try {
			val = Integer.parseInt(a);
		}
		catch (NumberFormatException e) {
			return null;
		}
		if (val == ESS_TEMP_UNAVAIL)
			return null;
		double degC = (((double)val)/10.0D);
		int i = (new Temperature(degC, Temperature.Units.CELSIUS)).round(CELSIUS);
		return Integer.valueOf(i);
	}

}
