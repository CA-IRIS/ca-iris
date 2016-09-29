/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2016  Minnesota Department of Transportation
 * Copyright (C) 2010-2015 AHMCT, University of California, Davis
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
import java.util.Iterator;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import us.mn.state.dot.sonar.User;
import us.mn.state.dot.tms.BitmapGraphic;
import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.DMS;
import us.mn.state.dot.tms.DMSHelper;
import us.mn.state.dot.tms.DMSMessagePriority;
import us.mn.state.dot.tms.InvalidMessageException;
import us.mn.state.dot.tms.RasterBuilder;
import us.mn.state.dot.tms.RasterGraphic;
import us.mn.state.dot.tms.SignMessage;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.WordHelper;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxySelectionListener;
import us.mn.state.dot.tms.client.proxy.ProxySelectionModel;
import us.mn.state.dot.tms.utils.Base64;
import us.mn.state.dot.tms.utils.I18N;
import us.mn.state.dot.tms.utils.MultiString;

/**
 * The DMSDispatcher is a GUI component for creating and deploying DMS messages.
 * It contains several other components and keeps their state synchronized.
 * @see SignMessage, DMSPanelPager, SignMessageComposer
 *
 * @author Erik Engstrom
 * @author Douglas Lau
 * @author Michael Darter
 * @author Travis Swanston
 */
public class DMSDispatcher extends JPanel {

	/** User session */
	private final Session session;

	/** Currently logged in user */
	private final User user;

	/** Selection model */
	private final ProxySelectionModel<DMS> sel_model;

	/** Selection listener */
	private final ProxySelectionListener sel_listener =
		new ProxySelectionListener()
	{
		public void selectionChanged() {
			doSelectionChanged();
		}
	};

	/** Sign message creator */
	private final SignMessageCreator creator;

	/** Selection tab pane */
	private final JTabbedPane tabPane = new JTabbedPane();

	/** Single sign tab */
	private final SingleSignTab singleTab;

	/** Multiple sign tab */
	private final MultipleSignTab multipleTab;

	/** Message composer widget */
	private final SignMessageComposer composer;

	/** Raster graphic builder */
	private RasterBuilder builder;

	/** Selected message MULTI string */
	private String message = "";

	/** Create a new DMS dispatcher */
	public DMSDispatcher(Session s, DMSManager manager) {
		super(new BorderLayout());
		session = s;
		DmsCache dms_cache = session.getSonarState().getDmsCache();
		user = session.getUser();
		creator = new SignMessageCreator(s, user);
		sel_model = manager.getSelectionModel();
		singleTab = new SingleSignTab(session, this);
		multipleTab = new MultipleSignTab(dms_cache, sel_model);
		composer = new SignMessageComposer(session, this, manager);
		tabPane.addTab(I18N.get("dms.single"), singleTab);
		tabPane.addTab(I18N.get("dms.multiple"), multipleTab);
		add(tabPane, BorderLayout.CENTER);
		add(composer, BorderLayout.SOUTH);
	}

	/** Initialize the dispatcher */
	public void initialize() {
		singleTab.initialize();
		multipleTab.initialize();
		sel_model.addProxySelectionListener(sel_listener);
		clearSelected();
	}

	/** Dispose of the dispatcher */
	public void dispose() {
		sel_model.removeProxySelectionListener(sel_listener);
		clearSelected();
		removeAll();
		singleTab.dispose();
		multipleTab.dispose();
		composer.dispose();
	}

	/** Get a list of the selected DMS */
	private Set<DMS> getValidSelected() {
		Set<DMS> sel = sel_model.getSelected();
		Iterator<DMS> it = sel.iterator();
		while (it.hasNext()) {
			DMS dms = it.next();
			if (!checkDimensions(dms))
				it.remove();
		}
		return sel;
	}

	/** Check the dimensions of a sign against the pixel map builder */
	private boolean checkDimensions(DMS dms) {
		RasterBuilder b = builder;
		if(b != null) {
			Integer w = dms.getWidthPixels();
			Integer h = dms.getHeightPixels();
			if(w != null && h != null)
				return b.width == w && b.height == h;
		}
		return false;
	}

