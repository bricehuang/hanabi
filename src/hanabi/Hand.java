package hanabi;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Hand {
    private final int handSize;
    private final LinkedList<Card> cards;
    private boolean isFinished;

    public Hand(int handSize, List<Card> cards) {
        this.handSize = handSize;
        this.cards = new LinkedList<>(cards);
        this.isFinished = false;
        checkRep();
    }

    private void checkRep() {
        assert(
            this.cards.size() == handSize && !this.isFinished || 
            this.cards.size() == handSize-1 && this.isFinished
        );
    }

    public List<Card> cards() {
        return Collections.unmodifiableList(cards);
    }

    public int size() {
        return handSize;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public boolean hintColor(Color color) {
        boolean colorExists = false;
        for (Card card : this.cards) {
            colorExists = colorExists || card.color().equals(color);
        }
        if (!colorExists) { return false; }
        for (Card card: this.cards) {
            card.learnColor(color);
        }
        return true;
    }

    public boolean hintNumber(int number) {
        boolean numberExists = false;
        for (Card card: this.cards) {
            numberExists = numberExists || card.number() == number;
        }
        if (!numberExists) { return false; }
        for (Card card: this.cards) {
            card.learnNumber(number);
        }
        return true;
    }

    public Card playOrDiscard(int position, Card newCard) {
        assert !this.isFinished;
        assert newCard.possibleColors().equals(Color.ALL_COLORS);
        assert newCard.possibleNumbers().equals(Card.ALL_NUMBERS);
        cards.addLast(newCard);
        Card removedCard = cards.remove(position);
        checkRep();
        return removedCard;
    }

    public Card playOrDiscardLast(int position) {
        assert !this.isFinished;
        this.isFinished = true;
        Card removedCard = cards.remove(position);
        checkRep();
        return removedCard;
    }

    @Override
    public String toString() {
        String rep = "[";
        for (Card card: cards) {
            rep += card.toString() + ", ";
        }
        rep += "]";
        return rep;
    }

}
