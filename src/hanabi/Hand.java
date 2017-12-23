package hanabi;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

    public boolean hintColor(Color color) {
        boolean colorExists = false;
        for (Card card : this.cards) {
            colorExists = colorExists || card.color().equals(color);
        }
        if (!colorExists) { return false; }
        for (Card card: this.cards) {
            card.learnColor(color);
        }
        refreshViews();
        checkRep();
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
        refreshViews();
        checkRep();
        return true;
    }

    public Card playOrDiscard(int position, Card newCard) {
        assert !this.isFinished;
        assert newCard.hiddenView().equals(HiddenCardView.NO_INFO);
        cards.addLast(newCard);
        Card removedCard = cards.remove(position);
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
