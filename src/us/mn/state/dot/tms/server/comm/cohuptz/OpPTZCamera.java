/*
 * IRIS -- Intelligent Roadway Information System
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

package us.mn.state.dot.tms.server.comm.cohuptz;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.Float;

import us.mn.state.dot.tms.server.CameraImpl;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.utils.NumericAlphaComparator;

/**
 * Cohu PTZ operation to pan/tilt/zoom a camera.
 *
 * @author Travis Swanston
 * @author Jacob Barde
 */
public class OpPTZCamera extends OpCohuPTZ {

	/** Log a message to the debug log */
	static public void log(String msg) {
		CohuPTZPoller.DEBUG_LOG.log(msg);
	}

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
		log(String.format("PTZ command: P:%s  T:%s  Z:%s",
			p==null?"?":p.toString(), t==null?"?":t.toString(),
			z==null?"?":z.toString()));
	}

	/** Begin the operation. */
	@Override
	protected Phase<CohuPTZProperty> phaseTwo() {
		return new PTZFullStopPhase();
	}

	/**
	 * ptz full stop phase... special case, 0/3
	 * anytime all three values are null or all effectively zero send a full
	 * stop command
	 */
	protected class PTZFullStopPhase extends Phase<CohuPTZProperty> {
		protected Phase<CohuPTZProperty> poll(
			CommMessage<CohuPTZProperty> mess)
			throws IOException {

			if (!(NumericAlphaComparator.compareFloats(pan, tilt,
				CohuPTZProperty.PTZ_THRESH) == 0
				&& NumericAlphaComparator.compareFloats(pan,
				zoom, CohuPTZProperty.PTZ_THRESH) == 0)) {

				return new PanPhase();
			}

			// at this point all values are effectively the same

			if(pan == null) {
				log("PTZ values null.");
			}

			if (pan == null
				|| Math.abs(pan) < CohuPTZProperty.PTZ_THRESH) {

				log("sending ptz full stop");
				mess.add(new PTZFullStopProperty());
				doStoreProps(mess);
				updateOpStatus("ptz full stop sent");
				log("ptz full stop sent");
			}
			return null;
		}
	}

	/** pan phase, 1/3 */
	protected class PanPhase extends Phase<CohuPTZProperty> {
		/**
		 * Whether this is first time sending this instance.
		 */

		protected Phase<CohuPTZProperty> poll(
				CommMessage<CohuPTZProperty> mess)
				throws IOException {

				if (pan != null) {
					log("sending pan=" + pan);
					mess.add(new PanProperty(pan));
					doStoreProps(mess);
					updateOpStatus("pan sent");
					log("pan sent");
				}

				return new TiltPhase();
		}
	}

	/** tilt phase, 2/3 */
	protected class TiltPhase extends Phase<CohuPTZProperty> {
		/**
		 * Whether this is first time sending this instance.
		 */

		protected Phase<CohuPTZProperty> poll(
			CommMessage<CohuPTZProperty> mess)
			throws IOException {

			if (tilt != null) {
				log("sending tilt=" + tilt);
				mess.add(new TiltProperty(tilt));
				doStoreProps(mess);
				updateOpStatus("tilt sent");
				log("tilt sent");
			}

			return new ZoomPhase();
		}
	}

	/** zoom phase, 3/3 */
	protected class ZoomPhase extends Phase<CohuPTZProperty> {
		/**
		 * Whether this is first time sending this instance.
		 */

		protected Phase<CohuPTZProperty> poll(
			CommMessage<CohuPTZProperty> mess)
			throws IOException {

			if (zoom != null) {
				log("sending zoom=" + zoom);
				mess.add(new ZoomProperty(zoom));
				doStoreProps(mess);
				updateOpStatus("zoom sent");
			}

			return null;
		}
	}

	/** pan-tilt stop property, used for special case to send this exact command */
	protected class PTZFullStopProperty extends CohuPTZProperty {
		/** Encode a STORE request */
		@Override
		public void encodeStore(ControllerImpl c, OutputStream os)
			throws IOException
		{
			byte[] cmd = new byte[]{ 'P', 'S', 'T', 'S', 'Z', 'S' };
			writePayload(os, c.getDrop(), cmd);
		}
	}

}
