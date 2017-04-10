package Windows;

import Management.Communication;

import static DataHandling.GlobalData.*;
import static java.lang.Math.abs;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class Client extends JFrame {
    private JTabbedPane tabbedPane1;
    private JTextField textField1;
    private JTextArea textArea1;
    private JTextArea textArea2;
    private JButton viewButton;
    private JPanel viewPanel;
    private JPanel panel1;
    private JLabel image;
    private JLabel fps;
    private JLabel alertLabel;
    private JComboBox comboBox1;

    public Client(String ip, int port) throws IOException {
        super("Client");
        setContentPane(panel1);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        Communication com = new Communication(ip, port, textArea1, textArea2);
        image.add(fps);
        viewButton.setIcon(new ImageIcon(DIR_SOURCE_IMAGE + "triangle_icon_right.png"));
        alertLabel.setIcon(new ImageIcon(DIR_SOURCE_IMAGE + "true_alert_icon.png"));
        alertLabel.setText("");
        ((DefaultCaret)textArea1.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        ((DefaultCaret)textArea2.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

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

        // Развернуть или скрыть viewPanel;
        viewButton.addActionListener(e -> {
            viewPanel.setVisible(!viewPanel.isVisible());
            if (viewPanel.isVisible()) {
                viewButton.setIcon(new ImageIcon(DIR_SOURCE_IMAGE + "triangle_icon_left.png"));
                Client.super.setSize(600, 450);
            } else {
                viewButton.setIcon(new ImageIcon(DIR_SOURCE_IMAGE + "triangle_icon_right.png"));
                Client.super.setSize(320, 450);
            }
        });

        // Ожидаем нажатие определенных клавиш
        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER && !textField1.getText().equals("")) {
                    if (tabbedPane1.getSelectedIndex() == 0) {
                        textArea1.append(com.time() + " You:\n" + textField1.getText() + "\n\n");
                        com.sayStringMessage(textField1.getText(), Communication.Mode.MESSAGE);
                        textField1.setText("");
                    } else {
                        textArea2.append(textField1.getText() + "\n");
                        com.sayStringMessage(textField1.getText(), Communication.Mode.COMMAND);
                        textField1.setText("");
                    }
                } else if (e.isShiftDown() && e.getKeyChar() == KeyEvent.VK_SPACE) {
                    tabbedPane1.setSelectedIndex(abs(tabbedPane1.getSelectedIndex() - 1));
                    textField1.setText("");
                }
            }
        });

        alertLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                com.alert = !com.alert;
                alertLabel.setIcon(new ImageIcon(DIR_SOURCE_IMAGE + com.alert + "_alert_icon.png"));
            }
        });

        // Изменение размера desktop окна
        comboBox1.addItemListener(e ->
                com.sayStringMessage("SIZE" + comboBox1.getSelectedItem().toString(), Communication.Mode.PARAMETERS));

        new ImageRun().start();
    }

    public class ImageRun extends Thread{
        private int counter = 0;
        private long timeForFps = System.currentTimeMillis() + 1000;

        @Override
        public void run() {
            if (System.currentTimeMillis() > timeForFps) {
                fps.setText(String.valueOf(counter) + "fps");
                timeForFps = System.currentTimeMillis() + 1000;
            }
            counter++;
            try {
                image.setIcon(new ImageIcon(ImageIO.read(new File("src//main//resources//image//desktop//desktop_screen.jpg"))));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException ignored) {}
        }
    }

    @Override
    protected void finalize() throws Throwable {

        Thread.sleep(300);
        System.exit(0);
    }
}
