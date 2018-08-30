package us.mn.state.dot.tms.client;

import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.ControllerIO;

import java.util.Iterator;

import static us.mn.state.dot.tms.client.comm.ControllerIOModel.DeviceType;
import static us.mn.state.dot.tms.client.comm.ControllerIOModel.DeviceType.Alarm;
import static us.mn.state.dot.tms.client.comm.ControllerIOModel.DeviceType.Beacon;
import static us.mn.state.dot.tms.client.comm.ControllerIOModel.DeviceType.Camera;
import static us.mn.state.dot.tms.client.comm.ControllerIOModel.DeviceType.DMS;
import static us.mn.state.dot.tms.client.comm.ControllerIOModel.DeviceType.Detector;
import static us.mn.state.dot.tms.client.comm.ControllerIOModel.DeviceType.Gate_Arm;
import static us.mn.state.dot.tms.client.comm.ControllerIOModel.DeviceType.LCSIndication;
import static us.mn.state.dot.tms.client.comm.ControllerIOModel.DeviceType.Lane_Marking;
import static us.mn.state.dot.tms.client.comm.ControllerIOModel.DeviceType.Ramp_Meter;
import static us.mn.state.dot.tms.client.comm.ControllerIOModel.DeviceType.Tag_Reader;
import static us.mn.state.dot.tms.client.comm.ControllerIOModel.DeviceType.Weather_Sensor;

/**
 * A class used for iterating over all devices for the state.
 * @author Wesley Skillern (Southwest Research Institute)
 */
public class DeviceIterator implements Iterator<ControllerIO> {
	private SonarState state;
	private DeviceType nextType;
	private Iterator<ControllerIO> iterator;
	private boolean singleType = false;

	public DeviceIterator(SonarState state) {
		this.state = state;
		nextType = Alarm;
		nextIterator();
	}

	/**
	 * @param type restricts iterated type to single type
	 */
	public DeviceIterator(SonarState state, DeviceType type) {
		singleType = true;
		this.state = state;
		nextType = type;
		nextIterator();
	}

	@Override
	public boolean hasNext() {
		if (!iterator.hasNext())
			nextIterator();
		return iterator.hasNext();
	}

	@Override
	public ControllerIO next() {
		return iterator.next();
	}

	/**
	 * A state machine to increment the iterator if it is consumed.
	 * It will recursively call itself until it can find a Device Type that
	 * has an iterator that hasNext().
	 */
	private void nextIterator() {
		if (nextType != null) {
			switch (nextType) {
			case Alarm:
				setIterator(state.getAlarms());
				nextType = Camera;
				break;
			case Camera:
				setIterator(state.getCamCache().getCameras());
				nextType = Detector;
				break;
			case Detector:
				setIterator(state.getDetCache().getDetectors());
				nextType = DMS;
				break;
			case DMS:
				setIterator(state.getDmsCache().getDMSs());
				nextType = Gate_Arm;
				break;
			case Gate_Arm:
				setIterator(state.getGateArms());
				nextType = Lane_Marking;
				break;
			case Lane_Marking:
				setIterator(state.getLaneMarkings());
				nextType = LCSIndication;
				break;
			case LCSIndication:
				setIterator(state.getLcsCache()
					.getLCSIndications());
				nextType = Ramp_Meter;
				break;
			case Ramp_Meter:
				setIterator(state.getRampMeters());
				nextType = Beacon;
				break;
			case Beacon:
			case Beacon_Verify:
				setIterator(state.getBeacons());
				nextType = Weather_Sensor;
				break;
			case Weather_Sensor:
				setIterator(state.getWeatherSensorsCache()
					.getWeatherSensors());
				nextType = Tag_Reader;
				break;
			case Tag_Reader:
				setIterator(state.getTagReaders());
				nextType = null;
				break;
			}
			if (singleType)
				nextType = null;
			if (!iterator.hasNext() && nextType != null)
				nextIterator();
		}
	}

	private void setIterator(TypeCache cio) {
		iterator = cio.iterator();
	}
}
