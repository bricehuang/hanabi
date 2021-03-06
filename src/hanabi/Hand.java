package hanabi;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import views.HiddenCardView;
import views.HiddenHandView;
import views.VisibleCardView;
import views.VisibleHandView;

public class Hand {
    private final int handSize;
    private final LinkedList<Card> cards;
    private boolean isFinished;
    
    private HiddenHandView hiddenView;
    private VisibleHandView visibleView;

    public Hand(int handSize, List<Card> cards) {
        this.handSize = handSize;
        this.cards = new LinkedList<>(cards);
        this.isFinished = false;
        refreshViews();
        checkRep();
    }

    private void checkRep() {
        assert(
            this.cards.size() == handSize && !this.isFinished || 
            this.cards.size() == handSize-1 && this.isFinished
        );
        assert visibleView.cardViews.size() == hiddenView.cardViews.size();
        for (int i=0; i<visibleView.cardViews.size(); i++) {
            assert (
                visibleView.cardViews.get(i).colors().equals(
                    hiddenView.cardViews.get(i).colors()
                )
            );
            assert (
                visibleView.cardViews.get(i).numbers().equals(
                    hiddenView.cardViews.get(i).numbers()
                )
            );
        }
    }

    private void refreshViews() {
        List<HiddenCardView> hiddenCardViews = new ArrayList<>();
        List<VisibleCardView> visibleCardViews = new ArrayList<>();
        for (Card card : cards) {
            hiddenCardViews.add(card.hiddenView());
            visibleCardViews.add(card.visibleView());
        }
        this.hiddenView = new HiddenHandView(hiddenCardViews);
        this.visibleView = new VisibleHandView(visibleCardViews);
    }

    public int size() {
        return handSize;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public HiddenHandView hiddenView() {
        return hiddenView;
    }

    public VisibleHandView visibleView() {
        return visibleView;
    }

    /**
     * Gives a color hint to this hand.  
     * @param color
     * @return list of positions hinted.  If hint invalid, returns empty 
     * list with no mutation of state.    
     */
    public List<Integer> hintColor(Color color) {
        boolean colorExists = false;
        for (Card card : this.cards) {
            colorExists = colorExists || card.color().equals(color);
        }
        if (!colorExists) { return new ArrayList<>(); }
        List<Integer> hintedPositions = new ArrayList<>();
        for (int i=0; i<this.cards.size(); i++) {
            Card card = cards.get(i);
            card.learnColor(color);
            if (card.color().equals(color)) {
                hintedPositions.add(i);
            }
        }
        refreshViews();
        checkRep();
        return hintedPositions;
    }

    /**
     * Gives a number hint to this hand
     * @param number
     * @return list of positions hinted.  If hint invalid, returns empty 
     * list with no mutation of state.  
     */
    public List<Integer> hintNumber(int number) {
        boolean numberExists = false;
        for (Card card: this.cards) {
            numberExists = numberExists || card.number() == number;
        }
        if (!numberExists) { return new ArrayList<>(); }
        List<Integer> hintedPositions = new ArrayList<>();
        for (int i=0; i<this.cards.size(); i++) {
            Card card = cards.get(i);
            card.learnNumber(number);
            if (card.number() == number) {
                hintedPositions.add(i);
            }
        }
        refreshViews();
        checkRep();
        return hintedPositions;
    }

    public Card playOrDiscard(int position, Card newCard) {
        assert !this.isFinished;
        assert newCard.hiddenView().equals(HiddenCardView.NO_INFO);
        Card removedCard = cards.remove(position);
        cards.addFirst(newCard);
        refreshViews();
        checkRep();
        return removedCard;
    }

    public Card playOrDiscardLast(int position) {
        assert !this.isFinished;
        this.isFinished = true;
        Card removedCard = cards.remove(position);
        refreshViews();
        checkRep();
        return removedCard;
    }
    
    public boolean isValidColorHint(Set<Integer> hintedPositions) {
        if (hintedPositions.size() == 0) { return false; }
        int arbitraryHintedPosition = hintedPositions.iterator().next();
        Color color = cards.get(arbitraryHintedPosition).color();
        for (int i=0; i<handSize; i++) {
            Color cardColor = cards.get(i).color();
            if (
                hintedPositions.contains(i) && !cardColor.equals(color) ||
                !hintedPositions.contains(i) && cardColor.equals(color)
            ) { return false; }
        }
        return true;
    }

    public boolean isValidNumberHint(Set<Integer> hintedPositions) {
        if (hintedPositions.size() == 0) { return false; }
        int arbitraryHintedPosition = hintedPositions.iterator().next();
        Integer number = cards.get(arbitraryHintedPosition).number();
        for (int i=0; i<handSize; i++) {
            Integer cardNumber = cards.get(i).number();
            if (
                hintedPositions.contains(i) && cardNumber != number ||
                !hintedPositions.contains(i) && cardNumber == number
            ) { return false; }
        }
        return true;
    }

    @Override
    public String toString() {
        return this.visibleView.toString();
    }

}
