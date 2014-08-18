/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2014  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.dms;

import java.awt.event.ActionEvent;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.client.widget.IAction;
import static us.mn.state.dot.tms.client.widget.Widgets.UI;

/**
 * The ComposerButtonPanel is a GUI panel for buttons related to the sign
 * composer panel.
 *
 * @author Douglas Lau
 */
public class ComposerButtonPanel extends JPanel {

	/** DMS dispatcher */
	private final DMSDispatcher dispatcher;

	/** Message composer */
	private final SignMessageComposer composer;

	/** Clear action */
	private final IAction clear = new IAction("dms.clear") {
		protected void doActionPerformed(ActionEvent e) {
			composer.clearWidgets();
		}
	};

	/** Button to clear the selected message */
	private final JButton clear_btn = new JButton(clear);

	/** Action used to send a message to the DMS */
	private final IAction send_msg = new IAction("dms.send") {
		protected void doActionPerformed(ActionEvent e) {
			dispatcher.sendSelectedMessage();
		}
	};

	/** Button to send the selected message */
	private final JButton send_btn = new JButton(send_msg);

	/** Action to blank selected DMS */
	private final BlankDmsAction blank_msg;

	/** Button to blank the selected message */
	private final JButton blank_btn;

	/** Action to query the DMS message (optional) */
	private final IAction query_msg = new IAction("dms.query.msg",
		SystemAttrEnum.DMS_QUERYMSG_ENABLE)
	{
		protected void doActionPerformed(ActionEvent e) {
			dispatcher.queryMessage();
		}
	};

	/** Button to query the DMS message */
	private final JButton query_btn = new JButton(query_msg);

	/** Create a new composer button panel */
	public ComposerButtonPanel(DMSManager manager, DMSDispatcher ds,
		SignMessageComposer smc)
	{
		dispatcher = ds;
		composer = smc;
		blank_msg = new BlankDmsAction(dispatcher);
		manager.setBlankAction(blank_msg);
		blank_btn = new JButton(blank_msg);
		layoutPanel();
		initializeWidgets();
	}

	/** Layout the panel */
	private void layoutPanel() {
		GroupLayout gl = new GroupLayout(this);
		gl.setHonorsVisibility(false);
		gl.setAutoCreateGaps(false);
		gl.setAutoCreateContainerGaps(false);
		setLayout(gl);
		GroupLayout.ParallelGroup bg = gl.createParallelGroup(
			GroupLayout.Alignment.CENTER);
		bg.addComponent(clear_btn);
		bg.addComponent(send_btn);
		bg.addComponent(blank_btn);
		bg.addComponent(query_btn);
		GroupLayout.SequentialGroup vert_g = gl.createSequentialGroup();
		vert_g.addGroup(bg);
		gl.setVerticalGroup(vert_g);
		GroupLayout.SequentialGroup hg = gl.createSequentialGroup();
		hg.addGroup(gl.createParallelGroup().addComponent(clear_btn));
		hg.addGap(UI.hgap * 2, UI.hgap * 4, UI.hgap * 64);
		hg.addGroup(gl.createParallelGroup().addComponent(send_btn));
		hg.addGap(UI.hgap);
		hg.addGroup(gl.createParallelGroup().addComponent(blank_btn));
		hg.addGap(UI.hgap);
		hg.addGroup(gl.createParallelGroup().addComponent(query_btn));
		gl.setHorizontalGroup(hg);
	}

	/** Initialize the widgets */
	private void initializeWidgets() {
		clear_btn.setMargin(UI.buttonInsets());
		// Leave send_btn margins alone, to make it stand out
		blank_btn.setMargin(UI.buttonInsets());
		query_btn.setMargin(UI.buttonInsets());
		query_btn.setVisible(query_msg.getIEnabled());
	}

	/** Dispose of the button panel */
	public void dispose() {
		removeAll();
	}

	/** Enable or disable the button panel */
	@Override
	public void setEnabled(boolean b) {
		super.setEnabled(b);
		clear.setEnabled(b);
		send_msg.setEnabled(b && dispatcher.canSend());
		blank_msg.setEnabled(b && dispatcher.canSend());
		query_msg.setEnabled(b && dispatcher.canRequest());
	}
}
