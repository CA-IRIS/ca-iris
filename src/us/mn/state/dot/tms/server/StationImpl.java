/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2004-2013  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.NavigableMap;
import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.R_Node;
import us.mn.state.dot.tms.Station;
import us.mn.state.dot.tms.SystemAttrEnum;
import static us.mn.state.dot.tms.server.Constants.MISSING_DATA;
import static us.mn.state.dot.tms.server.XmlWriter.createAttribute;

/**
 * A station is a group of related detectors.
 *
 * @author Douglas Lau
 */
public class StationImpl implements Station {

	/** Bottleneck debug log */
	static private final DebugLog BOTTLENECK_LOG =
		new DebugLog("bottleneck");

	/** Density ranks for calculating rolling sample count */
	static protected enum DensityRank {
		First(55, 6),	// 55+ vpm => 6 samples (3 minutes)
		Second(40, 4),	// 40-55 vpm => 4 samples (2 minutes)
		Third(25, 3),	// 25-40 vpm => 3 samples (1.5 minutes)
		Fourth(15, 4),	// 15-25 vpm => 4 samples (2 minutes)
		Fifth(10, 6),	// 10-15 vpm => 6 samples (3 minutes)
		Last(0, 0);	// less than 10 vpm => 0 samples
		protected final int density;
		protected final int samples;
		private DensityRank(int k, int n_smp) {
			density = k;
			samples = n_smp;
		}
		/** Get the number of rolling samples for the given density */
		static protected int samples(float k) {
			for(DensityRank dr: values()) {
				if(k > dr.density)
					return dr.samples;
			}
			return Last.samples;
		}
		/** Get the maximum number of samples in any density rank */
		static protected int getMaxSamples() {
			int s = 0;
			for(DensityRank dr: values())
				s = Math.max(s, dr.samples);
			return s;
		}
	}

	/** Speed ranks for extending rolling sample averaging */
	static protected enum SpeedRank {
		First(40, 2),	// 40+ mph => 2 samples (1 minute)
		Second(25, 4),	// 25-40 mph => 4 samples (2 minutes)
		Third(20, 6),	// 20-25 mph => 6 samples (3 minutes)
		Fourth(15, 8),	// 15-20 mph => 8 samples (4 minutes)
		Last(0, 10);	// 0-15 mph => 10 samples (5 minutes)
		protected final int speed;
		protected final int samples;
		private SpeedRank(int spd, int n_smp) {
			speed = spd;
			samples = n_smp;
		}
		/** Get the number of rolling samples for the given speed */
		static protected int samples(float s) {
			for(SpeedRank sr: values()) {
				if(s > sr.speed)
					return sr.samples;
			}
			return Last.samples;
		}
		/** Get the number of rolling samples for a set of speeds */
		static protected int samples(float[] speeds) {
			int n_smp = First.samples;
			// NOTE: n_smp might be changed inside loop, extending
			//       the for loop bounds
			for(int i = 0; i < n_smp; i++) {
				float s = speeds[i];
				if(s > 0)
					n_smp = Math.max(n_smp, samples(s));
			}
			return n_smp;
		}
	}

	/** Calculate the rolling average speed */
	static protected float averageSpeed(float[] speeds) {
		return average(speeds, SpeedRank.samples(speeds));
	}

	/** Calculate the rolling average of some samples.
	 * @param samples Array of samples to average.
	 * @param n_smp Number of samples to average.
	 * @return Average of samples, or MISSING_DATA. */
	static protected float average(float[] samples, int n_smp) {
		float total = 0;
		int count = 0;
		for(int i = 0; i < n_smp; i++) {
			float s = samples[i];
			if(s > 0) {
				total += s;
				count += 1;
			}
		}
		return average(total, count);
	}

	/** Calculate the average from a total and sample count.
	 * @param total Total of all sample data.
	 * @param count Count of samples.
	 * @return Average of samples, or MISSING_DATA. */
	static protected float average(float total, int count) {
		if(count > 0)
			return total / count;
		else
			return MISSING_DATA;
	}

