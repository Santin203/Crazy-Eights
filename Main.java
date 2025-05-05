import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Main {
    
    public static void main(String[] args) {
        // Parse command-line arguments
        CommandHandler handler = new CommandHandler(args);
        
        // Process the command
        handler.execute();
    }
    
    // Class to handle command-line arguments and execute commands
    static class CommandHandler {
        private String[] args;
        private String command = null;
        private String gameName = null;
        private String username = null;
        private String card = null;
        private String viewUsername = null;
        
        public CommandHandler(String[] args) {
            this.args = args;
            parseArguments();
        }
        
        private void parseArguments() {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "--init":
                        command = "init";
                        break;
                    case "--add-user":
                        command = "add-user";
                        if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                            username = args[++i];
                        }
                        break;
                    case "--remove-user":
                        command = "remove-user";
                        if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                            username = args[++i];
                        }
                        break;
                    case "--start":
                        command = "start";
                        break;
                    case "--order":
                        command = "order";
                        break;
                    case "--play":
                        command = "play";
                        if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                            card = args[++i];
                        }
                        break;
                    case "--cards":
                        command = "cards";
                        if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                            viewUsername = args[++i];
                        }
                        break;
                    case "--draw":
                        command = "draw";
                        break;
                    case "--pass":
                        command = "pass";
                        break;
                    case "--game":
                        if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                            gameName = args[++i];
                        }
                        break;
                    case "--user":
                        if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                            username = args[++i];
                        }
                        break;
                }
            }
        }
        
        public void execute() {
            if (command == null) {
                System.err.println("No command specified.");
                printUsage();
                return;
            }
            
            if (gameName == null && !command.equals("help")) {
                System.err.println("Game name not specified. Use --game <game_name>");
                return;
            }
            
            switch (command) {
                case "init":
                    initializeGame();
                    break;
                case "add-user":
                    addUser();
                    break;
                case "remove-user":
                    removeUser();
                    break;
                case "start":
                    startGame();
                    break;
                case "order":
                    showTurnOrder();
                    break;
                case "play":
                    playCard();
                    break;
                case "cards":
                    showCards();
                    break;
                case "draw":
                    drawCard();
                    break;
                case "pass":
                    passTurn();
                    break;
                case "help":
                    printUsage();
                    break;
                default:
                    System.err.println("Unknown command: " + command);
                    printUsage();
            }
        }
        
        private void initializeGame() {
            AuthenticationManager.initializeGame(gameName);
        }
        
        private void addUser() {
            if (username == null) {
                System.err.println("Username not specified. Use --add-user <username>");
                return;
            }
            
            AuthenticationManager.addUser(gameName, username);
        }
        
        private void removeUser() {
            if (username == null) {
                System.err.println("Username not specified. Use --remove-user <username>");
                return;
            }
            
            AuthenticationManager.removeUser(gameName, username);
        }
        
        private void startGame() {
            // Authenticate as admin
            System.out.println("Enter admin password for game '" + gameName + "':");
            String adminPassword = AuthenticationManager.readPassword();
            if (!AuthenticationManager.authenticateAdmin(gameName, adminPassword)) {
                System.err.println("Invalid admin password.");
                return;
            }
            
            try {
                // Get list of users
                List<String> users = GameFileManager.getUsers(gameName);
                
                // Remove admin from the list
                users.removeIf(user -> user.equals("admin"));
                
                if (users.size() < 2) {
                    System.err.println("At least 2 players are required to start a game.");
                    return;
                }
                
                // Create and initialize the game
                Game game = new Game(gameName, users, new Deck());
                game.initializeGame();
                
                // Save game state to files
                saveGameState(game);
                
                System.out.println("Game started successfully with players: " + String.join(", ", users));
                System.out.println("Current turn: " + game.getCurrentPlayer().getName());
                System.out.println("Top card: " + game.getTopDiscard().getCode());
                
            } catch (IOException e) {
                System.err.println("Error starting game: " + e.getMessage());
            }
        }
        
        private void showTurnOrder() {
            if (username == null) {
                System.err.println("Username not specified. Use --user <username>");
                return;
            }
            
            // Authenticate user
            System.out.println("Enter password for user '" + username + "':");
            String password = AuthenticationManager.readPassword();
            if (!AuthenticationManager.authenticateUser(gameName, username, password)) {
                System.err.println("Invalid user credentials.");
                return;
            }
            
            try {
                // Load game state
                Game game = loadGameState();
                if (game == null) {
                    return;
                }
                
                // Get turn order
                List<String> turnOrder = game.getTurnOrder();
                
                System.out.println("Turn order:");
                for (int i = 0; i < turnOrder.size(); i++) {
                    System.out.println((i + 1) + ". " + turnOrder.get(i));
                }
                
            } catch (IOException e) {
                System.err.println("Error showing turn order: " + e.getMessage());
            }
        }
        
        private void playCard() {
            if (username == null) {
                System.err.println("Username not specified. Use --user <username>");
                return;
            }
            
            if (card == null) {
                System.err.println("Card not specified. Use --play <card>");
                return;
            }
            
            // Authenticate user
            System.out.println("Enter password for user '" + username + "':");
            String password = AuthenticationManager.readPassword();
            if (!AuthenticationManager.authenticateUser(gameName, username, password)) {
                System.err.println("Invalid user credentials.");
                return;
            }
            
            try {
                // Load game state
                Game game = loadGameState();
                if (game == null) {
                    return;
                }
                
                // Check if it's the user's turn
                if (!game.getCurrentPlayer().getName().equals(username)) {
                    System.err.println("It's not your turn. Current player: " + game.getCurrentPlayer().getName());
                    return;
                }
                
                // Attempt to play the card
                boolean success = game.playCard(card);
                if (!success) {
                    System.err.println("Cannot play card " + card + ". Invalid move or card not in hand.");
                    return;
                }
                
                // Save updated game state
                saveGameState(game);
                
                System.out.println("Card " + card + " played successfully.");
                
                if (game.isGameOver()) {
                    Player winner = game.getWinner();
                    System.out.println("Game over! Winner: " + winner.getName());
                } else {
                    System.out.println("Next player: " + game.getCurrentPlayer().getName());
                }
                
            } catch (IOException e) {
                System.err.println("Error playing card: " + e.getMessage());
            }
        }
        
        private void showCards() {
            if (username == null) {
                System.err.println("Username not specified. Use --user <username>");
                return;
            }
            
            if (viewUsername == null) {
                viewUsername = username;  // Default to showing own cards
            }
            
            // Authenticate user
            System.out.println("Enter password for user '" + username + "':");
            String password = AuthenticationManager.readPassword();
            if (!AuthenticationManager.authenticateUser(gameName, username, password)) {
                System.err.println("Invalid user credentials.");
                return;
            }
            
            try {
                // Load game state
                Game game = loadGameState();
                if (game == null) {
                    return;
                }
                
                // Check if allowed to view cards
                if (!username.equals(viewUsername) && !username.equals("admin")) {
                    System.err.println("You are not authorized to view other players' cards.");
                    return;
                }
                
                // Get player's cards
                Player player = game.getPlayerByName(viewUsername);
                if (player == null) {
                    System.err.println("Player '" + viewUsername + "' not found.");
                    return;
                }
                
                // Show cards
                List<Card> hand = player.getHand();
                Card topDiscard = game.getTopDiscard();
                
                System.out.println(viewUsername + "'s cards (" + hand.size() + "):");
                for (Card card : hand) {
                    String validMove = Rules.isValidPlay(card, topDiscard) ? " (Valid Move)" : "";
                    System.out.println("- " + card.getCode() + " (" + card + ")" + validMove);
                }
                
                System.out.println("\nTop card on discard pile: " + topDiscard.getCode() + " (" + topDiscard + ")");
                
            } catch (IOException e) {
                System.err.println("Error showing cards: " + e.getMessage());
            }
        }
        
        private void drawCard() {
            if (username == null) {
                System.err.println("Username not specified. Use --user <username>");
                return;
            }
            
            // Authenticate user
            System.out.println("Enter password for user '" + username + "':");
            String password = AuthenticationManager.readPassword();
            if (!AuthenticationManager.authenticateUser(gameName, username, password)) {
                System.err.println("Invalid user credentials.");
                return;
            }
            
            try {
                // Load game state
                Game game = loadGameState();
                if (game == null) {
                    return;
                }
                
                // Check if it's the user's turn
                if (!game.getCurrentPlayer().getName().equals(username)) {
                    System.err.println("It's not your turn. Current player: " + game.getCurrentPlayer().getName());
                    return;
                }
                
                // Check if the player has already drawn this turn
                if (game.getCurrentPlayer().hasDrawnThisTurn()) {
                    System.err.println("You have already drawn a card this turn.");
                    return;
                }
                
                // Draw a card
                Card drawnCard = game.drawCard();
                if (drawnCard == null) {
                    System.err.println("No cards left in the draw pile.");
                    return;
                }
                
                // Save updated game state
                saveGameState(game);
                
                System.out.println("You drew: " + drawnCard.getCode() + " (" + drawnCard + ")");
                
                // Show updated hand
                List<Card> hand = game.getCurrentPlayer().getHand();
                Card topDiscard = game.getTopDiscard();
                
                System.out.println("\nYour cards (" + hand.size() + "):");
                for (Card card : hand) {
                    String validMove = Rules.isValidPlay(card, topDiscard) ? " (Valid Move)" : "";
                    System.out.println("- " + card.getCode() + " (" + card + ")" + validMove);
                }
                
                System.out.println("\nTop card on discard pile: " + topDiscard.getCode() + " (" + topDiscard + ")");
                
            } catch (IOException e) {
                System.err.println("Error drawing card: " + e.getMessage());
            }
        }
        
        private void passTurn() {
            if (username == null) {
                System.err.println("Username not specified. Use --user <username>");
                return;
            }
            
            // Authenticate user
            System.out.println("Enter password for user '" + username + "':");
            String password = AuthenticationManager.readPassword();
            if (!AuthenticationManager.authenticateUser(gameName, username, password)) {
                System.err.println("Invalid user credentials.");
                return;
            }
            
            try {
                // Load game state
                Game game = loadGameState();
                if (game == null) {
                    return;
                }
                
                // Check if it's the user's turn
                if (!game.getCurrentPlayer().getName().equals(username)) {
                    System.err.println("It's not your turn. Current player: " + game.getCurrentPlayer().getName());
                    return;
                }
                
                // Attempt to pass the turn
                boolean success = game.passTurn();
                if (!success) {
                    System.err.println("Cannot pass. You must draw a card first or play a valid card if possible.");
                    return;
                }
                
                // Save updated game state
                saveGameState(game);
                
                System.out.println("Turn passed.");
                System.out.println("Next player: " + game.getCurrentPlayer().getName());
                
            } catch (IOException e) {
                System.err.println("Error passing turn: " + e.getMessage());
            }
        }
        
        private Game loadGameState() throws IOException {
            // Check if game exists
            if (!GameFileManager.gameExists(gameName)) {
                System.err.println("Game '" + gameName + "' does not exist.");
                return null;
            }
            
            // Get users
            List<String> users = GameFileManager.getUsers(gameName);
            
            // Remove admin from the list
            users.removeIf(user -> user.equals("admin"));
            
            if (users.isEmpty()) {
                System.err.println("No players found for the game.");
                return null;
            }
            
            // Create game
            Game game = new Game(gameName, users, new Deck(new ArrayList<>()));
            
            // Load player hands and states
            for (String user : users) {
                List<String> cardCodes = GameFileManager.loadPlayerHand(gameName, user);
                Player player = game.getPlayerByName(user);
                
                if (player != null) {
                    // Load cards
                    if (!cardCodes.isEmpty()) {
                        for (String code : cardCodes) {
                            player.addCard(Card.fromCode(code));
                        }
                    }
                    
                    // Load drawn state
                    boolean hasDrawn = GameFileManager.loadPlayerDrawnState(gameName, user);
                    player.setHasDrawnThisTurn(hasDrawn);
                }
            }
            
            // Load draw pile
            List<String> drawCodes = GameFileManager.loadDrawPile(gameName);
            Deck drawPile = Deck.fromCardCodes(drawCodes);
            for (Card card : drawPile.getCards()) {
                game.getDrawPile().addCard(card);
            }
            
            // Load discard pile
            List<String> discardCodes = GameFileManager.loadDiscardPile(gameName);
            Deck discardPile = Deck.fromCardCodes(discardCodes);
            game.getDiscardPile().getCards().clear();
            for (Card card : discardPile.getCards()) {
                game.getDiscardPile().addCard(card);
            }
            
            // Determine current player
            findCurrentPlayer(game);
            
            return game;
        }
        
        // Determine which player's turn it is based on game state
        private void findCurrentPlayer(Game game) throws IOException {
            // Check if there's a turns.txt file that stores the current player
            String turnsFilePath = gameName + File.separator + "turns.txt";
            File turnsFile = new File(turnsFilePath);
            
            if (turnsFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(turnsFilePath))) {
                    String currentPlayerName = reader.readLine();
                    if (currentPlayerName != null && !currentPlayerName.isEmpty()) {
                        // Find the index of this player and set it as current
                        List<Player> players = game.getPlayers();
                        for (int i = 0; i < players.size(); i++) {
                            if (players.get(i).getName().equals(currentPlayerName)) {
                                // Use reflection to set the currentPlayerIndex field
                                try {
                                    Field indexField = Game.class.getDeclaredField("currentPlayerIndex");
                                    indexField.setAccessible(true);
                                    indexField.set(game, i);
                                } catch (NoSuchFieldException | IllegalAccessException e) {
                                    System.err.println("Error setting current player: " + e.getMessage());
                                }
                                break;
                            }
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error reading current player: " + e.getMessage());
                }
            }
        }
        
        private void saveGameState(Game game) throws IOException {
            // Save player hands
            for (Player player : game.getPlayers()) {
                GameFileManager.savePlayerHand(gameName, player.getName(), player.getHandAsCodes());
                // Save whether player has drawn this turn
                GameFileManager.savePlayerDrawnState(gameName, player.getName(), player.hasDrawnThisTurn());
            }
            
            // Save draw pile - only save the current state, not adding new cards
            GameFileManager.saveDrawPile(gameName, game.getDrawPile().toCardCodes());
            
            // Save discard pile
            GameFileManager.saveDiscardPile(gameName, game.getDiscardPile().toCardCodes());
            
            // Save current player's turn
            String turnsFilePath = gameName + File.separator + "turns.txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(turnsFilePath))) {
                writer.write(game.getCurrentPlayer().getName());
            }
        }
        
        private void printUsage() {
            System.out.println("Crazy Eights Game - Usage:");
            System.out.println("  java CrazyEights --init --game <game_name>");
            System.out.println("  java CrazyEights --add-user <user_name> --game <game_name>");
            System.out.println("  java CrazyEights --remove-user <user_name> --game <game_name>");
            System.out.println("  java CrazyEights --start --game <game_name>");
            System.out.println("  java CrazyEights --order --user <user_name> --game <game_name>");
            System.out.println("  java CrazyEights --play <card> --user <user_name> --game <game_name>");
            System.out.println("  java CrazyEights --cards [<other_user>] --user <user_name> --game <game_name>");
            System.out.println("  java CrazyEights --draw --user <user_name> --game <game_name>");
            System.out.println("  java CrazyEights --pass --user <user_name> --game <game_name>");
        }
    }
}