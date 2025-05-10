import java.io.Console;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;

public class AuthenticationManager {

    /*
     * Prompt: Hash the password and encode it in Base64.
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA3-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) { //
            throw new RuntimeException("Error hashing password", e); //
        }
    }
    
    /*
     * Prompt: Authenticate the admin using the hashed password
     */
    public static boolean authenticateAdmin(String gameName, String password) {
        try {
            String storedHash = GameFileManager.getHashedPassword(gameName, "admin");
            if (storedHash == null) {
                return false;
            }
            
            String inputHash = hashPassword(password);
            return storedHash.equals(inputHash);
        } catch (IOException e) {
            System.err.println("Error authenticating admin: " + e.getMessage());
            return false;
        }
    }
    

    public static boolean authenticateUser(String gameName, String username, String password) {
        try {
            String storedHash = GameFileManager.getHashedPassword(gameName, username);
            if (storedHash == null) {
                return false;
            }
            
            String inputHash = hashPassword(password);
            return storedHash.equals(inputHash);
        } catch (IOException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
            return false;
        }
    }
    
    /*
     * Make a method to read from the console without echoing the password.
     */
    public static String readPassword() {
        Console console = System.console();
        if (console != null) {
            char[] passwordChars = console.readPassword();
            String password = new String(passwordChars);
            // Clear the password from memory
            for (int i = 0; i < passwordChars.length; i++) {
                passwordChars[i] = ' ';
            }
            return password;
        } else {
            // If console is not available (e.g., running from an IDE)
            Scanner scanner = new Scanner(System.in);
            System.out.print("Password (input will be visible): ");
            return scanner.nextLine();
        }
    }
    
    public static boolean initializeGame(String gameName) {

        if (GameFileManager.gameExists(gameName)) {
            System.err.println("Game '" + gameName + "' already exists.");
            return false;
        }
        
        if (!GameFileManager.createGameDirectory(gameName)) {
            System.err.println("Failed to create game directory.");
            return false;
        }

        System.out.println("Set an admin password for game '" + gameName + "':");
        String password = readPassword();
        
        try {
            String hashedPassword = hashPassword(password);
            GameFileManager.saveAdminCredentials(gameName, hashedPassword);
            System.out.println("Game '" + gameName + "' initialized successfully.");
            return true;
        } catch (IOException e) {
            System.err.println("Error saving admin credentials: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean addUser(String gameName, String username) {

        if (!GameFileManager.gameExists(gameName)) {
            System.err.println("Game '" + gameName + "' does not exist.");
            return false;
        }
        
        if (username.equalsIgnoreCase("admin")) {
            System.err.println("Username 'admin' is reserved and cannot be used as a player name.");
            return false;
        }
        
        System.out.println("Enter admin password for game '" + gameName + "':");
        String adminPassword = readPassword();
        if (!authenticateAdmin(gameName, adminPassword)) {
            System.err.println("Invalid admin password.");
            return false;
        }
        
        System.out.println("Set a password for user '" + username + "':");
        String userPassword = readPassword();
        
        try {
            String hashedPassword = hashPassword(userPassword);
            GameFileManager.addUser(gameName, username, hashedPassword);
            System.out.println("User '" + username + "' added successfully.");
            return true;
        } catch (IOException e) {
            System.err.println("Error adding user: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean removeUser(String gameName, String username) {

        if (!GameFileManager.gameExists(gameName)) {
            System.err.println("Game '" + gameName + "' does not exist.");
            return false;
        }
        
        System.out.println("Enter admin password for game '" + gameName + "':");
        String adminPassword = readPassword();
        if (!authenticateAdmin(gameName, adminPassword)) {
            System.err.println("Invalid admin password.");
            return false;
        }
        
        try {
            boolean removed = GameFileManager.removeUser(gameName, username);
            if (removed) {
                System.out.println("User '" + username + "' removed successfully.");
                return true;
            } else {
                System.err.println("User '" + username + "' not found or cannot be removed.");
                return false;
            }
        } catch (IOException e) {
            System.err.println("Error removing user: " + e.getMessage());
            return false;
        }
    }
}