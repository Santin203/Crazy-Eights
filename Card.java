public class Card {
    private final String rank;
    private final String suit;
    
    // Constants for card suits
    public static final String HEARTS = "H";
    public static final String DIAMONDS = "D";
    public static final String CLUBS = "C";
    public static final String SPADES = "S";
    
    // Constants for card ranks
    public static final String ACE = "A";
    public static final String JACK = "J";
    public static final String QUEEN = "Q";
    public static final String KING = "K";
    
    public Card(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
    }
    
    public String getRank() {
        return rank;
    }
    
    public String getSuit() {
        return suit;
    }
    
    // Returns the 2-character representation of the card (e.g., "4C" for four of clubs)
    public String getCode() {
        return rank + suit;
    }
    
    // Creates a Card object from a 2 or 3-character code (e.g., "4C" for four of clubs, "10H" for ten of hearts)
    public static Card fromCode(String code) {
        if (code.length() < 2 || code.length() > 3) {
            throw new IllegalArgumentException("Card code must be 2 or 3 characters");
        }
        
        String rank;
        String suit;
        
        if (code.length() == 3 && code.startsWith("10")) {
            rank = "10";
            suit = code.substring(2, 3);
        } else {
            rank = code.substring(0, 1);
            suit = code.substring(1, 2);
        }
        
        return new Card(rank, suit);
    }
    
    // Calculate the point value of the card according to Crazy Eights rules
    public int getPointValue() {
        switch (rank) {
            case ACE:
            case JACK:
            case QUEEN:
            case KING:
                return 10;
            default:
                try {
                    return Integer.parseInt(rank);
                } catch (NumberFormatException e) {
                    // This should not happen with valid cards
                    return 0;
                }
        }
    }
    
    @Override
    public String toString() {
        String rankName;
        switch (rank) {
            case ACE: rankName = "Ace"; break;
            case JACK: rankName = "Jack"; break;
            case QUEEN: rankName = "Queen"; break;
            case KING: rankName = "King"; break;
            default: rankName = rank;
        }
        
        String suitName;
        switch (suit) {
            case HEARTS: suitName = "Hearts"; break;
            case DIAMONDS: suitName = "Diamonds"; break;
            case CLUBS: suitName = "Clubs"; break;
            case SPADES: suitName = "Spades"; break;
            default: suitName = suit;
        }
        
        return rankName + " of " + suitName;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Card other = (Card) obj;
        return rank.equals(other.rank) && suit.equals(other.suit);
    }
    
    @Override
    public int hashCode() {
        return 31 * rank.hashCode() + suit.hashCode();
    }
}