	/** Send the currently selected message */
	public void sendSelectedMessage() {
		if (shouldSendMessage()) {
			sendMessage();
			removeInvalidSelections();
		}
	}

	/** Remove all invalid selected DMS */
	private void removeInvalidSelections() {
		sel_model.setSelected(getValidSelected());
	}

	/** Determine if the message should be sent, which is a function
 	 * of spell checking options and send confirmation options.
	 * @return True to send the message else false to cancel. */
	private boolean shouldSendMessage() {
		if (WordHelper.spellCheckEnabled())
			if (!checkWords(message))
				return false;
		if (SystemAttrEnum.DMS_SEND_CONFIRMATION_ENABLE.getBoolean())
			return showConfirmDialog();
		else
			return true;
	}

	/** Check all the words in the specified MULT string.
	 * @param multi Multi string to spell check.
	 * @return True to send the sign message else false to cancel. */
	private static boolean checkWords(String multi) {
		String msg = WordHelper.spellCheck(multi);
		String amsg = WordHelper.abbreviationCheck(multi);
		if(msg.isEmpty() && amsg.isEmpty()) {
			return true;
		} else if (msg.isEmpty()) {
			Object[] options = {"Send", "Cancel"};
			int sel = JOptionPane.showOptionDialog(null, msg + amsg, 
				"Spell Check", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, 
				options, options[1]);
			return (sel == JOptionPane.OK_OPTION);
		} if (WordHelper.spellCheckEnforced()) {
			JOptionPane.showMessageDialog(null, msg + amsg, 
				"Spell Check", JOptionPane.ERROR_MESSAGE);
			return false;
		} else if (WordHelper.spellCheckRecommend()) {
			Object[] options = {"Send", "Cancel"};
			int sel = JOptionPane.showOptionDialog(null, msg + amsg, 
				"Spell Check", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, 
				options, options[1]);
			return (sel == JOptionPane.OK_OPTION);
		} else {
			return false;
		}
	}

	/** Show a message confirmation dialog.
	 * @return True if message should be sent. */
	private boolean showConfirmDialog() {
		String m = buildConfirmMsg();
		if(!m.isEmpty()) {
			return 0 == JOptionPane.showConfirmDialog(null, m, 
				I18N.get("dms.send.confirmation.title"),
				JOptionPane.OK_CANCEL_OPTION);
		} else
			return false;
	}

	/** Build a confirmation message containing all selected DMS.
	 * @return An empty string if no DMS selected else the message. */
	private String buildConfirmMsg() {
		String sel = buildSelectedList();
		if(sel.isEmpty())
			return sel;
		else {
			return I18N.get("dms.send.confirmation.msg") + " " +
				sel + "?";
		}
	}

	/** Build a string of selected DMS */
	private String buildSelectedList() {
		boolean first = true;
		StringBuilder sb = new StringBuilder();
		for (DMS dms: getValidSelected()) {
			if (!first)
				sb.append(", ");
			sb.append(dms.getName());
			first = false;
		}
		return sb.toString();
	}

	/** Get page prefix MULTI string from scheduled message (if any) */
	public String getPagePrefix() {
		DMS dms = sel_model.getSingleSelection();
		if (dms != null) {
			SignMessage sm = dms.getMessageSched();
			if(sm != null && sm.getActivationPriority() ==
			   DMSMessagePriority.PREFIX_PAGE.ordinal())
				return sm.getMulti();
		}
		return "";
	}

	/** Send a new message to the selected DMS */
	private void sendMessage() {
		Set<DMS> sel = getValidSelected();
		if (sel.size() > 0) {
			SignMessage sm = createMessage();
			if (sm != null) {
				for (DMS dms: sel) {
					dms.setOwnerNext(user);
					dms.setMessageNext(sm);
				}
			}
			if (sel.size() == 1)
				composer.updateMessageLibrary();
			selectPreview(false);
		}
	}

