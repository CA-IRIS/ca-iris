/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2015 AHMCT, University of California
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
package us.mn.state.dot.tms.client.toolbar;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumn;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;
import us.mn.state.dot.sched.Job;
import us.mn.state.dot.sched.Scheduler;
import us.mn.state.dot.tms.CommLink;
import us.mn.state.dot.tms.CommLinkHelper;
import us.mn.state.dot.tms.CommProtocol;
import us.mn.state.dot.tms.client.widget.AbstractForm;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.utils.I18N;
import us.mn.state.dot.tms.utils.Log;
import us.mn.state.dot.tms.utils.SFile;
import us.mn.state.dot.tms.utils.SString;
import us.mn.state.dot.tms.utils.STime;

/**
 * Form that displays all current AWS messages. It reads current AWS
 * messages from a URL directly, rather than getting this information
 * via SONAR, which it should do in the future.
 *
 * @author Michael Darter
 */
public class ViewAwsMsgsForm2 extends AbstractForm {

	/** Number of columns in table */
	static private final int NUM_COLS = 8;

	/** Scheduler that runs refresh job */
	static private final Scheduler m_scheduler = new Scheduler(
		"Scheduler: AWS form refresh");

	/** Scheduler refresh job */
	private final Job m_rjob = new RefreshTimerJob();

	/** Create a new form */
	public ViewAwsMsgsForm2() {
		super("Current AWS Messages");
		setPreferredSize(new Dimension(670, 580));
	}

	/** Initialize form. Called from SmartDesktop.addForm() */
	protected void initialize() {
		add(createFormPanel());
		m_scheduler.addJob(m_rjob);
	}

	/** Create form panel */
	private JPanel createFormPanel() {

		// center panel, contains text
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel,
			BoxLayout.Y_AXIS));
		centerPanel.add(Box.createHorizontalStrut(10));

		JTable table = createTable();
		JScrollPane scrollPane = new JScrollPane(table);
		centerPanel.add(scrollPane, BorderLayout.CENTER);

		centerPanel.add(Box.createHorizontalStrut(10));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		return centerPanel;
	}

	/** Create table */
	private JTable createTable() {
		// create table
		String[] columnNames = {"Time", I18N.get("dms"),
			"Line 1", "Line 2", "Line 3", "Line 4", "Line 5",
			"Line 6"};

		// read AWS file
		Object[][] data = getAwsValues(getAwsUrl());
		if(data == null)
			data = new Object[1][columnNames.length];
		JTable table = new JTable(data, columnNames);
		setTableColWidths(table);
		return table;
	}

	/** Set table column widths */
	private static void setTableColWidths(JTable jt) {
		jt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		int[] cw = {64, 30, 90, 90, 90, 90, 90, 90};
		assert cw.length == NUM_COLS;
		for(int i = 0; i < cw.length; ++i)
			setTableColWidth(jt, i, cw[i]);
	}

	/** Set table column width */
	private static void setTableColWidth(JTable jt, int col, int wid) {
		jt.getColumnModel().getColumn(col).setPreferredWidth(wid);
	}

	/** Refresh timer job */
	protected class RefreshTimerJob extends Job {

		/** Create a new 10-second timer job */
		protected RefreshTimerJob() {
			super(Calendar.SECOND, 10, Calendar.SECOND, 8);
		}

		/** Perform the timer job */
		public void perform() throws Exception {
			JPanel jp = createFormPanel();
			removeAll();
			add(jp);
			revalidate();
		}
	}

	/** Get the AWS message file name, which is specified in 
	 * a system attribute. */
	private String getAwsUrl() {
		return SystemAttrEnum.DMS_AWS_MSG_FILE_URL.getString();
	}

	/** Read AWS message values from a URL */
	private Object[][] getAwsValues(String awsFileUrl) {
		if(awsFileUrl == null || awsFileUrl.length() <= 0)
			return null;
		byte[] msgs = SFile.readUrl(awsFileUrl);
		ArrayList lines = null;
		Object[][] ret = null;
		if(msgs != null) {
			lines = parseAwsFile(msgs);
			if(lines != null) {
				ret = new Object[lines.size()][];
				for(int i=0; i<lines.size(); ++i)
					ret[i] = parseAwsFileLine(
						(String)lines.get(i));
			}
		}
		return ret;
	}

	/** Parse a byte array of AWS messages. */
	private ArrayList parseAwsFile(byte[] argmsgs) {
		ArrayList ret = new ArrayList<String>();

		// cycle through each line, which is terminated by '\n'
		String msgs = SString.byteArrayToString(argmsgs);
		StringTokenizer lineTok = new StringTokenizer(msgs, "\n");
		while(lineTok.hasMoreTokens()) {
			String line = lineTok.nextToken();
			ret.add(line);
		}
		return ret;
	}

	/** Parse a string that contains a single DMS message.
	 * @param argline a single DMS message, fields delimited with ';'. */
	private Object[] parseAwsFileLine(String argline) {
		if(argline == null)
			argline = "";
		Object[] ret = new Object[NUM_COLS];
		for(int i = 0; i < ret.length; ++i)
			ret[i] = "";
		try {
			// add a space between successive delimiters. This is
			// done so the tokenizer doesn't skip over delimeters
			// with nothing between them.
			String line = argline.replace(";;", "; ;");
			line = line.replace(";;", "; ;");

			// verify syntax
			StringTokenizer tok = new StringTokenizer(line, ";");

			// validity check
			int numtoks = tok.countTokens();
			final int EXPNUMTOKS = 8 + 1;
			if(numtoks != EXPNUMTOKS) {
				throw new IllegalArgumentException("Bogus " +
					"DMS file format, numtoks was " +
					numtoks + ", expected " + EXPNUMTOKS +
					" (" + argline + ").");
			}

			// #1, date: 20080403085910
			ret[0] = new String(tok.nextToken());

			// #2, id: 39
			ret[1] = new String(tok.nextToken());

			// #3 - #8, rows of text
			{
				final int numrows = 6;
				String[] row = new String[numrows];
				for(int i = 0; i < numrows; ++i)
					ret[2 + i] = tok.nextToken().trim();
			}

		} catch(Exception ex) {
			Log.warning("ViewAwsMsgsForm2.parse(): " +
				"unexpected exception: " + ex + ", line=" +
				argline);
		}

		return ret;
	}

	/** Form closed */
	protected void dispose() {
		m_scheduler.removeJob(m_rjob);
		super.dispose();
	}
}
