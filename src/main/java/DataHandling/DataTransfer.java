package DataHandling;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

import static DataHandling.GlobalData.DIR_SOURCE_IMAGE;

public class DataTransfer extends Thread {
    private final DatagramSocket datagramSocket;
    private final boolean server_mode;
    private final Socket socket;
    private final File DESKTOP_SCREEN = new File("src//main//resources//image//desktop//desktop_screen.jpg");
    private static final File GET_DESKTOP_SCREEN = new File("src//main//resources//image//desktop//get_screen.jpg");
    private final int
            maxYScreen = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight(),
            maxXScreen = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    private boolean drawController = false;
    private int height = 500, width = 500, mouseAxisX, mouseAxisY;
    private Point mouseLocation;


    DataTransfer(Socket socket) throws SocketException {
        datagramSocket = new DatagramSocket();
        server_mode = true;
        this.socket = socket;
    }

    DataTransfer(int port) throws SocketException {
        datagramSocket = new DatagramSocket(port);
        server_mode = false;
        socket = null;
    }

    // Создание скриншота
    private void screen() {
        try {
            int locationAxisX, locationAxisY;
            Robot robot = new Robot();
            Point location = MouseInfo.getPointerInfo().getLocation();
            if (width != 0 && height != 0) {
                locationAxisX = mouseAxisX - width / 2;
                locationAxisY = mouseAxisY - height / 2;
                if (mouseAxisX < 0) locationAxisX = 0;
                if (mouseAxisY < 0) locationAxisY = 0;
                if (mouseAxisX + width > maxXScreen) locationAxisX = maxXScreen - width;
                if (mouseAxisY + height > maxYScreen) locationAxisY = maxYScreen - height;
            } else {
                locationAxisX = 0;
                locationAxisY = 0;
                height = maxYScreen;
                width = maxXScreen;
            }
            BufferedImage screenShot = robot.createScreenCapture(new Rectangle(locationAxisX, locationAxisY, width, height));
            if (drawController) {
                Graphics2D g2d = screenShot.createGraphics();
                g2d.drawImage(ImageIO.read(new File(DIR_SOURCE_IMAGE + "Cursor.png")), mouseAxisX, mouseAxisY, null);
            }
            ImageIO.write(screenShot, "jpg", DESKTOP_SCREEN);
        } catch (NullPointerException | AWTException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        DatagramPacket datagramPacket;
        byte[] data = new byte[30000];
        try {
            if (server_mode) { // Передача
                FileInputStream fileInputStream;
                while (true) {
                    System.out.println("SerF");
                    data = new byte[30000];
                    screen();
                    fileInputStream = new FileInputStream(DESKTOP_SCREEN);
                    while (fileInputStream.read(data) != -1) {
                        assert socket != null;
                        datagramPacket = new DatagramPacket(data, data.length, socket.getInetAddress(), socket.getPort());
                        datagramSocket.send(datagramPacket);
                    }
                    fileInputStream.close();
                }
            } else { // Прием
                FileOutputStream fileOutputStream;
                while (true) {
                    System.out.println("CliF");
                    datagramPacket = new DatagramPacket(data, data.length);
                    fileOutputStream = new FileOutputStream(GET_DESKTOP_SCREEN);
                    datagramSocket.receive(datagramPacket);
                    fileOutputStream.write(data);
                    fileOutputStream.flush();
                }
            }
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
    }
}
