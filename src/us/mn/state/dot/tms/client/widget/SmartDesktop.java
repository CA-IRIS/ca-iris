/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2013  Minnesota Department of Transportation
 * Copyright (C) 2010 AHMCT, University of California
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
package us.mn.state.dot.tms.client.widget;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import us.mn.state.dot.sched.Job;
import us.mn.state.dot.sched.SwingRunner;
import us.mn.state.dot.tms.client.IrisClient;
import us.mn.state.dot.tms.client.help.Help;
import us.mn.state.dot.tms.client.widget.Screen;

/**
 * SmartDesktop
 *
 * @author Douglas Lau
 * @author Michael Darter
 */
public class SmartDesktop extends JDesktopPane {

	/** Layer which contains all internal frames */
	static protected final Integer FRAME_LAYER = new Integer(1);

	/** Select the given frame */
	static protected void selectFrame(JInternalFrame frame) {
		try {
			frame.setIcon(false);
			frame.setSelected(true);
		}
		catch(PropertyVetoException e) {
			// Do nothing
		}
	}

	/** Main desktop screen */
	protected final Screen screen;

	/** Iris client */
	public final IrisClient client;

	/** Create a new smart desktop */
	public SmartDesktop(Screen s, IrisClient ic) {
		screen = s;
		client = ic;

		// register the keystroke that invokes the help system
		setFocusable(true); // required to receive focus notification
		registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				invokeHelp();
			}
		}, Help.getSystemHelpKey(), 
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

	/** Invoke the help system */
	protected void invokeHelp() {
		client.WORKER.addJob(new Job() {
			public void perform() throws IOException {
				AbstractForm cf = findTopFrame();
				if(cf != null)
					Help.invokeHelp(cf.getHelpPageUrl());
				else
					Help.invokeHelp(null);
			}
		});
	}

	/** Create a new internal frame */
	protected JInternalFrame createFrame(final AbstractForm form) {
		final JInternalFrame frame = new JInternalFrame();
		frame.setTitle(form.getTitle());
		frame.setClosable(true);
		frame.setIconifiable(true);
		frame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
		frame.addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosed(InternalFrameEvent e) {
				smartDesktopRequestFocus();	// see note
				form.dispose();
			}
		});
		frame.setContentPane(form);
		form.addFormCloseListener(new FormCloseListener() {
			public void formClosed(FormCloseEvent e) {
				frame.dispose();
			}
		});
		return frame;
	}

	/** This method is being invoked to solve a subtle problem with
	 *  help system invocation--if an JInternalFrame is created, then
	 *  closed, then the help key pressed, the Help.actionPerformed() 
	 *  listener doesn't get notification, and the help page isn't 
	 *  opened. The solution is to set the focus to the JDesktopPane
	 *  explicitly as each JInternalFrame is closing. This seems to 
	 *  work. */
	protected void smartDesktopRequestFocus() {
		this.requestFocus();
	}

	/** Add an abstract form to the desktop pane */
	protected JInternalFrame addForm(AbstractForm form) {
		form.initialize();
		JInternalFrame frame = createFrame(form);
		frame.pack();
		super.add(frame, FRAME_LAYER);
		return frame;
	}

	/** Find a frame with a specific title */
	protected JInternalFrame find(String title) {
		for(JInternalFrame frame: getAllFrames()) {
			if(title.equals(frame.getTitle()))
				return frame;
		}
		return null;
	}

	/** Show the specified form */
	public void show(final AbstractForm form) {
		SwingRunner.invoke(new Runnable() {
			public void run() {
				doShow(form);
			}
		});
	}

	/** Show the specified form */
	protected void doShow(AbstractForm form) {
		JInternalFrame frame = find(form.getTitle());
		if(frame != null)
			selectFrame(frame);
		else
			frame = addForm(form);
		frame.setLocation(screen.getCenteredLocation(this,
			frame.getSize()));
		frame.show();
	}

	/** Close all internal frames */
	public void closeFrames() {
		for(JInternalFrame frame: getAllFrames()) {
			try {
				frame.setClosed(true);
			}
			catch(PropertyVetoException e) {
				// Do nothing
			}
		}
	}

	/** Dispose of the desktop */
	public void dispose() {
		closeFrames();
	}

	/** Find the top level frame */
	protected AbstractForm findTopFrame() {
		for(JInternalFrame f: getAllFrames())
			if(f.getFocusOwner() != null)
				if(f.getContentPane() instanceof AbstractForm)
					return (AbstractForm)f.getContentPane();
		return null;
	}
}
