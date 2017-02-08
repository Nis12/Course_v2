package Management;

import java.io.*;
import java.util.*;

public class ManagementCommands extends Thread {
    private final Process process;
    private final PrintWriter pw;
    private final InputStream istrm;
    private final InputStream errtrm;
    private final Map<String, String> descriptionLocCom = new HashMap<>();
    private ArrayList<String> comStr = new ArrayList<>();

    public ManagementCommands() throws IOException {
        String OPERATION_SYSTEM = System.getProperty("os.name").toUpperCase().substring(0, System.getProperty("os.name").indexOf(" "));
        descriptionLocCom.put("info", "Information about local commands");
        descriptionLocCom.put("givi", "Start transfer picture desktop");
        if (OPERATION_SYSTEM.contains("WINDOWS")) {
            process = new ProcessBuilder("cmd").start();
            pw = new PrintWriter(process.getOutputStream(), true);
            errtrm = process.getErrorStream();
            istrm = process.getInputStream();
            new InputConsole().start();
            new InputError().start();
            pw.println("chcp 65001");
        } else {
            process = null;
            pw = null;
            errtrm = null;
            istrm = null;
        }
    }

    // исполнение команд
    public String[] command(String command) {
        pw.println(command);
        int i;
        do {
            i = comStr.size();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (i < comStr.size());
        String[] strings = null;
        if (comStr.size() > 0) {
            strings = new String[i];
            for (int j = 0; j < strings.length; j++) strings[j] = comStr.get(j);
            comStr = new ArrayList<>();
        }
        return strings;
    }

    /*private boolean localCommand(String com) {
        try {
            descriptionLocCom.get(com.toLowerCase());
            return true;
        } catch (Exception e) {
            return false;
        }
    }*/

    class InputConsole extends Thread {

        @Override
        public void run() {
            try {
                assert istrm != null;
                BufferedReader isr = new BufferedReader(new InputStreamReader(istrm));
                String string;
                while ((string = isr.readLine()) != null) comStr.add(string);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class InputError extends Thread {

        @Override
        public void run() {
            try {
                assert errtrm != null;
                BufferedReader isr = new BufferedReader(new InputStreamReader(errtrm));
                String string;
                while ((string = isr.readLine()) != null) comStr.add(string);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        pw.close();
        errtrm.close();
        istrm.close();
        process.destroy();
    }
}

