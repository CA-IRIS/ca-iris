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
package us.mn.state.dot.tms.client;

import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPopupMenu;
import us.mn.state.dot.geokit.ZoomLevel;
import us.mn.state.dot.map.MapBean;
import us.mn.state.dot.map.MapModel;
import us.mn.state.dot.map.PointSelector;
import us.mn.state.dot.sched.Job;
import us.mn.state.dot.sched.Scheduler;
import us.mn.state.dot.sonar.SonarException;
import us.mn.state.dot.sonar.User;
import us.mn.state.dot.tms.MapExtent;
import us.mn.state.dot.tms.MapExtentHelper;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.client.system.LoginForm;
import us.mn.state.dot.tms.client.widget.Screen;
import us.mn.state.dot.tms.client.widget.ScreenLayout;
import us.mn.state.dot.tms.client.widget.SmartDesktop;
import us.mn.state.dot.tms.client.widget.Widgets;
import us.mn.state.dot.tms.utils.I18N;

/**
 * The main window for the IRIS client application.
 *
 * @author Douglas Lau
 * @author Erik Engstrom
 * @author Michael Darter
 */
public class IrisClient extends JFrame {

	/** Worker thread */
	static public final Scheduler WORKER = new Scheduler("worker");

	/** Login scheduler */
	static private final Scheduler LOGIN = new Scheduler("login");

	/** Create the window title */
	static private String createTitle(String suffix) {
		return SystemAttrEnum.WINDOW_TITLE.getString() + suffix;
	}

	/** Create the window title.
	 * @param s Current session, or null if not logged in. */
	static private String createTitle(Session s) {
		if(s != null) {
			User u = s.getUser();
			return createTitle(u.getName() + " (" +
				u.getFullName() + ")");
		}
		return createTitle(I18N.get("iris.logged.out"));
	}

	/** Array of screens to display client */
	private final Screen[] screens;

	/** Array of screen panes */
	private final ScreenPane[] s_panes;

	/** Desktop pane */
	private final SmartDesktop desktop;

	/** Screen layout for desktop pane */
	private final ScreenLayout layout;

	/** Client properties */
	private final Properties props;

	/** Exception handler */
	private final SimpleHandler handler;

	/** Mutable user properties stored on client workstation */
	private final UserProperties user_props;

	/** Menu bar */
	private final IMenuBar menu_bar;

	/** Login session information */
	private Session session;

	/** Get the current user session */
	public Session getSession() {
		return session;
	}

