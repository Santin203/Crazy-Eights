import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GameFileManager {
    private static final String USERS_FILE = "users.txt";
    private static final String DRAW_FILE = "draw.txt";
    private static final String DISCARD_FILE = "discard.txt";
    private static final String DRAWN_STATE_SUFFIX = "_drawn.txt";
    
    public static boolean createGameDirectory(String gameName) {
        File gameDir = new File(gameName);
        if (gameDir.exists()) {
            return false;
        }
        return gameDir.mkdir();
    }
    
    public static boolean gameExists(String gameName) {
        File gameDir = new File(gameName);
        return gameDir.exists() && gameDir.isDirectory();
    }
    
    public static void saveAdminCredentials(String gameName, String hashedPassword) throws IOException {
        String usersFilePath = gameName + File.separator + USERS_FILE;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFilePath))) {
            writer.write("admin," + hashedPassword);
            writer.newLine();
        }
    }
    
    public static void addUser(String gameName, String username, String hashedPassword) throws IOException {

        if (username.equalsIgnoreCase("admin")) {
            throw new IOException("Username 'admin' is reserved and cannot be added manually.");
        }
        
        String usersFilePath = gameName + File.separator + USERS_FILE;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFilePath, true))) {
            writer.write(username + "," + hashedPassword);
            writer.newLine();
        }
    }
    
    public static boolean removeUser(String gameName, String username) throws IOException {
        if (username.equals("admin")) {
            return false;
        }
        
        String usersFilePath = gameName + File.separator + USERS_FILE;
        List<String> lines = Files.readAllLines(Paths.get(usersFilePath));
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFilePath))) {
            boolean found = false;
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length == 2 && !parts[0].equals(username)) {
                    writer.write(line);
                    writer.newLine();
                } else {
                    found = true;
                }
            }

            File handFile = new File(gameName + File.separator + username + ".txt");
            if (handFile.exists()) {
                handFile.delete();
            }
            
            return found;
        }
    }

    public static List<String> getUsers(String gameName) throws IOException {
        String usersFilePath = gameName + File.separator + USERS_FILE;
        List<String> users = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    users.add(parts[0]);
                }
            }
        }
        
        return users;
    }
    

    public static String getHashedPassword(String gameName, String username) throws IOException {
        String usersFilePath = gameName + File.separator + USERS_FILE;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[0].equals(username)) {
                    return parts[1];
                }
            }
        }
        
        return null;
    }
    
    public static void savePlayerHand(String gameName, String username, List<String> cardCodes) throws IOException {
        String playerFilePath = gameName + File.separator + username + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(playerFilePath))) {
            for (String code : cardCodes) {
                writer.write(code);
                writer.newLine();
            }
        }
    }
    
    public static List<String> loadPlayerHand(String gameName, String username) throws IOException {
        String playerFilePath = gameName + File.separator + username + ".txt";
        List<String> cardCodes = new ArrayList<>();
        
        File playerFile = new File(playerFilePath);
        if (!playerFile.exists()) {
            return cardCodes;
        }

        
        try (BufferedReader reader = new BufferedReader(new FileReader(playerFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    cardCodes.add(line);
                }
            }
        }
        
        return cardCodes;
    }
    

    public static void saveDrawPile(String gameName, List<String> cardCodes) throws IOException {
        String drawFilePath = gameName + File.separator + DRAW_FILE;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(drawFilePath))) {
            for (String code : cardCodes) {
                writer.write(code);
                writer.newLine();
            }
        }
    }
    
    public static List<String> loadDrawPile(String gameName) throws IOException {
        String drawFilePath = gameName + File.separator + DRAW_FILE;
        List<String> cardCodes = new ArrayList<>();
        
        File drawFile = new File(drawFilePath);
        if (!drawFile.exists()) {
            return cardCodes;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(drawFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    cardCodes.add(line);
                }
            }
        }
        
        return cardCodes;
    }
    

    public static void saveDiscardPile(String gameName, List<String> cardCodes) throws IOException {
        String discardFilePath = gameName + File.separator + DISCARD_FILE;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(discardFilePath))) {
            for (String code : cardCodes) {
                writer.write(code);
                writer.newLine();
            }
        }
    }
    
    public static List<String> loadDiscardPile(String gameName) throws IOException {
        String discardFilePath = gameName + File.separator + DISCARD_FILE;
        List<String> cardCodes = new ArrayList<>();
        
        File discardFile = new File(discardFilePath);
        if (!discardFile.exists()) {
            return cardCodes;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(discardFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    cardCodes.add(line);
                }
            }
        }
        
        return cardCodes;
    }
    
    public static void savePlayerDrawnState(String gameName, String username, boolean hasDrawn) throws IOException {
        String drawnStateFilePath = gameName + File.separator + username + DRAWN_STATE_SUFFIX;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(drawnStateFilePath))) {
            writer.write(String.valueOf(hasDrawn));
        }
    }
    

    public static boolean loadPlayerDrawnState(String gameName, String username) throws IOException {
        String drawnStateFilePath = gameName + File.separator + username + DRAWN_STATE_SUFFIX;
        File drawnStateFile = new File(drawnStateFilePath);
        
        if (!drawnStateFile.exists()) {
            return false;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(drawnStateFilePath))) {
            String line = reader.readLine();
            if (line != null && !line.isEmpty()) {
                return Boolean.parseBoolean(line.trim());
            }
        }
        
        return false;
    }
}