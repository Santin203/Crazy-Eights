import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Prompt: Class to handle command-line arguments and execute commands for the Crazy Eights game, based on the commands attached.
 */
public class CommandHandler {
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
            default:
                System.err.println("Unknown command: " + command);
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

        System.out.println("Enter admin password for game '" + gameName + "':");
        String adminPassword = AuthenticationManager.readPassword();
        if (!AuthenticationManager.authenticateAdmin(gameName, adminPassword)) {
            System.err.println("Invalid admin password.");
            return;
        }
        
        try {

            List<String> users = GameFileManager.getUsers(gameName);

            users.removeIf(user -> user.equals("admin")); //
            
            if (users.size() < 2) {
                System.err.println("At least 2 players are required to start a game.");
                return;
            }

            Game game = new Game(gameName, users, new Deck());
            game.initializeGame();
            

            saveGameState(game);
            
            System.out.println("Game started successfully with players: " + String.join(", ", users)); //
            System.out.println("Current turn: " + game.getCurrentPlayer().getName()); //
            System.out.println("Top card: " + game.getTopDiscard().getCode()); //
            
        } catch (IOException e) {
            System.err.println("Error starting game: " + e.getMessage());
        }
    }
    
    private void showTurnOrder() {
        if (username == null) {
            System.err.println("Username not specified. Use --user <username>");
            return;
        }
        
        System.out.println("Enter password for user '" + username + "':");
        String password = AuthenticationManager.readPassword();
        if (!AuthenticationManager.authenticateUser(gameName, username, password)) {
            System.err.println("Invalid user credentials.");
            return;
        }
        
        try {

            Game game = loadGameState();
            if (game == null) {
                return;
            }
            
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
        
        System.out.println("Enter password for user '" + username + "':");
        String password = AuthenticationManager.readPassword();
        if (!AuthenticationManager.authenticateUser(gameName, username, password)) {
            System.err.println("Invalid user credentials.");
            return;
        }
        
        try {
            Game game = loadGameState();
            if (game == null) {
                return;
            }

            if (!game.getCurrentPlayer().getName().equals(username)) {
                System.err.println("It's not your turn. Current player: " + game.getCurrentPlayer().getName());
                return;
            }

            boolean success = game.playCard(card);
            if (!success) {
                System.err.println("Cannot play card " + card + ". Invalid move or card not in hand.");
                return;
            }

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
            System.err.println("Player username not specified. Use --cards <username>");
            return;
        }
        
        System.out.println("Enter password for user '" + username + "':");
        String password = AuthenticationManager.readPassword();
        if (!AuthenticationManager.authenticateUser(gameName, username, password)) {
            System.err.println("Invalid user credentials.");
            return;
        }
        
        try {
            Game game = loadGameState();
            if (game == null) {
                return;
            }

            if (!username.equals(viewUsername) && !username.equals("admin")) {
                System.err.println("You are not authorized to view other players' cards.");
                return;
            }
            
            Player player = game.getPlayerByName(viewUsername);
            if (player == null) {
                System.err.println("Player '" + viewUsername + "' not found.");
                return;
            }

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
        
        System.out.println("Enter password for user '" + username + "':");
        String password = AuthenticationManager.readPassword();
        if (!AuthenticationManager.authenticateUser(gameName, username, password)) {
            System.err.println("Invalid user credentials.");
            return;
        }
        
        try {
            Game game = loadGameState();
            if (game == null) {
                return;
            }

            if (!game.getCurrentPlayer().getName().equals(username)) {
                System.err.println("It's not your turn. Current player: " + game.getCurrentPlayer().getName());
                return;
            }
            
            if (game.getCurrentPlayer().hasDrawnThisTurn()) {
                System.err.println("You have already drawn a card this turn.");
                return;
            }
            
            Card drawnCard = game.drawCard();
            if (drawnCard == null) {
                System.err.println("No cards left in the draw pile.");
                return;
            }
            
            saveGameState(game);
            
            System.out.println("You drew: " + drawnCard.getCode() + " (" + drawnCard + ")");
            
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
        
        System.out.println("Enter password for user '" + username + "':");
        String password = AuthenticationManager.readPassword();
        if (!AuthenticationManager.authenticateUser(gameName, username, password)) {
            System.err.println("Invalid user credentials.");
            return;
        }
        
        try {

            Game game = loadGameState();
            if (game == null) {
                return;
            }
            
            if (!game.getCurrentPlayer().getName().equals(username)) {
                System.err.println("It's not your turn. Current player: " + game.getCurrentPlayer().getName());
                return;
            }

            boolean success = game.passTurn();
            if (!success) {
                System.err.println("Cannot pass. You must draw a card first or play a valid card if possible.");
                return;
            }
            

            saveGameState(game);
            
            System.out.println("Turn passed.");
            System.out.println("Next player: " + game.getCurrentPlayer().getName());
            
        } catch (IOException e) {
            System.err.println("Error passing turn: " + e.getMessage());
        }
    }
    


    private Game loadGameState() throws IOException {

        if (!GameFileManager.gameExists(gameName)) {
            System.err.println("Game '" + gameName + "' does not exist.");
            return null;
        }
        

        List<String> users = GameFileManager.getUsers(gameName);
        
        users.removeIf(user -> user.equals("admin"));
        
        if (users.isEmpty()) {
            System.err.println("No players found for the game.");
            return null;
        }
        
        Game game = new Game(gameName, users, new Deck(new ArrayList<>()));
        
        // Load player hands and states
        for (String user : users) {
            List<String> cardCodes = GameFileManager.loadPlayerHand(gameName, user);
            Player player = game.getPlayerByName(user);
            
            if (player != null) {
                if (!cardCodes.isEmpty()) {
                    for (String code : cardCodes) {
                        player.addCard(Card.fromCode(code));
                    }
                }

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
        
        findCurrentPlayer(game);
        
        return game;
    }
    
    /*
     * Prompt: Find the current player based on the turns file. This method reads the turns file and sets the current player index in the game object.
     */
    private void findCurrentPlayer(Game game) throws IOException {
        String turnsFilePath = gameName + File.separator + "turns.txt";
        File turnsFile = new File(turnsFilePath);
        
        if (turnsFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(turnsFilePath))) {
                String currentPlayerName = reader.readLine();
                if (currentPlayerName != null && !currentPlayerName.isEmpty()) {

                    List<Player> players = game.getPlayers();
                    for (int i = 0; i < players.size(); i++) {
                        if (players.get(i).getName().equals(currentPlayerName)) {

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
        for (Player player : game.getPlayers()) {
            GameFileManager.savePlayerHand(gameName, player.getName(), player.getHandAsCodes());
            GameFileManager.savePlayerDrawnState(gameName, player.getName(), player.hasDrawnThisTurn());
        }
        
        GameFileManager.saveDrawPile(gameName, game.getDrawPile().toCardCodes());
        GameFileManager.saveDiscardPile(gameName, game.getDiscardPile().toCardCodes());

        String turnsFilePath = gameName + File.separator + "turns.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(turnsFilePath))) {
            writer.write(game.getCurrentPlayer().getName());
        }
    }
    
}