	/** Create a new Iris client */
	public IrisClient(Properties props, SimpleHandler h) {
		super(createTitle(I18N.get("iris.logged.out")));
		this.props = props;
		handler = h;
		user_props = new UserProperties(h);
		Widgets.init(user_props.getScale());
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		screens = Screen.getAllScreens();
		s_panes = new ScreenPane[screens.length];
		desktop = new SmartDesktop(screens[0], this);
		initializeScreenPanes();
		layout = new ScreenLayout(desktop);
		getContentPane().add(desktop);
		menu_bar = new IMenuBar(this, desktop);
		setMenuBar();
		autoLogin();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				doQuit();
			}
		});
	}

	/** Quit the IRIS client application */
	private void doQuit() {
		user_props.setWindowProperties(this);
		try {
			user_props.write();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	/** Quit the IRIS client */
	public void quit() {
		dispatchEvent(new WindowEvent(this,
			WindowEvent.WINDOW_CLOSING));
	}

	/** Get the currently selected tabs in each screen pane */
	public String[] getSelectedTabs() {
		String[] st = new String[s_panes.length];
		for(int i = 0; i < st.length; i++)
			st[i] = s_panes[i].getSelectedTabId();
		return st;
	}

	/** Initialize the screen panes */
	private void initializeScreenPanes() {
		for(int s = 0; s < s_panes.length; s++)
			s_panes[s] = new ScreenPane();
		for(ScreenPane sp: s_panes) {
			sp.addComponentListener(new ComponentAdapter() {
				public void componentHidden(ComponentEvent e) {
					arrangeTabs();
				}
				public void componentShown(ComponentEvent e) {
					arrangeTabs();
				}
			});
			desktop.add(sp, JLayeredPane.DEFAULT_LAYER);
		}
	}

	/** Make the frame displayable (called by window toolkit) */
	@Override public void addNotify() {
		super.addNotify();
		setPosition();
	}

	/** Set position of frame window using properties values */
	private void setPosition() {
		Rectangle r = user_props.getWindowPosition();
		if(r == null)
			r = Screen.getMaximizedBounds();
		setBounds(r);
		Integer ext = user_props.getWindowState();
		if(ext != null)
			setExtendedState(ext);
		getContentPane().validate();
	}

	/** Auto-login the user if enabled */
	private void autoLogin() {
		String user = props.getProperty("autologin.username");
		String pws = props.getProperty("autologin.password");
		if(user != null && pws != null) {
			char[] pwd = pws.toCharArray();
			pws = null;
			if(user.length() > 0 && pwd.length > 0)
				login(user, pwd);
		}
	}

	/** Get a list of all visible screen panes. Will return an empty
	 * list if IRIS is minimized. */
	private LinkedList<ScreenPane> getVisiblePanes() {
		LinkedList<ScreenPane> visible = new LinkedList<ScreenPane>();
		for(ScreenPane s: s_panes) {
			if(s.isVisible())
				visible.add(s);
		}
		return visible;
	}

	/** Arrange the tabs on the visible screen panes */
	private void arrangeTabs() {
		setMenuBar();
		removeTabs();
		Session s = session;
		if(s != null)
			arrangeTabs(s);
	}

	/** Set the menu bar to the first visible screen pane */
	private void setMenuBar() {
		IMenuBar mb = menu_bar;
		for(ScreenPane sp: getVisiblePanes()) {
			sp.setMenuBar(mb);
			mb = null;
		}
	}

	/** Arrange the tabs on the visible screen panes */
	private void arrangeTabs(Session s) {
		LinkedList<ScreenPane> visible = getVisiblePanes();
		if(visible.isEmpty())
			return;
		Iterator<ScreenPane> it = visible.iterator();
		for(MapTab mt: s.getTabs()) {
			if(!it.hasNext())
				it = visible.iterator();
			ScreenPane sp = it.next();
			sp.addTab(mt);
		}
		setSelectedTabs(s);
	}

	/** Set the selected tab in each screen pane */
	private void setSelectedTabs(Session s) {
		String[] st = user_props.getSelectedTabs();
		for(int i = 0; i < s_panes.length && i < st.length; i++) {
			MapTab mt = lookupTab(s, st[i]);
			if(mt != null)
				s_panes[i].setSelectedTab(mt);
		}
	}

	/** Lookup a map tab */
	private MapTab lookupTab(Session s, String tid) {
		for(MapTab mt: s.getTabs()) {
			if(mt.getTextId().equals(tid))
				return mt;
		}
		return null;
	}

	/** Show the login form */
	public void login() {
		if(session == null)
			desktop.show(new LoginForm(this, desktop));
	}

	/** Login a user */
	public void login(final String user, final char[] pwd) {
		LOGIN.addJob(new Job() {
			public void perform() throws Exception {
				doLogin(user, pwd);
			}
		});
	}

	/** Login a user */
	private void doLogin(String user, char[] pwd) throws Exception {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		setTitle(createTitle(I18N.get("iris.logging.in")));
		menu_bar.disableSessionMenu();
		closeSession();
		try {
			session = createSession(user, pwd);
		}
		finally {
			for(int i = 0; i < pwd.length; ++i)
				pwd[i] = ' ';
			menu_bar.setSession(session);
			updateMaps(session);
			arrangeTabs();
			setTitle(createTitle(session));
			setCursor(null);
			invalidate();
		}
	}

	/** Create a new session */
	private Session createSession(String user, char[] pwd) throws Exception{
		SonarState state = createSonarState();
		state.login(user, new String(pwd));
		if(state.isLoggedIn()) {
			state.populateCaches();
			Session s = new Session(state, desktop, props);
			s.initialize();
			return s;
		} else
			return null;
	}

	/** Create a new SONAR state */
	private SonarState createSonarState() throws IOException,
		SonarException, NoSuchFieldException, IllegalAccessException
	{
		return new SonarState(props, handler);
	}

	/** Update the maps on all screen panes */
	private void updateMaps(Session s) {
		for(ScreenPane sp: s_panes) {
			MapBean mb = sp.getMap();
			mb.setModel(createMapModel(mb, s));
			if(s != null)
				sp.createToolPanels(s);
			else
				sp.clearToolPanels();
		}
		setInitExtent();
	}

	/** Set initial map extent */
	public void setInitExtent() {
		MapExtent me = MapExtentHelper.lookup("Home");
		if(me != null) {
			for(ScreenPane sp: s_panes)
				sp.setMapExtent(me);
		}
 	}

	/** Set the map extent.
	 * @param zoom Zoom level.
	 * @param lat Latitude coordinate.
	 * @param lon Longitude coordinate. */
	public void setMapExtent(ZoomLevel zoom, float lat, float lon) {
		for(ScreenPane sp: s_panes)
			sp.setMapExtent(zoom, lat, lon);
	}

	/** Create a new map model */
	private MapModel createMapModel(MapBean mb, Session s) {
		MapModel mm = new MapModel();
		if(s != null)
			s.createLayers(mb, mm);
		return mm;
	}

	/** Get the map model for the first screen pane */
	public MapModel getMapModel() {
		for(ScreenPane sp: s_panes)
			return sp.getMap().getModel();
		return null;
	}

	/** Logout of the current session */
	public void logout() {
		LOGIN.addJob(new Job() {
			public void perform() {
				doLogout();
			}
		});
	}

	/** Clean up when the user logs out */
	private void doLogout() {
		user_props.setWindowProperties(this);
		menu_bar.setSession(null);
		removeTabs();
		closeSession();
		updateMaps(null);
		setTitle(createTitle(I18N.get("iris.logged.out")));
		invalidate();
	}

	/** Close the session */
	private void closeSession() {
		Session s = session;
		if(s != null)
			s.dispose();
		session = null;
	}

	/** Removed all the tabs */
	private void removeTabs() {
		for(ScreenPane sp: s_panes)
			sp.removeTabs();
	}

	/** Set the point selector for all map beans */
	public void setPointSelector(PointSelector ps) {
		for(ScreenPane sp: getVisiblePanes())
			sp.getMap().setPointSelector(ps);
	}
}
