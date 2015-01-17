// $Id: BrissGUI.java 70 2012-05-26 16:20:19Z laborg $
/**
 * Copyright 2010, 2011 Gerhard Aigner, Rastislav Wartiak
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
package at.laborg.briss;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jpedal.exception.PdfException;

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
import at.laborg.briss.utils.PDFFileFilter;
import at.laborg.briss.utils.PageNumberParser;

import com.itextpdf.text.DocumentException;

/**
 * 
 * @author gerhard
 * 
 */
@SuppressWarnings("serial")
public class BrissGUI extends JFrame implements ActionListener, PropertyChangeListener, ComponentListener {

	private static final String EXCLUDE_PAGES_DESCRIPTION = "Enter pages to be excluded from merging (e.g.: \"1-4;6;9\").\n"
			+ "First page has number: 1\n" + "If you don't know what you should do just press \"Cancel\"";
	private static final String SET_SIZE_DESCRIPTION = "Enter size in milimeters (width height)";
	private static final String SET_POSITION_DESCRIPTION = "Enter position in milimeters (x y)";
	private static final String LOAD = "Load File";
	private static final String CROP = "Crop PDF";
	private static final String EXIT = "Exit";
	private static final String MAXIMIZE_WIDTH = "Maximize to width";
	private static final String MAXIMIZE_HEIGHT = "Maximize to height";
	private static final String EXCLUDE_OTHER_PAGES = "Exclude other pages";
	private static final String PREVIEW = "Preview";
	private static final String DONATE = "Donate";
	private static final String HELP = "Show help";
	private static final String MAXIMIZE_SIZE = "Maximize to size (all)";
	private static final String SET_SIZE = "Set size (selected)";
	private static final String SET_POSITION = "Set position (selected)";
	private static final String MOVE_LEFT = "Move left (selected)";
	private static final String MOVE_RIGHT = "Move right (selected)";
	private static final String MOVE_UP = "Move up (selected)";
	private static final String MOVE_DOWN = "Move down (selected)";
	private static final String SELECT_ALL = "Select all";
	private static final String SELECT_NONE = "Select none";

	private static final String DONATION_URI = "http://sourceforge.net/project/project_donations.php?group_id=320676";
	private static final String RES_ICON_PATH = "/resources/Briss_icon_032x032.gif";

	private JMenuBar menuBar;
	private JPanel previewPanel;
	private JProgressBar progressBar;
	private JMenuItem loadButton, cropButton, maximizeWidthButton, maximizeHeightButton, showPreviewButton,
			showHelpButton, openDonationLinkButton, excludePagesButton;
	private JMenuItem maximizeSizeButton, setSizeButton, setPositionButton, moveLeftButton, moveRightButton,
			moveUpButton, moveDownButton, selectAllButton, selectNoneButton;
	private List<MergedPanel> mergedPanels = null;

	private File lastOpenDir;

	private WorkingSet workingSet;

	public BrissGUI(String[] args) {
		super("BRISS - BRight Snippet Sire");
		init();
		tryToLoadFileFromArgument(args);
	}

