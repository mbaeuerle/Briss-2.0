// $Id: BrissGUI.java 70 2012-05-26 16:20:19Z laborg $
/**
 * Copyright 2010, 2011 Gerhard Aigner, Rastislav Wartiak
 * <p>
 * This file is part of BRISS.
 * <p>
 * BRISS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * <p>
 * BRISS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * BRISS. If not, see http://www.gnu.org/licenses/.
 */
package at.laborg.briss;

import at.laborg.briss.exception.CropException;
import at.laborg.briss.gui.HelpDialog;
import at.laborg.briss.gui.MergedPanel;
import at.laborg.briss.gui.WrapLayout;
import at.laborg.briss.model.ClusterDefinition;
import at.laborg.briss.model.CropDefinition;
import at.laborg.briss.model.PageCluster;
import at.laborg.briss.model.PageExcludes;
import at.laborg.briss.model.WorkingSet;
import at.laborg.briss.utils.BrissFileHandling;
import at.laborg.briss.utils.ClusterCreator;
import at.laborg.briss.utils.ClusterRenderWorker;
import at.laborg.briss.utils.DesktopHelper;
import at.laborg.briss.utils.DocumentCropper;
import at.laborg.briss.utils.FileDrop;
import at.laborg.briss.utils.PageNumberParser;
import at.laborg.briss.utils.PDFReaderUtil;
import com.itextpdf.text.DocumentException;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import org.jpedal.exception.PdfException;

/**
 * @author gerhard, hybridtupel
 */
public class BrissSwingGUI implements BrissGUIApp {
	private final JFrame mainWindow;

	private static final int DEFAULT_HEIGHT = 600;
	private static final int DEFAULT_WIDTH = 800;
	private static final int MIN_HEIGHT = 400;
	private static final int MIN_WIDTH = 400;

	private static final String DONATION_URI = "http://sourceforge.net/project/project_donations.php?group_id=320676"; //$NON-NLS-1$
	private static final String RES_ICON_PATH = "Briss_icon_032x032.gif"; // $NON-NLS-1$
	private static final String RES_DROP_IMG_PATH = "drop.png"; // $NON-NLS-1$
	private static final String PROGRESS = "progress";
	private static final AncestorListener FOCUS_REQUESTING_ANCESTOR_LISTENER = new AncestorListener() {

		@Override
		public void ancestorRemoved(AncestorEvent event) {
		}

		@Override
		public void ancestorMoved(AncestorEvent event) {
		}

		@Override
		public void ancestorAdded(AncestorEvent event) {
			JComponent component = event.getComponent();

			component.requestFocusInWindow();

			component.removeAncestorListener(this);
		}
	};

	private JPanel previewPanel;
	private JProgressBar progressBar;
	private List<MergedPanel> mergedPanels = new ArrayList<>();

	private File lastOpenDir = new File(System.getProperty("user.home"));

	private WorkingSet workingSet;

	// Use AWT FileDialog for native file chooser
	private final FileDialog fileChooser;
	private DragAndDropPanel dndPanel;
	private JScrollPane scrollPane;
	private CardLayout cardLayout;
	private JPanel wrapperPanel;
	private JButton showPreview;
	private JButton startCropping;

	public BrissSwingGUI(String[] args) {
		mainWindow = new JFrame(Messages.getString("BrissGUI.windowTitle")); // $NON-NLS-1$
		init();
		tryToLoadFileFromArgument(args);

		fileChooser = new FileDialog(mainWindow, "Open", FileDialog.LOAD);
		initFileChooser();
	}

	private void initFileChooser() {
		fileChooser.setDirectory(lastOpenDir.getAbsolutePath());
		fileChooser.setFilenameFilter(((dir, name) -> name.endsWith(".pdf")));
	}

