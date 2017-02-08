package Windows;

import DataHandling.Communication;

import static DataHandling.ConAndVar.*;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class GraphicalInitialSettings extends JFrame {
    private JPanel contentPane;
    private JButton clientButton;
    private JButton serverButton;
    private JFormattedTextField ipAddressFormattedTextField;
    private JFormattedTextField portFormattedTextField;
    private JComboBox<String> friendsComboBox;
    private JCheckBox searchCheckBox;
    private JTextField globalIPTextField;
    private JTextField localIPTextField;
    private JSlider slider1;
    private JLabel sliderValue;
    private JLabel saveLabel;
    private AnimationLoading AL;
    private JPopupMenu popup;
    private ArrayList<String> list = new ArrayList<>();
    private boolean save = true, click = false;
    private Connect connect;

    public GraphicalInitialSettings() {
        super("Initialization");
        setContentPane(contentPane);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        super.setLocation(200, 200);
        setVisible(true);
        pack();

        saveLabel.setText("");
        saveLabel.setIcon(new ImageIcon(DIR_SOURCE_IMAGE + save + "_save.png"));
        globalIPTextField.setText(ethernetIP());
        localIPTextField.setText(localIP());
        popup = new JPopupMenu();

        friends();

        // Remove one string
        JMenuItem removeItem = new JMenuItem("Remove");
        removeItem.addActionListener(e -> {
            for (int i = 0; i < list.size(); i++)
                if (list.get(i).equals(friendsComboBox.getSelectedItem().toString()))
                    list.remove(i);
            friendsComboBox.removeAllItems();
            for (String str : list) friendsComboBox.addItem(str);
            friendsComboBox.setSelectedIndex(0);
        });
        popup.add(removeItem);

        // Clean all friend list
        JMenuItem cleanItem = new JMenuItem("Clean");
        cleanItem.addActionListener(e -> {
            friendsComboBox.removeAllItems();
            list = new ArrayList<>();
        });
        popup.add(cleanItem);

        // Add popup menu
        friendsComboBox.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                if (SwingUtilities.isRightMouseButton(event))
                    popup.show(friendsComboBox, event.getX(), event.getY());
            }
        });

        // Client
        clientButton.addActionListener(e -> {
            if (click) {
                click = false;
                connect.interrupt();
                AL.close();
            } else {
                click = true;
                AL = new AnimationLoading(GraphicalInitialSettings.super.getLocation());
                connect = new Connect(clientButton.getName());
                connect.start();
            }
        });
        // Server
        serverButton.addActionListener(e -> {
            if (click) {
                click = false;
                connect.inter();
                connect.interrupt();
            } else {
                click = true;
                AL = new AnimationLoading(GraphicalInitialSettings.super.getLocation());
                connect = new Connect(serverButton.getName());
                connect.start();
            }
        });

        // click exit
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                end(true);
            }
        });

        // click escape
        contentPane.registerKeyboardAction(e -> end(true), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // Friends list
        friendsComboBox.addActionListener(e -> {
            try {
                String string = friendsComboBox.getSelectedItem().toString();
                ipAddressFormattedTextField.setText(string.substring(friendsComboBox.getSelectedItem().toString().indexOf(" ")).trim());
                Collections.swap(list, 0, friendsComboBox.getSelectedIndex());
                friendsComboBox.removeAllItems();
                for (String str : list) friendsComboBox.addItem(str);
            } catch (NullPointerException ignored) {
            }
        });

        // click to combobox
        searchCheckBox.addActionListener(e -> {
            if (!searchCheckBox.isSelected()) ipAddressFormattedTextField.setText("192168001100");
            else ipAddressFormattedTextField.setText("192168");
            ipAddressFormattedTextField.setEnabled(!ipAddressFormattedTextField.isEnabled());
        });
        slider1.addChangeListener(e -> sliderValue.setText("Timeout connection: " + String.valueOf(slider1.getValue()) + " sec"));

        // change form location
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                try {
                    AL.setLoadingLocation(GraphicalInitialSettings.super.getLocation());
                } catch (NullPointerException ignored) {
                }
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                try {
                    AL.setLoadingLocation(GraphicalInitialSettings.super.getLocation());
                } catch (NullPointerException ignored) {
                }
            }

            @Override
            public void componentShown(ComponentEvent e) {
                try {
                    AL.setLoadingLocation(GraphicalInitialSettings.super.getLocation());
                } catch (NullPointerException ignored) {
                }
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                try {
                    AL.setLoadingLocation(GraphicalInitialSettings.super.getLocation());
                } catch (NullPointerException ignored) {
                }
            }
        });

        // clicking the saveIcon
        saveLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                save = !save;
                saveLabel.setIcon(new ImageIcon(DIR_SOURCE_IMAGE + save + "_save.png"));
            }
        });
    }

    // add friends list
    private void friends() {
        list = new ArrayList<>();
        try {
            BufferedReader input = new BufferedReader(new FileReader(DIR_DATABASE + "FriendsIP.txt"));
            String line;
            while ((line = input.readLine()) != null)
                if (!line.equals("") && !line.equals("\n")) {
                    list.add(line);
                    friendsComboBox.addItem(line);
                }
            input.close();
            File file = new File(DIR_DATABASE + "FriendsIP.txt");
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            } else file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // end InitialSettings
    private void end(boolean exit) {
        try {
            FileWriter writer = new FileWriter(DIR_DATABASE + "FriendsIP.txt", true);
            PrintWriter printWriter = new PrintWriter(writer);
            for (String str : list) printWriter.println(str);
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (exit) System.exit(0);
        else dispose();
    }

    private void createUIComponents() {
        try {
            MaskFormatter f = new MaskFormatter("###.###.###.###");
            ipAddressFormattedTextField = new JFormattedTextField(f);
            ipAddressFormattedTextField.setValue("192.168.000.100");
            f.setValidCharacters("0123456789 ");
            f = new MaskFormatter("*****");
            portFormattedTextField = new JFormattedTextField(f);
            portFormattedTextField.setValue("8382 ");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // get Ethernet address
    private String ethernetIP() {
        String ip = null;
        try {
            ip = new BufferedReader(new InputStreamReader(
                    new URL("http://checkip.amazonaws.com").openStream())).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ip;
    }

    // get local address
    private String localIP() {
        String ip = null;
        try {
            InetAddress LocalAddress = InetAddress.getLocalHost();
            ip = LocalAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ip;
    }

    // connect timer
    private class Timeout extends Thread {
        private long time;
        private final ServerSocket socket;

        private Timeout(ServerSocket socket, long time) {
            this.time = time;
            this.socket = socket;
        }

        @Override
        public void run() {
            long timer = System.currentTimeMillis() + time * 1000,
                    counter = timer - time * 1000;
            while (!isInterrupted()) {
                if (counter < System.currentTimeMillis()) {
                    counter = System.currentTimeMillis() + 980;
                    sliderValue.setText("Timeout connection: " + time + " sec");
                    time--;
                }
                if (timer < System.currentTimeMillis())
                    try {
                        socket.close();
                        sliderValue.setText("Timeout connection: " + slider1.getValue() + " sec");
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
    }

    // check connection
    private class Connect extends Thread {
        final String mode;
        ServerSocket serverSocket;
        Timeout timeout;

        private Connect(String mode) {
            this.mode = mode;
        }

        @Override
        public void run() {
            switch (mode) {
                case "Client":
                    try {
                        if (checkConnection()) {
                            boolean have = true;
                            for (String currentLine : list)
                                if (currentLine.substring(currentLine.lastIndexOf(" ") + 1).trim().equals(ipAddressFormattedTextField.getText()))
                                    have = false;
                            if (have && save) {
                                String[] strings = new String[list.size() + 1];
                                strings[0] = ipAddressFormattedTextField.getText();
                                for (int i = 1; i < strings.length; i++) strings[i] = list.get(i - 1);
                                Save.main(strings);
                            }
                            end(false);
                            new Client(new Communication(ipAddressFormattedTextField.getText(), Integer.parseInt(portFormattedTextField.getText().trim())));
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    break;
                case "Server":
                    try {
                        if (waitConnection(Integer.parseInt(portFormattedTextField.getText().trim()))) {
                            end(false);
                            new Server(new Communication(Integer.parseInt(portFormattedTextField.getText().trim())));
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    break;
                default:
                    JOptionPane.showConfirmDialog(new JDialog(), "No have this mode", "Error", JOptionPane.CLOSED_OPTION);
                    break;
            }
            if (!connect.isInterrupted()) AL.close();
        }

        // Ждать проверочного подлючения
        private boolean waitConnection(int port) {
            try {
                serverSocket = new ServerSocket(port);
                timeout = new Timeout(serverSocket, slider1.getValue());
                timeout.start();
                serverSocket.accept();
                if (serverSocket.isBound()) {
                    timeout.interrupt();
                    serverSocket.close();
                    return true;
                }
                serverSocket.close();
            } catch (SocketException e) {
                timeout.interrupt();
                //JOptionPane.showConfirmDialog(new JDialog(), "Timeout connection is over", "Timeout", JOptionPane.CLOSED_OPTION);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        private void inter() {
            try {
                sliderValue.setText("Timeout connection: 10 sec");
                AL.close();
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Проверочное подлючение
        private boolean checkConnection() {
            try {
                if (searchCheckBox.isSelected()) {
                    String ip = searchIP(Integer.parseInt(portFormattedTextField.getText().trim()));
                    if (!Objects.equals(ip, null)) {
                        ipAddressFormattedTextField.setText(ip);
                        return true;
                    }
                } else {
                    Socket socket = new Socket(ipAddressFormattedTextField.getText(),
                            Integer.parseInt(portFormattedTextField.getText().trim()));
                    if (socket.isConnected()) {
                        socket.close();
                        return true;
                    }
                }
            } catch (ConnectException ignored) {
                JOptionPane.showConfirmDialog(new JDialog(), "Server is not found. Maybe you or\n" +
                        "\"Server\" have problem with connection.", "Trouble :(", JOptionPane.CLOSED_OPTION);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        // Поиск локального подкоючения
        private String searchIP(int port) {
            String str = localIP();
            Socket socket;
            for (int i = 0; i <= 256; i++) {
                try {
                    socket = new Socket();
                    str = str.substring(0, str.lastIndexOf(".") + 1) + i;
                    socket.connect(new InetSocketAddress(str, port), 15);
                    if (socket.isConnected()) {
                        str = socket.getInetAddress().getHostAddress();
                        socket.close();
                        break;
                    }
                } catch (SocketTimeoutException ignored) {
                } catch (IOException | NullPointerException e) {
                    e.getMessage();
                }
            }
            if (str.endsWith("256")) {
                JOptionPane.showConfirmDialog(new JDialog(), "Server is not found. Maybe you or\n" +
                        "\"Server\" have problem with connection.", "Trouble :(", JOptionPane.CLOSED_OPTION);
                return null;
            }
            int[] dots = new int[3];
            char[] ch, charIPAdr = new char[]{'0', '0', '0', '.', '0', '0', '0', '.', '0', '0', '0', '.', '0', '0', '0',};
            int j = 0;
            for (int i = 0; i < str.length(); i++) {
                if (dots.length == j) break;
                else if (str.substring(i, i + 1).equals(".")) {
                    dots[j] = i;
                    j++;
                }
            }
            ch = str.substring(0, dots[0]).toCharArray();
            System.arraycopy(ch, 0, charIPAdr, 3 - ch.length, ch.length);
            ch = str.substring(dots[0] + 1, dots[1]).toCharArray();
            System.arraycopy(ch, 0, charIPAdr, 7 - ch.length, ch.length);
            ch = str.substring(dots[1] + 1, dots[2]).toCharArray();
            System.arraycopy(ch, 0, charIPAdr, 11 - ch.length, ch.length);
            ch = str.substring(dots[2] + 1).toCharArray();
            System.arraycopy(ch, 0, charIPAdr, 15 - ch.length, ch.length);
            return String.valueOf(charIPAdr);
        }
    }

    // animation of loading
    private class AnimationLoading extends JWindow {
        JLabel label = new JLabel();

        AnimationLoading(Point loc) {
            setLayout(new GridLayout());
            setBackground(new Color(0, 0, 0, 0));
            setSize(140, 140);
            setLoadingLocation(loc);
            label.setIcon(new ImageIcon(DIR_SOURCE_IMAGE + "loading.gif"));
            add(label);
            setVisible(true);
        }

        private void close() {
            dispose();
        }

        private void setLoadingLocation(Point loc) {
            loc = new Point(loc.x + GraphicalInitialSettings.super.getWidth() / 2 - super.getWidth() / 2,
                    loc.y + GraphicalInitialSettings.super.getHeight() / 2 - super.getHeight() / 2);
            super.setLocation(loc);
        }
    }
}
