package hanabi;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import views.HiddenCardView;
import views.VisibleCardView;

/**
 * An datatype for cards
 */
public class Card {
    /*
     * Represents a card with color color and number number.  The holder
     * of this card knows that the card's color is one of possibleColors, 
     * and that its number is one of possibleNumbers.  
     */

    public static final int NUMBER_MIN = 1;
    public static final int NUMBER_MAX = 5;
    public static final Set<Integer> ALL_NUMBERS = Collections.unmodifiableSet(
        new TreeSet<>(Arrays.asList(1,2,3,4,5))
    );    

    private final Color color;
    private final int number;
    private final Set<Color> possibleColors;
    private final Set<Integer> possibleNumbers;
    private HiddenCardView hiddenView;
    private VisibleCardView visibleView;
    private boolean colorHinted;
    private boolean numberHinted;

    public Card(Color color, Integer number){
        this.color = color;
        this.number = number;
        this.possibleColors = new TreeSet<>(Color.ALL_COLORS);
        this.possibleNumbers = new TreeSet<>(ALL_NUMBERS);
        this.colorHinted = false;
        this.numberHinted = false;
        refreshViews();
        checkRep();
    }

    private void checkRep() {
        assert (NUMBER_MIN <= number && number <= NUMBER_MAX);
        assert Color.ALL_COLORS.containsAll(possibleColors);
        assert possibleColors.contains(color);
        assert ALL_NUMBERS.containsAll(possibleNumbers);
        assert possibleNumbers.contains(number);
        assert color.equals(visibleView.color());
        assert number == visibleView.number();
        assert possibleColors.equals(visibleView.colors());
        assert possibleNumbers.equals(visibleView.numbers());
        assert possibleColors.equals(hiddenView.colors());
        assert possibleNumbers.equals(hiddenView.numbers());        
    }

    private void refreshViews(){
        this.hiddenView = new HiddenCardView(possibleColors, possibleNumbers, colorHinted, numberHinted);
        this.visibleView = new VisibleCardView(color, number, hiddenView);
    }

    public Color color() {
        return this.color;
    }

    public int number() {
        return this.number;
    }

    public HiddenCardView hiddenView() {
        return hiddenView;
    }

    public VisibleCardView visibleView() {
        return visibleView;
    }

    /**
     * Update this card with positive or negative information about its color 
     * that its holder learned.  
     * @param color the color the owner of this card learned information about
    */
    public void learnColor(Color color) {
        if (this.color.equals(color)) {
            this.colorHinted = true;
            for (Color anyColor : Color.ALL_COLORS) {
                if (!anyColor.equals(color)) {
                    this.possibleColors.remove(anyColor);
                }
            }
        } else {
            this.possibleColors.remove(color);
        }
        refreshViews();
        checkRep();
    }

    /**
     * Update this card with positive or negative information about its number 
     * that its holder learned.  
     * @param number the number the owner of this card learned information about
     */
    public void learnNumber(Integer number) {
        if (this.number == number) {
            this.numberHinted = true;
            for (Integer anyNumber : ALL_NUMBERS) {
                if (!anyNumber.equals(number)) {
                    this.possibleNumbers.remove(anyNumber);
                }
            }
        } else {
            this.possibleNumbers.remove(number);
        }
        refreshViews();
        checkRep();
    }
    
    public String shortRep() {
        return this.color.toString() + this.number;
    }

    @Override
    public String toString() {
        return visibleView.toString();
    }

}
