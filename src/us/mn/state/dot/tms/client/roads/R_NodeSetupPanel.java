/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2007-2013  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.roads;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import us.mn.state.dot.sched.Job;
import us.mn.state.dot.sched.ChangeJob;
import us.mn.state.dot.sched.FocusLostJob;
import us.mn.state.dot.tms.R_Node;
import static us.mn.state.dot.tms.R_Node.MAX_LANES;
import static us.mn.state.dot.tms.R_Node.MAX_SHIFT;
import static us.mn.state.dot.tms.R_Node.MIN_SHIFT;
import us.mn.state.dot.tms.R_NodeTransition;
import us.mn.state.dot.tms.R_NodeType;
import static us.mn.state.dot.tms.client.IrisClient.WORKER;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.widget.IAction;
import us.mn.state.dot.tms.client.widget.IPanel;

/**
 * A panel for editing the setup properties of an r_node.
 *
 * @author Douglas Lau
 */
public class R_NodeSetupPanel extends IPanel {

	/** R_Node action */
	abstract private class NAction extends IAction {
		protected NAction(String text_id) {
			super(text_id);
		}
		@Override protected final void do_perform() {
			R_Node n = node;
			if(n != null)
				do_perform(n);
		}
		abstract void do_perform(R_Node n);
	}

	/** Node type combobox */
	private final JComboBox type_cbx =
		new JComboBox(R_NodeType.getDescriptions());

	/** Pickable check box */
	private final JCheckBox pick_chk = new JCheckBox();

	/** Transition type combobox */
	private final JComboBox trans_cbx =
		new JComboBox(R_NodeTransition.getDescriptions());

	/** Above check box */
	private final JCheckBox above_chk = new JCheckBox();

	/** Component for number of lanes */
	private final JSpinner lane_spn = new JSpinner(
		new SpinnerNumberModel(2, 0, MAX_LANES, 1));

	/** Attach side check box */
	private final JCheckBox attach_chk = new JCheckBox();

	/** Component for lane shift */
	private final JSpinner shift_spn = new JSpinner(
		new SpinnerNumberModel(MIN_SHIFT, MIN_SHIFT, MAX_SHIFT, 1));

	/** Active check box */
	private final JCheckBox active_chk = new JCheckBox();

	/** Station ID text field */
	protected final JTextField station_txt = new JTextField(8);

	/** Abandoned check box */
	private final JCheckBox abandoned_chk = new JCheckBox();

	/** Component for speed limit */
	protected final JSpinner speed_spn = new JSpinner(
		new SpinnerNumberModel(55, 25, 120, 5));

	/** User session */
	protected final Session session;

	/** Node being edited */
	protected R_Node node;

	/** Create the roadway node setup panel */
	public R_NodeSetupPanel(Session s) {
		session = s;
	}

	/** Initialize the panel */
	public void initialize() {
		type_cbx.setAction(new NAction("r_node.type") {
			@Override protected void do_perform(R_Node n) {
				n.setNodeType(type_cbx.getSelectedIndex());
			}
		});
		pick_chk.setAction(new NAction(null) {
			@Override protected void do_perform(R_Node n) {
				n.setPickable(pick_chk.isSelected());
			}
		});
		trans_cbx.setAction(new NAction("r_node.transition") {
			@Override protected void do_perform(R_Node n) {
				n.setTransition(trans_cbx.getSelectedIndex());
			}
		});
		above_chk.setAction(new NAction(null) {
			@Override protected void do_perform(R_Node n) {
				n.setAbove(above_chk.isSelected());
			}
		});
		attach_chk.setAction(new NAction(null) {
			@Override protected void do_perform(R_Node n) {
				n.setAttachSide(attach_chk.isSelected());
			}
		});
		active_chk.setAction(new NAction(null) {
			@Override protected void do_perform(R_Node n) {
				n.setActive(active_chk.isSelected());
			}
		});
		abandoned_chk.setAction(new NAction(null) {
			@Override protected void do_perform(R_Node n) {
				n.setAbandoned(abandoned_chk.isSelected());
			}
		});
		add("r_node.type");
		add(type_cbx);
		add("r_node.pickable");
		add(pick_chk, true);
		add("r_node.transition");
		add(trans_cbx);
		add("r_node.above");
		add(above_chk, true);
		add("r_node.lanes");
		add(lane_spn);
		add("r_node.attach.side");
		add(attach_chk, true);
		add("r_node.shift");
		add(shift_spn);
		add("r_node.active");
		add(active_chk, true);
		add("r_node.station");
		add(station_txt);
		add("r_node.abandoned");
		add(abandoned_chk, true);
		add("r_node.speed.limit");
		add(speed_spn, true);
		createJobs();
		clear();
	}

