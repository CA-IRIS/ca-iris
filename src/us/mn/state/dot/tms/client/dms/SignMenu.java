/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2013  Minnesota Department of Transportation
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
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.warning.WarningSignForm;
import us.mn.state.dot.tms.client.widget.AbstractForm;
import us.mn.state.dot.tms.client.widget.IAction2;
import us.mn.state.dot.tms.client.widget.SmartDesktop;
import us.mn.state.dot.tms.utils.I18N;

/**
 * SignMenu is a menu for DMS-related items.
 *
 * @author Douglas Lau
 */
public class SignMenu extends JMenu {

	/** User Session */
	private final Session session;

	/** Desktop */
	private final SmartDesktop desktop;

	/** Create a new sign menu */
	public SignMenu(final Session s) {
		super(I18N.get("sign.menu"));
		session = s;
		desktop = s.getDesktop();
		JMenuItem item = createDmsItem();
		if(item != null)
			add(item);
		item = createFontItem();
		if(item != null)
			add(item);
		item = createGraphicItem();
		if(item != null)
			add(item);
		item = createQuickMessageItem();
		if(item != null)
			add(item);
		item = createWarningSignItem();
		if(item != null)
			add(item);
	}

	/** Create the DMS menu item */
	private JMenuItem createDmsItem() {
		if(!DMSForm.isPermitted(session))
			return null;
		return new JMenuItem(new IAction2("dms") {
			protected void doActionPerformed(ActionEvent e) {
				desktop.show(createDMSForm());
			}
		});
	}

	/** Create the DMS form */
	private AbstractForm createDMSForm() {
		if(SystemAttrEnum.DMS_FORM.getInt() == 2)
			return new DMSForm2(session);
		else
			return new DMSForm(session);
	}

	/** Create the font menu item */
	private JMenuItem createFontItem() {
		if(!FontForm.isPermitted(session))
			return null;
		return new JMenuItem(new IAction2("font.title") {
			protected void doActionPerformed(ActionEvent e) {
				desktop.show(new FontForm(session));
			}
		});
	}

	/** Create the graphics menu item */
	private JMenuItem createGraphicItem() {
		if(!GraphicForm.isPermitted(session))
			return null;
		return new JMenuItem(new IAction2("graphics") {
			protected void doActionPerformed(ActionEvent e) {
				desktop.show(new GraphicForm(session));
			}
		});
	}

	/** Create the quick message menu item */
	private JMenuItem createQuickMessageItem() {
		if(!QuickMessageForm.isPermitted(session))
			return null;
		return new JMenuItem(new IAction2("quick.messages") {
			protected void doActionPerformed(ActionEvent e) {
				desktop.show(new QuickMessageForm(session));
			}
		});
	}

	/** Create the warning sign menu item */
	private JMenuItem createWarningSignItem() {
		if(!WarningSignForm.isPermitted(session))
			return null;
		return new JMenuItem(new IAction2("warning.signs") {
			protected void doActionPerformed(ActionEvent e) {
				desktop.show(new WarningSignForm(session));
			}
		});
	}
}
