/**
 * Copyright 2010 Gerhard Aigner
 * 
 * This file is part of BRISS.
 * 
 * BRISS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * BRISS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * BRISS. If not, see http://www.gnu.org/licenses/.
 */
package at.laborg.briss.gui;

import java.awt.Dialog;
import java.awt.Frame;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class HelpDialog extends JDialog {

	private static final String HELP_FILE_PATH = "/help.html";

	public HelpDialog(final Frame owner, final String title,
			final Dialog.ModalityType modalityType) {
		super(owner, title, modalityType);
		setBounds(232, 232, 500, 800);

		String helpText = "";

		InputStream is = getClass().getResourceAsStream(HELP_FILE_PATH);
		byte[] buf = new byte[1024 * 100];
		try {
			int cnt = is.read(buf);
			helpText = new String(buf, 0, cnt);
		} catch (IOException e) {
			helpText = "Couldn't read the help file... Please contact gerhard.aigner@gmail.com";
		}

		JEditorPane jEditorPane = new JEditorPane("text/html", helpText);
		jEditorPane.setEditable(false);
		jEditorPane.setVisible(true);

		JScrollPane scroller = new JScrollPane(jEditorPane);
		getContentPane().add(scroller);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

}
