package hanabi;

public class DeckCard {

    public final Color color;
    public final int number;

    public DeckCard(Color color, int number) {
        this.color = color;
        this.number = number;
    }

    public Card createCard() {
        return new Card(color, number);
    }

    @Override
    public boolean equals (Object other) {
        if (!(other instanceof DeckCard)) { return false; }
        DeckCard that = (DeckCard) other;
        return (
            this.color.equals(that.color) && 
            this.number == that.number
        );
    }

    @Override
    public int hashCode() {
        return this.color.hashCode() + this.number;
    }

    @Override
    public String toString() {
        return this.color.toString() + this.number;
    }

}
