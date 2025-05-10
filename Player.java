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
    
    public void addCard(Card card) {
        hand.add(card);
    }
    
    public void addCards(List<Card> cards) {
        hand.addAll(cards);
    }
    
    public Card playCard(String cardCode) {
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).getCode().equals(cardCode)) {
                return hand.remove(i);
            }
        }
        return null;
    }
    
    public boolean hasCard(String cardCode) {
        for (Card card : hand) {
            if (card.getCode().equals(cardCode)) {
                return true;
            }
        }
        return false;
    }
    

    public boolean canPlay(Card topDiscard) {
        for (Card card : hand) {
            if (canPlayCard(card, topDiscard)) {
                return true;
            }
        }
        return false;
    }
    
    public List<Card> getPlayableCards(Card topDiscard) {
        List<Card> playableCards = new ArrayList<>();
        for (Card card : hand) {
            if (canPlayCard(card, topDiscard)) {
                playableCards.add(card);
            }
        }
        return playableCards;
    }

    public boolean canPlayCard(Card card, Card topDiscard) {
        if (card.getRank().equals("8")) {
            return true;
        }
        
        return card.getRank().equals(topDiscard.getRank()) || 
               card.getSuit().equals(topDiscard.getSuit());
    }
    
    public int calculatePoints() {
        int total = 0;
        for (Card card : hand) {
            total += card.getPointValue();
        }
        return total;
    }
    
    public int getHandSize() {
        return hand.size();
    }
    
    public boolean hasWon() {
        return hand.isEmpty();
    }
    
    public List<Card> getHand() {
        return new ArrayList<>(hand);
    }
    
    public List<String> getHandAsCodes() {
        List<String> codes = new ArrayList<>();
        for (Card card : hand) {
            codes.add(card.getCode());
        }
        return codes;
    }

    public void setHasDrawnThisTurn(boolean hasDrawn) {
        this.hasDrawnThisTurn = hasDrawn;
    }
    
    public boolean hasDrawnThisTurn() {
        return hasDrawnThisTurn;
    }
    
    @Override
    public String toString() {
        return name + " (" + hand.size() + " cards)";
    }
}