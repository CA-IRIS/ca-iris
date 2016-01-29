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

package us.mn.state.dot.tms.server.comm.cohuptz;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.Float;

import us.mn.state.dot.tms.server.CameraImpl;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.PriorityLevel;

/**
 * Cohu PTZ operation to pan/tilt/zoom a camera.
 *
 * @author Travis Swanston
 */
public class OpPTZCamera extends OpCohuPTZ {

	/** Op description */
	static private final String OP_DESC = "PTZ";

	/** pan vector */
	protected final Float pan;

	/** tilt vector */
	protected final Float tilt;

	/** zoom vector */
	protected final Float zoom;

	/**
	 * Create the operation.
	 * @param c the CameraImpl instance
	 * @param cp the CohuPTZPoller instance
	 * @param p the pan vector [-1..1] or null for NOP
	 * @param t the tilt vector [-1..1] or null for NOP
	 * @param z the zoom vector [-1..1] or null for NOP
	 */
	public OpPTZCamera(CameraImpl c, CohuPTZPoller cp, Float p, Float t, Float z) {
		super(PriorityLevel.COMMAND, c, cp, OP_DESC);
		pan  = p;
		tilt = t;
		zoom = z;
	}

	/** Begin the operation. */
	@Override
	protected Phase<CohuPTZProperty> phaseTwo() {
		return new PanTiltStopPhase();
	}

	/** pan-tilt stop phase... special case, 0/3 */
	protected class PanTiltStopPhase extends Phase<CohuPTZProperty> {
		protected Phase<CohuPTZProperty> poll(
			CommMessage<CohuPTZProperty> mess)
			throws IOException {

			if ((pan == null && tilt == null && zoom == null)
				|| (pan != null && tilt != null
				&& Math.abs(pan) < CohuPTZProperty.PTZ_THRESH
				&& Math.abs(tilt) < CohuPTZProperty.PTZ_THRESH)
				) {

				if(pan == null && tilt == null && zoom == null) {
					CohuPTZPoller.DEBUG_LOG.log("PTZ values all null.");
				}

				mess.add(new PanTiltStopProperty());
				doStoreProps(mess);
				updateOpStatus("pan-tilt stop sent");

				return new ZoomPhase();
			}
			return new PanPhase();
		}
	}

	/** pan phase, 1/3 */
	protected class PanPhase extends Phase<CohuPTZProperty> {
		/**
		 * Whether this is first time sending this instance.
		 */
		//private boolean first = true;

		protected Phase<CohuPTZProperty> poll(
				CommMessage<CohuPTZProperty> mess)
				throws IOException
			{
				Phase<CohuPTZProperty> ret = null;
				if (pan != null) {
					mess.add(new PanProperty(pan));
					doStoreProps(mess);
					updateOpStatus("pan sent");

					// double-send stops
					//if (first && Math.abs(pan) < CohuPTZProperty.PTZ_THRESH)
					//	ret = this;
				}

				//first = false;
				return ret != null ? ret : new TiltPhase();
		}
	}

	/** tilt phase, 2/3 */
	protected class TiltPhase extends Phase<CohuPTZProperty> {
		/**
		 * Whether this is first time sending this instance.
		 */
		//private boolean first = true;

		protected Phase<CohuPTZProperty> poll(
			CommMessage<CohuPTZProperty> mess)
			throws IOException
		{
			Phase<CohuPTZProperty> ret = null;
			if (tilt != null) {
				mess.add(new TiltProperty(tilt));
				doStoreProps(mess);
				updateOpStatus("tilt sent");

				// double-send stops
				//if (first && Math.abs(tilt) < CohuPTZProperty.PTZ_THRESH)
				//	ret = this;
			}

			//first = false;
			return ret != null ? ret : new ZoomPhase();
		}
	}

	/** zoom phase, 3/3 */
	protected class ZoomPhase extends Phase<CohuPTZProperty> {
		/**
		 * Whether this is first time sending this instance.
		 */
		//private boolean first = true;

		protected Phase<CohuPTZProperty> poll(
			CommMessage<CohuPTZProperty> mess)
			throws IOException
		{
			Phase<CohuPTZProperty> ret = null;
			if (zoom != null) {
				mess.add(new ZoomProperty(zoom));
				doStoreProps(mess);
				updateOpStatus("zoom sent");

				// double-send stops
				//if (first && Math.abs(zoom) < CohuPTZProperty.PTZ_THRESH)
				//	ret = this;
			}

			//first = false;
			return ret;
		}
	}

	/** pan-tilt stop property, used for special case to send this exact command */
	protected class PanTiltStopProperty extends CohuPTZProperty {
		/** Encode a STORE request */
		@Override
		public void encodeStore(ControllerImpl c, OutputStream os)
			throws IOException
		{
			byte[] cmd = new byte[]{ 'P', 'S', 'T', 'S' };
			writePayload(os, c.getDrop(), cmd);
		}
	}

}