	/** Staiton name */
	protected final String name;

	/** Get the station name */
	public String getName() {
		return name;
	}

	/** Get the station index */
	public String getIndex() {
		if(name.startsWith("S"))
			return name.substring(1);
		else
			return name;
	}

	/** Roadway node */
	protected final R_NodeImpl r_node;

	/** Get the roadway node */
	public R_Node getR_Node() {
		return r_node;
	}

        /** Create a new station */
	public StationImpl(String station_id, R_NodeImpl n) {
		name = station_id;
		r_node = n;
		for(int i = 0; i < rlg_speed.length; i++)
			rlg_speed[i] = MISSING_DATA;
		for(int i = 0; i < avg_speed.length; i++)
			avg_speed[i] = MISSING_DATA;
		for(int i = 0; i < low_speed.length; i++)
			low_speed[i] = MISSING_DATA;
	}

	/** Get the SONAR type name */
	public String getTypeName() {
		return SONAR_TYPE;
	}

	/** Destroy a station */
	public void destroy() {
		// Nothing to do
	}

	/** Get a string representation of the station */
	public String toString() {
		return name;
	}

	/** Does this node have the specified detector? */
	public boolean hasDetector(DetectorImpl det) {
		return r_node.hasDetector(det);
	}

	/** Is this station active? */
	public boolean getActive() {
		for(DetectorImpl det: r_node.getDetectors()) {
			if(!det.getAbandoned())
				return true;
		}
		return false;
	}

	/** Current average station volume */
	protected float volume = MISSING_DATA;

	/** Current average station occupancy */
	protected float occupancy = MISSING_DATA;

	/** Current average station flow */
	protected int flow = MISSING_DATA;

	/** Get the average station flow */
	public int getFlow() {
		return flow;
	}

	/** Current average station density */
	private float density = MISSING_DATA;

	/** Get the average station density */
	public float getDensity() {
		return density;
	}

	/** Current average station speed */
	private float speed = MISSING_DATA;

	/** Get the average station speed */
	public float getSpeed() {
		return speed;
	}

	/** Get the station speed limit */
	public int getSpeedLimit() {
		return r_node.getSpeedLimit();
	}

	/** Averate station speed for rolling speed calculation */
	protected float[] rlg_speed = new float[DensityRank.getMaxSamples()];

	/** Update rolling speed array with a new sample */
	protected void updateRollingSpeed(float s) {
		System.arraycopy(rlg_speed, 0, rlg_speed, 1,
			rlg_speed.length - 1);
		// Clamp the speed to 10 mph above the speed limit
		rlg_speed[0] = Math.min(s, getSpeedLimit() + 10);
	}

	/** Average station speed for previous ten samples */
	protected float[] avg_speed = new float[SpeedRank.Last.samples];

	/** Update average station speed with a new sample */
	protected void updateAvgSpeed(float s) {
		System.arraycopy(avg_speed, 0, avg_speed, 1,
			avg_speed.length - 1);
		avg_speed[0] = Math.min(s, getSpeedLimit());
	}

	/** Get the average speed smoothed over several samples */
	public float getSmoothedAverageSpeed() {
		return averageSpeed(avg_speed);
	}

	/** Get the average speed using a rolling average of samples */
	public float getRollingAverageSpeed() {
		if(isSpeedValid()) {
			int n_samples = calculateRollingSamples();
			if(n_samples > 0)
				return average(rlg_speed, n_samples);
			else
				return getSpeedLimit();
		} else
			return MISSING_DATA;
	}

	/** Samples used in previous time step */
	protected int rolling_samples = 0;

	/** Update the rolling samples for previous time step */
	protected void updateRollingSamples() {
		rolling_samples = calculateRollingSamples();
	}

