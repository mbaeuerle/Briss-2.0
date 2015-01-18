package at.laborg.briss.wizard;

import javax.swing.JPanel;

public abstract class AbstractWizardPage extends JPanel {

	private static final long serialVersionUID = 8497991822100752358L;

	public void aboutToDisplayPanel() {

		// Place code here that will be executed before the
		// panel is displayed.

	}

	public void displayingPanel() {

		// Place code here that will be executed when the
		// panel is displayed.

	}

	public void aboutToHidePanel() {

		// Place code here that will be executed when the
		// panel is hidden.

	}

	protected abstract JPanel getContentPanel();
}
