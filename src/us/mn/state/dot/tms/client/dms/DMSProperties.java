/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2012  Minnesota Department of Transportation
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

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import us.mn.state.dot.sched.ActionJob;
import us.mn.state.dot.sched.ChangeJob;
import us.mn.state.dot.sched.FocusJob;
import us.mn.state.dot.sonar.User;
import us.mn.state.dot.sonar.client.TypeCache;
import us.mn.state.dot.tms.Base64;
import us.mn.state.dot.tms.BitmapGraphic;
import us.mn.state.dot.tms.Camera;
import us.mn.state.dot.tms.Controller;
import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.DMS;
import us.mn.state.dot.tms.DMSType;
import us.mn.state.dot.tms.SignMessageHelper;
import us.mn.state.dot.tms.Temperature;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.SonarState;
import us.mn.state.dot.tms.client.toast.ControllerForm;
import us.mn.state.dot.tms.client.toast.FormPanel;
import us.mn.state.dot.tms.client.toast.LocationPanel;
import us.mn.state.dot.tms.client.toast.SonarObjectForm;
import us.mn.state.dot.tms.client.widget.IButton;
import us.mn.state.dot.tms.client.widget.WrapperComboBoxModel;
import us.mn.state.dot.tms.client.widget.ZTable;
import us.mn.state.dot.tms.utils.I18N;

/**
 * This is a form for viewing and editing the properties of a dynamic message
 * sign (DMS).
 *
 * @author Douglas Lau
 * @author Michael Darter
 */
public class DMSProperties extends SonarObjectForm<DMS> {

	/** Format a string field */
	static protected String formatString(String s) {
		if(s != null && s.length() > 0)
			return s;
		else
			return UNKNOWN;
	}

	/** Format milimeter units for display */
	static protected String formatMM(Integer i) {
		if(i != null && i > 0)
			return i + " " + I18N.get("units.mm");
		else
			return UNKNOWN;
	}

	/** Format pixel units for display */
	static protected String formatPixels(Integer i) {
		if(i != null) {
			if(i > 0)
				return i + " " + I18N.get("units.pixels");
			else if(i == 0)
				return I18N.get("units.pixels.variable");
		}
		return UNKNOWN;
	}

	/** Format the temperature */
	static protected String formatTemp(Integer minTemp, Integer maxTemp) {
		if(minTemp == null || minTemp == maxTemp)
			return new Temperature(maxTemp).toString();
		else if(maxTemp == null)
			return new Temperature(minTemp).toString();
		else
			return new Temperature(minTemp).toString() + "..." + 
				new Temperature(maxTemp).toString();
	}

	/** Frame title */
	static protected String TITLE = I18N.get("dms.abbreviation") + ": ";

	/** Generic sign make */
	static protected final String MAKE_GENERIC = "Generic";

	/** Ledstar sign make */
	static protected final String MAKE_LEDSTAR = "Ledstar";

	/** Skyline sign make */
	static protected final String MAKE_SKYLINE = "Skyline";

	/** Location panel */
	protected LocationPanel location;

	/** Notes text area */
	protected final JTextArea notes = new JTextArea(3, 24);

	/** Camera combo box */
	protected final JComboBox camera = new JComboBox();

	/** Controller button */
	private final IButton controllerBtn = new IButton("controller");

	/** Messages tab */
	protected final MessagesTab messagesTab;

	/** Sign type label */
	protected final JLabel type = new JLabel();

	/** Sign technology label */
	protected final JLabel tech = new JLabel();

	/** Sign access label */
	protected final JLabel access = new JLabel();

	/** Sign legend label */
	protected final JLabel legend = new JLabel();

	/** Beacon label */
	protected final JLabel beacon = new JLabel();

	/** Sign face width label */
	protected final JLabel faceWidth = new JLabel();

	/** Sign face height label */
	protected final JLabel faceHeight = new JLabel();

	/** Horizontal border label */
	protected final JLabel hBorder = new JLabel();

	/** Vertical border label */
	protected final JLabel vBorder = new JLabel();

	/** Horizontal pitch label */
	protected final JLabel hPitch = new JLabel();

	/** Vertical pitch label */
	protected final JLabel vPitch = new JLabel();

	/** Sign width (pixels) label */
	protected final JLabel pWidth = new JLabel();

