package at.laborg.briss.gui;

import javax.swing.*;

public class ClusterConfigurationDialog extends JDialog {

	private static final long serialVersionUID = -918825385363863390L;
	private static final String EVEN_ODD_QUESTION = "Cluster even and odd pages differently";
	private static final String OK_STRING = "Crop it!";
	private JCheckBox evenAndOddChecker;
	private JButton okButton;

	public ClusterConfigurationDialog() {
		initUI();
	}

	private void initUI() {
		evenAndOddChecker = new JCheckBox(EVEN_ODD_QUESTION, true);
		okButton = new JButton(OK_STRING);
		this.add(evenAndOddChecker);
		this.add(okButton);
		okButton.addActionListener(e -> okPressed());
	}

	private void okPressed() {
	}
}
