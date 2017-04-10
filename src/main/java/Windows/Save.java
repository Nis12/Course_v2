package Windows;

import DataHandling.Directory;
import DataHandling.GlobalData;

import javax.swing.*;
import java.awt.event.*;

public class Save extends JDialog implements Directory {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField1;

    public Save(GlobalData globalData, String ipAddress) {
        setContentPane(contentPane);
        setModal(true);
        setLocation(250, 250);
        pack();
        setVisible(true);

        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK(globalData, ipAddress));

        buttonCancel.addActionListener(e -> dispose());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) onOK(globalData, ipAddress);
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) dispose();
            }
        });
    }

    private void onOK(GlobalData globalData, String ipAddress) {
        globalData.addFriendsList(textField1.getText(), ipAddress);
        dispose();
    }
}
