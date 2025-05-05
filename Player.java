import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private List<Card> hand;
    private boolean hasDrawnThisTurn;
    
    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.hasDrawnThisTurn = false;
    }
    
    public String getName() {
        return name;
    }
    
    // Add a card to the player's hand
    public void addCard(Card card) {
        hand.add(card);
    }
    
    // Add multiple cards to the player's hand
    public void addCards(List<Card> cards) {
        hand.addAll(cards);
    }
    
    // Play a card from the player's hand by its code
    public Card playCard(String cardCode) {
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).getCode().equals(cardCode)) {
                return hand.remove(i);
            }
        }
        return null; // Card not found
    }
    
    // Check if the player has a specific card
    public boolean hasCard(String cardCode) {
        for (Card card : hand) {
            if (card.getCode().equals(cardCode)) {
                return true;
            }
        }
        return false;
    }
    
    // Check if the player can play a card based on the top discard card
    public boolean canPlay(Card topDiscard) {
        for (Card card : hand) {
            if (canPlayCard(card, topDiscard)) {
                return true;
            }
        }
        return false;
    }
    
    // Get a list of playable cards based on the top discard card
    public List<Card> getPlayableCards(Card topDiscard) {
        List<Card> playableCards = new ArrayList<>();
        for (Card card : hand) {
            if (canPlayCard(card, topDiscard)) {
                playableCards.add(card);
            }
        }
        return playableCards;
    }
    
    // Check if a specific card can be played on the top discard card
    public boolean canPlayCard(Card card, Card topDiscard) {
        // Eights are wild and can be played at any time
        if (card.getRank().equals("8")) {
            return true;
        }
        
        // Match rank or suit
        return card.getRank().equals(topDiscard.getRank()) || 
               card.getSuit().equals(topDiscard.getSuit());
    }
    
    // Calculate the total point value of cards in hand
    public int calculatePoints() {
        int total = 0;
        for (Card card : hand) {
            total += card.getPointValue();
        }
        return total;
    }
    
    // Get the number of cards in the player's hand
    public int getHandSize() {
        return hand.size();
    }
    
    // Check if the player has won (no cards left)
    public boolean hasWon() {
        return hand.isEmpty();
    }
    
    // Get all cards in the player's hand
    public List<Card> getHand() {
        return new ArrayList<>(hand);
    }
    
    // Convert the player's hand to a list of card codes
    public List<String> getHandAsCodes() {
        List<String> codes = new ArrayList<>();
        for (Card card : hand) {
            codes.add(card.getCode());
        }
        return codes;
    }
    
    // Mark that the player has drawn a card this turn
    public void setHasDrawnThisTurn(boolean hasDrawn) {
        this.hasDrawnThisTurn = hasDrawn;
    }
    
    // Check if the player has drawn a card this turn
    public boolean hasDrawnThisTurn() {
        return hasDrawnThisTurn;
    }
    
    @Override
    public String toString() {
        return name + " (" + hand.size() + " cards)";
    }
}