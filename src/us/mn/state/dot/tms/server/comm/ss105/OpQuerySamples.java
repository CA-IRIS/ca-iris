/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2004-2012  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm.ss105;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import us.mn.state.dot.sched.Completer;
import static us.mn.state.dot.tms.server.Constants.MISSING_DATA;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.DownloadRequestException;
import us.mn.state.dot.tms.server.comm.PriorityLevel;

/**
 * Operation to get binned samples from a SS105 device
 *
 * @author Douglas Lau
 */
public class OpQuerySamples extends OpSS105 {

	/** Sample period (seconds) */
	static private final int SAMPLE_PERIOD_SEC = 30;

	/** 30-Second interval completer */
	protected final Completer completer;

	/** Time stamp of sample data */
	protected long stamp;

	/** Oldest time stamp to accept from controller */
	protected final long oldest;

	/** Newest timestamp to accept from controller */
	protected final long newest;

	/** Volume data for each detector */
	protected int[] volume = new int[8];

	/** Scan data for each detector */
	protected int[] scans = new int[8];

	/** Speed data for each detector */
	protected int[] speed = new int[8];

	/** Create a new "query binned samples" operation */
	public OpQuerySamples(ControllerImpl c, Completer comp) {
		super(PriorityLevel.DATA_30_SEC, c);
		completer = comp;
		stamp = comp.getStamp();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(stamp);
		cal.add(Calendar.HOUR, -4);
		oldest = cal.getTimeInMillis();
		cal.setTimeInMillis(stamp);
		cal.add(Calendar.MINUTE, 5);
		newest = cal.getTimeInMillis();
		for(int i = 0; i < 8; i++) {
			volume[i] = MISSING_DATA;
			scans[i] = MISSING_DATA;
			speed[i] = MISSING_DATA;
		}
	}

	/** Begin the operation */
	public boolean begin() {
		return completer.beginTask(getKey()) && super.begin();
	}

	/** Create the first phase of the operation */
	protected Phase<SS105Property> phaseOne() {
		return new GetCurrentSamples();
	}

	/** Phase to get the most recent binned samples */
	protected class GetCurrentSamples extends Phase<SS105Property> {

		/** Get the most recent binned samples */
		protected Phase<SS105Property> poll(
			CommMessage<SS105Property> mess) throws IOException
		{
			BinnedSampleProperty bs = new BinnedSampleProperty();
			mess.add(bs);
			mess.queryProps();
			stamp = bs.timestamp.getTime();
			volume = bs.getVolume();
			scans = bs.getScans();
			speed = bs.getSpeed();
			SS105_LOG.log(controller.getName() + ": " + bs);
			if(stamp < oldest || stamp > newest) {
				SS105_LOG.log("BAD TIMESTAMP: " +
					new Date(stamp) + " for " + controller);
				setFailed();
				throw new DownloadRequestException(
					controller.toString());
			}
			return null;
		}
	}

	/** Cleanup the operation */
	public void cleanup() {
		controller.storeVolume(stamp, SAMPLE_PERIOD_SEC, 1, volume);
		controller.storeOccupancy(stamp, SAMPLE_PERIOD_SEC, 1, scans,
			BinnedSampleProperty.MAX_PERCENT);
		controller.storeSpeed(stamp, SAMPLE_PERIOD_SEC, 1, speed);
		completer.completeTask(getKey());
		super.cleanup();
	}
}
