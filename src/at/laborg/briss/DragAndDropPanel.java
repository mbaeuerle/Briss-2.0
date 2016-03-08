package at.laborg.briss;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Created by hybridtupel on 08.03.16.
 */
public class DragAndDropPanel extends JPanel {

    JButton loadButton;


    public DragAndDropPanel(ActionListener actionListener) {
        init(actionListener);
    }

    private void init(ActionListener actionListener) {
        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
        setLayout(boxLayout);
        JLabel dndText = new JLabel("");//"Drag and drop your PDF file here");
        dndText.setAlignmentX(Component.CENTER_ALIGNMENT);
        dndText.setPreferredSize(new Dimension(100,50));
        loadButton = new JButton("Load file");
        loadButton.addActionListener(actionListener);
        loadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Box.createVerticalGlue());
        add(dndText);
        add(loadButton);
        add(Box.createVerticalGlue());
    }

}