	/** Calculate the number of samples for rolling average */
	protected int calculateRollingSamples() {
		return Math.min(calculateMaxSamples(), rolling_samples + 1);
	}

	/** Calculate the maximum number of samples for rolling average */
	protected int calculateMaxSamples() {
		if(isSpeedTrending())
			return 2;
		else
			return DensityRank.samples(getDensity());
	}

	/** Is the speed trending over the last few time steps? */
	protected boolean isSpeedTrending() {
		return isSpeedValid() &&
		      (isSpeedTrendingDownward() || isSpeedTrendingUpward());
	}

	/** Is recent rolling speed data valid? */
	protected boolean isSpeedValid() {
		return rlg_speed[0] > 0 && rlg_speed[1] > 0 && rlg_speed[2] > 0;
	}

	/** Is the speed trending downward? */
	protected boolean isSpeedTrendingDownward() {
		return rlg_speed[0] < rlg_speed[1] &&
		       rlg_speed[1] < rlg_speed[2];
	}

	/** Is the speed trending upward? */
	protected boolean isSpeedTrendingUpward() {
		return rlg_speed[0] > rlg_speed[1] &&
		       rlg_speed[1] > rlg_speed[2];
	}

	/** Low station speed for previous ten samples */
	protected float[] low_speed = new float[SpeedRank.Last.samples];

	/** Update low station speed with a new sample */
	protected void updateLowSpeed(float s) {
		System.arraycopy(low_speed, 0, low_speed, 1,
			low_speed.length - 1);
		low_speed[0] = Math.min(s, getSpeedLimit());
	}

	/** Get the low speed smoothed over several samples */
	public float getSmoothedLowSpeed() {
		return averageSpeed(low_speed);
	}

	/** Calculate the current station data */
	public void calculateData() {
		updateRollingSamples();
		float low = MISSING_DATA;
		float t_volume = 0;
		int n_volume = 0;
		float t_occ = 0;
		int n_occ = 0;
		float t_flow = 0;
		int n_flow = 0;
		float t_density = 0;
		int n_density = 0;
		float t_speed = 0;
		int n_speed = 0;
		for(DetectorImpl det: r_node.getDetectors()) {
			if(det.getAbandoned() || !det.isStationOrCD() ||
			   !det.isSampling())
				continue;
			float f = det.getVolume();
			if(f != MISSING_DATA) {
				t_volume += f;
				n_volume++;
			}
			f = det.getOccupancy();
			if(f != MISSING_DATA) {
				t_occ += f;
				n_occ++;
			}
			f = det.getFlow();
			if(f != MISSING_DATA) {
				t_flow += f;
				n_flow++;
			}
			f = det.getDensity();
			if(f != MISSING_DATA) {
				t_density += f;
				n_density++;
			}
			f = det.getSpeed();
			if(f > 0) {
				t_speed += f;
				n_speed++;
				if(low == MISSING_DATA)
					low = f;
				else
					low = Math.min(f, low);
			}
		}
		volume = average(t_volume, n_volume);
		occupancy = average(t_occ, n_occ);
		flow = Math.round(average(t_flow, n_flow));
		density = average(t_density, n_density);
		speed = average(t_speed, n_speed);
		updateRollingSpeed(speed);
		updateAvgSpeed(speed);
		updateLowSpeed(low);
	}

	/** Write the current sample as an XML element */
	public void writeSampleXml(Writer w) throws IOException {
		if(!getActive())
			return;
		int f = getFlow();
		int s = Math.round(getSpeed());
		float o = occupancy;
		w.write("\t<sample");
		w.write(createAttribute("sensor", name));
		if(f > MISSING_DATA)
			w.write(createAttribute("flow", f));
		if(s > 0)
			w.write(createAttribute("speed", s));
		if(o >= 0) {
			w.write(createAttribute("occ",
				BaseObjectImpl.formatFloat(o, 2)));
		}
		w.write("/>\n");
	}

