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


	private List<Byte> cmd;

	private final boolean fixspd;

	private enum Command {
		PAN,
		TILT,
		ZOOM
	};

	// fixed speed commands
	private static final byte cpF = (byte)'P';
	private static final byte ctF = (byte)'T';
	private static final byte czF = (byte)'Z';

	// fixed speed arguments                   direction
	private static final byte apP = (byte)'R'; //+
	private static final byte apN = (byte)'L'; //-
	private static final byte atP = (byte)'U'; //+
	private static final byte atN = (byte)'D'; //-
	private static final byte azP = (byte)'I'; //+
	private static final byte azN = (byte)'O'; //-

	// variable speed zoom command
	private static final byte cz = (byte)'c';

	// variable speed commands and arguments    direction
	private static final byte cpP = (byte)'r';  //+
	private static final byte cpN = (byte)'l';  //-
	private static final byte ctP = (byte)'u';  //+
	private static final byte ctN = (byte)'d';  //-
	private static final byte azPv = (byte)'Z'; //+
	private static final byte azNv = (byte)'z'; //-

	// stop argument
	private static final byte aS = (byte)'S';



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

		fixspd = SystemAttrEnum.CAMERA_PTZ_FIXED_SPEED.getBoolean();

		log(String.format("PTZ command: P:%s  T:%s  Z:%s",
			p==null?"?":p.toString(), t==null?"?":t.toString(),
			z==null?"?":z.toString()));

	}

	/** Begin the operation. */
	@Override
	protected Phase<CohuPTZProperty> phaseTwo() {

		if(SystemAttrEnum.CAMERA_PTZ_FORCE_FULL.getBoolean()) {
			// TODO add DB entry for this

			cmd = new ArrayList<Byte>();

			processPTZInfo(Command.PAN, pan);
			processPTZInfo(Command.TILT, tilt);
			processPTZInfo(Command.ZOOM, zoom);

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


			byte[] carr = new byte[cmd.size()];
			int i = 0;
			for(byte a : cmd) { carr[i] = a; i++; }

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
			throws IOException
		{
			//byte[] carr = new byte[]{ 'P', 'S', 'T', 'S', 'Z', 'S' };
			writePayload(os, c.getDrop(), carr);
		}
	}

	/**
	 * processes the params and adds them as byte commands on the command list
	 * @param c Command type [enum]
	 * @param vF float speed value
	 */
	private void processPTZInfo(Command c, Float vF) {

		float v = (vF == null) ? 0f : vF;
		int a = NumericAlphaComparator.compareFloats(v, 0f,
			CohuPTZProperty.PTZ_THRESH);

		boolean stopping = (a == 0);
		boolean posDir = (a > 0);

		byte b;

		// figure out which command to send
		switch(c) {
		case PAN:
			if(fixspd || stopping)
				b = cpF;
			else
				b = posDir ? cpP : cpN;
			cmd.add(b);
			break;
		case TILT:
			if(fixspd || stopping)
				b = ctF;
			else
				b = posDir ? ctP : ctN;
			cmd.add(b);
			break;
		case ZOOM:
			b = (fixspd || stopping) ? czF : cz;
			cmd.add(b);
			break;
		}

		// if stopping, just add a stop and exit
		if(stopping) {
			cmd.add(aS);
			return;
		}

		// first argument (variable zoom takes another)
		switch(c) {
		case PAN:
			if(fixspd)
				b = posDir ? apP : apN;
			else
				b = CohuPTZProperty.getPanTiltSpeedByte(v);
			cmd.add(b);
			break;
		case TILT:
			if(fixspd)
				b = posDir ? atP : atN;
			else
				b = CohuPTZProperty.getPanTiltSpeedByte(v);
			cmd.add(b);
			break;
		case ZOOM:
			if(fixspd)
				b = posDir ? azP : azN;
			else
				b = posDir ? azPv : azNv;
			cmd.add(b);
			break;
		}

		// add the variable zoom speed
		if(!fixspd && c.equals(Command.ZOOM))
			cmd.add(CohuPTZProperty.getZoomSpeedByte(v));
	}
}
