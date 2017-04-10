package Management;

import DataHandling.GlobalData;
import Windows.Client;
import Windows.Save;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Objects;

// check connection

public class ClientCheck extends Thread {
    private final String ip_address;
    private final int port;
    private final ArrayList<String> friendsList;
    private final boolean search;
    private final GlobalData globalData;
    public boolean connect = false;

    public ClientCheck(ArrayList<String> friendsList, String ip_address, int port, GlobalData globalData, boolean search) {
        this.friendsList = friendsList;
        this.ip_address = ip_address;
        this.port = port;
        this.globalData = globalData;
        this.search = search;
    }

    @Override
    public void run() {
        try {
            if (checkConnection()) {
                boolean have = true;
                for (String currentLine : friendsList)
                    if (currentLine.substring(currentLine.lastIndexOf(" ") + 1).trim().equals(ip_address)) {
                        have = false;
                        break;
                    }
                if (have && !Objects.equals(globalData, null)) {
                    //friendsList.add(ip_address);
                    new Save(globalData, ip_address);
                }
                connect = true;
                new Client(ip_address, port);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    // Проверочное подлючение
    private boolean checkConnection() {
        try {
            if (search) {
                String ip = searchIP(port);
                if (!Objects.equals(ip, null)) return true;
            } else {
                Socket socket = new Socket(ip_address, port);
                if (socket.isConnected()) {
                    socket.close();
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Поиск локального подкоючения
    private String searchIP(int port) {
        String str = ip_address;
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
        if (str.endsWith("256")) return null;

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