package Windows;

import Management.Communication;

import static DataHandling.GlobalData.*;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.event.*;
import java.io.IOException;

public class Server extends JFrame {
    private JTextArea textArea1;
    private JTextArea textArea2;
    private JTextField textField1;
    private JButton giveAccessButton;
    private JButton cancelButton;
    private JPanel panel1;
    private JLabel alertLabel;

    public Server(int port) throws IOException {
        super("Server");
        setContentPane(panel1);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        Communication com = new Communication(port, textArea1, textArea2);
        textField1.setEnabled(true);
        alertLabel.setIcon(new ImageIcon(DIR_SOURCE_IMAGE + "true_alert_icon.png"));
        alertLabel.setText("");
        ((DefaultCaret) textArea1.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        ((DefaultCaret) textArea2.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        //При закрытии окна
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                com.loopWorkStatus = false;
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });

        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER && !textField1.getText().equals("")) {
                    textArea1.append(com.time() + " You:\n" + textField1.getText() + "\n\n");
                    com.sayStringMessage(textField1.getText(), Communication.Mode.MESSAGE);
                    textField1.setText("");
                }
            }
        });
        cancelButton.addActionListener(e -> System.exit(0));

        alertLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                com.alert = !com.alert;
                alertLabel.setIcon(new ImageIcon(DIR_SOURCE_IMAGE + com.alert + "_alert_icon.png"));
            }
        });

        // Дать доступ к управлению.
        giveAccessButton.addActionListener(e -> {
            if (giveAccessButton.getText().equals("Give access")) {
                giveAccessButton.setText("Close access");
                com.sayStringMessage(com.changePassword(), Communication.Mode.ACCESS);
            } else {
                giveAccessButton.setText("Give access");
                com.changePassword();
            }
        });
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.exit(0);
    }
}
