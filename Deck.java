import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Prompt: Based on the Card class, create a basic Deck class
 */
public class Deck {
    private List<Card> cards;
    
    // Standard ranks for a deck of cards
    private static final String[] RANKS = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
    
    // Standard suits for a deck of cards
    private static final String[] SUITS = {Card.HEARTS, Card.DIAMONDS, Card.CLUBS, Card.SPADES};

    public Deck() {
        cards = new ArrayList<>();
        initializeDeck();
    }
    
    // Create a deck from an existing list of cards
    public Deck(List<Card> cards) {
        this.cards = new ArrayList<>(cards);
    }
    
    // Initialize with a standard 52-card deck
    private void initializeDeck() {
        for (String suit : SUITS) {
            for (String rank : RANKS) {
                cards.add(new Card(rank, suit));
            }
        }
    }
    
    // Shuffle the deck
    public void shuffle() {
        Collections.shuffle(cards);
    }
    
    // Draw a card from the top of the deck
    public Card drawCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }
    
    // Add a card to the deck (to the bottom by default)
    public void addCard(Card card) {
        cards.add(card);
    }
    
    // Add a card to the top of the deck
    public void addCardToTop(Card card) {
        cards.add(0, card);
    }
    
    // Check if the deck is empty
    public boolean isEmpty() {
        return cards.isEmpty();
    }
    
    // Get the number of cards in the deck
    public int size() {
        return cards.size();
    }
    
    // Deal a specific number of cards from the deck
    public List<Card> dealCards(int numCards) {
        List<Card> dealtCards = new ArrayList<>();
        for (int i = 0; i < numCards && !cards.isEmpty(); i++) {
            dealtCards.add(drawCard());
        }
        return dealtCards;
    }
    
    // Get a copy of all cards in the deck without removing them
    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }
    
    // Convert the deck to a list of card codes
    public List<String> toCardCodes() {
        List<String> codes = new ArrayList<>();
        for (Card card : cards) {
            codes.add(card.getCode());
        }
        return codes;
    }
    
    // Create a deck from a list of card codes
    public static Deck fromCardCodes(List<String> codes) {
        List<Card> cards = new ArrayList<>();
        for (String code : codes) {
            cards.add(Card.fromCode(code));
        }
        return new Deck(cards);
    }
}