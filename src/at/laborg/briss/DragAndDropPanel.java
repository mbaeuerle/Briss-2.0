package at.laborg.briss;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Created by hybridtupel on 08.03.16.
 */
public class DragAndDropPanel extends JPanel {

    JButton loadButton;


    public DragAndDropPanel(ImageIcon icon, ActionListener actionListener) {
        init(icon, actionListener);
    }

    private void init(ImageIcon icon, ActionListener actionListener) {
        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
        setLayout(boxLayout);
        JLabel dndText = new JLabel(icon);//"Drag and drop your PDF file here");
        dndText.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadButton = new JButton("Load file");
        loadButton.addActionListener(actionListener);
        loadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadButton.setPreferredSize(new Dimension(100,50));
        add(Box.createVerticalGlue());
        add(dndText);
        add(Box.createRigidArea(new Dimension(0, 40)));
        add(loadButton);
        add(Box.createVerticalGlue());
    }

}

