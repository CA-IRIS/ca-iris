/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2014  Minnesota Department of Transportation
 * Copyright (C) 2011  Berkeley Transportation Systems Inc.
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
package us.mn.state.dot.tms.server;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import us.mn.state.dot.geokit.Position;
import us.mn.state.dot.sched.TimeSteward;
import us.mn.state.dot.sonar.SonarException;
import us.mn.state.dot.tms.Cabinet;
import us.mn.state.dot.tms.ChangeVetoException;
import us.mn.state.dot.tms.CommLink;
import us.mn.state.dot.tms.CommProtocol;
import us.mn.state.dot.tms.Controller;
import us.mn.state.dot.tms.ControllerHelper;
import us.mn.state.dot.tms.ControllerIO;
import us.mn.state.dot.tms.CtrlCondition;
import static us.mn.state.dot.tms.DeviceRequest.QUERY_MESSAGE;
import static us.mn.state.dot.tms.DeviceRequest.QUERY_STATUS;
import us.mn.state.dot.tms.EventType;
import us.mn.state.dot.tms.TMSException;
import us.mn.state.dot.tms.VehLengthClass;
import static us.mn.state.dot.tms.server.Constants.MISSING_DATA;
import static us.mn.state.dot.tms.server.XmlWriter.createAttribute;
import us.mn.state.dot.tms.server.comm.DevicePoller;
import us.mn.state.dot.tms.server.comm.SamplePoller;
import us.mn.state.dot.tms.server.comm.WeatherPoller;
import us.mn.state.dot.tms.server.event.CommEvent;

/**
 * A controller represents a field device controller.
 *
 * @author Douglas Lau
 * @author Michael Darter
 * @author Travis Swanston
 * @author Jacob Barde
 */
public class ControllerImpl extends BaseObjectImpl implements Controller {

	/** Time stamp of most recent data store */
	private transient long last_store_time =
		TimeSteward.currentTimeMillis();

	/** Get last store time */
	public long getLastStoreTime() {
		return last_store_time;
	}

	/** Get comm link impl */
	static private CommLinkImpl commLinkImpl(CommLink cl) {
		return (cl instanceof CommLinkImpl) ? (CommLinkImpl)cl : null;
	}

	/** Get the device poller of a comm link */
	static private DevicePoller getPoller(CommLinkImpl cl) {
		return (cl != null) ? cl.getPoller() : null;
	}

	/** Load all the controllers */
	static protected void loadAll() throws TMSException {
		namespace.registerType(SONAR_TYPE, ControllerImpl.class);
		store.query("SELECT name, cabinet, comm_link, drop_id, " +
			"condition, password, notes, fail_time FROM iris." +
			SONAR_TYPE  +";", new ResultFactory()
		{
			public void create(ResultSet row) throws Exception {
				namespace.addObject(new ControllerImpl(
					row.getString(1),	// name
					row.getString(2),	// cabinet
					row.getString(3),	// comm_link
					row.getShort(4),	// drop_id
					row.getInt(5),		// condition
					row.getString(6),	// password
					row.getString(7),	// notes
					row.getTimestamp(8)	// failTime
				));
			}
		});
	}