	private void tryToLoadFileFromArgument(String[] args) {
		if (args.length == 0)
			return;
		File fileArg = new File(args[0]);
		if (fileArg.exists() && fileArg.getAbsolutePath().trim().endsWith(".pdf")) { // $NON-NLS-1$
			try {
				importNewPdfFile(fileArg);
			} catch (IOException | PdfException e) {
				JOptionPane.showMessageDialog(mainWindow, e.getMessage(), Messages.getString("BrissGUI.brissError"),
						JOptionPane.ERROR_MESSAGE); // $NON-NLS-1$
			}
		}
	}

	private void init() {
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		mainWindow.setTransferHandler(new BrissTransferHandler(this));

		setUILook();

		loadAppIcon();

		createMenuBar();

		MouseAdapter mousePressedAdapter = createMousePressedAdapter();

		previewPanel = new JPanel();
		previewPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 4, 4));
		previewPanel.setEnabled(true);
		previewPanel.setBackground(new Color(186, 186, 186));

		ComponentAdapter previewPanelComponentListener = new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				previewPanel.revalidate();
				for (Component component : previewPanel.getComponents()) {
					component.repaint();
				}
			}
		};

		previewPanel.addComponentListener(previewPanelComponentListener);

		previewPanel.addMouseListener(mousePressedAdapter);

		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(true);
		progressBar.setPreferredSize(new Dimension(400, 30));
		progressBar.setVisible(false);

		startCropping = new JButton("Crop PDF");
		startCropping.setVisible(false);
		startCropping.addActionListener(a -> startCropping());

		showPreview = new JButton(("Preview"));
		showPreview.addActionListener(a -> showPreview());
		showPreview.setVisible(false);

		scrollPane = new JScrollPane(previewPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVisible(true);
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setUnitIncrement(30);

		dndPanel = new DragAndDropPanel(loadDragAndDropLabelImage(), e -> showOpenFileDialog());
		new FileDrop(dndPanel, true, files -> {
			if (files.length == 1) {
				File file = files[0];
				if (file.getName().toLowerCase().endsWith(".pdf")) {
					loadPDF(file);
				}
			}
		});

		cardLayout = new CardLayout();
		wrapperPanel = new JPanel(cardLayout);
		wrapperPanel.add(scrollPane, "scroll");
		wrapperPanel.add(dndPanel, "dnd");
		mainWindow.add(wrapperPanel, BorderLayout.CENTER);

		cardLayout.last(wrapperPanel);

		JPanel footer = new JPanel();
		footer.add(progressBar);
		footer.add(showPreview);
		footer.add(startCropping);
		mainWindow.add(footer, BorderLayout.PAGE_END);

		mainWindow.addMouseListener(mousePressedAdapter);

		setWindowBounds();
		mainWindow.pack();
		mainWindow.setVisible(true);
	}

	private void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu(Messages.getString("BrissGUI.file")); // $NON-NLS-1$
		fileMenu.setMnemonic(KeyEvent.VK_F);
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);

		menuBar.add(fileMenu);
		menuBar.add(helpMenu);

		JMenuItem loadButton = new JMenuItem(Messages.getString("BrissGUI.loadFile"), KeyEvent.VK_L); // $NON-NLS-1$
		loadButton = new JMenuItem(Messages.getString("BrissGUI.loadFile"), KeyEvent.VK_L); // $NON-NLS-1$
		loadButton.setEnabled(true);
		loadButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, 0));
		loadButton.addActionListener(a -> showOpenFileDialog());
		fileMenu.add(loadButton);

		fileMenu.addSeparator();

		JMenuItem exitButton = new JMenuItem(Messages.getString("BrissGUI.exit"), KeyEvent.VK_E); // $NON-NLS-1$
		exitButton = new JMenuItem(Messages.getString("BrissGUI.exit"), KeyEvent.VK_E); // $NON-NLS-1$
		exitButton.addActionListener(a -> System.exit(0));
		fileMenu.add(exitButton);

		JMenuItem openDonationLinkButton = new JMenuItem(Messages.getString("BrissGUI.donate")); // $NON-NLS-1$
		openDonationLinkButton = new JMenuItem(Messages.getString("BrissGUI.donate")); // $NON-NLS-1$
		openDonationLinkButton.addActionListener(a -> {
			try {
				if (Desktop.isDesktopSupported()) {
					Desktop.getDesktop().browse(URI.create(DONATION_URI));
				}
			} catch (IOException e) {
				// Ignore error
				e.printStackTrace();
			}
		});
		helpMenu.add(openDonationLinkButton);

		JMenuItem showHelpButton = new JMenuItem(Messages.getString("BrissGUI.showHelp")); // $NON-NLS-1$
		showHelpButton = new JMenuItem(Messages.getString("BrissGUI.showHelp")); // $NON-NLS-1$
		showHelpButton.addActionListener(a -> new HelpDialog(mainWindow, Messages.getString("BrissGUI.brissHelp"),
				Dialog.ModalityType.MODELESS));
		helpMenu.add(showHelpButton);

		mainWindow.setJMenuBar(menuBar);
	}

	private void startCropping() {
		showSaveFileDialog();
	}

	private void setWindowBounds() {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int screenWidth = gd.getDisplayMode().getWidth();
		int screenHeight = gd.getDisplayMode().getHeight();
		mainWindow.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		mainWindow.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		mainWindow.setLocation(screenWidth / 2 - DEFAULT_WIDTH / 2, screenHeight / 2 - DEFAULT_HEIGHT / 2);
	}

	private void setUILook() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException ex) {
			System.out.println(Messages.getString("BrissGUI.lookAndFeelError")); // $NON-NLS-1$
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
		}
	}

	private void loadAppIcon() {
		ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(RES_ICON_PATH));
		mainWindow.setIconImage(icon.getImage());
	}

	private ImageIcon loadDragAndDropLabelImage() {
		return new ImageIcon(getClass().getClassLoader().getResource(RES_DROP_IMG_PATH));
	}

	private void showSaveFileDialog() {
		initFileChooser();
		fileChooser.setTitle("Save cropped PDF File");
		fileChooser.setMode(FileDialog.SAVE);
		fileChooser.setFile(BrissFileHandling.getRecommendedFileName(workingSet.getSourceFile()));
		fileChooser.setVisible(true);
		String filename = fileChooser.getFile();
		String directory = fileChooser.getDirectory();
		if (filename != null && directory != null) {
			SwingUtilities.invokeLater(() -> savePDF(new File(directory + filename)));
		}
	}

	private void showOpenFileDialog() {
		initFileChooser();
		fileChooser.setTitle("Open PDF File");
		fileChooser.setMode(FileDialog.LOAD);
        fileChooser.setFile("*.pdf");
		fileChooser.setVisible(true);
		String filename = fileChooser.getFile();
		String directory = fileChooser.getDirectory();
		if (filename != null && directory != null) {
			SwingUtilities.invokeLater(() -> loadPDF(new File(directory + filename)));
		}
	}

	private void loadPDF(File file) {
		try {
			importNewPdfFile(file);
			mainWindow.setTitle("BRISS - " + file.getName()); // $NON-NLS-1$
		} catch (IOException e) {
			JOptionPane.showMessageDialog(mainWindow, e.getMessage(), Messages.getString("BrissGUI."),
					JOptionPane.ERROR_MESSAGE); // $NON-NLS-1$
		} catch (PdfException e) {
			JOptionPane.showMessageDialog(mainWindow, e.getMessage(), Messages.getString("BrissGUI.loadingError"),
					JOptionPane.ERROR_MESSAGE); // $NON-NLS-1$
		}
	}

	private void savePDF(File file) {
		setWorkingState(Messages.getString("BrissGUI.loadingPDF")); // $NON-NLS-1$
		try {
			CropDefinition cropDefinition = CropDefinition.createCropDefinition(workingSet.getSourceFile(), file,
					workingSet.getClusterDefinition());
			File result = DocumentCropper.crop(cropDefinition, workingSet.getSourceFilePassword());
			if (result != null) {
				DesktopHelper.openFileWithDesktopApp(result);
				lastOpenDir = result.getParentFile();
			}

		} catch (IOException | DocumentException | CropException e) {
			JOptionPane.showMessageDialog(mainWindow, e.getMessage(), Messages.getString("BrissGUI.croppingError"),
					JOptionPane.ERROR_MESSAGE); // $NON-NLS-1$
		} finally {
			setIdleState(); // $NON-NLS-1$
		}
	}

	private void showPreview() {
		try {
			setWorkingState(Messages.getString("BrissGUI.createShowPreview")); // $NON-NLS-1$
			File result = createAndExecuteCropJobForPreview();
			DesktopHelper.openFileWithDesktopApp(result);
		} catch (IOException | DocumentException | CropException e) {
			JOptionPane.showMessageDialog(mainWindow, e.getMessage(), Messages.getString("BrissGUI.croppingError"),
					JOptionPane.ERROR_MESSAGE); // $NON-NLS-1$
		} finally {
			setIdleState(); // $NON-NLS-1$
		}
	}

	private File createAndExecuteCropJobForPreview() throws IOException, DocumentException, CropException {
		File tmpCropFileDestination = File.createTempFile("briss", ".pdf"); // $NON-NLS-1$ //$NON-NLS-2$
		CropDefinition cropDefinition = CropDefinition.createCropDefinition(workingSet.getSourceFile(),
				tmpCropFileDestination, workingSet.getClusterDefinition());
		File result = DocumentCropper.crop(cropDefinition, workingSet.getSourceFilePassword());
		return result;
	}

	private void setIdleState() {
		progressBar.setVisible(false);
		progressBar.setValue(0);
		progressBar.setString("");
		mainWindow.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	private void setWorkingState(String stateMessage) {
		progressBar.setVisible(true);
		progressBar.setString(stateMessage);
		mainWindow.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	public void importNewPdfFile(File loadFile) throws IOException, PdfException {
		String password = null;

		if (PDFReaderUtil.isEncrypted(loadFile.getAbsolutePath())) {
			while (PDFReaderUtil.isInvalidPassword(loadFile.getAbsolutePath(), password)) {
				password = promptForPassword();

				if (password == null) {
					return;
				}
			}
		}

		lastOpenDir = loadFile.getParentFile();
		previewPanel.removeAll();
		cardLayout.first(wrapperPanel);
		progressBar.setVisible(true);
		progressBar.setString(Messages.getString("BrissGUI.loadingNewFile")); // $NON-NLS-1$

		ClusterPagesTask clusterTask = new ClusterPagesTask(loadFile, password, getExcludedPages());

		clusterTask.addPropertyChangeListener(event -> {
			if (PROGRESS.equals(event.getPropertyName())) { // $NON-NLS-1$
				progressBar.setValue((Integer) event.getNewValue());
			}
		});

		clusterTask.execute();
	}

	private String promptForPassword() {
		JPanel panel = new JPanel();

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JLabel label = new JLabel(Messages.getString("BrissGUI.passwordPrompt"));

		JPasswordField pass = new JPasswordField();

		pass.addAncestorListener(FOCUS_REQUESTING_ANCESTOR_LISTENER);

		panel.add(label);
		panel.add(pass);

		String[] options = new String[]{"OK", "Cancel"};

		int option = JOptionPane.showOptionDialog(null, panel, "Action required", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

		if (option == JOptionPane.OK_OPTION) {
			char[] password = pass.getPassword();

			return new String(password);
		}

		return null;
	}

	private static PageExcludes getExcludedPages() {
		boolean inputIsValid = false;
		String previousInput = "";

		// repeat show_dialog until valid input or canceled
		while (!inputIsValid) {
			String input = JOptionPane.showInputDialog(Messages.getString("BrissGUI.excludedPagesInfo"), previousInput);
			previousInput = input;

			if (input == null || input.equals(""))
				return null;

			try {
				PageExcludes pageExcludes = new PageExcludes(PageNumberParser.parsePageNumber(input));
				return pageExcludes;
			} catch (ParseException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
			}

		}
		return null;
	}

	@Override
	public void alignSelRects(int x, int y, int w, int h) {
		// set position and size of selected rectangles
		for (MergedPanel mp : mergedPanels) {
			mp.setSelCropSize(w, h);
			mp.moveToSelectedCrops(x, y);
		}
	}

	@Override
	public void resizeAndMoveSelectedRects(int width, int height, int x, int y) {
		// resize and move selected rectangles
		// parameters are relative to current position
		for (MergedPanel mp : mergedPanels) {
			mp.resizeAndMoveSelectedCrops(width, height, x, y);
		}
	}

	@Override
	public void moveSelectedRects(int x, int y) {
		// move selected rectangles
		// parameters are relative to current position
		for (MergedPanel mp : mergedPanels) {
			mp.moveSelectedCrops(x, y);
		}
	}

	@Override
	public void deselectAllRects() {
		for (MergedPanel mp : mergedPanels) {
			mp.selectCrops(false);
		}
	}

	@Override
	public void setDefinedSizeSelRects() {
		// set size of selected rectangles
		// based on user input

		String defInput = ""; // $NON-NLS-1$

		// get maximum dimensions
		int maxWidth = -1;
		int maxHeight = -1;
		for (MergedPanel panel : mergedPanels) {
			int panelMaxWidth = panel.getWidestSelectedRect();
			if (maxWidth < panelMaxWidth) {
				maxWidth = panelMaxWidth;
			}
			int panelMaxHeight = panel.getHighestSelectedRect();
			if (maxHeight < panelMaxHeight) {
				maxHeight = panelMaxHeight;
			}
		}
		if ((maxWidth >= 0) && (maxHeight >= 0)) {
			maxWidth = Math.round(25.4f * maxWidth / 72f);
			maxHeight = Math.round(25.4f * maxHeight / 72f);
			defInput = maxWidth + " " + maxHeight; // $NON-NLS-1$
		}

		// get user input
		// maximums are used as a default
		String input = JOptionPane.showInputDialog(Messages.getString("BrissGUI.size"), defInput);

		if (input == null || input.equals("")) // $NON-NLS-1$
			return;

		String[] dims = input.split(" ", 2); // $NON-NLS-1$
		if (dims.length != 2)
			return;

		int w = -1;
		int h = -1;
		try {
			w = Integer.parseInt(dims[0]);
			h = Integer.parseInt(dims[1]);
		} catch (NumberFormatException e) {
			return;
		}

		// convert from milimeters to points
		w = Math.round(w * 72f / 25.4f);
		h = Math.round(h * 72f / 25.4f);

		for (MergedPanel mp : mergedPanels) {
			mp.setSelCropWidth(w);
			mp.setSelCropHeight(h);
		}
	}

	@Override
	public void setPositionSelRects() {
		// set position of selected rectangles
		// based on user input

		String defInput = ""; // $NON-NLS-1$

		// get minimums of positions
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		for (MergedPanel panel : mergedPanels) {
			int panelMinX = panel.getLeftmostSelectedRect();
			if (minX > panelMinX) {
				minX = panelMinX;
			}
			int panelMinY = panel.getUpmostSelectedRect();
			if (minY > panelMinY) {
				minY = panelMinY;
			}
		}
		if ((minX < Integer.MAX_VALUE) && (minY < Integer.MAX_VALUE)) {
			minX = Math.round(25.4f * minX / 72f);
			minY = Math.round(25.4f * minY / 72f);
			defInput = minX + " " + minY; // $NON-NLS-1$
		}

		// get user input
		// minimums are used as a default
		String input = JOptionPane.showInputDialog(Messages.getString("BrissGUI.position"), defInput);

		if (input == null || input.equals("")) // $NON-NLS-1$
			return;

		String[] dims = input.split(" ", 2); // $NON-NLS-1$
		if (dims.length != 2)
			return;

		int x = -1;
		int y = -1;
		try {
			x = Integer.parseInt(dims[0]);
			y = Integer.parseInt(dims[1]);
		} catch (NumberFormatException e) {
			return;
		}

		// convert from milimeters to points
		x = Math.round(x * 72f / 25.4f);
		y = Math.round(y * 72f / 25.4f);

		for (MergedPanel mp : mergedPanels) {
			mp.moveToSelectedCrops(x, y);
		}
	}

	@Override
	public void resizeSelRects(int w, int h) {
		// change size of selected rectangles (relative)
		for (MergedPanel mp : mergedPanels) {
			mp.resizeSelCrop(w, h);
		}
	}

	private void setStateAfterClusteringFinished(ClusterDefinition newClusters, PageExcludes newPageExcludes,
			File newSource, String password) {
		updateWorkingSet(newClusters, newPageExcludes, newSource, password);

		previewPanel.removeAll();
		mergedPanels = new ArrayList<>();

		for (PageCluster cluster : workingSet.getClusterDefinition().getClusterList()) {
			MergedPanel p = new MergedPanel(cluster, this);
			previewPanel.add(p);
			mergedPanels.add(p);
		}
		progressBar.setString(Messages.getString("BrissGUI.clusteringRenderingFinished")); // $NON-NLS-1$

		setIdleState(); // $NON-NLS-1$
		mainWindow.pack();
		mainWindow.setExtendedState(Frame.MAXIMIZED_BOTH);
		previewPanel.repaint();
		showPreview.setVisible(true);
		startCropping.setVisible(true);
		progressBar.setVisible(false);
		mainWindow.repaint();
	}

	private void updateWorkingSet(ClusterDefinition newClusters, PageExcludes newPageExcludes, File newSource,
			String password) {
		if (workingSet == null) {
			// completely new
			workingSet = new WorkingSet(newSource, password);
		} else if (workingSet.getSourceFile().equals(newSource)) {
			// just reload with other excludes
			copyCropsToClusters(workingSet.getClusterDefinition(), newClusters);
		}
		workingSet.setSourceFile(newSource);
		workingSet.setClusters(newClusters);
		workingSet.setPageExcludes(newPageExcludes);
	}

	private void copyCropsToClusters(ClusterDefinition oldClusters, ClusterDefinition newClusters) {

		for (PageCluster newCluster : newClusters.getClusterList()) {
			for (Integer pageNumber : newCluster.getAllPages()) {
				PageCluster oldCluster = oldClusters.getSingleCluster(pageNumber);
				for (float[] ratios : oldCluster.getRatiosList()) {
					newCluster.addRatios(ratios);
				}
			}
		}
	}

	private MouseAdapter createMousePressedAdapter() {
		return new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				deselectAllRects();
			}
		};
	}

	private class ClusterPagesTask extends SwingWorker<Void, Void> {

		private final File source;
		private final PageExcludes pageExcludes;
		private ClusterDefinition clusterDefinition = null;
		private String password;

		public ClusterPagesTask(File source, String password, PageExcludes pageExcludes) {
			super();
			this.source = source;
			this.pageExcludes = pageExcludes;
			this.password = password;
		}

		@Override
		protected void done() {
			setStateAfterClusteringFinished(clusterDefinition, pageExcludes, source, password);
		}

		@Override
		protected Void doInBackground() {

			try {
				clusterDefinition = ClusterCreator.clusterPages(source, password, pageExcludes);

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return null;
			}

			int totalWorkUnits = clusterDefinition.getNrOfPagesToRender();

			ClusterRenderWorker renderWorker = new ClusterRenderWorker(source, password, clusterDefinition);

			renderWorker.start();

			while (renderWorker.isAlive()) {
				int percent = (int) (100.0 * renderWorker.workerUnitCounter / totalWorkUnits);

				setProgress(percent);

				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}
			}

			return null;
		}
	}
}