	/** Create a new message from the widgets.
	 * @return A newly created SignMessage else null. */
	private SignMessage createMessage() {
		String multi = message;	// Avoid races
		if(multi.isEmpty())
			return null;
		else
			return createMessage(multi);
	}

	/** Create a new message using the specified MULTI */
	private SignMessage createMessage(String multi) {
		String bitmaps = createBitmaps(multi);
		if(bitmaps != null) {
			boolean be = composer.isBeaconEnabled();
			DMSMessagePriority p = composer.getPriority();
			Integer d = composer.getDuration();
			return creator.create(multi, be, bitmaps, p, p, d);
		} else
			return null;
	}

	/** Blank the select DMS */
	public void sendBlankMessage() {
		Set<DMS> sel = sel_model.getSelected();
		if (sel.size() > 0) {
			SignMessage sm = createBlankMessage();
			if(sm != null) {
				for(DMS dms: sel) {
					dms.setOwnerNext(user);
					dms.setMessageNext(sm);
				}
			}
		}
	}

	/** Create a new blank message */
	private SignMessage createBlankMessage() {
		String multi = "";
		String bitmaps = createBitmaps(multi);
		if(bitmaps != null) {
			return creator.create(multi, false, bitmaps,
			       DMSMessagePriority.OVERRIDE,
			       DMSMessagePriority.BLANK, null);
		} else
			return null;
	}

	/** Create bitmap graphics for a MULTI string */
	private String createBitmaps(String multi) {
		RasterBuilder b = builder;
		if (b != null) {
			MultiString ms = new MultiString(multi);
			try {
				return encodeBitmaps(b.createBitmaps(ms));
			}
			catch (InvalidMessageException e) {
				// Message is not valid
			}
		}
		return null;
	}

	/** Encode the bitmaps to Base64 */
	private String encodeBitmaps(BitmapGraphic[] bmaps) {
		int blen = bmaps[0].length();
		byte[] bitmaps = new byte[bmaps.length * blen];
		for(int i = 0; i < bmaps.length; i++) {
			byte[] pix = bmaps[i].getPixelData();
			System.arraycopy(pix, 0, bitmaps, i * blen, blen);
		}
		return Base64.encode(bitmaps);
	}

	/** Query the current message on all selected signs */
	public void queryMessage() {
		for (DMS dms: sel_model.getSelected()) {
			dms.setDeviceRequest(
				DeviceRequest.QUERY_MESSAGE.ordinal());
		}
		selectPreview(false);
	}

	/** Called whenever the selection is changed */
	private void doSelectionChanged() {
		if (!areBuilderAndComposerValid()) {
			builder = null;
			for (DMS s: sel_model.getSelected()) {
				createBuilder(s);
				break;
			}
		}
		updateSelected();
	}

	/** Check if the builder is valid for at least one selected DMS */
	private boolean areBuilderAndComposerValid() {
		Set<DMS> sel = sel_model.getSelected();
		// If there is only one DMS selected, then the
		// composer needs to be updated for that sign.
		if (sel.size() > 1) {
			for (DMS dms: sel) {
				if (checkDimensions(dms))
					return true;
			}
		}
		return false;
	}

	/** Create a pixel map builder */
	private void createBuilder(DMS dms) {
		builder = DMSHelper.createRasterBuilder(dms);
		composer.setSign(dms, builder);
	}

	/** Update the selected sign(s) */
	private void updateSelected() {
		Set<DMS> sel = sel_model.getSelected();
		if (sel.size() == 0)
			clearSelected();
		else if (sel.size() == 1) {
			for (DMS dms: sel)
				setSelected(dms);
		} else {
			singleTab.setSelected(null);
			setEnabled(true);
			selectMultipleTab();
		}
	}

	/** Clear the selection */
	private void clearSelected() {
		setEnabled(false);
		composer.setSign(null, null);
		setMessage("");
		singleTab.setSelected(null);
		selectSingleTab();
	}

	/** Set a single selected DMS */
	private void setSelected(DMS dms) {
		if(DMSHelper.isActive(dms)) {
			setEnabled(true);
			SignMessage sm = dms.getMessageCurrent();
			if(sm != null)
				setMessage(sm.getMulti());
		} else
			setEnabled(false);
		singleTab.setSelected(dms);
		selectSingleTab();
	}