	/** Get a mapping of the columns */
	@Override
	public Map<String, Object> getColumns() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("cabinet", cabinet);
		map.put("comm_link", comm_link);
		map.put("drop_id", drop_id);
		map.put("condition", condition.ordinal());
		map.put("password", password);
		map.put("notes", notes);
		map.put("fail_time", asTimestamp(failTime));
		return map;
	}

	/** Get the database table name */
	public String getTable() {
		return "iris." + SONAR_TYPE;
	}

	/** Get the SONAR type name */
	public String getTypeName() {
		return SONAR_TYPE;
	}

	/** Create a new controller */
	public ControllerImpl(String n) throws TMSException, SonarException {
		super(n);
		CabinetImpl c = new CabinetImpl(n);
		c.notifyCreate();
		cabinet = c;
		condition = CtrlCondition.PLANNED;
	}

	/** Create a new controller */
	protected ControllerImpl(String n, CabinetImpl c, CommLink cl, short d,
		int cnd, String p, String nt, Date ft) throws TMSException
	{
		super(n);
		cabinet = c;
		comm_link = commLinkImpl(cl);
		drop_id = d;
		condition = CtrlCondition.fromOrdinal(cnd);
		password = p;
		notes = nt;
		failTime = stampMillis(ft);
		initTransients();
	}

	/** Create a new controller */
	protected ControllerImpl(String n, String c, String cl, short d,
		int cnd, String p, String nt, Date ft) throws TMSException
	{
		this(n, lookupCabinet(c), lookupCommLink(cl), d, cnd, p, nt,ft);
	}

	/** Initialize the transient fields */
	@Override
	protected void initTransients() throws TMSException {
		version = "";
		CommLinkImpl cl = comm_link;
		if(cl != null)
			cl.putController(drop_id, this);
	}

	/** Get controller label */
	public String getLabel() {
		CommLinkImpl cl = comm_link;
		StringBuilder b = new StringBuilder();
		b.append("Link ");
		if(cl != null)
			b.append(cl.getName());
		else
			b.append("null");
		b.append(" drop ");
		b.append(drop_id);
		return b.toString();
	}

	/** Controller cabinet */
	protected CabinetImpl cabinet;

	/** Set the controller cabinet */
	public void setCabinet(Cabinet c) {
		if(c instanceof CabinetImpl)
			cabinet = (CabinetImpl)c;
	}

	/** Set the controller cabinet */
	public void doSetCabinet(Cabinet c) throws TMSException {
		if(!(c instanceof CabinetImpl))
			return;
		if(c == cabinet)
			return;
		store.update(this, "cabinet", c);
		setCabinet(c);
	}

	/** Get the controller cabinet */
	public Cabinet getCabinet() {
		return cabinet;
	}

	/** Put this controller into a comm link */
	private void putCommLink(int d, CommLinkImpl cl) throws TMSException {
		if(cl != null) {
			cl.testGateArmDisable("comm_link 0");
			cl.putController(d, this);
		}
	}

	/** Pull this controller from a comm link */
	private void pullCommLink(CommLinkImpl cl) {
		if(cl != null) {
			cl.testGateArmDisable("comm_link 1");
			cl.pullController(this);
		}
	}

	/** Comm link */
	private CommLinkImpl comm_link;

	/** Set the comm link for this controller */
	public void setCommLink(CommLink c) {
		comm_link = commLinkImpl(c);
	}

	/** Set the comm link for this controller */
	public void doSetCommLink(CommLink c) throws TMSException {
		CommLinkImpl cl = commLinkImpl(c);
		if(cl != comm_link) {
			putCommLink(drop_id, cl);
			store.update(this, "comm_link", cl);
			pullCommLink(comm_link);
			setCommLink(cl);
		}
	}

	/** Get the comm link */
	public CommLink getCommLink() {
		return comm_link;
	}

	/** Drop address */
	protected short drop_id;

	/** Set the drop address */
	public void setDrop(short d) {
		drop_id = d;
	}

	/** Set the drop address */
	public void doSetDrop(short d) throws TMSException {
		if(d == drop_id)
			return;
		putCommLink(d, comm_link);
		store.update(this, "drop_id", d);
		pullCommLink(comm_link);
		setDrop(d);
	}

	/** Get the drop address */
	public short getDrop() {
		return drop_id;
	}

	/** Test whether gate arm system should be disabled */
	private void testGateArmDisable(String reason) {
		CommLinkImpl cl = comm_link;
		if(cl != null)
			cl.testGateArmDisable(reason);
	}

	/** Controller condition */
	private CtrlCondition condition;

	/** Set the condition */
	@Override
	public void setCondition(int cnd) {
		testGateArmDisable("condition");
		condition = CtrlCondition.fromOrdinal(cnd);
		updateStyles();
	}

	/** Set the condition */
	public void doSetCondition(int cnd) throws TMSException {
		if (cnd != condition.ordinal()) {
			store.update(this, "condition", cnd);
			setCondition(cnd);
		}
	}

	/** Get the condition */
	@Override
	public int getCondition() {
		return condition.ordinal();
	}

	/** Check if condition is active */
	public boolean isActive() {
		return condition == CtrlCondition.ACTIVE;
	}

	/** Access password */
	protected String password;

	/** Set the access password */
	public void setPassword(String pwd) {
		testGateArmDisable("password");
		password = pwd;
	}

	/** Set the access password */
	public void doSetPassword(String pwd) throws TMSException {
		if(stringEquals(pwd, password))
			return;
		store.update(this, "password", pwd);
		setPassword(pwd);
	}

	/** Get the access password */
	public String getPassword() {
		return password;
	}

	/** Administrator notes for this controller */
	protected String notes = "";

	/** Set the administrator notes */
	public void setNotes(String n) {
		notes = n;
	}

	/** Set the administrator notes */
	public void doSetNotes(String n) throws TMSException {
		if(n.equals(notes))
			return;
		store.update(this, "notes", n);
		setNotes(n);
	}

	/** Get the administrator notes */
	public String getNotes() {
		return notes;
	}

	/** Update styles for associated devices */
	public synchronized void updateStyles() {
		for(ControllerIO io: io_pins.values()) {
			if(io instanceof DeviceImpl) {
				DeviceImpl dev = (DeviceImpl)io;
				dev.updateStyles();
			}
		}
	}

	/** Mapping of all controller I/O pins */
	protected transient HashMap<Integer, ControllerIO> io_pins =
		new HashMap<Integer, ControllerIO>();

	/** Get controller I/O for one pin */
	public synchronized ControllerIO getIO(int pin) {
		return io_pins.get(pin);
	}

	/** Assign an IO to the specified controller I/O pin */
	public synchronized void setIO(int pin, ControllerIO io) {
		if(io != null)
			io_pins.put(pin, io);
		else
			io_pins.remove(pin);
	}

	/** Determine whether this controller has an active ramp meter */
	public synchronized boolean hasActiveMeter() {
		if (isActive()) {
			for (ControllerIO io: io_pins.values()) {
				if (io instanceof RampMeterImpl)
					return true;
			}
		}
		return false;
	}

	/** Get an active beacon for the controller */
	public synchronized BeaconImpl getActiveBeacon() {
		if (isActive()) {
			for (ControllerIO io: io_pins.values()) {
				if (io instanceof BeaconImpl)
					return (BeaconImpl)io;
			}
		}
		return null;
	}

	/** Get a map of pins to detectors */
	protected synchronized HashMap<Integer, DetectorImpl> getDetectors() {
		HashMap<Integer, DetectorImpl> dets =
			new HashMap<Integer, DetectorImpl>();
		for(Integer pin: io_pins.keySet()) {
			ControllerIO io = io_pins.get(pin);
			if(io instanceof DetectorImpl)
				dets.put(pin, (DetectorImpl)io);
		}
		return dets;
	}

	/** Get a detector by its I/O pin number */
	public DetectorImpl getDetectorAtPin(int pin) {
		ControllerIO io = getIO(pin);
		if(io instanceof DetectorImpl)
			return (DetectorImpl)io;
		else
			return null;
	}

	/** Check whether this controller has any active detectors */
	public synchronized boolean hasActiveDetector() {
		if (isActive()) {
			for (ControllerIO io: io_pins.values()) {
				if (io instanceof DetectorImpl)
					return true;
			}
		}
		return false;
	}

	/** Find a matching detector for the specified input */
	public int getSpeedPair(int pin) {
		DetectorImpl d = getDetectorAtPin(pin);
		if(d != null && d.isVelocity())
			return getSpeedPair(d);
		return 0;
	}

	/** Find a matching detector for the specified velocity detector */
	protected synchronized int getSpeedPair(DetectorImpl v) {
		for(Map.Entry<Integer, ControllerIO> e: io_pins.entrySet()) {
			if(v.isSpeedPair(e.getValue()))
				return e.getKey();
		}
		return 0;
	}

	/** Get an alarm from the controller */
	public AlarmImpl getAlarm(int pin) {
		ControllerIO io = getIO(pin);
		if(io instanceof AlarmImpl)
			return (AlarmImpl)io;
		else
			return null;
	}

	/** Check if the controller is a message feed controller */
	public boolean isMsgFeed() {
		return getProtocol() == CommProtocol.MSG_FEED;
	}

	/** Get the comm protocol */
	public CommProtocol getProtocol() {
		CommLinkImpl cl = comm_link;
		return (cl != null)
		      ? CommProtocol.fromOrdinal(cl.getProtocol())
		      : null;
	}

	/** Get a sample value from an array */
	static private int sampleValue(int[] values, int i) {
		if(values != null && i >= 0 && i < values.length)
			return values[i];
		else
			return MISSING_DATA;
	}

	/** Store volume sample data.
	 * @param stamp Timestamp in milliseconds since epoch.
	 * @param period Sampling period in seconds.
	 * @param start_pin Start pin on controller I/O.
	 * @param volume Array of volume samples. */
	public void storeVolume(long stamp, int period, int start_pin,
		int[] volume)
	{
		storeVolume(stamp, period, start_pin, volume, null);
	}

	/** Store volume sample data.
	 * @param stamp Timestamp in milliseconds since epoch.
	 * @param period Sampling period in seconds.
	 * @param start_pin Start pin on controller I/O.
	 * @param volume Array of volume samples.
	 * @param vc Vehicle class. */
	public void storeVolume(long stamp, int period, int start_pin,
		int[] volume, VehLengthClass vc)
	{
		boolean stored = false;
		HashMap<Integer, DetectorImpl> dets = getDetectors();
		for(Integer pin: dets.keySet()) {
			DetectorImpl det = dets.get(pin);
			int i = pin - start_pin;
			int v = sampleValue(volume, i);
			if(v >= 0) {
				det.storeVolume(new PeriodicSample(stamp,
					period, v), vc);
				stored = true;
			}
		}
		if (stored)
			last_store_time = TimeSteward.currentTimeMillis();
	}

	/** Store occupancy sample data.
	 * @param stamp Timestamp in milliseconds since epoch.
	 * @param period Sampling period in seconds.
	 * @param start_pin Start pin on controller I/O.
	 * @param scans Array of scan samples (0 to max_scans).
	 * @param max_scans Maximum scan value (representing 100% occupancy). */
	public void storeOccupancy(long stamp, int period, int start_pin,
		int[] scans, int max_scans)
	{
		boolean stored = false;
		HashMap<Integer, DetectorImpl> dets = getDetectors();
		for(Integer pin: dets.keySet()) {
			DetectorImpl det = dets.get(pin);
			int i = pin - start_pin;
			int n_scans = sampleValue(scans, i);
			if(n_scans >= 0) {
				det.storeOccupancy(new OccupancySample(stamp,
					period, n_scans, max_scans));
				stored = true;
			}
		}
		if (stored)
			last_store_time = TimeSteward.currentTimeMillis();
	}

	/** Store speed sample data.
	 * @param stamp Timestamp in milliseconds since epoch.
	 * @param period Sampling period in seconds.
	 * @param start_pin Start pin on controller I/O.
	 * @param speed Array of speed samples (MPH). */
	public void storeSpeed(long stamp, int period, int start_pin,
		int[] speed)
	{
		boolean stored = false;
		HashMap<Integer, DetectorImpl> dets = getDetectors();
		for(Integer pin: dets.keySet()) {
			DetectorImpl det = dets.get(pin);
			int i = pin - start_pin;
			int s = sampleValue(speed, i);
			if(s > 0) {
				det.storeSpeed(new PeriodicSample(stamp,
					period, s));
				stored = true;
			}
		}
		if (stored)
			last_store_time = TimeSteward.currentTimeMillis();
	}

	/** Bin 30-second sample data */
	public synchronized void binEventSamples() {
		for(ControllerIO io: io_pins.values()) {
			if(io instanceof DetectorImpl)
				((DetectorImpl)io).binEventSamples();
		}
	}

	/** Controller firmware version */
	protected transient String version;

	/** Set the controller firmware version */
	public void setVersion(String v) {
		if(!v.equals(version)) {
			version = v;
			notifyAttribute("version");
		}
	}

	/** Get the controller firmware version */
	public String getVersion() {
		return version;
	}

	/** Controller error status */
	protected transient String errorStatus = "";

	/** Set the controller error status */
	public void setErrorStatus(String s) {
		if(!s.equals(errorStatus)) {
			errorStatus = s;
			notifyAttribute("status");
			updateStyles();
		}
	}

	/** Controller communication status */
	protected transient String commStatus = Constants.UNKNOWN;

	/** Get the controller error status */
	public String getStatus() {
		if(isFailed())
			return commStatus;
		else
			return errorStatus; 
	}

	/** Set the controller communication status */
	protected void setCommStatus(String s) {
		// NOTE: the status attribute is set here, but don't notify
		// clients until communication fails. That happens in the
		// setFailed method.
		commStatus = s;
	}

	/** Log a comm event */
	public void logCommEvent(EventType et, String id, String message) {
		incrementCommCounter(et);
		setCommStatus(message);
		if(!isFailed())
			logCommEvent(et, id);
	}

	/** Time stamp of most recent comm failure */
	protected Long failTime = TimeSteward.currentTimeMillis();

	/** Set the failed status of the controller */
	protected void setFailed(boolean f, String id) {
		if(f == isFailed())
			return;
		if(f) {
			setFailTime(TimeSteward.currentTimeMillis());
			logCommEvent(EventType.COMM_FAILED, id);
		} else {
			setFailTime(null);
			logCommEvent(EventType.COMM_RESTORED, id);
		}
		notifyAttribute("status");
		notifyAttribute("failTime");
		updateStyles();
	}

	/** Set the fail time */
	protected void setFailTime(Long ft) {
		try {
			store.update(this, "fail_time", asTimestamp(ft));
		}
		catch(TMSException e) {
			// FIXME: what else can we do with this exception?
			e.printStackTrace();
		}
		failTime = ft;
	}

	/** FIXME: CA-only temporary fix. */
	public void setFailTimeCA(Long ft) {
		setFailTime(ft);
	}

	/** Set the failed status of the controller */
	public void setFailed(boolean f) {
		setFailed(f, null);
	}

	/** Get the failure status */
	public boolean isFailed() {
		return failTime != null;
	}

	/** Get the controller fail time, or null if communication is not
	 * failed.  This time is in milliseconds since the epoch. */
	public Long getFailTime() {
		return failTime;
	}

	/** Get the number of milliseconds the controller has been failed */
	public long getFailMillis() {
		Long ft = failTime;
		if(ft != null)
			return TimeSteward.currentTimeMillis() - failTime;
		else
			return 0;
	}

	/** Controller maint status */
	private transient String maint = "";

	/** Set the controller maint status */
	public void setMaintNotify(String s) {
		if (!s.equals(maint)) {
			maint = s;
			notifyAttribute("maint");
			updateStyles();
		}
	}

	/** Get the controller maint status */
	@Override
	public String getMaint() {
		return maint;
	}

	/** Timeout error count */
	protected int timeoutErr;

	/** Get the timeout error count */
	public int getTimeoutErr() {
		return timeoutErr;
	}

	/** Increment the timeout error count */
	protected void incrementTimeoutErr() {
		timeoutErr++;
		notifyAttribute("timeoutErr");
	}

	/** Checksum error count */
	protected int checksumErr;

	/** Get the checksum error count */
	public int getChecksumErr() {
		return checksumErr;
	}

	/** Increment the checksum error count */
	protected void incrementChecksumErr() {
		checksumErr++;
		notifyAttribute("checksumErr");
	}

	/** Parsing error count */
	protected int parsingErr;

	/** Get the parsing error count */
	public int getParsingErr() {
		return parsingErr;
	}

	/** Increment the parsing error count */
	protected void incrementParsingErr() {
		parsingErr++;
		notifyAttribute("parsingErr");
	}

	/** Controller error count */
	protected int controllerErr;

	/** Get the controller error count */
	public int getControllerErr() {
		return controllerErr;
	}

	/** Increment the controller error count */
	protected void incrementControllerErr() {
		controllerErr++;
		notifyAttribute("controllerErr");
	}

	/** Increment a comm error counter */
	protected void incrementCommCounter(EventType et) {
		switch(et) {
		case POLL_TIMEOUT_ERROR:
			incrementTimeoutErr();
			break;
		case CHECKSUM_ERROR:
			incrementChecksumErr();
			break;
		case PARSING_ERROR:
			incrementParsingErr();
			break;
		case CONTROLLER_ERROR:
			incrementControllerErr();
			break;
		}
	}

	/** Successful operations count */
	protected int successOps;

	/** Get the successful operation count */
	public int getSuccessOps() {
		return successOps;
	}

	/** Increment the successful operation count */
	protected void incrementSuccessOps() {
		successOps++;
		notifyAttribute("successOps");
	}

	/** Failed operations count */
	protected int failedOps;

	/** Get the failed operation count */
	public int getFailedOps() {
		return failedOps;
	}

	/** Increment the failed operation count */
	protected void incrementFailedOps() {
		failedOps++;
		notifyAttribute("failedOps");
	}

	/** Clear the counters and error status */
	public void setCounters(boolean clear) {
		setMaintNotify("");
		setErrorStatus("");
		if(timeoutErr != 0) {
			timeoutErr = 0;
			notifyAttribute("timeoutErr");
		}
		if(checksumErr != 0) {
			checksumErr = 0;
			notifyAttribute("checksumErr");
		}
		if(parsingErr != 0) {
			parsingErr = 0;
			notifyAttribute("parsingErr");
		}
		if(controllerErr != 0) {
			controllerErr = 0;
			notifyAttribute("controllerErr");
		}
		if(successOps != 0) {
			successOps = 0;
			notifyAttribute("successOps");
		}
		if(failedOps != 0) {
			failedOps = 0;
			notifyAttribute("failedOps");
		}
	}

	/** Log a comm event */
	private void logCommEvent(EventType event, String id) {
		if (CommEvent.getCommEventPurgeDays() <= 0)
			return;
		CommEvent ev = new CommEvent(event, getName(), id);
		try {
			ev.doStore();
		}
		catch(TMSException e) {
			e.printStackTrace();
		};
	}

	/** Complete a controller operation */
	public void completeOperation(String id, boolean success) {
		if(success)
			incrementSuccessOps();
		else
			incrementFailedOps();
		setFailed(!success, id);
	}

	/** Get the device poller */
	public DevicePoller getPoller() {
		if (isActive()) {
			DevicePoller dp = getPoller(comm_link);
			if (dp == null && !isFailed()) {
				setCommStatus("comm_link error");
				setFailed(true, null);
			}
			return dp;
		}
		return null;
	}

	/** Poll controller devices */
	public void pollDevices() {
		// Must call getDevices so we don't hold the lock
		for (ControllerIO io: getDevices())
			pollDevice(io);
	}

	/** Get a list of all devices on controller */
	private synchronized Set<ControllerIO> getDevices() {
		return new HashSet<ControllerIO>(io_pins.values());
	}

	/** Poll one device */
	private void pollDevice(ControllerIO io) {
		if (io instanceof DMSImpl) {
			DMSImpl dms = (DMSImpl)io;
			if (dms.isPeriodicallyQueriable())
				dms.sendDeviceRequest(QUERY_MESSAGE);
		}
		if (io instanceof GateArmImpl) {
			GateArmImpl ga = (GateArmImpl)io;
			ga.sendDeviceRequest(QUERY_STATUS);
		}
		if (io instanceof BeaconImpl) {
			BeaconImpl b = (BeaconImpl)io;
			b.sendDeviceRequest(QUERY_STATUS);
		}
		if (io instanceof WeatherSensorImpl) {
			WeatherSensorImpl ws = (WeatherSensorImpl)io;
			ws.sendDeviceRequest(QUERY_STATUS);
		}
		if (io instanceof AlarmImpl) {
			AlarmImpl a = (AlarmImpl)io;
			a.sendDeviceRequest(QUERY_STATUS);
		}
	}

	/** Perform a controller download (reset) */
	public void setDownload(boolean reset) {
		DevicePoller dp = getPoller();
		if (dp instanceof SamplePoller) {
			SamplePoller sp = (SamplePoller)dp;
			if (reset)
				sp.resetController(this);
			else
				sp.sendSettings(this);
		}
		if (dp instanceof WeatherPoller) {
			WeatherSensorImpl ws = getWeatherSensor();
			if (ws != null) {
				WeatherPoller wp = (WeatherPoller)dp;
				wp.sendSettings(ws);
			}
		}
	}

	/** Get a weather sensor for the controller */
	private synchronized WeatherSensorImpl getWeatherSensor() {
		for (ControllerIO io: io_pins.values()) {
			if (io instanceof WeatherSensorImpl)
				return (WeatherSensorImpl)io;
		}
		return null;
	}

	/** Destroy an object */
	@Override
	public void doDestroy() throws TMSException {
		CommLinkImpl cl = comm_link;
		if(cl != null) {
			cl.testGateArmDisable("destroy");
			cl.pullController(this);
		}
		super.doDestroy();
		cabinet.notifyRemove();
	}

	/** Check if the controller is assigned to a modem comm link */
	public boolean hasModemCommLink() {
		CommLinkImpl cl = comm_link;
		return cl != null ? cl.isModemLink() : false;
	}

	/** Check if the controller comm link is currently connected */
	public boolean isConnected() {
		CommLinkImpl cl = comm_link;
		return cl != null ? cl.isConnected() : false;
	}

	/** Write the controller as an XML element */
	public void writeXml(Writer w) throws IOException {
		w.write("<controller");
		w.write(createAttribute("name", getName()));
		w.write(createAttribute("condition", condition));
		w.write(createAttribute("drop", getDrop()));
		CommLink cl = getCommLink();
		if(cl != null)
			w.write(createAttribute("commlink", cl.getName()));
		Position pos = ControllerHelper.getPosition(this);
		if(pos != null) {
			w.write(createAttribute("lon",
				formatDouble(pos.getLongitude())));
			w.write(createAttribute("lat",
				formatDouble(pos.getLatitude())));
		}
		w.write(createAttribute("location", 
			ControllerHelper.getLocation(this)));
		Cabinet cab = getCabinet();
		if(cab != null && cab.toString().length() > 0)
			w.write(createAttribute("cabinet", getCabinet()));
		if(getNotes().length() > 0)
			w.write(createAttribute("notes", getNotes()));
		w.write("/>\n");
	}
}
