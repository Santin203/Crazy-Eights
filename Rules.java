public class Rules {
    public static final int INITIAL_CARDS_PER_PLAYER = 5;
    
    public static boolean isValidPlay(Card cardToPlay, Card topDiscard) {
        if (cardToPlay.getRank().equals("8")) {
            return true;
        }

        return cardToPlay.getRank().equals(topDiscard.getRank()) || 
               cardToPlay.getSuit().equals(topDiscard.getSuit());
    }
    
    public static boolean isGameOver(Player currentPlayer, Deck drawPile) {

        if (currentPlayer.hasWon()) {
            return true;
        }

        if (drawPile.isEmpty()) {
            return true;
        }
        
        return false;
    }
    
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
    
    public static boolean canPlayerMakeValidMove(Player player, Card topDiscard) {
        return player.canPlay(topDiscard);
    }
    
    public static int getNextPlayerIndex(int currentPlayerIndex, int totalPlayers, boolean reverseDirection) {
        if (reverseDirection) {
            return (currentPlayerIndex - 1 + totalPlayers) % totalPlayers;
        } else {
            return (currentPlayerIndex + 1) % totalPlayers;
        }
    }
}