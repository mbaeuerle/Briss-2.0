package at.laborg.briss.wizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import at.laborg.briss.Messages;

public class WizardTest {

	public static void main(String[] args) {
		setUILook();
		Wizard wiz = new Wizard();
		JFrame frame = new JFrame();

		frame.setBounds(300, 300, 800, 600);
		frame.setPreferredSize(new Dimension(800, 600));
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.add(wiz, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);

		wiz.addWizardPage(new AbstractWizardPage() {

			@Override
			protected JPanel getContentPanel() {
				JLabel label = new JLabel("1. Seite");
				JPanel panel = new JPanel();
				panel.setBackground(Color.WHITE);
				panel.add(label);
				return panel;
			}
		});
		wiz.addWizardPage(new AbstractWizardPage() {

			@Override
			protected JPanel getContentPanel() {
				JLabel label = new JLabel("2. Seite");
				JPanel panel = new JPanel();
				panel.setBackground(Color.BLUE);
				panel.add(label);
				return panel;
			}
		});

		wiz.addWizardPage(new AbstractWizardPage() {

			@Override
			protected JPanel getContentPanel() {
				JLabel label = new JLabel("letzte Seite");
				JPanel panel = new JPanel();
				panel.setBackground(Color.GREEN);
				panel.add(label);
				return panel;
			}
		});

		// wiz.setButtonsInvisibleInsteadOfDisabled(true);
		wiz.showWizard();

	}

	private static void setUILook() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException ex) {
			System.out.println(Messages.getString("BrissGUI.lookAndFeelError")); //$NON-NLS-1$
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
	}

}
