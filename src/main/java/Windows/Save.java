package Windows;

import static DataHandling.ConAndVar.DIR_DATABASE;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.Arrays;
import java.util.Collections;

public class Save extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField1;

    private Save(String[] list) {
        setContentPane(contentPane);
        setModal(true);
        setLocation(250, 250);

        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK(list));

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
                if (e.getKeyCode() == KeyEvent.VK_ENTER) onOK(list);
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) dispose();
            }
        });
    }

    public static void main(String[] args) {
        Save save = new Save(args);
        save.pack();
        save.setVisible(true);
    }

    private void onOK(String[] list) {
        try {
            String line = textField1.getText();
            for (int i = 1; i < list.length; i++)
                if (line.equals(list[i].substring(0, list[i].indexOf(" ")))) {
                    list[i] = null;
                    Arrays.sort(list, Collections.reverseOrder());
                    System.arraycopy(list, 0, list, 0, list.length-1);
                    break;
                }
            FileWriter writer = new FileWriter(DIR_DATABASE + "FriendsIP.txt", true);
            BufferedWriter bufferWriter = new BufferedWriter(writer);
            bufferWriter.write(line + " " + list[0] + "\n");
            bufferWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dispose();
    }
}
