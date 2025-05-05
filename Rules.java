public class Rules {
    // Number of cards to deal to each player at the start of the game
    public static final int INITIAL_CARDS_PER_PLAYER = 5;
    
    // Check if a card can be played on the top discard card according to Crazy Eights rules
    public static boolean isValidPlay(Card cardToPlay, Card topDiscard) {
        // Eights are wild and can be played at any time
        if (cardToPlay.getRank().equals("8")) {
            return true;
        }
        
        // Match rank or suit
        return cardToPlay.getRank().equals(topDiscard.getRank()) || 
               cardToPlay.getSuit().equals(topDiscard.getSuit());
    }
    
    // Check if the game is over based on current game state
    public static boolean isGameOver(Player currentPlayer, Deck drawPile) {
        // Game is over if a player has no cards left
        if (currentPlayer.hasWon()) {
            return true;
        }
        
        // Game is over if the draw pile is empty
        if (drawPile.isEmpty()) {
            return true;
        }
        
        return false;
    }
    
    // Determine the winner when the draw pile is empty (player with lowest points)
    public static Player determineWinnerByPoints(Player[] players) {
        Player winner = players[0];
        int lowestPoints = winner.calculatePoints();
        
        for (int i = 1; i < players.length; i++) {
            int points = players[i].calculatePoints();
            if (points < lowestPoints) {
                lowestPoints = points;
                winner = players[i];
            }
        }
        
        return winner;
    }
    
    // Check if the current player can make a valid move
    public static boolean canPlayerMakeValidMove(Player player, Card topDiscard) {
        return player.canPlay(topDiscard);
    }
    
    // Get the next player in turn order
    public static int getNextPlayerIndex(int currentPlayerIndex, int totalPlayers, boolean reverseDirection) {
        if (reverseDirection) {
            return (currentPlayerIndex - 1 + totalPlayers) % totalPlayers;
        } else {
            return (currentPlayerIndex + 1) % totalPlayers;
        }
    }
}