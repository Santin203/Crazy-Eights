import java.util.ArrayList;
import java.util.List;

public class Game {
    private List<Player> players;
    private Deck drawPile;
    private Deck discardPile;
    private int currentPlayerIndex;
    private boolean gameOver;
    private String gameName;
    private boolean reverseDirection;
    
    public Game(String gameName, List<String> playerNames, Deck drawPile) {
        this.gameName = gameName;
        this.players = new ArrayList<>();
        for (String name : playerNames) {
            players.add(new Player(name));
        }
        this.drawPile = drawPile;
        this.discardPile = new Deck(new ArrayList<>());
        this.currentPlayerIndex = 0;
        this.gameOver = false;
        this.reverseDirection = false;
    }
    
    // Initialize the game (shuffle deck, deal cards, start discard pile)
    public void initializeGame() {
        // Create and shuffle a new deck
        drawPile = new Deck();
        drawPile.shuffle();
        
        // Clear discard pile
        discardPile = new Deck(new ArrayList<>());
        
        // Deal cards to each player
        for (Player player : players) {
            player.addCards(drawPile.dealCards(Rules.INITIAL_CARDS_PER_PLAYER));
        }
        
        // Start the discard pile with one card from the draw pile
        Card firstCard = drawPile.drawCard();
        discardPile.addCard(firstCard);
        
        // Reset game state
        currentPlayerIndex = 0;
        gameOver = false;
        reverseDirection = false;
        
        // Reset player states
        for (Player player : players) {
            player.setHasDrawnThisTurn(false);
        }
    }
    
    // Get the current player whose turn it is
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }
    
    // Get the top card of the discard pile
    public Card getTopDiscard() {
        List<Card> cards = discardPile.getCards();
        if (cards.isEmpty()) {
            return null;
        }
        return cards.get(cards.size() - 1);
    }
    
    // Play a card from the current player's hand
    public boolean playCard(String cardCode) {
        Player currentPlayer = getCurrentPlayer();
        Card topDiscard = getTopDiscard();
        
        // Verify the player has the card
        if (!currentPlayer.hasCard(cardCode)) {
            return false;
        }
        
        Card cardToPlay = Card.fromCode(cardCode);
        
        // Check if the move is valid according to the rules
        if (!Rules.isValidPlay(cardToPlay, topDiscard)) {
            return false;
        }
        
        // Play the card
        Card playedCard = currentPlayer.playCard(cardCode);
        if (playedCard == null) {
            return false; // Card not found (should not happen at this point)
        }
        
        // Add the card to the discard pile
        discardPile.addCard(playedCard);
        
        // Check for game over condition
        if (checkGameOver()) {
            return true;
        }
        
        // Move to the next player's turn
        nextTurn();
        return true;
    }
    
    // Draw a card for the current player
    public Card drawCard() {
        Player currentPlayer = getCurrentPlayer();
        
        // Check if the player has already drawn this turn
        if (currentPlayer.hasDrawnThisTurn()) {
            return null; // Can only draw once per turn
        }
        
        // Draw a card from the draw pile
        Card drawnCard = drawPile.drawCard();
        if (drawnCard == null) {
            // No cards left in the draw pile
            gameOver = true;
            return null;
        }
        
        // Add the card to the player's hand
        currentPlayer.addCard(drawnCard);
        currentPlayer.setHasDrawnThisTurn(true);
        
        return drawnCard;
    }
    
    // Pass the turn to the next player
    public boolean passTurn() {
        Player currentPlayer = getCurrentPlayer();
        
        // Can only pass if you've drawn a card this turn
        if (!currentPlayer.hasDrawnThisTurn()) {
            return false;
        }
        
        // Check if there's a valid move the player could make
        Card topDiscard = getTopDiscard();
        if (Rules.canPlayerMakeValidMove(currentPlayer, topDiscard)) {
            return false; // Player must make a valid move if possible
        }
        
        // Move to the next player's turn
        nextTurn();
        return true;
    }
    
    // Move to the next player's turn
    private void nextTurn() {
        // Reset the current player's drawn status
        getCurrentPlayer().setHasDrawnThisTurn(false);
        
        // Move to the next player
        currentPlayerIndex = Rules.getNextPlayerIndex(currentPlayerIndex, players.size(), reverseDirection);
    }
    
    // Check if the game is over
    private boolean checkGameOver() {
        Player currentPlayer = getCurrentPlayer();
        
        // Game is over if a player has no cards left or if the draw pile is empty
        if (Rules.isGameOver(currentPlayer, drawPile)) {
            gameOver = true;
            return true;
        }
        
        return false;
    }
    
    // Get the winner of the game
    public Player getWinner() {
        if (!gameOver) {
            return null; // Game is not over yet
        }
        
        // If a player has no cards, they are the winner
        for (Player player : players) {
            if (player.hasWon()) {
                return player;
            }
        }
        
        // If the draw pile is empty, the winner is the player with the lowest points
        Player[] playersArray = players.toArray(new Player[0]);
        return Rules.determineWinnerByPoints(playersArray);
    }
    
    // Get player by name
    public Player getPlayerByName(String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) {
                return player;
            }
        }
        return null;
    }
    
    // Get all players
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }
    
    // Get turn order starting from the next player
    public List<String> getTurnOrder() {
        List<String> order = new ArrayList<>();
        int index = currentPlayerIndex;
        
        for (int i = 0; i < players.size(); i++) {
            order.add(players.get(index).getName());
            index = Rules.getNextPlayerIndex(index, players.size(), reverseDirection);
        }
        
        return order;
    }
    
    // Get the game name
    public String getGameName() {
        return gameName;
    }
    
    // Check if the game is over
    public boolean isGameOver() {
        return gameOver;
    }
    
    // Get the draw pile
    public Deck getDrawPile() {
        return drawPile;
    }
    
    // Get the discard pile
    public Deck getDiscardPile() {
        return discardPile;
    }
}