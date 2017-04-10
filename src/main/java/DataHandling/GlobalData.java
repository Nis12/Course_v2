package DataHandling;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

public class GlobalData implements Directory {

    public ArrayList<String> fList = new ArrayList<>();
    public Properties connectSettings = new Properties();

    public GlobalData() {
        try {
            FileInputStream fis = new FileInputStream("conset.xml");
            connectSettings.loadFromXML(fis);
            BufferedReader input = new BufferedReader(new FileReader(FRIENDS_LIST));
            String line;
            while ((line = input.readLine()) != null) fList.add(line);
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveProp(String  port, String save, String search) {

        connectSettings.setProperty("PORT", port);
        connectSettings.setProperty("SAVE", save);
        connectSettings.setProperty("SEARCH", search);
        try {
            FileOutputStream fos = new FileOutputStream("conset.xml");
            connectSettings.storeToXML(fos,"Connect Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void deleteAllFriendsFromList() {
        fList = new ArrayList<>();
        try {
            File file = new File(FRIENDS_LIST);
        if (file.exists()) {
            file.delete();
            file.createNewFile();
        } else file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFriendFromList (String friend) {
        for (int i = 0; i < fList.size(); i++)
            if (fList.get(i).substring(fList.get(i).indexOf(" ")).equals(friend))
                fList.remove(i);
      }

    public void addFriendsList(String name, String IP_address){
        fList.add(name + " " + IP_address);
    }

    public void saveFriendsList() {
        try {
            File file = new File(FRIENDS_LIST);
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            } else file.createNewFile();
            FileWriter writer = new FileWriter(FRIENDS_LIST, true);
            PrintWriter printWriter = new PrintWriter(writer);
            for (String str : fList) printWriter.println(str);
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

