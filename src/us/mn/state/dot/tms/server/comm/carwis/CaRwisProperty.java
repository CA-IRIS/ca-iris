/*
 * IRIS -- Intelligent Roadway Information System
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
package us.mn.state.dot.tms.server.comm.carwis;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.HashMap;
import us.mn.state.dot.tms.utils.LineReader;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.ControllerProperty;

/**
 * Property.
 *
 * @author Travis Swanston
 */
public class CaRwisProperty extends ControllerProperty {

	/** Mapping of site_id to most recent RWIS records */
	private HashMap<String, RwisRec> records;

	/** Constructor. */
	public CaRwisProperty(HashMap<String, RwisRec> recs) {
		records = recs;
	}

	/** Decode a QUERY response.  */
	@Override
	public void decodeQuery(ControllerImpl c, InputStream is)
		throws IOException
	{
		if (is == null)
			throw new EOFException();
		FeedParser fp = new FeedParser(is);
		try {
			fp.parse(records);
		}
		catch (ParseException e) {
			CaRwisPoller.log("parse exception: " + e);
			records = null;
			return;
		}
		CaRwisPoller.log("parsing complete.  recs: "
			+ ((records == null) ? "null" : records.size()));
	}

}