	/** Sign height (pixels) label */
	protected final JLabel pHeight = new JLabel();

	/** Character width label */
	protected final JLabel cWidth = new JLabel();

	/** Character height label */
	protected final JLabel cHeight = new JLabel();

	/** Button to query configuration */
	private final IButton configBtn = new IButton("dms.query.config");

	/** Cabinet temperature label */
	protected final JLabel cabinetTemp = new JLabel();

	/** Ambient temperature label */
	protected final JLabel ambientTemp = new JLabel();

	/** Housing temperature label */
	protected final JLabel housingTemp = new JLabel();

	/** Power supply status table */
	protected final ZTable powerTable = new ZTable();

	/** Operation description label */
	protected final JLabel operation = new JLabel();

	/** Query message button */
	protected final IButton queryMsgBtn = new IButton("dms.query.msg", 
		SystemAttrEnum.DMS_QUERYMSG_ENABLE);

	/** Reset DMS button */
	protected final IButton resetBtn = new IButton("dms.reset", 
		SystemAttrEnum.DMS_RESET_ENABLE);

	/** Query status button */
	private final IButton queryStatBtn = new IButton("dms.query.status");

	/** Send settings button */
	private final IButton settingsBtn = new IButton("device.send.settings");

	/** Bad pixel count label */
	protected final JLabel badPixels = new JLabel();

	/** Stuck off pixel panel */
	protected final SignPixelPanel stuck_off_pnl = new SignPixelPanel(true);

	/** Stuck on pixel panel */
	protected final SignPixelPanel stuck_on_pnl = new SignPixelPanel(true);

	/** Button to query pixel failures */
	private final IButton queryPixelsBtn = new IButton("dms.query.pixels");

	/** Button to test pixel failures */
	private final IButton testPixelsBtn = new IButton("dms.test.pixels");

	/** Photocell status table */
	protected final ZTable photocellTable = new ZTable();

	/** Light output label */
	protected final JLabel lightOutput = new JLabel();

	/** Current brightness low feedback button */
	private final IButton brightLowBtn = new IButton("dms.brightness.low");

	/** Current brightness good feedback button */
	private final IButton brightGoodBtn =new IButton("dms.brightness.good");

	/** Current brightness high feedback button */
	private final IButton brightHighBtn =new IButton("dms.brightness.high");

	/** Card layout for manufacturer panels */
	protected final CardLayout cards = new CardLayout();

	/** Card panel for manufacturer panels */
	protected final JPanel card_panel = new JPanel(cards);

	/** Make label */
	protected final JLabel make = new JLabel();

	/** Model label */
	protected final JLabel model = new JLabel();

	/** Version label */
	protected final JLabel version = new JLabel();

	/** Spinner to adjuct LDC pot base */
	protected final JSpinner ldcPotBaseSpn = new JSpinner(
		new SpinnerNumberModel(20, 20, 65, 5));

	/** Pixel current low threshold spinner */
	protected final JSpinner currentLowSpn = new JSpinner(
		new SpinnerNumberModel(5, 0, 100, 1));

	/** Pixel current high threshold spinner */
	protected final JSpinner currentHighSpn = new JSpinner(
		new SpinnerNumberModel(40, 0, 100, 1));

	/** Heat tape status label */
	protected final JLabel heatTapeStatus = new JLabel();

	/** Sonar state */
	protected final SonarState state;

	/** SONAR user */
	protected final User user;

	/** Create a new DMS properties form */
	public DMSProperties(Session s, DMS sign) {
		super(TITLE, s, sign);
		setHelpPageName("Help.DMSProperties");
		state = s.getSonarState();
		user = s.getUser();
		messagesTab = new MessagesTab(s, sign);
	}

	/** Get the SONAR type cache */
	protected TypeCache<DMS> getTypeCache() {
		return state.getDmsCache().getDMSs();
	}