	/** Write the current sample as an XML element.  This is used for the
	 * old station.xml file, which should be removed at some point.  */
	public void writeStationXmlElement(Writer w) throws IOException {
		if(!getActive())
			return;
		String n = getIndex();
		if(n.length() < 1)
			return;
		if(volume == MISSING_DATA)
			w.write("\t<station id='" + n + "' status='fail'/>\n");
		else {
			w.write("\t<station id='" + n + "' status='ok'>\n");
			w.write("\t\t<volume>" + volume + "</volume>\n");
			w.write("\t\t<occupancy>" + occupancy +
				"</occupancy>\n");
			w.write("\t\t<flow>" + flow + "</flow>\n");
			w.write("\t\t<speed>" + speed + "</speed>\n");
			w.write("\t</station>\n");
		}
	}

	/** Acceleration from previous station */
	protected Float acceleration = null;

	/** Count of iterations where station was a bottleneck */
	protected int n_bottleneck = 0;

	/** Bottleneck exists flag */
	protected boolean bottleneck = false;

	/** Bottleneck flag from previous time step.  This is needed when
	 * checking if a bottleneck should be adjusted downstream. */
	protected boolean p_bottle = false;

	/** Set the bottleneck flag */
	protected void setBottleneck(boolean b) {
		p_bottle = bottleneck;
		bottleneck = b;
	}

	/** Calculate whether the station is a bottleneck.
	 * @param m Mile point of this station.
	 * @param upstream Mapping of mile points to upstream stations. */
	public void calculateBottleneck(float m,
		NavigableMap<Float, StationImpl> upstream)
	{
		Float mp = upstream.lowerKey(m);
		while(mp != null && isTooClose(m - mp))
			mp = upstream.lowerKey(mp);
		if(mp != null) {
			StationImpl sp = upstream.get(mp);
			float d = m - mp;
			acceleration = calculateAcceleration(sp, d);
			checkThresholds();
			if(isAboveBottleneckSpeed())
				setBottleneck(false);
			else if(isBeforeStartCount()) {
				setBottleneck(false);
				adjustDownstream(upstream);
			} else {
				setBottleneck(true);
				adjustUpstream(upstream);
			}
		} else
			clearBottleneck();
	}

	/** Test if upstream station is too close for bottleneck calculation */
	protected boolean isTooClose(float d) {
		return d < SystemAttrEnum.VSA_MIN_STATION_MILES.getFloat();
	}

	/** Calculate the acceleration from previous station.
	 * @param sp Previous station.
	 * @param d Distance to previous station (miles).
	 * @return acceleration in mphph */
	protected Float calculateAcceleration(StationImpl sp, float d) {
		float u = getRollingAverageSpeed();
		float up = sp.getRollingAverageSpeed();
		return calculateAcceleration(u, up, d);
	}

	/** Calculate the acceleration between two stations.
	 * @param u Downstream speed (mph).
	 * @param up Upstream speed (mph).
	 * @param d Distance between stations (miles).
	 * @return acceleration in mphph */
	protected Float calculateAcceleration(float u, float up, float d) {
		assert d > 0;
		if(u > 0 && up > 0)
			return (u * u - up * up) / (2 * d);
		else
			return null;
	}

	/** Check the bottleneck thresholds */
	protected void checkThresholds() {
		if(isAccelerationValid() && acceleration < getThreshold())
			n_bottleneck++;
		else
			n_bottleneck = 0;
	}

	/** Check if acceleration is valid */
	protected boolean isAccelerationValid() {
		return acceleration != null;
	}

	/** Get the current deceleration threshold */
	protected int getThreshold() {
		if(isBeforeStartCount())
			return getStartThreshold();
		else
			return getStopThreshold();
	}

	/** Get the starting deceleration threshold */
	protected int getStartThreshold() {
		return SystemAttrEnum.VSA_START_THRESHOLD.getInt();
	}

	/** Get the stopping deceleration threshold */
	protected int getStopThreshold() {
		return SystemAttrEnum.VSA_STOP_THRESHOLD.getInt();
	}

