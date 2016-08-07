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

	/**
	 * Debugging only.
	 * Utilize fixed speed commands?  Should be false for production.
	 */
	protected static boolean use_fixed_speed = false;

	/**
	 * Debugging only.
	 * Use combined commands
	 * true --  PTZ commands all going in same packet and queue item
	 * false -- PTZ commands each going in separate packet and queue
	 *          item. this is the original MN behavior
	 */
	protected static final boolean use_combined_commands = true;

	/** to evaluate whether to include zoom in full property/phase */
	private final boolean stop_zoom;

	/** so queued operations aren't dropped for being supposed duplicates */
	private final long created;

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

		created = System.nanoTime();
		pan  = p;
		tilt = t;
		zoom = z;

		stop_zoom = use_fixed_speed ||
			NumericAlphaComparator.compareFloats(z, 0F,
				CohuPTZProperty.PTZ_THRESH) == 0F;

		log(String.format("PTZ command: P:%s  T:%s  Z:%s",
			p == null ? "null" : p.toString(),
			t == null ? "null" : t.toString(),
			z == null ? "null" : z.toString()));
	}

	/** Begin the operation. */
	@Override
	protected Phase<CohuPTZProperty> phaseTwo() {

		if(use_combined_commands)
			return new PTZFullPhase();
		return new PanPhase();
	}

	/**
	 * ptz full command phase
	 */
	protected class PTZFullPhase extends Phase<CohuPTZProperty> {

		protected Phase<CohuPTZProperty> poll(
			CommMessage<CohuPTZProperty> mess)
			throws IOException, DeviceContentionException {

			if(pan != null || tilt != null) {
				log("sending full ptz");
				mess.add(new PTZFullProperty());
				doStoreProps(mess);
				updateOpStatus("pan/tilt sent");
				log("pan/tilt sent");
			}

			// zoom has to be handled separate w/ variable speed
			if(!stop_zoom)
				return new ZoomPhase();

			return null;
		}

		@Override
		public String toString() {
			return "OpPTZCamera$PTZFullPhase: p=" + pan + " t=" + tilt;
		}
	}

	/** pan phase, 1/3 */
	protected class PanPhase extends Phase<CohuPTZProperty> {

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

		@Override
		public String toString() {
			return getClass().getSimpleName() + ": z=" + zoom;
		}
	}

	/** PTZ full property, send this exact command */
	protected class PTZFullProperty extends CohuPTZProperty {

		public PTZFullProperty() { }

		/** Encode a STORE request */
		@Override
		public void encodeStore(ControllerImpl c, OutputStream os)
			throws IOException {

			byte[] cmd = new byte[] {};

			cmd = processPTZInfo(Command.PAN, pan, cmd);
			cmd = processPTZInfo(Command.TILT, tilt, cmd);

			// zoom can't be added with variable speeds
			// but it can be added so long as it is a stop command
			// see PTZFullPhase
			if(stop_zoom)
				cmd = processPTZInfo(Command.ZOOM, zoom, cmd);

			writePayload(os, c.getDrop(), cmd);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		OpPTZCamera that = (OpPTZCamera) o;

		if (created != that.created) return false;
		if (pan != null ? !pan.equals(that.pan) : that.pan != null)
			return false;
		if (tilt != null ? !tilt.equals(that.tilt) : that.tilt != null)
			return false;
		return zoom != null ? zoom.equals(that.zoom) :
			that.zoom == null;

	}

	@Override
	public int hashCode() {
		int result = pan != null ? pan.hashCode() : 0;
		result = 31 * result + (tilt != null ? tilt.hashCode() : 0);
		result = 31 * result + (zoom != null ? zoom.hashCode() : 0);
		result = 31 * result + (int) (created ^ (created >>> 32));
		return result;
	}

	public String toString2() {
		return getClass().getSimpleName() + ": p=" + pan + " t=" + tilt
			+ " z=" + zoom;
	}

}