	/** Select the single selection tab */
	private void selectSingleTab() {
		if(tabPane.getSelectedComponent() != singleTab)
			tabPane.setSelectedComponent(singleTab);
		composer.setMultiple(false);
	}

	/** Select the multiple selection tab */
	private void selectMultipleTab() {
		if(tabPane.getSelectedComponent() != multipleTab)
			tabPane.setSelectedComponent(multipleTab);
		composer.setMultiple(true);
	}

	/** Set the enabled status of the dispatcher */
	public void setEnabled(boolean e) {
		composer.setEnabled(e && canSend());
		if(e)
			selectPreview(false);
	}

	/** Set the fully composed message.  This will update all the widgets
	 * on the dispatcher with the specified message. */
	public void setMessage(String ms) {
		if(ms != null && !ms.equals(message)) {
			message = ms;
			singleTab.setMessage();
			composer.setMessage(ms);
		}
	}

	/** Get the selected message */
	public String getMessage() {
		return message;
	}

	/** Select the preview mode */
	public void selectPreview(boolean p) {
		singleTab.selectPreview(p);
	}

	/**
	 * Set the single sign tab's panel pager to the given page.
	 * @param p The desired page number
	 * @return true if successful, else false
	 */
	public boolean setSingleSignPage(int p) {
		return singleTab.setPagerPage(p);
	}

	/** Get raster graphic array for the selected message */
	public RasterGraphic[] getPixmaps() {
        return getPixmaps(builder);
	}

	/** Get raster graphic array for the selected message */
	private RasterGraphic[] getPixmaps(RasterBuilder b) {
		if (b != null) {
			MultiString multi = new MultiString(message);
			try {
				return b.createPixmaps(multi);
			}
			catch (IndexOutOfBoundsException e) {
				// pixmap too small for message
			}
			catch (InvalidMessageException e) {
				// most likely a MultiSyntaxError ...
			}
		}
		return null;
	}

	/** Can a message be sent to all selected DMS? */
	public boolean canSend() {
	    return canSend(false);
    }

	/** Can a message be sent to all selected DMS? */
	public boolean canSend(boolean checkMsg) {
		Set<DMS> sel = getValidSelected();
		if (sel.isEmpty())
			return false;
		for (DMS dms: sel) {
			if (!canSend(dms, checkMsg))
				return false;
		}
		return true;
	}

	/** Can a message be sent to the specified DMS? */
	private boolean canSend(DMS dms, boolean checkMsg) {
		return creator.canCreate() &&
			isUpdatePermitted(dms, "ownerNext") &&
			isUpdatePermitted(dms, "messageNext") &&
			(!checkMsg || isMsgValidOnSign(dms));
	}

	/** Is DMS attribute update permitted? */
	private boolean isUpdatePermitted(DMS dms, String a) {
		return session.isUpdatePermitted(dms, a);
	}

	/** Determine whether given message is valid on given sign. */
	private boolean isMsgValidOnSign(DMS dms) {
		// NOTE: This could be optimized if rendering for each check
		// becomes too expensive.
		// This optimization would require decoupling the validation in
		// RasterBuilder from the render process.
		RasterBuilder builder = DMSHelper.createRasterBuilder(dms);
		return getPixmaps(builder) != null;
	}

	/** Can a device request be sent to all selected DMS? */
	public boolean canRequest() {
		Set<DMS> sel = sel_model.getSelected();
		if (sel.isEmpty())
			return false;
		for (DMS dms: sel) {
			if (!canRequest(dms))
				return false;
		}
		return true;
	}

	/** Can a device request be sent to the specified DMS? */
	public boolean canRequest(DMS dms) {
		return isUpdatePermitted(dms, "deviceRequest");
	}

	/** Check if AWS is allowed and user has permission to change */
	public boolean isAwsPermitted(DMS dms) {
		return dms.getAwsAllowed() &&
			isUpdatePermitted(dms, "awsControlled");
	}
}