	/** Test if the number of intervals is lower than start count */
	protected boolean isBeforeStartCount() {
		return n_bottleneck <
			SystemAttrEnum.VSA_START_INTERVALS.getInt();
	}

	/** Test if station speed is above the bottleneck id speed */
	protected boolean isAboveBottleneckSpeed() {
		return getRollingAverageSpeed() >
			SystemAttrEnum.VSA_BOTTLENECK_ID_MPH.getInt();
	}

	/** Adjust the bottleneck downstream if necessary */
	protected void adjustDownstream(
		NavigableMap<Float, StationImpl> upstream)
	{
		Map.Entry<Float, StationImpl> entry = upstream.lastEntry();
		if(entry != null)
			adjustDownstream(entry.getValue());
	}

	/** Adjust the bottleneck downstream if necessary.
	 * @param sp Immediately upstream station */
	protected void adjustDownstream(StationImpl sp) {
		Float ap = sp.acceleration;
		Float a = acceleration;
		if(a != null && ap != null && a < ap && sp.p_bottle) {
			// Move bottleneck downstream
			// Don't use setBottleneck, because we don't want
			// p_bottle to be updated
			sp.bottleneck = false;
			bottleneck = true;
			// Bump the bottleneck count so it won't just
			// shut off at the next time step
			while(isBeforeStartCount())
				n_bottleneck++;
		}
	}

	/** Adjust the bottleneck upstream if necessary */
	protected void adjustUpstream(
		NavigableMap<Float, StationImpl> upstream)
	{
		StationImpl s = this;
		Map.Entry<Float, StationImpl> entry = upstream.lastEntry();
		while(entry != null) {
			StationImpl sp = entry.getValue();
			Float ap = sp.acceleration;
			Float a = s.acceleration;
			if(a != null && ap != null && a > ap) {
				// Move bottleneck upstream
				// Don't use setBottleneck, because we don't
				// want p_bottle to be updated
				s.bottleneck = false;
				s = sp;
				s.bottleneck = true;
			} else
				break;
			entry = upstream.lowerEntry(entry.getKey());
		}
		// Bump the bottleneck count so it won't just
		// shut off at the next time step
		while(s.isBeforeStartCount())
			s.n_bottleneck++;
	}

	/** Clear the station as a bottleneck */
	public void clearBottleneck() {
		n_bottleneck = 0;
		setBottleneck(false);
		acceleration = null;
	}

	/** Debug the bottleneck calculation */
	public void debug() {
		if(BOTTLENECK_LOG.isOpen()) {
			BOTTLENECK_LOG.log(name +
				", spd: " + getRollingAverageSpeed() +
				", acc: " + acceleration +
				", n_bneck: " + n_bottleneck +
				", bneck: " + bottleneck);
		}
	}

	/** Check if the station is a bottleneck for the given distance */
	public boolean isBottleneckFor(float d) {
		return bottleneck && isBottleneckInRange(d);
	}

	/** Check if the (bottleneck) station is in range */
	protected boolean isBottleneckInRange(float d) {
		if(d > 0)
			return d < getUpstreamDistance();
		else
			return -d < getDownstreamDistance();
	}

	/** Get the upstream bottleneck distance */
	protected float getUpstreamDistance() {
		float lim = getSpeedLimit();
		float sp = getRollingAverageSpeed();
		if(sp > 0 && sp < lim) {
			int acc = -getControlThreshold();
			return (lim * lim - sp * sp) / (2 * acc);
		} else
			return 0;
	}

	/** Get the control deceleration threshold */
	protected int getControlThreshold() {
		return SystemAttrEnum.VSA_CONTROL_THRESHOLD.getInt();
	}

	/** Get the downstream bottleneck distance */
	protected float getDownstreamDistance() {
		return SystemAttrEnum.VSA_DOWNSTREAM_MILES.getFloat();
	}
}