	/** Initialize the widgets on the form */
	protected void initialize() {
		super.initialize();
		JTabbedPane tab = new JTabbedPane();
		tab.add(I18N.get("location"), createLocationPanel());
		tab.add(I18N.get("dms.messages"), messagesTab);
		tab.add(I18N.get("dms.config"), createConfigurationPanel());
		tab.add(I18N.get("device.status"), createStatusPanel());
		if(SystemAttrEnum.DMS_PIXEL_STATUS_ENABLE.getBoolean())
			tab.add(I18N.get("dms.pixels"), createPixelPanel());
		if(SystemAttrEnum.DMS_BRIGHTNESS_ENABLE.getBoolean()) {
			tab.add(I18N.get("dms.brightness"),
				createBrightnessPanel());
		}
		if(SystemAttrEnum.DMS_MANUFACTURER_ENABLE.getBoolean()) {
			tab.add(I18N.get("dms.manufacturer"),
				createManufacturerPanel());
		}
		add(tab);
		updateAttribute(null);
		if(canUpdate())
			createUpdateJobs();
		createControllerJob();
		if(canRequest())
			createRequestJobs();
		else
			disableRequestWidgets();
		setBackground(Color.LIGHT_GRAY);
	}

	/** Dispose of the form */
	protected void dispose() {
		location.dispose();
		messagesTab.dispose();
		super.dispose();
	}

	/** Create the widget jobs */
	protected void createUpdateJobs() {
		new FocusJob(notes) {
			public void perform() {
				proxy.setNotes(notes.getText());
			}
		};
		new ActionJob(this, camera) {
			public void perform() {
				proxy.setCamera(
					(Camera)camera.getSelectedItem());
			}
		};
		new ChangeJob(this, ldcPotBaseSpn) {
			public void perform() {
				Number n = (Number)ldcPotBaseSpn.getValue();
				proxy.setLdcPotBase(n.intValue());
			}
		};
		new ChangeJob(this, currentLowSpn) {
			public void perform() {
				Number n = (Number)currentLowSpn.getValue();
				proxy.setPixelCurrentLow(n.intValue());
			}
		};
		new ChangeJob(this, currentHighSpn) {
			public void perform() {
				Number n = (Number)currentHighSpn.getValue();
				proxy.setPixelCurrentHigh(n.intValue());
			}
		};
	}

	/** Create the controller job */
	protected void createControllerJob() {
		new ActionJob(this, controllerBtn) {
			public void perform() {
				controllerPressed();
			}
		};
	}

	/** Create the device request jobs */
	protected void createRequestJobs() {
		new ActionJob(this, configBtn) {
			public void perform() {
				proxy.setDeviceRequest(DeviceRequest.
					QUERY_CONFIGURATION.ordinal());
			}
		};
		new ActionJob(this, queryMsgBtn) {
			public void perform() throws Exception {
				proxy.setDeviceRequest(DeviceRequest.
					QUERY_MESSAGE.ordinal());
			}
		};
		new ActionJob(this, resetBtn) {
			public void perform() {
				proxy.setDeviceRequest(DeviceRequest.
					RESET_DEVICE.ordinal());
			}
		};
		new ActionJob(this, queryStatBtn) {
			public void perform() {
				proxy.setDeviceRequest(DeviceRequest.
					QUERY_STATUS.ordinal());
			}
		};
		new ActionJob(this, settingsBtn) {
			public void perform() {
				proxy.setDeviceRequest(DeviceRequest.
					SEND_SETTINGS.ordinal());
			}
		};
		new ActionJob(this, queryPixelsBtn) {
			public void perform() {
				proxy.setDeviceRequest(DeviceRequest.
					QUERY_PIXEL_FAILURES.ordinal());
			}
		};
		new ActionJob(this, testPixelsBtn) {
			public void perform() {
				proxy.setDeviceRequest(
					DeviceRequest.TEST_PIXELS.ordinal());
			}
		};
		new ActionJob(this, brightLowBtn) {
			public void perform() {
				proxy.setDeviceRequest(DeviceRequest.
					BRIGHTNESS_TOO_DIM.ordinal());
			}
		};
		new ActionJob(this, brightGoodBtn) {
			public void perform() {
				proxy.setDeviceRequest(DeviceRequest.
					BRIGHTNESS_GOOD.ordinal());
			}
		};
		new ActionJob(this, brightHighBtn) {
			public void perform() {
				proxy.setDeviceRequest(DeviceRequest.
					BRIGHTNESS_TOO_BRIGHT.ordinal());
			}
		};
	}

