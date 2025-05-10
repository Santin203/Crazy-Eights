
/*
 * Prompt: Create a class named Card that represents a playing card in a standard deck of cards.
 * It should use this format: "rank" + "suit", where rank is a number from 2 to 10 or a letter (A, J, Q, K) and suit is one of the following:
 * "H" for hearts, "D" for diamonds, "C" for clubs, and "S" for spades.
 */
public class Card {
    private final String rank;
    private final String suit;
    
    public static final String HEARTS = "H";
    public static final String DIAMONDS = "D";
    public static final String CLUBS = "C";
    public static final String SPADES = "S";
    
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
    
    public String getCode() {
        return rank + suit;
    }
    
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

    /*
     * Prompt: Create a method that returns the point value of the card based on the following rules:
     * - 8 is worth 50 points
     * - 10, J, Q, K are worth 10 points each
     * - A is worth 1 point
     */
    public int getPointValue() {
        switch (rank) {
            case "8":
                return 50;
            case KING:
            case QUEEN:
            case JACK:
            case "10":
                return 10;
            case ACE:
                return 1;
            default:
                try {
                    return Integer.parseInt(rank);
                } catch (NumberFormatException e) {
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