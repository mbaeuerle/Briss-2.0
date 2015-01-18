package at.laborg.briss.wizard;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

public class Wizard extends JPanel implements ActionListener {

	private static final long serialVersionUID = 3782971195362578336L;

	private List<AbstractWizardPage> wizardPages;

	private int currentPageIndex;

	private JPanel pagePanel;

	private JButton backButton;
	private JButton nextButton;

	private boolean isInvisibleInsteadOfDisabled = false;

	public Wizard() {
		wizardPages = new ArrayList<AbstractWizardPage>();
		currentPageIndex = 0;
		initComponents();
	}

	public void setButtonsInvisibleInsteadOfDisabled(boolean buttonsInvisibleInsteadOfDisabled) {
		if (buttonsInvisibleInsteadOfDisabled) {
			backButton.setEnabled(true);
			nextButton.setEnabled(true);
		} else {
			backButton.setVisible(true);
			nextButton.setVisible(true);
		}
		isInvisibleInsteadOfDisabled = buttonsInvisibleInsteadOfDisabled;
		updateButtonStates();
	}

	private void initComponents() {
		JPanel buttonPanel = new JPanel();
		Box buttonBox = new Box(BoxLayout.X_AXIS);

		pagePanel = new JPanel();
		pagePanel.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));

		backButton = new JButton("Back");
		backButton.addActionListener(this);

		nextButton = new JButton("Next");
		nextButton.addActionListener(this);

		updateButtonStates();

		buttonPanel.setLayout(new BorderLayout());
		buttonPanel.add(new JSeparator(), BorderLayout.NORTH);

		buttonBox.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));
		buttonBox.add(backButton);
		buttonBox.add(Box.createHorizontalStrut(10));
		buttonBox.add(nextButton);

		buttonPanel.add(buttonBox, java.awt.BorderLayout.EAST);

		setLayout(new BorderLayout());

		add(buttonPanel, BorderLayout.SOUTH);
		add(pagePanel, BorderLayout.CENTER);
		setVisible(false);
	}

	public void addWizardPage(AbstractWizardPage wizardPage) {
		wizardPages.add(wizardPage);
		updateButtonStates();
	}

	protected void setBackButtonEnabled(boolean b) {
		if (isInvisibleInsteadOfDisabled) {
			backButton.setVisible(b);
		} else {
			backButton.setEnabled(b);
		}
	}

	protected void setNextButtonEnabled(boolean b) {
		if (isInvisibleInsteadOfDisabled) {
			nextButton.setVisible(b);
		} else {
			nextButton.setEnabled(b);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(backButton)) {
			switchToPreviousPage();
		} else if (e.getSource().equals(nextButton)) {
			switchToNextPage();
		}
		updateButtonStates();
	}

	private void updateButtonStates() {
		setBackButtonEnabled(hasPreviousPage());
		setNextButtonEnabled(hasNextPage());
	}

	private boolean hasPreviousPage() {
		return currentPageIndex > 0;
	}

	private boolean hasNextPage() {
		return currentPageIndex < wizardPages.size() - 1;
	}

	private void switchToNextPage() {
		if (hasNextPage()) {
			switchToPage(true);
		}
	}

	private void switchToPreviousPage() {
		if (hasPreviousPage()) {
			switchToPage(false);
		}
	}

	private void switchToPage(boolean next) {
		int indexModifier = next ? 1 : -1;

		AbstractWizardPage wizardPage = wizardPages.get(currentPageIndex);
		wizardPage.aboutToHidePanel();
		currentPageIndex += indexModifier;
		showCurrentPage();
	}

	public void showWizard() {
		if (wizardPages.isEmpty()) {
			throw new IllegalStateException("Wizard has no page to show.");
		}
		showCurrentPage();
		setVisible(true);
	}

	private void showCurrentPage() {
		AbstractWizardPage newWizardPage = wizardPages.get(currentPageIndex);
		newWizardPage.aboutToDisplayPanel();

		pagePanel.removeAll();
		pagePanel.add(newWizardPage.getContentPanel());

		validate();
		repaint();
	}
}
