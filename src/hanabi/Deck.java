package hanabi;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import game.Util;
import util.ImList;

public class Deck {

    // cards stored such that cards are drawn from the back
    private ImList<CardSpec> cards;

    private static final List<Integer> NUMBER_DIST = Arrays.asList(1,1,1,2,2,3,3,4,4,5);

    private static List<CardSpec> generateRandomCardOrder() {
        List<CardSpec> deck = new LinkedList<>();
        for (Color color : Color.ALL_COLORS) {
            for (Integer number : NUMBER_DIST) {
                deck.add(new CardSpec(color, number));
            }
        }
        Collections.shuffle(deck);
        return deck;
    }

    /**
     * Constructs deck with random card order
     */
    public Deck() {
        this(generateRandomCardOrder());
    }

    /**
     * Constructs a new Deck given the card order
     * @param cards list of DeckCards specifying the card 
     * order.  Cards are drawn from the back of the list  
     */
    public Deck(List<CardSpec> cards) {
        this.cards = ImList.<CardSpec>convert(cards);
    }

    public int size() {
        return cards.length();
    }

    public Card draw() {
        Card drawnCard = cards.last().createCard();
        cards = cards.start();
        return drawnCard;
    }

    public ImList<CardSpec> getView() {
        return cards;
    }

    @Override
    public String toString() {
        return Util.deckRep(cards);
    }

}
