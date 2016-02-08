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
import java.util.ArrayList;
import java.util.List;

import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.server.CameraImpl;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.DeviceContentionException;
import us.mn.state.dot.tms.server.comm.PriorityLevel;

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

	/** byte list of command values */
	private List<Byte> cmd;

	/** convenience value for the CAMERA_PTZ_FORCE_FULL setting */
	private final boolean force_full;

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

		force_full = SystemAttrEnum.CAMERA_PTZ_FORCE_FULL.getBoolean();

		log(String.format("PTZ command: P:%s  T:%s  Z:%s",
			p==null?"?":p.toString(), t==null?"?":t.toString(),
			z==null?"?":z.toString()));

	}

	/** Begin the operation. */
	@Override
	protected Phase<CohuPTZProperty> phaseTwo() {

		if(force_full) {
			cmd = new ArrayList<Byte>();

			cmd = CohuPTZProperty.processPTZInfo(CohuPTZProperty.Command.PAN, pan, cmd);
			cmd = CohuPTZProperty.processPTZInfo(CohuPTZProperty.Command.TILT, tilt, cmd);
			cmd = CohuPTZProperty.processPTZInfo(CohuPTZProperty.Command.ZOOM, zoom, cmd);

			return new PTZFullPhase();
		}

		return new PanPhase();
	}


	/**
	 * ptz full phase... CA-only
	 */
	protected class PTZFullPhase extends Phase<CohuPTZProperty> {
		protected Phase<CohuPTZProperty> poll(
			CommMessage<CohuPTZProperty> mess)
			throws IOException, DeviceContentionException {


			byte[] carr = CohuPTZProperty.list2bytearray(cmd);

			String decoded = new String(carr, "UTF-8");

			log("sending ptz full: " + decoded);
			mess.add(new PTZFullProperty(carr));
			doStoreProps(mess);
			updateOpStatus("ptz full sent");
			log("ptz full sent");
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
	protected class PTZFullProperty extends CohuPTZProperty {

		byte[] carr;

		public PTZFullProperty(byte[] c) {
			carr = c;
		}

		/** Encode a STORE request */
		@Override
		public void encodeStore(ControllerImpl c, OutputStream os)
			throws IOException {

			writePayload(os, c.getDrop(), carr);
		}
	}


}
