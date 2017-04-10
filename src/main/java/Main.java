import DataHandling.InitializeConsole;
import Windows.GraphicalInitialSettings;

class Main {

    public static void main(String[] args) {
        args = new String[]{"g"};
        if (args[0].equals("g")) {
            new GraphicalInitialSettings();
        } else if (args[0].equals("c")) {
            new InitializeConsole().start();
        }
    }
}