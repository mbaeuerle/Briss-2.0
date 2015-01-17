package at.laborg.briss.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;

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
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				okPressed();
			}
		});
	}

	private void okPressed() {

	}

}
