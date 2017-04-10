package DataHandling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class InitializeConsole extends Thread  {


    private BufferedReader reader;

    private void chat() {

    }

    private void server(){

    }

    private void client() {
        System.out.println("Select connection option" +
                "Start:" +
                "\n1 - To use the last/current IP address and PORT" +
                "\n2 - To search IP from localhost" +
                "Settings:" +
                "\n3 - To select IP from friend list" +
                "\n4 - To write IP address" +
                "\n5 - To write PORT");

        //new ClientCheck(list,);
    }

    private static boolean pat(String string){
        Pattern pattern = Pattern.compile("[sc]");
        return pattern.matcher(string).matches();
    }

    @Override
    public void run() {
        System.out.println(
                "Welcome!\n" +
                "Choose one of the mode:\n" +
                "c - client\n" +
                "s - server");
        reader = new BufferedReader(new InputStreamReader(System.in));
        String readLine;
        try {
            while (!pat(readLine = reader.readLine().toLowerCase())) {
                System.out.println(readLine + " - non-existent mode, choose from this option:\n" +
                        "c - client mode\n" +
                        "s - server mode");
            }
            switch (readLine) {
                case "c": client();
                case "s": server();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}