	/** Create the jobs */
	protected void createJobs() {
		lane_spn.addChangeListener(new ChangeJob(WORKER) {
			@Override public void perform() {
				Number n = (Number)lane_spn.getValue();
				setLanes(n.intValue());
			}
		});
		shift_spn.addChangeListener(new ChangeJob(WORKER) {
			@Override public void perform() {
				Number n = (Number)shift_spn.getValue();
				setShift(n.intValue());
			}
		});
		station_txt.addFocusListener(new FocusLostJob(WORKER) {
			@Override public void perform() {
				String s = station_txt.getText().trim();
				setStationID(s);
			}
		});
		speed_spn.addChangeListener(new ChangeJob(WORKER) {
			@Override public void perform() {
				Number n = (Number)speed_spn.getValue();
				setSpeedLimit(n.intValue());
			}
		});
	}

	/** Set the number of lanes */
	protected void setLanes(int l) {
		R_Node n = node;
		if(n != null)
			n.setLanes(l);
	}

	/** Set the lane shift */
	protected void setShift(int s) {
		R_Node n = node;
		if(n != null)
			n.setShift(s);
	}

	/** Set the station ID */
	protected void setStationID(String s) {
		R_Node n = node;
		if(n != null)
			n.setStationID(s);
	}

	/** Set the speed limit */
	protected void setSpeedLimit(int s) {
		R_Node n = node;
		if(n != null)
			n.setSpeedLimit(s);
	}

	/** Update one attribute */
	public final void update(final R_Node n, final String a) {
		// Serialize on WORKER thread
		WORKER.addJob(new Job() {
			@Override public void perform() {
				doUpdate(n, a);
			}
		});
	}

	/** Update one attribute */
	protected void doUpdate(R_Node n, String a) {
		if(a == null)
			node = n;
		if(a == null || a.equals("nodeType")) {
			type_cbx.setEnabled(canUpdate(n, "nodeType"));
			type_cbx.setSelectedIndex(n.getNodeType());
		}
		if(a == null || a.equals("pickable")) {
			pick_chk.setEnabled(canUpdate(n, "pickable"));
			pick_chk.setSelected(n.getPickable());
		}
		if(a == null || a.equals("above")) {
			above_chk.setEnabled(canUpdate(n, "above"));
			above_chk.setSelected(n.getAbove());
		}
		if(a == null || a.equals("transition")) {
			trans_cbx.setEnabled(canUpdate(n, "transition"));
			trans_cbx.setSelectedIndex(n.getTransition());
		}
		if(a == null || a.equals("lanes")) {
			lane_spn.setEnabled(canUpdate(n, "lanes"));
			lane_spn.setValue(n.getLanes());
		}
		if(a == null || a.equals("attachSide")) {
			attach_chk.setEnabled(canUpdate(n, "attachSide"));
			attach_chk.setSelected(n.getAttachSide());
		}
		if(a == null || a.equals("shift")) {
			shift_spn.setEnabled(canUpdate(n, "shift"));
			shift_spn.setValue(n.getShift());
		}
		if(a == null || a.equals("active")) {
			active_chk.setEnabled(canUpdate(n, "active"));
			active_chk.setSelected(n.getActive());
		}
		if(a == null || a.equals("stationID")) {
			station_txt.setEnabled(canUpdate(n, "stationID"));
			station_txt.setText(n.getStationID());
		}
		if(a == null || a.equals("abandoned")) {
			abandoned_chk.setEnabled(canUpdate(n, "abandoned"));
			abandoned_chk.setSelected(n.getAbandoned());
		}
		if(a == null || a.equals("speedLimit")) {
			speed_spn.setEnabled(canUpdate(n, "speedLimit"));
			speed_spn.setValue(n.getSpeedLimit());
		}
	}

	/** Test if the user can update an attribute */
	protected boolean canUpdate(R_Node n, String a) {
		return session.canUpdate(n, a);
	}

	/** Clear all attributes */
	public final void clear() {
		// Serialize on WORKER thread
		WORKER.addJob(new Job() {
			@Override public void perform() {
				doClear();
			}
		});
	}

	/** Clear all attributes */
	protected void doClear() {
		node = null;
		type_cbx.setEnabled(false);
		type_cbx.setSelectedIndex(0);
		pick_chk.setEnabled(false);
		pick_chk.setSelected(false);
		trans_cbx.setEnabled(false);
		trans_cbx.setSelectedIndex(0);
		above_chk.setEnabled(false);
		above_chk.setSelected(false);
		lane_spn.setEnabled(false);
		lane_spn.setValue(0);
		attach_chk.setEnabled(false);
		attach_chk.setSelected(false);
		shift_spn.setEnabled(false);
		shift_spn.setValue(0);
		active_chk.setEnabled(false);
		active_chk.setSelected(false);
		station_txt.setEnabled(false);
		station_txt.setText("");
		abandoned_chk.setEnabled(false);
		abandoned_chk.setSelected(false);
		speed_spn.setEnabled(false);
		speed_spn.setValue(55);
	}
}
