package Management;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

public class Communication extends Thread {

    public boolean loopWorkStatus = true, alert = true;
    private boolean access = true;
    private final ObjectOutputStream objectOutputStream;
    private final ObjectInputStream objectInputStream;
    private final Socket socket;
    private final Security security;
    private final JTextArea TAmessages, TAcommands;
    private final ManagementCommands commands;
    private String pass = null;
    private enum Alert {connect, disconnect, message}
    public enum Mode {MESSAGE, COMMAND, PARAMETERS, ACCESS, LINES}

    public Communication(int port) throws IOException {
        TAcommands = null;
        TAmessages = null;
        socket = new ServerSocket(port).accept();
        playMelody(Alert.connect);
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        security = new Security(objectInputStream, objectOutputStream, true);
        changePassword();
        security.start();
        try {
            security.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.start();
        commands = new ManagementCommands();
    }

    public Communication(String ip_address, int port) throws IOException {
        TAcommands = null;
        TAmessages = null;
        socket = new Socket(ip_address, port);
        playMelody(Alert.connect);
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        security = new Security(objectInputStream, objectOutputStream, false);
        security.start();
        try {
            security.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.start();
        commands = null;
    }

    public Communication(int port, JTextArea forMess, JTextArea forComm) throws IOException {
        TAcommands = forComm;
        TAmessages = forMess;
        socket = new ServerSocket(port).accept();
        playMelody(Alert.connect);
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        security = new Security(objectInputStream, objectOutputStream, true);
        changePassword();
        security.start();
        try {
            security.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.start();
        commands = new ManagementCommands();
    }

    public Communication(String ip_address, int port, JTextArea forMess, JTextArea forComm) throws IOException {
        TAcommands = forComm;
        TAmessages = forMess;
        socket = new Socket(ip_address, port);
        playMelody(Alert.connect);
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        security = new Security(objectInputStream, objectOutputStream, false);
        security.start();
        try {
            security.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.start();
        commands = null;
    }

    // generate password
    public String changePassword() {
        Random random;
        char[] charPassword = new char[13];
        int i = 0, randomInt;
        while (i < 13) {
            random = new Random();
            randomInt = random.nextInt(122) + 48;
            if (randomInt <= 57 || randomInt >= 65 && randomInt <= 90 || randomInt >= 97 && randomInt <= 122) {
                charPassword[i] = (char) ((byte) randomInt & 0x00FF);
                i++;
            }
        }
        pass = String.valueOf(charPassword);
        access = !access;
        return pass;
    }

    // play melody
    private void playMelody(Alert al) {
        if (alert) {
            try {
                File soundFile = new File("src//main//resources//sounds//" + al + ".wav");
                AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                clip.setFramePosition(0);
                clip.start();
                Thread.sleep(clip.getMicrosecondLength() / 1000);
                clip.stop();
                clip.close();
            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // get message
    @Override
    public void run() {
        Object getObject;
        try {
            while (loopWorkStatus) {
                getObject = objectInputStream.readObject();
                if (!Objects.equals(getObject, null)) objectProcessing(getObject);
            }
            objectOutputStream.close();
            objectInputStream.close();
        } catch (NullPointerException ignored) {
        } catch (IOException e) {
            playMelody(Alert.disconnect);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // say message
    public void sayStringMessage(Object message, Mode mode) {
        try {
            switch (mode) {
                case MESSAGE:
                    objectOutputStream.writeObject(security.write(mode.toString() + message.toString()));
                    break;
                case COMMAND:
                    objectOutputStream.writeObject(security.write(mode.toString() + message.toString() + pass));
                    break;
                case PARAMETERS:
                    objectOutputStream.writeObject(security.write(mode.toString() + message.toString()));
                    break;
                case ACCESS:
                    if (access) objectOutputStream.writeObject(security.write(mode.toString() + message.toString()));
                    break;
                case LINES:
                    objectOutputStream.writeObject(security.write(mode.toString() + message.toString()));
                    TAcommands.append(message.toString() + "\n");
                    break;
                default: System.err.println("Error");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            JOptionPane.showConfirmDialog(new JDialog(), "Security error!\nTry again perform connecting", "Connect failed", JOptionPane.YES_NO_OPTION);
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    // message processing
    private void objectProcessing(Object object) throws UnsupportedEncodingException {
        String getMess;
        if (object.getClass().getName().equals("[B")) {
            getMess = security.read(object);
            System.out.println(getMess);
            if (getMess.startsWith(String.valueOf(Mode.MESSAGE))) {
                print(TAmessages, time() + " Interlocutor:\n" + getMess.substring(7) + "\n\n");
                playMelody(Alert.message);
            } else if (getMess.startsWith(String.valueOf(Mode.COMMAND)) && getMess.endsWith(pass)) {
                String[] strings = commands.command(getMess.substring(7, getMess.length() - pass.length()));
                for (String string : strings) sayStringMessage(string, Mode.LINES);
            } else if (getMess.startsWith(String.valueOf(Mode.ACCESS))) {
                pass = getMess.substring(6);
                print(TAcommands, "Access was obtained\n");
            } else if (getMess.startsWith(String.valueOf(Mode.LINES))) {
                print(TAcommands, getMess.substring(5));
            }
        }
    }

    // determine where to display the text
    private void print(JTextArea ta, String string){
        if (ta.equals(TAmessages)) {
            if (!Objects.equals(ta, null)) ta.append(string);
            else System.out.println("\nmess:" + string);
        } else if (ta.equals(TAcommands)){
            if (!Objects.equals(ta, null)) ta.append(string);
            else System.out.println("#" + string);
        }
    }

    // current time
    public String time() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    @Override
    protected void finalize() throws Throwable {
        socket.close();
        // Очистить папку со скриншотами
        /*for (File myFile : new File(GET_DESKTOP_SCREEN).listFiles())
            if (myFile.isFile()) myFile.delete();*/
    }
}