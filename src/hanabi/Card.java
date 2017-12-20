package hanabi;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

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

    public Card(Color color, Integer number){
        this.color = color;
        this.number = number;
        this.possibleColors = new TreeSet<>(Color.ALL_COLORS);
        this.possibleNumbers = new TreeSet<>(ALL_NUMBERS);
        checkRep();
    }

    private void checkRep() {
        assert (NUMBER_MIN <= number && number <= NUMBER_MAX);
        assert Color.ALL_COLORS.containsAll(this.possibleColors);
        assert this.possibleColors.contains(this.color);
        assert ALL_NUMBERS.containsAll(this.possibleNumbers);
        assert this.possibleNumbers.contains(this.number);
    }

    public Color color() {
        return this.color;
    }

    public int number() {
        return this.number;
    }

    public Set<Color> possibleColors() {
        return Collections.unmodifiableSet(this.possibleColors);
    }

    public Set<Integer> possibleNumbers() {
        return Collections.unmodifiableSet(this.possibleNumbers);
    }

    /**
     * Update this card with positive or negative information about its color 
     * that its holder learned.  
     * @param color the color the owner of this card learned information about
    */
    public void learnColor(Color color) {
        if (this.color.equals(color)) {
            for (Color anyColor : Color.ALL_COLORS) {
                if (!anyColor.equals(color)) {
                    this.possibleColors.remove(anyColor);
                }
            }
        } else {
            this.possibleColors.remove(color);
        }
        checkRep();
    }

    /**
     * Update this card with positive or negative information about its number 
     * that its holder learned.  
     * @param number the number the owner of this card learned information about
     */
    public void learnNumber(Integer number) {
        if (this.number == number) {
            for (Integer anyNumber : ALL_NUMBERS) {
                if (!anyNumber.equals(number)) {
                    this.possibleNumbers.remove(anyNumber);
                }
            }
        } else {
            this.possibleNumbers.remove(number);
        }
        checkRep();
    }
    
    public String shortRep() {
        return this.color.toString() + this.number;
    }

    @Override
    public String toString() {
        String possibleColorsStr = "";
        for (Color color : this.possibleColors) {
            possibleColorsStr += color;
        }
        for (int i=0; i< Color.NUM_COLORS - this.possibleColors.size(); i++) {
            possibleColorsStr += " ";
        }
        String possibleNumbersStr = "";
        for (Integer number: this.possibleNumbers) {
            possibleNumbersStr += number;
        }
        for (int i=0; i< NUMBER_MAX - this.possibleNumbers.size(); i++) {
            possibleNumbersStr += " ";
        }
        return "" + this.color + this.number + " (" + possibleColorsStr + ", " 
            + possibleNumbersStr + ")"; 
    }

}
