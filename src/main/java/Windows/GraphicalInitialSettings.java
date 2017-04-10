package Windows;

import DataHandling.Directory;
import DataHandling.GlobalData;
import Management.ClientCheck;
import Management.ServerCheck;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.util.Collections;

public class GraphicalInitialSettings extends JFrame implements Directory {
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

    private boolean save = true;
    private boolean click = false;
    private GlobalData globalData;
    private ServerCheck serverCheck;
    private ClientCheck clientCheck;
    private Check check;


    public GraphicalInitialSettings() {
        super("Initialization");
        setContentPane(contentPane);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        super.setLocation(200, 200);
        setVisible(true);
        pack();

        globalData = new GlobalData();
        portFormattedTextField.setText(globalData.connectSettings.getProperty("PORT"));
        searchCheckBox.setSelected(Boolean.parseBoolean(globalData.connectSettings.getProperty("SEARCH")));
        save = Boolean.parseBoolean(globalData.connectSettings.getProperty("SAVE"));
        controlTextAndEnabled_ipAddressFormattedTextField();

        saveLabel.setText("");
        saveLabel.setIcon(new ImageIcon(DIR_SOURCE_IMAGE + save + "_save.png"));
        globalIPTextField.setText(ethernetIP());
        localIPTextField.setText(localIP());
        popup = new JPopupMenu();

        for (String str : globalData.fList) friendsComboBox.addItem(str);

        // Remove one string
        JMenuItem removeItem = new JMenuItem("Remove");
        removeItem.addActionListener(e -> deleteFriendFromList());
        popup.add(removeItem);

        // Clean all friend list
        JMenuItem cleanItem = new JMenuItem("Clean");
        cleanItem.addActionListener(e -> {
            friendsComboBox.removeAllItems();
            globalData.deleteAllFriendsFromList();
        });
        popup.add(cleanItem);

        // Add popup menu
        friendsComboBox.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                if (SwingUtilities.isRightMouseButton(event)) popup.show(friendsComboBox, event.getX(), event.getY());
            }
        });

        // Client
        clientButton.addActionListener(e -> {
            if (click) {
                AL.close();
                check.interrupt();
                clientCheck.interrupt();
            } else {
                AL = new AnimationLoading(GraphicalInitialSettings.super.getLocation());
                check = new Check(false);
                check.start();
            }
        });
        // Server
        serverButton.addActionListener(e -> {
            if (click) {
                AL.close();
                check.interrupt();
                serverCheck.interrupt();
            } else {
                AL = new AnimationLoading(GraphicalInitialSettings.super.getLocation());
                check = new Check(true);
                check.start();
            }
            click = !click;
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
                Collections.swap(globalData.fList, 0, friendsComboBox.getSelectedIndex());
                friendsComboBox.removeAllItems();
                for (String str : globalData.fList) friendsComboBox.addItem(str);
            } catch (NullPointerException ignored) {}
        });

        // click to combobox
        searchCheckBox.addActionListener(e -> controlTextAndEnabled_ipAddressFormattedTextField());
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

    private void controlTextAndEnabled_ipAddressFormattedTextField(){
        if (!searchCheckBox.isSelected()) ipAddressFormattedTextField.setText("192168001100");
        else ipAddressFormattedTextField.setText("192168");
        ipAddressFormattedTextField.setEnabled(!searchCheckBox.isSelected());
    }

    // end InitialSettings
    private void end(boolean exit) {
        globalData.saveFriendsList();
        globalData.saveProp(portFormattedTextField.getText(), String.valueOf(save), String.valueOf(searchCheckBox.isSelected()));
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

    private void deleteFriendFromList() {
        globalData.deleteFriendFromList(friendsComboBox.getSelectedItem().toString());
        friendsComboBox.removeAllItems();
        for (String str : globalData.fList) friendsComboBox.addItem(str);
        friendsComboBox.setSelectedIndex(0);
    }

    private class Check extends Thread {

        final boolean serv;

        private Check(boolean serv) {
            this.serv = serv;
        }

        @Override
        public void run() {
            if (serv) {
                serverCheck = new ServerCheck(Integer.parseInt(portFormattedTextField.getText().trim()), slider1.getValue());
                serverCheck.start();
                try {
                    serverCheck.join();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                if (serverCheck.connect) {
                    AL.close();
                    end(false);
                }
            } else {
                String ipAddress;
                if (searchCheckBox.isSelected()) ipAddress = localIPTextField.getText();
                else ipAddress = ipAddressFormattedTextField.getText();
                if (save) clientCheck = new ClientCheck(globalData.fList, ipAddress, Integer.parseInt(portFormattedTextField.getText().trim()), globalData, searchCheckBox.isSelected());
                else clientCheck = new ClientCheck(globalData.fList, ipAddress, Integer.parseInt(portFormattedTextField.getText().trim()), null, searchCheckBox.isSelected());
                clientCheck.start();
                try {
                    clientCheck.join();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                if (clientCheck.connect) {
                    AL.close();
                    end(false);
                }
            }
            AL.close();
        }
    }

    // animation of loading
    private class AnimationLoading extends JWindow {
        JLabel label = new JLabel();
        final Point loc;

        AnimationLoading(Point loc) {
            this.loc = loc;
            start();
        }

        private void close() {
            dispose();
        }

        private void setLoadingLocation(Point loc) {
            loc = new Point(loc.x + GraphicalInitialSettings.super.getWidth() / 2 - super.getWidth() / 2,
                    loc.y + GraphicalInitialSettings.super.getHeight() / 2 - super.getHeight() / 2);
            super.setLocation(loc);
        }

        private void start() {
            setLayout(new GridLayout());
            setBackground(new Color(0, 0, 0, 0));
            setSize(140, 140);
            setLoadingLocation(loc);
            label.setIcon(new ImageIcon(DIR_SOURCE_IMAGE + "loading.gif"));
            add(label);
            setVisible(true);
        }

        @Override
        protected void finalize() throws Throwable {
            dispose();
        }
    }
}