	private void tryToLoadFileFromArgument(String[] args) {
		if (args.length == 0)
			return;
		File fileArg = new File(args[0]);
		if (fileArg.exists() && fileArg.getAbsolutePath().trim().endsWith(".pdf")) {
			try {
				importNewPdfFile(fileArg);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Briss error", JOptionPane.ERROR_MESSAGE);
			} catch (PdfException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Briss error", JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	private void init() {

		setDefaultCloseOperation(EXIT_ON_CLOSE);

		this.setTransferHandler(new BrissTransferHandler(this));

		setUILook();

		loadIcon();

		// Create the menu bar.
		menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		JMenu rectangleMenu = new JMenu("Rectangle");
		rectangleMenu.setMnemonic(KeyEvent.VK_R);
		JMenu actionMenu = new JMenu("Action");
		actionMenu.setMnemonic(KeyEvent.VK_A);

		menuBar.add(fileMenu);
		menuBar.add(rectangleMenu);
		menuBar.add(actionMenu);

		loadButton = new JMenuItem(LOAD, KeyEvent.VK_L);
		loadButton.addActionListener(this);
		loadButton.setEnabled(true);
		loadButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, 0));
		fileMenu.add(loadButton);

		fileMenu.addSeparator();

		openDonationLinkButton = new JMenuItem(DONATE);
		openDonationLinkButton.addActionListener(this);
		fileMenu.add(openDonationLinkButton);

		excludePagesButton = new JMenuItem(EXCLUDE_OTHER_PAGES);
		excludePagesButton.addActionListener(this);
		excludePagesButton.setEnabled(false);
		excludePagesButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, 0));
		fileMenu.add(excludePagesButton);

		showHelpButton = new JMenuItem(HELP);
		showHelpButton.addActionListener(this);
		fileMenu.add(showHelpButton);

		fileMenu.addSeparator();

		JMenuItem menuItem = new JMenuItem(EXIT, KeyEvent.VK_E);
		menuItem.addActionListener(this);
		fileMenu.add(menuItem);

		cropButton = new JMenuItem(CROP, KeyEvent.VK_C);
		cropButton.addActionListener(this);
		cropButton.setEnabled(false);
		cropButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, 0));
		actionMenu.add(cropButton);

		showPreviewButton = new JMenuItem(PREVIEW, KeyEvent.VK_P);
		showPreviewButton.addActionListener(this);
		showPreviewButton.setEnabled(false);
		showPreviewButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0));
		actionMenu.add(showPreviewButton);

		maximizeWidthButton = new JMenuItem(MAXIMIZE_WIDTH, KeyEvent.VK_W);
		maximizeWidthButton.addActionListener(this);
		maximizeWidthButton.setEnabled(false);
		maximizeWidthButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0));
		rectangleMenu.add(maximizeWidthButton);

		maximizeHeightButton = new JMenuItem(MAXIMIZE_HEIGHT, KeyEvent.VK_H);
		maximizeHeightButton.addActionListener(this);
		maximizeHeightButton.setEnabled(false);
		maximizeHeightButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, 0));
		rectangleMenu.add(maximizeHeightButton);

		maximizeSizeButton = new JMenuItem(MAXIMIZE_SIZE, KeyEvent.VK_Z);
		maximizeSizeButton.addActionListener(this);
		maximizeSizeButton.setEnabled(false);
		maximizeSizeButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, 0));
		rectangleMenu.add(maximizeSizeButton);

		setSizeButton = new JMenuItem(SET_SIZE, KeyEvent.VK_S);
		setSizeButton.addActionListener(this);
		setSizeButton.setEnabled(false);
		setSizeButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0));
		rectangleMenu.add(setSizeButton);

		setPositionButton = new JMenuItem(SET_POSITION, KeyEvent.VK_O);
		setPositionButton.addActionListener(this);
		setPositionButton.setEnabled(false);
		setPositionButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, 0));
		rectangleMenu.add(setPositionButton);

		rectangleMenu.addSeparator();

		moveLeftButton = new JMenuItem(MOVE_LEFT, KeyEvent.VK_LEFT);
		moveLeftButton.addActionListener(this);
		moveLeftButton.setEnabled(false);
		moveLeftButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0));
		rectangleMenu.add(moveLeftButton);

		moveRightButton = new JMenuItem(MOVE_RIGHT, KeyEvent.VK_RIGHT);
		moveRightButton.addActionListener(this);
		moveRightButton.setEnabled(false);
		moveRightButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0));
		rectangleMenu.add(moveRightButton);

		moveUpButton = new JMenuItem(MOVE_UP, KeyEvent.VK_UP);
		moveUpButton.addActionListener(this);
		moveUpButton.setEnabled(false);
		moveUpButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));
		rectangleMenu.add(moveUpButton);

		moveDownButton = new JMenuItem(MOVE_DOWN, KeyEvent.VK_DOWN);
		moveDownButton.addActionListener(this);
		moveDownButton.setEnabled(false);
		moveDownButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
		rectangleMenu.add(moveDownButton);

		rectangleMenu.addSeparator();

		selectAllButton = new JMenuItem(SELECT_ALL, 0);
		selectAllButton.addActionListener(this);
		selectAllButton.setEnabled(false);
		selectAllButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0));
		rectangleMenu.add(selectAllButton);

		selectNoneButton = new JMenuItem(SELECT_NONE, 0);
		selectNoneButton.addActionListener(this);
		selectNoneButton.setEnabled(false);
		selectNoneButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, 0));
		rectangleMenu.add(selectNoneButton);

		setJMenuBar(menuBar);

		previewPanel = new JPanel();
		previewPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 4, 4));
		previewPanel.setEnabled(true);
		previewPanel.setBackground(Color.BLACK);
		previewPanel.addComponentListener(this);

		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(true);
		progressBar.setPreferredSize(new Dimension(400, 30));
		progressBar.setEnabled(true);

		JScrollPane scrollPane = new JScrollPane(previewPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getVerticalScrollBar().setUnitIncrement(30);
		add(scrollPane, BorderLayout.CENTER);
		add(progressBar, BorderLayout.PAGE_END);
		pack();
		setVisible(true);
	}

	private void setUILook() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException ex) {
			System.out.println("Unable to load native look and feel");
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
	}

	private void loadIcon() {
		InputStream is = getClass().getResourceAsStream(RES_ICON_PATH);
		byte[] buf = new byte[1024 * 100];
		try {
			int cnt = is.read(buf);
			byte[] imgBuf = Arrays.copyOf(buf, cnt);
			setIconImage(new ImageIcon(imgBuf).getImage());
		} catch (IOException e) {
		}
	}

	private static PageExcludes getExcludedPages() {
		boolean inputIsValid = false;
		String previousInput = "";

		// repeat show_dialog until valid input or canceled
		while (!inputIsValid) {
			String input = JOptionPane.showInputDialog(EXCLUDE_PAGES_DESCRIPTION, previousInput);
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

	private File getCropFileDestination(File sourceFile) {

		File recommendedFile = BrissFileHandling.getRecommendedDestination(sourceFile);
		JFileChooser fc = new JFileChooser(lastOpenDir);
		fc.setSelectedFile(recommendedFile);
		fc.setFileFilter(new PDFFileFilter());
		int retval = fc.showSaveDialog(this);

		if (retval == JFileChooser.APPROVE_OPTION)
			return fc.getSelectedFile();

		return null;
	}

	private File getNewFileToCrop() {

		JFileChooser fc = new JFileChooser(lastOpenDir);
		fc.setFileFilter(new PDFFileFilter());
		int retval = fc.showOpenDialog(this);

		if (retval == JFileChooser.APPROVE_OPTION)
			return fc.getSelectedFile();

		return null;
	}

	public void actionPerformed(ActionEvent action) {
		if (action.getActionCommand().equals(DONATE)) {
			try {
				DesktopHelper.openDonationLink(DONATION_URI);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error occured while loading", JOptionPane.ERROR_MESSAGE);
			}
		} else if (action.getActionCommand().equals(EXIT)) {
			System.exit(0);
		} else if (action.getActionCommand().equals(HELP)) {
			new HelpDialog(this, "Briss Help", Dialog.ModalityType.MODELESS);
		} else if (action.getActionCommand().equals(MAXIMIZE_HEIGHT)) {
			maximizeHeightInSelectedRects();
		} else if (action.getActionCommand().equals(MAXIMIZE_WIDTH)) {
			maximizeWidthInSelectedRects();
		} else if (action.getActionCommand().equals(EXCLUDE_OTHER_PAGES)) {
			if (workingSet.getSourceFile() == null)
				return;
			setWorkingState("Exclude other pages");
			try {
				reloadWithOtherExcludes();
				setTitle("BRISS - " + workingSet.getSourceFile().getName());
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error occured while reloading", JOptionPane.ERROR_MESSAGE);
			} catch (PdfException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error occured while reloading", JOptionPane.ERROR_MESSAGE);
			}
		} else if (action.getActionCommand().equals(LOAD)) {
			File inputFile = getNewFileToCrop();
			if (inputFile == null)
				return;
			try {
				importNewPdfFile(inputFile);
				setTitle("BRISS - " + inputFile.getName());
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error occured while loading", JOptionPane.ERROR_MESSAGE);
			} catch (PdfException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error occured while loading", JOptionPane.ERROR_MESSAGE);
			}

		} else if (action.getActionCommand().equals(CROP)) {
			try {
				setWorkingState("loading PDF");
				File result = createAndExecuteCropJob(workingSet.getSourceFile());
				if (result != null) {
					DesktopHelper.openFileWithDesktopApp(result);
					lastOpenDir = result.getParentFile();
				}

			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error occured while cropping", JOptionPane.ERROR_MESSAGE);
			} catch (DocumentException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error occured while cropping", JOptionPane.ERROR_MESSAGE);
			} catch (CropException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error occured while cropping", JOptionPane.ERROR_MESSAGE);
			} finally {
				setIdleState("");
			}
		} else if (action.getActionCommand().equals(PREVIEW)) {
			try {
				setWorkingState("Creating and showing preview...");
				File result = createAndExecuteCropJobForPreview();
				DesktopHelper.openFileWithDesktopApp(result);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error occured while cropping", JOptionPane.ERROR_MESSAGE);
			} catch (DocumentException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error occured while cropping", JOptionPane.ERROR_MESSAGE);
			} catch (CropException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error occured while cropping", JOptionPane.ERROR_MESSAGE);
			} finally {
				setIdleState("");
			}
		} else if (action.getActionCommand().equals(MAXIMIZE_SIZE)) {
			maximizeSizeInAllRects();
		} else if (action.getActionCommand().equals(SET_SIZE)) {
			setDefinedSizeSelRects();
		} else if (action.getActionCommand().equals(SET_POSITION)) {
			setPositionSelRects();
		} else if (action.getActionCommand().equals(SELECT_ALL)) {
			for (MergedPanel panel : mergedPanels)
				panel.selectCrops(true);
		} else if (action.getActionCommand().equals(SELECT_NONE)) {
			for (MergedPanel panel : mergedPanels)
				panel.selectCrops(false);
		}
	}

	private File createAndExecuteCropJobForPreview() throws IOException, DocumentException, CropException {
		File tmpCropFileDestination = File.createTempFile("briss", ".pdf");
		CropDefinition cropDefinition = CropDefinition.createCropDefinition(workingSet.getSourceFile(),
				tmpCropFileDestination, workingSet.getClusterDefinition());
		File result = DocumentCropper.crop(cropDefinition);
		return result;
	}

	private File createAndExecuteCropJob(File source) throws IOException, DocumentException, CropException {
		File cropDestinationFile = getCropFileDestination(workingSet.getSourceFile());
		if (cropDestinationFile == null)
			return null;
		CropDefinition cropDefinition = CropDefinition.createCropDefinition(workingSet.getSourceFile(),
				cropDestinationFile, workingSet.getClusterDefinition());
		File result = DocumentCropper.crop(cropDefinition);
		return result;
	}

	private void setIdleState(String stateMessage) {
		progressBar.setValue(0);
		progressBar.setString(stateMessage);
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	private void setWorkingState(String stateMessage) {
		progressBar.setString(stateMessage);
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	void importNewPdfFile(File loadFile) throws IOException, PdfException {
		lastOpenDir = loadFile.getParentFile();
		previewPanel.removeAll();
		progressBar.setString("Loading new file - Creating merged previews");
		ClusterPagesTask clusterTask = new ClusterPagesTask(loadFile, getExcludedPages());
		clusterTask.addPropertyChangeListener(this);
		clusterTask.execute();
	}

	private void reloadWithOtherExcludes() throws IOException, PdfException {
		previewPanel.removeAll();
		progressBar.setString("Reloading file - Creating merged previews");
		ClusterPagesTask clusterTask = new ClusterPagesTask(workingSet.getSourceFile(), getExcludedPages());
		clusterTask.addPropertyChangeListener(this);
		clusterTask.execute();
	}

	private void maximizeWidthInSelectedRects() {
		// maximize to width
		// search for maximum width
		int maxWidth = -1;
		for (MergedPanel panel : mergedPanels) {
			int panelMaxWidth = panel.getWidestSelectedRect();
			if (maxWidth < panelMaxWidth) {
				maxWidth = panelMaxWidth;
			}
		}
		// set maximum width to all rectangles
		if (maxWidth == -1)
			return;
		for (MergedPanel mp : mergedPanels) {
			mp.setSelCropWidth(maxWidth);
		}
	}

	private void maximizeHeightInSelectedRects() {
		// maximize to height
		// search for maximum height
		int maxHeight = -1;
		for (MergedPanel panel : mergedPanels) {
			int panelMaxHeight = panel.getHeighestSelectedRect();
			if (maxHeight < panelMaxHeight) {
				maxHeight = panelMaxHeight;
			}
		}
		// set maximum height to all rectangles
		if (maxHeight == -1)
			return;
		for (MergedPanel mp : mergedPanels) {
			mp.setSelCropHeight(maxHeight);
		}
	}

	private void maximizeSizeInAllRects() {
		// maximize to width and height for all rectangles
		// search for maximums
		int maxWidth = -1;
		int maxHeight = -1;
		for (MergedPanel panel : mergedPanels) {
			Dimension panelMaxSize = panel.getLargestRect();
			if (maxWidth < panelMaxSize.width) {
				maxWidth = panelMaxSize.width;
			}
			if (maxHeight < panelMaxSize.height) {
				maxHeight = panelMaxSize.height;
			}
		}
		// set maximum size to all rectangles
		if ((maxWidth == -1) || (maxHeight == -1))
			return;
		for (MergedPanel mp : mergedPanels) {
			mp.setAllCropSize(maxWidth, maxHeight);
		}
	}

	public void alignSelRects(int x, int y, int w, int h) {
		// set position and size of selected rectangles
		for (MergedPanel mp : mergedPanels) {
			mp.setSelCropSize(w, h);
			mp.moveToSelelectedCrops(x, y);
		}
	}

	public void moveSelectedRects(int x, int y) {
		// move selected rectangles
		// parameters are relative to current position
		for (MergedPanel mp : mergedPanels) {
			mp.moveSelelectedCrops(x, y);
		}
	}

	public void setDefinedSizeSelRects() {
		// set size of selected rectangles
		// based on user input

		String defInput = "";

		// get maximum dimensions
		int maxWidth = -1;
		int maxHeight = -1;
		for (MergedPanel panel : mergedPanels) {
			int panelMaxWidth = panel.getWidestSelectedRect();
			if (maxWidth < panelMaxWidth) {
				maxWidth = panelMaxWidth;
			}
			int panelMaxHeight = panel.getHeighestSelectedRect();
			if (maxHeight < panelMaxHeight) {
				maxHeight = panelMaxHeight;
			}
		}
		if ((maxWidth >= 0) && (maxHeight >= 0)) {
			maxWidth = Math.round(25.4f * maxWidth / 72f);
			maxHeight = Math.round(25.4f * maxHeight / 72f);
			defInput = Integer.toString(maxWidth) + " " + Integer.toString(maxHeight);
		}

		// get user input
		// maximums are used as a default
		String input = JOptionPane.showInputDialog(SET_SIZE_DESCRIPTION, defInput);

		if (input == null || input.equals(""))
			return;

		String[] dims = input.split(" ", 2);
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

	public void setPositionSelRects() {
		// set position of selected rectangles
		// based on user input

		String defInput = "";

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
			defInput = Integer.toString(minX) + " " + Integer.toString(minY);
		}

		// get user input
		// minimums are used as a default
		String input = JOptionPane.showInputDialog(SET_POSITION_DESCRIPTION, defInput);

		if (input == null || input.equals(""))
			return;

		String[] dims = input.split(" ", 2);
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
			mp.moveToSelelectedCrops(x, y);
		}
	}

	public void resizeSelRects(int w, int h) {
		// change size of selected rectangles (relative)
		for (MergedPanel mp : mergedPanels) {
			mp.resizeSelCrop(w, h);
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress".equals(evt.getPropertyName())) {
			progressBar.setValue((Integer) evt.getNewValue());
		}
	}

	private void setStateAfterClusteringFinished(ClusterDefinition newClusters, PageExcludes newPageExcludes,
			File newSource) {
		updateWorkingSet(newClusters, newPageExcludes, newSource);

		previewPanel.removeAll();
		mergedPanels = new ArrayList<MergedPanel>();

		for (PageCluster cluster : workingSet.getClusterDefinition().getClusterList()) {
			MergedPanel p = new MergedPanel(cluster, this);
			previewPanel.add(p);
			mergedPanels.add(p);
		}
		progressBar.setString("Clustering and Rendering finished");
		cropButton.setEnabled(true);
		maximizeWidthButton.setEnabled(true);
		maximizeHeightButton.setEnabled(true);
		excludePagesButton.setEnabled(true);
		showPreviewButton.setEnabled(true);
		maximizeSizeButton.setEnabled(true);
		setSizeButton.setEnabled(true);
		setPositionButton.setEnabled(true);
		moveLeftButton.setEnabled(true);
		moveRightButton.setEnabled(true);
		moveUpButton.setEnabled(true);
		moveDownButton.setEnabled(true);
		selectAllButton.setEnabled(true);
		selectNoneButton.setEnabled(true);
		setIdleState("");
		pack();
		setExtendedState(Frame.MAXIMIZED_BOTH);
		previewPanel.repaint();
		repaint();
	}

	private void updateWorkingSet(ClusterDefinition newClusters, PageExcludes newPageExcludes, File newSource) {
		if (workingSet == null) {
			// completely new
			workingSet = new WorkingSet(newSource);
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
				for (Float[] ratios : oldCluster.getRatiosList()) {
					newCluster.addRatios(ratios);
				}
			}
		}
	}

	private class ClusterPagesTask extends SwingWorker<Void, Void> {

		private final File source;
		private final PageExcludes pageExcludes;
		private ClusterDefinition clusterDefinition = null;

		public ClusterPagesTask(File source, PageExcludes pageExcludes) {
			super();
			this.source = source;
			this.pageExcludes = pageExcludes;
		}

		@Override
		protected void done() {
			setStateAfterClusteringFinished(clusterDefinition, pageExcludes, source);
		}

		@Override
		protected Void doInBackground() {

			try {
				clusterDefinition = ClusterCreator.clusterPages(source, pageExcludes);

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return null;
			}

			int totalWorkUnits = clusterDefinition.getNrOfPagesToRender();
			ClusterRenderWorker renderWorker = new ClusterRenderWorker(source, clusterDefinition);
			renderWorker.start();

			while (renderWorker.isAlive()) {
				int percent = (int) ((renderWorker.workerUnitCounter / (float) totalWorkUnits) * 100);
				setProgress(percent);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
			}

			return null;
		}
	}

	public void componentResized(ComponentEvent e) {
		previewPanel.revalidate();
		for (Component component : previewPanel.getComponents()) {
			component.repaint();
		}
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}
}
