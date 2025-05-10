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
    
    public void initializeGame() {

        drawPile = new Deck();
        drawPile.shuffle();

        discardPile = new Deck(new ArrayList<>());
        

        for (Player player : players) {
            player.addCards(drawPile.dealCards(Rules.INITIAL_CARDS_PER_PLAYER));
        }

        Card firstCard = drawPile.drawCard();
        discardPile.addCard(firstCard);
        

        currentPlayerIndex = 0;
        gameOver = false;
        reverseDirection = false;
        

        for (Player player : players) {
            player.setHasDrawnThisTurn(false);
        }
    }
    

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public Card getTopDiscard() {
        List<Card> cards = discardPile.getCards();
        if (cards.isEmpty()) {
            return null;
        }
        return cards.get(cards.size() - 1);
    }
    

    public boolean playCard(String cardCode) {
        Player currentPlayer = getCurrentPlayer();
        Card topDiscard = getTopDiscard();
        

        if (!currentPlayer.hasCard(cardCode)) {
            return false;
        }
        
        Card cardToPlay = Card.fromCode(cardCode);

        if (!Rules.isValidPlay(cardToPlay, topDiscard)) {
            return false;
        }

        Card playedCard = currentPlayer.playCard(cardCode);
        if (playedCard == null) {
            return false; 
        }
        
        discardPile.addCard(playedCard);
        

        if (checkGameOver()) {
            return true;
        }
        

        nextTurn();
        return true;
    }
    
    // Draw a card for the current player
    public Card drawCard() {
        Player currentPlayer = getCurrentPlayer();
        

        if (currentPlayer.hasDrawnThisTurn()) {
            return null; 
        }
        

        Card drawnCard = drawPile.drawCard();
        if (drawnCard == null) {

            gameOver = true;
            return null;
        }

        currentPlayer.addCard(drawnCard);
        currentPlayer.setHasDrawnThisTurn(true);
        
        return drawnCard;
    }
    
    public boolean passTurn() {
        Player currentPlayer = getCurrentPlayer();
        

        if (!currentPlayer.hasDrawnThisTurn()) {
            return false;
        }

        Card topDiscard = getTopDiscard();
        if (Rules.canPlayerMakeValidMove(currentPlayer, topDiscard)) {
            return false; 
        }

        nextTurn();
        return true;
    }
    
    private void nextTurn() {
        getCurrentPlayer().setHasDrawnThisTurn(false);
        currentPlayerIndex = Rules.getNextPlayerIndex(currentPlayerIndex, players.size(), reverseDirection);
    }
    
    public boolean checkGameOver() {
        Player currentPlayer = getCurrentPlayer();
        
        if (Rules.isGameOver(currentPlayer, drawPile)) {
            gameOver = true;
            return true;
        }
        
        return false;
    }

    public Player getWinner() {
        if (!gameOver) {
            return null;
        }

        for (Player player : players) {
            if (player.hasWon()) {
                return player;
            }
        }
        
        Player[] playersArray = players.toArray(new Player[0]);
        return Rules.determineWinnerByPoints(playersArray);
    }
    
    public Player getPlayerByName(String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) {
                return player;
            }
        }
        return null;
    }
    
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }
    
    public List<String> getTurnOrder() {
        List<String> order = new ArrayList<>();
        int index = currentPlayerIndex;
        
        for (int i = 0; i < players.size(); i++) {
            order.add(players.get(index).getName());
            index = Rules.getNextPlayerIndex(index, players.size(), reverseDirection);
        }
        
        return order;
    }
    
    public String getGameName() {
        return gameName;
    }
    
    public boolean isGameOver() {
        return gameOver;
    }
    
    public Deck getDrawPile() {
        return drawPile;
    }
    

    public Deck getDiscardPile() {
        return discardPile;
    }
}