	/** Disable the device request widgets */
	protected void disableRequestWidgets() {
		configBtn.setEnabled(false);
		queryMsgBtn.setEnabled(false);
		resetBtn.setEnabled(false);
		queryStatBtn.setEnabled(false);
		settingsBtn.setEnabled(false);
		queryPixelsBtn.setEnabled(false);
		testPixelsBtn.setEnabled(false);
		brightLowBtn.setEnabled(false);
		brightGoodBtn.setEnabled(false);
		brightHighBtn.setEnabled(false);
	}

	/** Controller lookup button pressed */
	protected void controllerPressed() {
		Controller c = proxy.getController();
		if(c != null) {
			session.getDesktop().show(
				new ControllerForm(session, c));
		}
	}

	/** Create the location panel */
	protected JPanel createLocationPanel() {
		location = new LocationPanel(session, proxy.getGeoLoc());
		location.initialize();
		location.addRow(I18N.get("device.notes"), notes);
		camera.setModel(new WrapperComboBoxModel(
			state.getCamCache().getCameraModel()));
		location.add(I18N.get("camera"), camera);
		location.finishRow();
		location.setCenter();
		location.addRow(controllerBtn);
		return location;
	}

	/** Create the configuration panel */
	protected JPanel createConfigurationPanel() {
		type.setForeground(OK);
		tech.setForeground(OK);
		access.setForeground(OK);
		legend.setForeground(OK);
		beacon.setForeground(OK);
		faceWidth.setForeground(OK);
		faceHeight.setForeground(OK);
		hBorder.setForeground(OK);
		vBorder.setForeground(OK);
		hPitch.setForeground(OK);
		vPitch.setForeground(OK);
		pWidth.setForeground(OK);
		pHeight.setForeground(OK);
		cWidth.setForeground(OK);
		cHeight.setForeground(OK);
		FormPanel panel = new FormPanel(true);
		panel.addRow(I18N.get("dms.type"), type);
		panel.addRow(I18N.get("dms.technology"), tech);
		panel.addRow(I18N.get("dms.access"), access);
		panel.addRow(I18N.get("dms.legend"), legend);
		panel.addRow(I18N.get("dms.beacon"), beacon);
		panel.addRow(I18N.get("dms.face.width"), faceWidth);
		panel.addRow(I18N.get("dms.face.height"), faceHeight);
		panel.addRow(I18N.get("dms.border.horiz"), hBorder);
		panel.addRow(I18N.get("dms.border.vert"), vBorder);
		panel.addRow(I18N.get("dms.pitch.horiz"), hPitch);
		panel.addRow(I18N.get("dms.pitch.vert"), vPitch);
		panel.addRow(I18N.get("dms.pixel.width"), pWidth);
		panel.addRow(I18N.get("dms.pixel.height"), pHeight);
		panel.addRow(I18N.get("dms.char.width"), cWidth);
		panel.addRow(I18N.get("dms.char.height"), cHeight);
		panel.addRow(configBtn);
		return panel;
	}

	/** Create status panel */
	protected JPanel createStatusPanel() {
		powerTable.setAutoCreateColumnsFromModel(false);
		powerTable.setVisibleRowCount(6);
		cabinetTemp.setForeground(OK);
		ambientTemp.setForeground(OK);
		housingTemp.setForeground(OK);
		operation.setForeground(OK);
		FormPanel panel = new FormPanel(true);
		panel.addRow(I18N.get("dms.temp.cabinet"), cabinetTemp);
		panel.addRow(I18N.get("dms.temp.ambient"), ambientTemp);
		panel.addRow(I18N.get("dms.temp.housing"), housingTemp);
		panel.addRow(I18N.get("dms.power.supplies"), powerTable);
		panel.add(I18N.get("device.operation"), operation);
		if(queryMsgBtn.getIEnabled())
			panel.add(queryMsgBtn);
		panel.finishRow();
		if(resetBtn.getIEnabled())
			panel.addRow(resetBtn);
		panel.addRow(queryStatBtn);
		panel.addRow(settingsBtn);
		return panel;
	}

