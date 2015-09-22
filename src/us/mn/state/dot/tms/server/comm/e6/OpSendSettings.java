/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2015  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm.e6;

import java.io.IOException;
import us.mn.state.dot.tms.server.TagReaderImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.PriorityLevel;

/**
 * Operation to send settings to an E6.
 * If the reader is in stop mode, send all settings.
 * Otherwise, check that settings are ok, and if not, put into stop mode and
 * start over.
 *
 * @author Douglas Lau
 */
public class OpSendSettings extends OpE6 {

	/** Messenger timeout */
	private final int timeout;

	/** Create a new "send settings" operation */
	public OpSendSettings(TagReaderImpl tr, E6Poller ep) {
		super(PriorityLevel.DOWNLOAD, tr, ep);
		timeout = ep.getTimeout();
	}

	/** Create the second phase of the operation */
	@Override
	protected Phase<E6Property> phaseTwo() {
		return new StoreAckTimeout();
	}

	/** Phase to store the data ack timeout */
	private class StoreAckTimeout extends Phase<E6Property> {

		/** Store the ack timeout */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			AckTimeoutProp dat = new AckTimeoutProp(
				AckTimeoutProp.Protocol.udp_ip, timeout);
			mess.logStore(dat);
			poller.sendStore(dat);
			return new QueryAckTimeout();
		}
	}

	/** Phase to query the data ack timeout */
	private class QueryAckTimeout extends Phase<E6Property> {

		/** Query the ack timeout */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			AckTimeoutProp timeout = new AckTimeoutProp(
				AckTimeoutProp.Protocol.udp_ip);
			poller.sendQuery(timeout);
			mess.logQuery(timeout);
			return new QueryTimeDate();
		}
	}

	/** Phase to query the time / date */
	private class QueryTimeDate extends Phase<E6Property> {

		/** Query the time / date */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			TimeDateProp stamp = new TimeDateProp();
			poller.sendQuery(stamp);
			mess.logQuery(stamp);
			if (stamp.isNear(500))
				return new QueryMode();
			else
				return new StoreTimeDate();
		}
	}

	/** Phase to store the time / date */
	private class StoreTimeDate extends Phase<E6Property> {

		/** Store the time / date */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			TimeDateProp stamp = new TimeDateProp();
			mess.logStore(stamp);
			poller.sendStore(stamp);
			return new QueryMode();
		}
	}

	/** Phase to query the mode */
	private class QueryMode extends Phase<E6Property> {

		/** Query the mode */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			ModeProp mode = new ModeProp();
			poller.sendQuery(mode);
			mess.logQuery(mode);
			if (mode.getMode() == ModeProp.Mode.stop)
				return new StoreDownlink();
			else
				return new QueryDownlink();
		}
	}

	/** Phase to store the downlink frequency */
	private class StoreDownlink extends Phase<E6Property> {

		/** Store the downlink frequency */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			FrequencyProp freq = new FrequencyProp(
				FrequencyProp.Source.downlink, 915.75f);
			mess.logStore(freq);
			poller.sendStore(freq);
			return new StoreUplink();
		}
	}

	/** Phase to store the uplink frequency */
	private class StoreUplink extends Phase<E6Property> {

		/** Store the uplink frequency */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			FrequencyProp freq = new FrequencyProp(
				FrequencyProp.Source.uplink, 903.25f);
			mess.logStore(freq);
			poller.sendStore(freq);
			return new StoreSeGoAtten();
		}
	}

	/** Phase to store the SeGo RF attenuation */
	private class StoreSeGoAtten extends Phase<E6Property> {

		/** Store the SeGo RF attenuation */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			RFAttenProp atten = new RFAttenProp(RFProtocol.SeGo,
				1, 1);
			mess.logStore(atten);
			poller.sendStore(atten);
			return new StoreASTMv6Atten();
		}
	}

	/** Phase to store the ASTMv6 RF attenuation */
	private class StoreASTMv6Atten extends Phase<E6Property> {

		/** Store the ASTMv6 RF attenuation */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			RFAttenProp atten = new RFAttenProp(RFProtocol.ASTMv6,
				15, 15);
			mess.logStore(atten);
			poller.sendStore(atten);
			return new StoreSeGoSeen();
		}
	}

	/** Phase to store the SeGo seen count */
	private class StoreSeGoSeen extends Phase<E6Property> {

		/** Store the SeGo seen count */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			SeenCountProp seen = new SeenCountProp(RFProtocol.SeGo,
				40, 255);
			mess.logStore(seen);
			poller.sendStore(seen);
			return new StoreASTMv6Seen();
		}
	}

	/** Phase to store the ASTMv6 seen count */
	private class StoreASTMv6Seen extends Phase<E6Property> {

		/** Store the ASTMv6 seen count */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			SeenCountProp seen = new SeenCountProp(
				RFProtocol.ASTMv6, 40, 255);
			mess.logStore(seen);
			poller.sendStore(seen);
			return new StoreSeGoDataDetect();
		}
	}

	/** Phase to store the SeGo data detect */
	private class StoreSeGoDataDetect extends Phase<E6Property> {

		/** Store the SeGo data detect */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			DataDetectProp det = new DataDetectProp(RFProtocol.SeGo,
				0);
			mess.logStore(det);
			poller.sendStore(det);
			return new StoreASTMv6DataDetect();
		}
	}

	/** Phase to store the ASTMv6 data detect */
	private class StoreASTMv6DataDetect extends Phase<E6Property> {

		/** Store the ASTMv6 data detect */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			DataDetectProp det = new DataDetectProp(
				RFProtocol.ASTMv6, 15);
			mess.logStore(det);
			poller.sendStore(det);
			return new StoreLineLoss();
		}
	}

	/** Phase to store the line loss */
	private class StoreLineLoss extends Phase<E6Property> {

		/** Store the line loss */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			LineLossProp loss = new LineLossProp(2);
			mess.logStore(loss);
			poller.sendStore(loss);
			return new StoreRFControl();
		}
	}

	/** Phase to store the RF control */
	private class StoreRFControl extends Phase<E6Property> {

		/** Store the RF control */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			RFControlProp ctrl = new RFControlProp(
				RFControlProp.Value.continuous);
			mess.logStore(ctrl);
			poller.sendStore(ctrl);
			return new StoreMuxMode();
		}
	}

	/** Phase to store the mux mode */
	private class StoreMuxMode extends Phase<E6Property> {

		/** Store the mux mode */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			MuxModeProp mode = new MuxModeProp(
				MuxModeProp.Value.channel_0);
			mess.logStore(mode);
			poller.sendStore(mode);
			return new StoreAntennaChannel();
		}
	}

	/** Phase to store the manual antenna channel control */
	private class StoreAntennaChannel extends Phase<E6Property> {

		/** Store the manual antenna channel */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			AntennaChannelProp chan = new AntennaChannelProp(
			    AntennaChannelProp.Value.disable_manual_control);
			mess.logStore(chan);
			poller.sendStore(chan);
			return new StoreMasterSlave();
		}
	}

	/** Phase to store the master/slave setting */
	private class StoreMasterSlave extends Phase<E6Property> {

		/** Store the master/slave setting */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			MasterSlaveProp mstr = new MasterSlaveProp(
				MasterSlaveProp.Value.master, 0);
			mess.logStore(mstr);
			poller.sendStore(mstr);
			return new StoreAppendData();
		}
	}

	/** Phase to store the append data */
	private class StoreAppendData extends Phase<E6Property> {

		/** Store the append data */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			AppendDataProp append = new AppendDataProp(
				AppendDataProp.Value.date_time_stamp);
			mess.logStore(append);
			poller.sendStore(append);
			return new QueryDownlink();
		}
	}

	/** Phase to query the downlink frequency */
	private class QueryDownlink extends Phase<E6Property> {

		/** Query the downlink frequency */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			FrequencyProp freq = new FrequencyProp(
				FrequencyProp.Source.downlink);
			poller.sendQuery(freq);
			mess.logQuery(freq);
			return new QueryUplink();
		}
	}

	/** Phase to query the uplink frequency */
	private class QueryUplink extends Phase<E6Property> {

		/** Query the uplink frequency */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			FrequencyProp freq = new FrequencyProp(
				FrequencyProp.Source.uplink);
			poller.sendQuery(freq);
			mess.logQuery(freq);
			return new QuerySeGoAtten();
		}
	}

	/** Phase to query the SeGo RF attenuation */
	private class QuerySeGoAtten extends Phase<E6Property> {

		/** Query the SeGo RF attenuation */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			RFAttenProp atten = new RFAttenProp(RFProtocol.SeGo);
			poller.sendQuery(atten);
			mess.logQuery(atten);
			return new QueryASTMv6Atten();
		}
	}

	/** Phase to query the ASTMv6 RF attenuation */
	private class QueryASTMv6Atten extends Phase<E6Property> {

		/** Query the ASTMv6 RF attenuation */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			RFAttenProp atten = new RFAttenProp(RFProtocol.ASTMv6);
			poller.sendQuery(atten);
			mess.logQuery(atten);
			return new QuerySeGoSeen();
		}
	}

	/** Phase to query the SeGo seen count */
	private class QuerySeGoSeen extends Phase<E6Property> {

		/** Query the SeGo seen count */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			SeenCountProp seen = new SeenCountProp(RFProtocol.SeGo);
			poller.sendQuery(seen);
			mess.logQuery(seen);
			return new QueryASTMv6Seen();
		}
	}

	/** Phase to query the ASTMv6 seen count */
	private class QueryASTMv6Seen extends Phase<E6Property> {

		/** Query the ASTMv6 seen count */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			SeenCountProp seen = new SeenCountProp(
				RFProtocol.ASTMv6);
			poller.sendQuery(seen);
			mess.logQuery(seen);
			return new QuerySeGoDataDetect();
		}
	}

	/** Phase to query the SeGo data detect */
	private class QuerySeGoDataDetect extends Phase<E6Property> {

		/** Query the SeGo data detect */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			DataDetectProp det = new DataDetectProp(RFProtocol.SeGo);
			poller.sendQuery(det);
			mess.logQuery(det);
			return new QueryASTMv6DataDetect();
		}
	}

	/** Phase to query the ASTMv6 data detect */
	private class QueryASTMv6DataDetect extends Phase<E6Property> {

		/** Query the ASTMv6 data detect */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			DataDetectProp det = new DataDetectProp(
				RFProtocol.ASTMv6);
			poller.sendQuery(det);
			mess.logQuery(det);
			return new QueryLineLoss();
		}
	}

	/** Phase to query the line loss */
	private class QueryLineLoss extends Phase<E6Property> {

		/** Query the line loss */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			LineLossProp loss = new LineLossProp();
			poller.sendQuery(loss);
			mess.logQuery(loss);
			return new QueryRFControl();
		}
	}

	/** Phase to query the RF control */
	private class QueryRFControl extends Phase<E6Property> {

		/** Query the RF control */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			RFControlProp ctrl = new RFControlProp();
			poller.sendQuery(ctrl);
			mess.logQuery(ctrl);
			return new QueryMuxMode();
		}
	}

	/** Phase to query the mux mode */
	private class QueryMuxMode extends Phase<E6Property> {

		/** Query the mux mode */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			MuxModeProp mode = new MuxModeProp();
			poller.sendQuery(mode);
			mess.logQuery(mode);
			return new QueryAntennaChannel();
		}
	}

	/** Phase to query the manual antenna channel control */
	private class QueryAntennaChannel extends Phase<E6Property> {

		/** Query the manual antenna channel */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			AntennaChannelProp chan = new AntennaChannelProp();
			poller.sendQuery(chan);
			mess.logQuery(chan);
			return new QueryMasterSlave();
		}
	}

	/** Phase to query the master/slave setting */
	private class QueryMasterSlave extends Phase<E6Property> {

		/** Query the master/slave setting */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			MasterSlaveProp mstr = new MasterSlaveProp();
			poller.sendQuery(mstr);
			mess.logQuery(mstr);
			return new QueryAppendData();
		}
	}

	/** Phase to query the append data */
	private class QueryAppendData extends Phase<E6Property> {

		/** Query the append data */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			AppendDataProp append = new AppendDataProp();
			poller.sendQuery(append);
			mess.logQuery(append);
			return null;
		}
	}
}