	/** Create pixel panel */
	protected JPanel createPixelPanel() {
		JPanel buttonPnl = new JPanel();
		buttonPnl.add(queryPixelsBtn);
		buttonPnl.add(testPixelsBtn);
		badPixels.setForeground(OK);
		FormPanel panel = new FormPanel(true);
		panel.addRow(I18N.get("dms.pixel.errors"), badPixels);
		panel.setFill();
		panel.addRow(createTitledPanel("dms.pixel.errors.off",
			stuck_off_pnl));
		panel.setFill();
		panel.addRow(createTitledPanel("dms.pixel.errors.on",
			stuck_on_pnl));
		panel.setCenter();
		panel.add(buttonPnl);
		return panel;
	}

	/** Create a panel with a titled border */
	private JPanel createTitledPanel(String text_id, JPanel p) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder(I18N.get(
			text_id)));
		panel.add(p, BorderLayout.CENTER);
		return panel;
	}

	/** Create brightness panel */
	protected JPanel createBrightnessPanel() {
		photocellTable.setAutoCreateColumnsFromModel(false);
		photocellTable.setVisibleRowCount(6);
		lightOutput.setForeground(OK);
		JPanel feedback = new JPanel();
		feedback.add(brightLowBtn);
		feedback.add(brightGoodBtn);
		feedback.add(brightHighBtn);
		FormPanel panel = new FormPanel(true);
		panel.addRow(I18N.get("dms.brightness.photocells"),
			photocellTable);
		panel.addRow(I18N.get("dms.brightness.output"), lightOutput);
		panel.addRow(I18N.get("dms.brightness.feedback"), feedback);
		return panel;
	}

	/** Create manufacturer-specific panel */
	protected JPanel createManufacturerPanel() {
		make.setForeground(OK);
		model.setForeground(OK);
		version.setForeground(OK);
		FormPanel panel = new FormPanel(true);
		panel.addRow(I18N.get("dms.make"), make);
		panel.addRow(I18N.get("dms.model"), model);
		panel.addRow(I18N.get("dms.version"), version);
		panel.addRow(card_panel);
		card_panel.add(createGenericPanel(), MAKE_GENERIC);
		card_panel.add(createLedstarPanel(), MAKE_LEDSTAR);
		card_panel.add(createSkylinePanel(), MAKE_SKYLINE);
		return panel;
	}

	/** Create generic manufacturer panel */
	protected JPanel createGenericPanel() {
		FormPanel panel = new FormPanel(true);
		panel.setTitle(I18N.get("dms.manufacturer.unknown"));
		panel.addRow(new JLabel(UNKNOWN));
		return panel;
	}

	/** Create Ledstar-specific panel */
	protected JPanel createLedstarPanel() {
		FormPanel panel = new FormPanel(canUpdate());
		panel.setTitle(MAKE_LEDSTAR);
		panel.addRow(I18N.get("dms.ledstar.pot.base"), ldcPotBaseSpn);
		panel.addRow(I18N.get("dms.ledstar.current.low"),currentLowSpn);
		panel.addRow(I18N.get("dms.ledstar.current.high"),
			currentHighSpn);
		return panel;
	}

	/** Create Skyline-specific panel */
	protected JPanel createSkylinePanel() {
		heatTapeStatus.setForeground(OK);
		FormPanel panel = new FormPanel(true);
		panel.setTitle(MAKE_SKYLINE);
		panel.addRow(I18N.get("dms.skyline.heat.tape"), heatTapeStatus);
		return panel;
	}

	/** Update one attribute on the form */
	protected void doUpdateAttribute(String a) {
		messagesTab.updateAttribute(a);
		if(a == null || a.equals("controller"))
			controllerBtn.setEnabled(proxy.getController() != null);
		if(a == null || a.equals("notes"))
			notes.setText(proxy.getNotes());
		if(a == null || a.equals("camera"))
			camera.setSelectedItem(proxy.getCamera());
		if(a == null || a.equals("make")) {
			String m = formatString(proxy.getMake());
			make.setText(m);
			updateMake(m.toUpperCase());
		}
		if(a == null || a.equals("model"))
			model.setText(formatString(proxy.getModel()));
		if(a == null || a.equals("version"))
			version.setText(formatString(proxy.getVersion()));
		if(a == null || a.equals("signAccess"))
			access.setText(formatString(proxy.getSignAccess()));
		if(a == null || a.equals("dmsType")) {
			DMSType t = DMSType.fromOrdinal(proxy.getDmsType());
			type.setText(t.description);
		}
		if(a == null || a.equals("faceHeight"))
			faceHeight.setText(formatMM(proxy.getFaceHeight()));
		if(a == null || a.equals("faceWidth"))
			faceWidth.setText(formatMM(proxy.getFaceWidth()));
		if(a == null || a.equals("heightPixels"))
			pHeight.setText(formatPixels(proxy.getHeightPixels()));
		if(a == null || a.equals("widthPixels"))
			pWidth.setText(formatPixels(proxy.getWidthPixels()));
		if(a == null || a.equals("horizontalBorder"))
			hBorder.setText(formatMM(proxy.getHorizontalBorder()));
		if(a == null || a.equals("verticalBorder"))
			vBorder.setText(formatMM(proxy.getVerticalBorder()));
		if(a == null || a.equals("legend"))
			legend.setText(formatString(proxy.getLegend()));
		if(a == null || a.equals("beaconType"))
			beacon.setText(formatString(proxy.getBeaconType()));
		if(a == null || a.equals("technology"))
			tech.setText(formatString(proxy.getTechnology()));
		if(a == null || a.equals("charHeightPixels")) {
			cHeight.setText(formatPixels(
				proxy.getCharHeightPixels()));
		}
		if(a == null || a.equals("charWidthPixels")) {
			cWidth.setText(formatPixels(
				proxy.getCharWidthPixels()));
		}
		if(a == null || a.equals("horizontalPitch"))
			hPitch.setText(formatMM(proxy.getHorizontalPitch()));
		if(a == null || a.equals("verticalPitch"))
			vPitch.setText(formatMM(proxy.getVerticalPitch()));
		// NOTE: messageCurrent attribute changes after all sign
		//       dimension attributes are updated.
		if(a == null || a.equals("messageCurrent")) {
			updatePixelStatus();
			updateFeedback();
		}
		if(a == null || a.equals("ldcPotBase")) {
			Integer b = proxy.getLdcPotBase();
			if(b != null)
				ldcPotBaseSpn.setValue(b);
		}
		if(a == null || a.equals("pixelCurrentLow")) {
			Integer c = proxy.getPixelCurrentLow();
			if(c != null)
				currentLowSpn.setValue(c);
		}
		if(a == null || a.equals("pixelCurrentHigh")) {
			Integer c = proxy.getPixelCurrentHigh();
			if(c != null)
				currentHighSpn.setValue(c);
		}
		if(a == null || a.equals("powerStatus"))
			updatePowerStatus();
		if(a == null || a.equals("heatTapeStatus"))
			heatTapeStatus.setText(proxy.getHeatTapeStatus());
		if(a == null || a.equals("pixelStatus"))
			updatePixelStatus();
		if(a == null || a.equals("photocellStatus"))
			updatePhotocellStatus();
		if(a == null || a.equals("lightOutput")) {
			Integer o = proxy.getLightOutput();
			if(o != null)
				lightOutput.setText("" + o + "%");
			else
				lightOutput.setText(UNKNOWN);
			updateFeedback();
		}
		if(a == null || a.equals("minCabinetTemp") ||
		   a.equals("maxCabinetTemp"))
		{
			cabinetTemp.setText(formatTemp(
				proxy.getMinCabinetTemp(),
				proxy.getMaxCabinetTemp()));
		}
		if(a == null || a.equals("minAmbientTemp") ||
		   a.equals("maxAmbientTemp"))
		{
			ambientTemp.setText(formatTemp(
				proxy.getMinAmbientTemp(),
				proxy.getMaxAmbientTemp()));
		}
		if(a == null || a.equals("minHousingTemp") ||
		   a.equals("maxHousingTemp"))
		{
			housingTemp.setText(formatTemp(
				proxy.getMinHousingTemp(),
				proxy.getMaxHousingTemp()));
		}
		if(a == null || a.equals("operation"))
			operation.setText(proxy.getOperation());
	}

	/** Select card on manufacturer panel for the given make */
	protected void updateMake(String m) {
		if(m.contains(MAKE_LEDSTAR.toUpperCase()))
			cards.show(card_panel, MAKE_LEDSTAR);
		else if(m.contains(MAKE_SKYLINE.toUpperCase()))
			cards.show(card_panel, MAKE_SKYLINE);
		else
			cards.show(card_panel, MAKE_GENERIC);
	}

	/** Update the power status */
	protected void updatePowerStatus() {
		String[] s = proxy.getPowerStatus();
		if(s != null) {
			PowerTableModel m = new PowerTableModel(s);
			powerTable.setColumnModel(m.createColumnModel());
			powerTable.setModel(m);
		}
	}

	/** Update the pixel status */
	protected void updatePixelStatus() {
		updatePixelPanel(stuck_off_pnl);
		updatePixelPanel(stuck_on_pnl);
		String[] pixels = proxy.getPixelStatus();
		if(pixels != null && pixels.length == 2) {
			try {
				updatePixelStatus(pixels);
				return;
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		stuck_off_pnl.setGraphic(null);
		stuck_on_pnl.setGraphic(null);
		badPixels.setText(UNKNOWN);
	}

	/** Update the pixel status */
	protected void updatePixelStatus(String[] pixels) throws IOException {
		BitmapGraphic stuckOff = createBlankBitmap();
		BitmapGraphic stuckOn = createBlankBitmap();
		if(stuckOff == null || stuckOn == null)
			return;
		byte[] b_off = Base64.decode(pixels[DMS.STUCK_OFF_BITMAP]);
		if(b_off.length == stuckOff.length())
			stuckOff.setPixels(b_off);
		stuck_off_pnl.setGraphic(stuckOff);
		byte[] b_on = Base64.decode(pixels[DMS.STUCK_ON_BITMAP]);
		if(b_on.length == stuckOn.length())
			stuckOn.setPixels(b_on);
		stuck_on_pnl.setGraphic(stuckOn);
		int n_off = stuckOff.getLitCount();
		int n_on = stuckOn.getLitCount();
		badPixels.setText(String.valueOf(n_off + n_on));
	}

	/** Create a blank bitmap */
	protected BitmapGraphic createBlankBitmap() {
		Integer w = proxy.getWidthPixels();	// Avoid race
		Integer h = proxy.getHeightPixels();	// Avoid race
		if(w != null && h != null)
			return new BitmapGraphic(w, h);
		else
			return null;
	}

	/** Update the dimensions of a sign pixel panel */
	protected void updatePixelPanel(SignPixelPanel p) {
		updatePixelPhysical(p);
		updatePixelLogical(p);
		p.repaint();
	}

	/** Update the physical dimensions of a sign pixel panel */
	protected void updatePixelPhysical(SignPixelPanel p) {
		Integer w = proxy.getFaceWidth();
		Integer h = proxy.getFaceHeight();
		Integer hp = proxy.getHorizontalPitch();
		Integer vp = proxy.getVerticalPitch();
		Integer hb = proxy.getHorizontalBorder();
		Integer vb = proxy.getVerticalBorder();
		if(w != null && h != null && hp != null && vp != null &&
		   hb != null && vb != null)
		{
			p.setPhysicalDimensions(w, h, hb, vb, hp, vp);
		}
	}

	/** Update the logical dimensions of a sign pixel panel */
	protected void updatePixelLogical(SignPixelPanel p) {
		Integer wp = proxy.getWidthPixels();
		Integer hp = proxy.getHeightPixels();
		Integer cw = proxy.getCharWidthPixels();
		Integer ch = proxy.getCharHeightPixels();
		if(wp != null && hp != null && cw != null && ch != null)
			p.setLogicalDimensions(wp, hp, cw, ch);
	}

	/** Update the photocell status */
	protected void updatePhotocellStatus() {
		String[] s = proxy.getPhotocellStatus();
		if(s != null) {
			PhotocellTableModel m = new PhotocellTableModel(s);
			photocellTable.setColumnModel(m.createColumnModel());
			photocellTable.setModel(m);
		}
	}

	/** Update the feedback buttons */
	protected void updateFeedback() {
		boolean enable = canRequest() && !SignMessageHelper.isBlank(
			proxy.getMessageCurrent());
		brightLowBtn.setEnabled(enable);
		brightGoodBtn.setEnabled(enable);
		brightHighBtn.setEnabled(enable);
	}

	/** Check if the user can make device requests */
	protected boolean canRequest() {
		return canUpdate("deviceRequest");
	}
}
